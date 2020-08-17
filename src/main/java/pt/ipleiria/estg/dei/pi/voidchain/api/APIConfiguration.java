package pt.ipleiria.estg.dei.pi.voidchain.api;

public class APIConfiguration {
    private static APIConfiguration INSTANCE = null;

    private APIConfiguration() {}

    public static APIConfiguration getInstance() {
        if (INSTANCE == null)
            INSTANCE = new APIConfiguration();

        return INSTANCE;
    }

    public int getId() {
        return 10;
    }

    public int getBlockSyncTimer() {
        return 60 * 1000;
    }
}
