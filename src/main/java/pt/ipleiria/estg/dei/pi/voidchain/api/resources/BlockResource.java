package pt.ipleiria.estg.dei.pi.voidchain.api.resources;

import bitcoinj.Base58;

import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.BlockDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.BlockHeaderDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.dtos.BlockNoTransactionsDTO;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.BlockNotFoundException;
import pt.ipleiria.estg.dei.pi.voidchain.api.exceptions.InternalErrorException;
import pt.ipleiria.estg.dei.pi.voidchain.api.managers.BlockchainManager;
import pt.ipleiria.estg.dei.pi.voidchain.blockchain.Block;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Block resource defines all possible requests related to returning block information to the client.
 */
@Path("/block")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlockResource {

    /**
     * To dto block dto.
     *
     * @param block the block
     * @return the block dto
     */
    public static BlockDTO toDTO(Block block) {
        return new BlockDTO(
                TransactionResource.toDTOs(new ArrayList<>(block.getTransactions().values())),
                new BlockHeaderDTO(
                        block.getTimestamp(),
                        Base58.encode(block.getPreviousBlockHash()),
                        block.getProtocolVersion(),
                        Base58.encode(block.getMerkleRoot())
                ),
                block.getTransactionCounter(),
                block.getBlockHeight(),
                Base58.encode(block.getHash()),
                block.getSize()
        );
    }

    /**
     * To dt os list.
     *
     * @param blocks the blocks
     * @return the list
     */
    public static List<BlockDTO> toDTOs(List<Block> blocks) {
        return blocks.stream().map(BlockResource::toDTO).collect(Collectors.toList());
    }

    /**
     * To dto no transaction block no transactions dto.
     *
     * @param block the block
     * @return the block no transactions dto
     */
    public static BlockNoTransactionsDTO toDTONoTransaction(Block block) {
        return new BlockNoTransactionsDTO(
                new BlockHeaderDTO(
                        block.getTimestamp(),
                        Base58.encode(block.getPreviousBlockHash()),
                        block.getProtocolVersion(),
                        Base58.encode(block.getMerkleRoot())
                ),
                block.getTransactionCounter(),
                block.getBlockHeight(),
                Base58.encode(block.getHash()),
                block.getSize()
        );
    }

    /**
     * To dt os no transacion list.
     *
     * @param blocks the blocks
     * @return the list
     */
    public static List<BlockNoTransactionsDTO> toDTOsNoTransacion(List<Block> blocks) {
        return blocks.stream().map(BlockResource::toDTONoTransaction).collect(Collectors.toList());
    }

    /**
     * Gets all blocks.
     *
     * @return the all blocks
     * @throws InternalErrorException the internal error exception
     */
    @GET
    @Path("/")
    public Response getAllBlocks() throws InternalErrorException {
        return Response.ok(toDTOs(BlockchainManager.getInstance().getAllBlocks())).build();
    }

    /**
     * Gets all blocks no transactions.
     *
     * @return the all blocks no transactions
     * @throws InternalErrorException the internal error exception
     */
    @GET
    @Path("/no-transactions")
    public Response getAllBlocksNoTransactions() throws InternalErrorException {
        return Response.ok(toDTOsNoTransacion(BlockchainManager.getInstance().getAllBlocks())).build();
    }

    /**
     * Gets block.
     *
     * @param blockId the block id
     * @return the block
     * @throws BlockNotFoundException the block not found exception
     */
    @GET
    @Path("/{bId}")
    public Response getBlock(@PathParam("bId") String blockId) throws BlockNotFoundException {
        return Response.ok(toDTO(BlockchainManager.getInstance().getBlock(blockId))).build();
    }

    /**
     * Gets block no transactions.
     *
     * @param blockId the block id
     * @return the block no transactions
     * @throws BlockNotFoundException the block not found exception
     */
    @GET
    @Path("/{bId}/no-transactions")
    public Response getBlockNoTransactions(@PathParam("bId") String blockId) throws BlockNotFoundException {
        return Response.ok(toDTONoTransaction(BlockchainManager.getInstance().getBlock(blockId))).build();
    }

    /**
     * Gets block.
     *
     * @param blockHeight the block height
     * @return the block
     * @throws BlockNotFoundException the block not found exception
     */
    @GET
    @Path("/height/{bHeight}")
    public Response getBlock(@PathParam("bHeight") int blockHeight) throws BlockNotFoundException {
        return Response.ok(toDTO(BlockchainManager.getInstance().getBlock(blockHeight))).build();
    }

    /**
     * Gets block no transactions.
     *
     * @param blockHeight the block height
     * @return the block no transactions
     * @throws BlockNotFoundException the block not found exception
     */
    @GET
    @Path("/height/{bHeight}/no-transactions")
    public Response getBlockNoTransactions(@PathParam("bHeight") int blockHeight) throws BlockNotFoundException {
        return Response.ok(toDTONoTransaction(BlockchainManager.getInstance().getBlock(blockHeight))).build();
    }
}
