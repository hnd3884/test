package com.me.devicemanagement.onpremise.webclient.settings;

import java.util.Hashtable;
import HTTPClient.HTTPResponse;
import HTTPClient.HTTPConnection;
import java.net.SocketAddress;
import java.util.List;
import java.net.URLConnection;
import java.io.InputStream;
import HTTPClient.NVPair;
import com.me.devicemanagement.onpremise.server.patch.EPMPatchUtilImpl;
import com.me.devicemanagement.framework.server.downloadmgr.SSLUtil;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import java.net.URL;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.util.ArrayList;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.db.api.RelationalAPI;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.mailmanager.MailerUtils;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Persistence;
import java.util.StringTokenizer;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.server.scheduler.SchedulerProviderImpl;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.scheduler.TaskInfo;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import java.util.Properties;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.Encoder;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.HashMap;
import java.util.logging.Logger;

public class SettingsUtil extends com.me.devicemanagement.framework.webclient.settings.SettingsUtil
{
    protected static Logger logger;
    
    public static void saveProxyAPI(final HashMap proxyMap, final boolean updateDB) throws Exception {
        try {
            String httpProxyHost = null;
            Integer httpProxyPort = null;
            String httpProxyUserName = null;
            String httpProxyPassword = null;
            String ftpProxyHost = null;
            Integer ftpProxyPort = null;
            String ftpProxyUserName = null;
            String ftpProxyPassword = null;
            final Row row1 = null;
            final DataObject dataObject = null;
            final StringTokenizer tokens = null;
            boolean ftp_same_as_http = false;
            Integer proxyType = null;
            String proxyScript = null;
            Integer proxyScriptEna = 0;
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            if (proxyMap != null) {
                final HashMap proxyConfigForm = proxyMap.get("proxyF");
                proxyType = proxyConfigForm.get("proxyType");
                if (proxyType == 2 || proxyType == 4) {
                    if (proxyType == 4) {
                        proxyScript = proxyConfigForm.get("proxyScript");
                        proxyScriptEna = 1;
                        httpProxyHost = "";
                        httpProxyPort = 80;
                        SyMUtil.updateSyMParameter("proxyType", "4");
                        downloadMgr.setProxyType(4);
                        SettingsUtil.logger.log(Level.INFO, "Proxy added Successfully : Auto Script Proxy");
                    }
                    else {
                        httpProxyHost = proxyConfigForm.get("proxyHost");
                        httpProxyPort = proxyConfigForm.get("proxyPort");
                        SyMUtil.updateSyMParameter("proxyType", "2");
                        downloadMgr.setProxyType(2);
                        SettingsUtil.logger.log(Level.INFO, "Proxy added Successfully : Manual Proxy");
                    }
                    httpProxyUserName = proxyConfigForm.get("userName");
                    httpProxyPassword = proxyConfigForm.get("password");
                    final Boolean isPasswordModified = proxyConfigForm.get("isPasswordModified");
                    final Boolean isFtpPasswordModified = proxyConfigForm.get("isFtpPasswordModified");
                    final String http_temporary_value = proxyConfigForm.get("temporary_value");
                    final String ftp_temporary_value = proxyConfigForm.get("ftp_temporary_value");
                    if (!isPasswordModified) {
                        httpProxyPassword = Encoder.convertFromBase(http_temporary_value);
                    }
                    ftp_same_as_http = proxyConfigForm.get("ftp_same_as_http");
                    if (ftp_same_as_http) {
                        ftpProxyHost = proxyConfigForm.get("ftpProxyHost");
                        ftpProxyPort = proxyConfigForm.get("ftpProxyPort");
                        ftpProxyUserName = proxyConfigForm.get("ftpProxyUserName");
                        ftpProxyPassword = proxyConfigForm.get("ftpProxyPassword");
                        if (!isFtpPasswordModified) {
                            ftpProxyPassword = Encoder.convertFromBase(ftp_temporary_value);
                        }
                    }
                    updateProxyConfiguration(httpProxyHost, httpProxyPort, httpProxyUserName, httpProxyPassword, ftpProxyHost, ftpProxyPort, ftpProxyUserName, ftpProxyPassword, ftp_same_as_http, proxyScript, proxyScriptEna);
                    writeProxyDetailsIntoFile(proxyType, httpProxyHost, httpProxyPort, httpProxyUserName, httpProxyPassword, proxyScript, proxyScriptEna);
                }
                else {
                    final Persistence persistence = SyMUtil.getPersistence();
                    final DataObject proxyDetails = getProxyDO();
                    if (!proxyDetails.isEmpty()) {
                        DataAccess.delete(proxyDetails.getRow("ProxyConfiguration"));
                    }
                    if (proxyType == 1) {
                        SyMUtil.updateSyMParameter("proxyType", "1");
                        downloadMgr.setProxyType(1);
                        final Properties props = new Properties();
                        final String proxyDetailsFile = SyMUtil.getInstallationDir() + File.separator + "Conf" + File.separator + "User-Conf" + File.separator + "proxy-details.props";
                        ((Hashtable<String, String>)props).put("proxyType", Integer.toString(proxyType));
                        SettingsUtil.logger.log(Level.INFO, "proxyDetailsFile Location : " + proxyDetailsFile);
                        if (new File(proxyDetailsFile).exists()) {
                            SettingsUtil.logger.log(Level.INFO, "proxyDetailsFile deleted status  : " + new File(proxyDetailsFile).delete());
                        }
                        final File proxyFile = new File(proxyDetailsFile);
                        if (!proxyFile.getParentFile().exists()) {
                            SettingsUtil.logger.log(Level.INFO, "Folder not available : " + proxyFile.getParentFile());
                            SettingsUtil.logger.log(Level.INFO, "Is Folder created : " + proxyFile.getParentFile().mkdir());
                        }
                        StartupUtil.storeProperties(props, proxyDetailsFile, "proxy details");
                        SettingsUtil.logger.log(Level.INFO, "Proxy added Successfully : Direct Connection");
                    }
                    else if (proxyType == 3) {
                        SyMUtil.updateSyMParameter("proxyType", "3");
                        downloadMgr.setProxyType(3);
                        final String proxyDetailsFile2 = SyMUtil.getInstallationDir() + File.separator + "Conf" + File.separator + "User-Conf" + File.separator + "proxy-details.props";
                        final Properties props2 = new Properties();
                        ((Hashtable<String, String>)props2).put("proxyType", Integer.toString(proxyType));
                        SettingsUtil.logger.log(Level.INFO, "proxyDetailsFile Location : " + proxyDetailsFile2);
                        if (new File(proxyDetailsFile2).exists()) {
                            SettingsUtil.logger.log(Level.INFO, "proxyDetailsFile deleted status  : " + new File(proxyDetailsFile2).delete());
                        }
                        final File proxyFile = new File(proxyDetailsFile2);
                        if (!proxyFile.getParentFile().exists()) {
                            SettingsUtil.logger.log(Level.INFO, "Folder not available : " + proxyFile.getParentFile());
                            SettingsUtil.logger.log(Level.INFO, "Is Folder created : " + proxyFile.getParentFile().mkdir());
                        }
                        StartupUtil.storeProperties(props2, proxyDetailsFile2, "proxy details");
                        SettingsUtil.logger.log(Level.INFO, "Proxy added Successfully : No Internet Connection");
                    }
                }
                if (LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T")) {
                    final TaskInfo taskinfo = new TaskInfo();
                    taskinfo.className = "com.me.tools.zcutil.mickeylite.MickeLiteZCSchedule";
                    taskinfo.scheduleTime = System.currentTimeMillis();
                    taskinfo.taskName = "METrackTask";
                    SyMLogger.debug(SettingsUtil.logger, "SettingsUtil", "SaveProxyConfig", "Task Info which is passed to Scheduler.executeAsynchronously(): " + taskinfo);
                    SettingsUtil.logger.log(Level.INFO, "Task info passed Scheduler.executeAsynchronously() is  :  " + taskinfo);
                    new SchedulerProviderImpl().executeAsynchronously(taskinfo);
                }
                MessageProvider.getInstance().hideMessage("PROXY_NOT_CONFIGURED");
                SyMUtil.updateSyMParameter("proxy_defined", "true");
            }
        }
        catch (final SyMException ex) {
            SettingsUtil.logger.log(Level.INFO, "Excpetion in saveProxyConfig of SettingsUtil: ", (Throwable)ex);
            throw ex;
        }
        catch (final Exception e) {
            SettingsUtil.logger.log(Level.INFO, "Excpetion in saveProxyConfig of SettingsUtil: ", e);
            throw e;
        }
    }
    
