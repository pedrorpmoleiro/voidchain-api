package pt.ipleiria.estg.dei.pi.voidchain.api.resources;

import org.bouncycastle.util.encoders.Base64;
import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.BlockDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.BlockHeaderDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.BlockNoTransactionsDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.BlockNotFoundException;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.InternalErrorException;
import pt.ipleiria.estg.dei.pi.voidchain.api.managers.BlockchainManager;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Block;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.BlockHeader;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/block")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlockResource {

    public static BlockDTO toDTO(Block block) {
        return new BlockDTO(
                TransactionResource.toDTOs(new ArrayList<>(block.getTransactions().values())),
                new BlockHeaderDTO(
                        block.getTimestamp(),
                        Base64.toBase64String(block.getPreviousBlockHash()),
                        block.getProtocolVersion(),
                        Base64.toBase64String(block.getMerkleRoot())
                ),
                block.getTransactionCounter(),
                block.getBlockHeight(),
                Base64.toBase64String(block.getHash()),
                block.getSize()
        );
    }

    public static List<BlockDTO> toDTOs(List<Block> blocks) {
        return blocks.stream().map(BlockResource::toDTO).collect(Collectors.toList());
    }

    public static BlockNoTransactionsDTO toDTONoTransaction(Block block) {
        return new BlockNoTransactionsDTO(
                new BlockHeaderDTO(
                        block.getTimestamp(),
                        Base64.toBase64String(block.getPreviousBlockHash()),
                        block.getProtocolVersion(),
                        Base64.toBase64String(block.getMerkleRoot())
                ),
                block.getTransactionCounter(),
                block.getBlockHeight(),
                Base64.toBase64String(block.getHash()),
                block.getSize()
        );
    }

    public static List<BlockNoTransactionsDTO> toDTOsNoTransacion(List<Block> blocks) {
        return blocks.stream().map(BlockResource::toDTONoTransaction).collect(Collectors.toList());
    }

    @GET
    @Path("/")
    public Response getAllBlocks() throws InternalErrorException {
        return Response.status(Response.Status.OK).entity(toDTOs(BlockchainManager.getInstance().getAllBlocks()))
                .build();
    }

    @GET
    @Path("/no-transactions")
    public Response getAllBlocksNoTransactions() throws InternalErrorException {
        return Response.status(Response.Status.OK).entity(toDTOsNoTransacion(BlockchainManager.getInstance()
                .getAllBlocks())).build();
    }

    @GET
    @Path("/{bId}")
    public Response getBlock(@PathParam("bId") String blockId) throws BlockNotFoundException {
        return Response.status(Response.Status.OK).entity(toDTO(BlockchainManager.getInstance()
                .getBlock(blockId))).build();
    }

    @GET
    @Path("/{bId}/no-transactions")
    public Response getBlockNoTransactions(@PathParam("bId") String blockId) throws BlockNotFoundException {
        return Response.status(Response.Status.OK).entity(toDTONoTransaction(BlockchainManager.getInstance()
                .getBlock(blockId))).build();
    }

    @GET
    @Path("/height/{bHeight}")
    public Response getBlock(@PathParam("bHeight") int blockHeight) throws BlockNotFoundException {
        return Response.status(Response.Status.OK).entity(toDTO(BlockchainManager.getInstance()
                .getBlock(blockHeight))).build();
    }

    @GET
    @Path("/height/{bHeight}/no-transactions")
    public Response getBlockNoTransactions(@PathParam("bHeight") int blockHeight) throws BlockNotFoundException {
        return Response.status(Response.Status.OK).entity(toDTONoTransaction(BlockchainManager.getInstance()
                .getBlock(blockHeight))).build();
    }
}
