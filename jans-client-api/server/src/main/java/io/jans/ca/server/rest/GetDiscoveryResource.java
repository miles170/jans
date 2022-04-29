package io.jans.ca.server.rest;

import io.jans.ca.common.CommandType;
import io.jans.ca.common.params.GetDiscoveryParams;
import io.jans.ca.common.params.GetJwksParams;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class GetDiscoveryResource extends BaseResource {

    @POST
    @Path("/get-discovery")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscovery(String params) {
        logger.info("Api Resource: /get-rp-jwks  Params: {}", params);
        String result = process(CommandType.GET_DISCOVERY, params, GetDiscoveryParams.class, null, null);
        logger.info("Api Resource: /get-rp-jwks - result:{}", result);

        return Response.ok(result).build();
    }
}
