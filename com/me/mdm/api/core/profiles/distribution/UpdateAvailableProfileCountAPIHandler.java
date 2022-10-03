package com.me.mdm.api.core.profiles.distribution;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class UpdateAvailableProfileCountAPIHandler extends ApiRequestHandler
{
    public Logger logger;
    private ProfileFacade profileFacade;
    
    public UpdateAvailableProfileCountAPIHandler() {
        this.logger = Logger.getLogger("MDMAPILogger");
        this.profileFacade = new ProfileFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject data = new JSONObject();
            final int count = this.profileFacade.getUpdateAvailableCount(APIUtil.getCustomerID(apiRequest.toJSONObject()));
            data.put("update_available_count", count);
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)data);
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
    }
}
