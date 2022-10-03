package com.me.mdm.server.user;

import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import java.util.logging.Level;
import org.json.JSONException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;

public class TwoFactorAuthenticationFacade
{
    public JSONObject getTFADetails(final JSONObject requestJSON) throws JSONException {
        final JSONObject bodyJSON = new JSONObject();
        bodyJSON.put("user_name", (Object)APIUtil.getUserName(requestJSON));
        return MDMApiFactoryProvider.getTwoFactorAuthenticationAPI().getTFADetails(bodyJSON);
    }
    
    public JSONObject addTFA(final JSONObject requestJSON) throws JSONException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        try {
            final JSONObject bodyJSON = requestJSON.getJSONObject("msg_body");
            secLog.putAll(bodyJSON.toMap());
            bodyJSON.put("user_name", (Object)APIUtil.getUserName(requestJSON));
            final JSONObject responseJson = MDMApiFactoryProvider.getTwoFactorAuthenticationAPI().addTFA(bodyJSON);
            remarks = "update-success";
            return responseJson;
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            SecurityOneLineLogger.log("User_Management", "TFA_Update", secLog, Level.INFO);
        }
    }
    
    public boolean removeTFA(final JSONObject requestJSON) throws JSONException {
        String remarks = "TFA deletion failed";
        try {
            final JSONObject bodyJSON = new JSONObject();
            bodyJSON.put("user_name", (Object)APIUtil.getUserName(requestJSON));
            final boolean returnValue = MDMApiFactoryProvider.getTwoFactorAuthenticationAPI().removeTFA(bodyJSON);
            remarks = "TFA deletion success";
            return returnValue;
        }
        finally {
            SecurityOneLineLogger.log("User_Management", "TFA_Update", remarks, Level.INFO);
        }
    }
}
