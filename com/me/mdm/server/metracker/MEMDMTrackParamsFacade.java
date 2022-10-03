package com.me.mdm.server.metracker;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MEMDMTrackParamsFacade
{
    public Logger logger;
    
    public MEMDMTrackParamsFacade() {
        this.logger = Logger.getLogger("METrackLog");
    }
    
    public void incrementTrackParams(final JSONObject requestJSON) throws APIHTTPException {
        try {
            Long customerId = null;
            try {
                customerId = APIUtil.getCustomerID(requestJSON);
            }
            catch (final APIHTTPException apiException) {
                this.logger.log(Level.WARNING, "CustomerId not found in Header");
            }
            if (!requestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject bodyJSON = requestJSON.getJSONObject("msg_body");
            if (bodyJSON.length() == 0) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final String module = String.valueOf(bodyJSON.get("MODULE".toLowerCase()));
            final String paramName = String.valueOf(bodyJSON.get("PARAM_NAME".toLowerCase()));
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, module.toUpperCase(), paramName.toUpperCase());
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- doPost()  >   Error   ", (Throwable)e);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
}
