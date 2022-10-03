package com.me.devicemanagement.onpremise.server.util;

import com.adventnet.persistence.DataAccess;
import java.util.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.onpremise.server.service.DMOnPremiseService;
import com.me.devicemanagement.onpremise.server.fos.FosUtil;
import com.adventnet.persistence.fos.FOS;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.util.Map;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.onpremise.webclient.dblock.CleanDbLockFiles;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.cache.CacheManager;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import java.util.Collection;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.tools.prevalent.ConsoleOut;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.onpremise.server.metrack.METrackerHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.tools.prevalent.Details;
import com.adventnet.tools.prevalent.User;
import com.adventnet.tools.prevalent.DataClass;
import com.adventnet.tools.prevalent.InputFileParser;
import com.adventnet.tools.prevalent.Wield;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.me.devicemanagement.onpremise.webclient.support.UploadAction;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.logging.Handler;
import java.util.List;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.winaccess.WmiAccessProvider;
import java.io.File;
import java.util.logging.Logger;

public class SyMUtil extends com.me.devicemanagement.framework.server.util.SyMUtil
{
    private static Logger logger;
    private static int httpPort;
    private static int httpnioPort;
    private static int sslPort;
    private static final String DC_TEMP = "dc-temp";
    private static final String SERVER_STARTTIME_FILE;
    
