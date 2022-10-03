package com.adventnet.db.adapter.postgres;

import java.util.Hashtable;
import java.nio.file.Files;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.db.api.RelationalAPI;
import java.util.concurrent.TimeUnit;
import java.util.Enumeration;
import java.util.Map;
import java.util.Collection;
import java.util.Locale;
import com.zoho.framework.utils.OSCheckUtil;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.net.ConnectException;
import com.adventnet.mfw.ConsoleOut;
import java.util.ArrayList;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.file.InvalidPathException;
import java.io.IOException;
import java.util.logging.Level;
import com.zoho.conf.Configuration;
import java.util.Properties;
import com.zoho.conf.AppResources;
import java.util.regex.Pattern;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import com.adventnet.db.adapter.DBInitializer;

public class DefaultPostgresDBInitializer extends DBInitializer
{
    private static final Logger OUT;
    private static final Logger PGLOGGER;
    String dbhome;
    boolean isDaemonStarted;
    private static String socket;
    private static String LOCALHOST_ADDRESS;
    boolean printErrMessage;
    static String dbServiceName;
    protected static final int MAX_RETRIES_COUNT;
    protected Boolean hasDBService;
    protected String dataDirectory;
    protected String dataDirectoryOwnerName;
    protected Boolean isRootUser;
    protected int checkPIDStatusTimeout;
    private static AtomicBoolean pgctlErrorFlag;
    protected File pg_isReady;
    public static final Pattern DB_HOME_PATH_PATTERN;
    
    public DefaultPostgresDBInitializer() {
        this.isDaemonStarted = false;
        this.printErrMessage = false;
        this.hasDBService = null;
        this.dataDirectory = null;
        this.dataDirectoryOwnerName = null;
        this.isRootUser = null;
        this.checkPIDStatusTimeout = AppResources.getInteger("check.pid.status.timeout", Integer.valueOf(25));
        this.pg_isReady = null;
    }
    
