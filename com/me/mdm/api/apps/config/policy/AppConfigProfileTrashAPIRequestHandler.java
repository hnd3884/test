package com.me.mdm.api.apps.config.policy;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppConfigProfileTrashAPIRequestHandler extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public AppConfigProfileTrashAPIRequestHandler() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public JSONObject doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject message = apiRequest.toJSONObject();
            message.getJSONObject("msg_header").getJSONObject("resource_identifier").remove("profile_id");
            message.put("move_to_trash", true);
            this.appConfigFacade.deleteOrTrashProfile(message);
            return JSONUtil.toJSON("status", 204);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception moving app configuration profile to trash", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
