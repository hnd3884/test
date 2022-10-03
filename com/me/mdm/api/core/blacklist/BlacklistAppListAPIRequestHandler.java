package com.me.mdm.api.core.blacklist;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.http.HttpException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.blacklist.BlacklistPolicyFacade;
import com.me.mdm.api.ApiRequestHandler;

public class BlacklistAppListAPIRequestHandler extends ApiRequestHandler
{
    BlacklistPolicyFacade blacklistPolicyFacade;
    
    public BlacklistAppListAPIRequestHandler() {
        this.blacklistPolicyFacade = new BlacklistPolicyFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.blacklistPolicyFacade.getAppGroupsForBlacklisting(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final SyMException ex) {
            throw new HttpException(ex.getErrorCode(), ex.getMessage());
        }
        catch (final HttpException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new HttpException(400, null);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.blacklistPolicyFacade.addAppsGroupsForBlacklistinginRepository(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Issue on adding app in inventory", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
