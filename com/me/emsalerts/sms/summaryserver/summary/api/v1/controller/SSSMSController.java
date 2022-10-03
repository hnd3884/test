package com.me.emsalerts.sms.summaryserver.summary.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import java.util.Map;
import com.me.emsalerts.sms.factory.SMSServiceFactorProvider;
import com.me.emsalerts.sms.factory.SMSService;
import javax.ws.rs.Path;

@Path("sms/")
public class SSSMSController
{
    SMSService smsService;
    
    public SSSMSController() {
        this.smsService = SMSServiceFactorProvider.getSmsService();
    }
    
    @GET
    @Path("probeConfigStatus")
    @Produces({ "application/probeSMSConfigStatus.v1+json" })
    public Map getSMSSettings() {
        return this.smsService.getProbeSMSConfiguredStatusList();
    }
}
