package com.me.mdm.onpremise.server.api.sdp;

import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.onpremise.server.integration.sdp.SDPTechniciansFacade;
import com.me.mdm.server.user.TechniciansFacade;
import com.me.mdm.api.ApiRequestHandler;

public class TechniciansAPIRequestHandler extends ApiRequestHandler
{
    TechniciansFacade techniciansFacade;
    
    public TechniciansAPIRequestHandler() {
        this.techniciansFacade = (TechniciansFacade)new SDPTechniciansFacade();
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            String userName = apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("resource_identifier").optString("sdp_technicia_id");
            JSONObject responseJSON = new JSONObject();
            if (userName == null || userName.length() == 0) {
                userName = String.valueOf(apiRequest.toJSONObject().getJSONObject("msg_body").get("user_name"));
                final Long loginId = DMUserHandler.getLoginIdForUser(userName);
                if (loginId == null || loginId == 0L) {
                    this.techniciansFacade.addTechnicians(apiRequest.toJSONObject());
                }
                responseJSON = new JSONObject();
                responseJSON.put("status", 202);
            }
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
    
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject req = apiRequest.toJSONObject();
            String userName = apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("resource_identifier").optString("sdp_technicia_id");
            if (userName == null || userName.length() == 0) {
                userName = String.valueOf(apiRequest.toJSONObject().getJSONObject("msg_body").get("user_name"));
            }
            else {
                req.put("msg_body", (Object)new JSONObject());
            }
            final Long loginId = DMUserHandler.getLoginIdForUser(userName);
            req.getJSONObject("msg_body").put("login_id", (Object)loginId);
            req.getJSONObject("msg_body").put("user_id", (Object)DMUserHandler.getUserID(loginId));
            this.techniciansFacade.removeTechnicians(req);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
    
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject req = apiRequest.toJSONObject();
            String userName = apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("resource_identifier").optString("sdp_technicia_id");
            String domainName = null;
            if (userName == null || userName.length() == 0) {
                userName = String.valueOf(apiRequest.toJSONObject().getJSONObject("msg_body").get("user_name"));
                domainName = String.valueOf(apiRequest.toJSONObject().getJSONObject("msg_body").get("ad_domain"));
            }
            else {
                req.put("msg_body", (Object)new JSONObject());
            }
            final Long loginId = DMUserHandler.getLoginIdForUser(userName);
            if (loginId == null || loginId == 0L) {
                this.techniciansFacade.addTechnicians(req);
            }
            final JSONObject responseJSON = new JSONObject();
            final Long loginIdForUpdate = DMUserHandler.getLoginIdForUser(userName, domainName);
            if (loginIdForUpdate == null || loginIdForUpdate == 0L) {
                req.getJSONObject("msg_body").put("login_id", (Object)loginIdForUpdate);
                req.getJSONObject("msg_body").put("user_id", (Object)DMUserHandler.getUserID(loginIdForUpdate));
                this.techniciansFacade.updateTechnicians(req);
                responseJSON.put("status", 202);
            }
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
}
