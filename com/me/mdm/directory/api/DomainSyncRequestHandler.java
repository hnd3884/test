package com.me.mdm.directory.api;

import com.me.idps.core.api.IdpsAPIException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.JSONObject;
import com.me.idps.core.api.DirectoryAPIFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DomainSyncRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            DirectoryAPIFacade.getInstance().syncAllDomain(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            return response;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error IdpsAPIException Occured in post /directory/sync", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
    }
}
