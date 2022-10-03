package com.me.mdm.api.tree;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.tree.TreeFacade;
import com.me.mdm.api.ApiRequestHandler;

public class GroupResourceDistAPIRequestHandler extends ApiRequestHandler
{
    TreeFacade treeFacade;
    
    public GroupResourceDistAPIRequestHandler() {
        this.treeFacade = new TreeFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject result = new JSONObject();
            result.put("status", 200);
            final JSONObject requestJson = apiRequest.toJSONObject();
            String[] paths = null;
            if (apiRequest.pathInfo != null) {
                paths = apiRequest.pathInfo.split("/");
            }
            final int size = paths.length;
            final JSONObject idJson = new JSONObject();
            idJson.put("tree_id", (Object)paths[size - 1]);
            requestJson.getJSONObject("msg_header").put("resource_identifier", (Object)idJson);
            requestJson.put("is_group", true);
            result.put("RESPONSE", (Object)this.treeFacade.getGroupResourceFilteredValue(requestJson));
            return result;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "jsonexception", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
