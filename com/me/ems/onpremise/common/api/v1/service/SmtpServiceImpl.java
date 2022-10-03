package com.me.ems.onpremise.common.api.v1.service;

import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.onpremise.server.mail.MailHandler;
import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.onpremise.webclient.settings.SettingsUtil;
import com.me.devicemanagement.framework.server.util.Encoder;
import java.util.List;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.server.mail.MailSettingsImpl;
import java.util.Iterator;
import java.util.Properties;
import com.me.ems.onpremise.common.core.SmtpUtil;
import com.adventnet.persistence.DataAccessException;
import com.me.ems.onpremise.common.oauth.OauthDataHandler;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import com.me.ems.onpremise.common.oauth.OauthException;
import com.me.ems.onpremise.common.oauth.OauthUtil;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.me.ems.onpremise.common.factory.SmtpService;

public class SmtpServiceImpl implements SmtpService
{
    protected Logger logger;
    
    public SmtpServiceImpl() {
        this.logger = Logger.getLogger(SmtpServiceImpl.class.getName());
    }
    
    @Override
    public Map<String, Boolean> isMailServerEnabled() {
        final Map<String, Boolean> isMailServerEnabledMap = new HashMap<String, Boolean>();
        isMailServerEnabledMap.put("status", this.isSmtpConfigured());
        return isMailServerEnabledMap;
    }
    
    @Override
    public Map<String, Object> getSmtpSettings() throws APIException {
        if (this.isSmtpConfigured()) {
            final Map<String, Object> smtpSettingsMap = this.getSmtpSettingsFromDB();
            smtpSettingsMap.remove("smtpPassword");
            smtpSettingsMap.remove("previousErrorCode");
            return smtpSettingsMap;
        }
        this.logger.log(Level.SEVERE, "SMTP Exception: SMTP settings not configured");
        throw new APIException("SMTP002");
    }
    
    @Override
    public void verifyAndSendTestMail(Map<String, Object> smtpSettingsMap) throws APIException {
        smtpSettingsMap = this.processSmtpInputParams(smtpSettingsMap);
        if (smtpSettingsMap.get("authType") == 1) {
            try {
                smtpSettingsMap = OauthUtil.getInstance().attachAccessToken(smtpSettingsMap, true);
            }
            catch (final OauthException e) {
                this.logger.log(Level.SEVERE, "Exception occured in verifyAndSendTestMail", e);
                this.handleOauthErrors(e);
            }
        }
        this.sendTestMail(smtpSettingsMap);
    }
    
    private void handleOauthErrors(final OauthException e) throws APIException {
        final String message = e.getMessage();
        if (message.equalsIgnoreCase("INVALID_REQUEST")) {
            throw new APIException(Response.Status.BAD_REQUEST, "OAUTH001", "dc.admin.smtp.oauth.invalid_request");
        }
        if (message.equalsIgnoreCase("INVALID_CLIENT")) {
            throw new APIException(Response.Status.BAD_REQUEST, "OAUTH004", "dc.admin.smtp.oauth.invalid_client");
        }
        if (message.equalsIgnoreCase("ACCESS_DENIED")) {
            throw new APIException(Response.Status.BAD_REQUEST, "OAUTH002", "dc.admin.smtp.oauth.reauthenticate");
        }
        if (message.equalsIgnoreCase("INVALID_GRANT")) {
            throw new APIException(Response.Status.BAD_REQUEST, "OAUTH003", "dc.admin.smtp.oauth.reauthenticate");
        }
        if (message.equalsIgnoreCase("UNAVAILABLE")) {
            throw new APIException(Response.Status.BAD_REQUEST, "OAUTH005", "dc.admin.smtp.oauth.unavailable");
        }
    }
    
