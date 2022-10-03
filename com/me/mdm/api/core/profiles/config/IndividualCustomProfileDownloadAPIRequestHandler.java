package com.me.mdm.api.core.profiles.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.CustomProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualCustomProfileDownloadAPIRequestHandler extends ApiRequestHandler
{
    CustomProfileFacade facade;
    private Logger logger;
    
    public IndividualCustomProfileDownloadAPIRequestHandler() {
        this.facade = new CustomProfileFacade();
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "customprofiles";
            this.facade.downloadCustomProfile(apiRequest);
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in downloading custom profiles", e2);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
}
