package pt.ipleiria.estg.dei.pi.voidchain.api.dtos;

import java.io.Serializable;

public class TransactionGetDTO implements Serializable {
    private final long timestamp;
    private final String data;
    private final String protocolVersion;
    private final String signature;
    private final String hash;

    /**
     * Instantiates a new Transaction Data Transfer Object for get requests.
     *
     * @param timestamp       the timestamp
     * @param data            the data
     * @param protocolVersion the protocol version
     * @param signature       the signature
     * @param hash            the hash
     */
    public TransactionGetDTO(long timestamp, String data, String protocolVersion, String signature, String hash) {
        this.timestamp = timestamp;
        this.data = data;
        this.protocolVersion = protocolVersion;
        this.signature = signature;
        this.hash = hash;
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
     * Gets data.
     *
     * @return the data
     */
    public String getData() {
        return data;
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
     * Gets signature.
     *
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Gets hash.
     *
     * @return the hash
     */
    public String getHash() {
        return hash;
    }
}
