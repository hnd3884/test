package com.me.mdm.directory.api;

import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.directory.DirectoryFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualDirBindTemplateRequestHandler extends ApiRequestHandler
{
    DirectoryFacade directory;
    
    public IndividualDirBindTemplateRequestHandler() {
        this.directory = null;
        this.directory = new DirectoryFacade();
    }
    
    @Override
    public Object doGet(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            request.urlStartKey = "bindpolicytemplates";
            response.put("RESPONSE", (Object)this.directory.getDirectoryTemplate(request.toJSONObject()));
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            request.urlStartKey = "bindpolicytemplates";
            response.put("RESPONSE", (Object)this.directory.modifyTemplate(request.toJSONObject()));
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final DataAccessException e2) {
            throw new APIHTTPException(206, null, new Object[0]);
        }
        catch (final Exception e3) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest request) throws APIHTTPException {
        try {
            request.urlStartKey = "bindpolicytemplates";
            this.directory.deleteTemplate(request.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)new JSONObject());
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
}
