package pt.ipleiria.estg.dei.pi.voidchain.api.exceptions;

import java.io.Serializable;

/**
 * The Internal error exception.
 */
public class InternalErrorException extends Exception implements Serializable {
    /**
     * Instantiates a new Internal error exception.
     */
    public InternalErrorException() {
    }

    /**
     * Instantiates a new Internal error exception.
     *
     * @param message the message
     */
    public InternalErrorException(String message) {
        super(message);
    }
}
