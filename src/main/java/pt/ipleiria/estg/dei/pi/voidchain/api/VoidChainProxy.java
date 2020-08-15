package pt.ipleiria.estg.dei.pi.voidchain.api;

import bftsmart.tom.ServiceProxy;

/*
 * TODO
 *  Add all communication to be done with the node network in this class with methods
 */
public class VoidChainProxy {
    private static VoidChainProxy INSTANCE = null;

    private final ServiceProxy proxy;

    private VoidChainProxy(int id) {
        this.proxy = new ServiceProxy(id);
    }

    public static VoidChainProxy getInstance(int id) {
        if (INSTANCE == null)
            INSTANCE = new VoidChainProxy(id);

        return INSTANCE;
    }

    public ServiceProxy getProxy() {
        return proxy;
    }
}
