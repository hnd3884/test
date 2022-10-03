package com.me.mdm.api.core.osupdate.distribution;

import com.me.mdm.http.HttpException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.updates.osupdates.OSUpdateProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class OsUpdateProfileToGroupsAPIRequestHandler extends ApiRequestHandler
{
    OSUpdateProfileFacade facade;
    
    public OsUpdateProfileToGroupsAPIRequestHandler() {
        this.facade = new OSUpdateProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws HttpException {
        try {
            this.facade.associateOSUpdateProfileToGroups(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
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
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws HttpException {
        try {
            this.facade.disassociateOSUpdateProfileToGroups(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
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
