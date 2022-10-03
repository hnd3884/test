package com.me.mdm.api.core.misc;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MdUserConfigParamsApiRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject apiJson = apiRequest.toJSONObject();
        final Long userID = APIUtil.getUserID(apiJson);
        final JSONObject messageBody = apiJson.getJSONObject("msg_body");
        final String paramName = messageBody.getString("param_name");
        final String paramValue = messageBody.getString("param_value");
        try {
            MDMUtil.getInstance().addOrUpdateMdUserConfigParams(userID, paramName, paramValue);
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)messageBody);
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in Post: MdUserConfigParamsApiRequestHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
