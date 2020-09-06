package pt.ipleiria.estg.dei.pi.voidchain.api.dtos;

import java.io.Serializable;

public class TransactionGetDTO implements Serializable {
    private final long timestamp;
    private final String data;
    private final String protocolVersion;
    private final String signature;
    private final String hash;

    public TransactionGetDTO(long timestamp, String data, String protocolVersion, String signature, String hash) {
        this.timestamp = timestamp;
        this.data = data;
        this.protocolVersion = protocolVersion;
        this.signature = signature;
        this.hash = hash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getData() {
        return data;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getSignature() {
        return signature;
    }

    public String getHash() {
        return hash;
    }
}
