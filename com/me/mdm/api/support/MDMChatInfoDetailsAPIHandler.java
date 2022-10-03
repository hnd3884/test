package com.me.mdm.api.support;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.api.ApiRequestHandler;

public class MDMChatInfoDetailsAPIHandler extends ApiRequestHandler
{
    MDMSupportFacade mdmSupportFacade;
    
    public MDMChatInfoDetailsAPIHandler() {
        this.mdmSupportFacade = MDMRestAPIFactoryProvider.getMdmSupportFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("RESPONSE", (Object)this.mdmSupportFacade.getChatDetails());
            responseJSON.put("status", 200);
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
    }
}
