package com.me.mdm.api.core.groups;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.customgroup.GroupFacade;
import com.me.mdm.api.ApiRequestHandler;

public class MultiMembersGroupsAPIRequestHandler extends ApiRequestHandler
{
    protected GroupFacade group;
    
    public MultiMembersGroupsAPIRequestHandler() {
        this.group = new GroupFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject request = apiRequest.toJSONObject();
            request.getJSONObject("msg_header").getJSONObject("resource_identifier").remove("group_id");
            request.put("allow_member", true);
            responseJSON.put("status", 202);
            this.group.addMembers(request);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in POST /groups/members", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject request = apiRequest.toJSONObject();
            request.getJSONObject("msg_header").getJSONObject("resource_identifier").remove("group_id");
            responseJSON.put("status", 204);
            this.group.deleteMembers(request);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in POST /groups/members", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
