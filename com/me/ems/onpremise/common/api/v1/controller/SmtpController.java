package com.me.ems.onpremise.common.api.v1.controller;

import java.util.HashMap;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import java.util.Map;
import com.me.ems.onpremise.common.factory.CommonOnPremiseServiceFactoryProvider;
import com.me.ems.onpremise.common.factory.SmtpService;
import javax.ws.rs.Path;

@Path("mailServer")
public class SmtpController
{
    SmtpService smtpService;
    
    public SmtpController() {
        this.smtpService = CommonOnPremiseServiceFactoryProvider.getSmtpService();
    }
    
    @GET
    @Path("status")
    @Produces({ "application/mailServerEnabledStatus.v1+json" })
    public Map<String, Boolean> isMailServerEnabled() {
        return this.smtpService.isMailServerEnabled();
    }
    
    @GET
    @Path("settings")
    @Produces({ "application/smtpSettings.v1+json" })
    public Map<String, Object> getSmtpSettings() throws APIException {
        return this.smtpService.getSmtpSettings();
    }
    
    @POST
    @Path("settings")
    @Consumes({ "application/smtpSettings.v1+json" })
    @Produces({ "application/smtpSettingsUpdateStatus.v1+json" })
    public Response updateSmtpSettings(final Map<String, Object> smtpSettings, @Context final SecurityContext securityContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return this.smtpService.updateSmtpSettings(smtpSettings, user.getName(), httpServletRequest);
    }
    
    @DELETE
    @Path("settings")
    public Response deleteSmtpSettings() throws APIException {
        this.smtpService.deleteSmtpSettings();
        return Response.ok().build();
    }
    
    @POST
    @Path("testMail")
    @Consumes({ "application/smtpSettings.v1+json" })
    public Response sendTestMail(final Map<String, Object> smtpToAddress) throws APIException {
        this.smtpService.verifyAndSendTestMail(smtpToAddress);
        return Response.ok().build();
    }
    
    @GET
    @Path("authServerDetails")
    @Produces({ "application/authServerDetails.v1+json" })
    public HashMap getAuthorizationServerDetails(@QueryParam("smtpHost") final String smtpHost) {
        return this.smtpService.getAuthorizationServerDetails(smtpHost);
    }
}
