package pt.ipleiria.estg.dei.pi.voidchain.api.resources;

import pt.ipleiria.estg.dei.pi.voidchain.api.managers.NetworkProxyManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The Network resource defines all possible requests related to information of the node network.
 */
@Path("/network")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NetworkResource {

    /**
     * Gets leader.
     *
     * @return the leader
     */
    @GET
    @Path("/leader")
    public Response getLeader() {
        int leader = NetworkProxyManager.getInstance().getLastConsensusLeaderNode();

        if (leader < 0)
            return Response.ok(leader).build();
        else
            return Response.ok("Unable to get leader").build();
    }

    /**
     * Gets amount of nodes.
     *
     * @return the amount of nodes
     */
    @GET
    @Path("/nodes/number")
    public Response getAmountOfNodes() {
        return Response.ok(NetworkProxyManager.getInstance().getAmountOfNodes()).build();
    }
}
