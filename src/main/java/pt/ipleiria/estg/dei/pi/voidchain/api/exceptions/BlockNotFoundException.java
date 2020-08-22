package pt.ipleiria.estg.dei.pi.voidchain.api.exceptions;

import java.io.Serializable;

public class BlockNotFoundException extends Exception implements Serializable {
    public BlockNotFoundException() {
    }

    public BlockNotFoundException(String message) {
        super(message);
    }
}
