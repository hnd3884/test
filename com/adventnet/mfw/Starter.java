package com.adventnet.mfw;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.Scanner;
import com.adventnet.tools.prevalent.Wield;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import com.adventnet.db.schema.analyze.DBDiffHandler;
import com.adventnet.db.migration.DBMigrationInterface;
import java.net.URLClassLoader;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;
import java.io.Console;
import java.io.RandomAccessFile;
import com.zoho.mickey.server.InformationDumpRequest;
import java.util.Locale;
import com.adventnet.mfw.logging.LoggerUtil;
import com.zoho.net.handshake.HandShakeClient;
import com.zoho.net.handshake.HandShakePacket;
import com.zoho.conf.AppResources;
import java.util.Map;
import com.zoho.mickey.server.InformationDumpListener;
import java.io.PrintStream;
import com.zoho.mickey.startup.ServerStartupHooks;
import com.adventnet.persistence.ConfigurationParser;
import org.apache.juli.ClassLoaderLogManager;
import java.util.logging.LogManager;
import java.util.logging.Level;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import com.zoho.net.handshake.HandShakeServerMessageHandler;
import com.zoho.net.handshake.HandShakeUtil;
import com.zoho.conf.Configuration;
import java.io.File;
import java.net.URLDecoder;
import java.util.logging.Logger;
import java.util.Properties;
import com.adventnet.mfw.diskutil.DiskSpaceMonitor;

public class Starter
{
    private static ServerInterface server;
    public static boolean extshutdown;
    public static boolean restart;
    public static boolean started;
    private static boolean isSafeStart;
    private static final String SERVERCLASS = "com.zoho.mickey.startup.MEServer";
    private static DiskSpaceMonitor diskSpaceMonitor;
    private static Properties startupNotifyProps;
    private static boolean isJARsLoaded;
    public static boolean isCatalinaShutdownHookDisabled;
    private static boolean isJuliShutdownHookDisabled;
    private static Logger log;
    private static Logger out;
    private static String server_home;
    private static ServerFailureHandler serverFailure;
    private static final String SERVERFAILURECLASS = "com.adventnet.mfw.ServerFailureHandlerImpl";
    
    public static Logger getLogger() {
        if (Starter.log == null) {
            Starter.log = Logger.getLogger(Starter.class.getName());
        }
        return Starter.log;
    }
    
    public static Logger getPatchUpdatorLogger() {
        if (Starter.out == null) {
            Starter.out = Logger.getLogger(PatchUpdater.class.getName());
        }
        return Starter.out;
    }
    
    public static void initialize() throws Exception {
        String path = Starter.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        path = URLDecoder.decode(path, "UTF-8");
        final File runJar = new File(path);
        final File serverFile = runJar.getParentFile().getParentFile();
        String serverPath = serverFile.getCanonicalPath();
        ConsoleOut.println("Starting Server from location: " + serverPath);
        if (isSafeStart()) {
            ConsoleOut.println("Starting Server in Safe Mode.");
        }
        Configuration.setString("server.dir", serverPath);
        serverPath = (Starter.server_home = Configuration.getString("server.dir"));
    }
    
    public static void startShutDownListener() throws IOException {
        HandShakeUtil.startHandShakeServer();
        HandShakeUtil.addMessageHandler((HandShakeServerMessageHandler)new ShutdownListener(Starter.server));
    }
    
