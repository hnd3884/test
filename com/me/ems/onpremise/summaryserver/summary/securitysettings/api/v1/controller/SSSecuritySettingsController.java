package com.me.ems.onpremise.summaryserver.summary.securitysettings.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.summaryserver.factory.SSServiceFactoryProvider;
import java.util.Map;
import javax.ws.rs.Path;

@Path("security")
public class SSSecuritySettingsController
{
    @GET
    @Path("probeConfigPercentage")
    @Produces({ "application/probeConfigPercentage.v1+json" })
    public Map getSecuritySettingsDetails() throws APIException {
        return SSServiceFactoryProvider.SecuritySettings.getSecuritySettingsService().getProbeSecurityConfiguredPercentageList();
    }
}
