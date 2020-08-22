package pt.ipleiria.estg.dei.pi.voidchain.api.resources;

import org.bouncycastle.util.encoders.Base64;

import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.TransactionGetDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.TransactionPostDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.BlockNotFoundException;
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
                Base64.toBase64String(transaction.getData()),
                transaction.getProtocolVersion(),
                Base64.toBase64String(transaction.getSignature()),
                Base64.toBase64String(transaction.getHash())
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
        byte[] transactionId = Base64.decode(idString);

        Transaction t = BlockchainManager.getInstance().getTransaction(transactionId);

        return Response.status(Response.Status.OK).entity(toDTO(t)).build();
    }

    @GET
    @Path("/block/{bId}")
    public Response getAllTransactionsInBlock(@PathParam("bId") String blockId) throws BlockNotFoundException {
        BlockchainManager blockchainManager = BlockchainManager.getInstance();

        return Response.status(Response.Status.OK).entity(toDTOs(blockchainManager.
                getTransactionInBlock(blockchainManager.getBlock(blockId)))).build();
    }

    @GET
    @Path("/block/height/{bHeight}")
    public Response getAllTransactionsInBlock(@PathParam("bHeight") int blockHeight) throws BlockNotFoundException {
        BlockchainManager blockchainManager = BlockchainManager.getInstance();

        return Response.status(Response.Status.OK).entity(toDTOs(blockchainManager.
                getTransactionInBlock(blockchainManager.getBlock(blockHeight)))).build();
    }
}
