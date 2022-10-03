package com.me.mdm.onpremise.api.support;

import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.api.support.MDMSupportFacade;
import com.me.mdm.api.ApiRequestHandler;

public class MDMPGetProductInfoAPIHandler extends ApiRequestHandler
{
    MDMSupportFacade mdmSupportFacade;
    Logger logger;
    
    public MDMPGetProductInfoAPIHandler() {
        this.mdmSupportFacade = MDMRestAPIFactoryProvider.getMdmSupportFacade();
        this.logger = Logger.getLogger(MDMPGetProductInfoAPIHandler.class.getCanonicalName());
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject jsonObject = this.mdmSupportFacade.getProductInfo();
            return this.mdmSupportFacade.setSuccessResponse(jsonObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getProductInfoAPI", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
