package com.me.mdm.onpremise.api.settings;

import java.util.Hashtable;
import java.io.InputStream;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.exception.NativeException;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import java.util.List;
import com.adventnet.i18n.I18N;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import java.util.ArrayList;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.api.RelationalAPI;
import java.util.TreeMap;
import java.util.Map;
import com.me.devicemanagement.onpremise.webclient.admin.UserController;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.CertificateUtil;
import com.me.devicemanagement.onpremise.server.silentupdate.ondemand.SilentUpdateHandler;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.ems.onpremise.server.util.ServerSettingsUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import java.io.File;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;

public class ServerSettingsFacade
{
    public Logger logger;
    String settingsUpdated;
    String general_module;
    private String mailSettingConf;
    private Properties mailProperties;
    JSONObject commonServerSettingsJson;
    
    public ServerSettingsFacade() {
        this.logger = Logger.getLogger(ServerSettingsFacade.class.getName());
        this.settingsUpdated = null;
        this.general_module = "General";
        this.mailSettingConf = System.getProperty("server.home") + File.separator + "conf" + File.separator + "user-conf" + File.separator + "mail-settings.props";
        this.mailProperties = new Properties();
        this.commonServerSettingsJson = new JSONObject();
    }
    
    public JSONObject getServerSettings() {
        try {
            final JSONObject commonServerSettingsJson = this.getCommonServerSettingsJSON();
            return commonServerSettingsJson;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting serverSettings details...", ex);
            throw new APIHTTPException("SERVRSETTINGS001", new Object[0]);
        }
    }
    
