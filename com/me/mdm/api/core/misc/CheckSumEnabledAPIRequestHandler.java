package com.me.mdm.api.core.misc;

import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class CheckSumEnabledAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject requestJSON = apiRequest.toJSONObject();
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        final JSONObject message = requestJSON.getJSONObject("msg_body");
        final int platformType = APIUtil.getIntegerFilter(requestJSON, "platform");
        final boolean enable = message.getBoolean("enable_checksum");
        try {
            MDMUtil.getInstance().enableOrDisableCheckSumTask(customerId, enable, platformType);
            return JSONUtil.toJSON("status", 202);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Cannot disable/enable checksum task", e);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Cannot disable/enable checksum task", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
