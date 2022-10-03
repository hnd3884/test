package com.me.mdm.api.content;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.doc.DocFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DocumentsToUsersAPIRequestHandler extends ApiRequestHandler
{
    private DocFacade docFacade;
    
    public DocumentsToUsersAPIRequestHandler() {
        this.docFacade = new DocFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.docFacade.getDocToUsersDistributionStatus(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = apiRequest.toJSONObject();
            requestJSON.put("task", (Object)"association");
            this.docFacade.shareOrRemoveDocToUsers(requestJSON);
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
            this.docFacade.shareOrRemoveDocToUsers(requestJSON);
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
