package pt.ipleiria.estg.dei.pi.voidchain.api.dtos;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

public class TransactionPostDTO implements Serializable {
    @NotNull(message = "Cannot create transaction without data")
    private String data;
    @NotNull(message = "Cannot create transaction without signature")
    private String signature;

    public TransactionPostDTO() {}

    public TransactionPostDTO(String data, String signature) {
        this.data = data;
        this.signature = signature;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
