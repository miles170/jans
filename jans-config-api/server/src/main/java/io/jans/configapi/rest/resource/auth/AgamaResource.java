/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.configapi.rest.resource.auth;

import io.jans.agama.model.Flow;
import static io.jans.as.model.util.Util.escapeLog;
import com.github.fge.jsonpatch.JsonPatchException;
import io.jans.configapi.core.rest.ProtectedApi;
import io.jans.configapi.service.auth.AgamaFlowService;
import io.jans.configapi.util.ApiAccessConstants;
import io.jans.configapi.util.ApiConstants;
import io.jans.configapi.core.util.Jackson;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

@Path(ApiConstants.AGAMA)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AgamaResource extends ConfigBaseResource {

    private static final String AGAMA_FLOW = "Agama flow";
    private static final String AGAMA_QName = "FlowName";
    private static final String AGAMA_SOURCE = "source";

    @Inject
    Logger log;

    @Inject
    AgamaFlowService agamaFlowService;

    @GET
    @ProtectedApi(scopes = { ApiAccessConstants.AGAMA_READ_ACCESS })
    public Response getFlows(@DefaultValue("") @QueryParam(value = ApiConstants.PATTERN) String pattern,
            @DefaultValue(DEFAULT_LIST_SIZE) @QueryParam(value = ApiConstants.LIMIT) int limit) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("Search Agama Flow with pattern:{}, sizeLimit:{}, ", escapeLog(pattern), escapeLog(limit));
        }

        List<Flow> flows = new ArrayList<Flow>();
        if (!pattern.isEmpty() && pattern.length() >= 2) {
            flows = agamaFlowService.searchAgamaFlows(pattern, limit);
        } else {
            flows = agamaFlowService.getAllAgamaFlows(limit);
        }

        return Response.ok(flows).build();
    }

    @GET
    @ProtectedApi(scopes = { ApiAccessConstants.AGAMA_READ_ACCESS })
    @Path(ApiConstants.QNAME_PATH)
    public Response getFlowByName(@PathParam(ApiConstants.QNAME) @NotNull String flowName) {
        if (log.isDebugEnabled()) {
            log.debug("Search Agama with flowName:{}, ", escapeLog(flowName));
        }
        Flow flow = agamaFlowService.getFlowByName(flowName);

        return Response.ok(flow).build();
    }

    @POST
    @ProtectedApi(scopes = { ApiAccessConstants.AGAMA_WRITE_ACCESS })
    public Response createFlow(@Valid Flow flow) {
        log.debug(" Flow to be added flow:{}, flow.getQName():{}, flow.getSource():{} ", flow, flow.getQname(),
                flow.getSource());

        // validate data
        validateAgamaFlowData(flow);
        agamaFlowService.addAgamaFlow(flow);

        flow = agamaFlowService.getFlowByName(flow.getQname());
        return Response.status(Response.Status.CREATED).entity(flow).build();
    }

    @DELETE
    @Path(ApiConstants.QNAME_PATH)
    @ProtectedApi(scopes = { ApiAccessConstants.AGAMA_DELETE_ACCESS })
    public Response deleteAttribute(@PathParam(ApiConstants.QNAME) @NotNull String flowName) {
        log.debug(" Flow to delete - flowName:{}", flowName);
        Flow flow = agamaFlowService.getFlowByName(flowName);
        checkResourceNotNull(flow, AGAMA_FLOW);
        agamaFlowService.removeAgamaFlow(flow);
        return Response.noContent().build();
    }

    private void validateAgamaFlowData(Flow flow) {
        if (flow == null) {
            return;
        }

        log.debug(" Validate Agama Flow to be added flow:{}, flow.getQname():{}, flow.getSource():{} ", flow,
                flow.getQname(), flow.getSource());
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(flow.getQname())) {
            sb.append(AGAMA_QName).append(",");
        }

        if (StringUtils.isBlank(flow.getSource())) {
            sb.append(AGAMA_SOURCE).append(",");
        }

        log.debug(" sb:{} ", sb);
        if (sb.length() > 0) {
            sb.insert(0, "Required feilds missing -> ");
            sb.replace(sb.lastIndexOf(","), sb.length(), "");
            thorwBadRequestException(sb.toString());
        }

    }
}