    private JSONObject getCommonServerSettingsJSON() {
        try {
            this.commonServerSettingsJson.put("LAUNCH_DEFAULT_BROWSER", (Object)ServerSettingsUtil.getDefaultClientSettings());
            this.commonServerSettingsJson.put("LOG_LEVEL", (Object)SyMUtil.getCurrentLogLevel());
            this.commonServerSettingsJson.put("ENABLE_TRIM", (Object)ReportCriteriaUtil.getTrimStatus());
            this.commonServerSettingsJson.put("SILENT_UPDATE_SETTINGS", (Object)new SilentUpdateHandler().isAutoApproveEnabled());
            final String enableHttps = com.me.devicemanagement.framework.server.util.SyMUtil.getSyMParameter("ENABLE_HTTPS");
            if (enableHttps != null && !enableHttps.equals("")) {
                if (enableHttps.equalsIgnoreCase("true")) {
                    final CertificateUtil certificateUtil = CertificateUtil.getInstance();
                    final String serverCertificatePath = certificateUtil.getServerCertificateWebSettingsFilePath();
                    if (serverCertificatePath != null) {
                        final Map certificateDetails = certificateUtil.getCertificateDetails(serverCertificatePath);
                        if (certificateDetails != null) {
                            final Map certificateDetailsModified = new HashMap();
                            certificateDetailsModified.put("Issuer_Name", certificateDetails.get("IssuerName"));
                            certificateDetailsModified.put("Issuer_Organizational_UnitName", certificateDetails.get("IssuerOrganizationalUnitName"));
                            certificateDetailsModified.put("Issuer_Organization_Name", certificateDetails.get("IssuerOrganizationName"));
                            certificateDetailsModified.put("Creation_Date", certificateDetails.get("CreationDate"));
                            certificateDetailsModified.put("Expiry_Date", certificateDetails.get("ExpiryDate"));
                            certificateDetailsModified.put("Certificate_Name", certificateDetails.get("Certificate_Name"));
                            this.commonServerSettingsJson.put("certificate_Details", certificateDetailsModified);
                        }
                    }
                }
                this.commonServerSettingsJson.put("ENABLE_HTTPS", (Object)Boolean.valueOf(enableHttps));
            }
            else {
                this.commonServerSettingsJson.put("ENABLE_HTTPS", (Object)Boolean.FALSE);
            }
            final CustomerInfoUtil customerInfoUtil = CustomerInfoUtil.getInstance();
            final boolean isMSP = customerInfoUtil.isMSP();
            if (!isMSP) {
                String defaultDomain = com.me.devicemanagement.framework.server.util.SyMUtil.getSyMParameter("DEFAULT_DOMAIN");
                if (defaultDomain == null) {
                    defaultDomain = "dcLocal";
                }
                this.commonServerSettingsJson.put("default_Domain_Name", (Object)defaultDomain);
                if (defaultDomain != null && !defaultDomain.equalsIgnoreCase("dcLocal")) {
                    this.commonServerSettingsJson.put("default_Domain_Select", (Object)defaultDomain);
                }
                final UserController userController = new UserController();
                final TreeMap domainList = UserController.getADDomainNamesForLoginPage();
                if (domainList != null) {
                    this.commonServerSettingsJson.put("default_DomainSetting_List", (Map)domainList);
                }
            }
            this.commonServerSettingsJson.put("START_SERVER_ON_BOOTUP", (Object)ServerSettingsUtil.isStartServerOnBootup());
            this.getEmailAlertConfigInfo();
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "ServerSettingsFacade: getCommonServerSettingsJSON () ends", ex);
        }
        return this.commonServerSettingsJson;
    }
    
    private JSONObject getEmailAlertConfigInfo() {
        try {
            final DataObject mailDObj = SyMUtil.getEmailAddDO("ServerStartupFailure");
            if (!mailDObj.isEmpty()) {
                final Row row = mailDObj.getRow("EMailAddr");
                final Boolean isEnabled = (Boolean)row.get("SEND_MAIL");
                final String emailIDS = (String)row.get("EMAIL_ADDR");
                this.commonServerSettingsJson.put("ENABLE_EMAIL_ALERT", (Object)isEnabled);
                this.commonServerSettingsJson.put("EMAIL_ID", (Object)emailIDS);
            }
            else {
                final RelationalAPI relapi = RelationalAPI.getInstance();
                Connection conn = null;
                DataSet dataSet = null;
                try {
                    final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUserContactInfo"));
                    selectQuery.addSelectColumn(new Column("AaaUserContactInfo", "CONTACTINFO_ID"));
                    selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
                    selectQuery.addJoin(new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
                    selectQuery.addJoin(new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2));
                    selectQuery.setCriteria(new Criteria(new Column("UMRole", "UM_ROLE_NAME"), (Object)"Administrator", 0));
                    conn = relapi.getConnection();
                    dataSet = relapi.executeQuery((Query)selectQuery, conn);
                    final ArrayList<Long> userContactInfoID = new ArrayList<Long>();
                    while (dataSet.next()) {
                        userContactInfoID.add(Long.parseLong(dataSet.getAsString("CONTACTINFO_ID")));
                    }
                    final Criteria criteria = new Criteria(new Column("AaaContactInfo", "CONTACTINFO_ID"), (Object)userContactInfoID.toArray(new Long[userContactInfoID.size()]), 8);
                    final DataObject userContactInfo = DataAccess.get("AaaContactInfo", criteria);
                    String emailIDs = "";
                    int count = 0;
                    if (!userContactInfo.isEmpty()) {
                        for (Iterator iterator = userContactInfo.getRows("AaaContactInfo"); iterator.hasNext() && count < 3; ++count) {
                            final Row row2 = iterator.next();
                            String mailID = (String)row2.get("EMAILID");
                            mailID = mailID.trim();
                            if (!mailID.equals("") && !emailIDs.contains(mailID)) {
                                emailIDs = emailIDs + mailID + ",";
                            }
                        }
                        this.commonServerSettingsJson.put("ENABLE_EMAIL_ALERT", (Object)Boolean.TRUE);
                        this.commonServerSettingsJson.put("EMAIL_ID", (Object)emailIDs);
                    }
                    else {
                        this.commonServerSettingsJson.put("ENABLE_EMAIL_ALERT", (Object)Boolean.FALSE);
                        this.commonServerSettingsJson.put("EMAIL_ID", (Object)"");
                    }
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "Exception occurred ", ex);
                }
                finally {
                    if (dataSet != null) {
                        dataSet.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception while getting email-address for server failure notification page", exp);
        }
        return this.commonServerSettingsJson;
    }
    
    public JSONObject updateServerSettings(final JSONObject jsonObject) {
        final JSONObject resultData = new JSONObject();
        try {
            final JSONObject serverSettingsDetailsFromReq = jsonObject.getJSONObject("msg_body");
            final CustomerInfoUtil customerInfoUtil = CustomerInfoUtil.getInstance();
            final boolean isMSP = customerInfoUtil.isMSP();
            final JSONObject serverSettingsDetails = new JSONObject();
            serverSettingsDetails.put("EMAIL_ID", serverSettingsDetailsFromReq.get("email_id"));
            serverSettingsDetails.put("START_SERVER_ON_BOOTUP", serverSettingsDetailsFromReq.get("start_server_on_bootup"));
            serverSettingsDetails.put("ENABLE_TRIM", serverSettingsDetailsFromReq.get("enable_trim"));
            serverSettingsDetails.put("LOG_LEVEL", serverSettingsDetailsFromReq.get("log_level"));
            serverSettingsDetails.put("LAUNCH_DEFAULT_BROWSER", serverSettingsDetailsFromReq.get("launch_default_browser"));
            serverSettingsDetails.put("ENABLE_HTTPS", serverSettingsDetailsFromReq.get("enable_https"));
            if (!isMSP) {
                serverSettingsDetails.put("default_Domain_Name", serverSettingsDetailsFromReq.get("default_domain_name"));
            }
            serverSettingsDetails.put("SILENT_UPDATE_SETTINGS", serverSettingsDetailsFromReq.get("silent_update_settings"));
            this.saveCommonServerSettingsData(serverSettingsDetails);
            final String emailIDs = serverSettingsDetails.get("EMAIL_ID").toString();
            final String[] emailArr = emailIDs.split(",");
            List<String> emailArrFromReq = new ArrayList<String>();
            emailArrFromReq = Arrays.asList(emailArr);
            if (new File(this.mailSettingConf).exists()) {
                this.mailProperties = this.getProperties(this.mailSettingConf);
            }
            final String emailFromDB = String.valueOf(((Hashtable<K, Object>)this.mailProperties).get("receiverAddress"));
            if (!emailFromDB.isEmpty() && emailFromDB != null) {
                final StringTokenizer st = new StringTokenizer(emailFromDB, ",");
                for (int size = st.countTokens(), j = 0; j < size; ++j) {
                    String str = st.nextToken();
                    str = str.trim();
                    if (!emailArrFromReq.contains(str.toLowerCase())) {
                        if (this.settingsUpdated != null && !this.settingsUpdated.contains(this.general_module)) {
                            this.settingsUpdated = this.settingsUpdated + "," + this.general_module;
                        }
                        else {
                            this.settingsUpdated = this.general_module;
                        }
                    }
                }
            }
            else if (!emailIDs.isEmpty() && emailIDs != null) {
                if (this.settingsUpdated != null && !this.settingsUpdated.contains(this.general_module)) {
                    this.settingsUpdated = this.settingsUpdated + "," + this.general_module;
                }
                else {
                    this.settingsUpdated = this.general_module;
                }
            }
            ServerSettingsUtil.updateEmailAlertInfo((boolean)Boolean.TRUE, emailIDs);
            if (this.settingsUpdated != null) {
                resultData.put("message", (Object)I18N.getMsg("dc.admin.generalsettings.updated_server_settings", new Object[] { this.settingsUpdated }));
                resultData.put("settings_changed", (Object)"true");
            }
            else {
                resultData.put("message", (Object)I18N.getMsg("mdm.serversettings.nodatamodified", new Object[0]));
                resultData.put("settings_changed", (Object)"false");
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in update ServerSettings", ex);
            throw new APIHTTPException("SERVRSETTINGS002", new Object[0]);
        }
        return resultData;
    }
    
    private void saveCommonServerSettingsData(final JSONObject jsonObject) {
        try {
            final String default_domain_module = "Default Domain";
            final String log_level_module = "Log Level";
            this.settingsUpdated = null;
            final String enableHttpsDb = com.me.devicemanagement.framework.server.util.SyMUtil.getSyMParameter("ENABLE_HTTPS");
            if (enableHttpsDb == null || Boolean.valueOf(enableHttpsDb) != (boolean)jsonObject.get("ENABLE_HTTPS")) {
                com.me.devicemanagement.framework.server.util.SyMUtil.updateSyMParameter("ENABLE_HTTPS", ((Boolean)jsonObject.get("ENABLE_HTTPS")).toString());
                if (this.settingsUpdated != null && !this.settingsUpdated.contains(this.general_module)) {
                    this.settingsUpdated = this.settingsUpdated + "," + this.general_module;
                }
                else {
                    this.settingsUpdated = this.general_module;
                }
            }
            final CustomerInfoUtil customerInfoUtil = CustomerInfoUtil.getInstance();
            final boolean isMSP = customerInfoUtil.isMSP();
            if (!isMSP) {
                final String defaultDomainName = com.me.devicemanagement.framework.server.util.SyMUtil.getSyMParameter("DEFAULT_DOMAIN");
                if (defaultDomainName == null || !defaultDomainName.equals(jsonObject.get("default_Domain_Name").toString())) {
                    com.me.devicemanagement.framework.server.util.SyMUtil.updateSyMParameter("DEFAULT_DOMAIN", jsonObject.get("default_Domain_Name").toString());
                    if (this.settingsUpdated != null && !this.settingsUpdated.contains(default_domain_module)) {
                        this.settingsUpdated = this.settingsUpdated + "," + default_domain_module;
                    }
                    else {
                        this.settingsUpdated = default_domain_module;
                    }
                }
            }
            final boolean clientSetting = ServerSettingsUtil.getDefaultClientSettings();
            if (clientSetting != (boolean)jsonObject.get("LAUNCH_DEFAULT_BROWSER")) {
                ServerSettingsUtil.clientSettings((Boolean)jsonObject.get("LAUNCH_DEFAULT_BROWSER"));
                if (this.settingsUpdated != null && !this.settingsUpdated.contains(this.general_module)) {
                    this.settingsUpdated = this.settingsUpdated + "," + this.general_module;
                }
                else if (this.settingsUpdated == null) {
                    this.settingsUpdated = this.general_module;
                }
            }
            final boolean isSilentUpdateEnabled = new SilentUpdateHandler().isAutoApproveEnabled();
            if (isSilentUpdateEnabled != (boolean)jsonObject.get("SILENT_UPDATE_SETTINGS")) {
                this.setSilentUpdateSettings((boolean)jsonObject.get("SILENT_UPDATE_SETTINGS"));
                if (this.settingsUpdated != null && !this.settingsUpdated.contains("General")) {
                    this.settingsUpdated = this.settingsUpdated + "," + this.general_module;
                }
                else if (this.settingsUpdated == null) {
                    this.settingsUpdated = this.general_module;
                }
            }
            final boolean startServerCurrVal = ServerSettingsUtil.isStartServerOnBootup();
            final boolean startServerNewVal = (boolean)jsonObject.get("START_SERVER_ON_BOOTUP");
            if (startServerCurrVal != startServerNewVal) {
                ServerSettingsUtil.setServerStartOnBootup(startServerNewVal);
                if (this.settingsUpdated != null && !this.settingsUpdated.contains(this.general_module)) {
                    this.settingsUpdated = this.settingsUpdated + "," + this.general_module;
                }
                else if (this.settingsUpdated == null) {
                    this.settingsUpdated = this.general_module;
                }
            }
            final boolean isTrimStatusUpdated = SYMClientUtil.setTrimStatusSetting((Object)(boolean)jsonObject.get("ENABLE_TRIM"));
            if (isTrimStatusUpdated) {
                if (this.settingsUpdated != null && !this.settingsUpdated.contains(this.general_module)) {
                    this.settingsUpdated = this.settingsUpdated + "," + this.general_module;
                }
                else if (this.settingsUpdated == null) {
                    this.settingsUpdated = this.general_module;
                }
            }
            final String oldLogLevel = SyMUtil.getCurrentLogLevel();
            final String newLogLevel = (String)jsonObject.get("LOG_LEVEL");
            if (!oldLogLevel.equalsIgnoreCase(newLogLevel)) {
                SyMUtil.changeLogLevel(newLogLevel);
                if (this.settingsUpdated != null && !this.settingsUpdated.contains(log_level_module)) {
                    this.settingsUpdated = this.settingsUpdated + "," + log_level_module;
                }
                else {
                    this.settingsUpdated = log_level_module;
                }
            }
        }
        catch (final NativeException e) {
            this.logger.log(Level.WARNING, "NativeException while saving server settings ", (Throwable)e);
        }
        catch (final SyMException e2) {
            this.logger.log(Level.WARNING, "SyMException while saving server settings ", (Throwable)e2);
        }
        catch (final Exception e3) {
            this.logger.log(Level.WARNING, "Exception while saving server settings ", e3);
        }
    }
    
    protected void setSilentUpdateSettings(final boolean isSilentUpdateEnabled) {
        final SilentUpdateHandler silentUpdate = new SilentUpdateHandler();
        if (isSilentUpdateEnabled) {
            silentUpdate.enableAutoApprove();
        }
        else {
            silentUpdate.disableAutoApprove();
        }
    }
    
    public Properties getProperties(final String confFileName) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        try {
            if (new File(confFileName).exists()) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Caught exception: ", ex);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Caught exception: ", ex);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {
                this.logger.log(Level.SEVERE, "Caught exception: ", ex2);
            }
        }
        return props;
    }
}
