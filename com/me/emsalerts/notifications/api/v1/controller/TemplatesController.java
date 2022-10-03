package com.me.emsalerts.notifications.api.v1.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import java.util.Map;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.HashMap;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.emsalerts.notifications.api.v1.service.TemplatesService;
import javax.ws.rs.Path;

@Path("alerts/")
public class TemplatesController
{
    TemplatesService templatesService;
    
    public TemplatesController() {
        this.templatesService = new TemplatesService();
    }
    
    @GET
    @Path("event/{eventCode}/template")
    @Produces({ "application/templateDetails.v1+json" })
    public HashMap getTemplateDetails(@Context final ContainerRequestContext containerRequestContext, @PathParam("eventCode") final Long eventCode) {
        final User user = (User)containerRequestContext.getSecurityContext().getUserPrincipal();
        final String customerID = (String)containerRequestContext.getProperty("X-Customer");
        return this.templatesService.getTemplateDetails(Long.valueOf(customerID), user.getUserID(), eventCode);
    }
    
    @GET
    @Path("event/{eventCode}/defaultMediumData/{mediumID}")
    @Produces({ "application/mediumDetails.v1+json" })
    public HashMap getDefaultMediumData(@PathParam("eventCode") final Long eventCode, @PathParam("mediumID") final Long mediumID) {
        return this.templatesService.getDefaultMediumData(eventCode, mediumID);
    }
    
    @POST
    @Path("event/{eventCode}/template")
    @Consumes({ "application/templateDetails.v1+json" })
    @Produces({ "application/templateDetails.v1+json" })
    public Response saveTemplateDetails(final Map templateDetails, @PathParam("eventCode") final Long eventCode, @Context final ContainerRequestContext containerRequestContext) {
        final User user = (User)containerRequestContext.getSecurityContext().getUserPrincipal();
        final String customerID = (String)containerRequestContext.getProperty("X-Customer");
        return this.templatesService.saveTemplateDetails(templateDetails, eventCode, user.getUserID(), Long.valueOf(customerID));
    }
}
