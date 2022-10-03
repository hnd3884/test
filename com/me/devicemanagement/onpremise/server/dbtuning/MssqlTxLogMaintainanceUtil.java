package com.me.devicemanagement.onpremise.server.dbtuning;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MssqlTxLogMaintainanceUtil
{
    static Logger logger;
    private static MssqlTxLogMaintainanceUtil handler;
    
    public static MssqlTxLogMaintainanceUtil getInstance() {
        if (MssqlTxLogMaintainanceUtil.handler == null) {
            try {
                MssqlTxLogMaintainanceUtil.handler = new MssqlTxLogMaintainanceUtil();
            }
            catch (final Exception e) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Exception while creating commonupdatesutil obj", e);
            }
        }
        return MssqlTxLogMaintainanceUtil.handler;
    }
    
    public static boolean hasPermissionsToReadSysDbFiles() {
        ResultSet rs = null;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            statement = conn.createStatement();
            rs = RelationalAPI.getInstance().executeQueryForSQL("SELECT permission_name FROM fn_my_permissions('sys.database_files', 'OBJECT') ORDER BY subentity_name, permission_name ;", (Map)null, statement);
            while (rs.next()) {
                if (rs.getString("permission_name").equalsIgnoreCase("SELECT")) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in hasPermissionsToReadSysDbFiles ", e);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in finally block  ", e);
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e2) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in finally block  ", e2);
            }
        }
        return false;
    }
    
    public static boolean hasPermissionsToReadSysdbs() {
        ResultSet rs = null;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            statement = conn.createStatement();
            rs = RelationalAPI.getInstance().executeQueryForSQL("SELECT permission_name FROM fn_my_permissions('sys.databases', 'OBJECT') ORDER BY subentity_name, permission_name ;", (Map)null, statement);
            while (rs.next()) {
                if (rs.getString("permission_name").equalsIgnoreCase("SELECT")) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in hasPermissionsToReadSysdbs ", e);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in finally block  ", e);
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e2) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in finally block  ", e2);
            }
        }
        return false;
    }
    
    public static boolean hasAllPermissions() {
        ResultSet rs = null;
        Connection conn = null;
        final Statement statement = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            rs = RelationalAPI.getInstance().executeQueryForSQL("SELECT permission_name FROM fn_my_permissions('sys.dm_os_performance_counters', 'OBJECT') where permission_name='SELECT' ;", (Map)null, statement);
            if (!rs.isBeforeFirst()) {
                return false;
            }
            rs = RelationalAPI.getInstance().executeQueryForSQL("SELECT permission_name FROM fn_my_permissions('sys.dm_tran_session_transactions', 'OBJECT') where permission_name='SELECT' ;", (Map)null, statement);
            if (!rs.isBeforeFirst()) {
                return false;
            }
            rs = RelationalAPI.getInstance().executeQueryForSQL("SELECT permission_name FROM fn_my_permissions('sys.dm_exec_connections', 'OBJECT') where permission_name='SELECT' ;", (Map)null, statement);
            if (!rs.isBeforeFirst()) {
                return false;
            }
            rs = RelationalAPI.getInstance().executeQueryForSQL("SELECT permission_name FROM fn_my_permissions('sys.dm_exec_sql_text', 'OBJECT') where permission_name='SELECT' ;", (Map)null, statement);
            if (!rs.isBeforeFirst()) {
                return false;
            }
            rs = RelationalAPI.getInstance().executeQueryForSQL("SELECT permission_name FROM fn_my_permissions('sys.databases', 'OBJECT') where permission_name='SELECT' ;", (Map)null, statement);
            if (!rs.isBeforeFirst()) {
                return false;
            }
            rs = RelationalAPI.getInstance().executeQueryForSQL("SELECT permission_name FROM fn_my_permissions('sys.dm_tran_database_transactions', 'OBJECT') where permission_name='SELECT' ;", (Map)null, statement);
            if (!rs.isBeforeFirst()) {
                return false;
            }
        }
        catch (final Exception e) {
            MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in hasNecessaryPermissions ", e);
            return false;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e2) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in finally block  ", e2);
            }
        }
        return true;
    }
    
    public static int txLogSizeToBeSet() {
        ResultSet rs = null;
        Connection conn = null;
        Statement statement = null;
        final String dbname = SYMClientUtil.getDataBaseName();
        int dbsize = 0;
        int txLogSizeInMB = 5120;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            statement = conn.createStatement();
            rs = RelationalAPI.getInstance().executeQueryForSQL("select name, size from sys.master_files where name = '" + dbname + "';", (Map)null, statement);
            while (rs.next()) {
                dbsize = rs.getInt("size");
            }
            dbsize /= 128;
            if (dbsize < 5120) {
                txLogSizeInMB = 5120;
            }
            else if (dbsize > 15360) {
                txLogSizeInMB = 15360;
            }
            else {
                txLogSizeInMB = dbsize;
            }
        }
        catch (final Exception e) {
            MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in txLogSizeToBeSet ", e);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in finally block  ", e);
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e2) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in finally block  ", e2);
            }
        }
        return txLogSizeInMB;
    }
    
    public static boolean isRecoveryModeSimple() {
        ResultSet rs = null;
        Connection conn = null;
        Statement statement = null;
        final String dbname = SYMClientUtil.getDataBaseName();
        try {
            conn = RelationalAPI.getInstance().getConnection();
            statement = conn.createStatement();
            rs = RelationalAPI.getInstance().executeQueryForSQL("SELECT name, recovery_model_desc FROM sys.databases WHERE name = '" + dbname + "' ;", (Map)null, statement);
            if (rs.next()) {
                return rs.getString("recovery_model_desc").equalsIgnoreCase("SIMPLE");
            }
        }
        catch (final Exception e) {
            MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in isRecoveryModeSimple ", e);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in finally block  ", e);
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e2) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.SEVERE, "Error in finally block  ", e2);
            }
        }
        return false;
    }
    
    private static String getEMailAddress() throws Exception {
        String strEMailAddr = SyMUtil.getEMailAddress("DBBackup");
        if (strEMailAddr == null) {
            strEMailAddr = SyMUtil.getEMailAddress("ServerStartupFailure");
        }
        return strEMailAddr;
    }
    
    public static void sendRecoveryModeReminderEmail(final String reason) {
        try {
            final String strToAddress = getEMailAddress();
            if (strToAddress == null) {
                MssqlTxLogMaintainanceUtil.logger.log(Level.WARNING, "E-Mail address is null.  Cant Proceed!!!");
                return;
            }
            final Hashtable<String, String> mailSenderDetails = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails();
            final String frAdd = mailSenderDetails.get("mail.fromAddress");
            final String subject = StartupUtil.getProperties(SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "general_properties.conf").getProperty("displayname") + " " + I18N.getMsg("dm.common.txlog.mail.subject", new Object[0]);
            final MailDetails maildetails = new MailDetails(frAdd, strToAddress);
            maildetails.senderDisplayName = mailSenderDetails.get("mail.fromName");
            final String mailContent = getMailContent(reason);
            maildetails.bodyContent = mailContent;
            maildetails.ccAddress = null;
            maildetails.subject = subject;
            maildetails.attachment = null;
            ApiFactoryProvider.getMailSettingAPI().addToMailQueue(maildetails, 0);
        }
        catch (final Exception ex) {
            MssqlTxLogMaintainanceUtil.logger.log(Level.WARNING, "Exception while sending alert mail", ex);
        }
    }
    
    private static String getMailContent(final String reason) throws Exception {
        final String serverURL = ApiFactoryProvider.getUtilAccessAPI().getServerURL();
        final String bodyContent = I18N.getMsg(reason, new Object[] { ProductUrlLoader.getInstance().getValue("displayname"), SYMClientUtil.getDataBaseName(), ProductUrlLoader.getInstance().getValue("customize_transaction_log_size") });
        final String footerTag = "<br><br><p style='color:#888888;'>" + I18N.getMsg("dc.admin.backup_failed.mail.footer", new Object[] { serverURL, ProductUrlLoader.getInstance().getValue("displayname") }) + " </p>";
        StringBuilder mailContent = new StringBuilder();
        mailContent = mailContent.append(bodyContent).append(footerTag);
        return mailContent.toString();
    }
    
    public static void mssqlTxLogTask() {
        MssqlTxLogMaintainanceUtil.logger.info("Entering mssqlTxLogTask");
        if (!hasPermissionsToReadSysDbFiles() || !hasPermissionsToReadSysdbs()) {
            if (SyMUtil.getServerParameter("mssqlTxMailSent") != null) {
                MssqlTxLogMaintainanceUtil.logger.info("message already sent once");
            }
            else {
                MessageProvider.getInstance().unhideMessage("MSSQL_TRANSACTION_LOG_NO_PERMISSION");
                SyMUtil.updateServerParameter("mssqlTxMailSent", "true");
                sendRecoveryModeReminderEmail("dm.common.txlog.mail.nopermission.body");
            }
        }
        else {
            MessageProvider.getInstance().hideMessage("MSSQL_TRANSACTION_LOG_NO_PERMISSION");
        }
    }
    
    static {
        MssqlTxLogMaintainanceUtil.logger = Logger.getLogger("DatabaseMaintenanceLogger");
        MssqlTxLogMaintainanceUtil.handler = null;
    }
}
