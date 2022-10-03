package com.me.mdm.api.core.groups;

import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.customgroup.UserGroupFacade;
import com.me.mdm.api.ApiRequestHandler;

public class UserGroupAppsApiRequestHandler extends ApiRequestHandler
{
    UserGroupFacade userGroupFacade;
    
    public UserGroupAppsApiRequestHandler() {
        this.userGroupFacade = MDMRestAPIFactoryProvider.getUserGroupFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject result = new JSONObject();
            result.put("status", 200);
            result.put("RESPONSE", (Object)this.userGroupFacade.getAppsAssociatedToGroup(apiRequest.toJSONObject()));
            return result;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final AppFacade appFacade = MDMRestAPIFactoryProvider.getAppFacade();
        try {
            final JSONObject result = new JSONObject();
            final JSONObject message = apiRequest.toJSONObject();
            final String serviceName = CustomerInfoUtil.isSAS() ? APIUtil.optStringFilter(message, "service", null) : null;
            if (serviceName != null && !serviceName.equalsIgnoreCase("mdm") && message.has("msg_body")) {
                final JSONObject messageBody = message.getJSONObject("msg_body");
                messageBody.put("invite_user", true);
                message.put("msg_body", (Object)messageBody);
            }
            appFacade.associateAppsToUserGroups(message);
            result.put("status", 202);
            return result;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        final AppFacade appFacade = MDMRestAPIFactoryProvider.getAppFacade();
        try {
            final JSONObject result = new JSONObject();
            appFacade.disassociateAppsToUserGroups(apiRequest.toJSONObject());
            result.put("status", 202);
            return result;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
