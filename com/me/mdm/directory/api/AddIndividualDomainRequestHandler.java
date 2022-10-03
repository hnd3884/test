package com.me.mdm.directory.api;

import com.me.idps.core.api.IdpsAPIException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.api.DirectoryAPIFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AddIndividualDomainRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPut(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 201);
            final JSONObject j = DirectoryAPIFacade.getInstance().modifyDomainDetails(request.toJSONObject());
            response.put("RESPONSE", (Object)j);
            return response;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error IdpsAPIException Occured in put /directory/:id", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            DirectoryAPIFacade.getInstance().deleteDomain(apiRequest.toJSONObject());
            return response;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error IdpsAPIException Occured in delete /directory/:id", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)DirectoryAPIFacade.getInstance().getDirectory(apiRequest.toJSONObject()));
            return response;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error IdpsAPIException Occured in get /directory/:id", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
    }
}
