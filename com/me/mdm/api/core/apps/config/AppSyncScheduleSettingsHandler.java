package com.me.mdm.api.core.apps.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppSyncScheduleSettingsHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    private static Logger logger;
    
    public AppSyncScheduleSettingsHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        try {
            this.appFacade.updateAppsSyncScheduler(apiRequest.toJSONObject());
            responseJSON.put("status", 202);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            AppSyncScheduleSettingsHandler.logger.log(Level.SEVERE, "Issue on updating apps sync scheduler", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
        return responseJSON;
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        try {
            responseJSON.put("RESPONSE", (Object)this.appFacade.getAppsSyncScheduler(apiRequest.toJSONObject()));
            responseJSON.put("status", 200);
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            AppSyncScheduleSettingsHandler.logger.log(Level.SEVERE, "Issue on fetching scheduler info", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
    }
    
    static {
        AppSyncScheduleSettingsHandler.logger = Logger.getLogger("MDMAPILogger");
    }
}
