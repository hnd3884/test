package com.me.mdm.api.settings;

import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ZOHOAPIKeyAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject response = new JSONObject();
            final String key = MDMUtil.getSyMParameter("ZOHO_MAPS_API_KEY");
            if (key != null) {
                response.put("zoho_maps_api_key", (Object)key);
                responseJSON.put("status", 200);
                responseJSON.put("RESPONSE", (Object)response);
            }
            else {
                responseJSON.put("status", 204);
            }
            return responseJSON;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
