package com.me.mdm.api.core.profiles.config;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.FontFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualFontAPIRequestHandler extends ApiRequestHandler
{
    FontFacade facade;
    
    public IndividualFontAPIRequestHandler() {
        this.facade = new FontFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            apiRequest.urlStartKey = "fonts";
            responseJSON.put("RESPONSE", (Object)this.facade.getFontDetails(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting font ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
