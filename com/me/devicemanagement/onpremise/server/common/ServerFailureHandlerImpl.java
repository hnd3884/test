package com.me.devicemanagement.onpremise.server.common;

import java.sql.Connection;
import java.util.Properties;
import java.util.List;
import java.util.HashMap;
import java.sql.DriverManager;
import com.adventnet.persistence.PersistenceUtil;
import java.sql.SQLException;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import com.me.devicemanagement.onpremise.start.servertroubleshooter.util.ServerTroubleshooterUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.onpremise.start.util.DCLogUtil;
import java.util.logging.Level;
import com.adventnet.mfw.ServerFailureException;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.util.logging.Logger;
import com.adventnet.mfw.ServerFailureHandler;

public class ServerFailureHandlerImpl implements ServerFailureHandler
{
    private static final Logger OUT;
    public static final int DB_ERROR_STATUS = 10001;
    public static final String INVALID_AUTHORIZATION_SPECIFICATION = "28000";
    public static final String COMMUNICATION_LINK_FAILURE1 = "08S01";
    public static final String COMMUNICATION_LINK_FAILURE2 = "08S03";
    public static final String COMMUNICATION_LINK_FAILURE3 = "08001";
    public static final String COMMUNICATION_LINK_FAILURE4 = "08006";
    public static final String DATA_OR_LOGS_SIZE_ERROR_SQL_STATE = "S1000";
    public static final int DISK_SIZE_ERROR_CODE1 = 1101;
    public static final int DISK_SIZE_ERROR_CODE2 = 1105;
    public static final int TRANSACTION_LOGS_ERROR_CODE = 9002;
    public static final int WINDOW_AUTH_ERROR_CODE = 18452;
    public static final int SQL_AUTH_ERROR_CODE = 18456;
    String pageTitle;
    String heading;
    String unReachableMsg;
    String solution;
    String solutionDetails;
    String solutionHeading;
    String productUrl;
    String uploadLogsUrl;
    String contactSupportMsg;
    
    public ServerFailureHandlerImpl() {
        this.pageTitle = "";
        this.heading = "";
        this.unReachableMsg = "";
        this.solution = "";
        this.solutionDetails = "";
        this.solutionHeading = "Solution";
        this.productUrl = WebServerUtil.getProductUrl();
        this.uploadLogsUrl = this.productUrl + "/logs-how-to.html";
        this.contactSupportMsg = "If the problem persists, please <a target=\"_blank\" href=\"" + this.uploadLogsUrl + "\">upload</a> the logs to support for further assistance.";
    }
    
    public void handle(final ServerFailureException exception) {
        try {
            if (exception.getErrorCode() == 10001) {
                this.handleDBException(exception);
            }
        }
        catch (final Exception e) {
            ServerFailureHandlerImpl.OUT.log(Level.INFO, "Exception while getting server home" + e);
        }
    }
    
