package com.me.mdm.api.core.apps.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppToUserGroupsAPIRequestHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppToUserGroupsAPIRequestHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            final JSONObject message = apiRequest.toJSONObject();
            final String serviceName = CustomerInfoUtil.isSAS() ? APIUtil.optStringFilter(message, "service", null) : null;
            if (serviceName != null && !serviceName.equalsIgnoreCase("mdm") && message.has("msg_body")) {
                final JSONObject messageBody = message.getJSONObject("msg_body");
                messageBody.put("invite_user", true);
                message.put("msg_body", (Object)messageBody);
            }
            this.appFacade.associateAppsToUserGroups(message);
            responseDetails.put("status", 202);
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in POST /apps/:id/user_groups", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            this.appFacade.disassociateAppsToUserGroups(apiRequest.toJSONObject());
            responseDetails.put("status", 202);
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in DELETE /apps/:id/user_groups", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