    public static Properties getProperties(final String path) {
        if (!new File(path).exists()) {
            return null;
        }
        FileInputStream fis = null;
        final Properties props = new Properties();
        try {
            fis = new FileInputStream(path);
            props.load(fis);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return props;
    }
    
    public static void consoleOutAfterServerStartUp(final long startupTime) {
        ConsoleOut.println("");
        ConsoleOut.println("Server started in :: [" + startupTime + " ms]");
        ConsoleOut.println("");
        System.out.println("Server started in :: [" + startupTime + " ms]");
        Configuration.setString("serverstarttime", String.valueOf(startupTime));
        if (Configuration.getString("check.tomcatport", "false").equals("true")) {
            final String connectProtocol = Configuration.getString("connect.protocol", "http");
            final String clientURL = connectProtocol + "://" + Configuration.getString("bindaddress", "localhost") + ":" + Configuration.getString("port.check", Configuration.getString("http.port")) + Configuration.getString("index.page", "");
            ConsoleOut.println("Connect to: [ " + clientURL + " ]");
            if (Configuration.getString("start.webclient") != null) {
                FileOutputStream fos = null;
                try {
                    Configuration.setString("clientURL", clientURL);
                    fos = new FileOutputStream(new File(Starter.server_home + "/conf/webclient.conf"));
                    final byte[] b = clientURL.getBytes();
                    fos.write(b);
                    fos.close();
                    openWebClient();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                    if (fos != null) {
                        try {
                            fos.close();
                        }
                        catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        }
                        catch (final Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    public static void openWebClient() throws Exception {
        String clientURL = Configuration.getString("clientURL", "");
        final String fileURL = Starter.server_home + "/conf/webclient.conf";
        final File webClientFile = new File(fileURL);
        if (clientURL.equals("") && webClientFile.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(fileURL));
                if (br != null) {
                    clientURL = br.readLine();
                }
            }
            finally {
                if (br != null) {
                    try {
                        br.close();
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (Configuration.getString("start.webclient", "false").equals("true") && Configuration.getString("os.name").toLowerCase().indexOf("windows") != -1) {
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + clientURL);
            }
            catch (final Exception e2) {
                throw new RuntimeException("Error occured while trying to start web Client", e2);
            }
        }
        if (new File(Configuration.getString("start.webclient")).exists() && Configuration.getString("os.name").indexOf("Linux") != -1) {
            String browser = null;
            Properties props = new Properties();
            props = getProperties(Configuration.getString("start.webclient"));
            for (int i = 1; i <= props.size(); ++i) {
                final String checkBrowser = props.getProperty("browser" + i);
                final Process p = Runtime.getRuntime().exec(new String[] { "which", checkBrowser });
                final int status = p.waitFor();
                if (status == 0) {
                    final InputStream ip = p.getInputStream();
                    final InputStreamReader ipr = new InputStreamReader(ip);
                    final BufferedReader br2 = new BufferedReader(ipr);
                    String str;
                    while ((str = br2.readLine()) != null) {
                        browser = str;
                    }
                    break;
                }
            }
            if (browser != null) {
                browser = browser + " " + clientURL;
                Runtime.getRuntime().exec(browser);
            }
        }
    }
    
    public static ServerInterface getServerInstance() {
        return Starter.server;
    }
    
    public static void start() throws Exception {
        final long startTime = System.currentTimeMillis();
        try {
            loadSystemProperties();
            initialize();
            getLogger().log(Level.INFO, "System Properties :: {0}", Configuration.getProperties());
            checkAndChangePorts();
            addShutdownHook();
            getLogger().info("Shutdown Hook added successfully");
            final LogManager logManager = LogManager.getLogManager();
            if (logManager instanceof ClassLoaderLogManager) {
                ((ClassLoaderLogManager)logManager).setUseShutdownHook(false);
            }
            Starter.isJuliShutdownHookDisabled = true;
            getLogger().log(Level.INFO, "ClassLoaderLogManager (Juli) shutdown hook disabled..");
            Starter.server = getNewServerClassInstance();
            final String fileName = System.getProperty("server.home") + "/conf/Persistence/persistence-configurations.xml";
            final ConfigurationParser parser = new ConfigurationParser(fileName);
            final String startupHook = parser.getConfigurationValue("ServerStartupHook");
            ServerStartupHooks hook = null;
            if (startupHook != null && !startupHook.isEmpty()) {
                hook = (ServerStartupHooks)Thread.currentThread().getContextClassLoader().loadClass(startupHook).newInstance();
                hook.preStartServer();
            }
            try {
                final String productName = parser.getConfigurationValue("ProductName");
                validateLicense(productName);
            }
            catch (final Exception e) {
                getServerFailureHandler().handle(new ServerFailureException(10017, "License Validation Failed", e));
                throw e;
            }
            System.setOut(new SysLogStream(true));
            System.setErr(new SysLogStream(false));
            startShutDownListener();
            HandShakeUtil.addMessageHandler((HandShakeServerMessageHandler)new InformationDumpListener(Starter.server));
            initializeDiskSpaceMonitor();
            Starter.server.startServer(Starter.startupNotifyProps);
            final long currentTime = System.currentTimeMillis();
            Configuration.setString("serverstartedtime", String.valueOf(currentTime));
            final long startupTime = currentTime - startTime;
            consoleOutAfterServerStartUp(startupTime);
            Starter.started = true;
            getLogger().log(Level.INFO, "System Properties after server startup :: {0}", Configuration.getProperties());
            if (hook != null) {
                hook.postStartServer();
            }
        }
        catch (final Throwable e2) {
            e2.printStackTrace();
            ConsoleOut.println("\n\nProblem while Starting Server");
            getLogger().log(Level.SEVERE, "Problem while Starting Server  ", e2);
            System.exit(-1);
        }
    }
    
    public static void initializeDiskSpaceMonitor() throws Exception {
        if (isDiskSpaceMonitoringEnabled()) {
            (Starter.diskSpaceMonitor = new DiskSpaceMonitor()).createDiskSpaceScheduleFromProps(Configuration.getProperties());
            getLogger().log(Level.FINE, "Initialized DiskSpaceMonitor in Starter");
        }
    }
    
    public static void shutDownDiskSpaceMonitor() {
        if (Starter.diskSpaceMonitor != null) {
            Starter.diskSpaceMonitor.cancelDiskSpaceScheduler();
        }
    }
    
    public static boolean isDiskSpaceMonitoringEnabled() {
        return Configuration.getString("diskcheck.enable", "true").equalsIgnoreCase("true");
    }
    
    public static void loadSystemProperties() {
        Properties properties = getProperties(Starter.server_home + "/conf/system_properties.conf");
        if (properties == null) {
            properties = getProperties(Starter.server_home + "/conf/systemproperties.conf");
        }
        if (properties != null) {
            final String oldValue = System.getProperty("gen.db.password");
            if (oldValue != null) {
                properties.setProperty("gen.db.password", oldValue);
            }
            properties.putAll(Configuration.getProperties());
            Configuration.setProperties(properties);
        }
        try {
            AppResources.setProperties(System.getProperties());
            AppResources.setProperty("line.separator", System.getProperty("line.separator"));
            final File sysprops = new File(Starter.server_home + "/conf/system_properties.conf");
            if (sysprops.exists()) {
                AppResources.load("system_properties.conf");
            }
            final File appResfile = new File(Starter.server_home + "/conf/app.properties");
            if (appResfile.exists()) {
                AppResources.load("app.properties");
            }
        }
        catch (final IOException ioe) {
            Starter.out.info("Problem while loading AppResources!!!" + ioe);
            ioe.printStackTrace();
        }
    }
    
    public static void getServerStatus() {
        HandShakePacket pingMessage = null;
        try {
            final HandShakeClient hsc = HandShakeUtil.getHandShakeClient();
            if (hsc == null) {
                ConsoleOut.println("Received response message : Server is not running");
                return;
            }
            pingMessage = hsc.getPingMessage("PING");
        }
        catch (final IOException e) {
            e.printStackTrace();
            getLogger().warning("Exception while get PING status(IO Exception). " + e.getMessage());
            ConsoleOut.println("Exception while get PING status(IO Exception). " + e.getMessage());
        }
        catch (final ClassNotFoundException e2) {
            getLogger().warning("Exception while get PING status(ClassNotFoundException). " + e2.getMessage());
            ConsoleOut.println("Exception while get PING status(ClassNotFoundException). " + e2.getMessage());
        }
        getLogger().info("Received response message :: " + pingMessage.toString());
        ConsoleOut.println("Received response message :: " + pingMessage.toString());
    }
    
    public static void main(final String[] args) throws Exception {
        Configuration.setString("server.home", (null != Configuration.getString("server_home")) ? Configuration.getString("server_home") : Configuration.getString("server.home"));
        Configuration.setString("app.home", Configuration.getString("server.home"));
        Configuration.setString("server.dir", Configuration.getString("server.home"));
        String javaArg = null;
        if (args.length >= 1) {
            javaArg = args[0];
            Starter.isSafeStart = Boolean.parseBoolean(javaArg);
        }
        if (!Starter.isSafeStart && "DBMigration".equals(javaArg)) {
            startMigration(Configuration.getString("db.migration.main.class", "com.adventnet.db.migration.DBMigration"), args);
        }
        if (!Starter.isSafeStart && "SchemaAnalyzer".equals(javaArg)) {
            analyzeSchema(Configuration.getString("schema.analyzer.main.class", "com.adventnet.db.schema.analyze.DBDiffTool"), args);
        }
        else if (!Starter.isSafeStart && "runStandalone".equals(javaArg)) {
            final String loggerName = System.getProperty("standalone.logger.name", "runStandAlone");
            System.setProperty("gen.db.password", "false");
            LoggerUtil.initLog(loggerName);
            String[] cmdArgs = null;
            if (args.length > 2) {
                cmdArgs = new String[args.length - 1];
                System.arraycopy(args, 1, cmdArgs, 0, args.length - 1);
            }
            runStandAlone(cmdArgs);
        }
        else if ("reinitDB".equals(javaArg)) {
            LoggerUtil.initLog("reinitialize");
            String forceFul = "false";
            if (args.length == 2) {
                forceFul = args[1];
            }
            ReinitializeDB.reinitDB(forceFul);
        }
        else if ("backupDB".equals(javaArg)) {
            System.setProperty("gen.db.password", "false");
            LoggerUtil.initLog("backup");
            if (args.length % 2 == 1) {
                final Properties p = new Properties();
                for (int i = 1; i < args.length; i += 2) {
                    p.setProperty(args[i].toLowerCase(Locale.ENGLISH), args[i + 1]);
                }
                BackupDB.backup(p);
            }
            else {
                BackupDB.showHelpMessage();
            }
        }
        else if ("restoreDB".equals(javaArg)) {
            LoggerUtil.initLog("restore");
            if (args.length == 1) {
                ConsoleOut.println("File to be restored is not specified");
                System.exit(1);
            }
            final String restoreFile = args[1];
            String password = null;
            if (args.length == 4) {
                if (args[2].equals("-p")) {
                    password = args[3];
                }
            }
            else if (args.length == 3 && args[2].equals("-p")) {
                final Console console = System.console();
                if (console == null) {
                    ConsoleOut.println("Password for the file to be restored is not specified");
                    System.exit(1);
                }
                else {
                    char[] passwordArray;
                    while (true) {
                        passwordArray = console.readPassword("Enter Password : ", new Object[0]);
                        if (passwordArray.length > 0) {
                            break;
                        }
                        console.printf("Password cannot be empty\n", new Object[0]);
                    }
                    password = new String(passwordArray);
                }
            }
            RestoreDB.restoreDB(restoreFile, password);
        }
        else if ("startDB".equals(javaArg)) {
            System.setProperty("gen.db.password", "false");
            LoggerUtil.initLog("startDB");
            StartDB.startDBServer();
        }
        else if ("stopDB".equals(javaArg)) {
            LoggerUtil.initLog("stopDB");
            StopDB.stopDBServer();
        }
        else if ("shutdown".equals(javaArg)) {
            LoggerUtil.initLog("shutdown");
            String host = "localhost";
            if (args.length > 1) {
                host = args[1];
            }
            Shutdown.shutdown(host);
        }
        else if ("serverStatus".equals(javaArg)) {
            getServerStatus();
        }
        else if ("infodump".equals(javaArg)) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; ++i) {
                sb.append(args[i]).append(' ');
            }
            InformationDumpRequest.threadDump("localhost", sb.toString().trim());
        }
        else {
            final int retryLock = Configuration.getInteger("lock.retry", 15);
            final File f = new File("lockfile");
            final FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
            FileLock lock = null;
            int index;
            for (index = 0, index = 0; index < retryLock && (lock = fc.tryLock()) == null; ++index) {
                Thread.sleep(200L);
            }
            if (index == retryLock || !checkShutdownListenerPort()) {
                final String msg = "Already Server seems to be running";
                ConsoleOut.println(msg);
                ConsoleOut.println("Error Code : 10000");
                getServerFailureHandler().handle(new ServerFailureException(10000, "Server Instance Already Running"));
                getLogger().info("Already another server instance running : error code : 10000");
                System.exit(-1);
            }
            if (LoggerUtil.reloadLoggingProperties()) {
                Starter.log = null;
            }
            start();
        }
    }
    
    public static ServerFailureHandler getServerFailureHandler() throws Exception {
        if (Starter.serverFailure == null) {
            final URLClassLoader ucl = (URLClassLoader)Thread.currentThread().getContextClassLoader();
            final String serverFailureClassStr = Configuration.getString("serverFailure.class", "com.adventnet.mfw.ServerFailureHandlerImpl");
            final Class serverFailureClass = ucl.loadClass(serverFailureClassStr);
            Starter.serverFailure = serverFailureClass.newInstance();
        }
        return Starter.serverFailure;
    }
    
    public static void startMigration(final String className, final String[] args) throws Exception {
        loadSystemProperties();
        final String logFileName = "db_migration_" + ((args.length > 1) ? args[1] : "") + "_";
        LoggerUtil.initLog(logFileName);
        initialize();
        LoadJars();
        getLogger().info("db.migration.main.class ::: " + className);
        final DBMigrationInterface dbm = (DBMigrationInterface)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
        dbm.migrateDB((args.length > 1) ? args[1] : null, (args.length > 2) ? args[2] : null);
    }
    
    public static void analyzeSchema(final String className, final String[] args) throws Exception {
        loadSystemProperties();
        final String logFileName = "schema_analyzer_" + ((args.length > 1) ? args[1] : "") + "_";
        LoggerUtil.initLog(logFileName);
        initialize();
        LoadJars();
        getLogger().info("schema.analyzer.main.class ::: " + className);
        final DBDiffHandler analyzer = (DBDiffHandler)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
        if (args.length == 1) {
            analyzer.compareSchemaVsTableDef();
        }
        else if (args.length == 3) {
            analyzer.compareSchema(args[1], args[2]);
        }
        else {
            ConsoleOut.println("Improper Usage!!");
            System.exit(0);
        }
    }
    
    public static void runStandAlone() throws Exception {
        runStandAlone(null);
    }
    
    public static void runStandAlone(final String[] args) throws Exception {
        LoadJars();
        getNewServerClassInstance().runStandAlone(args);
    }
    
    public static boolean isSafeStart() {
        return Starter.isSafeStart;
    }
    
    public static void LoadJars() throws Exception {
        if (!Starter.isJARsLoaded) {
            final List<URL> jarsList = getAllJarURLs();
            getLogger().log(Level.FINE, "The list jars to be added to UCL are {0}", jarsList);
            final URLClassLoader ucl = new URLClassLoader(jarsList.toArray(new URL[0]));
            Thread.currentThread().setContextClassLoader(ucl);
            Starter.isJARsLoaded = true;
        }
        else {
            getLogger().log(Level.INFO, "LoadJARs already called. JARs have been loaded in previous server initialization");
        }
    }
    
    private static List<URL> getAllJarURLs() throws Exception {
        final String classpathFile = Starter.server_home + File.separator + "conf" + File.separator + "classpath.conf";
        FileInputStream fis = null;
        final List<URL> jarsToLoad = new ArrayList<URL>();
        final Properties cpProps = new Properties();
        final File source = new File(Starter.server_home + File.separator + "fixes");
        final File destn = new File(Starter.server_home + File.separator + "lib" + File.separator + "fix");
        final File patch = new File(destn.getPath() + File.separator + "patch.jar");
        if (!destn.exists()) {
            getPatchUpdatorLogger().log(Level.INFO, "is lib/fix directory created : " + destn.mkdir());
        }
        if (patch.exists()) {
            if (!patch.delete()) {
                throw new RuntimeException("lib" + File.separator + "fix" + File.separator + "patch.jar cannot be DELETED. Check whether it is opened or it is used by any other program");
            }
            getPatchUpdatorLogger().log(Level.INFO, "old patch.jar is deleted SUCCESSFULLY");
        }
        final File webapps = new File(Starter.server_home + File.separator + "webapps");
        File[] contexts = null;
        if (webapps.exists()) {
            final File[] listFiles;
            contexts = (listFiles = webapps.listFiles());
            for (final File context : listFiles) {
                final File dir = new File(context.getPath() + File.separator + "WEB-INF" + File.separator + "classes");
                if (dir.exists()) {
                    try {
                        PatchUpdater.deleteAllClassFiles(dir);
                    }
                    catch (final Exception e) {
                        throw new RuntimeException(dir.getPath() + File.separator + "classes directory cannot be DELETED. Check whether it is opened or it is used by any other program");
                    }
                }
            }
        }
        else {
            getPatchUpdatorLogger().log(Level.WARNING, "webapps directory is missing. Hence the patch(es) for web application(s) will be SKIPPED");
        }
        if (source.exists()) {
            final File[] srcFiles = source.listFiles();
            if (srcFiles.length > 0) {
                final List<String> contextList = new ArrayList<String>();
                contextList.add("lib_fix");
                if (contexts != null & contexts.length > 0) {
                    for (final File context2 : srcFiles) {
                        final String fileName = context2.getName();
                        if (fileName.endsWith(".wjar") && fileName.contains("_")) {
                            final String cntxt = fileName.split("_")[0];
                            if (!contextList.contains(cntxt)) {
                                contextList.add(cntxt);
                            }
                        }
                    }
                }
                PatchUpdater.createPatchToLoad(source, patch, contextList);
                if (patch.exists()) {
                    getPatchUpdatorLogger().info("PatchUpdater - Going to add jar from Directory lib/" + destn.getName());
                    getPatchUpdatorLogger().log(Level.FINEST, "Adding URL for jar file :: {0} to classpath", patch.getAbsoluteFile());
                    jarsToLoad.add(patch.toURI().toURL());
                    getPatchUpdatorLogger().log(Level.FINEST, "Adding URL for jar file :: {0} to classpath", destn.getAbsoluteFile());
                    jarsToLoad.add(destn.toURI().toURL());
                }
                else {
                    getPatchUpdatorLogger().log(Level.WARNING, "patch.jar is missing and Patch Loading is skipped. Hence Product loads the jar(s) without PATCH");
                }
            }
            else {
                getPatchUpdatorLogger().log(Level.WARNING, "The 'fixes' directory is EMPTY. Product loads the jar(s) without PATCH");
            }
        }
        else {
            getPatchUpdatorLogger().log(Level.WARNING, "The 'fixes' directory is missing. Product loads the jar(s) without PATCH");
        }
        try {
            fis = new FileInputStream(new File(classpathFile));
            cpProps.load(fis);
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        final Enumeration<?> e2 = cpProps.propertyNames();
        while (e2.hasMoreElements()) {
            final String dirName = cpProps.getProperty((String)e2.nextElement());
            final String dirPath = Configuration.getString("server.home") + File.separator + dirName;
            addJars(dirPath, jarsToLoad);
        }
        if (Configuration.getString("external.lib.path") != null) {
            final String externalLib = Configuration.getString("external.lib.path");
            addJars(externalLib, jarsToLoad);
        }
        return jarsToLoad;
    }
    
    private static void addJars(final String dirPath, final List<URL> jarsToLoad) throws MalformedURLException {
        getLogger().log(Level.INFO, "Going to add jars from Directory : {0} to classpath", dirPath);
        final File jarsList = new File(dirPath);
        final File[] toLoad = jarsList.listFiles();
        if (toLoad != null) {
            for (final File file : toLoad) {
                if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
                    getLogger().log(Level.FINEST, "Adding URL for jar file :: {0} to classpath", file.getAbsoluteFile());
                    jarsToLoad.add(file.toURI().toURL());
                }
                if (file.isDirectory()) {
                    getLogger().log(Level.FINEST, "Adding URL for directory :: {0} to classpath", file.getAbsoluteFile());
                    jarsToLoad.add(new File(file.getPath() + '/').toURI().toURL());
                }
            }
        }
    }
    
    public static void validateLicense(final String productName) throws Exception {
        final Wield wield = Wield.getInstance();
        wield.applyLicense("false", productName, (String)null);
    }
    
    public static void handleDisabledShutdownHooks() {
        if (Starter.isJuliShutdownHookDisabled) {
            Starter.log.log(Level.INFO, "Handling juli shutdown hook");
            final LogManager logManager = LogManager.getLogManager();
            if (logManager instanceof ClassLoaderLogManager) {
                Starter.log.log(Level.INFO, "Shutting down log manager..resetting bufferes");
                ((ClassLoaderLogManager)logManager).shutdown();
            }
            Starter.isJuliShutdownHookDisabled = false;
        }
    }
    
    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    if (Starter.extshutdown) {
                        ConsoleOut.println("System halted");
                    }
                    else {
                        ConsoleOut.println("System going to Shutdown --- received process interrupt");
                        Starter.server.shutDown(true);
                    }
                }
                catch (final Exception e) {
                    ConsoleOut.println("Problem during shutting down refer logs");
                }
                finally {
                    Starter.handleDisabledShutdownHooks();
                }
            }
        });
    }
    
    private static void checkFreePortFromList(final int actualPort, final List portList, final String key, final Properties newPorts, final List occupiedPorts) {
        String altPorts = "";
        final String portName = newPorts.getProperty(key + ".name");
        if (isPortFree(actualPort)) {
            newPorts.setProperty(key, actualPort + "");
        }
        else {
            for (int i = 0; i < portList.size(); ++i) {
                final int port = Integer.valueOf(portList.get(i));
                if (isPortFree(port)) {
                    newPorts.setProperty(key, port + "");
                    portList.remove(port + "");
                    break;
                }
            }
        }
        int port2 = actualPort;
        if (newPorts.getProperty(key) == null) {
            altPorts = actualPort + ",";
            portList.remove(actualPort + "");
            port2 = getFreePort(actualPort, null, portName, occupiedPorts);
            newPorts.setProperty(key, port2 + "");
        }
        if (newPorts.getProperty(key) == null) {
            throw new IllegalArgumentException("Port not free for :: [" + key + "]");
        }
        occupiedPorts.add(port2 + "");
        for (int j = 0; j < portList.size(); ++j) {
            altPorts = altPorts + portList.get(j) + ((j == portList.size() - 1) ? "" : ",");
        }
        if (portList.size() > 0) {
            newPorts.setProperty("alternate." + key, altPorts);
        }
    }
    
    private static void checkAndChangePorts() throws Exception {
        final String portsFileStr = Starter.server_home + "/conf/ports.properties";
        final Properties portProps = getProperties(portsFileStr);
        if (new File(portsFileStr).exists()) {
            OutputStream fos = null;
            try {
                final Properties newPortProps = new Properties();
                final List occupiedPorts = new ArrayList();
                Enumeration e = portProps.keys();
                while (e.hasMoreElements()) {
                    final String key = e.nextElement();
                    if (portProps.getProperty(key).equals("")) {
                        throw new IllegalArgumentException("Value not Specified For key [" + key + "] in port.porperties.");
                    }
                }
                e = portProps.keys();
                while (e.hasMoreElements()) {
                    final String key = e.nextElement();
                    if (!key.toLowerCase().startsWith("alternate.") && !key.toLowerCase().endsWith(".name")) {
                        if (!portProps.getProperty(key + ".name", "not set").equals("not set")) {
                            newPortProps.setProperty(key + ".name", portProps.getProperty(key + ".name"));
                        }
                        final String alternateKey = "alternate." + key;
                        final List altPortsList = new ArrayList();
                        final int port = Integer.valueOf(portProps.getProperty(key));
                        if (portProps.getProperty(alternateKey) != null) {
                            final String altPortStr = portProps.getProperty("alternate." + key);
                            final StringTokenizer stk = new StringTokenizer(altPortStr, ",", false);
                            while (stk.hasMoreTokens()) {
                                final String tok = stk.nextToken();
                                altPortsList.add(tok);
                            }
                        }
                        checkFreePortFromList(port, altPortsList, key, newPortProps, occupiedPorts);
                    }
                }
                fos = new FileOutputStream(new File(portsFileStr));
                newPortProps.store(fos, "Ports");
            }
            finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }
    
    public static void checkAndChangeMysqlPort(final String fileNameWithAbsolutePath, final int newport) {
        try {
            final File mysqldsPath = new File(fileNameWithAbsolutePath);
            if (!mysqldsPath.exists()) {
                throw new FileNotFoundException("Specified File Not Found :: [" + fileNameWithAbsolutePath + "]");
            }
            final StringBuilder buffer = new StringBuilder();
            BufferedReader br = null;
            BufferedWriter bw = null;
            try {
                br = new BufferedReader(new FileReader(mysqldsPath));
                for (String str = br.readLine(); str != null; str = br.readLine()) {
                    final Pattern pat = Pattern.compile("(.*url=.*)(jdbc.*):([0-9]*)(.*)");
                    final Matcher mat = pat.matcher(str);
                    if (mat.matches()) {
                        int port = Integer.parseInt(mat.group(3));
                        final String portString = ":" + port;
                        if (newport == -1) {
                            final boolean isAvailable = isPortFree(port);
                            if (!isAvailable) {
                                port = getAvailablePort(port);
                            }
                        }
                        final String newPortString = ":" + ((newport == -1) ? port : newport);
                        buffer.append(str.replaceAll(portString, newPortString) + "\n");
                    }
                    else {
                        buffer.append(str);
                        buffer.append("\n");
                    }
                }
                bw = new BufferedWriter(new FileWriter(mysqldsPath));
                bw.write(buffer.toString());
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
            finally {
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
            }
        }
        catch (final Exception ex2) {
            getLogger().log(Level.SEVERE, ex2.getMessage(), ex2);
        }
    }
    
    public static boolean checkShutdownListenerPort() {
        return !HandShakeUtil.isServerListening();
    }
    
    public static int getFreePort(final int port, final Scanner scanner) {
        return getFreePort(port, scanner, null, new ArrayList());
    }
    
    private static int getFreePort(final int port, Scanner scanner, String portName, final List occupiedPorts) {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        if (isPortFree(port) && !occupiedPorts.contains(port + "")) {
            return port;
        }
        if (portName == null || portName.equals("")) {
            portName = "the server";
        }
        int newport = port;
        do {
            newport = getAvailablePort(newport);
        } while (occupiedPorts.contains(newport + ""));
        ConsoleOut.println("\nThe port " + port + " required for starting " + portName + " is already being used by another application");
        ConsoleOut.print("Please <enter> to proceed with this port " + newport + " [or] enter a valid port [or] Press e/E to Exit : ");
        if (scanner.hasNextLine()) {
            final String input = scanner.nextLine();
            if (input != null) {
                if (input.trim().equals("")) {
                    return newport;
                }
                try {
                    int newport2 = -1;
                    try {
                        newport2 = Integer.parseInt(input.trim());
                    }
                    catch (final NumberFormatException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                    return getFreePort(newport2, scanner, portName, occupiedPorts);
                }
                catch (final Exception ex) {
                    getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                    return getFreePort(port, scanner, portName, occupiedPorts);
                }
            }
        }
        return newport;
    }
    
    public static boolean isPortFree(final int port) {
        final String bindAddress = Configuration.getString("bindaddress");
        return isPortFree(port, bindAddress);
    }
    
    private static boolean isPortFree(final int port, final String bindAddress) {
        if (port <= 0) {
            return false;
        }
        ServerSocket sock = null;
        try {
            if (bindAddress == null) {
                sock = new ServerSocket(port);
            }
            else {
                sock = new ServerSocket(port, 0, InetAddress.getByName(bindAddress));
            }
        }
        catch (final Exception ex) {
            return false;
        }
        finally {
            if (sock != null) {
                try {
                    sock.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    
    public static int getAvailablePort(int port) {
        if (port <= 0) {
            port = 1024;
        }
        while (!isPortFree(++port)) {}
        return port;
    }
    
    public static void changePort(final String portsFileStrWithAbsolutePath, final String portName, final int newPort) throws Exception {
        final File portFile = new File(portsFileStrWithAbsolutePath);
        if (!portFile.exists()) {
            throw new FileNotFoundException("The given file :: [" + portFile + "] doesnot exists");
        }
        FileOutputStream fos = null;
        FileInputStream fis = null;
        Properties portProps = new Properties();
        try {
            fis = new FileInputStream(portFile);
            portProps = new Properties();
            portProps.load(fis);
            portProps.setProperty(portName, newPort + "");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        try {
            fos = new FileOutputStream(portFile);
            portProps.store(fos, "ports");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
    
    public static boolean isStarted() {
        return Starter.started;
    }
    
    private static String buildString(final String... strArray) {
        final StringBuilder strBuff = new StringBuilder();
        for (final String string : strArray) {
            strBuff.append(string);
        }
        return strBuff.toString();
    }
    
    public static ServerInterface getNewServerClassInstance() throws Exception {
        LoadJars();
        if (Starter.server == null) {
            final URLClassLoader ucl = (URLClassLoader)Thread.currentThread().getContextClassLoader();
            final String serverClassStr = Configuration.getString("server.class", "com.zoho.mickey.startup.MEServer");
            final Class serverClass = ucl.loadClass(serverClassStr);
            return serverClass.newInstance();
        }
        getLogger().log(Level.INFO, "Server instance already created.");
        return getServerInstance();
    }
    
    public static ServerInterface getNewServerClassInstanceForWar() throws Exception {
        if (Starter.server == null) {
            final URLClassLoader ucl = (URLClassLoader)Thread.currentThread().getContextClassLoader();
            final String serverClassStr = Configuration.getString("server.class", "com.zoho.mickey.startup.MEServer");
            final Class serverClass = ucl.loadClass(serverClassStr);
            return serverClass.newInstance();
        }
        getLogger().log(Level.INFO, "Server instance already created.");
        return getServerInstance();
    }
    
    static {
        Starter.server = null;
        Starter.extshutdown = false;
        Starter.restart = false;
        Starter.started = false;
        Starter.isSafeStart = false;
        Starter.diskSpaceMonitor = null;
        Starter.startupNotifyProps = new Properties();
        Starter.isJARsLoaded = false;
        Starter.isCatalinaShutdownHookDisabled = false;
        Starter.isJuliShutdownHookDisabled = false;
        Starter.log = null;
        Starter.out = null;
        Starter.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : ((Configuration.getString("server_home") != null) ? Configuration.getString("server_home") : Configuration.getString("app.home")));
        Starter.serverFailure = null;
    }
    
    public static class SysLogStream extends PrintStream
    {
        Logger logger;
        
        public SysLogStream(final boolean isOut) throws Exception {
            super(System.err);
            this.logger = null;
            this.logger = (isOut ? Logger.getLogger("SYSOUT") : Logger.getLogger("SYSERR"));
        }
        
        @Override
        public void println(final String message) {
            this.log(message);
        }
        
        @Override
        public void println(final Object message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final long message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final int message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final float message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final double message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final char[] message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final char message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final boolean message) {
            this.log(message ? "true" : "false");
        }
        
        @Override
        public void println() {
        }
        
        @Override
        public void print(final String message) {
            this.log(message);
        }
        
        @Override
        public void print(final Object message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final long message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final int message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final float message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final double message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final char[] message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final char message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final boolean message) {
            this.log(message ? "true" : "false");
        }
        
        @Override
        public void flush() {
        }
        
        @Override
        public void close() {
        }
        
        @Override
        public boolean checkError() {
            return false;
        }
        
        @Override
        protected void setError() {
        }
        
        @Override
        public void write(final int b) {
            this.log(String.valueOf(b));
        }
        
        @Override
        public void write(final byte[] buf, final int off, final int len) {
            this.log(new String(buf, off, len));
        }
        
        private void log(final String logMessage) {
            this.logger.log(Level.INFO, logMessage);
        }
    }
}
