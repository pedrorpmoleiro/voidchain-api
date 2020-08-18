package pt.ipleiria.estg.dei.pi.voidchain.api.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    /*
     * TODO
     *  TO DTO
     *  Finish
     */

    @POST
    @Path("/")
    public Response create() {
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{id}")
    public Response getTransaction(@PathParam("id") String idString) {
        return Response.status(Response.Status.FOUND).entity(idString).build();
    }
}