    protected static void updateProxyConfiguration(final String httpProxyHost, final Integer httpProxyPort, final String httpProxyUserName, final String httpProxyPassword, final String ftpProxyHost, final Integer ftpProxyPort, final String ftpProxyUserName, final String ftpProxyPassword, final boolean ftp_same_as_http) throws SyMException {
        updateProxyConfiguration(httpProxyHost, httpProxyPort, httpProxyUserName, httpProxyPassword, ftpProxyHost, ftpProxyPort, ftpProxyUserName, ftpProxyPassword, ftp_same_as_http, null, new Integer(0));
    }
    
    protected static void updateProxyConfiguration(final String httpProxyHost, final Integer httpProxyPort, final String httpProxyUserName, final String httpProxyPassword, final String ftpProxyHost, final Integer ftpProxyPort, final String ftpProxyUserName, final String ftpProxyPassword, final boolean ftp_same_as_http, final String proxyScript, final Integer proxyScriptEna) throws SyMException {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("ProxyConfiguration"));
            final Column col = new Column("ProxyConfiguration", "*");
            sq.addSelectColumn(col);
            final Persistence persistenceAPI = SyMUtil.getPersistence();
            boolean addrow = false;
            Row rowObjForUpdate = null;
            final DataObject dataobj = persistenceAPI.get(sq);
            String encHttpProxyPassword = null;
            if (dataobj.isEmpty()) {
                rowObjForUpdate = new Row("ProxyConfiguration");
                addrow = true;
            }
            else {
                rowObjForUpdate = dataobj.getFirstRow("ProxyConfiguration");
                encHttpProxyPassword = (String)rowObjForUpdate.get("HTTPPROXYPASSWORD");
            }
            rowObjForUpdate.set("PCID", (Object)new Integer(1));
            if (proxyScript != null) {
                rowObjForUpdate.set("PROXYSCRIPT", (Object)proxyScript);
                rowObjForUpdate.set("HTTPPROXYHOST", (Object)"--");
            }
            else {
                rowObjForUpdate.set("HTTPPROXYHOST", (Object)httpProxyHost);
                rowObjForUpdate.set("HTTPPROXYPORT", (Object)httpProxyPort);
            }
            rowObjForUpdate.set("HTTPPROXYUSER", (Object)httpProxyUserName);
            encHttpProxyPassword = Encoder.convertToNewBase(httpProxyPassword);
            rowObjForUpdate.set("HTTPPROXYPASSWORD", (Object)encHttpProxyPassword);
            rowObjForUpdate.set("PROXYSCRIPT_ENABLED", (Object)proxyScriptEna);
            if (ftp_same_as_http) {
                rowObjForUpdate.set("FTPPROXYHOST", (Object)ftpProxyHost);
                rowObjForUpdate.set("FTPPROXYPORT", (Object)ftpProxyPort);
                rowObjForUpdate.set("FTPPROXYUSER", (Object)ftpProxyUserName);
                final String encFtpProxyPassword = Encoder.convertToNewBase(ftpProxyPassword);
                rowObjForUpdate.set("FTPPROXYPASSWORD", (Object)encFtpProxyPassword);
            }
            if (addrow) {
                dataobj.addRow(rowObjForUpdate);
                persistenceAPI.add(dataobj);
            }
            else {
                dataobj.updateRow(rowObjForUpdate);
                persistenceAPI.update(dataobj);
            }
        }
        catch (final DataAccessException ex) {
            SettingsUtil.logger.log(Level.INFO, "DataAccessException in updateProxyConfiguration of SettingsUtil...: ", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            SettingsUtil.logger.log(Level.INFO, "Exception in updateProxyConfiguration of SettingsUtil...: ", ex2);
            throw new SyMException(1002, (Throwable)ex2);
        }
    }
    
    public static DataObject getProxyDO() throws SyMException {
        try {
            final SelectQueryImpl proxy = new SelectQueryImpl(Table.getTable("ProxyConfiguration"));
            proxy.addSelectColumn(new Column("ProxyConfiguration", "*"));
            final DataObject proxyDetails = SyMUtil.getPersistence().get((SelectQuery)proxy);
            return proxyDetails;
        }
        catch (final Exception ex) {
            SettingsUtil.logger.log(Level.WARNING, "Exception while getting proxy settings DO :" + ex);
            throw new SyMException(1002, (Throwable)ex);
        }
    }
    
    public static boolean isMailserverConfigured() {
        boolean mailServerNotConfigured = true;
        mailServerNotConfigured = !ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
        return mailServerNotConfigured;
    }
    
    public static boolean saveSmtpConfig(final HttpServletRequest request, final Properties props) throws SyMException {
        final boolean checkMailServer = true;
        if (props != null) {
            try {
                final String serverName = ((Hashtable<K, String>)props).get("smtpHost");
                final String port = ((Hashtable<K, Object>)props).get("smtpPort") + "";
                final Boolean isSMTPSEnabled = ((Hashtable<K, Boolean>)props).get("smtpsEnabled");
                final Properties logProperties = new Properties();
                logProperties.putAll(props);
                logProperties.remove("smtpPassword");
                logProperties.remove("senderAddress");
                logProperties.remove("toAddress");
                SettingsUtil.logger.log(Level.INFO, "Smtp config props = " + logProperties);
                saveSmtpConfigIntoFile(props);
                saveSmtpConfig(props);
                SettingsUtil.logger.log(Level.INFO, "Mail server configuration saved");
                MailerUtils.getInstance().validateSMTPSSSLCertificate((boolean)isSMTPSEnabled, serverName, port.toString());
                MessageProvider.getInstance().hideMessage("MAIL_SERVER_NOT_CONFIGURED");
                MessageProvider.getInstance().hideMessage("MAIL_SERVER_CONFIGURED_INCORRECTLY");
                updateErrorCodeInTable(-1);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return checkMailServer;
    }
    
    public static void postSaveSMTP(final Properties props) throws Exception {
        final boolean isSMTPSEnabled = Boolean.parseBoolean(((Hashtable<K, String>)props).get("smtpsEnabled"));
        final String serverName = ((Hashtable<K, String>)props).get("smtpHost");
        final String port = ((Hashtable<K, String>)props).get("smtpPort");
        MailerUtils.getInstance().validateSMTPSSSLCertificate(isSMTPSEnabled, serverName, port);
        MessageProvider.getInstance().hideMessage("MAIL_SERVER_NOT_CONFIGURED");
        MessageProvider.getInstance().hideMessage("MAIL_SERVER_CONFIGURED_INCORRECTLY");
        MessageProvider.getInstance().hideMessage("USER_ADMIN_PAGE_MAIL_SERVER_NOT_CONFIGURED");
        updateErrorCodeInTable(-1);
    }
    
    protected static void updateErrorCodeInTable(final Integer errorCode) throws Exception {
        final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("SmtpConfiguration");
        query.setUpdateColumn("PREVIOUS_ERROR_CODE", (Object)errorCode);
        SyMUtil.getPersistence().update(query);
    }
    
    public static void saveSmtpConfigIntoFile(final Properties prop) {
        try {
            final String mailConfig = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "user-Conf" + File.separator + "mail-settings.props";
            final Properties logProperties = new Properties();
            logProperties.putAll(prop);
            logProperties.remove("smtpPassword");
            logProperties.remove("senderAddress");
            logProperties.remove("toAddress");
            SettingsUtil.logger.log(Level.INFO, "Props in saveSmtpConfigIntoFile = " + logProperties);
            final File mailSettingsFile = new File(mailConfig);
            if (mailSettingsFile.exists()) {
                final Properties mailProperties = StartupUtil.getProperties(mailConfig);
                final String host = CryptoUtil.encrypt(prop.getProperty("smtpHost"));
                final String port = CryptoUtil.encrypt(prop.getProperty("smtpPort"));
                mailProperties.setProperty("smtpHost", host);
                mailProperties.setProperty("smtpPort", port);
                if (String.valueOf(((Hashtable<K, Object>)prop).get("needAuthentication")).equalsIgnoreCase("true")) {
                    mailProperties.setProperty("smtpUserName", prop.getProperty("smtpUserName"));
                    final String password = CryptoUtil.encrypt(prop.getProperty("smtpPassword"));
                    mailProperties.setProperty("smtpPassword", password);
                }
                mailProperties.setProperty("senderName", prop.getProperty("senderName"));
                mailProperties.setProperty("senderAddress", prop.getProperty("senderAddress"));
                mailProperties.setProperty("tlsEnabled", String.valueOf(((Hashtable<K, Object>)prop).get("tlsEnabled")));
                mailProperties.setProperty("smtpsEnabled", String.valueOf(((Hashtable<K, Object>)prop).get("smtpsEnabled")));
                mailProperties.setProperty("needAuthentication", String.valueOf(((Hashtable<K, Object>)prop).get("needAuthentication")));
                final String[] receiverAddrInfo = getEmailAddressForNotification();
                if (receiverAddrInfo != null) {
                    prop.setProperty("receiverAddress", receiverAddrInfo[0]);
                    prop.setProperty("isEmailAlertEnabled", receiverAddrInfo[1]);
                }
                StartupUtil.storeProperties(mailProperties, mailConfig);
            }
        }
        catch (final Exception e) {
            SettingsUtil.logger.log(Level.SEVERE, "Exception occurred while creating mail-config file..", e);
        }
    }
    
    public static String[] getEmailAddressForNotification() {
        Boolean isEnabled = Boolean.TRUE;
        String emailIDS = "";
        Connection conn = null;
        DataSet ds = null;
        try {
            final DataObject mailDObj = com.me.devicemanagement.onpremise.server.util.SyMUtil.getEmailAddDO("ServerStartupFailure");
            if (mailDObj.isEmpty()) {
                final RelationalAPI relapi = RelationalAPI.getInstance();
                try {
                    final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUserContactInfo"));
                    selectQuery.addSelectColumn(new Column("AaaUserContactInfo", "CONTACTINFO_ID"));
                    selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
                    selectQuery.addJoin(new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
                    selectQuery.addJoin(new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2));
                    selectQuery.setCriteria(new Criteria(new Column("UMRole", "UM_ROLE_NAME"), (Object)"Administrator", 0));
                    conn = relapi.getConnection();
                    ds = relapi.executeQuery((Query)selectQuery, conn);
                    final ArrayList<Long> userContactInfoID = new ArrayList<Long>();
                    while (ds.next()) {
                        userContactInfoID.add(Long.parseLong(ds.getAsString("CONTACTINFO_ID")));
                    }
                    final Criteria criteria = new Criteria(new Column("AaaContactInfo", "CONTACTINFO_ID"), (Object)userContactInfoID.toArray(new Long[userContactInfoID.size()]), 8);
                    final DataObject userContactInfo = DataAccess.get("AaaContactInfo", criteria);
                    if (!userContactInfo.isEmpty()) {
                        final Iterator iterator = userContactInfo.getRows("AaaContactInfo");
                        while (iterator.hasNext()) {
                            final Row row = iterator.next();
                            String mailID = (String)row.get("EMAILID");
                            mailID = mailID.trim();
                            if (!mailID.equals("")) {
                                emailIDS = emailIDS + mailID + ",";
                            }
                        }
                    }
                    ds.close();
                }
                catch (final Exception ex) {}
                finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
                return new String[] { emailIDS, String.valueOf(isEnabled) };
            }
            final Row row2 = mailDObj.getRow("EMailAddr");
            isEnabled = (Boolean)row2.get("SEND_MAIL");
            emailIDS = (String)row2.get("EMAIL_ADDR");
        }
        catch (final Exception exp) {
            SettingsUtil.logger.log(Level.WARNING, "Exception while getting email-address for server failure notification page", exp);
            return null;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final Exception e) {
                    SettingsUtil.logger.log(Level.INFO, "Exception while closing the connection..", e);
                }
            }
        }
        return null;
    }
    
    public static void writeProxyDetailsIntoFile(final int proxyType, final String httpProxyHost, final Integer httpProxyPort, final String httpProxyUserName, final String httpProxyPassword, final String proxyScript, final int proxyScriptEna) throws SyMException {
        SettingsUtil.logger.log(Level.INFO, "Invoke  writeProxyConfigurationDetails");
        final Properties props = new Properties();
        try {
            SettingsUtil.logger.log(Level.INFO, "Enterred into writeProxyDetailsIntoFile ");
            final String proxyPort = httpProxyPort.toString();
            String proxyScriptValue = "";
            ((Hashtable<String, String>)props).put("proxyType", Integer.toString(proxyType));
            ((Hashtable<String, String>)props).put("proxyUser", CryptoUtil.encrypt(httpProxyUserName));
            ((Hashtable<String, String>)props).put("proxyScriptEnabled", Integer.toString(proxyScriptEna));
            if (httpProxyPassword != null) {
                final String encHttpProxyPassword = Encoder.convertToNewBase(httpProxyPassword);
                ((Hashtable<String, String>)props).put("proxyPass", encHttpProxyPassword);
            }
            if (proxyScriptEna == 1) {
                proxyScriptValue = proxyScript;
            }
            else {
                ((Hashtable<String, String>)props).put("proxyHost", CryptoUtil.encrypt(httpProxyHost));
                ((Hashtable<String, String>)props).put("proxyPort", CryptoUtil.encrypt(proxyPort));
            }
            if (proxyScriptValue.trim().length() > 0) {
                ((Hashtable<String, String>)props).put("proxyScript", CryptoUtil.encrypt(proxyScript));
            }
            if (props != null && !props.isEmpty()) {
                final Properties logProperties = new Properties();
                logProperties.putAll(props);
                logProperties.remove("proxyPass");
                final String proxyDetailsFile = SyMUtil.getInstallationDir() + File.separator + "Conf" + File.separator + "User-Conf" + File.separator + "proxy-details.props";
                SettingsUtil.logger.log(Level.INFO, "Proxy details : " + logProperties);
                SettingsUtil.logger.log(Level.INFO, "proxyDetailsFile Location : " + proxyDetailsFile);
                if (new File(proxyDetailsFile).exists()) {
                    SettingsUtil.logger.log(Level.INFO, "proxyDetailsFile deleted status  : " + new File(proxyDetailsFile).delete());
                }
                final File proxyFile = new File(proxyDetailsFile);
                if (!proxyFile.getParentFile().exists()) {
                    SettingsUtil.logger.log(Level.INFO, "Folder not available : " + proxyFile.getParentFile());
                    SettingsUtil.logger.log(Level.INFO, "Is Folder created : " + proxyFile.getParentFile().mkdir());
                }
                StartupUtil.storeProperties(props, proxyDetailsFile, "proxy details");
            }
        }
        catch (final Exception ex) {
            SettingsUtil.logger.log(Level.INFO, "Exception in ProxyConfig : ", ex);
        }
    }
    
    public static DataObject getSmtpDO() throws SyMException {
        try {
            final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("SmtpConfiguration"));
            sq.addSelectColumn(new Column("SmtpConfiguration", "*"));
            final DataObject smtpDetails = SyMUtil.getPersistence().get((SelectQuery)sq);
            return smtpDetails;
        }
        catch (final Exception ex) {
            SettingsUtil.logger.log(Level.WARNING, "Exception while getting smtp settings DO :" + ex);
            throw new SyMException(1002, (Throwable)ex);
        }
    }
    
    public static void saveSmtpConfig(final Properties props) throws SyMException {
        try {
            String serverName = null;
            Integer port = null;
            String userName = null;
            String password = null;
            Boolean needAuthentication = null;
            String senderName = null;
            String senderAddress = null;
            Integer authType = null;
            Long credentialId = null;
            Boolean useProxy = null;
            Boolean isTLSEnabled = Boolean.FALSE;
            Boolean isSMTPSEnabled = Boolean.FALSE;
            DataObject dataObject = null;
            if (props != null) {
                serverName = ((Hashtable<K, String>)props).get("smtpHost");
                port = Integer.parseInt(((Hashtable<K, Object>)props).get("smtpPort").toString());
                needAuthentication = Boolean.valueOf(((Hashtable<K, String>)props).getOrDefault("needAuthentication", "false").toString());
                senderName = ((Hashtable<K, String>)props).get("senderName");
                senderAddress = ((Hashtable<K, String>)props).get("senderAddress");
                isTLSEnabled = Boolean.valueOf(((Hashtable<K, Object>)props).get("tlsEnabled").toString());
                isSMTPSEnabled = Boolean.valueOf(((Hashtable<K, Object>)props).get("smtpsEnabled").toString());
                authType = Integer.parseInt(((Hashtable<K, Object>)props).get("authType").toString());
                useProxy = Boolean.parseBoolean(((Hashtable<K, String>)props).getOrDefault("proxyEnabled", "false"));
                if (needAuthentication) {
                    userName = ((Hashtable<K, String>)props).get("smtpUserName");
                    password = ((Hashtable<K, String>)props).get("smtpPassword");
                }
                else if (authType == 1) {
                    userName = ((Hashtable<K, String>)props).get("smtpUserName");
                    credentialId = Long.parseLong(((Hashtable<K, String>)props).get("CREDENTIAL_ID"));
                }
            }
            try {
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SmtpConfiguration"));
                sq.addSelectColumn(new Column((String)null, "*"));
                dataObject = DataAccess.get(sq);
                if (dataObject.isEmpty()) {
                    if (senderName == null) {
                        senderName = "Administrator";
                    }
                    if (senderAddress == null) {
                        senderAddress = "admin@manageengine.com";
                    }
                    final Row row = new Row("SmtpConfiguration");
                    row.set("SERVERNAME", (Object)serverName);
                    row.set("PORT", (Object)port);
                    row.set("USERNAME", (Object)userName);
                    String encPassword = null;
                    if (!"".equals(password) && !"dummypassword".equals(password)) {
                        encPassword = Encoder.convertToNewBase(password);
                    }
                    row.set("PASSWORD", (Object)encPassword);
                    row.set("SENDER_NAME", (Object)senderName);
                    row.set("SENDER_ADDRESS", (Object)senderAddress);
                    row.set("IS_TLS_ENABLED", (Object)isTLSEnabled);
                    if (isSMTPSEnabled != null) {
                        row.set("IS_SMTPS_ENABLED", (Object)isSMTPSEnabled);
                    }
                    row.set("AUTH_TYPE", (Object)authType);
                    row.set("USE_PROXY", (Object)useProxy);
                    row.set("CREDENTIAL_ID", (Object)credentialId);
                    dataObject.addRow(row);
                    final DataObject tempDataObject = DataAccess.add(dataObject);
                    SettingsUtil.logger.log(Level.FINEST, "saveSmtpConfig - add - tempDataObject :" + tempDataObject);
                }
                else {
                    final Row row = dataObject.getFirstRow("SmtpConfiguration");
                    final String senderNameInDB = (String)row.get("SENDER_NAME");
                    final String senderAddressInDB = (String)row.get("SENDER_ADDRESS");
                    if (senderName == null) {
                        if (senderNameInDB == null || senderNameInDB.equals("--")) {
                            senderName = "Administrator";
                        }
                        else {
                            senderName = senderNameInDB;
                        }
                    }
                    if (senderAddress == null) {
                        if (senderAddressInDB == null || senderAddressInDB.equals("--")) {
                            senderAddress = "admin@manageengine.com";
                        }
                        else {
                            senderAddress = senderAddressInDB;
                        }
                    }
                    final String old_enc_pass = (String)row.get("PASSWORD");
                    dataObject.deleteRow(row);
                    DataAccess.delete(row);
                    row.set("SERVERNAME", (Object)serverName);
                    row.set("PORT", (Object)port);
                    row.set("USERNAME", (Object)userName);
                    row.set("IS_TLS_ENABLED", (Object)isTLSEnabled);
                    if (isSMTPSEnabled != null) {
                        row.set("IS_SMTPS_ENABLED", (Object)isSMTPSEnabled);
                    }
                    String encPassword2 = null;
                    if (password == null) {
                        encPassword2 = null;
                    }
                    else if (!"".equals(password) && !"dummypassword".equals(password)) {
                        encPassword2 = Encoder.convertToNewBase(password);
                    }
                    else {
                        encPassword2 = old_enc_pass;
                    }
                    row.set("PASSWORD", (Object)encPassword2);
                    row.set("SENDER_NAME", (Object)senderName);
                    row.set("SENDER_ADDRESS", (Object)senderAddress);
                    row.set("AUTH_TYPE", (Object)authType);
                    row.set("USE_PROXY", (Object)useProxy);
                    row.set("CREDENTIAL_ID", (Object)credentialId);
                    dataObject.addRow(row);
                    final DataObject tempDataObject2 = DataAccess.add(dataObject);
                    SettingsUtil.logger.log(Level.FINEST, "saveSmtpConfig - modify - tempDataObject :" + tempDataObject2);
                }
                SyMUtil.updateSyMParameter("mail_defined", "true");
            }
            catch (final DataAccessException e) {
                SettingsUtil.logger.log(Level.INFO, "DataAccessException in saveSmtpConfig of SettingsUtil ...: ", (Throwable)e);
                throw new SyMException(1001, (Throwable)e);
            }
        }
        catch (final Exception ex) {
            SettingsUtil.logger.log(Level.INFO, "Exception in saveSmtpConfig of SettingsUtil ...: ", ex);
            throw new SyMException(1002, (Throwable)ex);
        }
    }
    
    public static int validateProxy(final JSONObject proxyJson) {
        int directConnection = 0;
        int errCode = 1504;
        InputStream in = null;
        String proxyHost = null;
        String proxyType = null;
        final Properties prop = new Properties();
        try {
            if (proxyJson.opt("proxyType") != null) {
                proxyType = String.valueOf(proxyJson.get("proxyType"));
            }
            SettingsUtil.logger.log(Level.INFO, "Proxy Type configured " + proxyType);
            if (proxyType != null) {
                directConnection = Integer.parseInt(proxyType);
            }
            final String crsbaseUrl = ApiFactoryProvider.getUtilAccessAPI().getCrsBaseUrl();
            final String urlStr = crsbaseUrl + "/dc-crs/crs-meta-data.xml";
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            boolean proxyScriptEnabled = Boolean.FALSE;
            if (proxyJson.opt("proxyScript") != null) {
                proxyScriptEnabled = Boolean.TRUE;
            }
            if (directConnection == 1 || directConnection == 3) {
                prop.setProperty("proxyHost", "");
                prop.setProperty("ftpProxyHost", "");
                if (directConnection == 1) {
                    SettingsUtil.logger.log(Level.INFO, "Trying to connect directly");
                    final URL url = new URL(urlStr);
                    final URLConnection connection = url.openConnection();
                    connection.getInputStream();
                    SyMUtil.updateSyMParameter("is-closed-netowrk", "false");
                    SyMUtil.updateSyMParameter("proxyType", "1");
                    downloadMgr.setNetworkType((boolean)Boolean.FALSE);
                    downloadMgr.setProxyType(1);
                    SettingsUtil.logger.log(Level.INFO, "Machine is directly connected to internet.");
                    SettingsUtil.logger.log(Level.INFO, "Proxy validated Successfully : Direct Conection");
                }
                else {
                    SyMUtil.updateSyMParameter("is-closed-netowrk", "true");
                    SyMUtil.updateSyMParameter("proxyType", "3");
                    downloadMgr.setNetworkType((boolean)Boolean.TRUE);
                    downloadMgr.setProxyType(3);
                    SettingsUtil.logger.log(Level.INFO, "No Internet Connection available");
                    SettingsUtil.logger.log(Level.INFO, "Proxy validated Successfully : No Internet Connection");
                }
            }
            else {
                if (proxyScriptEnabled) {
                    final String proxyScript = proxyJson.optString("proxyScript");
                    prop.setProperty("proxyScript", proxyJson.optString("proxyScript"));
                    prop.setProperty("proxyScriptEna", "1");
                    final PacProxySelector pacProxySelector = new PacProxySelector((PacScriptSource)new UrlPacScriptSource(proxyScript));
                    final List<Proxy> proxyList = pacProxySelector.select(new URI(urlStr));
                    if (proxyList != null && !proxyList.isEmpty()) {
                        for (final Proxy proxy : proxyList) {
                            final SocketAddress address = proxy.address();
                            if (address != null) {
                                proxyHost = ((InetSocketAddress)address).getHostName();
                                if (proxyHost != null) {
                                    break;
                                }
                                continue;
                            }
                        }
                    }
                }
                else {
                    prop.setProperty("proxyHost", proxyJson.optString("proxyHost"));
                    prop.setProperty("proxyPort", proxyJson.optString("proxyPort"));
                }
                if (proxyScriptEnabled && proxyHost == null) {
                    errCode = 1501;
                    SettingsUtil.logger.log(Level.INFO, "Invalid Proxy PAC url");
                }
                else {
                    String user = proxyJson.optString("userName");
                    String pass = proxyJson.optString("password");
                    SettingsUtil.logger.log(Level.INFO, "Trying to establish proxy Connection");
                    if (user == null) {
                        user = "";
                    }
                    if (pass == null) {
                        pass = "";
                    }
                    prop.setProperty("proxyUser", user);
                    prop.setProperty("proxyPass", pass);
                    final SSLUtil util = SSLUtil.getInstance();
                    try {
                        String userAgent = new EPMPatchUtilImpl().getUserAgent();
                        if (userAgent == null) {
                            userAgent = System.getProperty("http.agent");
                            if (userAgent == null) {
                                userAgent = "ManageEngine";
                            }
                        }
                        final NVPair[] headersNVPair = { null };
                        final NVPair nvJsonHeader = new NVPair("User-Agent", userAgent);
                        headersNVPair[0] = nvJsonHeader;
                        final HTTPConnection conn = util.getConnection(urlStr, prop);
                        final HTTPResponse rsp = conn.Get(new URL(urlStr).getFile(), headersNVPair);
                        if (rsp.getStatusCode() != 200) {
                            errCode = 1501;
                            SettingsUtil.logger.log(Level.INFO, "Proxy Failed with status code ::" + rsp.getStatusCode());
                        }
                        else {
                            in = rsp.getInputStream();
                            SettingsUtil.logger.log(Level.INFO, "INPUTSTREAM:" + in);
                            in.close();
                            SyMUtil.updateSyMParameter("is-closed-netowrk", "false");
                            if (directConnection == 2) {
                                SyMUtil.updateSyMParameter("proxyType", "2");
                                downloadMgr.setProxyType(2);
                                SettingsUtil.logger.log(Level.INFO, "Proxy validated Successfully : Manual Proxy");
                            }
                            else if (directConnection == 4) {
                                SyMUtil.updateSyMParameter("proxyType", "4");
                                downloadMgr.setProxyType(4);
                                SettingsUtil.logger.log(Level.INFO, "Proxy validated Successfully : Auto Script Proxy");
                            }
                            downloadMgr.setNetworkType((boolean)Boolean.FALSE);
                            SettingsUtil.logger.log(Level.INFO, "HTTP Based URL Checking is Successfull");
                        }
                    }
                    catch (final Exception ee) {
                        ee.printStackTrace();
                        SettingsUtil.logger.log(Level.INFO, "Proxy Failed To Configure with error ::" + ee.getMessage());
                        if (ee.getMessage().indexOf("server") != -1) {
                            errCode = 1501;
                        }
                        else {
                            errCode = 1503;
                        }
                        SyMUtil.updateSyMParameter("proxy_failed", "true");
                    }
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            SettingsUtil.logger.log(Level.INFO, "Proxy failed To Configure with error ::" + ex.getMessage());
            try {
                if (directConnection == 1) {
                    errCode = 1502;
                }
                else {
                    errCode = 1501;
                }
            }
            catch (final Exception ee2) {
                ee2.printStackTrace();
            }
        }
        return errCode;
    }
    
    static {
        SettingsUtil.logger = Logger.getLogger(SettingsUtil.class.getName());
    }
}
