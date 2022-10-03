package com.me.ems.onpremise.uac.core;

import com.adventnet.authentication.PasswordException;
import java.util.logging.Level;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import java.util.Map;
import com.me.ems.onpremise.uac.api.v1.model.PasswordPolicy;
import org.json.JSONObject;
import java.util.logging.Logger;

public class PasswordPolicyUtil
{
    private static Logger logger;
    
    public static PasswordPolicy constructPasswordPolicy(final JSONObject passwordPolicyDetails) {
        final PasswordPolicy passwordPolicy = new PasswordPolicy();
        passwordPolicy.setComplexPasswordEnabled(passwordPolicyDetails.getBoolean("IS_COMPLEX_PASSWORD"));
        passwordPolicy.setLoginRestrictionEnabled(passwordPolicyDetails.getBoolean("ENABLE_LOGIN_RESTRICTION"));
        if (passwordPolicy.getLoginRestrictionEnabled()) {
            passwordPolicy.setBadAttemptCount(passwordPolicyDetails.getInt("BAD_ATTEMPT"));
            passwordPolicy.setLockPeriod(passwordPolicyDetails.getInt("LOCK_PERIOD"));
        }
        passwordPolicy.setPreventReuseFor(passwordPolicyDetails.getInt("PREVENT_REUSE_FOR"));
        passwordPolicy.setMinimumLength(passwordPolicyDetails.getInt("MIN_LENGTH"));
        return passwordPolicy;
    }
    
    public static boolean savePasswordPolicyDetails(final Map passwordPolicy) {
        final JSONObject passwordPolicyDetails = new JSONObject();
        passwordPolicyDetails.put("BAD_ATTEMPT", (Object)String.valueOf((passwordPolicy.get("badAttemptCount") == null) ? "0" : passwordPolicy.get("badAttemptCount")));
        passwordPolicyDetails.put("IS_COMPLEX_PASSWORD", passwordPolicy.get("complexPasswordEnabled"));
        passwordPolicyDetails.put("LOCK_PERIOD", (Object)String.valueOf((passwordPolicy.get("lockPeriod") == null) ? "0" : passwordPolicy.get("lockPeriod")));
        passwordPolicyDetails.put("ENABLE_LOGIN_RESTRICTION", passwordPolicy.get("loginRestrictionEnabled"));
        passwordPolicyDetails.put("PREVENT_REUSE_FOR", (Object)String.valueOf(passwordPolicy.get("preventReuseFor")));
        passwordPolicyDetails.put("MIN_LENGTH", (Object)String.valueOf(passwordPolicy.get("minimumLength")));
        return DMOnPremiseUserUtil.addOrUpdatePasswordPolicy(passwordPolicyDetails);
    }
    
    public static void validatePassword(final String loginName, final String password) throws APIException {
        try {
            if (password.equalsIgnoreCase("admin")) {
                final String message = I18N.getMsg("desktopcentral.admin.new_password_default", new Object[0]);
                throw new APIException(Response.Status.PRECONDITION_FAILED, "PASS0001", message);
            }
            final JSONObject passwordPolicy = DMOnPremiseUserUtil.getPasswordPolicyDetails();
            final Integer minLength = (Integer)(passwordPolicy.has("MIN_LENGTH") ? passwordPolicy.get("MIN_LENGTH") : 5);
            final Boolean isComplexPwd = passwordPolicy.has("IS_COMPLEX_PASSWORD") && (boolean)passwordPolicy.get("IS_COMPLEX_PASSWORD");
            final Integer reuseFor = (Integer)(passwordPolicy.has("PREVENT_REUSE_FOR") ? passwordPolicy.get("PREVENT_REUSE_FOR") : 0);
            try {
                final boolean isPwdValid = DMOnPremiseUserUtil.isValidPassword(loginName, password, passwordPolicy);
                if (!isPwdValid) {
                    String message2 = I18N.getMsg("dc.uac.PASSWORDPOLICY.PASSWORD_VALIDATION", new Object[] { minLength });
                    message2 = (isComplexPwd ? (message2 + ' ' + I18N.getMsg("dc.uac.PASSWORDPOLICY.COMPLEXITY_VALIDATION", new Object[0])) : message2);
                    throw new APIException(Response.Status.PRECONDITION_FAILED, "PASS0001", message2);
                }
            }
            catch (final PasswordException pwdEx) {
                PasswordPolicyUtil.logger.log(Level.WARNING, "Password Validation Failed", (Throwable)pwdEx);
                String message2 = pwdEx.getMessage();
                if (message2.contains("New password cannot be same as old password")) {
                    message2 = I18N.getMsg("desktopcentral.admin.personalise.new_password_same", new Object[0]);
                }
                else if (message2.contains("Old password specified does not match")) {
                    message2 = I18N.getMsg("desktopcentral.admin.personalise.old_password_mismatch", new Object[0]);
                }
                else if (message2.contains("Length of the new password is smaller")) {
                    message2 = I18N.getMsg("dc.admin.UserAdmin.Pwd_char_len", new Object[0]);
                }
                else if (message2.contains("Length of the new password is greater")) {
                    message2 = I18N.getMsg("desktopcentral.admin.personalise.new_password_max_length_mismatch", new Object[0]) + ":" + message2.split(":")[1];
                }
                else if (message2.contains("Password cannot be the same as Login Name")) {
                    message2 = I18N.getMsg("desktopcentral.admin.personalise.new_password_user_name_same", new Object[0]);
                }
                else if (message2.contains("new password matches one of old password")) {
                    message2 = I18N.getMsg("dc.admin.personalise.password_reused", new Object[] { reuseFor });
                }
                throw new APIException(Response.Status.PRECONDITION_FAILED, "PASS0001", message2);
            }
        }
        catch (final APIException dce) {
            PasswordPolicyUtil.logger.log(Level.SEVERE, "Exception while validating password", (Throwable)dce);
            throw dce;
        }
        catch (final Exception ex) {
            PasswordPolicyUtil.logger.log(Level.SEVERE, "Exception while validating password", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "Internal Server Error");
        }
    }
    
    static {
        PasswordPolicyUtil.logger = Logger.getLogger("UserManagementLogger");
    }
}
