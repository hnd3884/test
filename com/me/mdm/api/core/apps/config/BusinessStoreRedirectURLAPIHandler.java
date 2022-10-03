package com.me.mdm.api.core.apps.config;

import com.me.idps.core.api.IdpsAPIException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class BusinessStoreRedirectURLAPIHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public BusinessStoreRedirectURLAPIHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.appFacade.getBstoreRedirectURL(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "IdpsAPIException ", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, null, ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, null, ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
