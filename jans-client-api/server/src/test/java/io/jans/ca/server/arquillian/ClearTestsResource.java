package io.jans.ca.server.arquillian;

import io.jans.ca.server.persistence.service.PersistenceService;
import io.jans.ca.server.service.RpService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;

@Path("/")
public class ClearTestsResource {

    @Inject
    Logger logger;

    @Inject
    RpService rpService;
    @Inject
    PersistenceService persistenceService;


    @GET
    @Path("/clear-tests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearTests() {
        try {
            persistenceService.create();
            rpService.removeAllRps();
            rpService.load();
            logger.debug("Finished removeExistingRps successfullly.");
            return Response.ok("OK").build();
        } catch (Exception e) {
            logger.error("Failed to removed existing RPs.", e);
            return Response.status(500, "Failed to removed existing RPs.").build();
        }
    }
}
