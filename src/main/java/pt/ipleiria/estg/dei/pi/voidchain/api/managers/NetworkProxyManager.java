package pt.ipleiria.estg.dei.pi.voidchain.api.managers;

import bftsmart.tom.ServiceProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Transaction;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.TransactionStatus;
import pt.ipleiria.estg.dei.pi.voidchain.client.ClientMessage;
import pt.ipleiria.estg.dei.pi.voidchain.client.ClientMessageType;

import javax.swing.*;
import java.io.*;
import java.util.List;

public class NetworkProxyManager {
    private static NetworkProxyManager INSTANCE = null;

    private static final Logger logger = LoggerFactory.getLogger(NetworkProxyManager.class);

    private final ServiceProxy proxy;

    private NetworkProxyManager(int id) {
        this.proxy = new ServiceProxy(id);
    }

    private NetworkProxyManager(ServiceProxy proxy) {
        this.proxy = proxy;
    }

    public static NetworkProxyManager createInstance(int id) {
        if (INSTANCE == null)
            INSTANCE = new NetworkProxyManager(id);

        return INSTANCE;
    }

    public static NetworkProxyManager createInstance(ServiceProxy proxy) {
        if (INSTANCE == null)
            INSTANCE = new NetworkProxyManager(proxy);

        return INSTANCE;
    }

    public static NetworkProxyManager getInstance() {
        return INSTANCE;
    }

    public ServiceProxy getProxy() {
        return proxy;
    }

