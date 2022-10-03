package com.me.mdm.api.core.security;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFileVaultFacade;
import com.me.mdm.api.ApiRequestHandler;

public class FilevaultAPIRequestHandler extends ApiRequestHandler
{
    MDMFileVaultFacade facade;
    
    public FilevaultAPIRequestHandler() {
        this.facade = new MDMFileVaultFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.facade.addFileVault(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e3) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
