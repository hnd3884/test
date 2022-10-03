package com.me.mdm.api.announcement;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.announcement.facade.AnnouncementFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AnnouncementNbarIconHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject response = AnnouncementFacade.getNewInstance().getAnnoucementNBarIcon(apiRequest.toJSONObject());
            responseDetails.put("RESPONSE", (Object)response);
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in AnnouncementAPIHandler", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