    public void handleDBException(final ServerFailureException exception) {
        try {
            if (System.getProperty("uniformlogformatter.enable", "false").equalsIgnoreCase("true")) {
                DCLogUtil.getOneLineLoggerInstance().log(Level.WARNING, "DCServer Startup Failure:" + exception.getMessage());
            }
            final String dbName = DBUtil.getActiveDBName();
            final boolean isremotePG = DBUtil.isRemoteDB();
            final String serverHome = SyMUtil.getInstallationDir();
            ServerFailureHandlerImpl.OUT.log(Level.INFO, "is remote database : " + isremotePG);
            if ((dbName.equals("postgres") || dbName.equals("pgsql")) && !isremotePG) {
                ServerFailureHandlerImpl.OUT.log(Level.INFO, "Inside the ServerFailure HandlerImpl class");
                ServerFailureHandlerImpl.OUT.log(Level.INFO, "ErrorCode: " + exception.getErrorCode());
                ServerFailureHandlerImpl.OUT.log(Level.INFO, "ErrorMsg: " + exception.getMessage());
                if (exception.getErrorCode() == 10001) {
                    final HashMap dbProps = ServerTroubleshooterUtil.getDBPropertiesFromFile();
                    final String dbPort = dbProps.get("PORT").toString();
                    boolean isDBPortInUse = ServerTroubleshooterUtil.isPortEngaged(Integer.parseInt(dbPort));
                    if (isDBPortInUse) {
                        final String exeKillStatusFile = System.getProperty("server.home") + File.separator + "logs" + File.separator + "exekill_status.props";
                        final String pgsqlBinDir = new File(System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "bin" + File.separator + "postgres.exe").getCanonicalPath();
                        ServerTroubleshooterUtil.killExeIfRunning("postgres.exe", pgsqlBinDir, exeKillStatusFile);
                        Thread.sleep(20000L);
                        isDBPortInUse = ServerTroubleshooterUtil.isPortEngaged(Integer.parseInt(dbPort));
                        if (isDBPortInUse && System.getProperty("ignore.db.auto.port.detect.handling") != null && System.getProperty("ignore.db.auto.port.detect.handling").equalsIgnoreCase("false")) {
                            final int availableDBPort = ServerTroubleshooterUtil.getNextFreePort(dbPort);
                            if (availableDBPort != 0) {
                                final boolean status = ServerTroubleshooterUtil.isDBPortChanged(dbPort, Integer.toString(availableDBPort));
                                if (status) {
                                    if (!new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "port_in_use_restart.lock").exists()) {
                                        ServerFailureHandlerImpl.OUT.log(Level.INFO, "port_in_use_restart.lock created status : " + new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "port_in_use_restart.lock").createNewFile());
                                    }
                                    ServerFailureHandlerImpl.OUT.log(Level.INFO, "**************************************************");
                                    ServerFailureHandlerImpl.OUT.log(Level.INFO, "Going to Restart the server...");
                                    ServerFailureHandlerImpl.OUT.log(Level.INFO, "**************************************************");
                                    com.me.devicemanagement.onpremise.server.util.SyMUtil.triggerServerRestart("DB Port occupied hence DB port changed and server requires restart.");
                                }
                                else {
                                    ServerTroubleshooterUtil.changeDefaultPortIntoDatabaseParams();
                                }
                            }
                            else {
                                ServerTroubleshooterUtil.changeDefaultPortIntoDatabaseParams();
                            }
                        }
                        else {
                            ServerFailureHandlerImpl.OUT.log(Level.INFO, "**************************************************");
                            ServerFailureHandlerImpl.OUT.log(Level.INFO, "Going to Restart the server...");
                            ServerFailureHandlerImpl.OUT.log(Level.INFO, "**************************************************");
                            if (isDBPortInUse) {
                                com.me.devicemanagement.onpremise.server.util.SyMUtil.triggerServerRestart("DB port occupied and db port cannot be changed since db port auto detection handling not configured.");
                            }
                            else {
                                com.me.devicemanagement.onpremise.server.util.SyMUtil.triggerServerRestart("DB port is free now and can start the server.");
                            }
                        }
                    }
                    else if (ServerTroubleshooterUtil.isServiceRunninginLocalSystemAccount() == Boolean.FALSE) {
                        String wrapperConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "custom_wrapperservice.conf";
                        if (!new File(wrapperConfFile).exists()) {
                            wrapperConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "wrapper.conf";
                        }
                        final String serviceName = ServerTroubleshooterUtil.getPropertyValueFromFile(wrapperConfFile, "wrapper.name");
                        if (serviceName != null || serviceName.trim().length() > 1) {
                            final String serviceStatusFile = System.getProperty("server.home") + File.separator + "logs" + File.separator + "servicechange_status.props";
                            ServerTroubleshooterUtil.changeUserAccountToSystemAccount(serviceName, serviceStatusFile);
                        }
                    }
                    else if (ServerTroubleshooterUtil.isPgCheckpointNotLocated() == Boolean.TRUE) {
                        ServerTroubleshooterUtil.getInstance().serverStartupFailure("pgsql_fails_in_recovery_mode");
                    }
                    else {
                        ServerFailureHandlerImpl.OUT.log(Level.INFO, " Unable to Find root cause of PGSQL DB Startup Failure Issue..");
                        boolean corrupted = false;
                        final List hbaRecs = ServerTroubleshooterUtil.getHbaRecords(new File(System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "data" + File.separator + "pg_hba.conf"));
                        if (!hbaRecs.isEmpty()) {
                            corrupted = true;
                        }
                        final String fileCorruption = System.getProperty("server.home") + File.separator + "logs" + File.separator + "fileCorruption.props";
                        final Properties props = StartupUtil.getProperties(fileCorruption);
                        props.setProperty("PgHbaConfCorrupted", String.valueOf(corrupted));
                        StartupUtil.storeProperties(props, fileCorruption);
                        if (new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "port_in_use_restart.lock").exists()) {
                            ServerFailureHandlerImpl.OUT.log(Level.INFO, "Port is use Restart Lock File Delete status : " + new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "port_in_use_restart.lock").delete());
                        }
                        ServerTroubleshooterUtil.getInstance().setStartFailureException((Exception)exception);
                        ServerTroubleshooterUtil.getInstance().serverStartupFailure("pgsql_startup_failure_unknown_case");
                    }
                }
            }
            else if ((dbName.equals("postgres") || dbName.equals("pgsql")) && isremotePG) {
                ServerFailureHandlerImpl.OUT.log(Level.INFO, "Inside is remote db");
                ServerFailureHandlerImpl.OUT.log(Level.INFO, "ErrorCode: " + exception.getErrorCode());
                ServerFailureHandlerImpl.OUT.log(Level.INFO, "ErrorMsg: " + exception.getMessage());
                ServerFailureHandlerImpl.OUT.log(Level.INFO, "Error trace: " + exception);
                for (Throwable cause = (Throwable)exception; cause != null; cause = cause.getCause()) {
                    final String sqlCause = cause.toString();
                    final String sqlMsg = cause.getMessage();
                    String sqlState = "";
                    int errorCode = 0;
                    ServerFailureHandlerImpl.OUT.log(Level.INFO, "sqlCause : " + sqlCause);
                    if (cause instanceof SQLException) {
                        sqlState = ((SQLException)cause).getSQLState();
                        errorCode = ((SQLException)cause).getErrorCode();
                        ServerFailureHandlerImpl.OUT.log(Level.INFO, "sqlCause : " + sqlCause);
                        ServerFailureHandlerImpl.OUT.log(Level.INFO, "sql state and errorcode : " + sqlState + " : " + errorCode);
                        if (sqlState.equals("28000")) {
                            ServerTroubleshooterUtil.getInstance().serverStartupFailure("mssql_invalid_login_credentials");
                        }
                        else if (sqlState.equals("08001")) {
                            final String connectionLostCause = cause.toString();
                            if (connectionLostCause.contains("Connection refused: connect")) {
                                ServerTroubleshooterUtil.getInstance().serverStartupFailure("mssql_connection_lost");
                            }
                        }
                    }
                }
            }
            if (dbName.equalsIgnoreCase("mssql")) {
                for (Throwable cause = (Throwable)exception; cause != null; cause = cause.getCause()) {
                    final String sqlCause = cause.toString();
                    final String sqlMsg = cause.getMessage();
                    String sqlState = "";
                    int errorCode = 0;
                    if (sqlCause.contains("java.sql.SQLException") && cause instanceof SQLException) {
                        sqlState = ((SQLException)cause).getSQLState();
                        errorCode = ((SQLException)cause).getErrorCode();
                        ServerFailureHandlerImpl.OUT.log(Level.INFO, "SQLState: " + sqlState);
                        ServerFailureHandlerImpl.OUT.log(Level.INFO, "ErrorCode: " + errorCode);
                        if (sqlState != null) {
                            if ((sqlState.equals("28000") && errorCode == 18452) || (sqlState.equals("28000") && errorCode == 18456)) {
                                ServerTroubleshooterUtil.getInstance().serverStartupFailure("mssql_invalid_login_credentials");
                            }
                            else if ((sqlState.equals("08S01") && errorCode == 0) || (sqlState.equals("08S03") && errorCode == 0)) {
                                final String connectionLostCause = cause.toString();
                                if (connectionLostCause.contains("Unknown server host name") || connectionLostCause.contains("Network error IOException: Connection refused: connect") || connectionLostCause.contains("Network error IOException: Connection timed out: connect") || connectionLostCause.contains("Connection reset by peer: socket write error")) {
                                    ServerTroubleshooterUtil.getInstance().serverStartupFailure("mssql_connection_lost");
                                }
                            }
                            else if (sqlState.equals("08001") || sqlState.equals("08006")) {
                                ServerTroubleshooterUtil.getInstance().serverStartupFailure("mssql_connection_lost");
                            }
                            else if ((sqlState.equals("S1000") && errorCode == 1101) || (sqlState.equals("S1000") && errorCode == 1105) || (sqlState.equals("S1000") && errorCode == 9002)) {
                                ServerTroubleshooterUtil.getInstance().serverStartupFailure("mssql_transaction_log_size_exceed");
                            }
                            else {
                                final HashMap dbProps2 = ServerTroubleshooterUtil.getDBPropertiesFromFile();
                                final String userName = dbProps2.get("USER").toString();
                                String password = dbProps2.get("PASSWORD").toString();
                                if (password != null && password.trim().length() > 0) {
                                    password = PersistenceUtil.getDBPasswordProvider().getPassword((Object)password);
                                }
                                final String dbUrl = dbProps2.get("dbURL").toString();
                                Connection conn = null;
                                try {
                                    conn = DriverManager.getConnection(dbUrl, userName, password);
                                }
                                catch (final Exception e) {
                                    ServerFailureHandlerImpl.OUT.log(Level.WARNING, "Unable to connect SQL Server.Connection lost!!");
                                    try {
                                        if (conn != null) {
                                            conn.close();
                                        }
                                    }
                                    catch (final Exception exp) {
                                        ServerFailureHandlerImpl.OUT.log(Level.FINE, "Unable to close connection. ", exp);
                                    }
                                }
                                finally {
                                    try {
                                        if (conn != null) {
                                            conn.close();
                                        }
                                    }
                                    catch (final Exception exp2) {
                                        ServerFailureHandlerImpl.OUT.log(Level.FINE, "Unable to close connection. ", exp2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e2) {
            ServerFailureHandlerImpl.OUT.log(Level.INFO, "Exception while handling exception", e2);
        }
    }
    
    static {
        OUT = Logger.getLogger(ServerFailureHandlerImpl.class.getName());
    }
}
