package com.adventnet.db.adapter.mysql;

import java.util.Hashtable;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import java.util.Arrays;
import java.util.Collection;
import com.zoho.framework.utils.OSCheckUtil;
import java.util.ArrayList;
import java.net.ConnectException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.IOException;
import com.zoho.conf.AppResources;
import com.zoho.conf.Configuration;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.db.adapter.DBInitializer;

public class MySqlDBInitializer extends DBInitializer
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    String socket;
    String dbhome;
    public static boolean isDemonStarted;
    boolean printErrMessage;
    private static String server_home;
    private static List<String> commandTemplate;
    
    public MySqlDBInitializer() {
        this.socket = null;
        this.printErrMessage = false;
        this.dbhome = Configuration.getString("db.home", MySqlDBInitializer.server_home + File.separator + "mysql");
        try {
            this.dbhome = new File(Configuration.getString("db.home")).getCanonicalPath();
        }
        catch (final Exception ex) {}
        if (this.osName.toLowerCase().indexOf("windows") == -1) {
            final String sockFile = AppResources.getString("mysql.socket");
            if (sockFile != null) {
                try {
                    final File dbFile = new File(sockFile);
                    this.socket = dbFile.getCanonicalPath();
                }
                catch (final IOException iex) {
                    iex.printStackTrace();
                }
            }
            else {
                final String sockethome = this.dbhome + File.separator + "tmp";
                try {
                    final File f = new File(sockethome);
                    if (!f.exists()) {
                        f.mkdir();
                    }
                    this.socket = f.getCanonicalPath();
                }
                catch (final IOException iex2) {
                    iex2.printStackTrace();
                }
            }
        }
    }
    
    public boolean getMySQLAdminPingStatus() throws IOException {
        final Properties dbProps = PersistenceInitializer.getDefaultDBProps();
        final String mysqlstr = dbProps.getProperty("url");
        final String userName = dbProps.getProperty("username", "root");
        final String passwd = dbProps.getProperty("password");
        final int port = PersistenceUtil.getPort(mysqlstr);
        return this.getMySQLAdminPingStatus(userName, port, passwd);
    }
    
    public boolean getMySQLAdminPingStatus(final String userName, final int port, final String passwd) throws IOException {
        boolean pingStatus = false;
        final Process p = this.executeCommand(port, "localhost", userName, passwd, new String[] { "ping" });
        final BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String str = null;
        while ((str = buf.readLine()) != null) {
            MySqlDBInitializer.OUT.info(str);
            if (str.contains("mysqld is alive")) {
                pingStatus = true;
                break;
            }
        }
        MySqlDBInitializer.OUT.log(Level.INFO, "getMySQLAdminPingStatus :: returning :: [{0}]", pingStatus);
        return pingStatus;
    }
    
    @Override
    public boolean startDBServer(int port, final String host, final String userName, final String password) throws IOException, ConnectException {
        MySqlDBInitializer.OUT.log(Level.INFO, "host :: [{0}], port :: [{1}]", new Object[] { host, port });
        boolean serverRunning = this.checkServerStatus(host, port);
        MySqlDBInitializer.OUT.log(Level.INFO, "serverRunning :: [{0}]", serverRunning);
        if (!serverRunning) {
            serverRunning = this.getMySQLAdminPingStatus(userName, port, password);
        }
        if (serverRunning) {
            String datadirstr = null;
            final String[] cmd = { "variables" };
            final Process p = this.executeCommand(port, host, userName, password, cmd);
            final BufferedReader bis = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputStream = null;
            String portStr = null;
            while ((inputStream = bis.readLine()) != null) {
                if (inputStream.indexOf("datadir") >= 0) {
                    MySqlDBInitializer.OUT.log(Level.INFO, "inputStream :: [{0}]", inputStream);
                    final StringTokenizer stringTokenizer = new StringTokenizer(inputStream, "|", false);
                    while (stringTokenizer.hasMoreTokens()) {
                        inputStream = stringTokenizer.nextToken();
                    }
                    datadirstr = inputStream.trim();
                    if (portStr != null) {
                        break;
                    }
                }
                if (inputStream.indexOf(" port ") >= 0) {
                    MySqlDBInitializer.OUT.log(Level.INFO, "inputStream :: [{0}]", inputStream);
                    inputStream = inputStream.replaceAll("\\|", "");
                    inputStream = inputStream.replaceAll("port", "");
                    portStr = inputStream.trim();
                    if (datadirstr != null) {
                        break;
                    }
                    continue;
                }
            }
            MySqlDBInitializer.OUT.log(Level.INFO, "datadirstr :: [{0}], portStr :: [{1}]", new Object[] { datadirstr, portStr });
            datadirstr = ((datadirstr == null) ? null : new File(datadirstr).getParentFile().getCanonicalPath());
            final String dbhomestr = new File(this.dbhome).getCanonicalPath();
            MySqlDBInitializer.OUT.log(Level.INFO, "datadirstr :: [{0}], dbhomestr :: [{1}], portStr :: [{2}]", new Object[] { datadirstr, dbhomestr, portStr });
            if (datadirstr == null || !datadirstr.equals(dbhomestr)) {
                if (AppResources.getString("useAvailableDBPort", "false").equals("false")) {
                    throw new ConnectException("Unable to start MySQL server on port " + port + ", since another instance of mysql is running in this port.");
                }
                port = DBInitializer.checkAndChangeDBPort(PersistenceInitializer.getDBParamsFilePath(), -1);
                Configuration.setString("generate.dbparams", "true");
            }
            else {
                if (Integer.parseInt(portStr) != port) {
                    throw new ConnectException("Unable to start MySQL server on port " + port + ", since another instance of this mysql is running in this port.");
                }
                return true;
            }
        }
        if (!this.preDBServerStartUp()) {
            MySqlDBInitializer.OUT.log(Level.WARNING, "Since prehandling of database server has failed, startDBServer is skipped.");
            return false;
        }
        final List<String> commandList = new ArrayList<String>();
        commandList.add(String.valueOf(port));
        if (this.osName.toLowerCase().indexOf("windows") == -1 && this.socket != null) {
            commandList.add(this.socket);
        }
        final int size = commandList.size();
        final String[] startBatchFileArgs = commandList.toArray(new String[size]);
        this.startDBServer(startBatchFileArgs);
        MySqlDBInitializer.isDemonStarted = (this.isServerStarted(port, host) || this.getMySQLAdminPingStatus(userName, port, password));
        if (!this.postDBServerStartUp()) {
            MySqlDBInitializer.OUT.warning("Handling for post DB Startup is not successfull!!");
        }
        return MySqlDBInitializer.isDemonStarted;
    }
    
    public void stopDBServer(final int port, final String userName, final String passwd) throws IOException {
        final Boolean isInvokedFromScript = Boolean.getBoolean("stopscript");
        if (MySqlDBInitializer.isDemonStarted || isInvokedFromScript) {
            final List<String> commandList = new ArrayList<String>();
            commandList.add(String.valueOf(port));
            commandList.add(userName);
            if (this.osName.toLowerCase().indexOf("windows") == -1 && this.socket != null) {
                commandList.add(this.socket);
            }
            if (passwd != null) {
                commandList.add(passwd);
            }
            final int size = commandList.size();
            final String[] stopBatchFileArgs = commandList.toArray(new String[size]);
            this.stopDBServer(stopBatchFileArgs);
        }
    }
    
    public boolean createDB(final int port, final String host, final String userName, final String passwd, final String dbName) {
        try {
            final String[] crtDBCmd = { "create ", dbName };
            final Process p = this.executeCommand(port, host, userName, passwd, crtDBCmd);
            final BufferedReader buf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String errorStream = buf.readLine();
            final String chkErrorMsg = " database exists";
            errorStream = ((errorStream != null) ? errorStream.toLowerCase() : null);
            MySqlDBInitializer.OUT.log(Level.INFO, "errorStream :: [{0}]", errorStream);
            if (errorStream != null && errorStream.indexOf(chkErrorMsg) == -1) {
                MySqlDBInitializer.OUT.log(Level.SEVERE, "Problem while creating database {0}.", dbName);
                MySqlDBInitializer.OUT.log(Level.SEVERE, "{0}", errorStream);
                return false;
            }
        }
        catch (final Exception ex) {
            MySqlDBInitializer.OUT.log(Level.SEVERE, " \n \n Problem while creating database {0}.", ex);
            return false;
        }
        return true;
    }
    
    private Process executeCommand(final int port, final String host, final String userName, final String passwd, final String[] cmd) throws IOException {
        final List<String> commandList = new ArrayList<String>();
        if (MySqlDBInitializer.commandTemplate.isEmpty()) {
            this.dbhome = this.dbhome.replace('/', File.separatorChar);
            final String binaryName = "mysqladmin" + ((OSCheckUtil.getOS() == 2) ? ".exe" : "");
            final File mysqlAdmin = new File(this.dbhome + File.separatorChar + "bin" + File.separatorChar + binaryName);
            if (!mysqlAdmin.exists()) {
                MySqlDBInitializer.OUT.log(Level.INFO, "mysqld path :: {0}", mysqlAdmin.getCanonicalPath());
                throw new IOException("mysqld does not exists. Make sure whether the mysql DB bundled with the product.");
            }
            commandList.add(mysqlAdmin.getCanonicalPath());
            commandList.add("--no-defaults");
            commandList.add("-u");
            commandList.add(userName);
            if (passwd != null && !passwd.equals("")) {
                commandList.add("--password=" + passwd);
            }
            commandList.add("--port=" + port);
            commandList.add("-h");
            commandList.add(host.equals("localhost") ? this.getHostAddressName(host) : host);
            MySqlDBInitializer.commandTemplate.addAll(commandList);
        }
        else {
            commandList.addAll(MySqlDBInitializer.commandTemplate);
        }
        commandList.addAll(Arrays.asList(cmd));
        final Process procBuilder = new ProcessBuilder(commandList).start();
        if (passwd != null && !passwd.equals("")) {
            commandList.set(commandList.indexOf("--password=" + passwd), "--password=*******");
        }
        MySqlDBInitializer.OUT.log(Level.INFO, "Command to be executed {0} ", commandList);
        return procBuilder;
    }
    
    public void startDBServer(final int port, final String host) throws IOException, ConnectException {
        if (!this.preDBServerStartUp()) {
            MySqlDBInitializer.OUT.log(Level.WARNING, "Since prehandling of database server has failed, startDBServer is skipped.");
            return;
        }
        final List<String> commandList = new ArrayList<String>();
        commandList.add(String.valueOf(port));
        final String[] startBatchFileArgs = commandList.toArray(new String[commandList.size()]);
        this.startDBServer(startBatchFileArgs);
        if (!(MySqlDBInitializer.isDemonStarted = this.isServerStarted(port, host))) {
            throw new ConnectException("Trying to start MySQL server failed ");
        }
        if (!this.postDBServerStartUp()) {
            MySqlDBInitializer.OUT.warning("Handling for post DB Startup is not successfull!!");
        }
    }
    
    @Override
    public void stopDBServer(final int port, final String host, final String userName, final String password) throws IOException {
        final Boolean isInvokedFromScript = Boolean.getBoolean("stopscript");
        String pidFileDirectory = null;
        final String[] cmd = { "variables" };
        final Process p = this.executeCommand(port, host, userName, password, cmd);
        final BufferedReader bis = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String inputStream = null;
        while ((inputStream = bis.readLine()) != null) {
            if (inputStream.indexOf("pid_file") >= 0) {
                MySqlDBInitializer.OUT.log(Level.INFO, "inputStream :: [{0}]", inputStream);
                inputStream = inputStream.replaceAll("\\|", "");
                inputStream = inputStream.replaceAll("pid_file", "");
                pidFileDirectory = inputStream.trim();
            }
        }
        if (pidFileDirectory == null) {
            MySqlDBInitializer.OUT.log(Level.WARNING, "PID file could not be found.Either DB server is not started or PID file location has been changed in database.");
            return;
        }
        final File pidFile = new File(pidFileDirectory);
        if (MySqlDBInitializer.isDemonStarted || isInvokedFromScript) {
            final Process proc = this.executeCommand(port, host, userName, password, new String[] { "shutdown" });
            this.dump(proc);
            try {
                proc.waitFor();
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                proc.destroy();
            }
            MySqlDBInitializer.OUT.log(Level.INFO, "Check PID status default timeout ", MySqlDBInitializer.CHECK_PID_STATUS_TIMEOUT);
            if (!pidFile.exists()) {
                MySqlDBInitializer.OUT.log(Level.INFO, "DB server shutdown successful.");
                return;
            }
            for (int retryCount = 0; retryCount < MySqlDBInitializer.CHECK_PID_STATUS_TIMEOUT; ++retryCount) {
                if (!pidFile.exists()) {
                    MySqlDBInitializer.OUT.log(Level.INFO, "{0} file doesn't exist. DB server shutdown successful.", pidFile.getName());
                    return;
                }
                MySqlDBInitializer.OUT.log(Level.WARNING, "{0} file exist. Waiting for DB server shutdown.", pidFile.getName());
                try {
                    Thread.sleep(1000L);
                }
                catch (final InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
            MySqlDBInitializer.OUT.log(Level.SEVERE, "waited {0} secs for DB server shutdown. Unable to stop DB server. ", MySqlDBInitializer.CHECK_PID_STATUS_TIMEOUT);
        }
    }
    
    @Override
    public boolean isServerStarted() throws IOException {
        final Properties props = RelationalAPI.getInstance().getDBAdapter().getDBProps();
        MySqlDBInitializer.OUT.log(Level.FINEST, "props :: {0}", props);
        return this.isServerStarted(((Hashtable<K, Integer>)props).get("Port"), props.getProperty("Server"));
    }
    
    @Override
    public String getVersion() throws Exception {
        final String mysql = new File(Configuration.getString("db.home")).getAbsolutePath() + File.separator + "bin" + File.separator + "mysql" + (this.isWindows ? ".exe" : "");
        if (new File(mysql).exists()) {
            MySqlDBInitializer.OUT.info("Getting current version using 'mysql' binary.");
            final List<String> commandList = new ArrayList<String>();
            commandList.add(mysql);
            commandList.add("-V");
            final Process mysqlProcess = this.executeCommand(commandList, null);
            String myVersion = new BufferedReader(new InputStreamReader(mysqlProcess.getInputStream())).readLine();
            final int waitFor = mysqlProcess.waitFor();
            MySqlDBInitializer.OUT.log(Level.INFO, "mysqlProcess waitFor :: {0}", waitFor);
            mysqlProcess.destroy();
            if (myVersion != null) {
                myVersion = myVersion.substring(myVersion.indexOf("Distrib") + 7, myVersion.indexOf(",")).trim();
            }
            MySqlDBInitializer.OUT.log(Level.INFO, "Returning version {0}", myVersion);
            return myVersion;
        }
        Connection conn = null;
        String version = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            version = RelationalAPI.getInstance().getDBAdapter().getDBSystemProperty(conn, "version");
            if (version.contains("-")) {
                version = version.substring(0, version.indexOf("-"));
            }
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return version;
    }
    
    Process executeCommand(final List<String> commandList, final Properties envVariables) throws IOException {
        MySqlDBInitializer.OUT.log(Level.INFO, "Command to be executed {0} ", commandList);
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
    
    @Override
    public String getDBDataDirectory() {
        Connection conn = null;
        String dir = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            dir = RelationalAPI.getInstance().getDBAdapter().getDBSystemProperty(conn, "datadir");
        }
        catch (final Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final SQLException e2) {
                e2.printStackTrace();
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final SQLException e3) {
                e3.printStackTrace();
            }
        }
        return dir;
    }
    
    @Override
    public byte getDBArchitecture() throws Exception {
        Connection conn = null;
        byte arch = -1;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            final String version = RelationalAPI.getInstance().getDBAdapter().getDBSystemProperty(conn, "version_compile_machine");
            if (!version.contains("unknown")) {
                arch = (byte)(version.contains("64") ? 64 : 32);
            }
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return arch;
    }
    
    void invalidateCommandTemplate() {
        MySqlDBInitializer.commandTemplate.clear();
    }
    
    static {
        CLASS_NAME = MySqlDBInitializer.class.getName();
        OUT = Logger.getLogger(MySqlDBInitializer.CLASS_NAME);
        MySqlDBInitializer.isDemonStarted = false;
        MySqlDBInitializer.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        MySqlDBInitializer.commandTemplate = new ArrayList<String>();
    }
}
