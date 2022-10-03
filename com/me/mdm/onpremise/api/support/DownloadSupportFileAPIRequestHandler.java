package com.me.mdm.onpremise.api.support;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DownloadSupportFileAPIRequestHandler extends ApiRequestHandler
{
    MDMPSupportFacade mdmSupportFacade;
    
    public DownloadSupportFileAPIRequestHandler() {
        this.mdmSupportFacade = new MDMPSupportFacade();
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            this.mdmSupportFacade.downloadSupportFile(apiRequest);
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "exception in download support file api", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
