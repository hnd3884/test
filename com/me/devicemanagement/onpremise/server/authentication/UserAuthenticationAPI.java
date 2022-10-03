package com.me.devicemanagement.onpremise.server.authentication;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.onpremise.server.twofactor.GoogleTwoFactorPassword;
import java.util.Calendar;
import java.security.SecureRandom;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.twofactor.GoogleAuthAction;
import com.me.devicemanagement.onpremise.server.twofactor.MailTwoFactorPassword;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.authentication.PAMException;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.authentication.lm.ADAuthenticator;
import org.json.JSONException;
import java.util.Map;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import com.adventnet.authentication.lm.Authenticator;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import java.util.Properties;
import com.me.devicemanagement.framework.server.admin.AbstractAuthenticationKeyHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.authentication.CredentialAPI;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import org.apache.commons.codec.binary.Base64;
import com.me.devicemanagement.framework.webclient.api.util.DMApi;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.webclient.api.util.APIUtil;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.api.util.APIRequest;
import java.util.logging.Logger;

public class UserAuthenticationAPI
{
    public static UserAuthenticationAPI userAuthenticationAPI;
    protected static Logger logger;
    private String user_name;
    private String domain_name;
    private String service_name;
    private String host_name;
    public static final int API_SERVICE_SDP = 101;
    
    public static UserAuthenticationAPI getInstance() {
        if (UserAuthenticationAPI.userAuthenticationAPI == null) {
            UserAuthenticationAPI.userAuthenticationAPI = new UserAuthenticationAPI();
        }
        return UserAuthenticationAPI.userAuthenticationAPI;
    }
    
