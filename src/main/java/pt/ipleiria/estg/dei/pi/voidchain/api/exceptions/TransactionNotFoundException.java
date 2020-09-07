package pt.ipleiria.estg.dei.pi.voidchain.api.exceptions;

import java.io.Serializable;

/**
 * The Transaction not found exception.
 */
public class TransactionNotFoundException extends Exception implements Serializable {
    /**
     * Instantiates a new Transaction not found exception.
     */
    public TransactionNotFoundException() {
    }

    /**
     * Instantiates a new Transaction not found exception.
     *
     * @param message the message
     */
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
