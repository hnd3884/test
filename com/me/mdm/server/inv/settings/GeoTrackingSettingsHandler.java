package com.me.mdm.server.inv.settings;

import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import org.json.JSONException;
import com.me.mdm.server.role.RBDAUtil;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.google.gson.Gson;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class GeoTrackingSettingsHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public GeoTrackingSettingsHandler() {
        this.logger = Logger.getLogger("InventoryLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final GeoTrackingSettings settings = LocationSettingsDataHandler.getInstance().getGeoTrackingSettings(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", (Object)JSONUtil.getInstance().convertLongToString(new JSONObject(new Gson().toJson((Object)settings))));
            response.put("status", 200);
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception occured in GeoTrackingSettingsHander.doGet", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        final org.json.simple.JSONObject logJson = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        try {
            if (!MDMUtil.getInstance().isGeoTrackingEnabled()) {
                throw new APIHTTPException("GEO004", new Object[0]);
            }
            final long loginId = APIUtil.getLoginID(apiRequest.toJSONObject());
            if (!RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginId, false)) {
                throw new APIHTTPException("GEO005", new Object[0]);
            }
            final String requestBody = new String(apiRequest.requestBody);
            logJson.put((Object)"DATA", (Object)requestBody);
            final GeoTrackingSettings geoTrackingSettings = (GeoTrackingSettings)new Gson().fromJson(requestBody, (Class)GeoTrackingSettings.class);
            LocationSettingsDataHandler.getInstance().setLocationSettings(geoTrackingSettings, apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            remarks = "update-success";
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "error occured in PUT /inventory_settings/app_scan", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            logJson.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "UPDATE_GEO_TRACKING", logJson);
        }
    }
}
