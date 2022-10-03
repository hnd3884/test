package com.me.mdm.api.core.osupdate.config;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.profiles.ProfileException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.updates.osupdates.OSUpdateProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualOSUpdateProfileAPIRequestHandler extends ApiRequestHandler
{
    OSUpdateProfileFacade facade;
    
    public IndividualOSUpdateProfileAPIRequestHandler() {
        this.facade = new OSUpdateProfileFacade();
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.facade.modifyProfile(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final ProfileException e2) {
            throw new APIHTTPException("COM0022", new Object[0]);
        }
        catch (final APIHTTPException e3) {
            throw e3;
        }
        catch (final Exception e4) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.facade.getOSUpdatePolicy(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.facade.deleteOSUpdateProfile(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final ProfileException e2) {
            throw new APIHTTPException("COM0022", new Object[0]);
        }
        catch (final APIHTTPException e3) {
            throw e3;
        }
        catch (final Exception e4) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
