package com.me.ems.onpremise.summaryserver.probe.sync.api.v1.controller;

import com.me.ems.framework.common.api.annotations.RestrictMatched;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import java.util.Map;
import com.me.ems.onpremise.summaryserver.probe.sync.api.v1.service.ProbeSyncService;
import javax.ws.rs.Path;

@Path("probe/sync")
public class ProbeSyncController
{
    ProbeSyncService probeSyncService;
    
    public ProbeSyncController() {
        this.probeSyncService = new ProbeSyncService();
    }
    
    @POST
    @Path("updateSyncStatus")
    @Consumes({ "application/probeUpdateSyncStatus.v1+json" })
    @Produces({ "application/probeUpdateSyncStatusResult.v1+json" })
    @RestrictMatched("SummaryServer")
    public Map updateSyncStatus(final Map properties) {
        return this.probeSyncService.updateSyncStatusFromSummaryServer(properties);
    }
}
