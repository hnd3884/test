package com.me.devicemanagement.onpremise.start;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Enumeration;
import java.io.PrintStream;
import com.adventnet.tools.prevalent.ConsoleOut;
import java.util.List;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.io.IOException;
import java.util.Arrays;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import java.util.Collection;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.Writer;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Map;
import java.util.logging.Logger;

public class StartupUtil extends com.me.devicemanagement.framework.start.StartupUtil
{
    public static final String SERVICE_START_MANUAL = "demand";
    public static final String SERVICE_START_AUTO = "auto";
    public static final String SERVICE_START_DISABLE = "disabled";
    public static final String DC_SERVER_32BIT = "32-bit";
    public static final String DC_SERVER_64BIT = "64-bit";
    public static final String OS_32BIT = "32-bit";
    public static final String OS_64BIT = "64-bit";
    private static final Logger LOGGER;
    private static final Map<String, Properties> CONF_CACHE;
    private static StartupUtil startupUtil;
    
    private static Properties getConfCacheForFileName(final String fileName) {
        return fileName.toLowerCase().contains("websettings.conf") ? getConfCacheForFileName(fileName, Boolean.TRUE) : null;
    }
    
    private static Properties getConfCacheForFileName(final String fileName, final Boolean isCloningRequired) {
        Properties tempProps = StartupUtil.CONF_CACHE.get(fileName);
        if (tempProps != null && isCloningRequired) {
            tempProps = (Properties)tempProps.clone();
        }
        return tempProps;
    }
    
    private static void addToConfCache(final String fileName, final Properties confProps) {
        if (fileName.toLowerCase().contains("websettings.conf")) {
            StartupUtil.CONF_CACHE.put(fileName, confProps);
        }
    }
    
    private static Properties loadPropertiesFromFileAndCache(final Path filePath, final String cacheKey) {
        final Properties props = new Properties();
        if (Files.exists(filePath, new LinkOption[0])) {
            try (final BufferedReader reader = Files.newBufferedReader(filePath)) {
                props.load(reader);
            }
            catch (final Exception ex) {
                StartupUtil.LOGGER.log(Level.SEVERE, "Caught exception: " + ex);
            }
            if (cacheKey != null) {
                addToConfCache(cacheKey, props);
            }
        }
        return props;
    }
    
    private static String getCacheKeyForFileName(final Path filePath) {
        return filePath.toAbsolutePath().normalize().toString();
    }
    
