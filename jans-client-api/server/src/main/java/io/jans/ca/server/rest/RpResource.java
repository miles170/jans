package io.jans.ca.server.rest;

import io.jans.ca.common.CommandType;
import io.jans.ca.common.params.GetJwksParams;
import io.jans.ca.common.params.GetRpParams;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class RpResource extends BaseResource {

    @GET
    @Path("/get-rp-jwks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRpJwks() {
        logger.info("Api Resource: get-rp-jwks");
        String result = process(CommandType.GET_RP_JWKS, null, GetJwksParams.class, null, null);
        logger.info("Api Resource: get-rp-jwks - result:{}", result);

        return Response.ok(result).build();
    }

    @POST
    @Path("/get-rp")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRp(@HeaderParam("Authorization") String authorization, @HeaderParam("AuthorizationRpId") String authorizationRpId, String params) {
        logger.info("Api Resource: get-rp");
        String result = process(CommandType.GET_RP, params, GetRpParams.class, authorization, authorizationRpId);
        logger.info("Api Resource: get-rp - result:{}", result);

        return Response.ok(result).build();
    }
}
