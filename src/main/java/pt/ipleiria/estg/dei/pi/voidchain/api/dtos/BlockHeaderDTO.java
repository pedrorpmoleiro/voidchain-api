package pt.ipleiria.estg.dei.pi.voidchain.api.dtos;

import java.io.Serializable;

public class BlockHeaderDTO implements Serializable {
    private final long timestamp;
    private final String previousBlockHash;
    private final String protocolVersion;
    private final String merkleRoot;

    /**
     * Instantiates a new Block header Data Transfer Object.
     *
     * @param timestamp         the timestamp
     * @param previousBlockHash the previous block hash
     * @param protocolVersion   the protocol version
     * @param merkleRoot        the merkle root
     */
    public BlockHeaderDTO(long timestamp, String previousBlockHash, String protocolVersion, String merkleRoot) {
        this.timestamp = timestamp;
        this.previousBlockHash = previousBlockHash;
        this.protocolVersion = protocolVersion;
        this.merkleRoot = merkleRoot;
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets previous block hash.
     *
     * @return the previous block hash
     */
    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    /**
     * Gets protocol version.
     *
     * @return the protocol version
     */
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Gets merkle root.
     *
     * @return the merkle root
     */
    public String getMerkleRoot() {
        return merkleRoot;
    }
}
