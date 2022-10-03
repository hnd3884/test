package com.me.mdm.directory.api;

import com.me.idps.core.api.IdpsAPIException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.idps.core.api.DirectoryAPIFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualDomainSyncRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            DirectoryAPIFacade.getInstance().syncParticularDomain(apiRequest.toJSONObject());
            return response;
        }
        catch (final IdpsAPIException e) {
            this.logger.log(Level.SEVERE, "Error IdpsAPIException Occured in post /directory/sync", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)DirectoryAPIFacade.getInstance().getDomainSyncDetails(apiRequest.toJSONObject()));
            return response;
        }
        catch (final IdpsAPIException e) {
            this.logger.log(Level.SEVERE, "Error IdpsAPIException Occured in post /directory/:id/sync", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
    }
}
