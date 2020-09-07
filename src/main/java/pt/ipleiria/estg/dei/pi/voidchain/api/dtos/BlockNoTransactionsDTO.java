package pt.ipleiria.estg.dei.pi.voidchain.api.dtos;

import java.io.Serializable;

public class BlockNoTransactionsDTO implements Serializable {
    private final BlockHeaderDTO blockHeader;
    private final int transactionCounter;
    private final int blockHeight;
    private final String hash;
    private final int size;

    /**
     * Instantiates a new Block no transactions Data Transfer Object.
     *
     * @param blockHeader        the block header
     * @param transactionCounter the transaction counter
     * @param blockHeight        the block height
     * @param hash               the hash
     * @param size               the size
     */
    public BlockNoTransactionsDTO(BlockHeaderDTO blockHeader, int transactionCounter, int blockHeight, String hash,
                                  int size) {
        this.blockHeader = blockHeader;
        this.transactionCounter = transactionCounter;
        this.blockHeight = blockHeight;
        this.hash = hash;
        this.size = size;
    }

    /**
     * Gets block header.
     *
     * @return the block header
     */
    public BlockHeaderDTO getBlockHeader() {
        return blockHeader;
    }

    /**
     * Gets transaction counter.
     *
     * @return the transaction counter
     */
    public int getTransactionCounter() {
        return transactionCounter;
    }

    /**
     * Gets block height.
     *
     * @return the block height
     */
    public int getBlockHeight() {
        return blockHeight;
    }

    /**
     * Gets hash.
     *
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public int getSize() {
        return size;
    }
}
