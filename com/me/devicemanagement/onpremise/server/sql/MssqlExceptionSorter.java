package com.me.devicemanagement.onpremise.server.sql;

import com.me.devicemanagement.onpremise.start.DCStarter;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.devicemanagement.onpremise.server.dbtuning.MssqlTxLogMaintainanceUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.onpremise.start.util.NginxServerUtils;
import java.util.Properties;
import com.adventnet.mfw.Starter;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.util.Locale;
import com.me.devicemanagement.onpremise.start.servertroubleshooter.util.ServerTroubleshooterUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.adventnet.db.adapter.BaseExceptionSorter;

public class MssqlExceptionSorter extends BaseExceptionSorter
{
    private static Logger logger;
    public static final String INVALID_AUTHORIZATION_SPECIFICATION = "28000";
    public static final String COMMUNICATION_LINK_FAILURE1 = "08S01";
    public static final String COMMUNICATION_LINK_FAILURE2 = "08S03";
    public static final String DATA_OR_LOGS_SIZE_ERROR_SQL_STATE = "S1000";
    public static final int DISK_SIZE_ERROR_CODE1 = 1101;
    public static final int DISK_SIZE_ERROR_CODE2 = 1105;
    public static final int TRANSACTION_LOGS_ERROR_CODE = 9002;
    public static final int WINDOW_AUTH_ERROR_CODE = 18452;
    public static final int SQL_AUTH_ERROR_CODE = 18456;
    public static final String TRANSACTIONLOG_FLAG_FILENAME = "transactionlogerror.flag";
    
    public boolean isExceptionFatal(final SQLException e) {
        MssqlExceptionSorter.logger.log(Level.INFO, "Entered MSSQL Sorter");
        try {
            final String serverHome = SyMUtil.getInstallationDir();
            final String sqlState = e.getSQLState();
            final int errorCode = e.getErrorCode();
            MssqlExceptionSorter.logger.log(Level.INFO, "SQLState: " + sqlState);
            MssqlExceptionSorter.logger.log(Level.INFO, "ErrorCode: " + errorCode);
            MssqlExceptionSorter.logger.log(Level.INFO, "Exception : " + e);
            String pageTitle = "";
            String heading = "";
            String unReachableMsg = "";
            String solution = "";
            String solutionDetails = "";
            String solutionHeading = "";
            ServerTroubleshooterUtil.getInstance();
            final String genSolutionHeading = ServerTroubleshooterUtil.getString("troubleshooter_solution_header", (Locale)null);
            if (genSolutionHeading.equals("")) {
                solutionHeading = "";
            }
            else {
                solutionHeading = "Solution";
            }
            final String uploadLogsUrl = WebServerUtil.getUploadLogsUrl();
            final String contactSupportMsg = "If the problem persists, please <a target=\"_blank\" href=\"" + uploadLogsUrl + "\">upload</a> the logs to support for further assistance.";
            final String product = StartupUtil.getProperties(serverHome + File.separator + "conf" + File.separator + "general_properties.conf").getProperty("displayname");
            if (!Starter.checkShutdownListenerPort()) {
                pageTitle = "Unable to start " + product + " Server";
            }
            else {
                pageTitle = product + " Server has Stopped!";
            }
            if (sqlState != null) {
                final Properties wsProps = new Properties();
                if ((sqlState.equals("28000") && errorCode == 18452) || (sqlState.equals("28000") && errorCode == 18456)) {
                    heading = product + " is unable to connect SQL database - Connection to the database is lost!";
                    unReachableMsg = "";
                    solution = "<li>Ensure that the login credentials of SQL database is appropriate to establish connection with the " + product + ".<ol><b>Steps</b><li>Execute the script <b>\"changeDBServer.bat\"</b> available under &lt;Installation_Dir&gt;/bin directory.</li><li>Verify your login credentials.</li><li>Click Test button to test your connectivity.</li><li>Click Save.</li></ol></li>";
                    solutionDetails = "<li>Restart " + product + " Server.</li>";
                    if (WebServerUtil.getWebServerName().equals("nginx")) {
                        NginxServerUtils.generateNginxStandaloneConf(wsProps);
                    }
                    final boolean isStarted = WebServerUtil.startWebServer(serverHome);
                    WebServerUtil.generateHtmlRedirectionFile(pageTitle, heading, unReachableMsg, solution, solutionDetails, solutionHeading, contactSupportMsg);
                    this.openMaintenancePage(isStarted);
                }
                else if ((sqlState.equals("S1000") && errorCode == 1101) || (sqlState.equals("S1000") && errorCode == 1105)) {
                    heading = product + " server is unable to access SQL database, due to one of the following reasons ";
                    unReachableMsg = "<li>Insufficient disk space.</li><li>Insufficient space in transaction logs.</li>";
                    solution = "<li> Ensure that, sufficient disk space is available to store SQL data and log files.</li><li>Ensure that, sufficient space is available to store the transaction logs. <a href=\"" + ProductUrlLoader.getInstance().getValue("customize_transaction_log_size") + "\" target=\"_blank\" >Learn More</a></li>";
                    solutionDetails = "<li>Restart " + product + " Server.</li>";
                    if (WebServerUtil.getWebServerName().equals("nginx")) {
                        NginxServerUtils.generateNginxStandaloneConf(wsProps);
                    }
                    final boolean isStarted = WebServerUtil.startWebServer(serverHome);
                    WebServerUtil.generateHtmlRedirectionFile(pageTitle, heading, unReachableMsg, solution, solutionDetails, solutionHeading, contactSupportMsg);
                    this.openMaintenancePage(isStarted);
                    this.createTransactionLogErrorFlagFile();
                }
                else if (sqlState.equals("S1000") && errorCode == 9002) {
                    if (MssqlTxLogMaintainanceUtil.hasAllPermissions()) {
                        MssqlExceptionSorter.logger.info("All permissions present.");
                        if (!MssqlTxLogMaintainanceUtil.isRecoveryModeSimple()) {
                            MssqlExceptionSorter.logger.info("Recovery mode is not simple.");
                            heading = product + " server is unable to access MSSQL database since transaction log is full. <a href=\"" + ProductUrlLoader.getInstance().getValue("customize_transaction_log_size") + "#trans\" target=\"_blank\" >Learn More</a>";
                            unReachableMsg = "";
                            solution = "<li> Ensure that in MSSQL database properties, the " + SYMClientUtil.getDataBaseName() + " database recovery model is set as \"Simple\" to prevent transaction log growth.</li>";
                            solutionDetails = "<li>Restart " + product + " Server.</li>";
                        }
                        else {
                            MssqlExceptionSorter.logger.info("Recovery mode is simple.");
                            heading = product + " server is unable to access MSSQL database as the transaction log is full, due to one of the following reasons";
                            unReachableMsg = "<li>Insufficient disk space.</li><li>Insufficient space in transaction logs.</li>";
                            solution = "<li>Ensure that, sufficient space is available to store the transaction logs. <a href=\"" + ProductUrlLoader.getInstance().getValue("customize_transaction_log_size") + "#shrink\" target=\"_blank\" >Learn More</a></li>";
                            solutionDetails = "<li>Restart " + product + " Server.</li>";
                        }
                    }
                    else {
                        MssqlExceptionSorter.logger.info("no permission.");
                        heading = product + " server is unable to access MSSQL database since transaction log is full.";
                        unReachableMsg = "<li>" + product + " server is unable to view MSSQL's system tables, ensure that the server has sufficient privileges to view the system tables to automatically monitor transaction logs.</li>" + "<li>Grant permission for " + DBUtil.getDBServerProperties().get("db.user.name") + " user to view system tables\"</li>";
                        solution = "<li>Shrink the size of the " + product + " Transaction logs. <a href=\"" + ProductUrlLoader.getInstance().getValue("customize_transaction_log_size") + "#shrink\" target=\"_blank\" >Learn More</a></li>";
                        solutionDetails = "<li>Restart " + product + " Server.</li>";
                    }
                    if (WebServerUtil.getWebServerName().equals("nginx")) {
                        NginxServerUtils.generateNginxStandaloneConf(wsProps);
                    }
                    final boolean isStarted = WebServerUtil.startWebServer(serverHome);
                    WebServerUtil.generateHtmlRedirectionFile(pageTitle, heading, unReachableMsg, solution, solutionDetails, solutionHeading, contactSupportMsg);
                    this.openMaintenancePage(isStarted);
                    this.createTransactionLogErrorFlagFile();
                }
            }
            if (!super.isDBAlive(e)) {
                MssqlExceptionSorter.logger.log(Level.SEVERE, "Going to Halt the JVM");
                MssqlExceptionSorter.logger.log(Level.INFO, "Stop Process");
                RelationalAPI.getInstance().logAndHalt(Integer.parseInt(System.getProperty("check.dbcrash.delay", "10")));
                return true;
            }
        }
        catch (final Exception e2) {
            MssqlExceptionSorter.logger.log(Level.SEVERE, "Returning [{0}] from isDBAlive method");
        }
        return false;
    }
    
