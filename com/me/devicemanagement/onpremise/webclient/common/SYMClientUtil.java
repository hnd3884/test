package com.me.devicemanagement.onpremise.webclient.common;

import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import java.util.regex.Matcher;
import java.net.InetAddress;
import java.util.regex.Pattern;
import java.util.Locale;
import com.me.devicemanagement.onpremise.server.util.ServerSessionUtil;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.eventlog.EventLogThreadLocal;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.onpremise.server.dbtuning.MssqlTxLogMaintainanceUtil;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import java.util.LinkedHashMap;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import java.util.Hashtable;
import com.me.devicemanagement.onpremise.server.mail.MailHandler;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import com.me.devicemanagement.framework.webclient.cache.SessionAPI;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import java.util.Properties;
import com.adventnet.authentication.PasswordException;
import com.adventnet.authentication.util.AuthUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;

public class SYMClientUtil extends com.me.devicemanagement.framework.webclient.common.SYMClientUtil
{
    public static String getServiceName(final String loginName) throws SyMException {
        try {
            final List tables = new ArrayList();
            tables.add("AaaLogin");
            tables.add("AaaUser");
            tables.add("AaaAccount");
            tables.add("AaaService");
            final SelectQuery query = QueryConstructor.get(tables, new Criteria(Column.getColumn("AaaLogin", "NAME"), (Object)loginName, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            final String serviceName = (String)dataObject.getFirstValue("AaaService", "NAME");
            return serviceName;
        }
        catch (final Exception e) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while getting serviceName for loginName: " + loginName, e);
            throw new SyMException(1002, (Throwable)e);
        }
    }
    
    public static void changePassword(final String loginName, final String oldPasswd, final String newPasswd) throws Exception {
        try {
            final String serviceName = getServiceName(loginName);
            DMUserHandler.addOrUpdateAPIKeyForLoginId(DMUserHandler.getLoginIdForUser(loginName));
            AuthUtil.changePassword(loginName, serviceName, oldPasswd, newPasswd);
            SyMUtil.updateSyMParameter("IS_PASSWORD_CHANGED", "true");
        }
        catch (final PasswordException ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while changing password ", (Throwable)ex);
            throw ex;
        }
        catch (final SyMException ex2) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while changing password ", (Throwable)ex2);
            throw ex2;
        }
    }
    
