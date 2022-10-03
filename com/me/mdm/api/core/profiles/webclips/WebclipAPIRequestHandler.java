package com.me.mdm.api.core.profiles.webclips;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.webclips.WebClipsFacade;
import com.me.mdm.api.ApiRequestHandler;

public class WebclipAPIRequestHandler extends ApiRequestHandler
{
    WebClipsFacade facade;
    
    public WebclipAPIRequestHandler() {
        this.facade = new WebClipsFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.facade.getWebClipPolicies(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting webclips", e);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.facade.addWebClipsPolicy(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while creating webclips", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.facade.modifyBulkWebClips(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while modifying bulk webclips", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.facade.deleteBulkWebClips(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while deleting bulk webclips", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
