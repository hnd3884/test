package com.me.ems.onpremise.support.api.v1.controller;

import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.onpremise.support.api.v1.service.SupportTabService;
import javax.ws.rs.Path;

@Path("supportTab")
public class SupportTabController
{
    private SupportTabService supportTabService;
    @Context
    private SecurityContext securityContext;
    
    public SupportTabController() {
        this.supportTabService = new SupportTabService();
    }
    
    @GET
    @Produces({ "application/supportTabResponse.v1+json" })
    public Map getSupportPageDetails() throws APIException {
        return this.supportTabService.getSupportPageDetails();
    }
    
    @GET
    @Path("buildHistory")
    @Produces({ "application/buildHistoryDetails.v1+json" })
    public List getBuildHistoryDetails() throws APIException {
        return this.supportTabService.getBuildHistoryDetails();
    }
}
