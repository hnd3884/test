package com.me.mdm.api.core.groups;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.customgroup.GroupFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ADGroupDetailsAPIRequestHandler extends ApiRequestHandler
{
    GroupFacade group;
    
    public ADGroupDetailsAPIRequestHandler() {
        this.group = new GroupFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.group.getADGroupDetails(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "exception occurred in get groups", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
