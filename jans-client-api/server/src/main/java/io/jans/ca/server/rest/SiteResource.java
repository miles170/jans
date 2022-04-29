package io.jans.ca.server.rest;

import io.jans.ca.common.CommandType;
import io.jans.ca.common.params.GetDiscoveryParams;
import io.jans.ca.common.params.RegisterSiteParams;
import io.jans.ca.common.params.UpdateSiteParams;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class SiteResource extends BaseResource {

    @POST
    @Path("/register-site")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSite(String params) {
        logger.info("Api Resource: /register-site  Params: {}", params);
        String result = process(CommandType.REGISTER_SITE, params, RegisterSiteParams.class, null, null);
        logger.info("Api Resource: /register-site - result:{}", result);
        return Response.ok(result).build();
    }

    @POST
    @Path("/update-site")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSite(@HeaderParam("Authorization") String authorization, @HeaderParam("AuthorizationRpId") String AuthorizationRpId, String params) {
        logger.info("Api Resource: /update-site  Params: {}", params);
        String result = process(CommandType.UPDATE_SITE, params, UpdateSiteParams.class, authorization, AuthorizationRpId);
        logger.info("Api Resource: /update-site - result:{}", result);
        return Response.ok(result).build();
    }
}
