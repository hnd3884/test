package com.me.ems.onpremise.metracking.api.v1.controller;

import javax.ws.rs.PUT;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.Map;
import com.me.ems.onpremise.metracking.api.v1.service.METrackingService;
import javax.ws.rs.Path;

@Path("metracking")
public class METrackingController
{
    private final METrackingService service;
    
    public METrackingController() {
        this.service = new METrackingService();
    }
    
    @POST
    @Path("metrackingdetails")
    @Consumes({ "application/metrackingDetails.v1+json" })
    @Produces({ "application/metrackingDetailsResult.v1+json" })
    public Response pushMETrackingDetailsToDB(final Map<String, Object> meTrackingDetails) throws APIException {
        if (!this.validateMETrackingDetailsParams(meTrackingDetails)) {
            throw new APIException("IAM0003", "Invalid request body JSON", new String[0]);
        }
        return this.service.pushMETrackingDetailsToDB(meTrackingDetails);
    }
    
    private boolean validateMETrackingDetailsParams(final Map<String, Object> meTrackingDetails) {
        for (final String key : meTrackingDetails.keySet()) {
            if (!(meTrackingDetails.get(key) instanceof String) && !(meTrackingDetails.get(key) instanceof CharSequence) && !(meTrackingDetails.get(key) instanceof LinkedHashMap)) {
                return false;
            }
        }
        return true;
    }
    
    @PUT
    @Path("details")
    @Consumes({ "application/metrackingDetails.v1+json" })
    public Response addOrUpdateMeTrackerDetails(final Map<String, Object> meTrackingDetails) throws APIException {
        return this.service.addOrUpdateMeTrackerDetails(meTrackingDetails);
    }
}
