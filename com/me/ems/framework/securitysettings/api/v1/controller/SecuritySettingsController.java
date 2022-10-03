package com.me.ems.framework.securitysettings.api.v1.controller;

import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;
import javax.ws.rs.POST;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.AllowEntityFilter;
import javax.ws.rs.GET;
import com.me.ems.framework.securitysettings.api.core.SecuritySettingsService;
import com.me.devicemanagement.framework.server.factory.ServiceFactoryProvider;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

@Path("security")
public class SecuritySettingsController
{
    @HeaderParam("X-Customer")
    private String customerIdStr;
    @Context
    private SecurityContext securityContext;
    
    @GET
    @AllowEntityFilter
    @Path("settings")
    @Produces({ "application/securitySettingsInfo.v1+json" })
    @Consumes({ "application/securitySettingsInfo.v1+json" })
    public Map getSecuritySettingsDetails() throws Exception {
        final SecuritySettingsService securitySettingsService = ServiceFactoryProvider.SecuritySettings.getSecuritySettingsService();
        final Long customerId = securitySettingsService.validateCustomer(this.customerIdStr);
        return securitySettingsService.getSecuritySettingsDetails(customerId);
    }
    
    @POST
    @Path("settings")
    @Produces({ "application/securitySettings.v1+json" })
    @Consumes({ "application/securitySettings.v1+json" })
    public Map saveSecuritySettings(final Map securitySettingsDetails, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final User user = (User)this.securityContext.getUserPrincipal();
        final SecuritySettingsService securitySettingsService = ServiceFactoryProvider.SecuritySettings.getSecuritySettingsService();
        final Long customerId = securitySettingsService.validateCustomer(this.customerIdStr);
        return securitySettingsService.saveSecuritySettings(securitySettingsDetails, user, customerId, httpServletRequest);
    }
    
    @GET
    @Path("alert")
    @Produces({ "application/securitySettingsAlert.v1+json" })
    @Consumes({ "application/securitySettingsAlert.v1+json" })
    public Map getSecuritySettingAlertDetails() throws APIException {
        final User user = (User)this.securityContext.getUserPrincipal();
        final SecuritySettingsService securitySettingsService = ServiceFactoryProvider.SecuritySettings.getSecuritySettingsService();
        final Long customerId = securitySettingsService.validateCustomer(this.customerIdStr);
        return securitySettingsService.getSecuritySettingAlertDetails(user, customerId);
    }
    
    @PUT
    @Path("redirection/status")
    @Consumes({ "application/securitySettingsRedirection.v1+json" })
    public Response updateSecurityRedirectionTime(@QueryParam("isRedirected") final Boolean isRedirected) throws APIException {
        ServiceFactoryProvider.SecuritySettings.getSecuritySettingsService().updateSecurityRedirectionTime(isRedirected);
        return Response.ok().build();
    }
    
    @GET
    @Path("settings/securityEnforceDetails")
    @Produces({ "application/securitySecurityEnforceDetails.v1+json" })
    public Map httpsEnforceDetails() throws APIException {
        return ServiceFactoryProvider.SecuritySettings.getSecuritySettingsService().getSecurityEnforceDetails();
    }
}
