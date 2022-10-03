package com.me.devicemanagement.onpremise.standalone;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.util.logging.LogManager;
import java.util.Arrays;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Logger;

public class MssqlStartupChecker
{
    private static final String MSSQLSTARTUPPIDFILE;
    private static final String SQLAUTOSTART;
    public static Logger logger;
    public static FileLock fl;
    public static FileChannel f;
    
    public static void main(final String[] args) throws Exception {
        initLogger();
        final File pidFile = new File(getServerHomeCanonicalPath() + MssqlStartupChecker.MSSQLSTARTUPPIDFILE);
        if (args.length == 0) {
            stopTask();
            return;
        }
        long retryInterval = 30000L;
        final String type = args[0];
        String password = "";
        if (type.equalsIgnoreCase("stop")) {
            stopTask();
            return;
        }
        retryInterval = Long.parseLong(args[1]);
        password = args[2];
        if (pidFile.exists() && isFileLocked(pidFile)) {
            MssqlStartupChecker.logger.info("Another instance has already been initiated. and pid id locked");
            System.exit(0);
        }
        createPIDFile();
        MssqlStartupChecker.logger.info("Inside main");
        while (true) {
            try {
                while (!checkIfSqlServerAccessible(password)) {
                    MssqlStartupChecker.logger.log(Level.INFO, "Connection not yet established");
                    Thread.sleep(retryInterval);
                }
                break;
            }
            catch (final Exception e) {
                MssqlStartupChecker.logger.log(Level.INFO, "Connection not yet established");
                Thread.sleep(retryInterval);
                continue;
            }
        }
        startProductServer();
    }
    
    public static boolean isFileLocked(final File flock) {
        boolean isLocked = false;
        try {
            isLocked = !flock.canWrite();
            MssqlStartupChecker.logger.info("file is locked : " + isLocked);
        }
        catch (final Exception ex) {
            MssqlStartupChecker.logger.info("Exception while checking file lock :: " + ex);
        }
        return isLocked;
    }
    
