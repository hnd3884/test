package com.me.mdm.api.support;

import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class MDMSendSupportActionAPIHandler extends ApiRequestHandler
{
    private MDMSupportFacade mdmSupportFacade;
    Logger logger;
    
    public MDMSendSupportActionAPIHandler() {
        this.mdmSupportFacade = MDMRestAPIFactoryProvider.getMdmSupportFacade();
        this.logger = Logger.getLogger(MDMSendSupportActionAPIHandler.class.getCanonicalName());
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject jsonObject = this.mdmSupportFacade.startUploadAction(apiRequest.toJSONObject());
            return this.mdmSupportFacade.setSuccessResponse(jsonObject);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in send_support_file API", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
