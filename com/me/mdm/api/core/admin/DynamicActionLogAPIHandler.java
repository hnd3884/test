package com.me.mdm.api.core.admin;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.audit.ActionLogViewerUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DynamicActionLogAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 202);
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget")) {
                responseDetails.put("RESPONSE", new ActionLogViewerUtil().addDynamicEventLog(apiRequest.toJSONObject()));
                return responseDetails;
            }
            throw new APIHTTPException("COM0015", new Object[] { "Need to configure feature param" });
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while adding dynamic action log", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