    private void openMaintenancePage(final boolean isStarted) {
        try {
            final String serverHome = SyMUtil.getInstallationDir();
            String pageUrlToOpen = "";
            if (!isStarted) {
                pageUrlToOpen = serverHome + File.separator + WebServerUtil.MAINTENANCE_MODE_FILE;
            }
            else {
                pageUrlToOpen = WebServerUtil.getServerProtocol() + "://" + WebServerUtil.getMachineName() + ":" + WebServerUtil.getAvailablePort();
            }
            if (DCStarter.getStartupStatusNotifier() != null && DCStarter.getStartupStatusNotifier().isStatusNotifierRunning()) {
                DCStarter.getStartupStatusNotifier().removeStatusNotifier(pageUrlToOpen);
            }
            else {
                WebServerUtil.openBrowserUsingDCWinutil(pageUrlToOpen);
            }
        }
        catch (final Exception e) {
            MssqlExceptionSorter.logger.info("Exception while getting server location" + e);
        }
    }
    
    private void createTransactionLogErrorFlagFile() {
        final String transactionLogErrorFlagFilePath = System.getProperty("server.home") + File.separator + "bin" + File.separator + "transactionlogerror.flag";
        try {
            final File transactionLogErrorFlagFile = new File(transactionLogErrorFlagFilePath);
            transactionLogErrorFlagFile.createNewFile();
        }
        catch (final Exception ex) {
            MssqlExceptionSorter.logger.log(Level.WARNING, "Caught exception while creating transaction log error flag file :", ex);
        }
    }
    
    static {
        MssqlExceptionSorter.logger = Logger.getLogger(MssqlExceptionSorter.class.getName());
    }
}
