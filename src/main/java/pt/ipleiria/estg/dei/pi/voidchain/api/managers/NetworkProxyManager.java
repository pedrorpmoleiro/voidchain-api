package pt.ipleiria.estg.dei.pi.voidchain.api.managers;

import bftsmart.tom.ServiceProxy;

/*
 * TODO
 *  Add all communication to be done with the node network in this class with methods
 */
public class NetworkProxyManager {
    private static NetworkProxyManager INSTANCE = null;

    private final ServiceProxy proxy;

    private NetworkProxyManager(int id) {
        this.proxy = new ServiceProxy(id);
    }

    public static NetworkProxyManager getInstance(int id) {
        if (INSTANCE == null)
            INSTANCE = new NetworkProxyManager(id);

        return INSTANCE;
    }

    public ServiceProxy getProxy() {
        return proxy;
    }
}
