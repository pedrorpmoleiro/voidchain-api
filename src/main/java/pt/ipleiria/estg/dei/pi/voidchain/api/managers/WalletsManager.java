package pt.ipleiria.estg.dei.pi.voidchain.api.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ipleiria.estg.dei.pi.voidchain.client.Wallet;

import java.util.ArrayList;
import java.util.List;

// TODO: Complete
public class WalletsManager {
    private static WalletsManager INSTANCE = null;

    private static final Logger logger = LoggerFactory.getLogger(WalletsManager.class);

    private final List<Wallet> wallets;

    private WalletsManager() {
        this.wallets = new ArrayList<>();
    }

    private WalletsManager(List<Wallet> wallets) {
        this.wallets = new ArrayList<>(wallets);
    }

    public static WalletsManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new WalletsManager();

        return INSTANCE;
    }
}
