package com.me.devicemanagement.onpremise.start.util;

import java.net.ServerSocket;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.util.logging.Level;
import java.io.FileInputStream;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class NSStartUpUtil
{
    protected static final Logger LOGGER;
    public static final String NS_SETTINGS_CONF_FILE;
    public static final String NS_EXE_NAME;
    public static final String CUSTOM_NS_SETTINGS_CONF_FILE;
    public static final String NS_PORT = "ns.port";
    public static final String NS_SERVICE_NAME;
    public static final String NS_SERVICE_DISPLAY_NAME;
    
    protected static Properties readProperties(final String confFileName) throws Exception {
        if (new File(confFileName).getName().equals("general_properties.conf")) {
            return GeneralPropertiesLoader.getInstance().getProperties();
        }
        final Properties props = new Properties();
        InputStream ism = null;
        try {
            if (new File(confFileName).exists()) {
                ism = new FileInputStream(confFileName);
                props.load(ism);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(NSStartUpUtil.class.getName()).log(Level.WARNING, "Caught exception while reading properties from file: " + confFileName, ex);
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return props;
    }
    
    public static void main(final String[] args) {
        try {
            if (args.length < 1) {
                showSyntaxAndExit(true);
            }
            final String action = args[0];
            performNSServerAction(action);
        }
        catch (final Exception ex) {
            NSStartUpUtil.LOGGER.log(Level.WARNING, "Caught some error: " + ex);
            NSStartUpUtil.LOGGER.log(Level.WARNING, "Requested operation is failed.");
            ex.printStackTrace();
            showSyntaxAndExit(true);
        }
    }
    
    protected static void performNSServerAction(final String action) throws Exception {
        if (action.equalsIgnoreCase("install")) {
            reinstallNSServer();
        }
        else if (action.equalsIgnoreCase("reinstall")) {
            stopNSServer();
            reinstallNSServer();
        }
        else if (action.equalsIgnoreCase("start")) {
            startNSServer();
        }
        else if (action.equalsIgnoreCase("stop")) {
            stopNSServer();
        }
        else if (action.equalsIgnoreCase("uninstall")) {
            uninstallNSServer();
        }
        else {
            showSyntaxAndExit(true);
        }
    }
    
    protected static void showSyntaxAndExit(final boolean exit) {
        NSStartUpUtil.LOGGER.log(Level.INFO, "USAGE: com.adventnet.sym.start.util.NSUtil [ACTION]");
        NSStartUpUtil.LOGGER.log(Level.INFO, "ACTION can be any one from install, reinstall, uninstall, start, stop");
        NSStartUpUtil.LOGGER.log(Level.INFO, "server.home system property must be set to jvm");
        if (exit) {
            System.exit(1);
        }
    }
    
    public static int reinstallNSServer() throws Exception {
        try {
            final String nsPortStr = getNSPort();
            return reinstallNSServer(nsPortStr);
        }
        catch (final Exception ex) {
            NSStartUpUtil.LOGGER.log(Level.WARNING, "Caught error while restarting the DCNSServer ");
            throw ex;
        }
    }
    
    public static int reinstallNSServer(final String nsPortStr) throws Exception {
        int result = 0;
        final List cmdList = getNSServiceInfo();
        cmdList.add("-a");
        cmdList.add("reinstall");
        cmdList.add("-k");
        cmdList.add(nsPortStr);
        result = executeCommand(cmdList);
        return result;
    }
    
    public static int changeNSPort(final String nsPortStr) throws Exception {
        int result = 0;
        final List cmdList = getNSServiceInfo();
        cmdList.add("-a");
        cmdList.add("changeport");
        cmdList.add("-k");
        cmdList.add(nsPortStr);
        NSStartUpUtil.LOGGER.log(Level.INFO, "DCNSServer command to be executed: " + cmdList.toString());
        result = executeCommand(cmdList);
        return result;
    }
    
    private static List getNSServiceInfo() {
        final List<String> cmdList = new ArrayList<String>();
        try {
            String serverHome = System.getProperty("server.home");
            if (serverHome != null) {
                serverHome = new File(serverHome).getCanonicalPath();
            }
            final String dcnsserverExePath = "\"" + serverHome + File.separator + NSStartUpUtil.NS_EXE_NAME + "\"";
            cmdList.add(dcnsserverExePath);
            cmdList.add("-n");
            final String serviceName = getNSServiceName();
            cmdList.add(serviceName);
            final String displayName = getNSServiceDisplayName();
            cmdList.add("-d");
            cmdList.add(displayName);
        }
        catch (final Exception e) {
            NSStartUpUtil.LOGGER.log(Level.WARNING, "Exception occured while getNSServiceInfo(): " + e.getMessage());
        }
        return cmdList;
    }
    
    public static int startNSServer() throws Exception {
        int result = -1;
        final List cmdList = getNSServiceInfo();
        cmdList.add("-a");
        cmdList.add("start");
        try {
            NSStartUpUtil.LOGGER.log(Level.INFO, "DCNSServer command to be executed: " + cmdList.toString());
            result = executeCommand(cmdList);
        }
        catch (final Exception ex) {
            NSStartUpUtil.LOGGER.log(Level.WARNING, "Caught error while executing the command: " + cmdList.toString() + " exception: " + ex);
            throw ex;
        }
        return result;
    }
    
    public static int stopNSServer() throws Exception {
        int result = -1;
        final List cmdList = getNSServiceInfo();
        cmdList.add("-a");
        cmdList.add("stop");
        try {
            NSStartUpUtil.LOGGER.log(Level.INFO, "DCNSServer command to be executed: " + cmdList.toString());
            result = executeCommand(cmdList);
        }
        catch (final Exception ex) {
            NSStartUpUtil.LOGGER.log(Level.WARNING, "Caught error while executing the command: " + cmdList.toString() + " exception: " + ex);
            throw ex;
        }
        return result;
    }
    
    public static int uninstallNSServer() throws Exception {
        int result = -1;
        List cmdList = new ArrayList();
        try {
            cmdList = getNSServiceInfo();
            cmdList.add("-a");
            cmdList.add("uninstall");
            NSStartUpUtil.LOGGER.log(Level.INFO, "DCNSServer command to be executed: " + cmdList.toString());
            result = executeCommand(cmdList);
        }
        catch (final Exception ex) {
            NSStartUpUtil.LOGGER.log(Level.WARNING, "Caught error while executing the command: " + cmdList.toString() + " exception: " + ex);
            throw ex;
        }
        return result;
    }
    
    public static String getNSPort() throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + NSStartUpUtil.NS_SETTINGS_CONF_FILE;
            final Properties portProps = StartupUtil.getProperties(fname);
            final String fname2 = serverHome + File.separator + NSStartUpUtil.CUSTOM_NS_SETTINGS_CONF_FILE;
            portProps.putAll(StartupUtil.getProperties(fname2));
            final String nsPortStr = portProps.getProperty("ns.port");
            return nsPortStr;
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static String getNSServiceName() {
        String nsServiceName = NSStartUpUtil.NS_SERVICE_NAME;
        if (nsServiceName != null) {
            if (!nsServiceName.isEmpty()) {
                return nsServiceName;
            }
        }
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + NSStartUpUtil.NS_SETTINGS_CONF_FILE;
            final Properties portProps = StartupUtil.getProperties(fname);
            final String fname2 = serverHome + File.separator + NSStartUpUtil.CUSTOM_NS_SETTINGS_CONF_FILE;
            portProps.putAll(StartupUtil.getProperties(fname2));
            nsServiceName = portProps.getProperty("ns.service.name");
            if (nsServiceName != null && !nsServiceName.startsWith("\"")) {
                nsServiceName = "\"" + nsServiceName + "\"";
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return nsServiceName;
    }
    
    public static String getNSServiceDisplayName() {
        String nsDisplayName = NSStartUpUtil.NS_SERVICE_DISPLAY_NAME;
        if (nsDisplayName != null) {
            if (!nsDisplayName.isEmpty()) {
                return nsDisplayName;
            }
        }
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + NSStartUpUtil.NS_SETTINGS_CONF_FILE;
            final Properties portProps = StartupUtil.getProperties(fname);
            final String fname2 = serverHome + File.separator + NSStartUpUtil.CUSTOM_NS_SETTINGS_CONF_FILE;
            portProps.putAll(StartupUtil.getProperties(fname2));
            nsDisplayName = portProps.getProperty("ns.display.name");
            if (nsDisplayName != null && !nsDisplayName.startsWith("\"")) {
                nsDisplayName = "\"" + nsDisplayName + "\"";
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return nsDisplayName;
    }
    
    private static int executeCommand(final List cmdList) throws Exception {
        String result = "";
        int exitcode = -1;
        try {
            final ProcessBuilder proc = new ProcessBuilder(cmdList);
            final Process p = proc.start();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                result = result + str + "\n";
            }
            exitcode = p.waitFor();
        }
        catch (final Exception ex) {
            NSStartUpUtil.LOGGER.log(Level.WARNING, "Caught error while executing the command: " + cmdList.toString() + " exception: " + ex);
            throw ex;
        }
        return exitcode;
    }
    
    private static String getNSServerPreCommand() throws Exception {
        String serverHome = System.getProperty("server.home");
        if (serverHome != null) {
            serverHome = new File(serverHome).getCanonicalPath();
        }
        String dcnsserverExePath = "\"" + serverHome + File.separator + NSStartUpUtil.NS_EXE_NAME + "\"";
        final String serviceName = getNSServiceName();
        if (serviceName != null) {
            dcnsserverExePath = dcnsserverExePath + " -n " + serviceName;
        }
        final String displayName = getNSServiceDisplayName();
        if (displayName != null) {
            dcnsserverExePath = dcnsserverExePath + " -d " + displayName;
        }
        return dcnsserverExePath;
    }
    
    public static boolean checkPortAvailability(final int portNumber) {
        try {
            final ServerSocket sock = new ServerSocket(portNumber);
            sock.close();
            return true;
        }
        catch (final Exception exp) {
            return false;
        }
    }
    
    public static Properties getNSSettings() throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + NSStartUpUtil.NS_SETTINGS_CONF_FILE;
            final Properties portProps = StartupUtil.getProperties(fname);
            final String fname2 = serverHome + File.separator + NSStartUpUtil.CUSTOM_NS_SETTINGS_CONF_FILE;
            portProps.putAll(StartupUtil.getProperties(fname2));
            return portProps;
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static boolean isNSServiceRunning() {
        boolean result = false;
        final String serviceName = getNSServiceName();
        result = StartupUtil.isServiceRunning(serviceName);
        return result;
    }
    
    public static boolean isNSServiceStopped() {
        boolean result = false;
        final String serviceName = getNSServiceName();
        result = StartupUtil.isServiceStopped(serviceName);
        return result;
    }
    
    static {
        LOGGER = Logger.getLogger("NSControllerLogger");
        NS_SETTINGS_CONF_FILE = "conf" + File.separator + "dcnssettings.conf";
        NS_EXE_NAME = "bin" + File.separator + "dcnotificationserver.exe";
        CUSTOM_NS_SETTINGS_CONF_FILE = "conf" + File.separator + "custom_dcnssettings.conf";
        final String file = System.getProperty("server.home") + File.separator + NSStartUpUtil.NS_SETTINGS_CONF_FILE;
        final String customFile = System.getProperty("server.home") + File.separator + NSStartUpUtil.CUSTOM_NS_SETTINGS_CONF_FILE;
        Properties properties = null;
        try {
            properties = readProperties(file);
            properties.putAll(readProperties(customFile));
        }
        catch (final Exception e) {
            Logger.getLogger(NSStartUpUtil.class.getName()).log(Level.SEVERE, "Unable to read  notification server properties");
        }
        NS_SERVICE_NAME = properties.getProperty("ns.service.name");
        NS_SERVICE_DISPLAY_NAME = properties.getProperty("ns.display.name");
    }
}
