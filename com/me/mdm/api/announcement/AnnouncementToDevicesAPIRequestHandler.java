package com.me.mdm.api.announcement;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.announcement.facade.AnnouncementDistributionFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class AnnouncementToDevicesAPIRequestHandler extends ApiRequestHandler
{
    private final Logger logger;
    
    public AnnouncementToDevicesAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMAnnouncementLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject distributionJSON = AnnouncementDistributionFacade.getNewInstance().getAnnouncementToDeviceDistributionDetails(requestJSON);
            responseJSON.put("RESPONSE", (Object)distributionJSON);
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementToDevicesAPIRequestHandler", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementToDevicesAPIRequestHandler", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            AnnouncementDistributionFacade.getNewInstance().distributeAnnouncementToDevices(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementToDevicesAPIRequestHandler", e);
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementToDevicesAPIRequestHandler", (Throwable)e2);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementToDevicesAPIRequestHandler", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            AnnouncementDistributionFacade.getNewInstance().disassociateAnnouncementFromDevice(apiRequest.toJSONObject());
            final JSONObject doDeleteResponseJSON = new JSONObject();
            doDeleteResponseJSON.put("status", 204);
            return doDeleteResponseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementToDevicesAPIRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0024", new Object[0]);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementToDevicesAPIRequestHandler", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
