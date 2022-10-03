package com.me.mdm.api.devices.details;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.doc.DocFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualDeviceDocumentAPIRequestHandler extends ApiRequestHandler
{
    private DocFacade docFacade;
    
    public IndividualDeviceDocumentAPIRequestHandler() {
        this.docFacade = new DocFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.docFacade.getDocsForDevice(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
}
