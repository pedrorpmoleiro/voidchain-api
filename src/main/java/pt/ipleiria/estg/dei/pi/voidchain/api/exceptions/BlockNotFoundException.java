package pt.ipleiria.estg.dei.pi.voidchain.api.exceptions;

import java.io.Serializable;

/**
 * The Block not found exception.
 */
public class BlockNotFoundException extends Exception implements Serializable {
    /**
     * Instantiates a new Block not found exception.
     */
    public BlockNotFoundException() {
    }

    /**
     * Instantiates a new Block not found exception.
     *
     * @param message the message
     */
    public BlockNotFoundException(String message) {
        super(message);
    }
}
