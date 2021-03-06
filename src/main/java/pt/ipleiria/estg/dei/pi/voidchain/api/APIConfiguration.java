package pt.ipleiria.estg.dei.pi.voidchain.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ipleiria.estg.dei.pi.voidchain.util.Configuration;
import pt.ipleiria.estg.dei.pi.voidchain.util.Storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The Api configuration loads and stores the configuration variables from configuration files.
 */
public class APIConfiguration {
    private static APIConfiguration INSTANCE = null;

    private static String CONFIG_FILE = Configuration.CONFIG_DIR + "voidchain-api.config";

    private static final Logger logger = LoggerFactory.getLogger(APIConfiguration.class);

    private boolean firstRun = true;

    /**
     * The constant CONFIG_FILES.
     */
    public static final List<String> CONFIG_FILES = new ArrayList<>() {{
        add("voidchain-api.config");
    }};

    /**
     * The constant DEFAULT_ID.
     */
    public static final int DEFAULT_ID = 1001;
    /**
     * The constant DEFAULT_BLOCK_SYNC_TIMER.
     */
    public static final int DEFAULT_BLOCK_SYNC_TIMER = 30000;
    /**
     * The constant DEFAULT_TRANSACTION_SUBMIT_TIMER.
     */
    public static final int DEFAULT_TRANSACTION_SUBMIT_TIMER = 5000;
    /**
     * The constant DEFAULT_MAX_TRANSACTION_SUBMIT.
     */
    public static final int DEFAULT_MAX_TRANSACTION_SUBMIT = 15;
    /**
     * The constant DEFAULT_NODE.
     */
    public static final boolean DEFAULT_NODE = false;
    /**
     * The constant DEFAULT_NODE_SYNC.
     */
    public static final boolean DEFAULT_NODE_SYNC = false;

    private int id = DEFAULT_ID;
    private int blockSyncTimer = DEFAULT_BLOCK_SYNC_TIMER;
    private int transactionSubmitTimer = DEFAULT_TRANSACTION_SUBMIT_TIMER;
    private int maxTransactionSubmit = DEFAULT_MAX_TRANSACTION_SUBMIT;
    private boolean node = DEFAULT_NODE;
    private boolean nodeSync = DEFAULT_NODE_SYNC;

    private APIConfiguration() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static APIConfiguration getInstance() {
        if (INSTANCE == null)
            INSTANCE = new APIConfiguration();

        INSTANCE.loadConfigurationFromDisk();

        return INSTANCE;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets block sync timer.
     *
     * @return the block sync timer
     */
    public int getBlockSyncTimer() {
        return blockSyncTimer;
    }

    /**
     * Gets send transactions timer.
     *
     * @return the send transactions timer
     */
    public int getSendTransactionsTimer() {
        return transactionSubmitTimer;
    }

    /**
     * Gets max number of transaction to send.
     *
     * @return the max number of transaction to send
     */
    public int getMaxNumberOfTransactionToSend() {
        return maxTransactionSubmit;
    }

    /**
     * Has node.
     *
     * @return the boolean
     */
    public boolean hasNode() {
        return node;
    }

    /**
     * Has sync.
     *
     * @return the boolean
     */
    public boolean hasSync() {
        if (!hasNode())
            return false;

        return nodeSync;
    }

    private void loadConfigurationFromDisk() {
        try {
            FileReader fr = new FileReader(CONFIG_FILE);
            BufferedReader rd = new BufferedReader(fr);

            String line;
            while ((line = rd.readLine()) != null) {
                if (line.startsWith("#")) continue;
                if (line.equalsIgnoreCase("")) continue;

                StringTokenizer str = new StringTokenizer(line, "=");
                if (str.countTokens() > 1) {
                    String aux;
                    switch (str.nextToken().trim()) {
                        case "system.voidchain.api.id":
                            if (firstRun) {
                                aux = str.nextToken().trim();
                                if (aux != null)
                                    this.id = Integer.parseInt(aux);
                            }
                            continue;
                        case "system.voidchain.api.block_sync_timer":
                            aux = str.nextToken().trim();
                            if (aux != null)
                                this.blockSyncTimer = Integer.parseInt(aux) * 1000;
                            continue;
                        case "system.voidchain.api.transaction_submit_timer":
                            aux = str.nextToken().trim();
                            if (aux != null)
                                this.transactionSubmitTimer = Integer.parseInt(aux) * 1000;
                            continue;
                        case "system.voidchain.api.max_transaction_to_submit":
                            aux = str.nextToken().trim();
                            if (aux != null)
                                this.maxTransactionSubmit = Integer.parseInt(aux);
                            continue;
                        case "system.voidchain.api.node":
                            if (firstRun) {
                                aux = str.nextToken().trim().toLowerCase();
                                if (aux != null)
                                    this.node = aux.equals("true");
                            }
                            continue;
                        case "system.voidchain.api.node.sync":
                            if (firstRun) {
                                aux = str.nextToken().trim().toLowerCase();
                                if (aux != null)
                                    this.nodeSync = aux.equals("true");
                            }
                            continue;
                    }
                }
            }

            fr.close();
            rd.close();

            this.firstRun = false;
        } catch (IOException e) {
            logger.error("Could not load configuration", e);
        }
    }

    /**
     * Create default config files.
     *
     * @throws IOException the io exception
     */
    public static void createDefaultConfigFiles() throws IOException {
        Path configDir = Paths.get(Configuration.CONFIG_DIR);

        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            logger.error("Unable to create config directory");
            throw new IOException("Unable to create config dir");
        }

        for (String f : APIConfiguration.CONFIG_FILES) {
            String filePath = configDir + File.separator + f;
            String filePathJar = "config/" + f;
            if (Files.notExists(Paths.get(filePath))) {
                logger.info("Creating file '" + f + "' in 'config'");

                InputStream in = Storage.class.getClassLoader().getResourceAsStream(filePathJar);
                File outFile = new File(filePath);
                FileOutputStream out = new FileOutputStream(outFile);

                outFile.createNewFile();

                out.write(in.readAllBytes());
                out.flush();
                out.close();
            }
        }
    }
}
