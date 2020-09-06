package pt.ipleiria.estg.dei.pi.voidchain.api.dtos;

import java.io.Serializable;
import java.util.List;

public class BlockDTO implements Serializable {
    private final List<TransactionGetDTO> transactions;
    private final BlockHeaderDTO blockHeader;
    private final int transactionCounter;
    private final int blockHeight;
    private final String hash;
    private final int size;

    public BlockDTO(List<TransactionGetDTO> transactions, BlockHeaderDTO blockHeader, int transactionCounter,
                    int blockHeight, String hash, int size) {
        this.transactions = transactions;
        this.blockHeader = blockHeader;
        this.transactionCounter = transactionCounter;
        this.blockHeight = blockHeight;
        this.hash = hash;
        this.size = size;

    }

    public List<TransactionGetDTO> getTransactions() {
        return transactions;
    }

    public BlockHeaderDTO getBlockHeader() {
        return blockHeader;
    }

    public int getTransactionCounter() {
        return transactionCounter;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public String getHash() {
        return hash;
    }

    public int getSize() {
        return size;
    }
}
