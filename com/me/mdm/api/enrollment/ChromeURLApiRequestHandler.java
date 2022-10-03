package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ChromeURLApiRequestHandler extends ApiRequestHandler
{
    AdminEnrollmentFacade enrollmentFacade;
    
    public ChromeURLApiRequestHandler() {
        this.enrollmentFacade = new AdminEnrollmentFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.getChromeRedirectURL(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
