package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.AllowEntityFilter;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import com.me.ems.framework.common.factory.CommonServiceFactoryProvider;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.HeaderParam;
import com.me.ems.framework.common.factory.ExportSettingsService;
import javax.ws.rs.Path;

@Path("exportsettings")
public class ExportSettingsController
{
    ExportSettingsService exportSettingsService;
    @HeaderParam("X-Customer")
    private String customerIdStr;
    @Context
    SecurityContext securityContext;
    
    public ExportSettingsController() {
        this.exportSettingsService = CommonServiceFactoryProvider.getExportSettingsService();
    }
    
    @GET
    @AllowEntityFilter
    @Produces({ "application/allExportSettings.v1+json" })
    public Map getExportSettings() throws APIException {
        return this.exportSettingsService.getExportSettings((User)this.securityContext.getUserPrincipal());
    }
    
    @GET
    @AllowEntityFilter
    @Path("currentexportredacttype")
    @Produces({ "application/currentExportRedactDetails.v1+json" })
    public Map currentExportRedactType() throws APIException {
        return this.exportSettingsService.currentExportRedactType((User)this.securityContext.getUserPrincipal());
    }
    
    @POST
    @Path("userchosenredactlevel")
    @Consumes({ "application/redactLevel.v1+json" })
    @Produces({ "application/userChosenRedactLevelDetails.v1+json" })
    public Map userChosenRedactLevel(final Map redactLevelDetails, @Context final HttpServletRequest httpServletRequest) throws APIException {
        this.exportSettingsService.userChosenRedactLevel((User)this.securityContext.getUserPrincipal(), redactLevelDetails, httpServletRequest);
        return redactLevelDetails;
    }
    
    @POST
    @Consumes({ "application/saveExportSettingsDetails.v1+json" })
    public Response saveExportSettings(final Map exportSettingsDetails, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final User User = (User)this.securityContext.getUserPrincipal();
        this.exportSettingsService.validateExportSettings(exportSettingsDetails, User);
        if (this.exportSettingsService.saveExportSettings(exportSettingsDetails, this.exportSettingsService.validateCustomer(this.customerIdStr), httpServletRequest)) {
            return Response.ok().build();
        }
        return Response.notModified().build();
    }
}
