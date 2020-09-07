package pt.ipleiria.estg.dei.pi.voidchain.api.dtos;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

public class TransactionPostDTO implements Serializable {

    @NotNull(message = "Cannot create transaction without data")
    private String data;
    @NotNull(message = "Cannot create transaction without signature")
    private String signature;

    /**
     * Instantiates a new Transaction Data Transfer Object (POST) used to create a new transaction.
     */
    public TransactionPostDTO() {}

    /**
     * Instantiates a new Transaction post dto.
     *
     * @param data      the data
     * @param signature the signature
     */
    public TransactionPostDTO(String data, String signature) {
        this.data = data;
        this.signature = signature;
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
     * Sets data.
     *
     * @param data the data
     */
    public void setData(String data) {
        this.data = data;
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
     * Sets signature.
     *
     * @param signature the signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }
}
