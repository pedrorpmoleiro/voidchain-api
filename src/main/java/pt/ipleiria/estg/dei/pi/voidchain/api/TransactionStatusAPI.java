package pt.ipleiria.estg.dei.pi.voidchain.api;

import java.io.Serializable;

public enum TransactionStatusAPI implements Serializable {
    IN_BLOCK,
    IN_NETWORK_MEM_POOL,
    IN_API_MEM_POOL,
    UNKNOWN
}
