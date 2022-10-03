package com.me.mdm.api.core.apps.addition;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.businessstore.StoreFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AppPromotionRequestAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        try {
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new StoreFacade().getStorePromoStatus(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in GET /apps/promotion", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
