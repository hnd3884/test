package com.adventnet.devicemanagement.silentmigration;

import java.util.Hashtable;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Formatter;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.FieldPosition;
import com.adventnet.sym.logging.LoggerUtil;
import java.util.logging.LogRecord;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.Properties;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileFilter;
import java.io.File;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;
import java.util.logging.Logger;

public class SilentUpdation
{
    public static Logger logger;
    public static final String SILENT_UPDATION_LOG;
    private static final String PPM_LOCK_FILE_NAME = "ppm.lock";
    public static SimpleFormatter formatter;
    public static SimpleDateFormat dateformat;
    public static SimpleDateFormat timeformat;
    public static final List<String> INSTALL_PPM_COMMAND;
    public static final String INSTALL_TRAY_ICON = " -forcelaunch";
    public static final String GENERAL_PROPERTIES;
    public static final String PRODUCT_EXE_PROPERTY = "product_exe";
    public static final String QPM_TRACKING_LIMIT_PROPERTY = "qpmTrackingLimit";
    public static final String MAX_QPM_SIZE_PROPERTY = "maxQpmSize";
    public static String productExeName;
    public static int meTrackingConstant;
    public static int maxQpmSize;
    public static final String QUICK_FIXER = "quickfixer";
    public static final String ARCHIVE = "archive";
    public static final String QUICK_FIX_BACKUP;
    public static final String QUICK_FIX_HISTORY;
    public static final String QUICK_FIX_TRACKING;
    public static final String ME_QF_STATUS_PROPERTY = "QuickFixerStatus";
    public static final String ME_QF_FILE_PROPERTY = "QuickFixerFilename";
    public static final String ME_QF_DATE_PROPERTY = "QuickFixerDate";
    public static final String LAST_FIX_STATUS_PROPERTY = "LastQuickFixerStatus";
    public static final String LAST_FIX_NAME_PROPERTY = "LastQuickFixerName";
    public static final String LAST_FIX_INSTALL_TIME_PROPERTY = "LastQuickFixerInstallTime";
    public static final String ONDEMAND_LAST_FIX_STATUS_PROPERTY = "OndemandLastQuickFixerStatus";
    public static final String ONDEMAND_LAST_FIX_NAME_PROPERTY = "OndemandLastQuickFixerName";
    public static final String ONDEMAND_LAST_FIX_INSTALL_TIME_PROPERTY = "OndemandLastQuickFixerInstallTime";
    public static final String DIFFREENT_FILE_MOVED_PROPERTY = "DifferentFileMoved";
    public static final String CHANGES_ADAPTED_PROPERTY = "ChangesAdapted";
    public static final String QPM = ".qpm";
    public static final String PPM = ".ppm";
    public static String lastFixStatus;
    public static String lastFixName;
    public static String lastFixInstallTime;
    static List list;
    static HashMap installSuccessMap;
    static HashMap revertSuccessMap;
    static HashMap exceptionMap;
    private static String enableUniformLogFormatter;
    
