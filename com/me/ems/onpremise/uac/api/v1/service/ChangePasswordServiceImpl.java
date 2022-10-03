package com.me.ems.onpremise.uac.api.v1.service;

import com.me.ems.framework.uac.api.v1.model.User;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.logging.Level;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.ems.onpremise.uac.api.v1.service.factory.ChangePasswordService;

public class ChangePasswordServiceImpl implements ChangePasswordService
{
    protected static final Logger LOGGER;
    
    @Override
    public Map<String, Object> changePassword(final Long userID, final Long loginID, final String loginName, final String oldPassword, final String newPassword, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            final Map<String, Object> responseMap = new HashMap<String, Object>();
            final JSONObject passwordPolicy = DMOnPremiseUserUtil.getPasswordPolicyDetails();
            final boolean isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
            final Integer minLength = (Integer)(passwordPolicy.has("MIN_LENGTH") ? passwordPolicy.get("MIN_LENGTH") : 5);
            final boolean isComplexPwd = passwordPolicy.has("IS_COMPLEX_PASSWORD") && (boolean)passwordPolicy.get("IS_COMPLEX_PASSWORD");
            final Integer reuseFor = (Integer)(passwordPolicy.has("PREVENT_REUSE_FOR") ? passwordPolicy.get("PREVENT_REUSE_FOR") : 0);
            try {
                responseMap.put("isDemoMode", isDemoMode);
                final boolean isValidPwd = DMOnPremiseUserUtil.isValidPassword(loginName, newPassword, passwordPolicy);
                if (oldPassword != null && !oldPassword.equals("") && newPassword != null && !newPassword.equals("") && isValidPwd && !isDemoMode) {
                    if (newPassword.equalsIgnoreCase("admin")) {
                        throw new APIException("PASS0001", "desktopcentral.admin.new_password_default", new String[0]);
                    }
                    if (newPassword.equalsIgnoreCase(loginName)) {
                        throw new APIException("PASS0001", "desktopcentral.admin.new_password_user_name_same", new String[0]);
                    }
                    SYMClientUtil.changePassword(loginName, oldPassword, newPassword);
                    responseMap.put("displayMsg", I18N.getMsg("dc.admin.uac.pwd_update_sucess", new Object[0]));
                    final String isPasswordChanged = SyMUtil.getSyMParameter("IS_PASSWORD_CHANGED");
                    if (isPasswordChanged.equals("false")) {
                        SyMUtil.updateSyMParameter("IS_PASSWORD_CHANGED", "true");
                    }
                    final UserManagementUtil userManagementUtil = new UserManagementUtil();
                    if (oldPassword.equalsIgnoreCase("admin")) {
                        userManagementUtil.defaultPasswordChanged(userID, false);
                    }
                }
                else if (!isValidPwd) {
                    String message = I18N.getMsg("dc.uac.PASSWORDPOLICY.PASSWORD_VALIDATION", new Object[] { minLength });
                    message = (isComplexPwd ? (message + ' ' + I18N.getMsg("dc.uac.PASSWORDPOLICY.COMPLEXITY_VALIDATION", new Object[0])) : message);
                    throw new APIException("PASS0001", message, new String[0]);
                }
            }
            catch (final APIException apiEx) {
                throw apiEx;
            }
            catch (final Exception ex) {
                ChangePasswordServiceImpl.LOGGER.log(Level.WARNING, "Exception while changing the password", ex);
                this.getExceptionMsgForPassword(ex.getMessage(), reuseFor);
                throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
            }
            DCEventLogUtil.getInstance().addEvent(4001, loginName, (HashMap)null, "dm.event.personalize.pwd_change", (Object)null, false, CustomerInfoUtil.getInstance().getCustomerId());
            return responseMap;
        }
        catch (final APIException apiEx2) {
            throw apiEx2;
        }
        catch (final Exception ex2) {
            ChangePasswordServiceImpl.LOGGER.log(Level.WARNING, "Exception while personalising password ", ex2);
            throw new APIException("GENERIC0002", ex2.getMessage(), new String[0]);
        }
    }
    
    @Override
    public Map<String, Object> getPasswordComplexity() {
        final JSONObject passwordPolicy = DMOnPremiseUserUtil.getPasswordPolicyDetails();
        final Map<String, Object> policyDetails = new HashMap<String, Object>(4);
        final Integer minLength = (Integer)(passwordPolicy.has("MIN_LENGTH") ? passwordPolicy.opt("MIN_LENGTH") : 5);
        final Boolean isComplexPwd = (Boolean)(passwordPolicy.has("IS_COMPLEX_PASSWORD") ? passwordPolicy.opt("IS_COMPLEX_PASSWORD") : Boolean.FALSE);
        final Boolean isLoginRestrictionEnabled = (Boolean)(passwordPolicy.has("ENABLE_LOGIN_RESTRICTION") ? passwordPolicy.opt("ENABLE_LOGIN_RESTRICTION") : Boolean.FALSE);
        if (isLoginRestrictionEnabled) {
            policyDetails.put("maximumBadAttemptCount", passwordPolicy.opt("BAD_ATTEMPT"));
            policyDetails.put("lockPeriod", passwordPolicy.opt("LOCK_PERIOD"));
        }
        policyDetails.put("isLoginRestrictionEnabled", isLoginRestrictionEnabled);
        policyDetails.put("minimumLength", minLength);
        policyDetails.put("isComplexPassword", isComplexPwd);
        return policyDetails;
    }
    
    @Override
    public void getExceptionMsgForPassword(String message, final Integer reuseFor) throws APIException {
        try {
            if (message != null) {
                if (message.contains("New password cannot be same as old password")) {
                    throw new APIException("PASS0002");
                }
                if (message.contains("Old password specified does not match")) {
                    throw new APIException("PASS0003");
                }
                if (message.contains("Length of the new password is smaller")) {
                    throw new APIException("PASS0004");
                }
                if (message.contains("Length of the new password is greater")) {
                    message = I18N.getMsg("desktopcentral.admin.personalise.new_password_max_length_mismatch", new Object[0]) + ":" + message.split(":")[1];
                    throw new APIException("PASS0005", message, new String[0]);
                }
                if (message.contains("Password cannot be the same as Login Name")) {
                    throw new APIException("PASS0006");
                }
                if (message.contains("new password matches one of old password")) {
                    throw new APIException("PASS0007", "dc.admin.personalise.password_reused", new String[] { reuseFor.toString() });
                }
            }
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception ex) {
            ChangePasswordServiceImpl.LOGGER.log(Level.WARNING, "Exception while constructing error msg ", ex);
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
    }
    
    @Override
    public Map<String, Object> changePasswordAndCloseSession(final User user, final HttpServletRequest request, final Map<String, String> passwordDetails) throws APIException {
        try {
            final Boolean closeAllSession = Boolean.parseBoolean(passwordDetails.getOrDefault("closeActiveSession", "true"));
            final boolean isSessionDeleted = closeAllSession && ApiFactoryProvider.getPersonalizationAPIForRest().deleteActiveSession((Long)null, request, user);
            if (isSessionDeleted) {
                String oldEncodedPassword = passwordDetails.get("oldPassword");
                String newEncodedPassword = passwordDetails.get("newPassword");
                oldEncodedPassword = ((oldEncodedPassword != null && !oldEncodedPassword.trim().isEmpty()) ? SyMUtil.decodeAsUTF16LE(oldEncodedPassword) : null);
                newEncodedPassword = ((newEncodedPassword != null && !newEncodedPassword.trim().isEmpty()) ? SyMUtil.decodeAsUTF16LE(newEncodedPassword) : null);
                return this.changePassword(user.getUserID(), user.getLoginID(), user.getName(), oldEncodedPassword, newEncodedPassword, request);
            }
            throw new APIException("USER0002");
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception ex) {
            ChangePasswordServiceImpl.LOGGER.log(Level.WARNING, "Exception while deleting active session of user : " + user.getName(), ex);
            throw new APIException("GENERIC0005");
        }
    }
    
    static {
        LOGGER = Logger.getLogger("UserManagementLogger");
    }
}
