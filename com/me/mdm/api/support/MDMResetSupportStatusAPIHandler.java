package com.me.mdm.api.support;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.server.support.SupportFileCreation;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class MDMResetSupportStatusAPIHandler extends ApiRequestHandler
{
    private MDMSupportFacade mdmSupportFacade;
    Logger logger;
    
    public MDMResetSupportStatusAPIHandler() {
        this.mdmSupportFacade = MDMRestAPIFactoryProvider.getMdmSupportFacade();
        this.logger = Logger.getLogger(MDMResetSupportStatusAPIHandler.class.getCanonicalName());
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            SupportFileCreation.getInstance().resetSFCProcess();
            final JSONObject responseJsonObject = new JSONObject();
            responseJsonObject.put("RESPONSE", (Object)new JSONObject());
            responseJsonObject.put("status", 202);
            return responseJsonObject;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