    public static String getInstallationDate() {
        try {
            String insDate = getSyMParameter("installation-date");
            if (insDate == null) {
                insDate = getInstallationProperty("it");
                insDate = getDate((long)new Long(insDate));
                updateSyMParameter("installation-date", insDate);
            }
            if (insDate != null) {
                return insDate;
            }
            final String fname = "wrapper.log";
            final File file = new File(fname);
            if (file.exists()) {
                final long lastModified = file.lastModified();
                insDate = getDate((long)new Long(lastModified));
                updateSyMParameter("installation-date", insDate);
                return insDate;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
    
    public static void findSystemHWType() {
        String systemHWType = "general";
        Hashtable systemProperties = null;
        final String systemHWTypeFromDB = getServerParameter("SYSTEM_HW_TYPE");
        if (systemHWTypeFromDB == null) {
            if (isAzureMachineFromScript()) {
                systemHWType = "azure_virtual";
            }
            else {
                try {
                    systemProperties = WmiAccessProvider.getInstance().getSystemProperties();
                    if (systemProperties != null) {
                        SyMUtil.logger.log(Level.INFO, "System Information :" + systemProperties.toString());
                        final String systemModel = systemProperties.get("SYSTEM_MODEL_NAME").toString().toLowerCase();
                        final String systemVendor = systemProperties.get("SYSTEM_VENDOR").toString().toLowerCase();
                        final String systemVersion = systemProperties.get("SYSTEM_VERSION").toString().toLowerCase();
                        if (systemVersion.contains("amazon") && systemModel.contains("hvm domu")) {
                            systemHWType = "amazon_virtual";
                        }
                        else {
                            systemHWType = "general";
                        }
                    }
                }
                catch (final Exception e) {
                    SyMUtil.logger.log(Level.WARNING, "Exception while retrieving system properties through jni", e);
                }
            }
            com.me.devicemanagement.framework.server.util.SyMUtil.updateServerParameter("SYSTEM_HW_TYPE", systemHWType);
            System.setProperty("SYSTEM_HW_TYPE", systemHWType);
        }
        else {
            systemHWType = systemHWTypeFromDB;
        }
        SyMUtil.logger.log(Level.INFO, "System Hardware Type " + systemHWType);
    }
    
    public static Boolean isAzureMachineFromScript() {
        BufferedReader reader = null;
        BufferedReader errReader = null;
        String isAzure = "false";
        try {
            final String osArch = getDCOSArchitecture();
            SyMUtil.logger.log(Level.INFO, "OS Architecture: " + osArch);
            if (osArch != null && osArch.equalsIgnoreCase("64-bit")) {
                try {
                    final String serverHome = System.getProperty("server.home");
                    final String confirmAzureBatchFile = serverHome + File.separator + "bin" + File.separator + "scripts" + File.separator + "Confirm-AzureVM.bat";
                    final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { confirmAzureBatchFile });
                    SyMUtil.logger.log(Level.INFO, "Command to be executed: " + processBuilder.command());
                    final Process process = processBuilder.start();
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    SyMUtil.logger.log(Level.INFO, "Closing the output stream...");
                    process.getOutputStream().close();
                    String line = null;
                    if ((line = reader.readLine()) != null) {
                        isAzure = line;
                    }
                    final StringBuilder error = new StringBuilder();
                    while ((line = errReader.readLine()) != null) {
                        error.append(line);
                    }
                    if (error.length() > 0) {
                        SyMUtil.logger.log(Level.INFO, "process Error: " + (Object)error);
                    }
                    reader.close();
                    errReader.close();
                    process.waitFor();
                    SyMUtil.logger.log(Level.INFO, "process Output: " + isAzure);
                    SyMUtil.logger.log(Level.INFO, "exitStatus: " + process.exitValue());
                }
                catch (final Exception e) {
                    SyMUtil.logger.log(Level.INFO, "Ignore the following exception if its non Azure machine");
                    SyMUtil.logger.log(Level.WARNING, "Caught error while executing powershell script for Azure: " + e);
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    }
                    catch (final Exception ex) {
                        SyMUtil.logger.log(Level.INFO, "Caught error while Closing the BufferedReader input Stream" + ex);
                    }
                    try {
                        if (errReader != null) {
                            reader.close();
                        }
                    }
                    catch (final Exception ex) {
                        SyMUtil.logger.log(Level.INFO, "Caught error while Closing the BufferedReader error Stream" + ex);
                    }
                }
                finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    }
                    catch (final Exception ex2) {
                        SyMUtil.logger.log(Level.INFO, "Caught error while Closing the BufferedReader input Stream" + ex2);
                    }
                    try {
                        if (errReader != null) {
                            reader.close();
                        }
                    }
                    catch (final Exception ex2) {
                        SyMUtil.logger.log(Level.INFO, "Caught error while Closing the BufferedReader error Stream" + ex2);
                    }
                }
                SyMUtil.logger.log(Level.INFO, "isAzureMachine: " + isAzure);
            }
            else {
                SyMUtil.logger.log(Level.INFO, "Not a 64-bit machine hence no need to verify whether its azure");
            }
        }
        catch (final Exception ex3) {
            SyMUtil.logger.log(Level.WARNING, "Exception while reading flag from DB", ex3);
        }
        SyMUtil.logger.log(Level.INFO, "isAzureMachine: " + isAzure);
        return Boolean.valueOf(isAzure);
    }
    
    public static String getLogsDir() throws Exception {
        return getInstallationDir() + File.separator + "logs";
    }
    
    public static void renewLogLevel() {
        try {
            final String level = getSyMParameter("LogLevel");
            if (level != null) {
                setLogLevel(level);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while renewing log levels.", ex);
        }
    }
    
    public static void changeLogLevel(final String level) throws Exception {
        if (level == null) {
            return;
        }
        try {
            setLogLevel(level);
            final String dblevel = getSyMParameter("LogLevel");
            if (dblevel == null || !dblevel.equalsIgnoreCase(level)) {
                updateSyMParameter("LogLevel", level);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while changing log level to: " + level, ex);
            throw ex;
        }
    }
    
    private static void setLogLevel(String level) {
        if (level == null) {
            return;
        }
        if (level.equalsIgnoreCase("NORMAL")) {
            level = "INFO";
        }
        else if (level.equalsIgnoreCase("DEBUG")) {
            level = "ALL";
        }
        final List loggerNames = getLoggerList();
        SyMUtil.logger.log(Level.INFO, "Going to set Log Level: " + level);
        if (loggerNames != null && level != null) {
            final Level l = Level.parse(level);
            for (int lsize = loggerNames.size(), j = 0; j < lsize; ++j) {
                final String loggerName = loggerNames.get(j);
                try {
                    final Logger log = Logger.getLogger(loggerName);
                    final Handler[] handlers = log.getHandlers();
                    if (handlers != null) {
                        for (int i = 0; i < handlers.length; ++i) {
                            final Handler temp = handlers[i];
                            temp.setLevel(l);
                        }
                    }
                    log.setLevel(Level.parse(level));
                    final Field field = Logger.class.getDeclaredField("kids");
                    field.setAccessible(true);
                    final ArrayList list = (ArrayList)field.get(log);
                    if (list != null) {
                        for (final WeakReference ref : list) {
                            final Logger kid = (Logger)ref.get();
                            kid.setLevel(l);
                        }
                    }
                    if (handlers.length == 0) {
                        Logger.getLogger("").getHandlers()[0].setLevel(l);
                        log.addHandler(Logger.getLogger("").getHandlers()[0]);
                        log.setUseParentHandlers(false);
                    }
                }
                catch (final Exception exp) {
                    exp.printStackTrace();
                }
            }
        }
    }
    
    private static List getLoggerList() {
        SyMUtil.logger.log(Level.INFO, "Going to parse logging.properties file to get the list of logger");
        List<String> loggerList = null;
        final Properties loggingProperties = new Properties();
        final String filePath = System.getProperty("java.util.logging.config.file", "../conf/logging.properties");
        try {
            loggingProperties.load(new FileInputStream(filePath));
            final String handlers = loggingProperties.getProperty("handlers");
            final String[] loggerNameList = handlers.split(",");
            for (int i = 0; i < loggerNameList.length; ++i) {
                String loggerName = loggerNameList[i];
                loggerName = loggerName.substring(0, loggerName.indexOf("."));
                loggerNameList[i] = removeNumbersInFront(loggerName);
            }
            loggerList = Arrays.asList(loggerNameList);
            SyMUtil.logger.log(Level.INFO, "Finished parsing logging.properties file");
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while parsing logging.properties file : ", ex);
        }
        return loggerList;
    }
    
    private static String removeNumbersInFront(String loggerName) {
        try {
            int loggerNameLength;
            int index;
            for (loggerNameLength = loggerName.length(), index = 0; index < loggerNameLength && !Character.isLetter(loggerName.charAt(index)); ++index) {}
            if (index != loggerNameLength) {
                loggerName = loggerName.substring(index);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught Exception while removing numeric values in front ", ex);
        }
        return loggerName;
    }
    
    public static void sendCrashLog() {
        final String baseDir = System.getProperty("server.home");
        final String binDir = baseDir + File.separator + "bin";
        final File currentDir = new File(binDir);
        final long currentTime = System.currentTimeMillis();
        final long timeLimit = 300000L;
        final long resultTime = currentTime - timeLimit;
        final FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(final File currentDir, final String name) {
                return name.startsWith("hs_err_pid") && name.indexOf("~") == -1;
            }
        };
        final String[] listCrashFiles = currentDir.list(filter);
        boolean send = false;
        String supportFile = "";
        for (int i = 0; i < listCrashFiles.length; ++i) {
            final String crashFilePath = binDir + File.separator + listCrashFiles[i];
            final File crashFile = new File(crashFilePath);
            final long lastModified = crashFile.lastModified();
            if (lastModified > resultTime) {
                final String logsDir = baseDir + File.separator + "logs" + File.separator;
                final String logsCrashFilePath = logsDir + listCrashFiles[i];
                if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(logsCrashFilePath)) {
                    try {
                        ApiFactoryProvider.getFileAccessAPI().copyFile(crashFilePath, logsCrashFilePath);
                    }
                    catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                    supportFile = listCrashFiles[i];
                    send = true;
                }
            }
        }
        if (send) {
            try {
                final String content = getMailContent(supportFile);
                UploadAction.doUpload(supportFile, "dc-team@zohocorp.com", "dc-team@zohocorp.com", content, null);
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }
    
    private static String getMailContent(final String supportFile) {
        String host = "";
        try {
            final InetAddress add = InetAddress.getLocalHost();
            host = add.getHostName();
        }
        catch (final UnknownHostException e) {
            SyMUtil.logger.log(Level.WARNING, "Exception while getting host name in support tab", e);
        }
        final int port = getWebServerPort();
        final String logLocation = "http://" + host + ":" + port + "/logs/" + supportFile;
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Server might be crashed in " + host + " . Given below is the URL for the logs.\n");
        buffer.append(logLocation);
        final String content = buffer.toString();
        return content;
    }
    
    public static void copyFile(final File src, final File dst) throws IOException {
        final InputStream in = new FileInputStream(src);
        final OutputStream out = new FileOutputStream(dst);
        final byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    public static void loadPortValues() {
        SyMUtil.logger.log(Level.INFO, "SyMUtil: Going to load DC webserver port values...");
        try {
            SyMUtil.httpPort = WebServerUtil.getHttpPort();
            SyMUtil.sslPort = WebServerUtil.getHttpsPort();
            SyMUtil.httpnioPort = WebServerUtil.getHttpNioPort();
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "SyMUtil: Caught exception while loading DC webserver port values...", ex);
        }
    }
    
    @Deprecated
    public static int getWebServerPort() {
        return getSSLPort();
    }
    
    public static int getNioWebServerPort() {
        if (SyMUtil.httpnioPort == -1) {
            try {
                SyMUtil.httpnioPort = WebServerUtil.getHttpNioPort();
            }
            catch (final Exception ex) {
                SyMUtil.logger.log(Level.WARNING, "SyMUtil: Caught exception while retrieving DC webserver nio http port value.", ex);
            }
        }
        return SyMUtil.httpnioPort;
    }
    
    public static int getSSLPort() {
        if (SyMUtil.sslPort == -1) {
            try {
                SyMUtil.sslPort = WebServerUtil.getHttpsPort();
            }
            catch (final Exception ex) {
                SyMUtil.logger.log(Level.WARNING, "SyMUtil: Caught exception while retrieving DC webserver https port value.", ex);
            }
        }
        return SyMUtil.sslPort;
    }
    
    @Deprecated
    public static String getProductVersionFromLicense() throws Exception {
        final Wield wield = Wield.getInstance();
        final String productHomeDir = System.getProperty("server.home");
        final String filePath = productHomeDir + File.separator + "lib" + File.separator + "AdventNetLicense.xml";
        final InputFileParser parser = new InputFileParser(filePath);
        final DataClass data = parser.getDataClass();
        final User user = data.getUserObject(wield.getUserName());
        final ArrayList ID = user.getIDs();
        final String mapID = ID.get(0);
        final Details details = data.getDetails(mapID);
        final String version = details.getProductVersion();
        return version;
    }
    
    public static void logLicenseDetails() {
        final Logger dcServiceLogger = Logger.getLogger("DCServiceLogger");
        dcServiceLogger.log(Level.INFO, "LICENSE DETAILS BEGIN");
        try {
            final LicenseProvider w = LicenseProvider.getInstance();
            dcServiceLogger.log(Level.INFO, "Company Name :" + w.getCompanyName());
            dcServiceLogger.log(Level.INFO, "User Name :" + w.getUserName());
            dcServiceLogger.log(Level.INFO, "Product Name :" + w.getProductName());
            dcServiceLogger.log(Level.INFO, "Product Version :" + w.getLicenseVersion());
            dcServiceLogger.log(Level.INFO, "License Type :" + w.getLicenseType());
            dcServiceLogger.log(Level.INFO, "Category Type :" + w.getProductCategoryString());
            dcServiceLogger.log(Level.INFO, "Evaluation Days :" + w.getEvaluationDays());
            dcServiceLogger.log(Level.INFO, "Evaluation Expiry Date :" + w.getProductExpiryDate());
            if (w.getLicenseType() != null && w.getLicenseType().equals("R")) {
                Properties numOfSystems = w.getModuleProperties("Users");
                String propertyKey = "NumberOfUsers";
                if (numOfSystems == null) {
                    propertyKey = "NumberOfComputers";
                    numOfSystems = w.getModuleProperties("Computers");
                }
                final String numberOfSystems = numOfSystems.getProperty(propertyKey);
                dcServiceLogger.log(Level.INFO, "No. Of Systems Licensed to Manage :" + numberOfSystems);
                final String numberOfMobileDevices = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
                dcServiceLogger.log(Level.INFO, "No. of Mobile Devices Licensed to Manage :" + numberOfMobileDevices);
                final Properties techProperties = w.getModuleProperties("Technicians");
                if (techProperties != null && techProperties.getProperty("NumberOfTechnicians") != null) {
                    final String userCount = techProperties.getProperty("NumberOfTechnicians");
                    dcServiceLogger.log(Level.INFO, "No. of Users Licensed to Manage :" + userCount);
                }
            }
        }
        catch (final Exception ex) {
            dcServiceLogger.log(Level.WARNING, "Exception while logging license Details ", ex);
        }
        dcServiceLogger.log(Level.INFO, "LICENSE DETAILS END");
    }
    
    public static Properties getProductInfo() {
        final Properties productProps = new Properties();
        final Boolean isMETrackEnabled = METrackerHandler.getMETrackSettings();
        try {
            productProps.setProperty("productVersion", getProductProperty("productversion"));
            productProps.setProperty("productID", ProductUrlLoader.getInstance().getValue("productcode"));
            productProps.setProperty("buildnumber", getProductProperty("buildnumber"));
            final LicenseProvider w = LicenseProvider.getInstance();
            final String licenseType = w.getLicenseType();
            productProps.setProperty("licenseType", licenseType);
            final String productType = w.getProductCategoryString();
            productProps.setProperty("productType", productType);
            final String licensedTo = w.getUserName();
            productProps.setProperty("licensedTo", licensedTo);
            final String licenseCompanyName = w.getCompanyName();
            productProps.setProperty("licenseCompanyName", licenseCompanyName);
            final String installationdateInLong = getInstallationProperty("it");
            productProps.setProperty("it", installationdateInLong);
            final String language = getInstallationProperty("lang");
            productProps.setProperty("lang", language);
            String som = "N/A";
            if (isMETrackEnabled) {
                som = getInstallationProperty("som");
                final String[] somPropsArray = som.split("metrId");
                som = somPropsArray[0];
            }
            productProps.setProperty("som", som);
            final String sdp = getInstallationProperty("sdp");
            productProps.setProperty("sdp", sdp);
            final String currentDataBase = DBUtil.getActiveDBName();
            productProps.setProperty("db", currentDataBase);
            String mdm = "N/A";
            if (isMETrackEnabled) {
                mdm = getInstallationProperty("mdm");
            }
            productProps.setProperty("mdm", mdm);
        }
        catch (final Exception excep) {
            SyMUtil.logger.log(Level.SEVERE, "Exception occured while getting product properties. ", excep);
        }
        return productProps;
    }
    
    public static String getServerName() {
        String serverName = null;
        try {
            final DataObject dataObject = getDCServerInfoDO();
            if (!dataObject.isEmpty() && dataObject.containsTable("DCServerInfo")) {
                serverName = (String)dataObject.getFirstValue("DCServerInfo", "SERVER_MAC_NAME");
            }
            else {
                serverName = InetAddress.getLocalHost().getHostName();
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception while getting DC Server Name");
        }
        return serverName;
    }
    
    public static String getServerFQDNName() {
        String serverFQDNName = null;
        try {
            final DataObject dataObject = getPersistence().get("DCServerInfo", (Criteria)null);
            if (!dataObject.isEmpty() && dataObject.containsTable("DCServerInfo")) {
                serverFQDNName = (String)dataObject.getFirstValue("DCServerInfo", "SERVER_FQDN");
            }
            else {
                serverFQDNName = InetAddress.getLocalHost().getCanonicalHostName();
            }
            if (serverFQDNName != null && !serverFQDNName.equals("") && serverFQDNName.charAt(serverFQDNName.length() - 1) == '.') {
                serverFQDNName = serverFQDNName.substring(0, serverFQDNName.length() - 1);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception while getting DC Server FQDN Name");
        }
        return serverFQDNName;
    }
    
    public static void writeInstallProps(final Properties props) {
        try {
            final String confFileName = getInstallationDir() + File.separator + "conf" + File.separator + "install.conf";
            SyMUtil.logger.log(Level.FINEST, "Product conf file Name :" + confFileName);
            FileAccessUtil.storeProperties(props, confFileName, true);
            SyMUtil.logger.log(Level.INFO, "Had written the the Properties " + props + "in " + confFileName);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception while writing props in install.conf file", ex);
        }
    }
    
    public static void writeLicenseTypeInFile() throws SyMException, Exception {
        final Thread licensePropsThread = new Thread("licensePropsThread") {
            @Override
            public void run() {
                try {
                    SyMUtil.logger.log(Level.INFO, "start of writeLicenseTypeInFile ");
                    final Properties props = new Properties();
                    final String lt = LicenseProvider.getInstance().getLicenseType();
                    props.setProperty("license", lt);
                    SyMUtil.writeInstallProps(props);
                    SyMUtil.logger.log(Level.INFO, "Had written the License Type in install.conf");
                }
                catch (final Exception ex) {
                    SyMUtil.logger.log(Level.SEVERE, "Caught exception while populating license type in install.conf. ", ex);
                }
            }
        };
        licensePropsThread.start();
    }
    
    public static void writeLogonCountInFile() throws SyMException, Exception {
        final Thread logonCountThread = new Thread("logonCountThread") {
            @Override
            public void run() {
                try {
                    SyMUtil.logger.log(Level.INFO, "start of writeLogonCountInFile ");
                    final Properties props = new Properties();
                    String lc = com.me.devicemanagement.framework.server.util.SyMUtil.getInstallationProperty("lc");
                    int count = 0;
                    if (lc != null && !lc.equals("")) {
                        count = Integer.parseInt(lc);
                    }
                    ++count;
                    final Properties loginProps = METrackerUtil.getMETrackParams("TotUserLoginCount");
                    if (loginProps == null) {
                        METrackerUtil.addOrUpdateMETrackParams("TotUserLoginCount", String.valueOf(count));
                    }
                    else {
                        METrackerUtil.incrementMETrackParams("TotUserLoginCount");
                    }
                    props.setProperty("lc", String.valueOf(count));
                    props.setProperty("ll", String.valueOf(System.currentTimeMillis()));
                    lc = com.me.devicemanagement.framework.server.util.SyMUtil.getInstallationProperty("lcpd");
                    count = 0;
                    if (lc != null && !lc.equals("")) {
                        count = Integer.parseInt(lc);
                    }
                    ++count;
                    props.setProperty("lcpd", String.valueOf(count));
                    SyMUtil.writeInstallProps(props);
                    SyMUtil.logger.log(Level.INFO, "Had written the Logon Count in install.conf");
                }
                catch (final Exception ex) {
                    SyMUtil.logger.log(Level.SEVERE, "Caught exception while populating logon count in install.conf. ", ex);
                }
            }
        };
        logonCountThread.start();
    }
    
    public static void resetDailyLogonCount() throws SyMException, Exception {
        final Thread logonCountThread = new Thread("resetDailyLogonCountThread") {
            @Override
            public void run() {
                try {
                    SyMUtil.logger.log(Level.INFO, "resetDailyLogonCount() start of writeLogonCountInFile ");
                    final Properties props = new Properties();
                    props.setProperty("lcpd", "0");
                    SyMUtil.writeInstallProps(props);
                    SyMUtil.logger.log(Level.INFO, "resetDailyLogonCount() Had written the Logon Count in install.conf");
                }
                catch (final Exception ex) {
                    SyMUtil.logger.log(Level.SEVERE, "resetDailyLogonCount() Caught exception while populating logon count in install.conf. ", ex);
                }
            }
        };
        logonCountThread.start();
    }
    
    public static void storeMessageProps(final String fileName, final String key, final String value) throws Exception {
        FileOutputStream outF = null;
        try {
            final String fname = ".." + File.separator + "conf" + File.separator + fileName;
            final Properties props = FileAccessUtil.readProperties(fname);
            props.setProperty(key, value);
            outF = new FileOutputStream(fname);
            props.store(outF, null);
        }
        catch (final Exception exp) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting property...", exp);
        }
        finally {
            outF.close();
        }
    }
    
    public static Properties getCustInstallSpecProps() {
        Properties props = new Properties();
        try {
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "customer_specific_settings.conf";
            SyMUtil.logger.log(Level.INFO, "***********customer_specific_settings fname: " + fname);
            props = FileAccessUtil.readProperties(fname);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting customer_specific_settings: ", ex);
        }
        return props;
    }
    
    public static void triggerServerRestart(final String reasonForRestart) {
        final Logger dcslogger = Logger.getLogger("DCServiceLogger");
        dcslogger.log(Level.INFO, "DevMgmtUtil.triggerServerRestart() is invoked...");
        Thread.currentThread();
        Thread.dumpStack();
        ConsoleOut.println("Going to restart the server. Reason :: " + reasonForRestart);
        ConsoleOut.println("Restart the SERVER Completely");
    }
    
    @Deprecated
    public static void triggerServerRestart() {
        triggerServerRestart("Server restart triggered");
    }
    
    public static void triggerServerShutdown(final String reasonForShutDown) {
        final Logger dcslogger = Logger.getLogger("DCServiceLogger");
        dcslogger.log(Level.INFO, "DevMgmtUtil.triggerServerShutdown() is invoked...");
        Thread.currentThread();
        Thread.dumpStack();
        ConsoleOut.println("Going to shut down the server. Reason :: " + reasonForShutDown);
        ConsoleOut.println("Shutdown the SERVER Completely");
    }
    
    @Deprecated
    public static void triggerServerShutdown() {
        triggerServerShutdown("Server shutdown triggered");
    }
    
    public static void updateCustomerSegmentationInfo() {
        try {
            final String confFileName = getInstallationDir() + File.separator + "conf" + File.separator + "customer_info.conf";
            SyMUtil.logger.log(Level.INFO, "Customer Info conf file Name :" + confFileName);
            if (!new File(confFileName).exists()) {
                final Properties props = new Properties();
                final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
                String customerName = null;
                if (customerId != null) {
                    props.setProperty("CUSTOMER_ID", customerId.toString());
                    customerName = CustomerInfoUtil.getInstance().getCustomerNameFromID(customerId);
                    props.setProperty("CUSTOMER_NAME", customerName);
                }
                final String mspName = "DC_MSP";
                props.setProperty("MSP_NAME", mspName);
                FileAccessUtil.storeProperties(props, confFileName, true);
                SyMUtil.logger.log(Level.INFO, "Had written the the Properties " + props + "in " + confFileName);
            }
            else {
                SyMUtil.logger.log(Level.INFO, "Customer Info conf file already exists !!!");
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception while writing props in customer_info.conf file", ex);
        }
    }
    
    public static void createDCTempDirectory() {
        try {
            String baseDir = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fs = File.separator;
            baseDir = baseDir + fs + "dc-temp";
            final boolean isDirExist = ApiFactoryProvider.getFileAccessAPI().isFileExists(baseDir);
            if (!isDirExist) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(baseDir);
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Exception while getting server ips...", e);
        }
    }
    
    public static String getDCTempDirectory() throws Exception {
        String baseDir = new File(System.getProperty("server.home")).getCanonicalPath();
        final String fs = File.separator;
        baseDir = baseDir + fs + "dc-temp";
        return baseDir;
    }
    
    public static Properties getTrackingProps() {
        final LicenseProvider w = LicenseProvider.getInstance();
        String companyName = w.getCompanyName();
        if (companyName != null && !companyName.equals("")) {
            companyName = companyName.replaceAll(" ", "+");
        }
        else {
            companyName = "";
        }
        final Properties licensePropety = w.getModuleProperties("LicenseDetails");
        final Properties formData = new Properties();
        ((Hashtable<String, String>)formData).put("cname", companyName);
        ((Hashtable<String, String>)formData).put("pname", ProductUrlLoader.getInstance().getValue("padbtrackingcode"));
        ((Hashtable<String, String>)formData).put("build", getProductProperty("buildnumber"));
        ((Hashtable<String, String>)formData).put("installtime", getInstallationProperty("it"));
        ((Hashtable<String, String>)formData).put("LT", w.getLicenseType());
        ((Hashtable<String, String>)formData).put("PED", w.getProductExpiryDate());
        ((Hashtable<String, String>)formData).put("SOM", getInstallationProperty("som"));
        ((Hashtable<String, String>)formData).put("db", getInstallationProperty("db"));
        ((Hashtable<String, String>)formData).put("lcpd", getInstallationProperty("lcpd"));
        ((Hashtable<String, String>)formData).put("pkg", getInstallationProperty("pkg"));
        ((Hashtable<String, String>)formData).put("sdp", getInstallationProperty("sdp"));
        ((Hashtable<String, String>)formData).put("mdm", getInstallationProperty("mdm"));
        if (licensePropety != null) {
            ((Hashtable<String, String>)formData).put("cid", licensePropety.getProperty("CustomerID"));
            ((Hashtable<String, String>)formData).put("licid", licensePropety.getProperty("LicenseID"));
        }
        return formData;
    }
    
    protected static String trimDBName(String dbName) {
        if (dbName != null && dbName.length() > 2) {
            dbName = dbName.substring(0, 2);
        }
        return dbName;
    }
    
    public static long getServerFreeSpace(final String baseDir) {
        final File serverFile = new File(baseDir);
        return serverFile.getFreeSpace();
    }
    
    public static String[] getBackupFoldersList(final String serverHome, final String activedb) {
        String backupFolder1 = "";
        if (activedb.equalsIgnoreCase("mysql")) {
            backupFolder1 = serverHome + File.separator + "mysql" + File.separator + "data";
        }
        else if (activedb.equalsIgnoreCase("postgres")) {
            backupFolder1 = serverHome + File.separator + "pgsql" + File.separator + "data";
        }
        final String backupFolder2 = serverHome + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "client-data";
        final ArrayList<String> dcServicebackupFiles = ApiFactoryProvider.getServiceAPI(true).getBackupFoldersList(serverHome);
        final ArrayList<String> mdmServicebackupFiles = ApiFactoryProvider.getServiceAPI(false).getBackupFoldersList(serverHome);
        dcServicebackupFiles.addAll(mdmServicebackupFiles);
        final ArrayList<String> commonbackupFiles = new ArrayList<String>();
        commonbackupFiles.add(backupFolder1);
        commonbackupFiles.add(backupFolder2);
        commonbackupFiles.addAll(dcServicebackupFiles);
        final String[] backupFileArray = commonbackupFiles.toArray(new String[commonbackupFiles.size()]);
        return backupFileArray;
    }
    
    public static void writeInstallPropsInFile() {
        try {
            final Thread installPropsThread = new Thread("installPropsThread") {
                @Override
                public void run() {
                    try {
                        SyMUtil.logger.log(Level.INFO, "start of writeInstallPropsInFile ");
                        final Properties props = new Properties();
                        Properties dcProp = null;
                        if (CustomerInfoUtil.isDC() || CustomerInfoUtil.isMDMP() || CustomerInfoUtil.isPMP() || CustomerInfoUtil.getInstance().isRAP()) {
                            dcProp = ApiFactoryProvider.getServiceAPI(true).getServiceProperty();
                        }
                        if (dcProp != null) {
                            props.setProperty("som", dcProp.getProperty("som"));
                            SyMUtil.updateSyMParameter("SoMSummary", dcProp.getProperty("som"));
                        }
                        final String licenseType = LicenseProvider.getInstance().getLicenseType();
                        props.setProperty("license", licenseType + "");
                        final String sdp = SolutionUtil.getInstance().getSDPPropertyForTracking();
                        props.setProperty("sdp", sdp);
                        String dbname = DBUtil.getActiveDBName();
                        dbname = SyMUtil.trimDBName(dbname);
                        props.setProperty("db", dbname);
                        if (CustomerInfoUtil.isMDM()) {
                            final String mdm = ApiFactoryProvider.getServiceAPI(false).getTrackingSummary();
                            props.setProperty("mdm", mdm);
                        }
                        SyMUtil.writeInstallProps(props);
                        SyMUtil.logger.log(Level.INFO, "Had written the Install Properties in install.conf");
                    }
                    catch (final Exception ex) {
                        SyMUtil.logger.log(Level.SEVERE, "Caught exception while populating som properties in install.conf. ", ex);
                    }
                }
            };
            installPropsThread.start();
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while writing install props in install.conf...", ex);
        }
    }
    
    public static void printCacheMemoryDetails() {
        SyMUtil.logger.log(Level.INFO, "################## Cache Repository statistics [MB] ##################");
        SyMUtil.logger.log(Level.INFO, "currentSize : " + CacheManager.getCacheRepository().currentSize());
        SyMUtil.logger.log(Level.INFO, "getCount : " + CacheManager.getCacheRepository().getCount());
        SyMUtil.logger.log(Level.INFO, "getMaxSize : " + CacheManager.getCacheRepository().getMaxSize());
        SyMUtil.logger.log(Level.INFO, "################## Cache Repository statistics [MB] ##################");
    }
    
    public static void printHashMemoryDetails() {
        final int mb = 1048576;
        final Runtime runtime = Runtime.getRuntime();
        SyMUtil.logger.log(Level.INFO, "################## Heap utilization statistics [MB] ##################");
        SyMUtil.logger.log(Level.INFO, "Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
        SyMUtil.logger.log(Level.INFO, "Free Memory:" + runtime.freeMemory() / mb);
        SyMUtil.logger.log(Level.INFO, "Total Memory:" + runtime.totalMemory() / mb);
        SyMUtil.logger.log(Level.INFO, "Max Memory:" + runtime.maxMemory() / mb);
        SyMUtil.logger.log(Level.INFO, "################## Heap utilization statistics [MB] ##################");
    }
    
    public static void setEvaluatorInfo(final HttpServletRequest request) {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType.equalsIgnoreCase("T")) {
            final String evaluatorInfoChanged = getSyMParameter("EvaluatorInfoModified");
            if (evaluatorInfoChanged != null && Boolean.parseBoolean(evaluatorInfoChanged)) {
                request.setAttribute("EvaluatorInfo", (Object)(getSyMParameter("EvaluatorInfo") + ""));
                updateSyMParameter("EvaluatorInfoModified", "false");
            }
        }
        request.setAttribute("licenseType", (Object)licenseType);
    }
    
    public static String getDCOSArchitecture() {
        String osArch = null;
        try {
            osArch = WinAccessProvider.getInstance().getOSArchitecture();
            if (osArch == null || osArch.isEmpty()) {
                osArch = "-";
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.SEVERE, "SyMutil - getOSArchitecture(): Error while getting OS Architecture...", e);
            osArch = "-";
        }
        return osArch;
    }
    
    public static void getDBLockReport(final HttpServletRequest request) {
        if (notifyDBLockToUser()) {
            request.setAttribute("NOTIFY_LOCK", (Object)"true");
        }
    }
    
    public static boolean notifyDBLockToUser() {
        Boolean notifyLockToUser = Boolean.FALSE;
        try {
            final Column col = Column.getColumn("DbLockInfo", "IS_DELETED");
            final Criteria DeletedCriteria = new Criteria(col, (Object)"false", 0);
            final Criteria BlockedCriteria = new Criteria(Column.getColumn("DbLockInfo", "THREADS_BLOCKED"), (Object)100, 4);
            final Criteria c = DeletedCriteria.and(BlockedCriteria);
            final DataObject LockInfoDo = getPersistence().get("DbLockInfo", c);
            final DataObject data = getPersistence().get("DbLockSettings", (Criteria)null);
            final Row dcSettingsRow = data.getRow("DbLockSettings");
            final Integer notify_limit = (Integer)dcSettingsRow.get("NOTIFY_LIMIT");
            final Boolean isAutomaticmailing = (Boolean)dcSettingsRow.get("IS_AUTOMATIC");
            if (!LockInfoDo.isEmpty() && !isAutomaticmailing) {
                final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                final Criteria criteria = new Criteria(new Column("DbLockNotification", "LOGIN_ID"), (Object)loginID, 0);
                final DataObject dblocknotificationdo = getPersistence().get("DbLockNotification", criteria);
                if (dblocknotificationdo.isEmpty()) {
                    notifyLockToUser = Boolean.TRUE;
                    CleanDbLockFiles.SetNotificationOff();
                }
                else {
                    final Long last_notified_time = (Long)dblocknotificationdo.getFirstValue("DbLockNotification", "LAST_NOTIFIED_TIME");
                    final Long last_dblock_detected_time = (Long)DBUtil.getMaxOfValue("DbLockInfo", "CREATED_TIME", c);
                    if (getDateDiff((long)last_notified_time, (long)last_dblock_detected_time) > notify_limit) {
                        notifyLockToUser = Boolean.TRUE;
                        CleanDbLockFiles.SetNotificationOff();
                    }
                }
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Exception in gettingDbLockReport", e);
        }
        return notifyLockToUser;
    }
    
    public static HashMap getFSMCorruptedTableList() {
        Connection conn = null;
        String corruptedTableName = null;
        String corruptedFSMPath = null;
        DataSet dataSet = null;
        final HashMap<String, String> corruptedMap = new HashMap<String, String>();
        try {
            if (System.getProperty("proceed.fsmcorruption.analysis", "false").equalsIgnoreCase("true")) {
                SyMUtil.logger.log(Level.INFO, "Going to check FSM Corrupted Tables in setup");
                final String fsmCorruptionAnalyzeQuery = "SELECT oid, oid::regclass AS FSM_CORRUPTED_TABLENAME, pg_relation_filepath(oid) || '_fsm' AS FSM_CORRUPTED_FILEPATH FROM pg_class, CAST(current_setting('block_size') AS BIGINT) AS bs WHERE relkind IN ('r', 'i', 't', 'm') AND EXISTS (SELECT 1 FROM generate_series(pg_relation_size(oid) / bs,(pg_relation_size(oid, 'fsm') - 2*bs) / 2) AS blk WHERE pg_freespace(oid, blk) > 0)";
                conn = RelationalAPI.getInstance().getConnection();
                dataSet = RelationalAPI.getInstance().executeQuery(fsmCorruptionAnalyzeQuery, conn);
                while (dataSet.next()) {
                    corruptedTableName = dataSet.getAsString("FSM_CORRUPTED_TABLENAME");
                    corruptedFSMPath = dataSet.getAsString("FSM_CORRUPTED_FILEPATH");
                    corruptedMap.put(corruptedTableName, corruptedFSMPath);
                    SyMUtil.logger.log(Level.INFO, "Table Name : {0} -- File path : {1}", new Object[] { corruptedTableName, corruptedFSMPath });
                }
            }
            else {
                SyMUtil.logger.log(Level.INFO, "******************************************************************************************");
                SyMUtil.logger.log(Level.INFO, "Seems FSM Corruption analysis disabled. so skip this analysis....");
                SyMUtil.logger.log(Level.INFO, "******************************************************************************************");
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.INFO, " Exception message " + ex.getMessage());
            if (ex.getMessage().equalsIgnoreCase("No results were returned by the query")) {
                SyMUtil.logger.log(Level.INFO, "Query executed, No rows found.");
            }
            else {
                SyMUtil.logger.log(Level.WARNING, "Unable to get FSM Corrupted tables due to the Exception : ", ex);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                SyMUtil.logger.log(Level.WARNING, "Exception while closing connection : ", e);
            }
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception e) {
                SyMUtil.logger.log(Level.WARNING, "Exception while closing dataset : ", e);
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e2) {
                SyMUtil.logger.log(Level.WARNING, "Exception while closing connection : ", e2);
            }
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception e2) {
                SyMUtil.logger.log(Level.WARNING, "Exception while closing dataset : ", e2);
            }
        }
        return corruptedMap;
    }
    
    public static void writeFSMCorruptionListIntoFile(final HashMap<String, String> fsmCorruptedMap, final String filePath) {
        try {
            final File fsmCorruptionListFile = new File(filePath);
            if (fsmCorruptionListFile.exists()) {
                SyMUtil.logger.log(Level.INFO, "Existing FSM Corruption list file deleted status {0} from Location {1} : ", new Object[] { fsmCorruptionListFile.delete(), fsmCorruptionListFile.getAbsolutePath() });
            }
            boolean isParantDirAvailable = true;
            if (!fsmCorruptionListFile.getParentFile().exists()) {
                SyMUtil.logger.log(Level.INFO, "User-conf Folder not available : " + fsmCorruptionListFile.getParentFile());
                if (!fsmCorruptionListFile.getParentFile().mkdir()) {
                    isParantDirAvailable = false;
                    SyMUtil.logger.log(Level.INFO, "Parent Directory does not exists {0} ", new Object[] { fsmCorruptionListFile.getParentFile() });
                }
            }
            if (isParantDirAvailable) {
                SyMUtil.logger.log(Level.INFO, "FSMCorruptionList creation status {0} from this Location {1} ", new Object[] { fsmCorruptionListFile.createNewFile(), fsmCorruptionListFile.getAbsolutePath() });
            }
            final String serverHome = getInstallationDir();
            final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
            for (final String key : fsmCorruptedMap.keySet()) {
                final String value = fsmCorruptedMap.get(key);
                final String fullPath = new File(serverHome + File.separator + "pgsql" + File.separator + "data" + File.separator + value).getCanonicalPath();
                SyMUtil.logger.log(Level.INFO, "Table name : {0} :  path {1}", new Object[] { key, fullPath });
                bw.write(fullPath);
                bw.newLine();
            }
            if (bw != null) {
                bw.close();
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Unable to write FSM Corruption list into File. Exception : ", ex);
        }
    }
    
    public static void updateFSMCorruptedInfoInDB(final HashMap<String, String> fsmCorruptedMap) {
        try {
            final SelectQueryImpl query = new SelectQueryImpl(Table.getTable("FSMCorruptionInfo"));
            query.addSelectColumn(new Column("FSMCorruptionInfo", "*"));
            for (final String key : fsmCorruptedMap.keySet()) {
                final Criteria cri = new Criteria(Column.getColumn("FSMCorruptionInfo", "FSM_CORRUPTED_TABLENAME"), (Object)key, 0);
                query.setCriteria(cri);
                final DataObject resultDO = getPersistence().get((SelectQuery)query);
                if (resultDO.isEmpty()) {
                    final Row row = new Row("FSMCorruptionInfo");
                    row.set("FSM_CORRUPTED_TABLENAME", (Object)key);
                    row.set("FSM_CORRUPTED_FINDING_DATE", (Object)System.currentTimeMillis());
                    row.set("IS_FSM_CORRUPTION_HANDLED", (Object)Boolean.FALSE);
                    row.set("FSM_CORRUPTION_COUNT", (Object)1);
                    resultDO.addRow(row);
                    getPersistence().add(resultDO);
                }
                else {
                    final Row row = resultDO.getFirstRow("FSMCorruptionInfo");
                    if (!row.get("IS_FSM_CORRUPTION_HANDLED").toString().equalsIgnoreCase("true")) {
                        continue;
                    }
                    row.set("FSM_CORRUPTED_FINDING_DATE", (Object)System.currentTimeMillis());
                    row.set("IS_FSM_CORRUPTION_HANDLED", (Object)Boolean.FALSE);
                    final int count = (int)row.get("FSM_CORRUPTION_COUNT");
                    row.set("FSM_CORRUPTION_COUNT", (Object)(count + 1));
                    resultDO.updateRow(row);
                    getPersistence().update(resultDO);
                }
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while update FSM Corruption list into Table. Exception : ", ex);
        }
    }
    
    public static void updateFSMCorruptedFlagInDB(final HashMap<String, String> fsmCorruptedMap) {
        try {
            final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("FSMCorruptionInfo");
            final String[] tableNames = new String[fsmCorruptedMap.size()];
            int index = 0;
            for (final String key : fsmCorruptedMap.keySet()) {
                tableNames[index] = key;
                ++index;
            }
            final Criteria cri = new Criteria(Column.getColumn("FSMCorruptionInfo", "FSM_CORRUPTED_TABLENAME"), (Object)tableNames, 9);
            query.setCriteria(cri);
            query.setUpdateColumn("IS_FSM_CORRUPTION_HANDLED", (Object)Boolean.TRUE);
            getPersistence().update(query);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while update FSM Corruption list into Table. Exception : ", ex);
        }
    }
    
    public static void analyseFSMCorruption() {
        try {
            final HashMap<String, String> fsmCorruptedMap = getFSMCorruptedTableList();
            if (fsmCorruptedMap.size() > 0) {
                final String filePath = getInstallationDir() + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "FSMCorruptionList.txt";
                writeFSMCorruptionListIntoFile(fsmCorruptedMap, filePath);
                updateFSMCorruptedInfoInDB(fsmCorruptedMap);
            }
            else {
                SyMUtil.logger.log(Level.INFO, "No FSM Corruption Table was corrupted in customer setup");
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurs while analyze FSM Corruption tables. Exception : ", ex);
        }
    }
    
    public static String getLastServerStartupTimeFromDB() {
        String lastServerStartedTime = null;
        lastServerStartedTime = getServerParameter("last_server_startup_time_DB");
        return lastServerStartedTime;
    }
    
    public static boolean isDBMatchWithFileSystem() {
        String lastServerStartupTimeFromFile = null;
        String lastServerStartupTimeFromDB = null;
        try {
            lastServerStartupTimeFromDB = getLastServerStartupTimeFromDB();
            if (lastServerStartupTimeFromDB == null) {
                SyMUtil.logger.log(Level.INFO, "setup seems first server startup after installing fresh exe");
                return true;
            }
            final String fileSystemConf = getInstallationDir() + File.separator + SyMUtil.SERVER_STARTTIME_FILE;
            if (new File(fileSystemConf).exists()) {
                lastServerStartupTimeFromFile = lastServerStartupTimeFromFileSystem();
                if (lastServerStartupTimeFromFile != null && lastServerStartupTimeFromDB.equals(lastServerStartupTimeFromFile)) {
                    SyMUtil.logger.log(Level.INFO, " File System time match with DB, So Proceed. ");
                    return true;
                }
            }
            else {
                SyMUtil.logger.log(Level.INFO, " Found fresh file system with existing DB. Going to stop the Process to avoid Data Corruption. ");
                lastServerStartupTimeFromFile = "";
            }
            final Properties pr = new Properties();
            pr.setProperty("lastServerStartupTimeinDB", lastServerStartupTimeFromDB);
            pr.setProperty("lastServerStartupTimeFromFile", lastServerStartupTimeFromFile);
            pr.setProperty("isAllowServerStartup", "false");
            pr.putAll(getAvailableBackupDetails());
            pr.putAll(getBakRestoreDetailsFromDB(validateDBAndFileTime(lastServerStartupTimeFromDB, lastServerStartupTimeFromFile)));
            createFileSystemLock(pr);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while checking DB and File system ", ex);
        }
        return false;
    }
    
    public static String lastServerStartupTimeFromFileSystem() {
        String lastServerStartupTimeFromFile = null;
        try {
            final String fileSystemConf = getInstallationDir() + File.separator + SyMUtil.SERVER_STARTTIME_FILE;
            final Properties fileSystemProps = StartupUtil.getProperties(fileSystemConf);
            lastServerStartupTimeFromFile = fileSystemProps.getProperty("last_server_startup_time");
            SyMUtil.logger.log(Level.INFO, "Last server startup time from file system :  " + lastServerStartupTimeFromFile);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.INFO, "Exception occurred while getting last server startup time from file system  ", ex);
        }
        return lastServerStartupTimeFromFile;
    }
    
    public static void createFileSystemLock(final Properties pr) {
        String fileSystemLockFileName = null;
        try {
            fileSystemLockFileName = getInstallationDir() + File.separator + "bin" + File.separator + "filesystem.lock";
            SyMUtil.logger.log(Level.WARNING, "Going to create a filesystem.lock file: " + fileSystemLockFileName);
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(fileSystemLockFileName)) {
                final Properties fileSystemLockProps = FileAccessUtil.readProperties(fileSystemLockFileName);
                SyMUtil.logger.log(Level.WARNING, "Contents of existing filesystem.lock file: " + fileSystemLockProps);
            }
            FileAccessUtil.storeProperties(pr, fileSystemLockFileName, false);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while creating filesystem.lock file: ", ex);
        }
    }
    
    public static String setSQLDBFileSystemMessageContentForBackup(String msgContent) {
        try {
            final String recentSuccessfullbackupFileName = getLastSuccessfullBackupName();
            msgContent = msgContent.replace("{0}", recentSuccessfullbackupFileName + "");
            msgContent = msgContent.replace("{1}", geScheduledDBBackupLocation() + "");
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while setting SQL server DB mismatch Message content. Exception : ", ex);
        }
        return msgContent;
    }
    
    public static Boolean isFosReplicationPending() {
        try {
            if (!FOS.isEnabled()) {
                return false;
            }
            return FosUtil.getFOSParameter("IS_REPLICATION_PENDING");
        }
        catch (final Exception ex) {
            Logger.getLogger(DMOnPremiseService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static Properties getAvailableBackupDetails() {
        final String backupDetailsFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "server.props";
        final Properties availableBackupProps = getLastBackupDetails();
        try {
            final Properties fileBackupDetails = FileAccessUtil.readProperties(backupDetailsFilePath);
            if (fileBackupDetails.size() > 0 && fileBackupDetails.getProperty("BackupGeneratedTime") != null) {
                final long longDBTime = Long.parseLong(availableBackupProps.getProperty("BackupGeneratedTime"));
                final long longFileTime = Long.parseLong(fileBackupDetails.getProperty("BackupGeneratedTime"));
                if (longDBTime == longFileTime) {
                    return availableBackupProps;
                }
                if (longDBTime > longFileTime) {
                    if (new File(getBackupPath(availableBackupProps)).exists()) {
                        return availableBackupProps;
                    }
                    if (new File(getBackupPath(fileBackupDetails)).exists()) {
                        return fileBackupDetails;
                    }
                    final File backupFile = geLatestScheduledDBBackupLocation(availableBackupProps.getProperty("ScheduledBackupLocation"));
                    if (backupFile != null) {
                        ((Hashtable<String, String>)availableBackupProps).put("LastSuccessfullScheduledBackup", backupFile.getName());
                        ((Hashtable<String, String>)availableBackupProps).put("ScheduledBackupLocation", backupFile.getParent());
                    }
                }
                else {
                    if (new File(getBackupPath(fileBackupDetails)).exists()) {
                        return fileBackupDetails;
                    }
                    if (new File(getBackupPath(availableBackupProps)).exists()) {
                        return availableBackupProps;
                    }
                    final File backupFile = geLatestScheduledDBBackupLocation(fileBackupDetails.getProperty("ScheduledBackupLocation"));
                    if (backupFile != null) {
                        ((Hashtable<String, String>)availableBackupProps).put("LastSuccessfullScheduledBackup", backupFile.getName());
                        ((Hashtable<String, String>)availableBackupProps).put("ScheduledBackupLocation", backupFile.getParent());
                    }
                }
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while collecting the backup details. Exception : ", e);
        }
        return availableBackupProps;
    }
    
    public static String getBackupPath(final Properties props) {
        return props.getProperty("ScheduledBackupLocation") + File.separator + props.getProperty("LastSuccessfullScheduledBackup");
    }
    
    public static Properties getLastBackupDetails() {
        final Properties backupProps = new Properties();
        try {
            final Criteria successBackupCriteria = new Criteria(new Column("DBBackupFilesInfo", "BACKUP_STATUS"), (Object)"true", 0);
            final Long recentBackupDate = (Long)DBUtil.getMaxOfValue("DBBackupFilesInfo", "GENRATED_TIME", successBackupCriteria);
            final Object result = DBUtil.getValueFromDB("DBBackupFilesInfo", "GENRATED_TIME", (Object)recentBackupDate, "FILE_NAME");
            if (result != null) {
                ((Hashtable<String, Object>)backupProps).put("LastSuccessfullScheduledBackup", result);
            }
            ((Hashtable<String, String>)backupProps).put("ScheduledBackupLocation", geScheduledDBBackupLocation());
            ((Hashtable<String, String>)backupProps).put("BackupGeneratedTime", Long.toString(recentBackupDate));
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while collecting the backup details. Exception : ", e);
        }
        return backupProps;
    }
    
    public static String getLastSuccessfullBackupName() {
        String recentSuccessfullbackupFileName = "";
        try {
            final Criteria successBackupCriteria = new Criteria(new Column("DBBackupFilesInfo", "BACKUP_STATUS"), (Object)"true", 0);
            final Long recentBackupDate = (Long)DBUtil.getMaxOfValue("DBBackupFilesInfo", "GENRATED_TIME", successBackupCriteria);
            final Object result = DBUtil.getValueFromDB("DBBackupFilesInfo", "GENRATED_TIME", (Object)recentBackupDate, "FILE_NAME");
            if (result != null) {
                recentSuccessfullbackupFileName = (String)result;
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while get last back name from DB. Exception : ", ex);
        }
        return recentSuccessfullbackupFileName;
    }
    
    public static String geScheduledDBBackupLocation() {
        String backupLocation = null;
        try {
            final Object result = DBUtil.getFirstValueFromDBWithOutCriteria("DBBackupInfo", "BACKUP_DIR");
            if (result != null) {
                backupLocation = (String)result;
            }
            else {
                backupLocation = new File(System.getProperty("server.home") + File.separator + "ScheduledDBBackup").getCanonicalPath();
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while get scheduled DB backup Location. Exception : ", ex);
        }
        return backupLocation;
    }
    
    public static void deleteJssecacertsFile() {
        try {
            final char SEP = File.separatorChar;
            final String server_home = System.getProperty("server.home");
            SyMUtil.logger.log(Level.INFO, "Going to delete jssecacerts file from JRE");
            final File file = new File(server_home + SEP + "jre" + SEP + "lib" + SEP + "security" + SEP + "jssecacerts");
            if (file.exists()) {
                final String fileName = server_home + SEP + "jre" + SEP + "lib" + SEP + "security" + SEP + "jssecacerts";
                ApiFactoryProvider.getFileAccessAPI().deleteFile(fileName);
                SyMUtil.logger.log(Level.INFO, "jssecacerts file has been deleted successfully");
            }
            else {
                SyMUtil.logger.log(Level.INFO, "jssecacerts file not founf in jre ");
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while deleting jssecacerts file in jre:", e);
        }
    }
    
    public static Properties getBakRestoreDetailsFromDB(final long lowTime) {
        Connection con = null;
        DataSet rs = null;
        final RelationalAPI relapi = RelationalAPI.getInstance();
        final Properties props = new Properties();
        try {
            final Map urlProps = relapi.getDBAdapter().splitConnectionURL(relapi.getDBAdapter().getDBProps().getProperty("url"));
            final String database = urlProps.get("DBName");
            final String bakRestoreQuery = "SELECT  restore_date, user_name FROM msdb.dbo.restorehistory WHERE destination_database_name = '" + database + "' ORDER BY restore_date DESC";
            con = relapi.getConnection();
            rs = relapi.executeQuery(bakRestoreQuery, con);
            if (rs != null && rs.next()) {
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                final Date date = sdf.parse(String.valueOf(rs.getAsTimestamp("restore_date")));
                final long bakRestoreTime = date.getTime();
                if (bakRestoreTime > lowTime) {
                    props.setProperty("LastBakRestoreTime", String.valueOf(rs.getAsTimestamp("restore_date")));
                    props.setProperty("RestoredUser", (String)rs.getValue("user_name"));
                }
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while getting bak restored details. Exception : ", e);
            try {
                if (con != null) {
                    con.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final SQLException throwables) {
                SyMUtil.logger.log(Level.WARNING, "Exception occurred while closing the connection!", throwables);
            }
        }
        finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final SQLException throwables2) {
                SyMUtil.logger.log(Level.WARNING, "Exception occurred while closing the connection!", throwables2);
            }
        }
        return props;
    }
    
    private static long validateDBAndFileTime(final String dbTime, final String fileTime) {
        final long longDBTime = Long.parseLong(dbTime);
        final long longFileTime = Long.parseLong(fileTime);
        if (longDBTime > longFileTime) {
            return longFileTime;
        }
        return longDBTime;
    }
    
    public static Boolean updateStartupTimeInDB(final String paramName, final String paramValue) {
        try {
            final DataObject serverParamsDO = DataAccess.get("ServerParams", (Criteria)null);
            final Criteria criteria = new Criteria(Column.getColumn("ServerParams", "PARAM_NAME"), (Object)paramName, 0, false);
            Row serverParamRow = serverParamsDO.getRow("ServerParams", criteria);
            if (serverParamRow == null) {
                serverParamRow = new Row("ServerParams");
                serverParamRow.set("PARAM_NAME", (Object)paramName);
                serverParamRow.set("PARAM_VALUE", (Object)paramValue);
                serverParamsDO.addRow(serverParamRow);
                SyMUtil.logger.log(Level.FINER, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                serverParamRow.set("PARAM_VALUE", (Object)paramValue);
                serverParamsDO.updateRow(serverParamRow);
                SyMUtil.logger.log(Level.FINER, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            com.me.devicemanagement.framework.server.util.SyMUtil.getPersistence().update(serverParamsDO);
            return Boolean.TRUE;
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while updating Parameter:" + paramName + " in DB.", ex);
            return Boolean.FALSE;
        }
    }
    
    public static File geLatestScheduledDBBackupLocation(final String backupLocation) {
        File backupFile = null;
        long lastModified = 0L;
        try {
            final File backupPath = new File(backupLocation);
            final File[] listFiles;
            final File[] files = listFiles = backupPath.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    return name.endsWith(".zip");
                }
            });
            for (final File file : listFiles) {
                if (file.lastModified() > lastModified) {
                    lastModified = file.lastModified();
                    backupFile = file;
                }
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while get scheduled DB backup Location. Exception : ", ex);
        }
        return backupFile;
    }
    
    public static String getServerBaseUrlForMail() throws Exception {
        final String protocolType = WebServerUtil.getServerProtocol();
        final String serverIP = getServerFQDNName();
        final Integer port = WebServerUtil.getWebServerPort();
        final StringBuilder baseURLStr = new StringBuilder(protocolType);
        baseURLStr.append("://").append(serverIP).append(":").append(port);
        return baseURLStr.toString();
    }
    
    static {
        SyMUtil.logger = Logger.getLogger(SyMUtil.class.getName());
        SyMUtil.httpPort = -1;
        SyMUtil.httpnioPort = -1;
        SyMUtil.sslPort = -1;
        SERVER_STARTTIME_FILE = "conf" + File.separator + "server.starttime";
    }
}
