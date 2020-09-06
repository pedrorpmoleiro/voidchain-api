package pt.ipleiria.estg.dei.pi.voidchain.api.exceptions;

import java.io.Serializable;

public class TransactionTooBigException extends Exception implements Serializable {
    public TransactionTooBigException() {}

    public TransactionTooBigException(String message) {
        super(message);
    }
}
