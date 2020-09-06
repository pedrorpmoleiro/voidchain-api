package pt.ipleiria.estg.dei.pi.voidchain.api.dtos;

import java.io.Serializable;

public class BlockHeaderDTO implements Serializable {
    private final long timestamp;
    private final String previousBlockHash;
    private final String protocolVersion;
    private final String merkleRoot;

    public BlockHeaderDTO(long timestamp, String previousBlockHash, String protocolVersion, String merkleRoot) {
        this.timestamp = timestamp;
        this.previousBlockHash = previousBlockHash;
        this.protocolVersion = protocolVersion;
        this.merkleRoot = merkleRoot;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }
}
