package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.framework.common.factory.CommonServiceFactoryProvider;
import com.me.ems.framework.common.factory.ActionLogService;
import javax.ws.rs.Path;

@Path("admin/actionLog")
public class ActionLogController
{
    private ActionLogService actionLogService;
    
    public ActionLogController() {
        this.actionLogService = CommonServiceFactoryProvider.getActionLogService();
    }
    
    @GET
    @Path("settings")
    @Produces({ "application/actionLogSettings.v1+json" })
    public Map<String, Object> getEventSettings(@Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return this.actionLogService.getSettings(user);
    }
    
    @GET
    @Path("retentionPeriod")
    @Produces({ "application/actionLogRetention.v1+json" })
    public Map<String, String> getRetentionPeriod() throws APIException {
        return this.actionLogService.getRetentionPeriod();
    }
    
    @POST
    @Path("retentionPeriod")
    @Consumes({ "application/actionLogRetention.v1+json" })
    public Response updateRetentionPeriod(final Map<String, String> noOfDaysMap, @Context final SecurityContext securityContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        this.actionLogService.updateRetentionPeriod(noOfDaysMap, user, httpServletRequest);
        return Response.ok().build();
    }
}