    public int getMostRecentBlockHeight() {
        logger.debug("Sending GET_MOST_RECENT_BLOCK_HEIGHT request to network");
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            ClientMessage req = new ClientMessage(ClientMessageType.GET_MOST_RECENT_BLOCK_HEIGHT);
            objOut.writeObject(req);

            objOut.flush();
            byteOut.flush();

            byte[] reply = proxy.invokeUnordered(byteOut.toByteArray());

            if (reply == null || reply.length == 0) {
                logger.error("Empty reply from network");
                return -1;
            }

            ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
            ObjectInput objIn = new ObjectInputStream(byteIn);

            int blockHeight = objIn.readInt();

            objIn.close();
            byteIn.close();

            logger.debug("Highest Block height in chain: " + blockHeight);
            return blockHeight;
        } catch (IOException ex) {
            logger.error("An error has occurred", ex);
            return -1;
        }
    }

    public boolean sendTransaction(Transaction transaction) {
        logger.debug("Sending ADD_TRANSACTION request to network");
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            ByteArrayOutputStream byteOut2 = new ByteArrayOutputStream();
            ObjectOutput objOut2 = new ObjectOutputStream(byteOut2);

            objOut2.writeObject(transaction);
            objOut2.flush();
            byteOut2.flush();

            byte[] tBytes = byteOut2.toByteArray();

            objOut2.close();
            byteOut2.close();

            ClientMessage req = new ClientMessage(ClientMessageType.ADD_TRANSACTION, tBytes);
            objOut.writeObject(req);

            objOut.flush();
            byteOut.flush();

            byte[] reply = proxy.invokeOrdered(byteOut.toByteArray());

            if (reply == null || reply.length == 0) {
                logger.error("Empty reply from network");
                return false;
            }

            boolean added;

            ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
            ObjectInput objIn = new ObjectInputStream(byteIn);

            added = objIn.readBoolean();

            objIn.close();
            byteIn.close();

            logger.debug("Transaction added: " + added);
            return added;
        } catch (IOException ex) {
            logger.error("An error has occurred", ex);
            return false;
        }
    }

    public boolean sendTransaction(List<Transaction> transactionList) {
        logger.debug("Sending ADD_TRANSACTIONS request to network");
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            ByteArrayOutputStream byteOut2 = new ByteArrayOutputStream();
            ObjectOutput objOut2 = new ObjectOutputStream(byteOut2);

            objOut2.writeObject(transactionList);
            objOut2.flush();
            byteOut2.flush();

            byte[] tBytes = byteOut2.toByteArray();

            objOut2.close();
            byteOut2.close();

            ClientMessage req = new ClientMessage(ClientMessageType.ADD_TRANSACTIONS, tBytes);
            objOut.writeObject(req);

            objOut.flush();
            byteOut.flush();

            byte[] reply = proxy.invokeOrdered(byteOut.toByteArray());

            if (reply == null || reply.length == 0) {
                logger.error("Empty reply from network");
                return false;
            }

            boolean added;

            ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
            ObjectInput objIn = new ObjectInputStream(byteIn);

            added = objIn.readBoolean();

            objIn.close();
            byteIn.close();

            logger.debug("Transactions added: " + added);
            return added;
        } catch (IOException ex) {
            logger.error("An error has occurred", ex);
            return false;
        }
    }

    public int getLastConsensusLeaderNode() {
        logger.debug("Sending GET_LEADER request to network");
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            ClientMessage req = new ClientMessage(ClientMessageType.GET_LEADER);
            objOut.writeObject(req);

            objOut.flush();
            byteOut.flush();

            byte[] reply = proxy.invokeUnordered(byteOut.toByteArray());

            if (reply == null || reply.length == 0) {
                logger.error("Empty reply from network");
                return -1;
            }

            ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
            ObjectInput objIn = new ObjectInputStream(byteIn);

            int leader = objIn.readInt();

            objIn.close();
            byteIn.close();

            logger.debug("Last consensus leader node id: " + leader);
            return leader;
        } catch (IOException ex) {
            logger.error("An error has occurred", ex);
            return -1;
        }
    }

    public TransactionStatus getTransactionStatus(byte[] transactionHash) {
        logger.debug("Sending TRANSACTION_STATUS request to network");
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            ClientMessage req = new ClientMessage(ClientMessageType.TRANSACTION_STATUS, transactionHash);
            objOut.writeObject(req);

            objOut.flush();
            byteOut.flush();

            byte[] reply = proxy.invokeUnordered(byteOut.toByteArray());

            if (reply == null || reply.length == 0) {
                logger.error("Empty reply from network");
                return TransactionStatus.UNKNOWN;
            }

            ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
            ObjectInput objIn = new ObjectInputStream(byteIn);

            TransactionStatus status = (TransactionStatus) objIn.readObject();

            objIn.close();
            byteIn.close();

            logger.debug("Transaction Status: " + status);
            return status;
        } catch (IOException | ClassNotFoundException ex) {
            logger.error("An error has occurred", ex);
            return TransactionStatus.UNKNOWN;
        }
    }

    public boolean isChainValid() {
        logger.debug("Sending IS_CHAIN_VALID request to network");
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            ClientMessage req = new ClientMessage(ClientMessageType.IS_CHAIN_VALID);
            objOut.writeObject(req);

            objOut.flush();
            byteOut.flush();

            byte[] reply = proxy.invokeOrdered(byteOut.toByteArray());

            if (reply == null || reply.length == 0) {
                logger.error("Empty reply from replicas");
                return false;
            }

            ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
            ObjectInput objIn = new ObjectInputStream(byteIn);

            boolean isValid = objIn.readBoolean();

            objIn.close();
            byteIn.close();

            logger.debug("Is network chain valid: " + isValid);
            return isValid;

        } catch (IOException ex) {
            logger.error("An error has occurred", ex);
            return false;
        }
    }

    public int getAmountOfNodes() {
        logger.debug("Sending NUMBER_NODES request to network");
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            ClientMessage req = new ClientMessage(ClientMessageType.NUMBER_NODES);
            objOut.writeObject(req);

            objOut.flush();
            byteOut.flush();

            byte[] reply = proxy.invokeUnordered(byteOut.toByteArray());

            if (reply == null || reply.length == 0) {
                logger.error("Empty reply from network");
                return -1;
            }

            ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
            ObjectInput objIn = new ObjectInputStream(byteIn);

            int numNodes = objIn.readInt();

            objIn.close();
            byteIn.close();

            logger.debug(numNodes + " nodes are connected");
            return numNodes;
        } catch (IOException ex) {
            logger.error("An error has occurred", ex);
            return -1;
        }
    }

    public void close() {
        this.proxy.close();

        INSTANCE = null;
    }
}
