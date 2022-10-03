package com.me.mdm.api.announcement;

import com.me.uem.announcement.AnnouncementException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.announcement.facade.AnnouncementFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAnnouncementAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)AnnouncementFacade.getNewInstance().getAnnouncementInfo(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementAPIHandler", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementAPIHandler", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 204);
            responseDetails.put("RESPONSE", (Object)AnnouncementFacade.getNewInstance().addAnnouncement(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementAPIHandler", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final AnnouncementException e2) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementAPIHandler", e2);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final APIHTTPException e3) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementAPIHandler", e3);
            throw e3;
        }
        catch (final Exception e4) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementAPIHandler", e4);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            AnnouncementFacade.getNewInstance().deleteAnnouncement(apiRequest.toJSONObject());
            final JSONObject doDeleteResponseJSON = new JSONObject();
            doDeleteResponseJSON.put("status", 204);
            return doDeleteResponseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementAPIHandler", (Throwable)e2);
            throw new APIHTTPException("COM0024", new Object[0]);
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementAPIHandler", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
