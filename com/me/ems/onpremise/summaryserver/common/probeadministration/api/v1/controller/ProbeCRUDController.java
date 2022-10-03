package com.me.ems.onpremise.summaryserver.common.probeadministration.api.v1.controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import org.json.JSONObject;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Map;
import com.me.ems.onpremise.summaryserver.common.probeadministration.api.v1.service.ProbeCRUDService;
import javax.ws.rs.Path;

@Path("")
public class ProbeCRUDController
{
    private ProbeCRUDService probeService;
    
    public ProbeCRUDController() {
        this.probeService = new ProbeCRUDService();
    }
    
    @POST
    @Path("addProbe")
    @Consumes({ "application/probeCreateRequest.v1+json" })
    @Produces({ "application/probeCreatedStatus.v1+json" })
    public HashMap addProbe(final Map probeDetail, @Context final ContainerRequestContext requestContext, @Context final HttpServletRequest servletRequest) {
        final JSONObject probeHandlerObject = new JSONObject();
        final HashMap addedDetails = this.probeService.addProbe(probeDetail);
        if (SyMUtil.isSummaryServer() && addedDetails.get("probeID") != null) {
            servletRequest.setAttribute("isProbeRequest", (Object)true);
            servletRequest.setAttribute("probeHandlerObject", (Object)probeHandlerObject.put("probeId", addedDetails.get("probeID")));
        }
        return addedDetails;
    }
    
    @PUT
    @Path("probes/{probeId}")
    @Consumes({ "application/probeUpdateRequest.v1+json" })
    @Produces({ "application/probeUpdatedStatus.v1+json" })
    public HashMap updateProbe(final Map probeDetail, @PathParam("probeId") final Long probeId, @Context final HttpServletRequest servletRequest) {
        probeDetail.put("probeId", probeId);
        if (SyMUtil.isSummaryServer()) {
            servletRequest.setAttribute("isProbeRequest", (Object)true);
        }
        return this.probeService.updateProbeDetail(probeDetail);
    }
    
    @DELETE
    @Path("probes/{probeId}")
    @Consumes({ "application/probeDeleteRequest.v1+json" })
    @Produces({ "application/probeDeletedStatus.v1+json" })
    public HashMap deleteProbe(@PathParam("probeId") final Long probeId, @Context final HttpServletRequest servletRequest) {
        if (SyMUtil.isSummaryServer()) {
            servletRequest.setAttribute("isProbeRequest", (Object)true);
        }
        return this.probeService.deleteProbe(probeId);
    }
}
