package com.me.ems.onpremise.common.summaryserver.summary.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import java.util.Map;
import com.me.ems.onpremise.common.factory.CommonOnPremiseServiceFactoryProvider;
import com.me.ems.onpremise.common.factory.SmtpService;
import javax.ws.rs.Path;

@Path("mailServer")
public class SSSmtpController
{
    SmtpService smtpService;
    
    public SSSmtpController() {
        this.smtpService = CommonOnPremiseServiceFactoryProvider.getSmtpService();
    }
    
    @GET
    @Path("probeConfigStatus")
    @Produces({ "application/probeMailServerConfigStatus.v1+json" })
    public Map isMailServerEnabled() {
        return this.smtpService.getProbeSmtpConfiguredStatusList();
    }
}
