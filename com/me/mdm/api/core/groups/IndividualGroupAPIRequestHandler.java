package com.me.mdm.api.core.groups;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.customgroup.GroupFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualGroupAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    protected GroupFacade group;
    
    public IndividualGroupAPIRequestHandler() {
        this.logger = Logger.getLogger(IndividualGroupAPIRequestHandler.class.getName());
        this.group = new GroupFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.group.getGroup(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "exception occurred in get groups", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.group.modifyGroup(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "exception occurred in get groups", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.group.deleteGroups(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "exception occurred in get groups", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
