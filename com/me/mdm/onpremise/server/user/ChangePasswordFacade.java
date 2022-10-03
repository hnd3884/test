package com.me.mdm.onpremise.server.user;

import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import javax.transaction.SystemException;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.adventnet.sym.server.util.SyMUtil;
import com.adventnet.i18n.I18N;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ChangePasswordFacade
{
    private Logger logger;
    
    public ChangePasswordFacade() {
        this.logger = Logger.getLogger("UserManagementLogger");
    }
    
    public JSONObject updatePassword(final JSONObject requestJSON) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        int preventReuseFor = 0;
        try {
            final JSONObject bodyJSON = requestJSON.getJSONObject("msg_body");
            final JSONObject responseJSON = new JSONObject();
            Long userId = APIUtil.getResourceID(requestJSON, "change_passwor_id");
            final Long loggedInUserId = APIUtil.getUserID(requestJSON);
            final Long loginId = APIUtil.getLoginID(requestJSON);
            if (!userId.equals(loggedInUserId) && !DMUserHandler.isUserInRole(loginId, "Common_Write")) {
                throw new APIHTTPException("COM0028", new Object[0]);
            }
            final String loginName = (String)MDMUtil.getPersistence().get("AaaLogin", new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)userId, 0)).getFirstRow("AaaLogin").get("NAME");
            String userName = "";
            final String oldpass = bodyJSON.optString("old_password", (String)null);
            final String newpass = String.valueOf(bodyJSON.get("new_password"));
            final String againpass = String.valueOf(bodyJSON.get("new_password_again"));
            secLog.put((Object)"CHANGE_PASSWORD_FOR", (Object)loginName);
            secLog.put((Object)"PASSWORD_CHANGED_BY", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName());
            if (!newpass.equalsIgnoreCase(againpass)) {
                throw new APIHTTPException("PS0010", new Object[0]);
            }
            if (newpass.equalsIgnoreCase("admin")) {
                throw new APIHTTPException("USR013", new Object[0]);
            }
            final JSONObject passwordPolicy = DMOnPremiseUserUtil.getPasswordPolicyDetails();
            final String isDemoMode = String.valueOf(ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
            try {
                MDMUtil.getUserTransaction().begin();
                preventReuseFor = passwordPolicy.optInt("PREVENT_REUSE_FOR");
                final boolean isValidPwd = DMOnPremiseUserUtil.isValidPassword(loginName, newpass, passwordPolicy);
                if (!isValidPwd) {
                    throw new APIHTTPException("PS0011", new Object[0]);
                }
                if (oldpass != null) {
                    if (!MDMUtil.isStringEmpty(oldpass) && !MDMUtil.isStringEmpty(newpass) && isValidPwd && isDemoMode.equalsIgnoreCase("false")) {
                        SYMClientUtil.changePassword(loginName, oldpass, newpass);
                        userName = loginName;
                    }
                }
                else if (!MDMUtil.isStringEmpty(newpass) && isValidPwd && isDemoMode.equalsIgnoreCase("false")) {
                    SYMClientUtil.changePassword(loginName, newpass);
                    userId = APIUtil.getUserID(requestJSON);
                    userName = DMUserHandler.getUserNameFromUserID(userId);
                }
                final UserManagementUtil userManagementUtil = new UserManagementUtil();
                if (!newpass.equalsIgnoreCase("admin")) {
                    userManagementUtil.defaultPasswordChanged(DMUserHandler.getUserID(loginId), false);
                }
                responseJSON.put("password_change", (Object)I18N.getMsg("dc.admin.uac.pwd_update_sucess", new Object[0]));
                final String isPasswordChanged = SyMUtil.getSyMParameter("IS_PASSWORD_CHANGED");
                if (isPasswordChanged.equals("false")) {
                    SyMUtil.updateSyMParameter("IS_PASSWORD_CHANGED", "true");
                }
                remarks = "update-success";
                DCEventLogUtil.getInstance().addEvent(713, userName, (HashMap)null, "dc.admin.uac.PWD_CHANGE_SUCCESS", (Object)loginName, true);
                MDMUtil.getUserTransaction().commit();
            }
            catch (final Exception e) {
                final String message = e.getMessage();
                if (message != null) {
                    if (message.contains("New password cannot be same as old password")) {
                        throw new APIHTTPException("PS0004", new Object[0]);
                    }
                    if (message.contains("Old password specified does not match")) {
                        throw new APIHTTPException("PS0005", new Object[0]);
                    }
                    if (message.contains("Length of the new password is smaller")) {
                        throw new APIHTTPException("PS0006", new Object[0]);
                    }
                    if (message.contains("Length of the new password is greater")) {
                        throw new APIHTTPException("PS0007", new Object[0]);
                    }
                    if (message.contains("Password cannot be the same as Login Name")) {
                        throw new APIHTTPException("PS0008", new Object[0]);
                    }
                    if (message.contains("new password matches one of old password")) {
                        throw new APIHTTPException("PS0009", new Object[] { preventReuseFor });
                    }
                }
                try {
                    MDMUtil.getUserTransaction().rollback();
                }
                catch (final SystemException e2) {
                    this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
                }
                throw e;
            }
            return responseJSON;
        }
        catch (final Exception e3) {
            if (e3 instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- updatePassword() >   Error ", e3);
                throw (APIHTTPException)e3;
            }
            this.logger.log(Level.SEVERE, " -- updatePassword() >   Error ", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            SecurityOneLineLogger.log("User_Management", "Password_Updation", secLog, Level.INFO);
        }
    }
}
