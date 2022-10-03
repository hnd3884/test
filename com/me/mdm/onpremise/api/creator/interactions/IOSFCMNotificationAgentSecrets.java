package com.me.mdm.onpremise.api.creator.interactions;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IOSFCMNotificationAgentSecrets extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "IOSFCMCreatorTask");
            taskInfoMap.put("poolName", "mdmPool");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.onpremise.notification.IOSFCMNotificationCreatorHandler", taskInfoMap, new Properties());
            final JSONObject responseJSON = new JSONObject();
            final JSONObject responseDataJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)responseDataJSON);
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in getting IOS FCM Agent Secrets from Creator...", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
