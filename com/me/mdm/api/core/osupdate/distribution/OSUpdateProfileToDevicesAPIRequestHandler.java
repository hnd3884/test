package com.me.mdm.api.core.osupdate.distribution;

import org.json.JSONException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.updates.osupdates.OSUpdateProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class OSUpdateProfileToDevicesAPIRequestHandler extends ApiRequestHandler
{
    OSUpdateProfileFacade facade;
    
    public OSUpdateProfileToDevicesAPIRequestHandler() {
        this.facade = new OSUpdateProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.facade.associateOSUpdateProfileToDevices(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final SyMException e2) {
            throw new APIHTTPException("PAY0009", new Object[0]);
        }
        catch (final JSONException e3) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e4) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.facade.disassociateOSUpdateProfileToDevices(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final APIHTTPException ex2) {
            throw ex2;
        }
        catch (final SyMException ex3) {
            throw new APIHTTPException("PAY0009", new Object[0]);
        }
        catch (final Exception ex4) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
