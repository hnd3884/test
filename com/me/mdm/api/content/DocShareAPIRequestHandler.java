package com.me.mdm.api.content;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.doc.DocFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DocShareAPIRequestHandler extends ApiRequestHandler
{
    private DocFacade docFacade;
    
    public DocShareAPIRequestHandler() {
        this.docFacade = new DocFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = apiRequest.toJSONObject();
            requestJSON.put("task", (Object)"association");
            this.docFacade.shareOrRemoveDocToResources(requestJSON);
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = apiRequest.toJSONObject();
            requestJSON.put("task", (Object)"disassociation");
            this.docFacade.shareOrRemoveDocToResources(requestJSON);
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
