package pt.ipleiria.estg.dei.pi.voidchain.api.exceptions;

import java.io.Serializable;

public class InternalErrorException extends Exception implements Serializable {
    public InternalErrorException() {
    }

    public InternalErrorException(String message) {
        super(message);
    }
}
