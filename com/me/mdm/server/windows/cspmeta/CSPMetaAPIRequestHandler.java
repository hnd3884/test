package com.me.mdm.server.windows.cspmeta;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class CSPMetaAPIRequestHandler extends ApiRequestHandler
{
    CSPMetaFacade facade;
    
    public CSPMetaAPIRequestHandler() {
        this.facade = new CSPMetaFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.facade.getCSPMetaData(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while procssing csp_meta request : ", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while procssing csp_meta request : ", e2);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
}
