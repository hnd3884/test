package com.me.mdm.api.core.blacklist;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.blacklist.BlacklistPolicyFacade;
import com.me.mdm.api.ApiRequestHandler;

public class BlacklistNetworkAPIRequestHandler extends ApiRequestHandler
{
    BlacklistPolicyFacade blacklistPolicyFacade;
    
    public BlacklistNetworkAPIRequestHandler() {
        this.blacklistPolicyFacade = new BlacklistPolicyFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject apiRequestJSON = apiRequest.toJSONObject();
            this.blacklistPolicyFacade.validateIfUserInRoleToMakeGlobalAppWhitelistBlockListOperation(APIUtil.getLoginID(apiRequestJSON), 1);
            responseDetails.put("RESPONSE", (Object)this.blacklistPolicyFacade.performBlacklistAction(apiRequestJSON, 5, 1));
            return responseDetails;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in network blacklist", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject apiRequestJSON = apiRequest.toJSONObject();
            this.blacklistPolicyFacade.validateIfUserInRoleToMakeGlobalAppWhitelistBlockListOperation(APIUtil.getLoginID(apiRequestJSON), 2);
            responseDetails.put("RESPONSE", (Object)this.blacklistPolicyFacade.performBlacklistAction(apiRequestJSON, 5, 2));
            return responseDetails;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in Delete network blacklist", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
