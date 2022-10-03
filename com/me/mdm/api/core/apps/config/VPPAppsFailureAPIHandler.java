package com.me.mdm.api.core.apps.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.apps.businessstore.StoreFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class VPPAppsFailureAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject jsonObject = apiRequest.toJSONObject();
            final Long businessStoreID = APIUtil.getResourceID(jsonObject, "vp_id");
            final JSONObject responseMsg = (JSONObject)new StoreFacade().getStoreAppsFailureDetails(jsonObject, 1, businessStoreID);
            if (responseMsg == null) {
                return JSONUtil.toJSON("status", 204);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)responseMsg);
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in GET /apps/account/vpp/:id/failure", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