    public static Boolean maintenanceCompletedSuccessfully() {
        String serverHome = System.getProperty("server.home");
        boolean returnVal = true;
        FileInputStream fis = null;
        final Properties maintenanceProps = new Properties();
        try {
            if (serverHome != null) {
                serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            }
            final File lockFile = new File(serverHome + File.separator + "bin" + File.separator + ".maintenanceLock");
            if (lockFile.exists()) {
                fis = new FileInputStream(lockFile);
                maintenanceProps.load(fis);
                String stopServerStart = "false";
                if (maintenanceProps.containsKey("stopServerStartUp")) {
                    stopServerStart = maintenanceProps.getProperty("stopServerStartUp");
                }
                if (stopServerStart != null && stopServerStart.equalsIgnoreCase("true")) {
                    returnVal = false;
                }
                else if (isPidRunning(maintenanceProps.getProperty("pid"))) {
                    returnVal = false;
                }
                else {
                    lockFile.delete();
                    returnVal = true;
                }
            }
        }
        catch (final Exception e) {
            StartupUtil.LOGGER.log(Level.SEVERE, "Exception in maintenanceCompletedSuccessfully ", e);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e) {
                StartupUtil.LOGGER.log(Level.SEVERE, "Exception in maintenanceCompletedSuccessfully finally ", e);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e2) {
                StartupUtil.LOGGER.log(Level.SEVERE, "Exception in maintenanceCompletedSuccessfully finally ", e2);
            }
        }
        return returnVal;
    }
    
    public static void deleteBackupLabel() {
        final File file = new File(System.getProperty("db.home", ".." + File.separator + "pgsql") + File.separator + "data" + File.separator + "backup_label");
        StartupUtil.LOGGER.log(Level.INFO, "Checking for existence of backup_label file :: {0}", file.exists());
        if (file.exists()) {
            StartupUtil.LOGGER.log(Level.INFO, "Attempting to delete backup_label :: {0}", file.delete());
        }
    }
    
    public static StartupUtil getInstance() {
        if (StartupUtil.startupUtil == null) {
            StartupUtil.startupUtil = new StartupUtil();
        }
        return StartupUtil.startupUtil;
    }
    
    public static void centerWindow(final Window win) {
        final Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (scrSize.width - win.getWidth()) / 2;
        final int y = (scrSize.height - win.getHeight()) / 2;
        win.setLocation(x, y);
    }
    
    public static String getString(final String str) {
        return str;
    }
    
    public static Image loadImage(final String fileName) {
        return Toolkit.getDefaultToolkit().getImage(fileName);
    }
    
    public static void storeProperties(final Properties newprops, final String confFileName, final boolean append) {
        storeProperties(newprops, Paths.get(confFileName, new String[0]), append);
    }
    
    public static void storeProperties(final Properties newprops, final String confFileName) {
        storeProperties(newprops, Paths.get(confFileName, new String[0]), true);
    }
    
    public static void storeProperties(final Properties newprops, final String confFileName, final String comments) {
        storeProperties(newprops, Paths.get(confFileName, new String[0]), comments, true);
    }
    
    private static void storeProperties(final Properties newprops, final Path filePath, final boolean append) {
        storeProperties(newprops, filePath, null, append);
    }
    
    private static void storeProperties(final Properties newprops, final Path filePath, final String comments, final boolean append) {
        String cacheKey = null;
        Properties props = null;
        try {
            cacheKey = getCacheKeyForFileName(filePath);
            props = getConfCacheForFileName(cacheKey);
            props = (append ? ((props == null) ? loadPropertiesFromFileAndCache(filePath, null) : props) : newprops);
        }
        catch (final Exception ex) {
            StartupUtil.LOGGER.log(Level.SEVERE, "Caught exception: " + ex);
            props = new Properties();
        }
        try (final BufferedWriter writer = Files.newBufferedWriter(filePath, new OpenOption[0])) {
            if (append) {
                props.putAll(newprops);
            }
            props.store(writer, comments);
            addToConfCache(cacheKey, props);
        }
        catch (final Exception ex) {
            StartupUtil.LOGGER.log(Level.SEVERE, "Caught exception: " + ex);
        }
    }
    
    public static void removeProperties(final ArrayList<String> keys, final String confFileName) {
        try {
            final Path filePath = Paths.get(confFileName, new String[0]);
            final String cacheKey = getCacheKeyForFileName(filePath);
            Properties props = getConfCacheForFileName(cacheKey);
            props = ((props == null) ? loadPropertiesFromFileAndCache(filePath, null) : props);
            ((Hashtable<Object, V>)props).keySet().removeAll(keys);
            storeProperties(props, filePath, false);
        }
        catch (final Exception ex) {
            StartupUtil.LOGGER.log(Level.SEVERE, "Caught exception: " + ex);
        }
    }
    
    public static Properties getProperties(final String confFileName) {
        if (new File(confFileName).getName().equals("general_properties.conf")) {
            try {
                return GeneralPropertiesLoader.getInstance().getProperties();
            }
            catch (final Exception ex) {
                StartupUtil.LOGGER.log(Level.SEVERE, "Caught exception: in Properties from general_properties" + ex);
            }
        }
        Properties props;
        try {
            final Path filePath = Paths.get(confFileName, new String[0]);
            final String cacheKey = getCacheKeyForFileName(filePath);
            props = getConfCacheForFileName(cacheKey);
            props = ((props == null) ? loadPropertiesFromFileAndCache(filePath, cacheKey) : props);
        }
        catch (final Exception ex2) {
            StartupUtil.LOGGER.log(Level.SEVERE, "Caught exception: " + ex2);
            props = new Properties();
        }
        return props;
    }
    
    public static String getCurrentTimeWithDate() {
        StartupUtil.LOGGER.log(Level.FINE, "getCurrentTimeWithDate method is called...");
        final Date date = new Date();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM  M dd,yyyy hh:mm:ss a");
        return dateFormat.format(date);
    }
    
    public static String changeServiceStartupType(final String serviceName, final String startupType) {
        String result = "";
        String scConfigCmd = "";
        try {
            final String[] command = { "sc", "config", serviceName, "start=" + startupType };
            scConfigCmd = command.toString();
            StartupUtil.LOGGER.log(Level.INFO, "SC CONFIG Command to be executed: " + scConfigCmd);
            final ProcessBuilder builder = new ProcessBuilder(command);
            final Process p = builder.start();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                result = result + str + "\n";
            }
        }
        catch (final Exception ex) {
            StartupUtil.LOGGER.log(Level.WARNING, "Caught error while executing the command: " + scConfigCmd + " exception: " + ex);
        }
        return result;
    }
    
    public static boolean isServiceRunning(final String serviceName) {
        boolean result = false;
        String resultStr = "";
        String scQueryCmd = "";
        try {
            final String[] command = { "sc", "query", serviceName };
            scQueryCmd = command.toString();
            final ProcessBuilder builder = new ProcessBuilder(command);
            final Process p = builder.start();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                resultStr = resultStr + str + "\n";
            }
            if (resultStr.toUpperCase().contains("RUNNING")) {
                result = true;
            }
        }
        catch (final Exception ex) {
            StartupUtil.LOGGER.log(Level.SEVERE, "Caught error while executing the command: " + scQueryCmd + " exception: " + ex);
        }
        return result;
    }
    
    public static boolean isServiceStopped(final String serviceName) {
        boolean result = false;
        String resultStr = "";
        String scQueryCmd = "";
        try {
            final String[] command = { "sc", "query", serviceName };
            scQueryCmd = command.toString();
            final ProcessBuilder builder = new ProcessBuilder(command);
            final Process p = builder.start();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                resultStr = resultStr + str + "\n";
            }
            StartupUtil.LOGGER.log(Level.INFO, "result String is for :  " + scQueryCmd + "is  :  " + resultStr);
            if (resultStr.contains("OpenService FAILED 1060") || resultStr.contains("1060")) {
                result = true;
            }
            if (resultStr.toUpperCase().contains("STOPPED")) {
                result = true;
            }
        }
        catch (final Exception ex) {
            StartupUtil.LOGGER.log(Level.SEVERE, "Caught error while executing the command: " + scQueryCmd + " exception: " + ex);
        }
        return result;
    }
    
    public static Boolean isDCProduct64bit() throws Exception {
        try {
            Boolean isDCProduct64bit = null;
            final String javaOSArch = System.getProperty("sun.arch.data.model");
            if (javaOSArch != null && !javaOSArch.isEmpty() && javaOSArch.equalsIgnoreCase("32")) {
                isDCProduct64bit = false;
                StartupUtil.LOGGER.log(Level.INFO, "DC Product Architecture is: " + javaOSArch + "  (32-bit)");
            }
            else if (javaOSArch != null && !javaOSArch.isEmpty() && javaOSArch.equalsIgnoreCase("64")) {
                isDCProduct64bit = true;
                StartupUtil.LOGGER.log(Level.INFO, "DC Product Architecture is: " + javaOSArch + "  (64-bit)");
            }
            return isDCProduct64bit;
        }
        catch (final Exception ex) {
            StartupUtil.LOGGER.log(Level.WARNING, "Exception occurred in method isDCProduct64bit()... Exception : ", ex);
            throw ex;
        }
    }
    
    public static String dcProductArch() throws Exception {
        try {
            final Boolean isDCProduct64bit = isDCProduct64bit();
            String dcProductArch = null;
            if (isDCProduct64bit != null && isDCProduct64bit) {
                dcProductArch = "64-bit";
            }
            else if (isDCProduct64bit != null && !isDCProduct64bit) {
                dcProductArch = "32-bit";
            }
            return dcProductArch;
        }
        catch (final Exception ex) {
            StartupUtil.LOGGER.log(Level.WARNING, "Exception occurred in method dcProductArch()... Exception : ", ex);
            return null;
        }
    }
    
    public static String getInstallationDirName() throws Exception {
        final String serverHome = System.getProperty("server.home");
        final File serverHomeFile = new File(serverHome).getCanonicalFile();
        return serverHomeFile.getName();
    }
    
    public static boolean isPidRunning(final String pid) {
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
            StartupUtil.LOGGER.log(Level.SEVERE, "Exception getting isPidRunning in MaintenanceUtil", e);
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e2) {
                StartupUtil.LOGGER.log(Level.SEVERE, "Exception finally block ", e2);
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e3) {
                StartupUtil.LOGGER.log(Level.SEVERE, "Exception finally block ", e3);
            }
        }
        return false;
    }
    
    public static void terminatePID(final String pid) {
        try {
            final ProcessBuilder builder = new ProcessBuilder(Arrays.asList("cmd.exe", "/C", "Taskkill", "/PID", pid, "/F"));
            final Process process = builder.start();
            process.getOutputStream().close();
            process.waitFor();
        }
        catch (final Exception e) {
            StartupUtil.LOGGER.log(Level.SEVERE, "Exception getting isPidRunning ", e);
        }
    }
    
    public static boolean fileContentEquals(final File file1, final File file2) throws IOException {
        final boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }
        if (!file1Exists) {
            return true;
        }
        if (file1.isDirectory() || file2.isDirectory()) {
            throw new IOException("Can't compare directories, only files");
        }
        if (file1.length() != file2.length()) {
            return false;
        }
        if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            return true;
        }
        InputStream input1 = null;
        FileInputStream input2 = null;
        boolean var5;
        try {
            input1 = new FileInputStream(file1);
            input2 = new FileInputStream(file2);
            var5 = com.me.devicemanagement.framework.start.StartupUtil.contentEquals(input1, input2);
        }
        finally {
            closeQuietly(input1);
            closeQuietly(input2);
        }
        return var5;
    }
    
    public static String readFileAsString(final File file, final String encoding) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            final StringWriter sw = new StringWriter();
            InputStreamReader input;
            if (encoding != null) {
                input = new InputStreamReader(in, encoding);
            }
            else {
                input = new InputStreamReader(in);
            }
            final char[] buffer = new char[4096];
            int count = 0;
            final boolean var4 = false;
            int n;
            while (-1 != (n = input.read(buffer))) {
                sw.write(buffer, 0, n);
                count += n;
            }
            return sw.toString();
        }
        finally {
            closeQuietly(in);
        }
    }
    
    public static void writeStringToFile(final File file, final String data, final String encoding) throws IOException {
        final FileOutputStream out = new FileOutputStream(file);
        try {
            if (data != null) {
                if (encoding == null) {
                    out.write(data.getBytes());
                }
                else {
                    out.write(data.getBytes(encoding));
                }
            }
        }
        finally {
            closeQuietly(out);
        }
    }
    
    public static void writeLines(final File file, final String encoding, final Collection lines) throws IOException {
        writeLines(file, encoding, lines, null);
    }
    
    public static void writeLines(final File file, final String encoding, final Collection lines, String lineEnding) throws IOException {
        final FileOutputStream out = new FileOutputStream(file);
        try {
            if (lines == null) {
                return;
            }
            if (lineEnding == null) {
                lineEnding = "\r\n";
            }
            for (final Object line : lines) {
                if (line != null) {
                    if (encoding == null) {
                        out.write(line.toString().getBytes());
                    }
                    else {
                        out.write(line.toString().getBytes(encoding));
                    }
                }
                out.write((encoding == null) ? lineEnding.getBytes() : lineEnding.getBytes(encoding));
            }
        }
        finally {
            closeQuietly(out);
        }
    }
    
    public static List readAllLines(final File file, final String encoding) throws IOException {
        FileInputStream in = null;
        final List lines = new ArrayList();
        try {
            in = new FileInputStream(file);
            InputStreamReader input;
            if (encoding != null) {
                input = new InputStreamReader(in, encoding);
            }
            else {
                input = new InputStreamReader(in);
            }
            final BufferedReader reader = new BufferedReader(input);
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lines.add(line);
            }
        }
        finally {
            closeQuietly(in);
        }
        return lines;
    }
    
    private static void closeQuietly(final InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (final IOException ex) {}
    }
    
    private static void closeQuietly(final OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (final IOException ex) {}
    }
    
    public static void triggerServerShutdown(final String reasonForShutDown) {
        Thread.currentThread();
        Thread.dumpStack();
        ConsoleOut.println("Going to shut down the server. Reason :: " + reasonForShutDown);
        ConsoleOut.println("Shutdown the SERVER Completely");
    }
    
    public void initSystemStreams(final String logFolderHome) {
        File startLogOutFile = null;
        if (logFolderHome != null) {
            startLogOutFile = new File(logFolderHome, "startout.log");
        }
        else {
            startLogOutFile = new File("startout.log");
        }
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(startLogOutFile, true);
            System.setOut(new PrintStream(outStream));
            System.setErr(new PrintStream(outStream));
        }
        catch (final Exception e) {
            StartupUtil.LOGGER.log(Level.SEVERE, "Exception while starting");
            e.printStackTrace();
        }
        finally {
            if (outStream != null) {
                try {
                    StartupUtil.LOGGER.log(Level.INFO, "closing output stream");
                    outStream.close();
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    public void printProperties(final Properties props, final PrintStream outStream) {
        final Enumeration enu = props.propertyNames();
        while (enu.hasMoreElements()) {
            final String name = enu.nextElement().toString();
            final String value = props.getProperty(name);
            outStream.println("\t" + name + " = " + value);
        }
        outStream.println("");
    }
    
    public void printStr(final String s) {
        StartupUtil.LOGGER.log(Level.INFO, s);
    }
    
    public void printErrStr(final String s) {
        StartupUtil.LOGGER.log(Level.INFO, s);
    }
    
    public void printChar(final char c) {
        StartupUtil.LOGGER.log(Level.INFO, new Character(c).toString());
    }
    
    public void printCharErr(final char c) {
        StartupUtil.LOGGER.log(Level.SEVERE, new Character(c).toString());
    }
    
    public static int executeCommand(final String... commandWithArgs) {
        int exitValue = -1;
        StartupUtil.LOGGER.log(Level.INFO, "----------------------- In Execute command ----------------------------");
        String output = "";
        BufferedReader commandOutput = null;
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
            StartupUtil.LOGGER.log(Level.INFO, "COMMAND: {0}", processBuilder.command());
            processBuilder.redirectErrorStream(true);
            final Process process = processBuilder.start();
            commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = "";
            while ((s = commandOutput.readLine()) != null) {
                StartupUtil.LOGGER.log(Level.INFO, s);
                output += s;
            }
            exitValue = process.waitFor();
            StartupUtil.LOGGER.log(Level.INFO, "EXIT VALUE :: {0}", exitValue);
            StartupUtil.LOGGER.log(Level.INFO, "OUT : " + output);
        }
        catch (final IOException ioe) {
            StartupUtil.LOGGER.log(Level.WARNING, "IOException while executing command " + Arrays.asList(commandWithArgs), ioe);
        }
        catch (final InterruptedException ie) {
            StartupUtil.LOGGER.log(Level.WARNING, "IOException while executing command " + Arrays.asList(commandWithArgs), ie);
        }
        finally {
            try {
                if (commandOutput != null) {
                    commandOutput.close();
                }
            }
            catch (final Exception exp) {
                StartupUtil.LOGGER.log(Level.WARNING, "Exception : ", exp);
            }
        }
        StartupUtil.LOGGER.log(Level.INFO, "---------------------- End of Execute command -------------------------");
        return exitValue;
    }
    
    static {
        LOGGER = Logger.getLogger(StartupUtil.class.getName());
        CONF_CACHE = new LinkedHashMap<String, Properties>(5, 0.7f, Boolean.TRUE) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<String, Properties> eldest) {
                return this.size() > 5;
            }
        };
    }
}
