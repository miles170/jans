package io.jans.ca.server.rest;

import io.jans.ca.common.CommandType;
import io.jans.ca.common.params.GetClientTokenParams;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class GetClientTokenResource extends BaseResource {

    @POST
    @Path("/get-client-token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientToken(String params) {
        logger.info("Api Resource: /get-client-token  Params: {}", params);
        String result = process(CommandType.GET_CLIENT_TOKEN, params, GetClientTokenParams.class, null, null);
        logger.info("Api Resource: /get-client-token - result:{}", result);

        return Response.ok(result).build();
    }
}
