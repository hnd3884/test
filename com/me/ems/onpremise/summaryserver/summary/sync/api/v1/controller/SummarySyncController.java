package com.me.ems.onpremise.summaryserver.summary.sync.api.v1.controller;

import com.me.ems.framework.common.api.annotations.RestrictMatched;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import java.util.Map;
import javax.ws.rs.QueryParam;
import com.me.ems.onpremise.summaryserver.summary.sync.api.v1.service.SummarySyncService;
import javax.ws.rs.Path;

@Path("summaryserver/sync")
public class SummarySyncController
{
    SummarySyncService summarySyncService;
    
    public SummarySyncController() {
        this.summarySyncService = new SummarySyncService();
    }
    
    @GET
    @Path("fetchSyncStatus")
    @Produces({ "application/summaryUpdateSyncStatusResult.v1+json" })
    public Map<String, Object> fetchSyncStatus(@QueryParam("probe_id") final long probeID, @QueryParam("sync_module_name") final String moduleName, @QueryParam("sync_time") final long syncTime) {
        final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
        final long moduleID = syncModuleMetaDAOUtil.getModuleID(moduleName);
        return this.summarySyncService.fetchSummarySyncStatus(probeID, moduleID, syncTime);
    }
    
    @POST
    @Path("updateSyncStatus")
    @Consumes({ "application/summaryUpdateSyncStatus.v1+json" })
    @Produces({ "application/summaryUpdateSyncStatusResult.v1+json" })
    @RestrictMatched("SummaryServer")
    public Response updateSummaryServerResponse(final Map properties) throws Exception {
        this.summarySyncService.updateProbeSyncStatus(properties);
        return Response.ok().build();
    }
}
