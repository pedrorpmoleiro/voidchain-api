package pt.ipleiria.estg.dei.pi.voidchain.api.resources;

import pt.ipleiria.estg.dei.pi.voidchain.api.managers.BlockchainManager;
import pt.ipleiria.estg.dei.pi.voidchain.api.managers.NetworkProxyManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/blockchain")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlockchainResource {

    @GET
    @Path("/valid/local")
    public Response isLocalChainValid() {
        return Response.ok(BlockchainManager.getInstance().isChainValid() ? "valid" : "invalid").build();
    }

    @GET
    @Path("/valid")
    public Response isNetworkChainValid() {
        return Response.ok(NetworkProxyManager.getInstance().isChainValid() ? "valid" : "invalid").build();
    }
}