    public static void main(final String[] args) {
        try {
            final String serverHome = getServerHome();
            initLogger(serverHome);
            SilentUpdation.logger.log(Level.INFO, "SilentUpdation Class is initialized");
            SilentUpdation.logger.log(Level.INFO, "Server home : " + serverHome);
            final String realPath = serverHome + File.separator + "bin" + File.separator;
            final String lockFile = realPath + ".lock";
            if (isServerRunning()) {
                SilentUpdation.logger.log(Level.WARNING, "It seems the server to be running. QPPM can't be applied when Server is running. Stop the service and try again");
            }
            else if (!isIncompletePPMInstallationFound(serverHome)) {
                final String fixesHome = serverHome + File.separator + "quickfixer" + File.separator + "qppm";
                final File fixFolder = new File(fixesHome);
                final String backupHome = serverHome + File.separator + SilentUpdation.QUICK_FIX_BACKUP;
                final File backupFolder = new File(backupHome);
                if (!backupFolder.exists()) {
                    backupFolder.mkdir();
                }
                if (fixFolder.exists()) {
                    SilentUpdation.logger.log(Level.INFO, "QuickFixer Folder Exist");
                    final File[] filesList = fixFolder.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(final File pathname) {
                            return pathname.isFile();
                        }
                    });
                    SilentUpdation.logger.log(Level.INFO, "No of Files Present in QuickFixer Folder: " + filesList.length);
                    if (filesList.length > 0) {
                        SilentUpdation.logger.log(Level.INFO, "Files Present in QuickFixer Folder: ");
                        for (final File temp : filesList) {
                            SilentUpdation.logger.log(Level.INFO, temp.getName());
                        }
                    }
                    if (filesList.length > 0) {
                        applyQPPMs(filesList, fixesHome, backupHome, false);
                    }
                }
                final String ondemandQuickFixHome = serverHome + File.separator + "quickfixer" + File.separator + "Ondemand";
                if (new File(ondemandQuickFixHome).exists()) {
                    final File[] ondemandSilentUpdateQPPMsList = getOndemandSilentUpdateQPPMsList();
                    if (ondemandSilentUpdateQPPMsList != null && ondemandSilentUpdateQPPMsList.length > 0) {
                        SilentUpdation.logger.log(Level.INFO, "Going to process on-demand silent update tasks : " + ondemandSilentUpdateQPPMsList.length);
                        applyQPPMs(ondemandSilentUpdateQPPMsList, ondemandQuickFixHome, backupHome + File.separator + "Ondemand", true);
                    }
                    else {
                        SilentUpdation.logger.log(Level.INFO, "There is no on-demand silent update tasks.");
                    }
                }
            }
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.SEVERE, "Exception occurred in SilentUpdation : ", e);
        }
    }
    
    private static boolean isServerRunning() throws Exception {
        int port = -1;
        final String lockFileLocation = getServerHome() + File.separator + "bin" + File.separator + ".lock";
        final File lockFile = new File(lockFileLocation);
        if (lockFile.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(lockFile));
                if (br != null) {
                    port = Integer.parseInt(br.readLine());
                    SilentUpdation.logger.log(Level.INFO, "Value from .lock file  : " + port);
                    if (!isPortFree(port)) {
                        return true;
                    }
                }
            }
            catch (final Exception e) {
                SilentUpdation.logger.log(Level.WARNING, "Exception occured : ", e);
                throw e;
            }
            finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                }
                catch (final Exception exp) {
                    SilentUpdation.logger.log(Level.WARNING, "Exception : ", exp);
                }
            }
        }
        return false;
    }
    
    private static boolean isPortFree(final int port) {
        if (port <= 0) {
            return false;
        }
        ServerSocket sock = null;
        final String bindAddress = System.getProperty("bindaddress");
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
    
    private static File[] getOndemandSilentUpdateQPPMsList() {
        try {
            final File fixesDir = new File(System.getProperty("server.home") + File.separator + "quickfixer" + File.separator + "Ondemand");
            return fixesDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(final File pathname) {
                    return pathname.isFile() && pathname.getAbsolutePath().endsWith(".qpm");
                }
            });
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.SEVERE, "Exception occurred while fetching Ondemand Silent Update fixes : ", e);
            return null;
        }
    }
    
    private static void applyQPPMs(final File[] filesList, final String fixesHome, final String backupHome, final boolean isOndemandQuickFix) throws Exception {
        final String serverHome = getServerHome();
        for (final File file : filesList) {
            updateConstatnts(serverHome);
            if (file.getName().contains(".qpm")) {
                final String fixName = file.getName();
                final long qpmSize = fixName.length() / 1024;
                SilentUpdation.logger.log(Level.INFO, "Size of the QPM file " + fixName + " is " + qpmSize + "kb");
                final String status = checkPreviousAttempt(serverHome, fixName);
                final String ppmName = fixName.replace(".qpm", ".ppm");
                try {
                    SilentUpdation.lastFixName = fixName;
                    move(fixesHome + File.separator + fixName, fixesHome + File.separator + ppmName);
                    SilentUpdation.logger.log(Level.SEVERE, "Renamed the QPM file: " + fixName + " to PPM file: " + ppmName);
                    loadMessages();
                    final SimpleDateFormat time_formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                    SilentUpdation.lastFixInstallTime = time_formatter.format(System.currentTimeMillis());
                    installPPM(fixesHome + File.separator + ppmName, serverHome, fixName, SilentUpdation.list, isOndemandQuickFix);
                }
                catch (final Exception e) {
                    SilentUpdation.logger.log(Level.SEVERE, "Exception occured " + e);
                }
                move(fixesHome + File.separator + ppmName, backupHome + File.separator + fixName);
            }
            else {
                final String fixesArchiveHome = serverHome + File.separator + "quickfixer" + File.separator + "archive";
                move(file.getAbsolutePath(), fixesArchiveHome + File.separator + file.getName());
                final String propertyFile = serverHome + File.separator + SilentUpdation.QUICK_FIX_HISTORY;
                final Properties fixHistory = new Properties();
                fixHistory.setProperty("DifferentFileMoved", "TRUE");
                storeProperties(fixHistory, propertyFile, true, null);
            }
        }
    }
    
    public static void initLogger(final String serverHome) {
        try {
            final String logFileLocation = serverHome + File.separator + "logs";
            final File logfolder = new File(logFileLocation);
            if (!logfolder.exists()) {
                logfolder.mkdir();
            }
            SilentUpdation.logger.setUseParentHandlers(false);
            final String logFileName = serverHome + File.separator + SilentUpdation.SILENT_UPDATION_LOG;
            final FileHandler hand = new FileHandler(logFileName, 5242880, 5, true);
            final SimpleFormatter formatterr = new SimpleFormatter() {
                private Date dat = new Date();
                private static final String FORMAT = "{0,date} {0,time}";
                private MessageFormat formatter;
                private Object[] args = new Object[1];
                private String lineSeparator = System.getProperty("line.separator");
                
                @Override
                public synchronized String format(final LogRecord record) {
                    if (SilentUpdation.enableUniformLogFormatter.equalsIgnoreCase("true")) {
                        final String message = this.formatMessage(record);
                        return LoggerUtil.defaultLogFormatter(record, message);
                    }
                    final StringBuffer sb = new StringBuffer();
                    this.dat.setTime(record.getMillis());
                    this.args[0] = this.dat;
                    final StringBuffer text = new StringBuffer();
                    if (this.formatter == null) {
                        this.formatter = new MessageFormat("{0,date} {0,time}");
                    }
                    this.formatter.format(this.args, text, null);
                    sb.append(text);
                    sb.append(" ");
                    sb.append(" [");
                    sb.append(record.getLoggerName());
                    sb.append("] ");
                    final String message2 = this.formatMessage(record);
                    sb.append(" [");
                    sb.append(record.getLevel().getLocalizedName());
                    sb.append("] ");
                    sb.append(": ");
                    sb.append(message2);
                    if (record.getThrown() != null) {
                        try {
                            final StringWriter sw = new StringWriter();
                            final PrintWriter pw = new PrintWriter(sw);
                            record.getThrown().printStackTrace(pw);
                            pw.close();
                            sb.append(sw.toString());
                        }
                        catch (final Exception ex) {}
                    }
                    sb.append(this.lineSeparator);
                    return sb.toString();
                }
            };
            hand.setFormatter(formatterr);
            SilentUpdation.logger.addHandler(hand);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadMessages() {
        SilentUpdation.installSuccessMap.put("install", "Service Pack installed successfully");
        SilentUpdation.revertSuccessMap.put("revertMsp", "Uninstallation Completed");
        SilentUpdation.revertSuccessMap.put("revertIntp", "Uninstalled successfully");
        SilentUpdation.exceptionMap.put("NullPointer", "might be null in row");
        SilentUpdation.exceptionMap.put("Fatal error", "some problem ");
        SilentUpdation.exceptionMap.put("NumberFormatException", "number mismatch ");
        SilentUpdation.exceptionMap.put("Could not create connection", "connection failed");
        SilentUpdation.exceptionMap.put("Error occurred :: Irrevertable exception occurred.", "Irrevertable exception occurred");
        SilentUpdation.exceptionMap.put("The ppm file that you have specified is not compatible with this product.", "Compatible issue");
        SilentUpdation.list.add(SilentUpdation.installSuccessMap);
        SilentUpdation.list.add(SilentUpdation.revertSuccessMap);
        SilentUpdation.list.add(SilentUpdation.exceptionMap);
    }
    
    public static String checkPreviousAttempt(final String serverHome, final String fixName) {
        String status = "";
        try {
            final String propertyFile = serverHome + File.separator + SilentUpdation.QUICK_FIX_HISTORY;
            if (new File(propertyFile).exists()) {
                final Properties fixHistory = readProperties(propertyFile);
                if (fixHistory.getProperty(fixName + ".status") != null && fixHistory.getProperty(fixName + ".status") != "") {
                    status = fixHistory.getProperty(fixName + ".status");
                }
            }
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.SEVERE, "Exception occured " + e);
        }
        SilentUpdation.logger.log(Level.SEVERE, "In checkPreviousAttempt(), value of status =" + status);
        return status;
    }
    
    public static String updatePropertiesFile(final String serverHome, final String fixName, final String status, final boolean isOndemandQuickFix) {
        try {
            final String propertyFile = serverHome + File.separator + SilentUpdation.QUICK_FIX_HISTORY;
            final Properties fixHistory = new Properties();
            fixHistory.setProperty(fixName + ".status", status);
            fixHistory.setProperty(fixName + ".updatedtime", String.valueOf(System.currentTimeMillis()));
            if (isOndemandQuickFix) {
                fixHistory.setProperty("OndemandLastQuickFixerStatus", SilentUpdation.lastFixStatus);
                fixHistory.setProperty("OndemandLastQuickFixerName", SilentUpdation.lastFixName);
                fixHistory.setProperty("OndemandLastQuickFixerInstallTime", SilentUpdation.lastFixInstallTime);
            }
            else {
                fixHistory.setProperty("LastQuickFixerStatus", SilentUpdation.lastFixStatus);
                fixHistory.setProperty("LastQuickFixerName", SilentUpdation.lastFixName);
                fixHistory.setProperty("LastQuickFixerInstallTime", SilentUpdation.lastFixInstallTime);
            }
            fixHistory.setProperty("ChangesAdapted", "FALSE");
            storeProperties(fixHistory, propertyFile, true, null);
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.SEVERE, "Exception occured " + e);
        }
        return status;
    }
    
    public static void updateMetrackingFile(final String serverHome, final String fixName, final String status, final boolean isOndemandQuickFix) {
        try {
            final SimpleDateFormat time_formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
            final String date = time_formatter.format(System.currentTimeMillis());
            final String propertyFile = serverHome + File.separator + SilentUpdation.QUICK_FIX_TRACKING;
            Properties fixTracking = new Properties();
            String existingStatus;
            String existingFixname;
            String existingDate;
            if (new File(propertyFile).exists()) {
                fixTracking = readProperties(propertyFile);
                existingStatus = fixTracking.getProperty("QuickFixerStatus");
                existingFixname = fixTracking.getProperty("QuickFixerFilename");
                existingDate = fixTracking.getProperty("QuickFixerDate");
                final String[] statusList = existingStatus.split(",");
                if (statusList.length > SilentUpdation.meTrackingConstant) {
                    existingFixname = existingFixname.substring(existingFixname.indexOf(",") + 1);
                    existingStatus = existingStatus.substring(existingStatus.indexOf(",") + 1);
                    existingDate = existingDate.substring(existingDate.indexOf(",") + 1);
                }
                existingFixname = existingFixname + "," + fixName;
                existingStatus = existingStatus + "," + status;
                existingDate = existingDate + "," + date;
            }
            else {
                existingFixname = fixName;
                existingStatus = status;
                existingDate = date;
            }
            fixTracking.setProperty("QuickFixerFilename", existingFixname);
            fixTracking.setProperty("QuickFixerStatus", existingStatus);
            fixTracking.setProperty("QuickFixerDate", existingDate);
            storeProperties(fixTracking, propertyFile, true, null);
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.SEVERE, "Exception occured " + e);
        }
    }
    
    public static void updateConstatnts(final String homePath) {
        final String propertyFile = homePath + SilentUpdation.GENERAL_PROPERTIES;
        try {
            if (new File(propertyFile).exists()) {
                final Properties generalProps = readProperties(propertyFile);
                SilentUpdation.productExeName = generalProps.getProperty("product_exe");
                SilentUpdation.meTrackingConstant = Integer.parseInt(generalProps.getProperty("qpmTrackingLimit"));
                SilentUpdation.maxQpmSize = Integer.parseInt(generalProps.getProperty("maxQpmSize"));
            }
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.SEVERE, "Exception occured " + e);
        }
    }
    
    public static String installPPM(String ppmPath, final String homePath, final String fixName, final List ppmProcessMessage, final boolean isOndemandQuickFix) {
        try {
            ppmPath = "\"" + ppmPath + "\"";
            final HashMap installMessage = ppmProcessMessage.get(0);
            final HashMap revertMessage = ppmProcessMessage.get(1);
            final HashMap exceptionMessage = ppmProcessMessage.get(2);
            final boolean stopFlag = true;
            final String realPath = homePath + File.separator + "bin" + File.separator;
            if (stopFlag) {
                final String driveName = realPath.substring(0, 2);
                String middlePart = realPath.substring(2);
                middlePart = middlePart.replace("/", File.separator);
                final List<String> command = new ArrayList<String>();
                command.add("cmd");
                command.add("/c");
                command.add(driveName);
                command.add("&&");
                command.add("cd");
                command.add(middlePart);
                command.add("&&");
                command.addAll(SilentUpdation.INSTALL_PPM_COMMAND);
                command.add(ppmPath);
                SilentUpdation.logger.log(Level.INFO, "Command executed for installation : " + command.toString());
                final ProcessBuilder builder = new ProcessBuilder(command);
                Process p = builder.start();
                SilentUpdation.logger.log(Level.INFO, "*********************INSTALL PPM START*********************");
                final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    SilentUpdation.logger.log(Level.INFO, "CMD: " + line);
                    if (installMessage.containsValue(line)) {
                        Thread.sleep(10000L);
                        p.waitFor();
                        SilentUpdation.logger.log(Level.INFO, "Installation Success");
                        final List<String> command2 = new ArrayList<String>();
                        command2.add("cmd");
                        command2.add("/c");
                        command2.add(driveName);
                        command2.add("&&");
                        command2.add("cd");
                        command2.add(middlePart);
                        command2.add("&&");
                        command2.add(SilentUpdation.productExeName);
                        command2.add(" -forcelaunch");
                        SilentUpdation.logger.log(Level.INFO, "Command executed for tray icon installation : " + command2.toString());
                        final ProcessBuilder builder2 = new ProcessBuilder(command2);
                        p = builder2.start();
                        p.waitFor();
                        SilentUpdation.lastFixStatus = "successful";
                        updatePropertiesFile(homePath, fixName, "installed", isOndemandQuickFix);
                        updateMetrackingFile(homePath, fixName, "success", isOndemandQuickFix);
                        if (isOndemandQuickFix) {
                            updateQPPMStatus(fixName, "installed");
                        }
                        return line;
                    }
                    if (revertMessage.containsValue(line)) {
                        Thread.sleep(10000L);
                        p.waitFor();
                        SilentUpdation.logger.log(Level.INFO, "Installation Reverted");
                        final List<String> command2 = new ArrayList<String>();
                        command2.add("cmd");
                        command2.add("/c");
                        command2.add(driveName);
                        command2.add("&&");
                        command2.add("cd");
                        command2.add(middlePart);
                        command2.add("&&");
                        command2.add(SilentUpdation.productExeName);
                        command2.add(" -forcelaunch");
                        SilentUpdation.logger.log(Level.INFO, "Command executed for tray icon installation : " + command2.toString());
                        final ProcessBuilder builder2 = new ProcessBuilder(command2);
                        p = builder2.start();
                        p.waitFor();
                        updatePropertiesFile(homePath, fixName, "reverted", isOndemandQuickFix);
                        updateMetrackingFile(homePath, fixName, "reverted", isOndemandQuickFix);
                        if (isOndemandQuickFix) {
                            updateQPPMStatus(fixName, "reverted");
                        }
                        return line;
                    }
                    if (exceptionMessage.containsKey(line)) {
                        SilentUpdation.logger.log(Level.INFO, "Installation Failed");
                        updatePropertiesFile(homePath, fixName, "failed", isOndemandQuickFix);
                        updateMetrackingFile(homePath, fixName, "failed", isOndemandQuickFix);
                        Thread.sleep(10000L);
                        SilentUpdation.logger.log(Level.INFO, "Installation Success");
                        if (isOndemandQuickFix) {
                            updateQPPMStatus(fixName, "failed");
                        }
                        return line;
                    }
                }
                final int exitCode = p.exitValue();
                updatePropertiesFile(homePath, fixName, "failed", isOndemandQuickFix);
                updateMetrackingFile(homePath, fixName, "failed", isOndemandQuickFix);
                SilentUpdation.logger.log(Level.INFO, "exit code for install ppm process is  " + exitCode);
                if (isOndemandQuickFix) {
                    updateQPPMStatus(fixName, "failed");
                }
                return line;
            }
            SilentUpdation.logger.log(Level.INFO, "Product is not stopped");
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.SEVERE, "Exception while installing ppm " + e);
        }
        return null;
    }
    
    public static void updateQPPMStatus(final String fixName, final String status) {
        try {
            SilentUpdation.logger.log(Level.INFO, "Entering updateAppliedQPPMStatus : status - " + status + ", fixName - " + fixName);
            final String qppmUniqueId = fixName.substring(0, fixName.indexOf("."));
            SilentUpdation.logger.log(Level.INFO, "Applied QPPM unique Id " + qppmUniqueId);
            final String qppmStatusPropsPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "SilentUpdate" + File.separator + "qppm-status.props";
            final Properties qppmStatusProps = readProperties(qppmStatusPropsPath);
            ((Hashtable<String, String>)qppmStatusProps).put(qppmUniqueId, status);
            storeProperties(qppmStatusProps, qppmStatusPropsPath, true, null);
            SilentUpdation.logger.log(Level.INFO, "LastAppliedQPPMStatus has been updated.");
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.WARNING, "Exception occurred while update last applied QPPM status :", e);
        }
    }
    
    public static String getServerHome() {
        String path = null;
        try {
            path = System.getProperty("user.dir");
            path = path.substring(0, path.lastIndexOf("\\"));
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.SEVERE, "Exception in finding applicationserverHome  " + e);
        }
        return path;
    }
    
    public static Properties readProperties(final String confFileName) throws Exception {
        final Properties props = new Properties();
        InputStream ism = null;
        try {
            if (new File(confFileName).exists()) {
                ism = new FileInputStream(confFileName);
                props.load(ism);
            }
        }
        catch (final Exception ex) {
            SilentUpdation.logger.log(Level.WARNING, "Caught exception while reading properties from file: " + confFileName, ex);
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex) {
                SilentUpdation.logger.log(Level.SEVERE, "Exception occured " + ex);
            }
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex2) {
                SilentUpdation.logger.log(Level.SEVERE, "Exception occured " + ex2);
            }
        }
        return props;
    }
    
    public static void storeProperties(final Properties props, final String confFileName, final boolean append, final String comments) throws Exception {
        final Properties writeProps = new Properties();
        InputStream ism = null;
        OutputStream osm = null;
        try {
            if (isFileExists(confFileName) && append) {
                ism = readFile(confFileName);
                writeProps.load(ism);
            }
            writeProps.putAll(props);
            osm = writeFile(confFileName);
            writeProps.store(osm, comments);
        }
        catch (final Exception ex) {
            SilentUpdation.logger.log(Level.WARNING, "Caught exception while storing properties: " + props + " with filename: " + confFileName, ex);
            try {
                if (ism != null) {
                    ism.close();
                }
                if (osm != null) {
                    osm.close();
                }
            }
            catch (final Exception ex) {
                SilentUpdation.logger.log(Level.SEVERE, "Exception occured " + ex);
            }
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
                if (osm != null) {
                    osm.close();
                }
            }
            catch (final Exception ex2) {
                SilentUpdation.logger.log(Level.SEVERE, "Exception occured " + ex2);
            }
        }
    }
    
    public static boolean isFileExists(final String fileName) {
        final File file = new File(fileName);
        return file.exists();
    }
    
    public static InputStream readFile(final String fileName) throws Exception {
        InputStream fis = null;
        try {
            if (new File(fileName).exists()) {
                fis = new FileInputStream(fileName);
            }
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.WARNING, "Exception occurred while reading file", e);
            if (fis != null) {
                fis.close();
            }
            throw e;
        }
        return fis;
    }
    
    public static OutputStream writeFile(final String fileName) throws IOException {
        OutputStream fos = null;
        try {
            final File fname = new File(fileName).getParentFile();
            if (!fname.exists()) {
                fname.mkdirs();
            }
            fos = new FileOutputStream(fileName);
        }
        catch (final IOException e) {
            SilentUpdation.logger.log(Level.WARNING, "Exception occurred while writing file", e);
            if (fos != null) {
                fos.close();
            }
            throw e;
        }
        return fos;
    }
    
    public static void move(final String oldLocation, final String newLocation) {
        try {
            final File newFile = new File(newLocation);
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();
            Files.move(Paths.get(oldLocation, new String[0]), Paths.get(newLocation, new String[0]), StandardCopyOption.ATOMIC_MOVE);
            SilentUpdation.logger.log(Level.INFO, oldLocation + " is successfully moved to " + newLocation);
        }
        catch (final Exception e) {
            SilentUpdation.logger.log(Level.SEVERE, "Exception occured while moving the file " + oldLocation);
        }
    }
    
    public static boolean isIncompletePPMInstallationFound(final String serverhome) {
        boolean found = false;
        try {
            final String ppmLockFileNameFull = serverhome + File.separator + "bin" + File.separator + "ppm.lock";
            final File ppmLockFileNameFullFile = new File(ppmLockFileNameFull);
            SilentUpdation.logger.log(Level.INFO, "PPM Lock file path: " + ppmLockFileNameFull);
            if (ppmLockFileNameFullFile.exists()) {
                found = true;
                final Properties ppmLockProps = new Properties();
                ppmLockProps.load(new FileInputStream(ppmLockFileNameFullFile));
                SilentUpdation.logger.log(Level.WARNING, "PPM Lock file found in the setup while silent updation. Properties are: " + ppmLockProps);
            }
        }
        catch (final Exception ex) {
            SilentUpdation.logger.log(Level.WARNING, "Caught exception while checking the existence of ppm lock file.", ex);
        }
        return found;
    }
    
    static {
        SilentUpdation.logger = Logger.getLogger(SilentUpdation.class.getName());
        SILENT_UPDATION_LOG = "logs" + File.separator + "silentupdation%g.log";
        SilentUpdation.formatter = new SimpleFormatter();
        SilentUpdation.dateformat = new SimpleDateFormat("ddMMM''yy");
        SilentUpdation.timeformat = new SimpleDateFormat("HH.mm");
        INSTALL_PPM_COMMAND = new ArrayList<String>(Arrays.asList("UpdMgr.bat", "-u", "conf", "-c", "-option", "i", "-ppmPath"));
        GENERAL_PROPERTIES = "conf" + File.separator + "general_properties.conf";
        SilentUpdation.meTrackingConstant = 20;
        SilentUpdation.maxQpmSize = 10;
        QUICK_FIX_BACKUP = "PatchBackup" + File.separator + "QuickFixerBackup";
        QUICK_FIX_HISTORY = "conf" + File.separator + "User-Conf" + File.separator + "fixHistory.properties";
        QUICK_FIX_TRACKING = "conf" + File.separator + "User-Conf" + File.separator + "fixTracking.properties";
        SilentUpdation.lastFixStatus = "failed";
        SilentUpdation.lastFixName = "";
        SilentUpdation.lastFixInstallTime = "";
        SilentUpdation.list = new ArrayList();
        SilentUpdation.installSuccessMap = new HashMap();
        SilentUpdation.revertSuccessMap = new HashMap();
        SilentUpdation.exceptionMap = new HashMap();
        SilentUpdation.enableUniformLogFormatter = System.getProperty("uniformlogformatter.enable", "false");
    }
}
