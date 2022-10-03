package com.me.mdm.api.core.blacklist;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.http.HttpException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.blacklist.BlacklistPolicyFacade;
import com.me.mdm.api.ApiRequestHandler;

public class BlacklistDashboardAPIRequestHandler extends ApiRequestHandler
{
    BlacklistPolicyFacade blacklistPolicyFacade;
    
    public BlacklistDashboardAPIRequestHandler() {
        this.blacklistPolicyFacade = new BlacklistPolicyFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.blacklistPolicyFacade.getBlacklistDashboardData(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final HttpException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new HttpException(400, null);
        }
    }
}
