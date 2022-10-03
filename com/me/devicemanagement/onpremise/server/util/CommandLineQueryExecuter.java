package com.me.devicemanagement.onpremise.server.util;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.DeleteQuery;
import java.util.Iterator;
import java.sql.Statement;
import java.sql.Connection;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.deletionfw.DeletionFramework;
import org.json.JSONObject;
import com.adventnet.db.api.RelationalAPI;
import java.util.LinkedHashMap;
import com.adventnet.mfw.ConsoleOut;
import org.xml.sax.SAXParseException;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DCNativeSQLHandler;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.util.DCLogUtil;
import java.util.Properties;
import java.io.File;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.eventlog.EventLogThreadLocal;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import com.me.devicemanagement.onpremise.start.util.CheckServerStatus;
import java.util.logging.Logger;
import com.adventnet.persistence.StandAlonePersistence;

public class CommandLineQueryExecuter extends StandAlonePersistence
{
    public static Logger logger;
    
    public CommandLineQueryExecuter() {
        initDBMigrationLog();
        showMsgInConsoleAndLog("\nInitializing DB Connection. Please wait...");
        if (!CheckServerStatus.getInstance().isServerRunning()) {
            new BackupRestoreUtil();
            BackupRestoreUtil.executeInitPgsql(System.getProperty("server.home"));
        }
        BackupRestoreUtil.setDBHome();
        showMsgInConsoleAndLog("\nInitializing DB Connection. Please wait...");
    }
    
