package com.me.mdm.directory.api;

import com.me.idps.core.api.IdpsAPIException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.JSONObject;
import com.me.mdm.server.conditionalaccess.AzureWinCEA;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DirectoryConditionalAccess extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            AzureWinCEA.getInstance().configureCA(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)new JSONObject());
            return response;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error IdpsAPIException ", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
        catch (final APIHTTPException e2) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error APIHTTPException Occured in get /directory/:id/conditionalAccess", e2);
            throw e2;
        }
        catch (final Exception e3) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error Exception Occured in get /directory/:id/conditionalAccess", e3);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)AzureWinCEA.getInstance().getCASummary(apiRequest.toJSONObject()));
            return response;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error IdpsAPIException ", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
        catch (final APIHTTPException e2) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error APIHTTPException Occured in get /directory/:id/conditionalAccess", e2);
            throw e2;
        }
        catch (final Exception e3) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error Exception Occured in get /directory/:id/conditionalAccess", e3);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
}
