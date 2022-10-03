package com.me.mdm.api.support;

import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class MDMSupportProcessStatusAPIHandler extends ApiRequestHandler
{
    MDMSupportFacade mdmSupportFacade;
    Logger logger;
    
    public MDMSupportProcessStatusAPIHandler() {
        this.mdmSupportFacade = MDMRestAPIFactoryProvider.getMdmSupportFacade();
        this.logger = Logger.getLogger(MDMSupportProcessStatusAPIHandler.class.getCanonicalName());
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject jsonObject = this.mdmSupportFacade.getUploadProcessStatus();
            return this.mdmSupportFacade.setSuccessResponse(jsonObject);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
