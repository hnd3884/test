package com.me.mdm.api.core.users;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class TechEmailUsageValidationAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)MDMRestAPIFactoryProvider.getTechnicianFacade().getNotifyConfiguredForUserEmail(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Issue on checking if user email is configured else where {0}", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
    }
}
