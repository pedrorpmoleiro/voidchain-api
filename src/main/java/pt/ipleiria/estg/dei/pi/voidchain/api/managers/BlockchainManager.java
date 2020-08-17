package pt.ipleiria.estg.dei.pi.voidchain.api.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ipleiria.estg.dei.pi.voidchain.api.APIConfiguration;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Blockchain;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Transaction;
import pt.ipleiria.estg.dei.pi.voidchain.sync.BlockSyncClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BlockchainManager {
    private static BlockchainManager INSTANCE = null;

    private static final Logger logger = LoggerFactory.getLogger(BlockchainManager.class);

    private final Blockchain blockchain;

    private final ReentrantLock transactionPoolLock = new ReentrantLock();
    private final List<Transaction> transactionPool;

    private final BlockSyncClient blockSyncClient;

    private Thread sendTransactionsThread;
    private boolean sendTransactionsThreadStop = false;
    private Thread refreshLocalChainThread;
    private boolean refreshLocalChainThreadStop = false;

    private BlockchainManager() {
        this.blockchain = Blockchain.getInstance();
        this.transactionPool = new ArrayList<>();
        this.blockSyncClient = new BlockSyncClient(NetworkProxyManager.getInstance(APIConfiguration.getInstance().
                getId()).getProxy());

        this.refreshLocalChainThread = new Thread(() -> {
            while (true) {
                if (Blockchain.getBlockFileHeightArray() == null)
                    this.blockSyncClient.sync(true);
                else
                    this.blockSyncClient.sync(false);

                try {
                    Thread.sleep(APIConfiguration.getInstance().getBlockSyncTimer());
                    if (refreshLocalChainThreadStop) return;
                } catch (InterruptedException e) {
                    logger.error("Block Proposal Thread error while waiting", e);
                    this.refreshLocalChainThread = null;
                }
            }
        });
        this.refreshLocalChainThread.start();
        // TODO THREAD for sending transactions to network
    }

    public static BlockchainManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new BlockchainManager();

        return INSTANCE;
    }

    public boolean addTransactionToPool(Transaction transaction) {
        int aux = this.transactionPool.size();

        transactionPoolLock.lock();
        this.transactionPool.add(transaction);
        transactionPoolLock.unlock();

        if (aux == this.transactionPool.size() || (aux + 1) != this.transactionPool.size()) {
            logger.error("Error occurred while adding transaction to memory pool", transaction, this.transactionPool);
            return false;
        }

        logger.info("Transaction added to memory pool");
        return true;
    }

    public boolean addTransactionsToPool(List<Transaction> transactions) {
        int aux = this.transactionPool.size();

        transactionPoolLock.lock();
        this.transactionPool.addAll(transactions);
        transactionPoolLock.unlock();

        if (aux == this.transactionPool.size() || (aux + transactions.size()) != this.transactionPool.size()) {
            logger.error("Error occurred while adding transactions to memory pool", transactions, this.transactionPool);
            return false;
        }

        logger.info("Transactions added to memory pool");
        return true;
    }

    public void close() {
        logger.info("Closing " + this.getClass().getName());
        try {
            this.refreshLocalChainThreadStop = true;
            this.refreshLocalChainThread.join();
            this.sendTransactionsThreadStop = true;
            this.sendTransactionsThread.join();
        } catch (InterruptedException e) {
            logger.error("Error while joining " + this.getClass().getName() + " Threads", e);
            logger.info("Unable to confirm " + this.getClass().getName() + " threads shutdown, " +
                    "continuing");
        }
    }
}
