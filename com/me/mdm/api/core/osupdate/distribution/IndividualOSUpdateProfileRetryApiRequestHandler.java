package com.me.mdm.api.core.osupdate.distribution;

import com.me.mdm.http.HttpException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.updates.osupdates.OSUpdateProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualOSUpdateProfileRetryApiRequestHandler extends ApiRequestHandler
{
    OSUpdateProfileFacade facade;
    
    public IndividualOSUpdateProfileRetryApiRequestHandler() {
        this.facade = new OSUpdateProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws HttpException {
        try {
            this.facade.retryOSUpdateProfiles(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final APIHTTPException e2) {
            throw e2;
        }
        catch (final SyMException e3) {
            throw new APIHTTPException("PAY0009", new Object[0]);
        }
        catch (final Exception e4) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
