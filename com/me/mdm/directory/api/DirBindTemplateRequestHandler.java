package com.me.mdm.directory.api;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.directory.DirectoryFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DirBindTemplateRequestHandler extends ApiRequestHandler
{
    DirectoryFacade directory;
    
    public DirBindTemplateRequestHandler() {
        this.directory = null;
        this.directory = new DirectoryFacade();
    }
    
    @Override
    public Object doGet(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.directory.getAllDirectoryTemplate(request.toJSONObject()));
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.directory.createTemplate(request.toJSONObject()));
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
