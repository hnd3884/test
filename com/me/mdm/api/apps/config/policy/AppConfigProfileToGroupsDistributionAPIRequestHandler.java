package com.me.mdm.api.apps.config.policy;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppConfigProfileToGroupsDistributionAPIRequestHandler extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public AppConfigProfileToGroupsDistributionAPIRequestHandler() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            apiRequest.urlStartKey = "profiles";
            this.appConfigFacade.associateProfilesToGroups(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while associating app config profile to groups", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            apiRequest.urlStartKey = "profiles";
            this.appConfigFacade.disassociateProfilesToGroups(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while disassociating app config profile to the group", ex2);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
}
