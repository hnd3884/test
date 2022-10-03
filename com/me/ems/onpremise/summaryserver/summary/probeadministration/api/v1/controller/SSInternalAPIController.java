package com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.Map;
import javax.ws.rs.QueryParam;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service.SSInternalAPIService;
import javax.ws.rs.Path;

@Path("summaryserver")
public class SSInternalAPIController
{
    SSInternalAPIService ssInternalAPIService;
    
    public SSInternalAPIController() {
        this.ssInternalAPIService = SSInternalAPIService.getInstance();
    }
    
    @POST
    @Path("syncdata")
    @Produces({ "application/syncStatus.v1+json" })
    @Consumes({ "application/syncRequest.v1+json" })
    public Map syncEventsToProbe(@QueryParam("probeId") final Long probeID, final Map requestData) throws APIException {
        if (probeID == null) {
            throw new APIException(Response.Status.BAD_REQUEST, "IAM0003", "ems.ss.probe_id_missing");
        }
        if (requestData.get("eventCodeList") == null && requestData.get("tableName") == null) {
            throw new APIException(Response.Status.BAD_REQUEST, "IAM0003", "ems.ss.param_not_passed");
        }
        return this.ssInternalAPIService.processDataToProbe(probeID, requestData);
    }
}