    @Override
    public Response updateSmtpSettings(Map<String, Object> smtpSettingsMap, final String userName, final HttpServletRequest httpServletRequest) throws APIException {
        smtpSettingsMap = this.processSmtpInputParams(smtpSettingsMap);
        final boolean isOauth = smtpSettingsMap.get("authType") == 1;
        if (!this.isSmtpSettingsModified(smtpSettingsMap)) {
            throw new APIException("SMTP016");
        }
        if (isOauth && !smtpSettingsMap.containsKey("REFRESH_TOKEN") && !smtpSettingsMap.containsKey("EXPIRES_AT")) {
            try {
                smtpSettingsMap = OauthUtil.getInstance().attachAccessToken(smtpSettingsMap, false);
            }
            catch (final OauthException e) {
                this.logger.log(Level.SEVERE, "Exception occured in updateSmtpSettings", e);
                this.handleOauthErrors(e);
            }
        }
        if (this.sendTestMail(smtpSettingsMap)) {
            if (isOauth) {
                final Long id = this.parseMapAndUpdateDB(smtpSettingsMap);
                smtpSettingsMap.remove("REFRESH_TOKEN");
                smtpSettingsMap.remove("EXPIRES_AT");
                if (id == null) {
                    throw new APIException("GENERIC0005");
                }
                smtpSettingsMap.put("CREDENTIAL_ID", String.valueOf(id));
            }
            this.saveSmtpSettings(smtpSettingsMap, userName);
            if (!isOauth) {
                OauthDataHandler.getInstance().deleteOAuthCredentials();
            }
        }
        return Response.ok().build();
    }
    
    private Long parseMapAndUpdateDB(final Map<String, Object> smtpSettingsMap) {
        Long credentialId = null;
        final String clientId = smtpSettingsMap.get("clientId");
        final String clientSecret = smtpSettingsMap.get("clientSecret");
        final String authUrl = smtpSettingsMap.get("authUrl");
        final String tokenUrl = smtpSettingsMap.get("tokenUrl");
        final String scope = smtpSettingsMap.get("scope");
        final String accessToken = smtpSettingsMap.get("smtpPassword");
        final String refreshToken = smtpSettingsMap.get("REFRESH_TOKEN");
        final Long expiresAt = smtpSettingsMap.get("EXPIRES_AT");
        try {
            credentialId = OauthDataHandler.getInstance().addOrUpdateOauthCredential(null, clientId, clientSecret, authUrl, tokenUrl, scope, accessToken, refreshToken, expiresAt);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in addOrUpdateOauthCredential", (Throwable)e);
        }
        return credentialId;
    }
    
    @Override
    public void deleteSmtpSettings() throws APIException {
        new SmtpUtil().deleteSmtpSettings();
        if (this.isSmtpConfigured()) {
            throw new APIException("GENERIC0005", "dc.admin.smtp.delete_failed", new String[0]);
        }
    }
    
    private Properties mapToSmtpProperties(final Map<String, Object> map) {
        final Properties properties = new Properties();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            properties.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return properties;
    }
    
    @Override
    public boolean isSmtpConfigured() {
        return new MailSettingsImpl().isMailServerConfigured();
    }
    
    private Map<String, Object> processSmtpInputParams(final Map<String, Object> smtpSettingsMap) throws APIException {
        this.verifySmtpsPort(smtpSettingsMap);
        String missingParams = "";
        final List<String> requiredParams = new ArrayList<String>();
        requiredParams.add("smtpHost");
        requiredParams.add("smtpPort");
        requiredParams.add("senderAddress");
        requiredParams.add("tlsEnabled");
        requiredParams.add("smtpsEnabled");
        if (smtpSettingsMap.getOrDefault("needAuthentication", false)) {
            final int authType = smtpSettingsMap.get("authType");
            if (authType == 0) {
                requiredParams.add("smtpUserName");
                requiredParams.add("smtpPassword");
                if (!smtpSettingsMap.containsKey("smtpPassword")) {
                    final String passwordFromDB = this.getPasswordFromDB();
                    if (!passwordFromDB.isEmpty()) {
                        smtpSettingsMap.put("smtpPassword", passwordFromDB);
                    }
                }
                else {
                    String encodedPassword = smtpSettingsMap.get("smtpPassword");
                    encodedPassword = (encodedPassword.isEmpty() ? encodedPassword : SyMUtil.decodeAsUTF16LE(encodedPassword));
                    smtpSettingsMap.put("smtpPassword", encodedPassword);
                }
            }
            else if (authType == 1) {
                requiredParams.remove("needAuthentication");
                requiredParams.add("clientId");
                requiredParams.add("clientSecret");
                requiredParams.add("authUrl");
                requiredParams.add("tokenUrl");
                requiredParams.add("scope");
                requiredParams.add("proxyEnabled");
            }
        }
        for (final String requiredParam : requiredParams) {
            if (!smtpSettingsMap.containsKey(requiredParam) || smtpSettingsMap.get(requiredParam) == null || smtpSettingsMap.get(requiredParam).toString().isEmpty()) {
                missingParams = missingParams.concat(requiredParam + " ");
            }
        }
        if (!missingParams.isEmpty()) {
            this.logger.log(Level.SEVERE, "SMTP Exception: Required parameters missing while updating SMTP settings : {0}", missingParams);
            String requiredParamsMissingI18n = "";
            try {
                requiredParamsMissingI18n = I18N.getMsg("dc.admin.smtp.params_missing", new Object[0]);
            }
            catch (final Exception ex) {}
            throw new APIException("SMTP001", requiredParamsMissingI18n + missingParams, new String[0]);
        }
        return smtpSettingsMap;
    }
    