    public void initialize(final Properties properties) {
        final String defaultDBHome = Configuration.getString("server.home") + File.separator + "pgsql";
        this.dbhome = ((properties != null) ? properties.getProperty("db.home", Configuration.getString("db.home", defaultDBHome)) : defaultDBHome);
        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "db.home ::: {0}", this.dbhome);
        if (!this.isWindows) {
            final String sockethome = this.buildString(this.dbhome, File.separator, "tmp");
            try {
                final File f = new File(sockethome);
                if (!f.exists()) {
                    f.mkdir();
                }
                DefaultPostgresDBInitializer.socket = f.getCanonicalPath();
            }
            catch (final IOException iex) {
                iex.printStackTrace();
            }
        }
        try {
            this.dataDirectory = new File(this.buildString(this.dbhome, File.separator, "data")).getCanonicalPath();
        }
        catch (final IOException e) {
            DefaultPostgresDBInitializer.OUT.severe("Exception occurred while initializing data directory" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean preDBServerStartUp() throws IOException {
        return true;
    }
    
    @Override
    public boolean postDBServerStartUp() throws IOException {
        return true;
    }
    
    @Override
    public boolean startDBServer(final int port, final String host, final String userName, final String password) throws IOException {
        if (!this.preDBServerStartUp()) {
            DefaultPostgresDBInitializer.OUT.log(Level.WARNING, "Since prehandling of database server has failed, startDBServer is skipped.");
            return false;
        }
        final Boolean isInvokedFromScript = Boolean.getBoolean("startscript");
        if (userName.equalsIgnoreCase("postgres")) {
            throw new IllegalArgumentException("Username cannot be postgres. Hint: DB connecting user cannot be a super user. Provide a normal user credential.");
        }
        final File file = new File(this.dataDirectory + File.separator + "postmaster.pid");
        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Checking for existance of postmaster.pid file :: {0}", file.exists());
        if (file.exists()) {
            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Attempting to delete postmaster.pid :: {0}", file.delete());
        }
        if (!DefaultPostgresDBInitializer.DB_HOME_PATH_PATTERN.matcher(this.dbhome).matches()) {
            throw new InvalidPathException(this.dbhome, "DB Home must be an absolute path containing only letter, numbers, space, characters like '-', '/', '.', '_'");
        }
        if (!this.hasDBService()) {
            this.validateVersion();
            DefaultPostgresDBInitializer.OUT.info("Checking for IPV6 support");
            final InetAddress address = InetAddress.getByName("localhost");
            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Resolved address of host [{0}] is [{1}]", new Object[] { host, address });
            if (address instanceof Inet6Address) {
                PostgresConfUtil.grantAccessForHost("::1/128", false);
            }
            DefaultPostgresDBInitializer.OUT.info("Going to start DB server using pg_ctl utility.");
            final Process startDBProcess = this.executePgCtlUtility(port, host, "start");
            final List<String> errMsgList = new ArrayList<String>();
            errMsgList.add("address already in use");
            errMsgList.add("could not create any tcp/ip sockets");
            this.dump(startDBProcess, DefaultPostgresDBInitializer.PGLOGGER, errMsgList, DefaultPostgresDBInitializer.pgctlErrorFlag);
            try {
                DefaultPostgresDBInitializer.OUT.info("waitfor pgctl Utility returns :: " + startDBProcess.waitFor());
            }
            catch (final InterruptedException e) {
                DefaultPostgresDBInitializer.OUT.severe("Exception occured while executing the pg_ctl utility.");
                e.printStackTrace();
            }
            if (DefaultPostgresDBInitializer.pgctlErrorFlag.get()) {
                DefaultPostgresDBInitializer.OUT.info("pgctl error flag returns :: " + DefaultPostgresDBInitializer.pgctlErrorFlag.get());
                throw new IOException("Trying to start PostgresSQL server failed");
            }
        }
        else {
            DefaultPostgresDBInitializer.OUT.info("Going to start Windows DB service.");
            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Windows DB service name :::: {0}", DefaultPostgresDBInitializer.dbServiceName);
            this.dump(this.executeDBServiceCommand("start"));
        }
        if (!(this.isDaemonStarted = this.isServerStarted(port, host, userName))) {
            if (isInvokedFromScript) {
                ConsoleOut.println("Trying to start PostgresSQL server failed ");
            }
            throw new ConnectException("Trying to start PostgresSQL server failed ");
        }
        if (isInvokedFromScript) {
            ConsoleOut.println("Database server successfully started...");
        }
        if (!this.postDBServerStartUp()) {
            DefaultPostgresDBInitializer.OUT.warning("Handling for post DB Startup is not successfull!!");
        }
        return this.isDaemonStarted;
    }
    
    private void validateVersion() throws IOException {
        try {
            final String version = this.getVersion();
            final String[] versionIndex = version.split("\\.");
            final String shortVersion = versionIndex[0] + "." + versionIndex[1];
            final Float dbVer = new Float(shortVersion);
            if (dbVer < new Float("9.4")) {
                throw new UnsupportedOperationException("Trying to connect to an incompatible version, PostgreSQL " + version + ". Only PostgreSQL versions 9.4 and above are supported");
            }
        }
        catch (final Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }
    
    @Override
    public void stopDBServer(final int port, final String host, final String userName, final String password) throws IOException {
        final Boolean isInvokedFromScript = Boolean.getBoolean("stopscript");
        if (this.isDaemonStarted || isInvokedFromScript) {
            if (userName.equalsIgnoreCase("postgres")) {
                throw new IllegalArgumentException("Username cannot be postgres. Hint: DB connecting user cannot be a super user. Provide a normal user credential.");
            }
            this.checkForPgIsReadybinary();
            DefaultPostgresDBInitializer.OUT.info("Stopping PgSQL Server ...");
            if (!this.hasDBService()) {
                if (!this.checkServerStatus(host, port, userName)) {
                    DefaultPostgresDBInitializer.OUT.info("Database server not running.");
                    if (isInvokedFromScript) {
                        ConsoleOut.println("Database server not running.");
                    }
                    return;
                }
                DefaultPostgresDBInitializer.OUT.info("Going to stop DB server using pg_ctl utility.");
                final Process stopDBProcess = this.executePgCtlUtility(port, host, "stop");
                final List<String> errMsgList = new ArrayList<String>();
                errMsgList.add("server does not shut down");
                this.dump(stopDBProcess, DefaultPostgresDBInitializer.PGLOGGER, errMsgList, DefaultPostgresDBInitializer.pgctlErrorFlag);
                try {
                    DefaultPostgresDBInitializer.OUT.info("waitfor pgctl Utility stop returns :: " + stopDBProcess.waitFor());
                }
                catch (final InterruptedException e) {
                    DefaultPostgresDBInitializer.OUT.severe("Exception occured while executing the pg_ctl utility.");
                    e.printStackTrace();
                }
                if (stopDBProcess.exitValue() != 0 || DefaultPostgresDBInitializer.pgctlErrorFlag.get()) {
                    DefaultPostgresDBInitializer.OUT.info("pgctl error flag returns :: " + DefaultPostgresDBInitializer.pgctlErrorFlag.get());
                    DefaultPostgresDBInitializer.OUT.severe("Trying to stop PostgresSQL server using pg_ctl utility failed");
                }
            }
            else {
                DefaultPostgresDBInitializer.OUT.info("Going to stop Windows DB service.");
                DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Windows DB service name :::: {0}", DefaultPostgresDBInitializer.dbServiceName);
                this.dump(this.executeDBServiceCommand("stop"));
            }
            if (!this.isServerStopped(port, host, userName)) {
                if (isInvokedFromScript) {
                    ConsoleOut.println("Unable to stop DB server !!!");
                }
                DefaultPostgresDBInitializer.OUT.severe("Unable to stop DB server !!!");
            }
            else if (isInvokedFromScript) {
                ConsoleOut.println("Shutdown completed.....");
            }
        }
    }
    
    private Process executeDBServiceCommand(final String operation) throws IOException {
        final List<String> commandToBeExecuted = new ArrayList<String>();
        commandToBeExecuted.add("sc");
        commandToBeExecuted.add(operation);
        commandToBeExecuted.add(DefaultPostgresDBInitializer.dbServiceName);
        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Command to be executed ::: {0}", commandToBeExecuted);
        final ProcessBuilder process = new ProcessBuilder(commandToBeExecuted);
        return process.start();
    }
    
    Process executePgCtlUtility(final int port, final String host, final String operation) throws IOException {
        final List<String> commandList = new ArrayList<String>();
        this.dbhome = this.dbhome.replace("/", File.separator);
        final String pgctlPath = this.buildString(this.dbhome, File.separator, "bin", File.separator, this.isWindows ? "pg_ctl.exe" : "pg_ctl");
        final File pg_ctl = new File(pgctlPath);
        if (!pg_ctl.exists()) {
            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "pg_ctl path :: {0}", pgctlPath);
            final String errMsg = pgctlPath + " file does not exist. Please check whether the AntiVirus software has quarantined the file.";
            ConsoleOut.println(errMsg);
            throw new IOException(errMsg);
        }
        commandList.add(pg_ctl.getCanonicalPath());
        if (operation.equals("status") || operation.equals("reload")) {
            commandList.add(operation);
            commandList.add("-D");
            commandList.add(this.dataDirectory);
        }
        else {
            commandList.add("-w");
            commandList.add("-D");
            commandList.add(this.dataDirectory);
            commandList.add("-o");
            commandList.add("\"-p" + port + "\"");
            if ("start".equals(operation)) {
                commandList.add(operation);
            }
            else if ("stop".equals(operation)) {
                commandList.add(operation);
                commandList.add("-s");
                commandList.add("-m");
                commandList.add("fast");
            }
        }
        DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, "Command :: {0}", commandList);
        final File extLibDir = new File(this.dbhome + File.separator + "extlib");
        if (!this.isWindows && this.isCurrentUserLinuxRootUser()) {
            final List<String> commandList2 = new ArrayList<String>();
            commandList2.add("su");
            commandList2.add("-");
            commandList2.add(this.getDataDirectoryOwnerName());
            commandList2.add("-c");
            final StringBuilder str = new StringBuilder();
            for (String string : commandList) {
                string = string.replaceAll("\"", "\\\"");
                str.append(string);
                str.append(" ");
            }
            if (!this.isWindows && extLibDir.exists()) {
                String ldLibPath = this.buildString("/lib:", extLibDir.getCanonicalPath(), ":$LD_LIBRARY_PATH");
                ldLibPath = "LD_LIBRARY_PATH=" + ldLibPath + " ";
                commandList2.add(ldLibPath + str.toString());
            }
            else {
                commandList2.add(str.toString());
            }
            DefaultPostgresDBInitializer.OUT.log(Level.FINE, "Command to be executed {0} ", Arrays.asList(commandList2));
            return this.executeCommand(commandList2, null);
        }
        Properties envProp = null;
        if (!this.isWindows && extLibDir.exists()) {
            envProp = new Properties();
            final String ldLibPath2 = this.buildString("/lib:", extLibDir.getCanonicalPath(), ":$LD_LIBRARY_PATH");
            ((Hashtable<String, String>)envProp).put("LD_LIBRARY_PATH", ldLibPath2);
        }
        DefaultPostgresDBInitializer.OUT.log(Level.FINE, "Command to be executed {0} ", commandList);
        return this.executeCommand(commandList, envProp);
    }
    
    private boolean isCurrentUserLinuxRootUser() throws IOException {
        if (this.isRootUser == null) {
            final String currentUserID = getUserID(null);
            DefaultPostgresDBInitializer.OUT.log(Level.FINE, "currentuser :::: {0}", currentUserID);
            this.isRootUser = currentUserID.trim().equals("0");
        }
        return this.isRootUser;
    }
    
    private static String getUserID(final String userName) throws IOException {
        final List<String> commandToBeExecuted = new ArrayList<String>();
        commandToBeExecuted.add("id");
        commandToBeExecuted.add("-g");
        if (userName != null) {
            commandToBeExecuted.add(userName);
        }
        final ProcessBuilder process = new ProcessBuilder(commandToBeExecuted);
        final Process start = process.start();
        final String id = new BufferedReader(new InputStreamReader(start.getInputStream())).readLine();
        start.destroy();
        return id;
    }
    
    private String getDataDirectoryOwnerName() throws IOException {
        if (this.dataDirectoryOwnerName == null) {
            final String owner = null;
            final List<String> cmd = new ArrayList<String>();
            cmd.add("stat");
            if (OSCheckUtil.getOSName().toLowerCase(Locale.ENGLISH).indexOf("mac os") != -1) {
                cmd.add("-f");
                cmd.add("%Su");
            }
            else {
                cmd.add("-c");
                cmd.add("%U");
            }
            cmd.add(this.dataDirectory);
            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Command to be executed ::: {0}", cmd);
            final ProcessBuilder ps = new ProcessBuilder(cmd);
            final Process ownerProcess = ps.start();
            final String ipStream = new BufferedReader(new InputStreamReader(ownerProcess.getInputStream())).readLine();
            if (ipStream == null) {
                this.dump(ownerProcess);
                throw new IOException("Error occured while getting data directory owner name.");
            }
            this.dataDirectoryOwnerName = ipStream.trim();
            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "dataDirectoryOwnerName :::: {0}", this.dataDirectoryOwnerName);
            ownerProcess.destroy();
        }
        return this.dataDirectoryOwnerName;
    }
    
