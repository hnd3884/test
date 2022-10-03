package com.me.ems.onpremise.server.core;

import java.util.Hashtable;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.adventnet.persistence.Persistence;
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
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import java.util.logging.Logger;

public class ProxySettingsUtil
{
    protected static Logger logger;
    
    public static void loadProxy(final Map returnMap) throws SyMException {
        try {
            final DataObject proxyDetails = getProxyDO();
            if (!proxyDetails.isEmpty()) {
                String userName = null;
                final Row proxyRow = proxyDetails.getRow("ProxyConfiguration");
                returnMap.put("proxyHost", proxyRow.get("HTTPPROXYHOST"));
                returnMap.put("proxyPort", proxyRow.get("HTTPPROXYPORT"));
                returnMap.put("proxyScript", proxyRow.get("PROXYSCRIPT"));
                returnMap.put("proxyType", 2);
                userName = (String)proxyRow.get("HTTPPROXYUSER");
                Boolean isPwdAvail = Boolean.FALSE;
                Boolean isFtpPwdAvail = Boolean.FALSE;
                if (userName != null && !userName.equals("")) {
                    returnMap.put("userName", proxyRow.get("HTTPPROXYUSER"));
                    final String passwordhttp = (String)proxyRow.get("HTTPPROXYPASSWORD");
                    if (passwordhttp != null && !passwordhttp.equals("")) {
                        isPwdAvail = true;
                    }
                    returnMap.put("isPasswordAvail", isPwdAvail);
                }
                if (proxyRow.get("FTPPROXYHOST") != null) {
                    returnMap.put("ftpProxyHost", proxyRow.get("FTPPROXYHOST"));
                    returnMap.put("ftpProxyPort", proxyRow.get("FTPPROXYPORT"));
                    returnMap.put("ftpProxyUserName", proxyRow.get("FTPPROXYUSER"));
                    final String passwordftp = (String)proxyRow.get("FTPPROXYPASSWORD");
                    if (passwordftp != null && !passwordftp.equals("")) {
                        isFtpPwdAvail = true;
                    }
                    returnMap.put("isFtpPasswordAvail", isFtpPwdAvail);
                }
                if ((int)proxyRow.get("PROXYSCRIPT_ENABLED") == 1) {
                    returnMap.put("proxyType", 4);
                }
            }
            final String proxy_defined = SyMUtil.getSyMParameter("proxy_defined");
            returnMap.put("proxyDefined", proxy_defined);
            if (proxyDetails.isEmpty() && proxy_defined.equals("true")) {
                final DownloadManager downloadMgr = DownloadManager.getInstance();
                final int proxyType = DownloadManager.proxyType;
                if (proxyType == 3) {
                    returnMap.put("proxyType", 3);
                }
                else {
                    returnMap.put("proxyType", 1);
                }
            }
        }
        catch (final DataAccessException ex) {
            ProxySettingsUtil.logger.log(Level.WARNING, "DataAccessException while loading proxy form ...", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ProxySettingsUtil.logger.log(Level.WARNING, "Exception while loading proxy form ...", ex2);
            throw new SyMException(1002, (Throwable)ex2);
        }
    }
    
    public static Map setUsedDetailsIfNeeded(final Map proxyDetails) throws SyMException {
        try {
            final Boolean isPasswordModified = proxyDetails.get("isPasswordModified");
            if (!isPasswordModified) {
                final DataObject proxyDetailsDO = getProxyDO();
                final Row proxyRow = proxyDetailsDO.getRow("ProxyConfiguration");
                proxyDetails.put("userName", proxyRow.get("HTTPPROXYUSER"));
                proxyDetails.put("password", proxyRow.get("HTTPPROXYPASSWORD"));
            }
        }
        catch (final DataAccessException e) {
            ProxySettingsUtil.logger.log(Level.WARNING, "DataAccessException while setting the proxy user details ...", (Throwable)e);
            throw new SyMException();
        }
        catch (final SyMException e2) {
            ProxySettingsUtil.logger.log(Level.WARNING, "SyMException while  setting the proxy user details ...", (Throwable)e2);
            throw new SyMException();
        }
        return proxyDetails;
    }
    
    private static DataObject getProxyDO() throws SyMException {
        try {
            final SelectQueryImpl proxy = new SelectQueryImpl(Table.getTable("ProxyConfiguration"));
            proxy.addSelectColumn(new Column("ProxyConfiguration", "*"));
            final DataObject proxyDetails = SyMUtil.getPersistence().get((SelectQuery)proxy);
            return proxyDetails;
        }
        catch (final Exception ex) {
            ProxySettingsUtil.logger.log(Level.WARNING, "Exception while getting proxy settings DO :" + ex);
            throw new SyMException(1002, (Throwable)ex);
        }
    }
    
    public static void saveProxyConfig(final Map proxyDetailsMap) throws Exception {
        final HashMap proxyMap = new HashMap();
        proxyMap.put("proxyF", proxyDetailsMap);
        try {
            String httpProxyHost = null;
            Integer httpProxyPort = null;
            String httpProxyUserName = null;
            String httpProxyPassword = null;
            String ftpProxyHost = null;
            Integer ftpProxyPort = null;
            String ftpProxyUserName = null;
            String ftpProxyPassword = null;
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
                        ProxySettingsUtil.logger.log(Level.INFO, "Proxy added Successfully : Auto Script Proxy");
                    }
                    else {
                        httpProxyHost = proxyConfigForm.get("proxyHost");
                        httpProxyPort = proxyConfigForm.get("proxyPort");
                        SyMUtil.updateSyMParameter("proxyType", "2");
                        downloadMgr.setProxyType(2);
                        ProxySettingsUtil.logger.log(Level.INFO, "Proxy added Successfully : Manual Proxy");
                    }
                    httpProxyUserName = proxyConfigForm.get("userName");
                    httpProxyPassword = Encoder.convertFromBase((String)proxyConfigForm.get("password"));
                    final Boolean isFtpPasswordModified = proxyConfigForm.get("isFtpPasswordModified");
                    final String ftp_temporary_value = proxyConfigForm.get("ftp_temporary_value");
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
                        ProxySettingsUtil.logger.log(Level.INFO, "proxyDetailsFile Location : " + proxyDetailsFile);
                        if (new File(proxyDetailsFile).exists()) {
                            ProxySettingsUtil.logger.log(Level.INFO, "proxyDetailsFile deleted status  : " + new File(proxyDetailsFile).delete());
                        }
                        final File proxyFile = new File(proxyDetailsFile);
                        if (!proxyFile.getParentFile().exists()) {
                            ProxySettingsUtil.logger.log(Level.INFO, "Folder not available : " + proxyFile.getParentFile());
                            ProxySettingsUtil.logger.log(Level.INFO, "Is Folder created : " + proxyFile.getParentFile().mkdir());
                        }
                        StartupUtil.storeProperties(props, proxyDetailsFile, "proxy details");
                        ProxySettingsUtil.logger.log(Level.INFO, "Proxy added Successfully : Direct Connection");
                    }
                    else if (proxyType == 3) {
                        SyMUtil.updateSyMParameter("proxyType", "3");
                        downloadMgr.setProxyType(3);
                        final String proxyDetailsFile2 = SyMUtil.getInstallationDir() + File.separator + "Conf" + File.separator + "User-Conf" + File.separator + "proxy-details.props";
                        final Properties props2 = new Properties();
                        ((Hashtable<String, String>)props2).put("proxyType", Integer.toString(proxyType));
                        ProxySettingsUtil.logger.log(Level.INFO, "proxyDetailsFile Location : " + proxyDetailsFile2);
                        if (new File(proxyDetailsFile2).exists()) {
                            ProxySettingsUtil.logger.log(Level.INFO, "proxyDetailsFile deleted status  : " + new File(proxyDetailsFile2).delete());
                        }
                        final File proxyFile = new File(proxyDetailsFile2);
                        if (!proxyFile.getParentFile().exists()) {
                            ProxySettingsUtil.logger.log(Level.INFO, "Folder not available : " + proxyFile.getParentFile());
                            ProxySettingsUtil.logger.log(Level.INFO, "Is Folder created : " + proxyFile.getParentFile().mkdir());
                        }
                        StartupUtil.storeProperties(props2, proxyDetailsFile2, "proxy details");
                        ProxySettingsUtil.logger.log(Level.INFO, "Proxy added Successfully : No Internet Connection");
                    }
                }
                if (LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T")) {
                    final TaskInfo taskinfo = new TaskInfo();
                    taskinfo.className = "com.me.tools.zcutil.mickeylite.MickeLiteZCSchedule";
                    taskinfo.scheduleTime = System.currentTimeMillis();
                    taskinfo.taskName = "METrackTask";
                    SyMLogger.debug(ProxySettingsUtil.logger, "SettingsUtil", "SaveProxyConfig", "Task Info which is passed to Scheduler.executeAsynchronously(): " + taskinfo);
                    ProxySettingsUtil.logger.log(Level.INFO, "Task info passed Scheduler.executeAsynchronously() is  :  " + taskinfo);
                    new SchedulerProviderImpl().executeAsynchronously(taskinfo);
                }
                MessageProvider.getInstance().hideMessage("PROXY_CONFIGURED_INCORRECTLY");
                MessageProvider.getInstance().hideMessage("PROXY_NOT_CONFIGURED");
                SyMUtil.updateSyMParameter("proxy_defined", "true");
            }
        }
        catch (final Exception e) {
            ProxySettingsUtil.logger.log(Level.INFO, "Excpetion in saveProxyConfig of SettingsUtil: ", e);
            throw e;
        }
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
            ProxySettingsUtil.logger.log(Level.INFO, "DataAccessException in updateProxyConfiguration of SettingsUtil...: ", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ProxySettingsUtil.logger.log(Level.INFO, "Exception in updateProxyConfiguration of SettingsUtil...: ", ex2);
            throw new SyMException(1002, (Throwable)ex2);
        }
    }
    
    public static void writeProxyDetailsIntoFile(final int proxyType, final String httpProxyHost, final Integer httpProxyPort, final String httpProxyUserName, final String httpProxyPassword, final String proxyScript, final int proxyScriptEna) throws SyMException {
        ProxySettingsUtil.logger.log(Level.INFO, "Invoke  writeProxyConfigurationDetails");
        final Properties props = new Properties();
        try {
            ProxySettingsUtil.logger.log(Level.INFO, "Enterred into writeProxyDetailsIntoFile ");
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
                ProxySettingsUtil.logger.log(Level.INFO, "Proxy details : " + logProperties);
                ProxySettingsUtil.logger.log(Level.INFO, "proxyDetailsFile Location : " + proxyDetailsFile);
                if (new File(proxyDetailsFile).exists()) {
                    ProxySettingsUtil.logger.log(Level.INFO, "proxyDetailsFile deleted status  : " + new File(proxyDetailsFile).delete());
                }
                final File proxyFile = new File(proxyDetailsFile);
                if (!proxyFile.getParentFile().exists()) {
                    ProxySettingsUtil.logger.log(Level.INFO, "Folder not available : " + proxyFile.getParentFile());
                    ProxySettingsUtil.logger.log(Level.INFO, "Is Folder created : " + proxyFile.getParentFile().mkdir());
                }
                StartupUtil.storeProperties(props, proxyDetailsFile, "proxy details");
            }
        }
        catch (final Exception ex) {
            ProxySettingsUtil.logger.log(Level.INFO, "Exception in ProxyConfig : ", ex);
        }
    }
    
    static {
        ProxySettingsUtil.logger = Logger.getLogger(ProxySettingsUtil.class.getName());
    }
}
