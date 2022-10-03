package com.me.mdm.onpremise.api.demo;

import javax.servlet.http.Cookie;
import com.me.devicemanagement.framework.server.security.DMCookieUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MDMPRequestDemoAPIHandler extends ApiRequestHandler
{
    private MDMPRequestDemoFacade mdmpRequestDemoFacade;
    
    public MDMPRequestDemoAPIHandler() {
        this.mdmpRequestDemoFacade = new MDMPRequestDemoFacade();
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = this.getDemoAction(apiRequest);
        final JSONObject result = new JSONObject();
        try {
            result.put("status", 200);
            result.put("RESPONSE", (Object)response);
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Error occurred in MDMPRequestDemoAPIHandler.doGet", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    private JSONObject getDemoAction(final APIRequest apiRequest) throws APIHTTPException {
        final String actionName = this.getDemoActionName(apiRequest);
        final JSONObject requestJson = apiRequest.toJSONObject();
        try {
            final String s = actionName;
            switch (s) {
                case "register": {
                    return this.setCookie(apiRequest, this.mdmpRequestDemoFacade.registerRequestDemo(requestJson));
                }
                case "skip": {
                    return this.setCookie(apiRequest, this.mdmpRequestDemoFacade.skipRequestDemo());
                }
                case "remove": {
                    return this.setCookie(apiRequest, this.mdmpRequestDemoFacade.neverShowRequestDemoPage());
                }
                default: {
                    throw new APIHTTPException("COM0014", new Object[0]);
                }
            }
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Issue on demo action", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private String getDemoActionName(final APIRequest apiRequest) throws APIHTTPException {
        try {
            return apiRequest.pathInfo.split("/")[2];
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error occurred in getDemoActionName()", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject setCookie(final APIRequest apiRequest, final JSONObject responseJSON) {
        if (responseJSON.optJSONObject("cookie") != null) {
            final JSONObject cookieJSON = responseJSON.getJSONObject("cookie");
            final Cookie cookie = DMCookieUtil.generateDMCookies(apiRequest.httpServletRequest, String.valueOf(cookieJSON.get("cookie_name")), cookieJSON.optString("cookie_value"));
            cookie.setMaxAge(cookieJSON.optInt("cookie_max_age"));
            cookie.setPath("/");
            apiRequest.httpServletResponse.addCookie(cookie);
            responseJSON.remove("cookie");
        }
        return responseJSON;
    }
}
