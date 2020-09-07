package pt.ipleiria.estg.dei.pi.voidchain.api.managers;

import bitcoinj.Base58;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ipleiria.estg.dei.pi.voidchain.api.APIConfiguration;
import pt.ipleiria.estg.dei.pi.voidchain.api.TransactionStatusAPI;
import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.TransactionPostDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.BlockNotFoundException;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.InternalErrorException;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.TransactionNotFoundException;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.TransactionTooBigException;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Block;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Blockchain;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Transaction;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.TransactionStatus;
import pt.ipleiria.estg.dei.pi.voidchain.sync.BlockSyncClient;
import pt.ipleiria.estg.dei.pi.voidchain.util.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The Blockchain manager manages the local blockchain, all operations that require the local blockchain, the API memory
 * pool and the thread which sends transactions from the API to the node network.
 */
public class BlockchainManager {
    private static BlockchainManager INSTANCE = null;

    private static final Logger logger = LoggerFactory.getLogger(BlockchainManager.class);

    private final Blockchain blockchain;

    private final ReentrantLock transactionPoolLock = new ReentrantLock();
    private final List<Transaction> transactionPool;

    private final BlockSyncClient blockSyncClient;

    private Thread transactionMessengerThread;
    private boolean transactionMessengerThreadStop = false;

    private Thread refreshLocalChainThread;
    private boolean refreshLocalChainThreadStop = false;

    private Thread blockchainValidationCheckThread;
    private boolean blockchainValidationCheckThreadStop = false;

    private boolean refreshAllBlocks = false;

    private BlockchainManager() {
        this.blockchain = Blockchain.getInstance();
        this.transactionPool = new ArrayList<>();
        this.blockSyncClient = new BlockSyncClient(NetworkProxyManager.getInstance().getProxy());

        this.refreshLocalChainThread = new Thread(() -> {
            while (true) {
                if (!APIConfiguration.getInstance().hasNode())
                    if (Blockchain.getBlockFileHeightArray() == null || this.refreshAllBlocks) {
                        this.blockSyncClient.sync(true);
                        this.refreshAllBlocks = false;
                    } else
                        this.blockSyncClient.sync(false);

                this.blockchain.reloadBlocksFromDisk();

                try {
                    Thread.sleep(APIConfiguration.getInstance().getBlockSyncTimer());
                    if (this.refreshLocalChainThreadStop) return;
                } catch (InterruptedException e) {
                    logger.error("Block Proposal Thread error while waiting", e);
                    this.refreshLocalChainThread = null;
                }
            }
        });
        this.refreshLocalChainThread.start();

        this.blockchainValidationCheckThread = new Thread(() -> {
            while (true) {
                logger.info("Validating local chain");
                boolean valid = this.blockchain.isChainValid();
                logger.info("Local chain is " + (valid ? "valid" : "invalid, re downloading all blocks from network"));
                if (!valid)
                    this.refreshAllBlocks = true;

                try {
                    Thread.sleep(Configuration.getInstance().getBlockchainValidTimer());
                    if (blockchainValidationCheckThreadStop) return;
                } catch (InterruptedException e) {
                    logger.error("Blockchain Validation Thread error while waiting", e);
                    this.blockchainValidationCheckThread = null;
                }
            }
        });

        if (!APIConfiguration.getInstance().hasNode())
            this.blockchainValidationCheckThread.start();

        this.transactionMessengerThread = new Thread(() -> {
            APIConfiguration config = APIConfiguration.getInstance();

            while (true) {
                this.transactionPoolLock.lock();
                if (this.transactionPool.size() == 1) {
                    if (NetworkProxyManager.getInstance().
                            sendTransaction(this.transactionPool.get(0)))
                        this.transactionPool.remove(0);
                } else if (this.transactionPool.size() > 1) {
                    int max = this.transactionPool.size();

                    if (max > config.getMaxNumberOfTransactionToSend())
                        max = config.getMaxNumberOfTransactionToSend();

                    List<Transaction> transactionsSend = new ArrayList<>();
                    for (int i = 0; i < max; i++) {
                        Transaction t = this.transactionPool.remove(0);
                        transactionsSend.add(t);
                    }

                    if (!NetworkProxyManager.getInstance().
                            sendTransactions(transactionsSend))
                        this.transactionPool.addAll(transactionsSend);
                }
                this.transactionPoolLock.unlock();

                try {
                    Thread.sleep(config.getSendTransactionsTimer());
                    if (transactionMessengerThreadStop) return;
                } catch (InterruptedException e) {
                    logger.error("Send Transactions to Network Thread error while waiting", e);
                    this.transactionMessengerThread = null;
                }
            }
        });
        this.transactionMessengerThread.start();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static BlockchainManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new BlockchainManager();

        return INSTANCE;
    }