    private String getPasswordFromDB() throws APIException {
        String password = "";
        final Map<String, Object> smtpSettingsFromDBMap = this.getSmtpSettingsFromDB();
        if (smtpSettingsFromDBMap.containsKey("smtpPassword")) {
            try {
                final String encodedPassword = smtpSettingsFromDBMap.get("smtpPassword").toString();
                password = Encoder.convertFromBase(encodedPassword);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "SMTP Exception: Exception while fetching credentials ", e);
                throw new APIException("GENERIC0005");
            }
        }
        return password;
    }
    
    private void verifySmtpsPort(final Map<String, Object> smtpSettingsMap) throws APIException {
        if (smtpSettingsMap.get("smtpsEnabled") && smtpSettingsMap.get("smtpPort") != 465) {
            this.logger.log(Level.SEVERE, "SMTP Exception: SMTPS enabled, but port = {0}", smtpSettingsMap.get("smtpPort"));
            throw new APIException("SMTP011");
        }
        if (smtpSettingsMap.get("smtpPort") == 465 && !smtpSettingsMap.get("smtpsEnabled")) {
            this.logger.log(Level.SEVERE, "SMTP Exception: SMTPS disabled, but port = 465");
            throw new APIException("SMTP011", "dc.admin.smtp.port_smtps", new String[0]);
        }
    }
    
    private Map<String, Object> getSmtpSettingsFromDB() throws APIException {
        try {
            final Map<String, Object> smtpSettingsMap = new HashMap<String, Object>();
            final DataObject smtpSettingsDO = SettingsUtil.getSmtpDO();
            if (smtpSettingsDO != null && !smtpSettingsDO.isEmpty()) {
                final Row smtpSettingsRow = smtpSettingsDO.getRow("SmtpConfiguration");
                final String smtpHost = smtpSettingsRow.get("SERVERNAME").toString();
                smtpSettingsMap.put("smtpHost", smtpHost);
                smtpSettingsMap.put("smtpPort", smtpSettingsRow.get("PORT").toString());
                smtpSettingsMap.put("senderName", smtpSettingsRow.get("SENDER_NAME").toString());
                smtpSettingsMap.put("senderAddress", smtpSettingsRow.get("SENDER_ADDRESS").toString());
                smtpSettingsMap.put("tlsEnabled", smtpSettingsRow.get("IS_TLS_ENABLED").toString());
                smtpSettingsMap.put("smtpsEnabled", smtpSettingsRow.get("IS_SMTPS_ENABLED").toString());
                smtpSettingsMap.put("previousErrorCode", smtpSettingsRow.get("PREVIOUS_ERROR_CODE").toString());
                smtpSettingsMap.put("proxyEnabled", smtpSettingsRow.get("USE_PROXY"));
                final int authType = (int)smtpSettingsRow.get("AUTH_TYPE");
                smtpSettingsMap.put("authType", smtpSettingsRow.get("AUTH_TYPE").toString());
                if (authType == 1) {
                    final Long credentialId = (Long)smtpSettingsRow.get("CREDENTIAL_ID");
                    final JSONObject oauthCredential = OauthDataHandler.getInstance().getOauthCredential(credentialId);
                    smtpSettingsMap.put("clientId", oauthCredential.get("CLIENT_ID"));
                    smtpSettingsMap.put("clientSecret", oauthCredential.get("CLIENT_SECRET"));
                    smtpSettingsMap.put("authUrl", oauthCredential.get("AUTH_URL"));
                    smtpSettingsMap.put("tokenUrl", oauthCredential.get("TOKEN_URL"));
                    smtpSettingsMap.put("scope", oauthCredential.get("SCOPE"));
                    smtpSettingsMap.put("urlParameters", this.getAuthorizationServerDetails(smtpHost).get("urlParameters"));
                }
                try {
                    smtpSettingsMap.put("smtpUserName", smtpSettingsRow.get("USERNAME").toString());
                    smtpSettingsMap.put("smtpPassword", smtpSettingsRow.get("PASSWORD").toString());
                    smtpSettingsMap.put("needAuthentication", Boolean.TRUE);
                }
                catch (final NullPointerException e) {
                    smtpSettingsMap.put("needAuthentication", Boolean.FALSE);
                }
            }
            return smtpSettingsMap;
        }
        catch (final SyMException | DataAccessException e2) {
            this.logger.log(Level.SEVERE, "SMTP Exception: Exception while getting SMTP settings DataObject", e2);
            throw new APIException("GENERIC0005");
        }
    }
    
    private boolean sendTestMail(final Map<String, Object> smtpSettingsMap) throws APIException {
        final Properties smtpSettingsProp = this.mapToSmtpProperties(smtpSettingsMap);
        boolean isValid;
        try {
            final SmtpUtil smtpUtil = new SmtpUtil();
            isValid = smtpUtil.checkMailServer(smtpSettingsProp);
            if (!isValid) {
                String mailExceptionMessage = "";
                String mailExceptionClass = "";
                final JSONObject mailFailureDetails = smtpUtil.getMailFailureDetails();
                if (mailFailureDetails != null && mailFailureDetails.has("mailExceptionMessage")) {
                    mailExceptionMessage = (String)mailFailureDetails.get("mailExceptionMessage");
                }
                if (mailFailureDetails != null && mailFailureDetails.has("mailExceptionClass")) {
                    mailExceptionClass = (String)mailFailureDetails.get("mailExceptionClass");
                }
                this.generateSmtpException(mailExceptionMessage, mailExceptionClass);
            }
        }
        catch (final APIException dce) {
            throw dce;
        }
        catch (final JSONException jse) {
            this.logger.log(Level.SEVERE, "SMTP Exception: Exception while parsing mailFailureDetails", (Throwable)jse);
            throw new APIException("GENERIC0005");
        }
        return isValid;
    }
    
    private void generateSmtpException(final String mailExceptionMessage, final String mailExceptionClass) throws APIException {
        String errorCode = "SMTP007";
        switch (mailExceptionClass) {
            case "SocketException": {
                if (mailExceptionMessage.toLowerCase().contains("permission denied")) {
                    errorCode = "SMTP012";
                    break;
                }
                if (!mailExceptionMessage.equalsIgnoreCase("notfound")) {
                    errorCode = "SMTP007";
                    break;
                }
                break;
            }
            case "SSLException": {
                if (mailExceptionMessage.toLowerCase().contains("unrecognized ssl message")) {
                    errorCode = "SMTP006";
                    break;
                }
                break;
            }
            case "SocketTimeoutException": {
                if (mailExceptionMessage.toLowerCase().contains("timed out")) {
                    errorCode = "SMTP009";
                    break;
                }
                break;
            }
            case "SMTPSendFailedException": {
                if (mailExceptionMessage.toLowerCase().contains("authenticat")) {
                    errorCode = "SMTP003";
                    break;
                }
                if (mailExceptionMessage.toLowerCase().contains("permissions to send as this sender")) {
                    errorCode = "SMTP013";
                    break;
                }
                if (mailExceptionMessage.toLowerCase().contains("access denied")) {
                    errorCode = "SMTP014";
                    break;
                }
                break;
            }
            case "AuthenticationFailedException": {
                if (mailExceptionMessage.toLowerCase().contains("no authentication mechansims")) {
                    errorCode = "SMTP004";
                    break;
                }
                if (!mailExceptionMessage.equalsIgnoreCase("notfound")) {
                    errorCode = "SMTP003";
                    break;
                }
                break;
            }
            case "SMTPAddressFailedException": {
                if (mailExceptionMessage.toLowerCase().contains("relay")) {
                    errorCode = "SMTP014";
                    break;
                }
                if (mailExceptionMessage.toLowerCase().contains("access denied")) {
                    errorCode = "SMTP003";
                    break;
                }
                if (mailExceptionMessage.toLowerCase().contains("sender address rejected")) {
                    errorCode = "SMTP015";
                    break;
                }
                if (mailExceptionMessage.toLowerCase().contains("recipient address rejected")) {
                    errorCode = "SMTP010";
                    break;
                }
                break;
            }
            case "AddressException": {
                if (!mailExceptionMessage.equalsIgnoreCase("notfound")) {
                    errorCode = "SMTP005";
                    break;
                }
                break;
            }
            case "UnknownHostException": {
                if (!mailExceptionMessage.equalsIgnoreCase("notfound")) {
                    errorCode = "SMTP008";
                    break;
                }
                break;
            }
            case "IllegalArgumentException": {
                if (mailExceptionMessage.contains("port")) {
                    errorCode = "SMTP011";
                    break;
                }
                break;
            }
        }
        throw new APIException(errorCode);
    }
    
    private boolean isSmtpSettingsModified(final Map<String, Object> smtpSettingsMap) throws APIException {
        final Map<String, Object> smtpSettingsFromDBMap = this.getSmtpSettingsFromDB();
        try {
            smtpSettingsFromDBMap.remove("previousErrorCode");
            if (smtpSettingsFromDBMap.get("smtpPassword") != null && smtpSettingsFromDBMap.get("smtpUserName") != null) {
                final String password = smtpSettingsFromDBMap.get("smtpPassword").toString();
                smtpSettingsFromDBMap.remove("smtpPassword");
                smtpSettingsFromDBMap.put("smtpPassword", Encoder.convertFromBase(password));
                smtpSettingsFromDBMap.put("needAuthentication", Boolean.TRUE.toString());
            }
            else {
                smtpSettingsFromDBMap.put("needAuthentication", Boolean.FALSE.toString());
            }
            if (!smtpSettingsMap.containsKey("smtpPassword")) {
                smtpSettingsFromDBMap.remove("smtpPassword");
            }
        }
        catch (final APIException dce) {
            this.logger.log(Level.SEVERE, "SMTP Exception: Exception in isSmtpSettingsModified", (Throwable)dce);
            throw dce;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "SMTP Exception: Exception in isSmtpSettingsModified", e);
            return true;
        }
        return !smtpSettingsMap.equals(smtpSettingsFromDBMap);
    }
    
    private void saveSmtpSettings(final Map<String, Object> smtpSettingsMap, final String loginUserName) throws APIException {
        if (smtpSettingsMap.get("authType") == 1) {
            smtpSettingsMap.remove("probeHandlerObject");
            smtpSettingsMap.remove("smtpPassword");
            smtpSettingsMap.remove("clientSecret");
        }
        final Properties smtpSettingsProp = this.mapToSmtpProperties(smtpSettingsMap);
        try {
            SettingsUtil.saveSmtpConfigIntoFile(smtpSettingsProp);
            SettingsUtil.saveSmtpConfig(smtpSettingsProp);
            SettingsUtil.postSaveSMTP(smtpSettingsProp);
            MailHandler.getInstance().invokeMailConfigureListener();
            final org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
            if (smtpSettingsMap.get("authType").equals(0)) {
                DCEventLogUtil.getInstance().addEvent(121, loginUserName, (HashMap)null, "dc.admin.smtp.Mail_server_config_applied", (Object)null, true);
                jsonObject.put((Object)"REMARK", (Object)"Mail Server Settings using Basic Authentication saved by ".concat(loginUserName));
            }
            else {
                DCEventLogUtil.getInstance().addEvent(121, loginUserName, (HashMap)null, "dc.admin.smtp.oauth_config_applied", (Object)null, true);
                jsonObject.put((Object)"REMARK", (Object)"Mail Server Settings using OAuth Authentication saved by ".concat(loginUserName));
            }
            SecurityOneLineLogger.log("DC_Integration", "DC_Mail_Server_Settings", jsonObject, Level.INFO);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "SMTP Exception: Exception saving SMTP settings to DB", ex);
            throw new APIException("GENERIC0005");
        }
    }
    
    @Override
    public HashMap getAuthorizationServerDetails(final String smtpHost) {
        final List<HashMap<String, String>> map = SmtpUtil.getAuthServerDetails();
        HashMap result = new HashMap();
        for (final HashMap<String, String> i : map) {
            if (i.get("smtpHost").equals(smtpHost)) {
                result = i;
            }
        }
        return result;
    }
}
