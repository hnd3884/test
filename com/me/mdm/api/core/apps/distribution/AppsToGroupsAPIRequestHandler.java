package com.me.mdm.api.core.apps.distribution;

import com.me.mdm.api.core.profiles.ProfilesWrapper;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppsToGroupsAPIRequestHandler extends ApiRequestHandler
{
    AppFacade app;
    
    public AppsToGroupsAPIRequestHandler() {
        this.app = new AppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.app.associateAppsToGroups(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 202);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in POST /apps/:id/groups", ex2);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.app.disassociateAppsToGroups(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
            return JSONUtil.toJSON("status", 202);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in DELETE /apps/:id/groups", ex2);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
}
