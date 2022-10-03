package com.me.mdm.api.core.misc;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ProfileSuffixAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public ProfileSuffixAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            final JSONObject requestObject = apiRequest.toJSONObject();
            final Long customerId = APIUtil.getCustomerID(requestObject);
            int profileType = APIUtil.getIntegerFilter(requestObject, "type");
            if (profileType == -1) {
                profileType = 1;
            }
            final String configurationSuffix = CustomerParamsHandler.getInstance().getParameterValue("profile-suffix-" + profileType, (long)customerId);
            int suffixInt = 1;
            if (configurationSuffix != null) {
                suffixInt = Integer.parseInt(configurationSuffix.trim()) + 1;
            }
            CustomerParamsHandler.getInstance().addOrUpdateParameter("profile-suffix-" + profileType, String.valueOf(suffixInt), (long)customerId);
            final JSONObject result = new JSONObject();
            result.put("profile-suffix", suffixInt);
            response.put("RESPONSE", (Object)result);
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in ProfileSuffixAPIRequestHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
