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

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    public static TransactionGetDTO toDTO(Transaction transaction) {
        return new TransactionGetDTO(
                transaction.getTimestamp(),
                Base58.encode(transaction.getData()),
                transaction.getProtocolVersion(),
                Base58.encode(transaction.getSignature()),
                Base58.encode(transaction.getHash())
        );
    }

    public static List<TransactionGetDTO> toDTOs(List<Transaction> transactions) {
        return transactions.stream().map(TransactionResource::toDTO).collect(Collectors.toList());
    }

    @POST
    @Path("/")
    public Response create(@Valid TransactionPostDTO transactionPost) throws TransactionTooBigException {
        Transaction t = BlockchainManager.getInstance().createAndAddTransactionToPool(transactionPost);

        return Response.status(Response.Status.CREATED).entity(toDTO(t)).build();
    }

    @GET
    @Path("/{txId}")
    public Response getTransaction(@PathParam("txId") String idString) throws TransactionNotFoundException {
        Transaction t = BlockchainManager.getInstance().getTransaction(idString);

        return Response.ok(toDTO(t)).build();
    }

    @GET
    @Path("/status/{txId}")
    public Response getTransactionStatus(@PathParam("txId") String idString) {
        return Response.ok(BlockchainManager.getInstance().getTransactionStatus(idString)).build();
    }

    @GET
    @Path("/block/{bId}")
    public Response getAllTransactionsInBlock(@PathParam("bId") String blockId) throws BlockNotFoundException {
        BlockchainManager blockchainManager = BlockchainManager.getInstance();

        return Response.ok(toDTOs(blockchainManager.getTransactionInBlock(blockchainManager.getBlock(blockId))))
                .build();
    }

    @GET
    @Path("/block/height/{bHeight}")
    public Response getAllTransactionsInBlock(@PathParam("bHeight") int blockHeight) throws BlockNotFoundException {
        BlockchainManager blockchainManager = BlockchainManager.getInstance();

        return Response.ok(toDTOs(blockchainManager.getTransactionInBlock(blockchainManager.getBlock(blockHeight))))
                .build();
    }

    @GET
    @Path("/memory-pool")
    public Response getAllTransactionsInMemPool() {
        return Response.ok(toDTOs(BlockchainManager.getInstance().getTransactionPool())).build();
    }

    @GET
    @Path("/user/{oId}")
    public Response getAllTransactionsOfOwner(@PathParam("oId") String ownerId) throws InternalErrorException {
        return Response.ok(toDTOs(BlockchainManager.getInstance().getTransactionOfOwner(ownerId))).build();
    }
}
