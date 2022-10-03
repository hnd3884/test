package com.me.mdm.api.core.osupdate.config;

import com.me.mdm.server.profiles.ProfileException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.updates.osupdates.OSUpdateProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class OSUpdateProfileTrashAPIRequestHandler extends ApiRequestHandler
{
    OSUpdateProfileFacade facade;
    
    public OSUpdateProfileTrashAPIRequestHandler() {
        this.facade = new OSUpdateProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.facade.trashOSUpdateProfile(apiRequest.toJSONObject());
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
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.facade.restoreOSUpdatePolicy(apiRequest.toJSONObject());
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