    /**
     * Create and add transaction to pool transaction.
     *
     * @param transactionPost the transaction post
     * @return the transaction
     * @throws TransactionTooBigException the transaction too big exception
     */
    public Transaction createAndAddTransactionToPool(TransactionPostDTO transactionPost)
            throws TransactionTooBigException {
        Transaction t;
        try {
            t = new Transaction(Base58.decode(transactionPost.getData()), this.blockchain.getMostRecentBlock().
                    getProtocolVersion(), Instant.now().toEpochMilli(), Base58.decode(transactionPost.getSignature()));
        } catch (IllegalArgumentException e) {
            logger.error("Error while creating transaction", e);
            throw new TransactionTooBigException("Transaction size exceeds " + Configuration.getInstance().
                    getTransactionMaxSize() + " bytes");
        }
        transactionPoolLock.lock();
        this.transactionPool.add(t);
        transactionPoolLock.unlock();

        return t;
    }

    /**
     * Gets transaction.
     *
     * @param idString the id string
     * @return the transaction
     * @throws TransactionNotFoundException the transaction not found exception
     */
    public Transaction getTransaction(String idString) throws TransactionNotFoundException {
        byte[] id = Base58.decode(idString);

        List<Integer> blocksDisk = Blockchain.getBlockFileHeightArray();
        Collections.reverse(blocksDisk);

        for (Integer i : blocksDisk) {
            Block b;
            try {
                b = this.blockchain.getBlock(i);
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error while retrieving block from disk", e);
                continue;
            }

            for (byte[] hash : b.getTransactions().keySet())
                if (Arrays.equals(id, hash))
                    return b.getTransactions().get(hash);
        }

        throw new TransactionNotFoundException("Requested transaction doesn't exists in the local chain");
    }

    /**
     * Gets transaction in block.
     *
     * @param block the block
     * @return the transaction in block
     */
    public List<Transaction> getTransactionInBlock(Block block) {
        return new ArrayList<>(block.getTransactions().values());
    }

    /**
     * Gets block.
     *
     * @param id the id
     * @return the block
     * @throws BlockNotFoundException the block not found exception
     */
    public Block getBlock(String id) throws BlockNotFoundException {
        final byte[] bId = Base58.decode(id);

        List<Integer> blocksDisk = Blockchain.getBlockFileHeightArray();
        Collections.reverse(blocksDisk);

        for (Integer i : blocksDisk) {
            Block b;
            try {
                b = this.blockchain.getBlock(i);
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error while retrieving block from disk", e);
                continue;
            }

            if (Arrays.equals(bId, b.getHash()))
                return b;
        }

        throw new BlockNotFoundException("Requested block doesn't exists in the local chain");
    }

    /**
     * Gets block.
     *
     * @param blockHeight the block height
     * @return the block
     * @throws BlockNotFoundException the block not found exception
     */
    public Block getBlock(int blockHeight) throws BlockNotFoundException {
        final int highestBlock = this.blockchain.getMostRecentBlock().getBlockHeight();
        if (blockHeight > highestBlock)
            throw new BlockNotFoundException("Requested block [" + blockHeight + "] doesn't exist, " + highestBlock +
                    " is the highest block in the chain");
        else if (blockHeight < 0)
            throw new BlockNotFoundException("Requested block [" + blockHeight + "] doesn't exist, " +
                    "block height cannot be bellow zero");

        try {
            return this.blockchain.getBlock(blockHeight);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Unable to retrieve block from disk", e);
            throw new BlockNotFoundException("Error while retrieving block");
        }
    }

