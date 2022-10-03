package com.me.mdm.api.core.profiles.webclips;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.webclips.WebClipsFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualWebclipAPIRequestHandler extends ApiRequestHandler
{
    WebClipsFacade facade;
    
    public IndividualWebclipAPIRequestHandler() {
        this.facade = new WebClipsFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "webclips";
            responseDetails.put("RESPONSE", (Object)this.facade.getWebClipsPolicy(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while getting webclips", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            apiRequest.urlStartKey = "webclips";
            this.facade.deleteWebClipPolicy(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while deleting webclips", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "webclips";
            responseDetails.put("RESPONSE", (Object)this.facade.modifyWebClipPolicy(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while modifying webclips", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