    private static boolean checkIfSqlServerAccessible(final String password) {
        try {
            Connection c = null;
            Statement s = null;
            ResultSet rs = null;
            try {
                final String dbConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
                final Properties props = new Properties();
                FileInputStream fis = null;
                try {
                    if (new File(dbConfFile).exists()) {
                        fis = new FileInputStream(dbConfFile);
                        props.load(fis);
                        fis.close();
                    }
                }
                catch (final Exception e) {
                    MssqlStartupChecker.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + e);
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    }
                    catch (final Exception e) {
                        MssqlStartupChecker.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + e);
                    }
                }
                finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    }
                    catch (final Exception e2) {
                        MssqlStartupChecker.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + e2);
                    }
                }
                final String dbUrl = props.getProperty("url");
                final String driver = props.getProperty("drivername");
                Class.forName(driver);
                final String username = props.getProperty("username");
                c = DriverManager.getConnection(dbUrl, username, password);
                s = c.createStatement();
                rs = s.executeQuery("SELECT 1");
                if (!rs.next()) {
                    return false;
                }
            }
            catch (final Exception e3) {
                return false;
            }
            finally {
                if (rs != null) {
                    try {
                        rs.close();
                        s.close();
                        c.close();
                    }
                    catch (final SQLException ex) {}
                }
            }
        }
        catch (final Exception ex2) {}
        return true;
    }
    
    private static void createPIDFile() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = null;
        final File file = new File(getServerHomeCanonicalPath() + MssqlStartupChecker.MSSQLSTARTUPPIDFILE);
        try {
            if (file.exists()) {
                final boolean del = file.delete();
                MssqlStartupChecker.logger.info("is file deleted : " + del);
            }
            final int pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
            MssqlStartupChecker.logger.info("pid of current task = " + pid);
            writer = new PrintWriter(getServerHomeCanonicalPath() + MssqlStartupChecker.MSSQLSTARTUPPIDFILE, "UTF-8");
            writer.println("pid:" + pid);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
        try {
            final File flock = new File(getServerHomeCanonicalPath() + MssqlStartupChecker.MSSQLSTARTUPPIDFILE);
            MssqlStartupChecker.f = new RandomAccessFile(flock, "rw").getChannel();
            MssqlStartupChecker.fl = MssqlStartupChecker.f.lock();
        }
        catch (final Exception e) {
            MssqlStartupChecker.logger.info("Exception while lovking the mssql pid file" + e);
        }
    }
    
    private static void stopTask() {
        final File pidFile = new File(getServerHomeCanonicalPath() + MssqlStartupChecker.MSSQLSTARTUPPIDFILE);
        try {
            FileInputStream fis = null;
            try {
                System.out.println("Kindly wait while we stop sql retry task.");
                if (pidFile.exists()) {
                    if (MssqlStartupChecker.fl != null) {
                        MssqlStartupChecker.fl.release();
                        MssqlStartupChecker.f.close();
                    }
                    final Properties maintenanceProps = new Properties();
                    fis = new FileInputStream(pidFile);
                    maintenanceProps.load(fis);
                    fis.close();
                    if (isPidRunning(maintenanceProps.getProperty("pid"))) {
                        terminatePID(maintenanceProps.getProperty("pid"));
                        System.out.println("SQL Retry task has been terminated.");
                    }
                    else {
                        System.out.println("SQL Retry task is not running.");
                    }
                }
                else {
                    MssqlStartupChecker.logger.info("SQL Retry task is not running.");
                    System.out.println("SQL Retry task is not running.");
                }
            }
            catch (final Exception e) {
                MssqlStartupChecker.logger.log(Level.SEVERE, "Exception in stopTask ", e);
                if (fis != null) {
                    try {
                        fis.close();
                    }
                    catch (final IOException e2) {
                        MssqlStartupChecker.logger.log(Level.SEVERE, "Exception in finally ", e2);
                    }
                }
            }
            finally {
                if (fis != null) {
                    try {
                        fis.close();
                    }
                    catch (final IOException e3) {
                        MssqlStartupChecker.logger.log(Level.SEVERE, "Exception in finally ", e3);
                    }
                }
            }
        }
        catch (final Exception e4) {
            MssqlStartupChecker.logger.log(Level.SEVERE, "");
        }
        pidFile.delete();
        System.exit(1);
    }
    
    private static void startProductServer() {
        try {
            if (MssqlStartupChecker.fl != null) {
                MssqlStartupChecker.logger.info("file is locked");
                MssqlStartupChecker.fl.release();
                MssqlStartupChecker.f.close();
            }
            for (int i = 0; i < 3; ++i) {
                boolean deleted = false;
                if (new File(getServerHomeCanonicalPath() + MssqlStartupChecker.MSSQLSTARTUPPIDFILE).exists()) {
                    MssqlStartupChecker.logger.info("file exists and going to delete it ");
                    deleted = new File(getServerHomeCanonicalPath() + MssqlStartupChecker.MSSQLSTARTUPPIDFILE).delete();
                }
                if (deleted) {
                    break;
                }
                Thread.sleep(2000L);
            }
            new File(getServerHomeCanonicalPath() + MssqlStartupChecker.SQLAUTOSTART).createNewFile();
            System.out.println("Starting product server");
            MssqlStartupChecker.logger.info("Going to start product server ");
            final ProcessBuilder builder = new ProcessBuilder(Arrays.asList("cmd.exe", "/C", "startProductServer.bat"));
            builder.directory(new File(System.getProperty("server.home") + File.separator + "bin" + File.separator));
            final Process process = builder.start();
            process.waitFor();
        }
        catch (final Exception e) {
            MssqlStartupChecker.logger.log(Level.SEVERE, "Exception startProductServer ", e);
        }
        System.exit(1);
    }
    
    private static String getServerHomeCanonicalPath() {
        String serverHome = System.getProperty("server.home");
        try {
            if (serverHome != null) {
                serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            }
        }
        catch (final Exception e) {
            MssqlStartupChecker.logger.log(Level.SEVERE, "Exception in getServerHomeCanonicalPath ", e);
        }
        return serverHome;
    }
    
    private static Properties getDefaultLogFileHandlerProps() {
        final Properties props = new Properties();
        props.setProperty("handlers", "java.util.logging.FileHandler");
        props.setProperty("java.util.logging.FileHandler.level", "INFO");
        props.setProperty("java.util.logging.FileHandler.limit", "5000");
        props.setProperty("java.util.logging.FileHandler.count", "5");
        props.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter");
        props.setProperty("java.util.logging.FileHandler.formatter", "com.me.devicemanagement.onpremise.start.util.DCLogFormatter");
        props.setProperty("java.util.logging.ConsoleHandler.level", "INFO");
        props.setProperty("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");
        props.setProperty("java.util.logging.FileHandler.append", "true");
        return props;
    }
    
    public static void initLogger(final String logFileName) {
        final Properties fileHandlerProps = getDefaultLogFileHandlerProps();
        fileHandlerProps.setProperty("java.util.logging.FileHandler.pattern", "../logs/" + logFileName + "%g.txt");
        try {
            LogManager.getLogManager().readConfiguration(getPropsAsStream(fileHandlerProps));
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private static InputStream getPropsAsStream(final Properties props) throws IOException {
        final Set set = props.entrySet();
        final Iterator iter = set.iterator();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            bos.write(entry.toString().getBytes());
            bos.write("\n".getBytes());
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bos.toByteArray());
        return byteArrayInputStream;
    }
    
    public static void initLogger() {
        final String logFileName = System.getProperty("log.filename");
        if (logFileName != null) {
            initLogger(logFileName);
        }
    }
    
    private static boolean isPidRunning(final String pid) {
        BufferedReader in = null;
        try {
            final ProcessBuilder builder = new ProcessBuilder(Arrays.asList("cmd.exe", "/C", "tasklist", "/FI", "\"PID eq " + pid + "\""));
            final Process process = builder.start();
            process.getOutputStream().close();
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains(pid)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            MssqlStartupChecker.logger.log(Level.SEVERE, "Exception getting isPidRunning in MaintenanceUtil", e);
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e2) {
                MssqlStartupChecker.logger.log(Level.SEVERE, "Exception finally block ", e2);
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e3) {
                MssqlStartupChecker.logger.log(Level.SEVERE, "Exception finally block ", e3);
            }
        }
        return false;
    }
    
    private static void terminatePID(final String pid) {
        try {
            final ProcessBuilder builder = new ProcessBuilder(Arrays.asList("cmd.exe", "/C", "Taskkill", "/PID", pid, "/F"));
            final Process process = builder.start();
            process.getOutputStream().close();
            process.waitFor();
        }
        catch (final Exception e) {
            MssqlStartupChecker.logger.log(Level.SEVERE, "Exception getting isPidRunning ", e);
        }
    }
    
    static {
        MSSQLSTARTUPPIDFILE = File.separator + "bin" + File.separator + ".mssqlpid";
        SQLAUTOSTART = File.separator + "bin" + File.separator + "sqlAutoStarted.lock";
        MssqlStartupChecker.logger = Logger.getLogger(MssqlStartupChecker.class.getName());
        MssqlStartupChecker.fl = null;
        MssqlStartupChecker.f = null;
    }
}