    public void startServer() throws Exception {
        try {
            super.startServer();
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private static Row getEventLogRow(final int eventID, final String userName, final String remarks) {
        final long currentTime = System.currentTimeMillis();
        final Row eventLogRow = new Row("EventLog");
        eventLogRow.set("EVENT_ID", (Object)new Integer(eventID));
        eventLogRow.set("LOGON_USER_NAME", (Object)userName);
        eventLogRow.set("EVENT_TIMESTAMP", (Object)new Long(currentTime));
        eventLogRow.set("EVENT_REMARKS", (Object)remarks);
        eventLogRow.set("EVENT_SOURCE_IP", (Object)EventLogThreadLocal.getSourceIpAddress());
        eventLogRow.set("EVENT_SOURCE_HOSTNAME", (Object)DCEventLogUtil.getHostNameForEventSource());
        return eventLogRow;
    }
    
    private static void initDBMigrationLog() {
        try {
            final String homeDir = System.getProperty("server.home");
            final String logFilePath = homeDir + File.separator + "logs" + File.separator + "cmdlinequeryexecuterlog_%g.txt";
            final Properties properties = new Properties();
            properties.setProperty("java.util.logging.FileHandler.pattern", logFilePath.replace("\\", "/"));
            properties.setProperty("java.util.logging.FileHandler.limit", "5000000");
            properties.setProperty("java.util.logging.FileHandler.count", "2");
            DCLogUtil.initLogger(properties);
            CommandLineQueryExecuter.logger.log(Level.INFO, "###########################################################");
        }
        catch (final Exception ex) {
            CommandLineQueryExecuter.logger.log(Level.WARNING, "Custom_Query_Executer_log.txt File not Found", ex);
        }
    }
    
    private static void parseCMDXML(final String[] args) throws Exception {
        try {
            if (args.length == 1 && new File(args[0]).isFile()) {
                showMsgInConsoleAndLog("\nXML File Complete Path " + args[0]);
                final File xmlFile = new File(args[0]);
                final DCNativeSQLHandler objDCNativeSQLHandler = new DCNativeSQLHandler();
                final LinkedHashMap queryId_SQL_Map = objDCNativeSQLHandler.parse(xmlFile.toURL());
                final String stopPro = String.valueOf(queryId_SQL_Map.get("stopPro"));
                CommandLineQueryExecuter.logger.log(Level.INFO, "\nStop Product value " + stopPro);
                final Boolean isServerRunning = CheckServerStatus.getInstance().isServerRunning();
                CommandLineQueryExecuter.logger.log(Level.INFO, "\nServer runing status " + isServerRunning);
                if (stopPro.equalsIgnoreCase("true") && isServerRunning) {
                    showMsgInConsoleAndLog("\n****************************************************");
                    showMsgInConsoleAndLog("SEVERE : Server is running...Please stop the server");
                    showMsgInConsoleAndLog("****************************************************");
                }
                else {
                    executeSQL(queryId_SQL_Map);
                }
            }
            else {
                showMsgInConsoleAndLog("\nPlease Enter valid XML Complete Path.If the directory names contain spaces, specify the path within double-quotes.");
            }
        }
        catch (final Exception e) {
            if (e instanceof SAXParseException) {
                ConsoleOut.println("\nPlease give valid XML File as input.");
                CommandLineQueryExecuter.logger.log(Level.WARNING, "Please give valid XML File as input ", e);
            }
            else {
                ConsoleOut.println("Unable to Execute the Query : " + e.getMessage());
                CommandLineQueryExecuter.logger.log(Level.WARNING, "Unable to Execute the Query :", e);
            }
        }
    }
    
    private static void executeSQL(final LinkedHashMap queryId_SQL_Map) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            stmt = conn.createStatement();
            final Iterator sqlIDKeySet = queryId_SQL_Map.keySet().iterator();
            while (sqlIDKeySet.hasNext()) {
                final String sqlMap = String.valueOf(sqlIDKeySet.next());
                if (!sqlMap.equalsIgnoreCase("stopPro")) {
                    final LinkedHashMap detailsMap = queryId_SQL_Map.get(sqlMap);
                    final String sqlID = String.valueOf(detailsMap.get("sql_id"));
                    final String sql = String.valueOf(detailsMap.get("sql_command"));
                    final String remarks = String.valueOf(detailsMap.get("sql_remarks"));
                    final String sql_remarks = remarks.equalsIgnoreCase("null") ? "Remarks not given ." : remarks;
                    final String isDependentDeletion = String.valueOf(detailsMap.get("sql_is_dependent_deletion"));
                    CommandLineQueryExecuter.logger.log(Level.INFO, "Going to execute query -- {0}", sql);
                    if (isDependentDeletion.trim().equalsIgnoreCase("true")) {
                        final String sqlJSON = String.valueOf(detailsMap.get("sql_json"));
                        CommandLineQueryExecuter.logger.log(Level.INFO, () -> "Executing the dependent deletion data with sql_json : " + s);
                        if (sqlJSON.equalsIgnoreCase("null") || sqlJSON.isEmpty()) {
                            CommandLineQueryExecuter.logger.log(Level.SEVERE, "JSON is not present for executing deletion");
                            showMsgInConsoleAndLog("\nFailed :JSON is not present for executing deletion. Please contact DesktopCentral Support.");
                            return;
                        }
                        try {
                            final DeleteQuery deleteQuery = DeletionFramework.jsonToDeleteQuery(new JSONObject(sqlJSON));
                            if (!DeletionFramework.isParentDependencyRemovedTables(deleteQuery.getTableName())) {
                                CommandLineQueryExecuter.logger.log(Level.SEVERE, () -> "The table [" + deleteQuery2.getTableName() + "] should not be deleted using Dependent deletion");
                                showMsgInConsoleAndLog("\nFailed : Wrong usage. Please contact DesktopCentral Support");
                                return;
                            }
                            DeletionFramework.doDependentDataDeletion(deleteQuery.getTableName(), deleteQuery.getCriteria());
                        }
                        catch (final Exception ex) {
                            CommandLineQueryExecuter.logger.log(Level.SEVERE, () -> "Exception while parsing and executing the sql_json : " + s2);
                            showMsgInConsoleAndLog("\nFailed : Exception while parsing and executing the sql_json. Please contact DesktopCentral Support.");
                            throw ex;
                        }
                    }
                    else {
                        try {
                            if (DeletionFramework.isDeleteQueryContainParentDependencyRemovedTables(sql)) {
                                CommandLineQueryExecuter.logger.log(Level.SEVERE, "Should not delete data in this table through query. Should use DependentDeletion Methods");
                                showMsgInConsoleAndLog("\nFailed : Unable to execute the query. Should not delete data in this table through query. Please contact DesktopCentral Support.");
                                return;
                            }
                        }
                        catch (final Exception e) {
                            CommandLineQueryExecuter.logger.log(Level.SEVERE, "Unable to parse the query [" + sql + "].", e);
                        }
                        stmt.executeUpdate(sql);
                    }
                    final DataObject dataObject = DataAccess.constructDataObject();
                    final Row eventLogRow = getEventLogRow(4000, "DC-SYSTEM-USER", sql_remarks + " Executed by " + System.getProperty("user.name"));
                    dataObject.addRow(eventLogRow);
                    DataAccess.add(dataObject);
                    CommandLineQueryExecuter.logger.log(Level.INFO, "\nevent log added row : " + eventLogRow);
                }
            }
            showMsgInConsoleAndLog("\nExecuted the query successfully.");
        }
        catch (final Exception e2) {
            showMsgInConsoleAndLog("\nUnable to execute the query. Please contact DesktopCentral Support.");
            CommandLineQueryExecuter.logger.log(Level.WARNING, "Exception while executing the query", e2);
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    private static void showMsgInConsoleAndLog(final String msg) {
        ConsoleOut.println(msg);
        CommandLineQueryExecuter.logger.log(Level.INFO, msg);
    }
    
    public static void main(final String[] args) throws Exception {
        try {
            final CommandLineQueryExecuter cmdQEObj = new CommandLineQueryExecuter();
            cmdQEObj.startServer();
            if (cmdQEObj.validateUserForAdminRole()) {
                parseCMDXML(args);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            new StandAlonePersistence().stopDB();
        }
        System.exit(0);
    }
    
    private boolean validateUserForAdminRole() {
        final String username = getUserName();
        final String password = getPassword();
        final String domain = getDomainIfAvailable();
        return this.validateUserForAdminRole(username, password, domain);
    }
    
    private boolean validateUserForAdminRole(final String username, final String password, final String domain) {
        User user = null;
        try {
            user = UserManagementUtil.validateAndAuthenticateUser(username, password, domain);
        }
        catch (final Exception ex) {
            CommandLineQueryExecuter.logger.log(Level.INFO, "Exception while validating user", ex);
        }
        if (user == null) {
            showMsgInConsoleAndLog("Invalid Username or Password or Domain");
            return false;
        }
        if (user.isAdminUser()) {
            CommandLineQueryExecuter.logger.log(Level.INFO, "User is in admin role");
            return true;
        }
        showMsgInConsoleAndLog("User does not have enough privilege to execute query");
        return false;
    }
    
    private static String getUserName() {
        String username = null;
        try {
            username = getInputFromCommandLine("Username : ", false);
        }
        catch (final IOException e) {
            ConsoleOut.println("Error trying to read Username!");
            System.exit(1);
        }
        return username;
    }
    
    private static String getPassword() {
        String password = "";
        try {
            password = getInputFromCommandLine("Password : ", true);
        }
        catch (final IOException e) {
            ConsoleOut.println("Error trying to read Password!");
            System.exit(1);
        }
        return password;
    }
    
    private static String getDomainIfAvailable() {
        String domain = null;
        try {
            domain = getInputFromCommandLine("Domain (If AD User) :  ", false);
        }
        catch (final IOException e) {
            ConsoleOut.println("Error trying to read Domain!");
            System.exit(1);
        }
        return domain;
    }
    
    private static String getInputFromCommandLine(final String prompt, final boolean isHidden) throws IOException {
        String password = "";
        ConsoleEraser consoleEraser = null;
        if (isHidden) {
            consoleEraser = new ConsoleEraser();
        }
        ConsoleOut.print(prompt);
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        if (consoleEraser != null) {
            consoleEraser.start();
        }
        password = in.readLine();
        if (consoleEraser != null) {
            consoleEraser.halt();
        }
        ConsoleOut.print("\b");
        return password;
    }
    
    static {
        CommandLineQueryExecuter.logger = Logger.getLogger(CommandLineQueryExecuter.class.getName());
    }
    
    private static class ConsoleEraser extends Thread
    {
        private boolean running;
        
        private ConsoleEraser() {
            this.running = true;
        }
        
        @Override
        public void run() {
            while (this.running) {
                ConsoleOut.print("\b ");
                try {
                    Thread.currentThread();
                    Thread.sleep(1L);
                    continue;
                }
                catch (final InterruptedException e) {}
                break;
            }
        }
        
        public synchronized void halt() {
            this.running = false;
        }
    }
}
