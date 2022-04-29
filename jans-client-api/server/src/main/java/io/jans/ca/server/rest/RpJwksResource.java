package io.jans.ca.server.rest;

import io.jans.ca.common.CommandType;
import io.jans.ca.common.params.GetJwksParams;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class RpJwksResource extends BaseResource {

    @GET
    @Path("/get-rp-jwks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRpJwks() {
        logger.info("Api Resource: get-rp-jwks");
        String result = process(CommandType.GET_RP_JWKS, null, GetJwksParams.class, null, null);
        logger.info("Api Resource: get-rp-jwks - result:{}", result);

        return Response.ok(result).build();
    }
}
