package pt.ipleiria.estg.dei.pi.voidchain.api.exceptions;

import java.io.Serializable;

/**
 * The Transaction too big exception.
 */
public class TransactionTooBigException extends Exception implements Serializable {
    /**
     * Instantiates a new Transaction too big exception.
     */
    public TransactionTooBigException() {}

    /**
     * Instantiates a new Transaction too big exception.
     *
     * @param message the message
     */
    public TransactionTooBigException(String message) {
        super(message);
    }
}
