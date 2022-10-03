package com.me.mdm.api.core;

import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import java.util.logging.Level;
import org.json.JSONException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;

public class PasswordPolicyFacade
{
    public JSONObject getPasswordPolicyDetails(final JSONObject requestJSON) throws JSONException {
        final JSONObject bodyJSON = new JSONObject();
        bodyJSON.put("user_name", (Object)APIUtil.getUserName(requestJSON));
        return MDMApiFactoryProvider.getPasswordPolicyAPI().getPasswordPolicyDetails(bodyJSON);
    }
    
    public JSONObject addPasswordPolicy(final JSONObject requestJSON) throws JSONException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        try {
            final JSONObject bodyJSON = requestJSON.getJSONObject("msg_body");
            secLog.putAll(bodyJSON.toMap());
            bodyJSON.put("user_name", (Object)APIUtil.getUserName(requestJSON));
            final JSONObject responseJson = MDMApiFactoryProvider.getPasswordPolicyAPI().addPasswordPolicy(bodyJSON);
            remarks = "update-success";
            return responseJson;
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            SecurityOneLineLogger.log("User_Management", "PasswordPolicy_Update", secLog, Level.INFO);
        }
    }
    
    public void removePasswordPolicy(final JSONObject requestJSON) throws JSONException {
        String remarks = "Password Policy deletion failed";
        try {
            final JSONObject bodyJSON = new JSONObject();
            bodyJSON.put("user_name", (Object)APIUtil.getUserName(requestJSON));
            MDMApiFactoryProvider.getPasswordPolicyAPI().removePasswordPolicy(bodyJSON);
            remarks = "Password Policy deletion success";
        }
        finally {
            SecurityOneLineLogger.log("User_Management", "PasswordPolicy_Update", remarks, Level.INFO);
        }
    }
}