    public boolean createDB(final int port, final String host, final String userName, final String passwd, final String dbName) {
        DefaultPostgresDBInitializer.OUT.info("Checking is DB server ready to accept connection...");
        if (this.isDBExists(port, host, userName, passwd, dbName)) {
            return true;
        }
        try {
            for (int retry = 0; retry < DefaultPostgresDBInitializer.MAX_RETRIES_COUNT; ++retry) {
                String createDBCmd = "CREATE DATABASE " + (this.isWindows ? ("\\\"" + dbName + "\\\"") : ("\"" + dbName + "\""));
                createDBCmd = createDBCmd + " OWNER = " + (this.isWindows ? ("\\\"" + userName + "\\\"") : ("\"" + userName + "\""));
                final Process p = this.executeCommand(port, host, userName, passwd, "postgres", createDBCmd);
                final BufferedReader errBuf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                final BufferedReader ipBuf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String errorStream = null;
                final String ipStream = ipBuf.readLine();
                final String chkErrorMsg = " database \"" + dbName + "\" already exists";
                while ((errorStream = errBuf.readLine()) != null) {
                    DefaultPostgresDBInitializer.OUT.log(Level.INFO, "ErrorStream ::: {0}", errorStream);
                    errorStream = errorStream.toLowerCase(Locale.ENGLISH);
                    if (errorStream.contains(chkErrorMsg.toLowerCase(Locale.ENGLISH))) {
                        DefaultPostgresDBInitializer.OUT.log(Level.FINE, "Database {0} already exists", dbName);
                        this.dump(p);
                        return true;
                    }
                    if (errorStream.contains("the database system is starting up") || errorStream.contains("could not connect to server: connection refused")) {
                        DefaultPostgresDBInitializer.OUT.info("Retrying createDB command");
                        p.destroy();
                        break;
                    }
                    if (errorStream.contains("the database system is shutting down") || errorStream.contains("password authentication failed for user")) {
                        DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, "Problem while creating database {0}.", dbName);
                        this.dump(p);
                        return false;
                    }
                }
                if (ipStream != null && ipStream.equalsIgnoreCase("CREATE DATABASE")) {
                    DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Database {0} created successfully...", dbName);
                    this.dump(p);
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, " \n \n Exception during createDB execution {0}.", ex);
        }
        return false;
    }
    