    public static void changePassword(final String loginName, final String newPasswd) throws Exception {
        try {
            final String serviceName = getServiceName(loginName);
            DMUserHandler.addOrUpdateAPIKeyForLoginId(DMUserHandler.getLoginIdForUser(loginName));
            AuthUtil.changePassword(loginName, serviceName, newPasswd);
            SyMUtil.updateSyMParameter("IS_PASSWORD_CHANGED", "true");
        }
        catch (final PasswordException ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while changing password ", (Throwable)ex);
            throw ex;
        }
        catch (final SyMException ex2) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while changing password ", (Throwable)ex2);
            throw ex2;
        }
    }
    
    public static void changeDefaultAwsPassword(final String loginName, final String newPasswd) throws Exception {
        try {
            final String serviceName = getServiceName(loginName);
            DMUserHandler.addOrUpdateAPIKeyForLoginId(DMUserHandler.getLoginIdForUser(loginName));
            AuthUtil.changePassword(loginName, serviceName, newPasswd);
            SyMUtil.updateSyMParameter("IS_PASSWORD_CHANGED", "true");
            SyMUtil.updateServerParameter("IS_AMAZON_DEFAULT_PASSWORD_CHANGED", "true");
        }
        catch (final PasswordException ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while changing password ", (Throwable)ex);
            throw ex;
        }
        catch (final SyMException ex2) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while changing password ", (Throwable)ex2);
            throw ex2;
        }
    }
    
    public static void convertPropertiesToSessionInfo(final Properties properties, final HttpSession session) {
        String attrName = "";
        if (properties != null && properties.size() > 0) {
            final Enumeration enume = properties.propertyNames();
            while (enume.hasMoreElements()) {
                attrName = enume.nextElement();
                session.setAttribute(attrName, (Object)properties.getProperty(attrName));
            }
        }
    }
    
    public static void setProductInfo(final HttpSession session) {
        final Properties props = SyMUtil.getProductInfo();
        convertPropertiesToSessionInfo(props, session);
    }
    
    public static void setProductInfoInSession(final HttpServletRequest request) {
        final SessionAPI sessionAPI = WebclientAPIFactoryProvider.getSessionAPI();
        final Properties props = SyMUtil.getProductInfo();
        final Set<String> productInfoKeys = props.stringPropertyNames();
        for (final String productInfoKey : productInfoKeys) {
            sessionAPI.addToSession(request, productInfoKey, (Object)props.getProperty(productInfoKey));
        }
    }
    
    public static String getBlockedPorts() {
        String ports = "";
        final int portNo = SyMUtil.getWebServerPort();
        final int httpsPortNo = SyMUtil.getSSLPort();
        final int httpNioPortNo = SyMUtil.getNioWebServerPort();
        final String fireWallAndDCOM = SyMUtil.getSyMParameter("FIREWALL_AND_DCOM_STATUS");
        final String chatNioStatus = SyMUtil.getSyMParameter("SHOW_CHAT_PORT_FIREWALL_EXCEPTION");
        if (fireWallAndDCOM != null && !fireWallAndDCOM.equals("")) {
            final int status = Integer.parseInt(fireWallAndDCOM);
            if (status == 0) {
                ports = portNo + " & " + httpsPortNo;
            }
            else if (status == 1) {
                ports = String.valueOf(portNo);
            }
            else if (status == 2) {
                ports = String.valueOf(httpsPortNo);
            }
        }
        if (chatNioStatus != null && !chatNioStatus.equals("")) {
            if (chatNioStatus.equalsIgnoreCase("true") && !ports.equals("")) {
                ports = ports + " & " + httpNioPortNo;
            }
            else if (chatNioStatus.equalsIgnoreCase("true")) {
                ports = String.valueOf(httpNioPortNo);
            }
        }
        return ports;
    }
    
    public static long getFolderSize(final File folder) {
        long folderSize = 0L;
        if (folder.isDirectory()) {
            final File[] filelist = folder.listFiles();
            for (int s = 0; s < filelist.length; ++s) {
                folderSize += getFolderSize(filelist[s]);
            }
            return folderSize;
        }
        return folder.length();
    }
    
    public static void checkFreeDiskSpaceAvailableStatus(final String activedb, final boolean sendMail) {
        try {
            long diskSpaceNeeded = 0L;
            String requiredSpaceinGBorMB = null;
            long finalDiskSpaceNeeded = 3221225472L;
            if (System.getProperty("diskcheck.max") != null && System.getProperty("diskcheck.enable").equalsIgnoreCase("true")) {
                final Long maxDiskSpace = Long.valueOf(System.getProperty("diskcheck.max"));
                final String byteType = System.getProperty("diskcheck.bytetype");
                if (byteType.equalsIgnoreCase("gb")) {
                    finalDiskSpaceNeeded = maxDiskSpace * 1024L * 1024L * 1024L;
                }
                else if (byteType.equalsIgnoreCase("mb")) {
                    finalDiskSpaceNeeded = maxDiskSpace * 1024L * 1024L;
                }
            }
            final String serverHome = System.getProperty("server.home");
            SYMClientUtil.out.log(Level.INFO, "Server Home :" + serverHome);
            SyMUtil.updateSyMParameter("Show_diskSpace_Msg", String.valueOf(Boolean.FALSE));
            SyMUtil.updateSyMParameter("Show_lowDiskSpace_Msg", String.valueOf(Boolean.FALSE));
            long freeDiskSpace = SyMUtil.getServerFreeSpace(serverHome);
            if (freeDiskSpace < finalDiskSpaceNeeded) {
                SYMClientUtil.out.log(Level.INFO, "Disk space is low than 3 gb");
                SyMUtil.updateSyMParameter("Show_lowDiskSpace_Msg", String.valueOf(Boolean.TRUE));
                MessageProvider.getInstance().unhideMessage("DISK_SPACE_CRITICALLY_LOW");
            }
            else {
                MessageProvider.getInstance().hideMessage("DISK_SPACE_CRITICALLY_LOW");
                final String dbBackUpDirLoc = getdbBackUpDirLoc();
                String defaultBackUpLocation = serverHome + File.separator + "ScheduledDBBackup";
                defaultBackUpLocation = new File(defaultBackUpLocation).getCanonicalPath();
                if (dbBackUpDirLoc != null && !dbBackUpDirLoc.startsWith("\\")) {
                    if (!defaultBackUpLocation.equalsIgnoreCase(dbBackUpDirLoc)) {
                        freeDiskSpace = SyMUtil.getServerFreeSpace(dbBackUpDirLoc);
                    }
                    final String[] backupFoldersList = SyMUtil.getBackupFoldersList(serverHome, activedb);
                    diskSpaceNeeded = getDiskSpaceNeeded(backupFoldersList);
                    finalDiskSpaceNeeded = diskSpaceNeeded * 2L;
                    SYMClientUtil.out.log(Level.INFO, "Disk space needed: " + convertBytesToGBorMB(diskSpaceNeeded) + " and 2 times of the value is: " + convertBytesToGBorMB(finalDiskSpaceNeeded) + " and the free space available is: " + convertBytesToGBorMB(freeDiskSpace));
                    if (freeDiskSpace < finalDiskSpaceNeeded) {
                        final String freeDiskSpaceinGBorMB = convertBytesToGBorMB(freeDiskSpace);
                        requiredSpaceinGBorMB = convertBytesToGBorMB(finalDiskSpaceNeeded);
                        SYMClientUtil.out.log(Level.INFO, "Disk Space is Low and the Needed Space is " + requiredSpaceinGBorMB);
                        SyMUtil.updateSyMParameter("Show_diskSpace_Msg", String.valueOf(Boolean.TRUE));
                        SyMUtil.updateSyMParameter("FreeDiskSpace_size", freeDiskSpaceinGBorMB);
                        SyMUtil.updateSyMParameter("RequiredDiskSpace_size", requiredSpaceinGBorMB);
                        if (sendMail) {
                            sendMailForDiskSpaceAlert(dbBackUpDirLoc, freeDiskSpaceinGBorMB, requiredSpaceinGBorMB);
                        }
                        MessageProvider.getInstance().unhideMessage("LOW_DISK_SPACE_WARNING");
                    }
                    else {
                        MessageProvider.getInstance().hideMessage("LOW_DISK_SPACE_WARNING");
                    }
                }
                else {
                    MessageProvider.getInstance().hideMessage("LOW_DISK_SPACE_WARNING");
                }
            }
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.SEVERE, "Exception occurred while finding Free space availability in server installed location", ex);
        }
    }
    
    public static String getdbBackUpDirLoc() {
        String dbBackUpDirLoc = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DBBackupInfo"));
            query.addSelectColumn(Column.getColumn("DBBackupInfo", "BACKUP_DIR"));
            query.addSelectColumn(Column.getColumn("DBBackupInfo", "ID"));
            final DataObject dobj = DataAccess.get(query);
            dbBackUpDirLoc = (String)dobj.getFirstValue("DBBackupInfo", "BACKUP_DIR");
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Caught exception while checking the backup location...", ex);
        }
        return dbBackUpDirLoc;
    }
    
    public static boolean isdbBackUpDirDefaultLoc(final String serverHome) {
        boolean isBackupDirDefaultLoc = false;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DBBackupInfo"));
            query.addSelectColumn(new Column((String)null, "*"));
            final DataObject data = SyMUtil.getPersistence().get(query);
            final Row dcBackupInfoRow = data.getRow("DBBackupInfo");
            final String dbBackUpDirLoc = (String)dcBackupInfoRow.get("BACKUP_DIR");
            String defaultBackUpLocation = serverHome + File.separator + "ScheduledDBBackup";
            defaultBackUpLocation = new File(defaultBackUpLocation).getCanonicalPath();
            if (defaultBackUpLocation.equalsIgnoreCase(dbBackUpDirLoc)) {
                isBackupDirDefaultLoc = true;
            }
            SYMClientUtil.out.log(Level.INFO, "DBBackupDir is in " + dbBackUpDirLoc);
            SYMClientUtil.out.log(Level.INFO, "DBBackupDir is in " + defaultBackUpLocation);
            SYMClientUtil.out.log(Level.INFO, "Defaultloc " + isBackupDirDefaultLoc);
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Caught exception while checking the backup location...", ex);
        }
        return isBackupDirDefaultLoc;
    }
    
    public static long getDiskSpaceNeeded(final String[] folderList) throws Exception {
        long spaceNeeded = -1L;
        try {
            for (int size = folderList.length, s = 0; s < size; ++s) {
                spaceNeeded += getFolderSize(new File(folderList[s]));
                SYMClientUtil.out.log(Level.INFO, "Folder: " + folderList[s] + "\t Size: " + spaceNeeded);
            }
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Caught exception while finding the size of the backup folders...", ex);
            throw ex;
        }
        return spaceNeeded;
    }
    
    private static void sendMailForDiskSpaceAlert(final String backupLocation, final String freeSize, final String sizeNeeded) {
        try {
            final String moduleName = "DBBackup";
            final String strToAddress = SyMUtil.getEMailAddress(moduleName);
            final DataObject dobj = SyMUtil.getEmailAddDO(moduleName);
            if (dobj.isEmpty()) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail Alert Not configured !!!");
                return;
            }
            final Row emailAlertRow = dobj.getRow("EMailAddr");
            if (emailAlertRow == null) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail Alert Not configured !!!");
                return;
            }
            final Boolean isEnable = (Boolean)emailAlertRow.get("SEND_MAIL");
            if (!isEnable) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail Alerts is Stopped !!!");
                return;
            }
            if (strToAddress == null) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail address is null.  Cant Proceed!!!");
            }
            final Hashtable<String, String> mailSenderDetails = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails();
            final String frAdd = mailSenderDetails.get("mail.fromAddress");
            final String displayName = ProductUrlLoader.getInstance().getValue("displayname");
            final String subject = I18N.getMsg("dc.admin.diskSpaceChk.mailsubject", new Object[] { displayName });
            final MailDetails maildetails = new MailDetails(frAdd, strToAddress);
            maildetails.senderDisplayName = mailSenderDetails.get("mail.fromName");
            final String mailContent = getMailContent(backupLocation, freeSize, sizeNeeded);
            maildetails.bodyContent = mailContent;
            maildetails.ccAddress = null;
            maildetails.subject = subject;
            maildetails.attachment = null;
            MailHandler.getInstance().addToMailQueue(maildetails, 0);
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while sending alert mail", ex);
        }
    }
    
    private static String getMailContent(final String backupLoc, final String freeSize, final String sizeNeeded) {
        try {
            final String displayName = ProductUrlLoader.getInstance().getValue("displayname");
            final String serverURL = ApiFactoryProvider.getUtilAccessAPI().getServerURL();
            String mailContent = I18N.getMsg("dc.admin.diskSpaceChk.mailContent", new Object[] { displayName, backupLoc, freeSize, sizeNeeded, serverURL });
            if (DMApplicationHandler.isMdmProduct()) {
                mailContent = I18N.getMsg("dc.mdm.admin.diskSpaceChk.mailContent", new Object[] { freeSize, sizeNeeded, serverURL });
            }
            return mailContent;
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while getting mail content", ex);
            return null;
        }
    }
    
    public static HashMap getDBDetails() throws Exception {
        HashMap hash = new LinkedHashMap();
        final String activeDB = DBUtil.getActiveDBName();
        final String sDataBaseParamsFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        hash = getDBPropertiesFromFile(sDataBaseParamsFile, activeDB);
        return hash;
    }
    
    private static HashMap getDBPropertiesFromFile(final String sDataBaseParamsFile, final String serverType) throws Exception {
        final Properties dbProps = FileAccessUtil.readProperties(sDataBaseParamsFile);
        final HashMap hash = new HashMap();
        if (dbProps != null) {
            final String connectionUrl = dbProps.getProperty("url");
            final String username = dbProps.getProperty("username");
            final String password = dbProps.getProperty("password");
            if ("mysql".equals(serverType) || "postgres".equals(serverType)) {
                String[] tmp = connectionUrl.split(":");
                hash.put("HOST", tmp[2].substring(2));
                tmp = tmp[3].split("/");
                hash.put("PORT", tmp[0]);
                if (tmp[1].indexOf("?") == -1) {
                    hash.put("DATABASE", tmp[1]);
                }
                else {
                    hash.put("DATABASE", tmp[1].substring(0, tmp[1].indexOf("?")));
                }
            }
            else if ("mssql".equals(serverType)) {
                String[] tmp = connectionUrl.split(":");
                hash.put("HOST", tmp[2].substring(2));
                tmp = tmp[3].split(";");
                hash.put("PORT", tmp[0]);
                for (int i = 1; i < tmp.length; ++i) {
                    final String[] tmp2 = tmp[i].split("=");
                    if ("DatabaseName".equalsIgnoreCase(tmp2[0])) {
                        hash.put("DATABASE", tmp2[1]);
                    }
                    else if ("Domain".equals(tmp2[0])) {
                        hash.put("DOMAIN_NAME", tmp2[1]);
                    }
                    else if ("authenticationScheme=NTLM".equalsIgnoreCase(tmp2[0])) {
                        hash.put("NTLMSetting", Boolean.TRUE);
                    }
                }
            }
            if (username != null) {
                hash.put("USER", username);
            }
            else {
                hash.put("USER", "");
            }
            if (password != null) {
                hash.put("PASSWORD", password);
            }
            else {
                hash.put("PASSWORD", "");
            }
        }
        return hash;
    }
    
    public static String getDataBaseName() {
        HashMap dbDetails = new HashMap();
        try {
            dbDetails = getDBDetails();
            final String dbName = dbDetails.get("DATABASE").toString();
            if (dbName != null && dbName.length() != 0) {
                return dbName;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return "desktopcentral";
    }
    
    public static boolean sendMailForFOS(final String peerIP, final String mailContent, final String mailSubject) {
        try {
            final String moduleName = "FailOver";
            SYMClientUtil.out.log(Level.INFO, "MAil send method called..");
            final String strToAddress = SyMUtil.getEMailAddress(moduleName);
            final DataObject dobj = SyMUtil.getEmailAddDO(moduleName);
            if (dobj.isEmpty()) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail Alert Not configured !!!");
                return false;
            }
            final Row emailAlertRow = dobj.getRow("EMailAddr");
            if (emailAlertRow == null) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail Alert Not configured !!!");
                return false;
            }
            final Boolean isEnable = (Boolean)emailAlertRow.get("SEND_MAIL");
            if (!isEnable) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail Alerts is Stopped !!!");
                return false;
            }
            if (strToAddress == null) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail address is null.  Cant Proceed!!!");
                return false;
            }
            final Hashtable<String, String> mailSenderDetails = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails();
            final String frAdd = mailSenderDetails.get("mail.fromAddress");
            final String subject = I18N.getMsg("dc.admin.fos.mailsubject", new Object[0]);
            final MailDetails maildetails = new MailDetails(frAdd, strToAddress);
            maildetails.senderDisplayName = mailSenderDetails.get("mail.fromName");
            maildetails.bodyContent = mailContent;
            maildetails.ccAddress = null;
            maildetails.subject = mailSubject;
            maildetails.attachment = null;
            MailHandler.getInstance().addToMailQueue(maildetails, 0);
            return true;
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while sending alert mail", ex);
            return false;
        }
    }
    
    public static boolean sendMailForFws(final String mailSubject, final String mailContent) {
        try {
            final String moduleName = "FwServer";
            SYMClientUtil.out.log(Level.INFO, "MAil send method called..");
            final Properties smtpProps = ApiFactoryProvider.getMailSettingAPI().getMailServerDetailsProps();
            final String strToAddress = SyMUtil.getEMailAddress(moduleName);
            final DataObject dobj = SyMUtil.getEmailAddDO(moduleName);
            if (dobj.isEmpty()) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail Alert Not configured !!!");
                return false;
            }
            final Row emailAlertRow = dobj.getRow("EMailAddr");
            if (emailAlertRow == null) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail Alert Not configured !!!");
                return false;
            }
            final Boolean isEnable = (Boolean)emailAlertRow.get("SEND_MAIL");
            if (!isEnable) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail Alerts is Stopped !!!");
                return false;
            }
            if (strToAddress == null) {
                SYMClientUtil.out.log(Level.WARNING, "E-Mail address is null.  Cant Proceed!!!");
                return false;
            }
            final String frAdd = ((Hashtable<K, String>)smtpProps).get("mail.fromAddress");
            final String subject = I18N.getMsg("dc.admin.fws.mailsubject", new Object[0]);
            final MailDetails maildetails = new MailDetails(frAdd, strToAddress);
            maildetails.senderDisplayName = ((Hashtable<K, String>)smtpProps).get("mail.fromName");
            maildetails.bodyContent = mailContent;
            maildetails.ccAddress = null;
            maildetails.subject = mailSubject;
            maildetails.attachment = null;
            MailHandler.getInstance().addToMailQueue(maildetails, 0);
            return true;
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while sending alert mail", ex);
            return false;
        }
    }
    
    public static void checkTransactionLogSizeforMSSQL() {
        try {
            MssqlTxLogMaintainanceUtil.getInstance();
            final int logSizeLimitinMB = MssqlTxLogMaintainanceUtil.txLogSizeToBeSet();
            SYMClientUtil.out.log(Level.INFO, "Check Transaction Log Size for MSSQL in Home page");
            SyMUtil.updateSyMParameter("Show_transactionlimitexceed_Msg", String.valueOf(Boolean.FALSE));
            MessageProvider.getInstance().hideMessage("MSSQL_TRANSACTOIN_LOG_LIMIT_EXCEED");
            MessageProvider.getInstance().hideMessage("MSSQL_TRANSACTION_LOG_CHANGE");
            final RelationalAPI relapi = RelationalAPI.getInstance();
            Connection conn = null;
            Statement selectStmt = null;
            ResultSet rs = null;
            String dcLogSize = null;
            final String dbName = getDataBaseName();
            try {
                conn = relapi.getConnection();
                selectStmt = conn.createStatement();
                rs = selectStmt.executeQuery("select name, size from sys.master_files where name = '" + dbName + "_log'");
                if (rs != null) {
                    while (rs.next()) {
                        dcLogSize = rs.getString("size");
                    }
                    int dcTransactionLogSizeinMB = 0;
                    if (dcLogSize != null) {
                        final long dcTransactionLogSize = Long.parseLong(dcLogSize);
                        dcTransactionLogSizeinMB = (int)dcTransactionLogSize / 128;
                        if (dcTransactionLogSizeinMB >= logSizeLimitinMB) {
                            SyMUtil.updateSyMParameter("Show_transactionlimitexceed_Msg", String.valueOf(Boolean.TRUE));
                            if (MssqlTxLogMaintainanceUtil.hasPermissionsToReadSysdbs()) {
                                if (!MssqlTxLogMaintainanceUtil.isRecoveryModeSimple()) {
                                    MessageProvider.getInstance().unhideMessage("MSSQL_TRANSACTION_LOG_CHANGE");
                                    if (SyMUtil.getServerParameter("mssqlTxMailSent") == null) {
                                        SyMUtil.updateServerParameter("mssqlTxMailSent", "true");
                                        MssqlTxLogMaintainanceUtil.sendRecoveryModeReminderEmail("dm.common.txlog.mail.haspermission.body");
                                    }
                                }
                                else {
                                    MessageProvider.getInstance().unhideMessage("MSSQL_TRANSACTOIN_LOG_LIMIT_EXCEED");
                                }
                            }
                            else {
                                MessageProvider.getInstance().unhideMessage("MSSQL_TRANSACTOIN_LOG_LIMIT_EXCEED");
                            }
                        }
                    }
                    SYMClientUtil.out.log(Level.INFO, "Result for Transaction Log size is : " + dcLogSize);
                    SYMClientUtil.out.log(Level.INFO, "Result for Transaction Log size in MB : " + dcTransactionLogSizeinMB);
                }
                else {
                    SYMClientUtil.out.log(Level.INFO, "No Result was found while getting size of transaction Log ");
                }
            }
            catch (final Exception ex) {
                SYMClientUtil.out.log(Level.WARNING, "SymClientUtil.checkTransactionLogSizeforMSSQL() : Caught exception while getting Transaction Log Size from DB.", ex);
                try {
                    if (conn != null) {
                        conn.close();
                    }
                    if (selectStmt != null) {
                        selectStmt.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (final Exception e) {
                    SYMClientUtil.out.log(Level.WARNING, "SymClientUtil.checkTransactionLogSizeforMSSQL() : Caught exception while closing connection.", e);
                }
            }
            finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                    if (selectStmt != null) {
                        selectStmt.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (final Exception e2) {
                    SYMClientUtil.out.log(Level.WARNING, "SymClientUtil.checkTransactionLogSizeforMSSQL() : Caught exception while closing connection.", e2);
                }
            }
        }
        catch (final Exception ex2) {
            SYMClientUtil.out.log(Level.WARNING, "Exception Occcured while Finding Desktopcentral Transaction log size  ", ex2);
        }
    }
    
    public static Properties webServerLocationReachableProps() {
        final Properties pathStatusProps = new Properties();
        try {
            final Properties wsProps = WebServerUtil.getWebServerSettings();
            final String storePathVal = wsProps.getProperty("store.loc");
            final boolean storePathStatus = WebServerUtil.hasWriteAccess(storePathVal);
            pathStatusProps.setProperty("store_access_status", Boolean.toString(storePathStatus));
            final String swPathVal = wsProps.getProperty("swrepository.loc");
            final boolean swRepositoryPathStatus = WebServerUtil.hasWriteAccess(swPathVal);
            pathStatusProps.setProperty("swrepository_access_status", Boolean.toString(swRepositoryPathStatus));
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Caught exception while checking share access status.", ex);
        }
        return pathStatusProps;
    }
    
    public static boolean isEveryonePrivilegeAvailableInDestinationLocation(final String sharePath) {
        final File sharePathLocation = new File(sharePath);
        final boolean direxists = sharePathLocation.isDirectory();
        SYMClientUtil.out.log(Level.INFO, "Share path Location {0} exists ? :: {1}", new Object[] { sharePathLocation, direxists });
        try {
            final String shareLocation = sharePath.replace("\\", "/");
            if (shareLocation.startsWith("//")) {
                final String[] strPathParts = shareLocation.split("/");
                SYMClientUtil.out.log(Level.INFO, " EveryOne permission is available in that Location : " + shareLocation);
                final String sid = "*s-1-1-0";
                final String everyOnePermissionAvailableResult = WebServerUtil.isPermissionAvailable(shareLocation, sid);
                if (everyOnePermissionAvailableResult != null && everyOnePermissionAvailableResult.startsWith("No files with a matching SID") && !everyOnePermissionAvailableResult.startsWith("SID Found")) {
                    return false;
                }
            }
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception occured while finding Everyone permission available to all folder from the shared location : ", ex);
        }
        return true;
    }
    
    public static HashMap getDesktopBuildVersionDetails() {
        final HashMap<String, String> dcServerVersionDetails = new HashMap<String, String>();
        final String dcAgentVer = SyMUtil.getProductProperty("agentversion");
        dcServerVersionDetails.put("agentversion", dcAgentVer);
        final String dcDSVer = SyMUtil.getProductProperty("distributionserversion");
        dcServerVersionDetails.put("distributionserversion", dcDSVer);
        final String dcMacAgentVer = SyMUtil.getProductProperty("macagentversion");
        dcServerVersionDetails.put("macagentversion", dcMacAgentVer);
        final String dcLinuxAgentVer = SyMUtil.getProductProperty("linuxagentversion");
        dcServerVersionDetails.put("linuxagentversion", dcLinuxAgentVer);
        return dcServerVersionDetails;
    }
    
    public static void setDefalutVlauesToSession(final HttpSession session, final HttpServletRequest request, final String loginUserName) throws Exception {
        final Locale locale = request.getLocale();
        final String serviceName = getServiceName(loginUserName);
        final Long userID = getCurrentlyLoggedInUserID(request);
        final Long accountID = ApiFactoryProvider.getAuthUtilAccessAPI().getAccountID();
        final DCEventLogUtil eventUtil = DCEventLogUtil.getInstance();
        final String remoteAddr = request.getRemoteAddr();
        final String remoteHost = ApiFactoryProvider.getDemoUtilAPI().isDemoMode() ? "xxx.xxx.xxx.xxx" : request.getRemoteHost();
        SYMClientUtil.out.log(Level.INFO, "Remote Address : " + remoteAddr);
        final String licenseType = SyMUtil.getSyMParameter("licenseType");
        final String productType = SyMUtil.getSyMParameter("productType");
        final String licenseVersion = SyMUtil.getSyMParameter("licenseVersion");
        final String swpackage = null;
        SYMClientUtil.out.log(Level.INFO, "Service Name     : " + serviceName);
        SYMClientUtil.out.log(Level.INFO, "Account ID       : " + accountID);
        SYMClientUtil.out.log(Level.INFO, "User ID          : " + userID);
        SYMClientUtil.out.log(Level.INFO, "License Type     : " + licenseType);
        SYMClientUtil.out.log(Level.INFO, "Product Type     : " + productType);
        SYMClientUtil.out.log(Level.INFO, "License Version     : " + licenseVersion);
        final String roleName = DMUserHandler.getRoleForUser(loginUserName);
        session.setAttribute("roleName", (Object)roleName);
        session.setAttribute("USER_ID", (Object)userID);
        session.setAttribute("loginID", (Object)accountID);
        session.setAttribute("loginUserName", (Object)loginUserName);
        session.setAttribute("licenseType", (Object)licenseType);
        session.setAttribute("productType", (Object)productType);
        session.setAttribute("licenseVersion", (Object)licenseVersion);
        final Long customerId = MSPWebClientUtil.getCustomerID(request);
        EventLogThreadLocal.setSourceIpAddress(request.getRemoteAddr());
        EventLogThreadLocal.setSourceHostName(request.getRemoteHost());
        final String i18n = "desktopcentral.webclient.filter.host_connected_msg";
        final Object remarksArgs = remoteHost + "@@@" + loginUserName + "@@@" + roleName;
        eventUtil.addEvent(121, loginUserName, (HashMap)null, i18n, remarksArgs, false, customerId);
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        if (isMSP) {
            final String techRoleScopeMsg = I18N.getMsg("desktopcentral.webclient.filter.DCAuthorizationFilter.TechRoleScopeMessage_MSP", new Object[0]);
            session.setAttribute("TechRoleScopeMessage", (Object)techRoleScopeMsg);
        }
        else {
            final String scope = I18N.getMsg("desktopcentral.webclient.admin.som.name", new Object[0]);
            final String I18Ndefn = I18N.getMsg("desktopcentral.webclient.admin.som.notdefine.technician", new Object[] { scope });
            session.setAttribute("TechRoleScopeMessage", (Object)I18Ndefn);
        }
        final Row userRow = SyMUtil.getUserAccountSettings(userID);
        final DataObject settingsDO = SyMUtil.getPersistence().get("UserSettings", userRow);
        final int maxInterval = 900;
        final Row userSettingsRow = DMUserHandler.setDefaultProperties(userID);
        final Integer timeout = (Integer)userSettingsRow.get("SESSION_EXPIRY_TIME");
        session.setMaxInactiveInterval((int)timeout);
        session.setAttribute("selectedskin", (Object)SyMUtil.getInstance().getTheme());
        session.setAttribute("title", (Object)ProductUrlLoader.getInstance().getValue("title"));
        session.setAttribute("selectcfgview", (Object)"myview");
        updateSOMDisplayName(request);
        DMUserHandler.updateUserSettings(userID, "CONFIGURATION_VIEW", (Object)"myview");
        session.setAttribute("VIEW_CONFIG_SELECTED_DOMAIN", (Object)"All");
        session.setAttribute("VIEW_CONFIG_SELECTED_TYPE", (Object)"All");
        SYMClientUtil.out.log(Level.INFO, loginUserName + " properties are loaded to session object");
        getCopyRihtProps(session);
        final Locale browserLocale = request.getLocale();
        final HashMap quickLinks = getQuickLinks(browserLocale);
        session.setAttribute("quickLinks", (Object)quickLinks);
        if (session.getAttribute("isPuginLogin") != null && session.getAttribute("isPuginLogin").equals("PLUGIN_LOGIN")) {
            session.setAttribute("selectedskin", (Object)getSDPTheme());
            final String appName = (String)session.getAttribute("appName");
            final boolean isEnabled = SolutionUtil.getInstance().getLeftTreeOption(appName, false);
            session.setAttribute("isLeftTreeEnable", (Object)isEnabled);
        }
        else {
            session.setAttribute("isLeftTreeEnable", (Object)true);
        }
        final ServerSessionUtil sessionUtil = new ServerSessionUtil();
        sessionUtil.setDefaultSessionValues(session);
    }
    
    public static boolean isRemote(final String path) {
        try {
            final String regexFileExtensionPattern = "([a-zA-Z]:.*)";
            final Pattern pattern = Pattern.compile(regexFileExtensionPattern);
            final Matcher m = pattern.matcher(path);
            if (path.startsWith("..") || path.startsWith("\\\\" + InetAddress.getLocalHost().getHostName()) || path.startsWith("\\\\" + InetAddress.getLocalHost().getHostAddress()) || m.matches()) {
                return false;
            }
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.SEVERE, "Exception while checking Remote repository for FOS..", ex);
        }
        return true;
    }
    
    public static void updateSOMDisplayName(final HttpServletRequest request) throws Exception {
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        final HttpSession session = request.getSession();
        String i18n = "";
        if (isMSP) {
            i18n = I18N.getMsg("desktopcentral.common.homePageQuickLinks.Customers", new Object[0]);
        }
        else {
            i18n = I18N.getMsg("desktopcentral.webclient.admin.som.name", new Object[0]);
        }
        session.setAttribute("SOM_DISPLAY_STRING", (Object)i18n);
    }
    
    public static int getFirewallAndDCOMSettings(final int portNo, final int httpsPortNo) throws SyMException {
        try {
            final boolean webServerPortRes = WinAccessProvider.getInstance().isFirewallEnabledInDCServer((long)portNo);
            SYMClientUtil.out.log(Level.INFO, "getFirewallAndDCOMSettings webServerPortRes : " + webServerPortRes);
            final boolean httpsPortRes = WinAccessProvider.getInstance().isFirewallEnabledInDCServer((long)httpsPortNo);
            SYMClientUtil.out.log(Level.INFO, "getFirewallAndDCOMSettings httpsPortRes : " + httpsPortRes);
            if (webServerPortRes && httpsPortRes) {
                SYMClientUtil.out.log(Level.INFO, "Firewall PORTs(http and https) are not opened.");
                return 0;
            }
            if (webServerPortRes && !httpsPortRes) {
                SYMClientUtil.out.log(Level.INFO, "Firewall PORTs (http) is not opened and https port is opened.");
                return 1;
            }
            if (!webServerPortRes && httpsPortRes) {
                SYMClientUtil.out.log(Level.INFO, "Firewall PORTs (http) is opened and https port is not opened.");
                return 2;
            }
            SYMClientUtil.out.log(Level.INFO, "Firewall PORTs (http and https) are opened.");
            return -1;
        }
        catch (final SyMException ex) {
            SYMClientUtil.out.log(Level.WARNING, "Caught SyMException : ", (Throwable)ex);
            throw ex;
        }
    }
    
    public static String getSDPTheme() {
        String theme = SyMUtil.getInstance().getTheme();
        try {
            if (CustomerInfoUtil.getInstance().isMSP()) {
                final int sdpBuild = Integer.valueOf(SolutionUtil.getInstance().getSDPBuildNumber());
                if (sdpBuild < 9206) {
                    theme = "sdp-iframe";
                }
            }
            else {
                final int sdpBuild = Integer.valueOf(SolutionUtil.getInstance().getSDPBuildNumber());
                if (sdpBuild < 9220) {
                    theme = "sdp-iframe";
                }
            }
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Caught exception while getting SDP Build Number" + ex);
        }
        return theme;
    }
}
