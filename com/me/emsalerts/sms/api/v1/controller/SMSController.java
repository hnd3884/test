package com.me.emsalerts.sms.api.v1.controller;

import javax.ws.rs.POST;
import java.util.List;
import javax.ws.rs.GET;
import java.util.Hashtable;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.emsalerts.sms.factory.SMSServiceFactorProvider;
import com.me.emsalerts.sms.factory.SMSService;
import javax.ws.rs.Path;

@Path("sms/")
public class SMSController
{
    SMSService smsService;
    
    public SMSController() {
        this.smsService = SMSServiceFactorProvider.getSmsService();
    }
    
    @PUT
    @Path("settings")
    @Produces({ "application/smsSettings.v1+json" })
    @Consumes({ "application/smsSettings.v1+json" })
    public Response updateSMSSettings(@Context final SecurityContext securityContext, final Map smsSettings, @Context final HttpServletRequest httpServletRequest) {
        final User user = (User)securityContext.getUserPrincipal();
        return this.smsService.updateSMSSettings(user, smsSettings, httpServletRequest);
    }
    
    @GET
    @Path("settings")
    @Produces({ "application/smsSettings.v1+json" })
    public Hashtable getSMSSettings() {
        return this.smsService.getSMSConfigurationSettings();
    }
    
    @GET
    @Path("dialingCodes")
    @Produces({ "application/dialingCodes.v1+json" })
    public List getDialingCodes() {
        return this.smsService.getDialingCodes();
    }
    
    @POST
    @Path("settings/enable")
    @Produces({ "application/enableSmsSettings.v1+json" })
    public Response enableSmsSettings(@Context final SecurityContext securityContext) {
        final User user = (User)securityContext.getUserPrincipal();
        this.smsService.enableSMSSettings(user);
        return Response.ok().build();
    }
    
    @POST
    @Path("settings/disable")
    @Produces({ "application/disableSmsSettings.v1+json" })
    public Response disableSmsSettings(@Context final SecurityContext securityContext) {
        final User user = (User)securityContext.getUserPrincipal();
        this.smsService.disableSMSSettings(user);
        return Response.ok().build();
    }
}