    public boolean isDBExists(final int port, final String host, final String userName, final String passwd, final String dbName) {
        for (int retrycount = 0; retrycount < DefaultPostgresDBInitializer.MAX_RETRIES_COUNT; ++retrycount) {
            Process p = null;
            try {
                final String isDBExistsCmd = "SELECT 'exists' FROM pg_database WHERE datname='" + dbName + "'";
                p = this.executeCommand(port, host, userName, passwd, "postgres", isDBExistsCmd);
                try (final BufferedReader errBuf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                     final BufferedReader ipBuf = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String errorStream = null;
                    final String chkErrorMsg = "the database system is starting up";
                    while ((errorStream = errBuf.readLine()) != null) {
                        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "ErrorStream ::: {0}", errorStream);
                        errorStream = errorStream.toLowerCase(Locale.ENGLISH);
                        if (errorStream.contains("the database system is starting up") || errorStream.contains("could not connect to server: connection refused")) {
                            DefaultPostgresDBInitializer.OUT.info("Retrying DB exists command");
                            p.destroy();
                            break;
                        }
                        if (errorStream.contains("the database system is shutting down") || errorStream.contains("password authentication failed for user")) {
                            DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, "Problem while connecting database.");
                            this.dump(p);
                            return false;
                        }
                        if (!errorStream.contains(chkErrorMsg)) {
                            DefaultPostgresDBInitializer.OUT.info("Database server is not ready to accept connection.");
                            p.destroy();
                            break;
                        }
                    }
                    String ipStream = null;
                    String dataRetrieved = null;
                    while ((ipStream = ipBuf.readLine()) != null) {
                        dataRetrieved += ipStream;
                    }
                    if (dataRetrieved != null && dataRetrieved.contains("exists")) {
                        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Database already exists");
                        this.dump(p);
                        return true;
                    }
                    break;
                }
            }
            catch (final IOException e) {
                DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, " \n \n Exception while checking existance of DB {0}.", e);
            }
            finally {
                if (p != null) {
                    p.destroy();
                }
            }
        }
        return false;
    }
    
    protected Process executeCommand(final int port, final String host, final String userName, final String passwd, final String dbName, final String commandToBeExecute) throws IOException {
        return this.executeCommand(port, host, userName, passwd, dbName, commandToBeExecute, null);
    }
    
    protected Process executeCommand(final int port, final String host, final String userName, final String passwd, final String dbName, final String commandToBeExecute, final List<String> otherArgs) throws IOException {
        try {
            final List<String> commandList = new ArrayList<String>();
            commandList.add(new File(this.buildString(this.dbhome, File.separator, "bin", File.separator, "psql")).getCanonicalPath());
            commandList.add("-U");
            commandList.add(userName);
            commandList.add("-p");
            commandList.add(String.valueOf(port));
            if (!host.equals("localhost")) {
                commandList.add("-h");
                commandList.add(host);
            }
            else {
                final String hostAddress = this.getHostAddressName(host);
                DefaultPostgresDBInitializer.OUT.log(Level.INFO, "hostAddress of localhost is {0}", hostAddress);
                commandList.add("-h");
                commandList.add(hostAddress);
            }
            if (otherArgs != null && !otherArgs.isEmpty()) {
                commandList.addAll(otherArgs);
            }
            commandList.add("-c");
            commandList.add(commandToBeExecute);
            commandList.add("-d");
            commandList.add(dbName);
            commandList.add("-w");
            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Command to be executed :::: {0} ", commandList);
            final Properties envProp = new Properties();
            envProp.setProperty("PGPASSWORD", passwd);
            final File extLibDir = new File(this.dbhome + File.separator + "extlib");
            if (!this.isWindows && extLibDir.exists()) {
                final String ldLibPath = this.buildString("/lib:", extLibDir.getCanonicalPath(), ":$LD_LIBRARY_PATH");
                envProp.setProperty("LD_LIBRARY_PATH", ldLibPath);
            }
            return this.executeCommand(commandList, envProp);
        }
        catch (final Exception ee) {
            ee.printStackTrace();
            return null;
        }
    }
    
    protected Process executeFile(final int port, final String host, final String userName, final String password, final String dbName, final String filePath) throws IOException {
        final List<String> commandList = new ArrayList<String>();
        commandList.add(new File(this.buildString(this.dbhome, File.separator, "bin", File.separator, "psql")).getCanonicalPath());
        commandList.add("-U");
        commandList.add(userName);
        commandList.add("-p");
        commandList.add(String.valueOf(port));
        if (!host.equals("localhost")) {
            commandList.add("-h");
            commandList.add(host);
        }
        else {
            final String hostAddress = this.getHostAddressName(host);
            DefaultPostgresDBInitializer.OUT.log(Level.FINE, "hostAddress of localhost is {0}", hostAddress);
            commandList.add("-h");
            commandList.add(hostAddress);
        }
        commandList.add("-f");
        commandList.add(new File(filePath).getAbsolutePath());
        commandList.add("-d");
        commandList.add(dbName);
        commandList.add("-w");
        final Properties envProp = new Properties();
        envProp.setProperty("PGPASSWORD", password);
        final File extLibDir = new File(this.dbhome + File.separator + "extlib");
        if (!this.isWindows && extLibDir.exists()) {
            final String ldLibPath = this.buildString("/lib:", extLibDir.getCanonicalPath(), ":$LD_LIBRARY_PATH");
            envProp.setProperty("LD_LIBRARY_PATH", ldLibPath);
        }
        return this.executeCommand(commandList, envProp);
    }
    
    Process executeCommand(final List<String> commandList, final Properties envVariables) throws IOException {
        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Command to be executed {0} ", commandList);
        final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        final Map<String, String> environment = processBuilder.environment();
        if (envVariables != null) {
            final Enumeration<Object> keys = ((Hashtable<Object, V>)envVariables).keys();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                environment.put(key, envVariables.getProperty(key));
            }
        }
        return processBuilder.start();
    }
    
    public boolean checkServerStatus(final String host, final int port, final String userName) throws IOException {
        try {
            if (this.pg_isReady == null) {
                this.checkForPgIsReadybinary();
            }
            final boolean isRunning = this.getServerStatus(host, port, userName, true);
            if (isRunning && Boolean.getBoolean("startscript")) {
                ConsoleOut.println("Database server already running.");
            }
            return isRunning;
        }
        catch (final IOException ex) {
            final String errMsg = ex.getMessage();
            final String pgctlPath = this.buildString(this.dbhome, File.separator, "bin", File.separator, this.isWindows ? "pg_isready.exe" : "pg_isready");
            if (errMsg.indexOf("Access is denied") != -1) {
                ConsoleOut.println("Unable execute " + this.pg_isReady.getAbsolutePath() + " file. Access is denied.\n* Please check whether the Antivirus software preventing pg_isready file execution.");
            }
            else if (errMsg.indexOf("The system cannot find the file specified") != -1) {
                final String errorMsg = this.pg_isReady.getAbsolutePath() + " file does not exist. Kindly check whether the AntiVirus software has quarantined the file.";
                ConsoleOut.println(errorMsg);
            }
            throw ex;
        }
    }
    
    @Deprecated
    public boolean isServerStarted(final int port, final String host, final String userName, final String password) {
        return this.isServerStarted(port, host, userName);
    }
    
    public boolean isServerStarted(final int port, final String host, final String userName) {
        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "is DB registered as a service ::: {0}", this.hasDBService());
        if (this.hasDBService() && !this.getServiceStatus("RUNNING")) {
            DefaultPostgresDBInitializer.OUT.severe("Unable to START Windows DB service... Returning false...");
            return false;
        }
        boolean isServerStarted = false;
        try {
            isServerStarted = this.getServerStatus(host, port, userName, true);
        }
        catch (final IOException ex) {
            DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, "Exception occured while checking server status.", ex);
        }
        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "isServerStarted :: going to return :: {0}", isServerStarted);
        return isServerStarted;
    }
    
    public boolean isServerStopped(final int port, final String host, final String userName) {
        boolean isServerStopped = false;
        try {
            isServerStopped = !this.getServerStatus(host, port, userName, false);
        }
        catch (final IOException ex) {
            DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, "Exception occured while checking server status", ex);
        }
        boolean isPidFileDeleted = false;
        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "isServerStopped :: going to return :: {0}", isServerStopped);
        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "is DB registered as a service ::: {0}", this.hasDBService());
        if (this.hasDBService() && !this.getServiceStatus("STOPPED")) {
            DefaultPostgresDBInitializer.OUT.severe("Unable to STOP Windows DB service... Returning false...");
            return false;
        }
        try {
            final File postmasterPid = new File(this.buildString(this.dataDirectory, File.separator, "postmaster.pid"));
            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Check PID status default timeout ", DefaultPostgresDBInitializer.CHECK_PID_STATUS_TIMEOUT);
            for (int i = 0; i < DefaultPostgresDBInitializer.CHECK_PID_STATUS_TIMEOUT; ++i) {
                if (!postmasterPid.exists()) {
                    isPidFileDeleted = true;
                    DefaultPostgresDBInitializer.OUT.info("postmaster.pid file doesn't exist.");
                    break;
                }
                DefaultPostgresDBInitializer.OUT.warning("postmaster.pid file exist. Waiting for DB server shutdown...");
                Thread.sleep(1000L);
            }
        }
        catch (final InterruptedException ex2) {
            DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, null, ex2);
        }
        DefaultPostgresDBInitializer.OUT.log(Level.INFO, "isServerStopped method returning status ::: {0}", isServerStopped);
        return isServerStopped && isPidFileDeleted;
    }
    
    protected boolean getServerStatus(final String host, final int port, final String userName, final boolean getActiveStatus) throws IOException {
        boolean serverStatus = false;
        boolean serverStartedProcessingRequest = false;
        for (int count = 0; count < DefaultPostgresDBInitializer.MAX_RETRIES_COUNT; ++count) {
            final int pgReady = this.isPgReady(port, host, userName);
            if (getActiveStatus && pgReady == 0) {
                serverStatus = true;
            }
            else if (getActiveStatus && serverStartedProcessingRequest && pgReady == 2) {
                DefaultPostgresDBInitializer.OUT.log(Level.INFO, "DB server failed to process request ");
                serverStatus = false;
            }
            else if (!getActiveStatus && pgReady == 2) {
                serverStatus = false;
            }
            else if (pgReady == 1 && !serverStartedProcessingRequest) {
                serverStartedProcessingRequest = true;
            }
            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "isServerRunning :: {0}", serverStatus);
            if (pgReady == 0) {
                break;
            }
            if (pgReady == 2) {
                break;
            }
            try {
                Thread.sleep(1000L);
            }
            catch (final InterruptedException e) {
                DefaultPostgresDBInitializer.OUT.log(Level.FINE, null, e);
            }
        }
        return serverStatus;
    }
    
    protected void validateDataDirectory(final String host, final int port, final String userName, final String password) throws IOException {
        DefaultPostgresDBInitializer.OUT.info("Validating data_directory path with running server.");
        final Process executeCommand = this.executeCommand(port, host, userName, password, "postgres", "SHOW data_directory");
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(executeCommand.getInputStream()));
        final BufferedReader errorReader = new BufferedReader(new InputStreamReader(executeCommand.getErrorStream()));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.endsWith("data")) {
                DefaultPostgresDBInitializer.OUT.info("dataDirectory path from server ::" + line);
                break;
            }
        }
        this.dump(executeCommand, null, null, DefaultPostgresDBInitializer.pgctlErrorFlag);
        if (line != null && !this.dataDirectory.equals(new File(line.trim()).getCanonicalPath())) {
            throw new IOException("Could not start Postgres database, The port " + port + " is in use.");
        }
        String errorStream = null;
        while ((errorStream = errorReader.readLine()) != null) {
            DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, "ErrorStream ::: {0}", errorStream);
            errorStream = errorStream.toLowerCase(Locale.ENGLISH);
            if (errorStream.contains("password authentication failed for user")) {
                ConsoleOut.println("password authentication failed for user: " + userName + ". Given credentials do not match with running postgres server instance.");
                throw new IOException("password authentication failed for user: " + userName + " since port: " + port + " is occupied by another server instance");
            }
        }
    }
    
    protected boolean getServiceStatus(final String serviceMsg) {
        if (!this.hasDBService()) {
            return false;
        }
        for (int count = 0; count < DefaultPostgresDBInitializer.MAX_RETRIES_COUNT; ++count) {
            try {
                Thread.sleep(1000L);
                Process statusProcess = null;
                statusProcess = this.executeDBServiceCommand("query");
                final BufferedReader buf = new BufferedReader(new InputStreamReader(statusProcess.getInputStream()));
                String inputStream = null;
                while ((inputStream = buf.readLine()) != null) {
                    DefaultPostgresDBInitializer.OUT.info(inputStream);
                    if (inputStream.contains("STATE")) {
                        statusProcess.destroy();
                        if (inputStream.contains(serviceMsg)) {
                            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "isWindowsServiceStarted returning :: true");
                            return true;
                        }
                        break;
                    }
                }
            }
            catch (final InterruptedException ex) {
                DefaultPostgresDBInitializer.OUT.log(Level.FINE, null, ex);
            }
            catch (final IOException ioe) {
                DefaultPostgresDBInitializer.OUT.log(Level.FINE, "ioe :: {0}", ioe);
            }
            DefaultPostgresDBInitializer.OUT.log(Level.INFO, "isWindowsServiceStarted :: false");
        }
        return false;
    }
    
    protected boolean hasDBService() {
        if (this.hasDBService == null) {
            this.hasDBService = (DefaultPostgresDBInitializer.dbServiceName != null && this.isWindows);
        }
        return this.hasDBService;
    }
    
    public boolean isDBReadyToAcceptConnection(final int port, final String host, final String userName, final String passwd) throws IOException {
        DefaultPostgresDBInitializer.OUT.info("Checking is DB server ready to accept connection...");
        for (int retry = 0; retry < DefaultPostgresDBInitializer.MAX_RETRIES_COUNT; ++retry) {
            final String createDBCmd = "SELECT 1";
            final Process p = this.executeCommand(port, host, userName, passwd, "postgres", createDBCmd);
            BufferedReader errBuf = null;
            BufferedReader ipBuf = null;
            try {
                errBuf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                ipBuf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String errorStream = null;
                final String ipStream = ipBuf.readLine();
                final String chkErrorMsg = "the database system is starting up";
                while ((errorStream = errBuf.readLine()) != null) {
                    DefaultPostgresDBInitializer.OUT.log(Level.INFO, "ErrorStream ::: {0}", errorStream);
                    errorStream = errorStream.toLowerCase(Locale.ENGLISH);
                    if (errorStream.contains("the database system is shutting down") || errorStream.contains("password authentication failed for user")) {
                        DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, "Problem while connecting database.");
                        this.dump(p);
                        return false;
                    }
                    if (!errorStream.contains(chkErrorMsg)) {
                        DefaultPostgresDBInitializer.OUT.info("Database server is not ready to accept connection.");
                        p.destroy();
                        break;
                    }
                }
                if (ipStream != null) {
                    DefaultPostgresDBInitializer.OUT.log(Level.INFO, "Database server responsing...\nDatabase server is now READY to accept connections!!!");
                    this.dump(p);
                    return true;
                }
                try {
                    TimeUnit.SECONDS.sleep(1L);
                }
                catch (final InterruptedException ex) {
                    DefaultPostgresDBInitializer.OUT.log(Level.FINE, null, ex);
                }
            }
            finally {
                if (errBuf != null) {
                    errBuf.close();
                }
                if (ipBuf != null) {
                    ipBuf.close();
                }
            }
        }
        return false;
    }
    
    void checkForPgIsReadybinary() throws IOException {
        final String pgIsreadyPath = this.buildString(this.dbhome, File.separator, "bin", File.separator, this.isWindows ? "pg_isready.exe" : "pg_isready");
        this.pg_isReady = new File(pgIsreadyPath);
        DefaultPostgresDBInitializer.OUT.info(this.pg_isReady.getAbsolutePath() + " isExists :: " + this.pg_isReady.exists());
        if (!this.pg_isReady.exists()) {
            DefaultPostgresDBInitializer.OUT.log(Level.SEVERE, "pg_isready binary does not exist. This may cause problem while checking server status if the startDBServer is true.");
            if (Boolean.getBoolean("stopscript") || Boolean.getBoolean("startscript")) {
                ConsoleOut.println("ERROR : pg_isready file, which is mandatory for DB server start/stop, does not exist.");
            }
            throw new IOException("pg_isready file, which is mandatory for DB server start/stop, does not exist.");
        }
    }
    
    protected int isPgReady(final int port, final String host, final String userName) throws IOException {
        int exitCode = 0;
        final List<String> commandList = new ArrayList<String>();
        commandList.add(this.pg_isReady.getAbsolutePath());
        if (!host.equals("localhost")) {
            commandList.add("--host");
            commandList.add(host);
        }
        else {
            commandList.add("--host");
            commandList.add(this.getHostAddressName(host));
        }
        commandList.add("--port");
        commandList.add(Integer.toString(port));
        commandList.add("-t");
        commandList.add(Integer.toString(DefaultPostgresDBInitializer.MAX_RETRIES_COUNT));
        commandList.add("--username");
        commandList.add(userName);
        final Process isReadyProc = this.executeCommand(commandList, null);
        this.dump(isReadyProc);
        try {
            exitCode = isReadyProc.waitFor();
        }
        catch (final InterruptedException e) {
            DefaultPostgresDBInitializer.OUT.log(Level.FINE, null, e);
            DefaultPostgresDBInitializer.OUT.warning("isReadyProc interrupted, hence returning false");
        }
        DefaultPostgresDBInitializer.OUT.info("pg_isready returning status :: " + exitCode);
        return exitCode;
    }
    
    @Override
    public boolean isServerStarted() throws IOException {
        final DBAdapter dbAdapter = RelationalAPI.getInstance().getDBAdapter();
        final Properties props = dbAdapter.getDBProps();
        DefaultPostgresDBInitializer.OUT.log(Level.FINEST, "props :: {0}", props);
        this.checkForPgIsReadybinary();
        return this.checkServerStatus(props.getProperty("Server"), ((Hashtable<K, Integer>)props).get("Port"), props.getProperty("username"));
    }
    
    @Override
    public String getHostAddressName(final String hostname) throws IOException {
        final String hostAddressName = super.getHostAddressName(hostname);
        DefaultPostgresDBInitializer.OUT.log(Level.FINE, "Resolved hostAddressName :: {0}", hostAddressName);
        return hostAddressName;
    }
    
    @Override
    public String getVersion() throws Exception {
        final String postgres = new File(Configuration.getString("db.home")).getAbsolutePath() + File.separator + "bin" + File.separator + "postgres" + (this.isWindows ? ".exe" : "");
        if (new File(postgres).exists()) {
            DefaultPostgresDBInitializer.OUT.fine("Getting current version using 'postgres' binary.");
            final List<String> commandList = new ArrayList<String>();
            commandList.add(postgres);
            commandList.add("-V");
            final Process postgresProcess = this.executeCommand(commandList, null);
            String pgVersion = new BufferedReader(new InputStreamReader(postgresProcess.getInputStream())).readLine();
            final int waitFor = postgresProcess.waitFor();
            DefaultPostgresDBInitializer.OUT.log(Level.FINE, "postgresProcess waitFor :: {0}", waitFor);
            postgresProcess.destroy();
            if (pgVersion != null) {
                pgVersion = pgVersion.substring(pgVersion.lastIndexOf(")") + 1).trim();
            }
            DefaultPostgresDBInitializer.OUT.log(Level.FINE, "Returning version {0}", pgVersion);
            return pgVersion;
        }
        Connection conn = null;
        String version = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            version = this.executeVersionQuery(conn);
            version = version.substring(version.indexOf(32) + 1, version.indexOf(44));
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return version;
    }
    
    private String executeVersionQuery(final Connection conn) throws SQLException, QueryConstructionException {
        DataSet ds = null;
        String version = null;
        try {
            ds = RelationalAPI.getInstance().executeQuery("SELECT version()", conn);
            if (ds.next()) {
                version = ds.getAsString(1);
            }
        }
        finally {
            if (ds != null) {
                ds.close();
            }
        }
        return version;
    }
    
    @Override
    public byte getDBArchitecture() throws Exception {
        final File pgArchFile = new File(Configuration.getString("db.home") + File.separator + "bin" + File.separator + "PG_ARCH");
        if (pgArchFile.exists()) {
            try {
                return new Byte(new String(Files.readAllBytes(pgArchFile.toPath())).trim());
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        byte arch = -1;
        Connection conn = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            final String version = this.executeVersionQuery(conn);
            arch = Byte.parseByte(version.substring(version.lastIndexOf(32) + 1, version.lastIndexOf(45)));
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return arch;
    }
    
    @Override
    public String getDBDataDirectory() {
        Connection conn = null;
        String dir = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            dir = RelationalAPI.getInstance().getDBAdapter().getDBSystemProperty(conn, "data_directory");
        }
        catch (final Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final SQLException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return dir;
    }
    
    static {
        OUT = Logger.getLogger(DefaultPostgresDBInitializer.class.getName());
        PGLOGGER = Logger.getLogger("pglog");
        DefaultPostgresDBInitializer.socket = "";
        DefaultPostgresDBInitializer.LOCALHOST_ADDRESS = null;
        DefaultPostgresDBInitializer.dbServiceName = null;
        MAX_RETRIES_COUNT = AppResources.getInteger("DBStartupRetries", Integer.valueOf(120));
        DefaultPostgresDBInitializer.pgctlErrorFlag = new AtomicBoolean(false);
        DB_HOME_PATH_PATTERN = Pattern.compile("[\\w:\\\\ \\-/._()]*", 2);
    }
}