    public JSONObject apiAuthenticateUser(final APIRequest apiRequest) {
        final HashMap paramaterList = apiRequest.getParameterList();
        final Object apiVersion = paramaterList.get("apiVersion");
        final float requestAPIVersion = Float.parseFloat(String.valueOf(apiVersion));
        JSONObject authenticateSummary = new JSONObject();
        final String userName = paramaterList.get("username").toLowerCase();
        String domainName = paramaterList.get("domainname");
        domainName = ((domainName == null) ? null : domainName.toLowerCase());
        String tempUserAgent = paramaterList.get("User-Agent");
        tempUserAgent = ((tempUserAgent == null) ? "" : tempUserAgent);
        paramaterList.put("User-Agent", tempUserAgent);
        this.user_name = userName;
        this.domain_name = ((domainName == null) ? "-" : domainName);
        try {
            this.service_name = SYMClientUtil.getServiceName(userName);
        }
        catch (final SyMException e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception while getting service name", (Throwable)e);
            this.service_name = "System";
        }
        this.host_name = apiRequest.getHttpServletRequest().getRemoteHost();
        if (UserManagementUtil.isAccountLocked(this.user_name, this.service_name, this.domain_name)) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Account Locked in User Authentication API");
            return new APIUtil().getErrorObject("10025", "Account locked due to too many failed Login attempts. Please try again later");
        }
        DMApi dmApi = null;
        try {
            final String className = ProductClassLoader.getSingleImplProductClass("DC_AUTH_API_UTIL_CLASS");
            if (className != null && className.trim().length() != 0) {
                dmApi = (DMApi)Class.forName(className).newInstance();
            }
        }
        catch (final Exception e2) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception getting DMApi", e2);
        }
        if (requestAPIVersion > 1.0) {
            authenticateSummary = null;
            String passWord = paramaterList.get("password");
            final String authType = paramaterList.get("auth_type");
            final String forSDP = paramaterList.get("fromsdpapi");
            final String rememberMeToken = paramaterList.get("device_token");
            try {
                passWord = new String(Base64.decodeBase64(passWord + ""));
                final boolean isTwofactorAuthGloballyEnabled = TwoFactorAction.isTwoFactorEnabledGlobaly();
                final HttpServletRequest request = apiRequest.getHttpServletRequest();
                final CredentialAPI credential = new CredentialAPI(userName, passWord, domainName);
                final String userAgent = paramaterList.get("User-Agent");
                final String integrationName = request.getHeader("DC-Integ-Param");
                authenticateSummary = this.resultForDirectLogin(authType, credential, request, userAgent);
                final JSONObject user_permissions = authenticateSummary.getJSONObject("user_permissions");
                final String writeRoles = user_permissions.get("write").toString();
                if (writeRoles.contains("Settings_Write")) {
                    dmApi.handlePostLogin(authenticateSummary, userAgent, integrationName, userName);
                    if (isTwofactorAuthGloballyEnabled) {
                        authenticateSummary = this.resultForTwoFactorLogin(authType, credential, request, rememberMeToken, userAgent);
                    }
                }
                else if (isTwofactorAuthGloballyEnabled) {
                    authenticateSummary = this.resultForTwoFactorLogin(authType, credential, request, rememberMeToken, userAgent);
                }
            }
            catch (final Exception e3) {
                UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception in User Authentication API", e3);
            }
            if (forSDP != null && forSDP.equalsIgnoreCase("true") && authenticateSummary.has("auth_data")) {
                try {
                    final Long loginID = DMUserHandler.getLoginIdForUser(userName, domainName);
                    final JSONObject sdpJson = new JSONObject();
                    final org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
                    if (DMUserHandler.isUserInAdminRole(loginID)) {
                        final String authToken = AbstractAuthenticationKeyHandler.getInstance().generateTechAPIKey();
                        final Properties apiProps = new Properties();
                        apiProps.setProperty("apiKey", authToken);
                        apiProps.setProperty("loginID", String.valueOf(loginID));
                        apiProps.setProperty("SERVICE_TYPE", Integer.toString(101));
                        AbstractAuthenticationKeyHandler.getInstance().addOrUpdateAPIKey(apiProps);
                        sdpJson.put("sdpAPIKey", (Object)authToken);
                        sdpJson.put("sdpResult", (Object)"success");
                        jsonObject.put((Object)"REMARK", (Object)"API Key generation request from SDP for ".concat(userName).concat(" is successful"));
                    }
                    else {
                        sdpJson.put("sdpResult", (Object)"failure");
                        sdpJson.put("sdpMSG", (Object)"User is not a admin user");
                        jsonObject.put((Object)"REMARK", (Object)"API Key generation request from SDP is for non admin user - ".concat(userName));
                    }
                    authenticateSummary.put("sdpAPIKey", (Object)sdpJson);
                    SecurityOneLineLogger.log("DC_Integration", "DC_API_Key_Generation", jsonObject, Level.INFO);
                }
                catch (final Exception e3) {
                    UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception in generating ServiceDesk Plus API Key", e3);
                }
            }
            return authenticateSummary;
        }
        String passWord = paramaterList.get("password");
        String authType = paramaterList.get("auth_type");
        final String forSDP = paramaterList.get("fromsdpapi");
        authType += "";
        passWord = new String(Base64.decodeBase64(passWord + ""));
        final HttpServletRequest request2 = apiRequest.getHttpServletRequest();
        final String userAgent2 = paramaterList.get("User-Agent");
        final CredentialAPI credential2 = new CredentialAPI(userName, passWord, domainName);
        if (authType.equalsIgnoreCase("local_authentication")) {
            authenticateSummary = this.getLoginJSONObject(new LocalAuthenticatorAPI(credential2), credential2, authType);
        }
        else if (authType.equalsIgnoreCase("ad_authentication")) {
            authenticateSummary = this.getLoginJSONObject((Authenticator)new ADAuthenticatorAPI(credential2, request2), credential2, authType);
        }
        else {
            authenticateSummary = new APIUtil().getErrorObject("10001", "Username and password did not match");
        }
        if (authenticateSummary.has("auth_data")) {
            dmApi.mobileAppLoginEntry(authenticateSummary, userAgent2);
            if (forSDP != null && forSDP.equalsIgnoreCase("true")) {
                try {
                    final Long loginID2 = DMUserHandler.getLoginIdForUser(userName, domainName);
                    final JSONObject sdpJson2 = new JSONObject();
                    if (DMUserHandler.isUserInAdminRole(loginID2)) {
                        final String authToken2 = AbstractAuthenticationKeyHandler.getInstance().generateTechAPIKey();
                        final Properties apiProps2 = new Properties();
                        apiProps2.setProperty("apiKey", authToken2);
                        apiProps2.setProperty("loginID", String.valueOf(loginID2));
                        apiProps2.setProperty("SERVICE_TYPE", Integer.toString(101));
                        AbstractAuthenticationKeyHandler.getInstance().addOrUpdateAPIKey(apiProps2);
                        sdpJson2.put("sdpAPIKey", (Object)authToken2);
                        sdpJson2.put("sdpResult", (Object)"success");
                    }
                    else {
                        sdpJson2.put("sdpResult", (Object)"failure");
                        sdpJson2.put("sdpMSG", (Object)"User is not a admin user");
                    }
                    authenticateSummary.put("sdpAPIKey", (Object)sdpJson2);
                }
                catch (final Exception e4) {
                    UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception in generating ServiceDesk Plus API Key", e4);
                }
            }
        }
        return authenticateSummary;
    }
    
    private JSONObject getLoginJSONObject(final Authenticator authenticator, final CredentialAPI credential, final String authType) {
        try {
            if (this.login(authenticator, credential, authType)) {
                final JSONObject loginJson = new JSONObject();
                final HashMap<String, HashMap> userMap = DMUserHandler.getLoginDataForUser(credential.loginName, credential.domainName);
                if (userMap.isEmpty()) {
                    throw new LoginException("Exception in logging in for the username " + credential.loginName);
                }
                UserManagementUtil.removeBadLoginStatusIfPresent(this.user_name, this.service_name, this.domain_name);
                loginJson.put("auth_data", (Object)new JSONObject((Map)userMap.get("auth_data")));
                loginJson.put("user_data", (Object)new JSONObject((Map)userMap.get("user_data")));
                loginJson.put("user_permissions", (Object)new JSONObject((Map)userMap.get("user_permissions")));
                return loginJson;
            }
        }
        catch (final LoginException ex) {
            ex.printStackTrace();
        }
        catch (final JSONException ex2) {
            ex2.printStackTrace();
        }
        return new APIUtil().getErrorObject("10001", "Username and password did not match");
    }
    
    public JSONObject OTPValidateUser(final APIRequest apiRequest) {
        JSONObject OTPauthenticateSummary = new JSONObject();
        final HashMap paramaterList = apiRequest.getParameterList();
        final String UID = paramaterList.get("uid");
        final String OTP = paramaterList.get("otp");
        final String isRememberMeEnabled = paramaterList.get("rememberme_enabled");
        final String forSDP = paramaterList.get("fromsdpapi");
        DMApi dmApi = null;
        try {
            final String className = ProductClassLoader.getSingleImplProductClass("DC_AUTH_API_UTIL_CLASS");
            if (className != null && className.trim().length() != 0) {
                dmApi = (DMApi)Class.forName(className).newInstance();
            }
        }
        catch (final Exception e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception getting DMApi", e);
        }
        if (UID == null) {
            return new APIUtil().getErrorObject("100104", "Unique User Identification ID cannot be null. Use Authentication API to generate it.");
        }
        final JSONObject twoFactorData = new JSONObject();
        final String userAgent = paramaterList.get("User-Agent");
        try {
            final boolean isTwofactorAuthgloballyEnabled = TwoFactorAction.isTwoFactorEnabledGlobaly();
            if (!isTwofactorAuthgloballyEnabled) {
                return new APIUtil().getErrorObject("100105", "Error Occurred. Two Factor not enabled.");
            }
            if (OTP == null) {
                return new APIUtil().getErrorObject("100104", "Error Occurred. OTP cannot be null.");
            }
            final HashMap authenticatedDetails = this.userIdentificationCheck(UID);
            if (authenticatedDetails.isEmpty()) {
                return new APIUtil().getErrorObject("100105", "Not Authenticated. Use Authentication API to generate OTP.");
            }
            final String userName = authenticatedDetails.get("userName");
            String domainName = authenticatedDetails.get("domain");
            domainName = (domainName.equalsIgnoreCase("Local") ? null : domainName);
            this.user_name = userName;
            this.domain_name = ((domainName == null) ? "-" : domainName);
            try {
                this.service_name = SYMClientUtil.getServiceName(userName);
            }
            catch (final SyMException e2) {
                UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception while getting service name", (Throwable)e2);
                this.service_name = "System";
            }
            this.host_name = apiRequest.getHttpServletRequest().getRemoteHost();
            if (UserManagementUtil.isAccountLocked(this.user_name, this.service_name, this.domain_name)) {
                UserAuthenticationAPI.logger.log(Level.SEVERE, "Account Locked in OTP Validate API");
                return new APIUtil().getErrorObject("10025", "Account locked due to too many failed Login attempts. Please try again later");
            }
            if (!this.validateOTP(userName, domainName, OTP)) {
                return new APIUtil().getErrorObject("100106", "Invalid OTP or Unique ID.");
            }
            OTPauthenticateSummary = this.getLoginJSONObject(userName, domainName);
            twoFactorData.put("is_TwoFactor_Enabled", isTwofactorAuthgloballyEnabled);
            if (isRememberMeEnabled.equalsIgnoreCase("true")) {
                twoFactorData.put("is_RememberMe_Enabled", true);
                twoFactorData.put("device_token", (Object)this.setRememberMeTwoFactorToken(userName, domainName));
                twoFactorData.put("remember_token_days", TwoFactorAction.getOtpTimeout());
            }
            else {
                twoFactorData.put("is_RememberMe_Enabled", false);
            }
            OTPauthenticateSummary.put("two_factor_data", (Object)twoFactorData);
            dmApi.mobileAppLoginEntry(OTPauthenticateSummary, userAgent);
            if (forSDP != null && forSDP.equalsIgnoreCase("true") && OTPauthenticateSummary.has("auth_data")) {
                try {
                    final Long loginID = DMUserHandler.getLoginIdForUser(userName, domainName);
                    final JSONObject sdpJson = new JSONObject();
                    if (DMUserHandler.isUserInAdminRole(loginID)) {
                        final String authToken = AbstractAuthenticationKeyHandler.getInstance().generateTechAPIKey();
                        final Properties apiProps = new Properties();
                        apiProps.setProperty("apiKey", authToken);
                        apiProps.setProperty("loginID", String.valueOf(loginID));
                        apiProps.setProperty("SERVICE_TYPE", Integer.toString(101));
                        AbstractAuthenticationKeyHandler.getInstance().addOrUpdateAPIKey(apiProps);
                        sdpJson.put("sdpAPIKey", (Object)authToken);
                        sdpJson.put("sdpResult", (Object)"success");
                    }
                    else {
                        sdpJson.put("sdpResult", (Object)"failure");
                        sdpJson.put("sdpMSG", (Object)"User is not a admin user");
                    }
                    OTPauthenticateSummary.put("sdpAPIKey", (Object)sdpJson);
                }
                catch (final Exception e3) {
                    UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception in generating ServiceDesk Plus API Key", e3);
                }
            }
            this.clearLoginCredentials(UID);
        }
        catch (final Exception e4) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception in User OTP Validate user API", e4);
        }
        return OTPauthenticateSummary;
    }
    
    private JSONObject getLoginJSONObjectForNormalLogin(final Authenticator authenticator, final CredentialAPI credential, final String authType) {
        final JSONObject twoFactorData = new JSONObject();
        try {
            if (this.login(authenticator, credential, authType)) {
                UserManagementUtil.removeBadLoginStatusIfPresent(this.user_name, this.service_name, this.domain_name);
                JSONObject authenticateSummary = new JSONObject();
                authenticateSummary = this.getLoginJSONObject(credential.loginName, credential.domainName);
                twoFactorData.put("is_TwoFactor_Enabled", TwoFactorAction.isTwoFactorEnabledGlobaly());
                authenticateSummary.put("two_factor_data", (Object)twoFactorData);
                return authenticateSummary;
            }
        }
        catch (final Exception e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception in getting Authentication ", e);
        }
        UserManagementUtil.updateBadLoginCount(this.user_name, this.service_name, this.domain_name, this.host_name);
        return new APIUtil().getErrorObject("10001", "Username and password did not match");
    }
    
    private boolean login(final Authenticator authenticator, final CredentialAPI credential, final String authType) throws LoginException {
        if (authType.equalsIgnoreCase("local_authentication")) {
            return authenticator.login();
        }
        return authType.equalsIgnoreCase("ad_authentication") && this.adAuthenticate((ADAuthenticatorAPI)authenticator, credential);
    }
    
    private boolean adAuthenticate(final ADAuthenticatorAPI authenticator, final CredentialAPI credential) throws LoginException {
        final Logger LOGGER = Logger.getLogger(ADAuthenticator.class.getName());
        boolean result = false;
        boolean ssl = false;
        try {
            String domain = credential.domainName;
            final ArrayList<String> dclist = new ArrayList<String>();
            final Column column = Column.getColumn("ActiveDirectoryInfo", "DEFAULTDOMAIN");
            final Criteria criteria = new Criteria(column, (Object)domain, 0);
            final DataObject dataObject = DataAccess.get("ActiveDirectoryInfo", criteria);
            final Row row = dataObject.getFirstRow("ActiveDirectoryInfo");
            final String serverName = (String)row.get("SERVERNAME");
            final String secServerName = (String)row.get("SECONDARYSERVERNAME");
            ssl = (boolean)row.get("ISSSL");
            dclist.add(serverName);
            if (secServerName != null) {
                for (final String dUserName : secServerName.split(",")) {
                    dclist.add(dUserName);
                }
            }
            if (domain == null) {
                domain = (String)row.get("DEFAULTDOMAIN");
            }
            final String userName = (String)row.get("USERNAME");
            final String pword = (String)row.get("PASSWORD");
            final DataObject accountDO = AuthDBUtil.getAccountDO(credential.loginName, credential.serviceName, domain);
            if (accountDO == null) {
                throw new PAMException("Account DO fetched is null");
            }
            if (!accountDO.containsTable("AaaAccount")) {
                throw new LoginException("No such account configured for the user");
            }
            String dUserName = null;
            if (credential.loginName.indexOf("\\") != -1) {
                dUserName = credential.loginName.substring(credential.loginName.indexOf("\\") + 1, credential.loginName.length());
            }
            else {
                dUserName = credential.loginName;
            }
            int counter = 0;
            for (final Object dc : dclist) {
                try {
                    result = authenticator.authenticateUser(dc.toString(), domain, dUserName.trim(), credential.passWord, ssl);
                }
                catch (final Exception var15) {
                    try {
                        authenticator.authenticateUser(dc.toString(), domain, userName, pword, ssl);
                    }
                    catch (final Exception var16) {
                        LOGGER.log(Level.SEVERE, "Domain controller " + dc + " is down");
                        ++counter;
                    }
                    continue;
                }
                if (result) {
                    break;
                }
            }
            LOGGER.log(Level.FINE, "Authentication Result for User {0} is {1}", new Object[] { "*****", result });
            if (result) {
                return result;
            }
            if (counter == dclist.size()) {
                throw new LoginException("Domain Controller " + dclist + "is down");
            }
            throw new LoginException("Invalid loginName/password");
        }
        catch (final Exception var17) {
            LOGGER.log(Level.SEVERE, "Exception occured in Native Authentication");
            final LoginException le = new LoginException(var17.getMessage());
            le.initCause(var17);
            throw le;
        }
    }
    
    private JSONObject getLoginJSONObjectForTwoFactorAuth(final Authenticator authenticator, final CredentialAPI credential, final String rememberMeToken, final String authType) {
        try {
            if (this.login(authenticator, credential, authType)) {
                final String domainName = (credential.domainName == null) ? "Local" : credential.domainName;
                final String UID = this.getLoginTokenEntry(credential.loginName, domainName);
                final JSONObject twoFactorData = new JSONObject();
                JSONObject authenticateSummary = new JSONObject();
                final Long userId = this.getUserIDforLoginName(credential.loginName, credential.domainName);
                final boolean isTwoFactorEnabled = TwoFactorAction.isTwoFactorEnabledGlobaly();
                if (rememberMeToken != null) {
                    if (this.validateDeviceToken(credential.loginName, rememberMeToken)) {
                        UserManagementUtil.removeBadLoginStatusIfPresent(this.user_name, this.service_name, this.domain_name);
                        authenticateSummary = this.getLoginJSONObject(credential.loginName, credential.domainName);
                        twoFactorData.put("is_TwoFactor_Enabled", isTwoFactorEnabled);
                        twoFactorData.put("remember_token_days", TwoFactorAction.getOtpTimeout());
                        twoFactorData.put("OTP_Validation_Required", false);
                        authenticateSummary.put("two_factor_data", (Object)twoFactorData);
                    }
                    else {
                        UserManagementUtil.updateBadLoginCount(this.user_name, this.service_name, this.domain_name, this.host_name);
                        authenticateSummary = new APIUtil().getErrorObject("100105", "Invalid or expired device token.");
                    }
                }
                else {
                    UserManagementUtil.removeBadLoginStatusIfPresent(this.user_name, this.service_name, this.domain_name);
                    final String twoFactorAuthType;
                    final String authMode = twoFactorAuthType = TwoFactorAction.getTwoFactorAuthType();
                    switch (twoFactorAuthType) {
                        case "mail": {
                            final MailTwoFactorPassword mail = new MailTwoFactorPassword();
                            final String userEmail = mail.sendMailPassword(userId);
                            twoFactorData.put("is_TwoFactor_Enabled", isTwoFactorEnabled);
                            twoFactorData.put("unique_userID", (Object)UID);
                            twoFactorData.put("message", (Object)("Mail has been sent to the mail address " + userEmail));
                            twoFactorData.put("remember_token_days", TwoFactorAction.getOtpTimeout());
                            twoFactorData.put("OTP_mail_validity_minutes", (Object)"15");
                            twoFactorData.put("OTP_Validation_Required", true);
                            authenticateSummary.put("two_factor_data", (Object)twoFactorData);
                            break;
                        }
                        case "googleApp": {
                            final GoogleAuthAction googleAuthAction = new GoogleAuthAction(userId);
                            final boolean isGAuthVerified = googleAuthAction.isFirstTimeStatus();
                            if (!isGAuthVerified) {
                                final String secret = googleAuthAction.getSecret();
                                twoFactorData.put("is_TwoFactor_Enabled", isTwoFactorEnabled);
                                twoFactorData.put("unique_userID", (Object)UID);
                                twoFactorData.put("google_authenticator_key", (Object)secret);
                                twoFactorData.put("remember_token_days", TwoFactorAction.getOtpTimeout());
                                twoFactorData.put("OTP_Validation_Required", true);
                                authenticateSummary.put("two_factor_data", (Object)twoFactorData);
                                break;
                            }
                            twoFactorData.put("is_TwoFactor_Enabled", true);
                            twoFactorData.put("unique_userID", (Object)UID);
                            twoFactorData.put("message", (Object)"Google authentication already created for this user. Validate OTP");
                            twoFactorData.put("OTP_Validation_Required", true);
                            twoFactorData.put("remember_token_days", TwoFactorAction.getOtpTimeout());
                            authenticateSummary.put("two_factor_data", (Object)twoFactorData);
                            break;
                        }
                    }
                }
                return authenticateSummary;
            }
        }
        catch (final Exception e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception in getting Two factor auth response API", e);
        }
        UserManagementUtil.updateBadLoginCount(this.user_name, this.service_name, this.domain_name, this.host_name);
        return new APIUtil().getErrorObject("10001", "Username and password did not match");
    }
    
    private JSONObject getLoginJSONObject(final String loginName, final String domainName) {
        final JSONObject loginJson = new JSONObject();
        try {
            final HashMap<String, HashMap> userMap = DMUserHandler.getLoginDataForUser(loginName, domainName);
            if (userMap.isEmpty()) {
                loginJson.put("Error", (Object)"User Data not available");
                loginJson.put("message", (Object)"Exception in server while gathering user data. Data unavailable.");
            }
            else {
                loginJson.put("auth_data", (Object)new JSONObject((Map)userMap.get("auth_data")));
                loginJson.put("user_data", (Object)new JSONObject((Map)userMap.get("user_data")));
                loginJson.put("user_permissions", (Object)new JSONObject((Map)userMap.get("user_permissions")));
            }
            return loginJson;
        }
        catch (final Exception e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception in validation of TwoFactorPassword in Rest API" + e);
            return null;
        }
    }
    
    private boolean validateOTP(final String userName, final String domainName, final String password) {
        try {
            final Long userId = this.getUserIDforLoginName(userName, domainName);
            final String twoFactorAuthType;
            final String authmode = twoFactorAuthType = TwoFactorAction.getTwoFactorAuthType();
            switch (twoFactorAuthType) {
                case "mail": {
                    return this.validateMailAuth(userId, password);
                }
                case "googleApp": {
                    return this.validateGoogleAuth(userId, password);
                }
            }
        }
        catch (final Exception e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception in validation of TwoFactorPassword in Rest API" + e);
        }
        return false;
    }
    
    public String setRememberMeTwoFactorToken(final String userName, final String domainName) {
        String twoFactorToken = null;
        try {
            final Long userId = this.getUserIDforLoginName(userName, domainName);
            final int timeout = TwoFactorAction.getOtpTimeout();
            twoFactorToken = this.generateDeviceToken(userId);
            if (twoFactorToken != null) {
                this.addDeviceTokentoDB(userName, twoFactorToken, timeout);
                UserAuthenticationAPI.logger.log(Level.INFO, "Two factor token generated successfully");
                return twoFactorToken;
            }
            throw new Exception("Exception occurred while generating twoFactorToken");
        }
        catch (final Exception e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception while adding generating " + e.getLocalizedMessage());
            return twoFactorToken;
        }
    }
    
    private Long getUserIDforLoginName(final String loginName, final String domainName) throws SyMException {
        try {
            Criteria cri = new Criteria(Column.getColumn("AaaLogin", "NAME"), (Object)loginName, 0);
            if (domainName != null) {
                cri = cri.and(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)domainName, 0));
            }
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLogin", cri);
            return (dataObject != null || dataObject.isEmpty()) ? ((Long)dataObject.getFirstValue("AaaLogin", "USER_ID")) : null;
        }
        catch (final Exception e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception while getting user Id  for loginName: " + loginName, e);
            return null;
        }
    }
    
    private void clearLoginCredentials(final String uid) {
        try {
            final Column col = Column.getColumn("TwoFactorAuthCheck", "TOKEN");
            final Criteria criteria = new Criteria(col, (Object)uid, 0);
            SyMUtil.getPersistence().delete(criteria);
            UserAuthenticationAPI.logger.log(Level.INFO, "Login token deleted from DB successfully ");
        }
        catch (final DataAccessException e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception while deleting Login token to DB " + e);
        }
    }
    
    private HashMap userIdentificationCheck(final String uid) {
        final HashMap userProperties = new HashMap();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("TwoFactorAuthCheck", "TOKEN"), (Object)uid, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("TwoFactorAuthCheck", criteria);
            final boolean isLoggedin = dataObject != null || dataObject.isEmpty();
            if (isLoggedin) {
                final Row row = dataObject.getFirstRow("TwoFactorAuthCheck");
                final String userName = row.get("USER_NAME").toString();
                final String domainName = row.get("DOMAIN").toString();
                userProperties.put("userName", userName);
                userProperties.put("domain", domainName);
            }
        }
        catch (final DataAccessException e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception while Checking Login token in DB " + e);
        }
        return userProperties;
    }
    
    private String getLoginTokenEntry(final String username, final String domainName) throws DataAccessException {
        final String uniqueUserToken = this.generateIdentificationToken(username);
        try {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            final Row row = new Row("TwoFactorAuthCheck");
            row.set("TOKEN", (Object)uniqueUserToken);
            row.set("USER_NAME", (Object)username);
            row.set("DOMAIN", (Object)domainName);
            dataObject.addRow(row);
            SyMUtil.getPersistence().add(dataObject);
            UserAuthenticationAPI.logger.log(Level.INFO, "Login token added to DB successfully ");
        }
        catch (final Exception ex) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception while adding Login token to DB " + ex);
        }
        return uniqueUserToken;
    }
    
    public String generateIdentificationToken(final String username) {
        final SecureRandom random = new SecureRandom();
        final int num = random.nextInt(100000);
        final String formatted = String.format("%05d", num);
        return username + formatted;
    }
    
    private boolean validateDeviceToken(final String username, final String token) throws Exception {
        try {
            Criteria criteria = new Criteria(Column.getColumn("TwoFactorTokenDetails", "USER_NAME"), (Object)username, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("TwoFactorTokenDetails", "TOKEN"), (Object)token, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get("TwoFactorTokenDetails", criteria);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("TwoFactorTokenDetails");
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(row.get("CREATED_TIME").toString()));
                calendar.add(5, Integer.valueOf(row.get("REMEMBER_ME").toString()));
                if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception while checking two factor token available " + ex);
        }
        return false;
    }
    
    public boolean validateMailAuth(final Long userId, final String password) throws Exception {
        if (password == null) {
            UserAuthenticationAPI.logger.log(Level.INFO, "In validate MailTwoFactorPassword... Password is null");
            return false;
        }
        if (MailTwoFactorPassword.passwordMap.get(userId) == null) {
            UserManagementUtil.updateBadLoginCount(this.user_name, this.service_name, this.domain_name, this.host_name);
            UserAuthenticationAPI.logger.log(Level.INFO, "In validate MailTwoFactorPassword...No match in PasswordMap for the user");
            return false;
        }
        final String comp = MailTwoFactorPassword.passwordMap.get(userId);
        if (comp.equals(password)) {
            UserManagementUtil.removeBadLoginStatusIfPresent(this.user_name, this.service_name, this.domain_name);
            UserAuthenticationAPI.logger.log(Level.INFO, "In validate MailTwoFactorPassword... Second Factor verification completed successfully");
            return true;
        }
        UserManagementUtil.updateBadLoginCount(this.user_name, this.service_name, this.domain_name, this.host_name);
        UserAuthenticationAPI.logger.log(Level.INFO, "In validate MailTwoFactorPassword...No match for the code in PasswordMap");
        return false;
    }
    
    public boolean validateGoogleAuth(final Long userId, final String password) throws Exception {
        UserAuthenticationAPI.logger.log(Level.INFO, "In validation method of GoogleTwoFactorPassword");
        final GoogleAuthAction googleAuthAction = new GoogleAuthAction(userId);
        if (password == null || password.equals("")) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "In validating Google Two Factor Password....Password is null");
            return false;
        }
        long code;
        try {
            code = Long.parseLong(password);
        }
        catch (final NumberFormatException ex) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Number Format Exception in validating Google Two Factor Password...." + ex);
            return false;
        }
        final long time = System.currentTimeMillis();
        boolean status;
        try {
            status = GoogleTwoFactorPassword.checkTOTPCode(googleAuthAction.getSecret(), code, time);
        }
        catch (final NumberFormatException ex2) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception in validating Google Two Factor Password...." + ex2);
            return false;
        }
        if (status) {
            UserManagementUtil.removeBadLoginStatusIfPresent(this.user_name, this.service_name, this.domain_name);
            UserAuthenticationAPI.logger.log(Level.INFO, "Second Factor verification completed successfully. Going to update TOTP Status...");
            googleAuthAction.setFirstTimeStatus(true);
            googleAuthAction.updateUserFirstTimeStatus(userId, true);
        }
        else {
            UserManagementUtil.updateBadLoginCount(this.user_name, this.service_name, this.domain_name, this.host_name);
        }
        return status;
    }
    
    private String generateDeviceToken(final Long userID) throws Exception {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaPassword"));
            selectQuery.addSelectColumn(Column.getColumn("AaaPassword", "PASSWORD_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaPassword", "SALT"));
            selectQuery.addSelectColumn(Column.getColumn("AaaPassword", "ALGORITHM"));
            selectQuery.addJoin(new Join("AaaPassword", "AaaAccPassword", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAccPassword", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)userID, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final MailTwoFactorPassword mailTwoFactorPassword = new MailTwoFactorPassword();
                final Row row = dataObject.getFirstRow("AaaPassword");
                String token = mailTwoFactorPassword.generateRandomPassword() + "" + row.get("SALT") + "" + mailTwoFactorPassword.generateRandomPassword();
                token = AuthUtil.getEncryptedPassword(token, (String)row.get("SALT"), (String)row.get("ALGORITHM"));
                return token;
            }
        }
        catch (final Exception ex) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception while generating two factor token " + ex);
        }
        return null;
    }
    
    private void addDeviceTokentoDB(final String userName, final String twoFactorToken, final int noOfDays) throws Exception {
        try {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            final Row row = new Row("TwoFactorTokenDetails");
            row.set("USER_NAME", (Object)userName);
            row.set("TOKEN", (Object)twoFactorToken);
            row.set("REMEMBER_ME", (Object)noOfDays);
            row.set("CREATED_TIME", (Object)System.currentTimeMillis());
            dataObject.addRow(row);
            SyMUtil.getPersistence().add(dataObject);
            UserAuthenticationAPI.logger.log(Level.INFO, "Two factor token added to DB successfully ");
        }
        catch (final Exception ex) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception while adding two factor token to DB " + ex);
        }
    }
    
    private JSONObject resultForDirectLogin(final String authType, final CredentialAPI credential, final HttpServletRequest request, final String userAgent) {
        final org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
        DMApi dmApi = null;
        try {
            final String className = ProductClassLoader.getSingleImplProductClass("DC_AUTH_API_UTIL_CLASS");
            if (className != null && className.trim().length() != 0) {
                dmApi = (DMApi)Class.forName(className).newInstance();
            }
        }
        catch (final Exception e) {
            UserAuthenticationAPI.logger.log(Level.SEVERE, "Exception getting DMApi", e);
        }
        JSONObject authenticateSummary;
        if (authType.equalsIgnoreCase("local_authentication")) {
            authenticateSummary = this.getLoginJSONObjectForNormalLogin(new LocalAuthenticatorAPI(credential), credential, authType);
            dmApi.mobileAppLoginEntry(authenticateSummary, userAgent);
            if (authenticateSummary.has("auth_data")) {
                jsonObject.put((Object)"REMARK", (Object)"Auth Token has been accessed via Local Authentication by ".concat(credential.loginName));
                SecurityOneLineLogger.log("DC_Integration", "DC_API_Key_Access", jsonObject, Level.INFO);
            }
        }
        else if (authType.equalsIgnoreCase("ad_authentication")) {
            authenticateSummary = this.getLoginJSONObjectForNormalLogin((Authenticator)new ADAuthenticatorAPI(credential, request), credential, authType);
            dmApi.mobileAppLoginEntry(authenticateSummary, userAgent);
            if (authenticateSummary.has("auth_data")) {
                jsonObject.put((Object)"REMARK", (Object)"Auth Token has been accessed via AD Authentication by ".concat(credential.loginName).concat(" - ").concat(credential.domainName));
                SecurityOneLineLogger.log("DC_Integration", "DC_API_Key_Access", jsonObject, Level.INFO);
            }
        }
        else {
            authenticateSummary = new APIUtil().getErrorObject("100010", "Invalid Authentication type.");
        }
        return authenticateSummary;
    }
    
    private JSONObject resultForTwoFactorLogin(final String authType, final CredentialAPI credential, final HttpServletRequest request, final String rememberMeToken, final String userAgent) {
        JSONObject authenticateSummary;
        if (authType.equalsIgnoreCase("local_authentication")) {
            authenticateSummary = this.getLoginJSONObjectForTwoFactorAuth(new LocalAuthenticatorAPI(credential), credential, rememberMeToken, authType);
        }
        else if (authType.equalsIgnoreCase("ad_authentication")) {
            authenticateSummary = this.getLoginJSONObjectForTwoFactorAuth((Authenticator)new ADAuthenticatorAPI(credential, request), credential, rememberMeToken, authType);
        }
        else {
            authenticateSummary = new APIUtil().getErrorObject("100010", "Invalid Authentication type.");
        }
        return authenticateSummary;
    }
    
    static {
        UserAuthenticationAPI.userAuthenticationAPI = null;
        UserAuthenticationAPI.logger = Logger.getLogger("UserManagementLogger");
    }
}
