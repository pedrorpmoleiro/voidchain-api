package pt.ipleiria.estg.dei.pi.voidchain.api;

import java.io.Serializable;

/**
 * The enum Transaction status api.
 */
public enum TransactionStatusAPI implements Serializable {
    /**
     * In block transaction status api.
     */
    IN_BLOCK,
    /**
     * In network mem pool transaction status api.
     */
    IN_NETWORK_MEM_POOL,
    /**
     * In api mem pool transaction status api.
     */
    IN_API_MEM_POOL,
    /**
     * Unknown transaction status api.
     */
    UNKNOWN
}
