package com.me.mdm.api.core.apps.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppUnpublishAPIRequestHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppUnpublishAPIRequestHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 202);
            final JSONObject message = apiRequest.toJSONObject();
            boolean testflag = true;
            try {
                Long.valueOf(String.valueOf(message.getJSONObject("msg_header").getJSONObject("resource_identifier").get("app_id")));
            }
            catch (final Exception e) {
                testflag = false;
            }
            if (testflag) {
                if (JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "app_id", (Long)null) != 0L) {
                    this.appFacade.unPublish(message);
                }
            }
            else if (apiRequest.getParameterList().containsKey("ids")) {
                final String temp = String.valueOf(apiRequest.getParameterList().get("ids"));
                final String[] ids = temp.split(",");
                final JSONArray appIds = new JSONArray();
                for (final String id : ids) {
                    appIds.put((Object)id);
                }
                if (!message.has("msg_body")) {
                    message.put("msg_body", (Object)new JSONObject());
                }
                message.getJSONObject("msg_body").put("app_ids", (Object)appIds);
                this.appFacade.unPublish(message);
            }
            else if (message.has("msg_body") && message.getJSONObject("msg_body").has("app_ids")) {
                this.appFacade.unPublish(message);
            }
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in POST AppUnpublishAPIRequestHandler", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
