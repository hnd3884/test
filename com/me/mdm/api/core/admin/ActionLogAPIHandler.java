package com.me.mdm.api.core.admin;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.audit.ActionLogViewerUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ActionLogAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new ActionLogViewerUtil().getActionLogEntries(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
