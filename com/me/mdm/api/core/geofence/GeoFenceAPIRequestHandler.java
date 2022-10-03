package com.me.mdm.api.core.geofence;

import com.me.mdm.http.HttpException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.geofence.GeoFenceFacade;
import com.me.mdm.api.ApiRequestHandler;

public class GeoFenceAPIRequestHandler extends ApiRequestHandler
{
    public GeoFenceFacade geoFenceFacade;
    private Logger logger;
    
    public GeoFenceAPIRequestHandler() {
        this.geoFenceFacade = new GeoFenceFacade();
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            JSONObject fencePolicyJSON = new JSONObject();
            fencePolicyJSON = this.geoFenceFacade.createGeoFence(apiRequest.toJSONObject());
            responseJSON.put("RESPONSE", (Object)fencePolicyJSON);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, " -- doPost()   >   Exception    ", (Throwable)ex);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.geoFenceFacade.getAllGeoFence(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- doGet()    >   Exception   ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            JSONObject responseJSON = new JSONObject();
            responseJSON = this.geoFenceFacade.removeGeoFence(apiRequest.toJSONObject());
            final JSONObject doDeleteResponseJSON = new JSONObject();
            doDeleteResponseJSON.put("status", 204);
            this.logger.log(Level.FINEST, " -- doDelete() >   doDeleteResponseJSON    {0}", doDeleteResponseJSON.toString());
            return doDeleteResponseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- doDelete()    >   Exception   ", (Throwable)e);
            throw new HttpException(400, null);
        }
    }
}
