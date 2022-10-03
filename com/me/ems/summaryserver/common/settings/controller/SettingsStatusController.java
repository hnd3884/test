package com.me.ems.summaryserver.common.settings.controller;

import com.me.ems.framework.common.api.annotations.RestrictMatched;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.PathParam;
import com.me.ems.summaryserver.common.settings.service.SettingsStatusService;
import javax.ws.rs.Path;

@Path("summaryserver/settingsStatus")
public class SettingsStatusController
{
    SettingsStatusService settingsStatusService;
    
    public SettingsStatusController() {
        this.settingsStatusService = new SettingsStatusService();
    }
    
    @GET
    @Path("{settingsID}")
    @Produces({ "application/settingsStatusInfo.v1+json" })
    public Map getSSSettingsStatus(@PathParam("settingsID") final Long settingsID, @Context final ContainerRequestContext requestContext) throws APIException {
        return this.settingsStatusService.getSSSettingsStatus(settingsID);
    }
    
    @PUT
    @Path("{settingsID}")
    @Consumes({ "application/settingsStatus.v1+json" })
    @RestrictMatched("Probe")
    public Response saveSSSettingsStatus(final Map<String, Object> settingsStatusMap, @PathParam("settingsID") final Long settingsID, @Context final ContainerRequestContext requestContext, @Context final HttpServletRequest servletRequest) throws APIException {
        final Long status = Long.parseLong(settingsStatusMap.get("status").toString());
        this.settingsStatusService.saveSSSettingsStatus(settingsID, status, servletRequest);
        return Response.status(Response.Status.OK).build();
    }
}
