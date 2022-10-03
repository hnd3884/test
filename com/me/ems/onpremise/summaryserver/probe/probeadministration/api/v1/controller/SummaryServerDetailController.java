package com.me.ems.onpremise.summaryserver.probe.probeadministration.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import java.util.HashMap;
import com.me.ems.onpremise.summaryserver.probe.probeadministration.api.v1.service.SummaryServerDetailService;
import javax.ws.rs.Path;

@Path("")
public class SummaryServerDetailController
{
    private SummaryServerDetailService summaryDetailService;
    
    public SummaryServerDetailController() {
        this.summaryDetailService = new SummaryServerDetailService();
    }
    
    @GET
    @Path("summaryServerDetails")
    @Produces({ "application/summaryServerDetails.v1+json" })
    public HashMap getSummaryServerDetails() {
        return this.summaryDetailService.getSummaryServerDetails();
    }
    
    @GET
    @Path("summaryServerUrl")
    @Produces({ "application/summaryServerUrl.v1+json" })
    public HashMap getSummaryServerUrl() {
        return this.summaryDetailService.getSummaryServerUrl();
    }
}
