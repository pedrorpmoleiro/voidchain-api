package pt.ipleiria.estg.dei.pi.voidchain.api.exceptions;

import java.io.Serializable;

public class TransactionNotFoundException extends Exception implements Serializable {
    public TransactionNotFoundException(String message) {
        super(message);
    }

    public TransactionNotFoundException() {
    }
}
