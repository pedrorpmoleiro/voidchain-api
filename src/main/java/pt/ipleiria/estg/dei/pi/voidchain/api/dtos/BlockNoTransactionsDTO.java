package pt.ipleiria.estg.dei.pi.voidchain.api.dtos;

import java.io.Serializable;

public class BlockNoTransactionsDTO implements Serializable {
    private final BlockHeaderDTO blockHeader;
    private final int transactionCounter;
    private final int blockHeight;
    private final String hash;
    private final int size;

    public BlockNoTransactionsDTO(BlockHeaderDTO blockHeader, int transactionCounter, int blockHeight, String hash,
                                  int size) {
        this.blockHeader = blockHeader;
        this.transactionCounter = transactionCounter;
        this.blockHeight = blockHeight;
        this.hash = hash;
        this.size = size;
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
