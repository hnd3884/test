package com.me.ems.onpremise.metracking.api.v1.service;

import java.util.List;
import java.util.Iterator;
import org.json.JSONObject;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.logging.Logger;

public class METrackingService
{
    private static Logger logger;
    
    public Response pushMETrackingDetailsToDB(final Map<String, Object> meTrackingDetails) throws APIException {
        try {
            for (final String meTrackingParam : meTrackingDetails.keySet()) {
                METrackerUtil.addOrUpdateMETrackParams(meTrackingParam, meTrackingDetails.get(meTrackingParam).toString());
            }
        }
        catch (final Exception e) {
            METrackingService.logger.log(Level.SEVERE, "An exception occurred in METrack API service.", e);
            throw new APIException("GENERIC0005");
        }
        return Response.status(Response.Status.CREATED).entity((Object)new JSONObject().put("success", true).toString()).build();
    }
    
    public void addOrIncrementMETrackingDetailsToDB(final List<String> meTrackingParams) throws APIException {
        try {
            for (final String meTrackingParam : meTrackingParams) {
                METrackerUtil.incrementMETrackParams(meTrackingParam);
            }
        }
        catch (final Exception e) {
            METrackingService.logger.log(Level.SEVERE, "An exception occurred in Increment METrack API service.", e);
            throw new APIException("GENERIC0005");
        }
    }
    
    public Response addOrUpdateMeTrackerDetails(final Map<String, Object> meTrackingDetails) throws APIException {
        try {
            final List incrementParams = meTrackingDetails.get("incrementParams");
            final Map params = meTrackingDetails.get("params");
            if (incrementParams != null) {
                this.addOrIncrementMETrackingDetailsToDB(incrementParams);
            }
            if (params != null) {
                this.pushMETrackingDetailsToDB(params);
            }
        }
        catch (final Exception e) {
            METrackingService.logger.log(Level.SEVERE, "An exception occurred in Increment METrack API service.", e);
            throw new APIException("GENERIC0005");
        }
        return Response.status(Response.Status.CREATED).build();
    }
    
    static {
        METrackingService.logger = Logger.getLogger(METrackingService.class.getName());
    }
}
