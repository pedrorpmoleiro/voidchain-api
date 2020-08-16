package pt.ipleiria.estg.dei.pi.voidchain.api.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Blockchain;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BlockchainManager {
    private static BlockchainManager INSTANCE = null;

    private static final Logger logger = LoggerFactory.getLogger(BlockchainManager.class);

    private final Blockchain blockchain;

    private final ReentrantLock transactionPoolLock = new ReentrantLock();
    private final List<Transaction> transactionPool;

    private Thread sendTransactionsThread;
    private Thread refreshLocalChainThread;

    private BlockchainManager() {
        this.blockchain = Blockchain.getInstance();
        this.transactionPool = new ArrayList<>();
        // TODO THREAD for sending transactions to network
        // TODO THREAD for refresing local blockchain
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
}