    /**
     * Gets all blocks.
     *
     * @return the all blocks
     * @throws InternalErrorException the internal error exception
     */
    public List<Block> getAllBlocks() throws InternalErrorException {
        List<Block> blocks = new ArrayList<>();
        List<Integer> blocksDisk = Blockchain.getBlockFileHeightArray();
        Collections.reverse(blocksDisk);

        for (Integer i : blocksDisk)
            try {
                blocks.add(this.blockchain.getBlock(i));
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error while adding block [" + i + "] to block list", e);
                throw new InternalErrorException("Error creating list of blocks");
            }

        return blocks;
    }

    /**
     * Gets transaction status.
     *
     * @param idString the transaction id string
     * @return the transaction status
     */
    public TransactionStatusAPI getTransactionStatus(String idString) {
        byte[] id = Base58.decode(idString);

        for (Transaction t : this.transactionPool)
            if (Arrays.equals(t.getHash(), id))
                return TransactionStatusAPI.IN_API_MEM_POOL;

        List<Integer> blocksDisk = Blockchain.getBlockFileHeightArray();
        Collections.reverse(blocksDisk);

        for (Integer i : blocksDisk) {
            Block b;
            try {
                b = this.blockchain.getBlock(i);
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error while retrieving block from disk", e);
                continue;
            }

            for (byte[] hash : b.getTransactions().keySet())
                if (Arrays.equals(id, hash))
                    return TransactionStatusAPI.IN_BLOCK;
        }

        TransactionStatus status = NetworkProxyManager.getInstance().getTransactionStatus(id);

        switch (status) {
            case IN_MEM_POOL:
                return TransactionStatusAPI.IN_NETWORK_MEM_POOL;
            case IN_BLOCK:
                return TransactionStatusAPI.IN_BLOCK;
            default:
                return TransactionStatusAPI.UNKNOWN;
        }
    }

    /**
     * Is local chain valid boolean.
     *
     * @return the boolean
     */
    public boolean isChainValid() {
        if (this.blockchain.isChainValid())
            return true;
        else {
            logger.warn("Local chain is invalid, re downloading all blocks from network");
            this.refreshAllBlocks = true;
            return false;
        }
    }

    /**
     * Gets transaction pool.
     *
     * @return the transaction pool
     */
    public List<Transaction> getTransactionPool() {
        return new ArrayList<>(this.transactionPool);
    }

    /**
     * Gets transaction of owner.
     *
     * @param ownerPubKey the owner pub key
     * @return the transaction of owner
     * @throws InternalErrorException the internal error exception
     */
    public List<Transaction> getTransactionOfOwner(String ownerPubKey) throws InternalErrorException {
        List<Transaction> transactions = new ArrayList<>();

        List<Block> blocks = getAllBlocks();
        byte[] pubKeyBytes = Base58.decode(ownerPubKey);

        for (Block b : blocks)
            for (Transaction t : b.getTransactions().values())
                try {
                    if (t.verifySignature(pubKeyBytes))
                        transactions.add(t);
                } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException |
                        InvalidKeyException | SignatureException | IOException e) {
                    logger.error("Unable to verify signature of transaction: " + Base58.encode(t.getHash()));
                    throw new InternalErrorException("Error creating list of transactions");
                }

        return transactions;
    }

    /**
     * Shutdown Blockchain Manager and it's services.
     */
    public void close() {
        logger.info("Closing " + this.getClass().getName());
        try {
            logger.info("Stopping Blockchain Sync thread");
            this.refreshLocalChainThreadStop = true;
            this.refreshLocalChainThread.join();
            logger.info("Blockchain Sync thread stopped");
            logger.info("Stopping Transaction Messenger thread");
            this.transactionMessengerThreadStop = true;
            this.transactionMessengerThread.join();
            logger.info("Transaction Messenger thread stopped");
            this.blockchainValidationCheckThreadStop = true;
            this.blockchainValidationCheckThread.join();
            logger.info("Blockchain Validation thread stopped");
        } catch (InterruptedException e) {
            logger.error("Error while joining " + this.getClass().getName() + " Threads", e);
            logger.info("Unable to confirm " + this.getClass().getName() + " threads shutdown, " +
                    "continuing");
        }

        INSTANCE = null;
    }
}
