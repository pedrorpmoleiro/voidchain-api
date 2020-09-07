package pt.ipleiria.estg.dei.pi.voidchain.api.resources;

import bitcoinj.Base58;

import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.TransactionGetDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.TransactionPostDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.BlockNotFoundException;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.InternalErrorException;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.TransactionNotFoundException;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.TransactionTooBigException;
import pt.ipleiria.estg.dei.pi.voidchain.api.managers.BlockchainManager;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Transaction;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The Transaction resource defines all possible requests related to creating and returning transaction information to
 * the client.
 */
@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    /**
     * To dto transaction get dto.
     *
     * @param transaction the transaction
     * @return the transaction get dto
     */
    public static TransactionGetDTO toDTO(Transaction transaction) {
        return new TransactionGetDTO(
                transaction.getTimestamp(),
                Base58.encode(transaction.getData()),
                transaction.getProtocolVersion(),
                Base58.encode(transaction.getSignature()),
                Base58.encode(transaction.getHash())
        );
    }

    /**
     * To dt os list.
     *
     * @param transactions the transactions
     * @return the list
     */
    public static List<TransactionGetDTO> toDTOs(List<Transaction> transactions) {
        return transactions.stream().map(TransactionResource::toDTO).collect(Collectors.toList());
    }

    /**
     * Create response.
     *
     * @param transactionPost the transaction post
     * @return the response
     * @throws TransactionTooBigException the transaction too big exception
     */
    @POST
    @Path("/")
    public Response create(@Valid TransactionPostDTO transactionPost) throws TransactionTooBigException {
        Transaction t = BlockchainManager.getInstance().createAndAddTransactionToPool(transactionPost);

        return Response.status(Response.Status.CREATED).entity(toDTO(t)).build();
    }

    /**
     * Gets transaction.
     *
     * @param idString the id string
     * @return the transaction
     * @throws TransactionNotFoundException the transaction not found exception
     */
    @GET
    @Path("/{txId}")
    public Response getTransaction(@PathParam("txId") String idString) throws TransactionNotFoundException {
        Transaction t = BlockchainManager.getInstance().getTransaction(idString);

        return Response.ok(toDTO(t)).build();
    }

    /**
     * Gets transaction status.
     *
     * @param idString the id string
     * @return the transaction status
     */
    @GET
    @Path("/status/{txId}")
    public Response getTransactionStatus(@PathParam("txId") String idString) {
        return Response.ok(BlockchainManager.getInstance().getTransactionStatus(idString)).build();
    }

    /**
     * Gets all transactions in block.
     *
     * @param blockId the block id
     * @return the all transactions in block
     * @throws BlockNotFoundException the block not found exception
     */
    @GET
    @Path("/block/{bId}")
    public Response getAllTransactionsInBlock(@PathParam("bId") String blockId) throws BlockNotFoundException {
        BlockchainManager blockchainManager = BlockchainManager.getInstance();

        return Response.ok(toDTOs(blockchainManager.getTransactionInBlock(blockchainManager.getBlock(blockId))))
                .build();
    }

    /**
     * Gets all transactions in block.
     *
     * @param blockHeight the block height
     * @return the all transactions in block
     * @throws BlockNotFoundException the block not found exception
     */
    @GET
    @Path("/block/height/{bHeight}")
    public Response getAllTransactionsInBlock(@PathParam("bHeight") int blockHeight) throws BlockNotFoundException {
        BlockchainManager blockchainManager = BlockchainManager.getInstance();

        return Response.ok(toDTOs(blockchainManager.getTransactionInBlock(blockchainManager.getBlock(blockHeight))))
                .build();
    }

    /**
     * Gets all transactions in mem pool.
     *
     * @return the all transactions in mem pool
     */
    @GET
    @Path("/memory-pool")
    public Response getAllTransactionsInMemPool() {
        return Response.ok(toDTOs(BlockchainManager.getInstance().getTransactionPool())).build();
    }

    /**
     * Gets all transactions of owner.
     *
     * @param ownerId the owner id
     * @return the all transactions of owner
     * @throws InternalErrorException the internal error exception
     */
    @GET
    @Path("/user/{oId}")
    public Response getAllTransactionsOfOwner(@PathParam("oId") String ownerId) throws InternalErrorException {
        return Response.ok(toDTOs(BlockchainManager.getInstance().getTransactionOfOwner(ownerId))).build();
    }
}
