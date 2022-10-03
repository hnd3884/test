package com.me.devicemanagement.onpremise.start.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.github.odiszapc.nginxparser.NgxDumper;
import com.github.odiszapc.nginxparser.NgxEntry;
import com.github.odiszapc.nginxparser.NgxParam;
import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxConfig;
import java.util.Hashtable;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.net.InetAddress;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import java.util.Properties;
import java.util.HashMap;
import java.util.Scanner;
import java.io.InputStream;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class NginxServerUtils extends WebServerUtil
{
    private static final String FS;
    private static final String NGINX_EXE = "dcnginx.exe";
    public static final String NGINX_PROXY_TEMPLATE_FILE = "nginx-proxy.conf.template";
    private static final String NGINX_PROXY_FILE = "nginx-proxy.conf";
    private static final String NGINX_SSL_PROXY_FILE = "nginx-ssl-proxy.conf";
    private static final String PROXY_HOSTNAME_PROPERTY = "host.name";
    private static final String PROXY_PORT_PROPERTY = "port";
    private static final String NGINX_CONF_DIR;
    public static final String NGINX_CONF_FILE;
    public static final String NGINXSSL_CONF_FILE;
    public static final String NGINX_PROXY_CONF_FILE;
    private static final String NGINX_CONF_TEMPLATE_FILE;
    private static final String NGINXSSL_CONF_TEMPLATE_FILE;
    public static final String NGINX_PROXY_CONF_TEMPLATE_FILE;
    public static final String NGINX_STOPPED_ALREADY = "NGINX STOPPED ALREADY";
    public static final String NGINX_STARTUP_TRACK_FILE = "NginxErrorFile.json";
    public static final String OS_VERSION = "OS_Version";
    public static final String OS_ARCH = "OS_Architecture";
    public static final String NGINX_ERROR = "Nginx_Error";
    public static final String NGINX_TEST_RESULT = "Nginx_Test_Result";
    public static final String PROXYPASS_LOCATION = "proxypass.loc";
    public static final String AJP_PASS = "ajp_keep_conn on;\najp_pass tomcat;";
    public static final String NGINX_TEMP_CONF = "nginx_temp.conf";
    public static final String STANDALONE_CONF_TEMPLATE = "nginx_standalone.conf.template";
    public static final String SERVER_LIMITING_SETTINGS;
    public static final String DEFAULT_SERVER_LIMITING_SETTINGS;
    private static String nginxConfDirPath;
    
    public static String invokeNginxExe(final String serverHome, final String action) throws IOException {
        final String nginxExe = serverHome + NginxServerUtils.FS + "nginx" + NginxServerUtils.FS + "dcnginx.exe";
        String commandToBeExecuted = "";
        if ("start".equalsIgnoreCase(action)) {
            commandToBeExecuted = nginxExe;
        }
        else if ("stop".equalsIgnoreCase(action)) {
            commandToBeExecuted = nginxExe + "-s stop";
        }
        else if ("reload".equalsIgnoreCase(action)) {
            commandToBeExecuted = nginxExe + "-s reload";
        }
        NginxServerUtils.logger.log(Level.INFO, "Nginx Exe Invoked. Command to be executed " + commandToBeExecuted);
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { commandToBeExecuted });
        processBuilder.environment().put("OPENSSL_CONF", getNginxConfDirPath());
        final Process process = processBuilder.start();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = "";
        String buffer;
        while ((buffer = bufferedReader.readLine()) != null) {
            result = result + buffer + "\n";
        }
        return result;
    }
    
    public static String startNginxServer(final String serverHome) throws Exception {
        NginxServerUtils.logger.log(Level.INFO, "Entered into startNginxServer Method. Checking whether nginx server is already running. ");
        if (isNginxServerRunning()) {
            NginxServerUtils.logger.log(Level.INFO, "Nginx Server is already running. Going to stop the process");
            final String stopResult = stopNginxServer(serverHome);
            NginxServerUtils.logger.log(Level.INFO, "Result of stopping Nginx server " + stopResult);
        }
        else {
            NginxServerUtils.logger.log(Level.INFO, "Nginx Server is not running already. ");
        }
        final String nginxDir = serverHome + NginxServerUtils.FS + "nginx";
        NginxServerUtils.logger.log(Level.INFO, "Going to start Nginx Server ");
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { nginxDir + NginxServerUtils.FS + "dcnginx.exe" });
        processBuilder.environment().put("OPENSSL_CONF", getNginxConfDirPath());
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(nginxDir));
        final Process process = processBuilder.start();
        Thread.sleep(15000L);
        if (isNginxServerRunning()) {
            NginxServerUtils.logger.log(Level.INFO, "Successfully Started Nginx Server");
            return "Success";
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = "";
        String buffer;
        while ((buffer = bufferedReader.readLine()) != null) {
            result = result + buffer + "\n";
        }
        if ("".equals(result)) {
            bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((buffer = bufferedReader.readLine()) != null) {
                result = result + buffer + "\n";
            }
        }
        NginxServerUtils.logger.log(Level.INFO, "Unable to start Nginx server . Result " + result);
        return result;
    }
    
    public static String stopNginxServer(final String serverHome) throws Exception {
        return stopNginxServer(serverHome, Boolean.FALSE);
    }
    
    public static String stopNginxServer(final String serverHome, final Boolean forceStop) throws Exception {
        final String stopStatus = stopNginx(serverHome);
        if (forceStop != null && forceStop) {
            final Boolean status = nginxForceStop(serverHome);
            NginxServerUtils.logger.log(Level.INFO, "Force Stopping Status for Nginx Web Server " + status);
        }
        return stopStatus;
    }
    
    private static String stopNginx(final String serverHome) throws Exception {
        final String[] nginxStopCommand = { serverHome + File.separator + "nginx" + File.separator + "dcnginx.exe", "-s", "stop" };
        NginxServerUtils.logger.log(Level.INFO, "Nginx stop command " + Arrays.toString(nginxStopCommand));
        String result = "";
        if (isNginxServerRunning()) {
            final ProcessBuilder processBuilder = new ProcessBuilder(nginxStopCommand);
            processBuilder.directory(new File(serverHome + File.separator + "nginx"));
            processBuilder.environment().put("OPENSSL_CONF", getNginxConfDirPath());
            final Process process = processBuilder.start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                result = result + buffer + "\n";
            }
            if (isNginxServerRunning()) {
                NginxServerUtils.logger.log(Level.INFO, "nginx stop command output = " + result);
                final int exitValue = WebServerUtil.executeCommand("Taskkill.exe", "/IM", "dcnginx.exe", "/F");
                NginxServerUtils.logger.info("nginx task kill output = " + exitValue);
                result = ((exitValue == 0) ? "True" : "False");
            }
        }
        else {
            result = "NGINX STOPPED ALREADY";
        }
        return result;
    }
    
    public static Boolean nginxForceStop(final String serverHome) throws IOException {
        final String nginxPath = new File(serverHome + File.separator + "nginx" + File.separator + "dcnginx.exe").getCanonicalPath();
        if (WebServerUtil.isTaskRunning("dcnginx.exe", nginxPath, serverHome)) {
            return WebServerUtil.forceStop("dcnginx.exe", nginxPath, serverHome);
        }
        NginxServerUtils.logger.log(Level.INFO, "nginxForceStop() Nginx Web Server Already Stopped!");
        return Boolean.TRUE;
    }
    
    public static String reloadNginxServer() throws Exception {
        final String serverHome = System.getProperty("server.home");
        final String[] nginxReloadCommand = { serverHome + File.separator + "nginx" + File.separator + "dcnginx.exe", "-s", "reload" };
        NginxServerUtils.logger.log(Level.INFO, "Nginx reload command " + Arrays.toString(nginxReloadCommand));
        String result = "";
        if (isNginxServerRunning()) {
            final ProcessBuilder processBuilder = new ProcessBuilder(nginxReloadCommand);
            processBuilder.environment().put("OPENSSL_CONF", getNginxConfDirPath());
            processBuilder.directory(new File(serverHome + File.separator + "nginx"));
            final Process process = processBuilder.start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                result = result + buffer + "\n";
            }
        }
        else {
            result = "NGINX STOPPED ALREADY";
        }
        return result;
    }
    
    public static boolean isNginxServerRunning() throws Exception {
        final String nginxPidFilePath = System.getProperty("server.home") + NginxServerUtils.FS + "nginx" + NginxServerUtils.FS + "logs" + NginxServerUtils.FS + "nginx.pid";
        final File nginxPidFile = new File(nginxPidFilePath);
        if (!nginxPidFile.exists()) {
            return false;
        }
        String pidAsStringFromFile = StartupUtil.readFileAsString(nginxPidFile, "UTF-8");
        if (null == pidAsStringFromFile || pidAsStringFromFile == "" || pidAsStringFromFile.trim().length() == 0) {
            return false;
        }
        pidAsStringFromFile = pidAsStringFromFile.replace("\r\n", "");
        return isProcessRunning(pidAsStringFromFile, "dcnginx.exe");
    }
    
    private static boolean isProcessRunning(final String processId, final String processName) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { "tasklist.exe", "/fi", "\"PID eq " + processId + "\"" });
        processBuilder.environment().put("OPENSSL_CONF", getNginxConfDirPath());
        final Process process = processBuilder.start();
        final String tasksList = toString(process.getInputStream());
        final boolean isNginxProcessIdRunning = tasksList.contains(processId) && tasksList.contains(processName);
        NginxServerUtils.logger.log(Level.INFO, "Does Tasklist contains processId " + processId + " ? " + isNginxProcessIdRunning);
        return isNginxProcessIdRunning;
    }
    
    private static String toString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        final String string = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return string;
    }
    
    public static String getNginxConfDirPath() {
        if (NginxServerUtils.nginxConfDirPath == null) {
            try {
                NginxServerUtils.nginxConfDirPath = WebServerUtil.getServerHomeCanonicalPath() + NginxServerUtils.FS + NginxServerUtils.NGINX_CONF_DIR;
            }
            catch (final Exception e) {
                NginxServerUtils.logger.log(Level.INFO, "error getting canonical path", e);
            }
        }
        return NginxServerUtils.nginxConfDirPath;
    }
    
    public HashMap getNginxCertificateFileNames() throws Exception {
        Properties webSettingsProp = new Properties();
        final HashMap nginxCertificateFileName = new HashMap();
        try {
            webSettingsProp = WebServerUtil.getWebServerSettings();
            final String oldServerCrtFileFromProperty = String.valueOf(webSettingsProp.getProperty("server.crt.loc", ""));
            final String oldServerKeyFileFromProperty = String.valueOf(webSettingsProp.getProperty("server.key.loc", ""));
            nginxCertificateFileName.put("server.crt.loc", oldServerCrtFileFromProperty);
            nginxCertificateFileName.put("server.key.loc", oldServerKeyFileFromProperty);
        }
        catch (final Exception ex) {
            NginxServerUtils.logger.log(Level.WARNING, "Exception in getNginxOldCertificateFiles... ", ex);
            throw ex;
        }
        return nginxCertificateFileName;
    }
    
    public HashMap getNginxOldCertificateFile(final String nginxFolder) throws Exception {
        final HashMap nginxOldCertificateFiles = new HashMap();
        HashMap nginxCertificateFileName = new HashMap();
        try {
            nginxCertificateFileName = this.getNginxCertificateFileNames();
            String oldServerCrtFileName = null;
            String oldServerKeyFileName = null;
            String oldIntermediateFileName = null;
            String oldRootFileName = null;
            final String serverHome = System.getProperty("server.home");
            final String confDirectory = serverHome + NginxServerUtils.FS + nginxFolder + NginxServerUtils.FS + "conf";
            if (nginxCertificateFileName != null && !nginxCertificateFileName.isEmpty()) {
                final String nginxServerCrt = String.valueOf(nginxCertificateFileName.get("server.crt.loc"));
                if (nginxServerCrt != null && !nginxServerCrt.isEmpty()) {
                    oldServerCrtFileName = confDirectory + NginxServerUtils.FS + nginxCertificateFileName.get("server.crt.loc");
                    nginxOldCertificateFiles.put("server.crt.loc", oldServerCrtFileName);
                }
                final String nginxServerKey = String.valueOf(nginxCertificateFileName.get("server.key.loc"));
                if (nginxServerKey != null && !nginxServerKey.isEmpty()) {
                    oldServerKeyFileName = confDirectory + NginxServerUtils.FS + nginxCertificateFileName.get("server.key.loc");
                    nginxOldCertificateFiles.put("server.key.loc", oldServerKeyFileName);
                }
                final String nginxIntermediateCrt = String.valueOf(nginxCertificateFileName.get("apache.ssl.intermediate.ca.file"));
                if (nginxIntermediateCrt != null && !nginxIntermediateCrt.isEmpty()) {
                    oldIntermediateFileName = confDirectory + NginxServerUtils.FS + nginxCertificateFileName.get("apache.ssl.intermediate.ca.file");
                    nginxOldCertificateFiles.put("apache.ssl.intermediate.ca.file", oldIntermediateFileName);
                }
                final String nginxRootCrt = String.valueOf(nginxCertificateFileName.get("apache.ssl.root.ca.file"));
                if (nginxRootCrt != null && !nginxRootCrt.isEmpty()) {
                    oldRootFileName = confDirectory + NginxServerUtils.FS + nginxCertificateFileName.get("apache.ssl.root.ca.file");
                    nginxOldCertificateFiles.put("apache.ssl.root.ca.file", oldRootFileName);
                }
            }
        }
        catch (final Exception ex) {
            NginxServerUtils.logger.log(Level.WARNING, "Exception in getNginxOldCertificateFiles... ", ex);
            throw ex;
        }
        return nginxOldCertificateFiles;
    }
    
    public static void generateNginxProxyConfFile(final Properties wsProps, final String wss_port, final String ws_port) throws Exception {
        String serverHome = WebServerUtil.getServerHomeCanonicalPath();
        serverHome = serverHome.replace("\\", "/");
        final String nginxConfDir = serverHome + NginxServerUtils.FS + "nginx" + NginxServerUtils.FS + "conf";
        final String nginxProxyConfTemplateFilePath = nginxConfDir + NginxServerUtils.FS + "nginx-proxy.conf.template";
        final String nginxProxyConfFilePath = nginxConfDir + NginxServerUtils.FS + "nginx-proxy.conf";
        final String nginxSSLProxyConfFilePath = nginxConfDir + NginxServerUtils.FS + "nginx-ssl-proxy.conf";
        final File nginxProxyFile = new File(nginxProxyConfFilePath);
        final File nginxSSLProxyFile = new File(nginxSSLProxyConfFilePath);
        if (!nginxProxyFile.exists()) {
            nginxProxyFile.createNewFile();
        }
        if (!nginxSSLProxyFile.exists()) {
            nginxSSLProxyFile.createNewFile();
        }
        if (wss_port != null && ws_port != null) {
            if (!WebServerUtil.isPortStarted(ws_port) || !WebServerUtil.isPortStarted(wss_port)) {
                ((Hashtable<String, String>)wsProps).put("proxypass.loc", "ajp_keep_conn on;\najp_pass tomcat;");
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxProxyConfTemplateFilePath, nginxProxyConfFilePath, wsProps, "%", Boolean.FALSE);
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxProxyConfTemplateFilePath, nginxSSLProxyConfFilePath, wsProps, "%", Boolean.FALSE);
            }
            else {
                ((Hashtable<String, String>)wsProps).put("proxypass.loc", "proxy_pass http://" + wsProps.getProperty("server.ip") + ":" + ws_port + ";");
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxProxyConfTemplateFilePath, nginxProxyConfFilePath, wsProps, "%", Boolean.FALSE);
                wsProps.remove("proxypass.loc");
                ((Hashtable<String, String>)wsProps).put("proxypass.loc", "proxy_pass https://" + wsProps.getProperty("server.ip") + ":" + wss_port + ";");
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxProxyConfTemplateFilePath, nginxSSLProxyConfFilePath, wsProps, "%", Boolean.FALSE);
            }
        }
        else {
            ((Hashtable<String, String>)wsProps).put("proxypass.loc", "ajp_keep_conn on;\najp_pass tomcat;");
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxProxyConfTemplateFilePath, nginxProxyConfFilePath, wsProps, "%", Boolean.FALSE);
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxProxyConfTemplateFilePath, nginxSSLProxyConfFilePath, wsProps, "%", Boolean.FALSE);
        }
    }
    
    public static void generateNginxConfFiles(final Properties wsProps) {
        try {
            String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            serverHome = serverHome.replace("\\", "/");
            NginxServerUtils.logger.log(Level.INFO, "Server Home from generatenginxConfFiles " + serverHome);
            final String nginxConfFileName = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_CONF_FILE;
            final String nginxConfTemplateFileName = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_CONF_TEMPLATE_FILE;
            final String nginxSSLConfFileName = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINXSSL_CONF_FILE;
            final String nginxSSLTemplateFileName = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINXSSL_CONF_TEMPLATE_FILE;
            final String nginxProxyConfFileName = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_PROXY_CONF_FILE;
            final String nginxProxyTemplateFileName = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_PROXY_CONF_TEMPLATE_FILE;
            if ("All".equals(wsProps.getProperty("mdm.protected.dir.deny"))) {
                wsProps.setProperty("mdm.protected.dir.deny", "all");
            }
            if (wsProps.containsKey("dclogs.dir.deny")) {
                final String directoryDenyValue = wsProps.getProperty("dclogs.dir.deny");
                if ("All".equalsIgnoreCase(directoryDenyValue)) {
                    wsProps.setProperty("dclogs.dir.deny", "deny all;");
                }
                else if (directoryDenyValue.equalsIgnoreCase("none")) {
                    wsProps.setProperty("dclogs.dir.deny", "");
                }
                else if (SYMClientUtil.isIPAddress(directoryDenyValue)) {
                    wsProps.setProperty("dclogs.dir.deny", "deny " + directoryDenyValue + ";");
                }
                else {
                    wsProps.setProperty("dclogs.dir.deny", "");
                }
            }
            else {
                wsProps.setProperty("dclogs.dir.deny", "");
            }
            if (wsProps.containsKey("server.ip") && !wsProps.getProperty("server.ip").isEmpty() && !wsProps.getProperty("server.ip").equalsIgnoreCase("localhost")) {
                wsProps.setProperty("server.ip.ajp", wsProps.getProperty("server.ip"));
            }
            else {
                wsProps.setProperty("server.ip.ajp", "127.0.0.1");
            }
            if ("127.0.0.1 ::1 localhost".equals(wsProps.getProperty("dclogs.dir.allow"))) {
                wsProps.setProperty("dclogs.dir.allow", "127.0.0.1 ::1 localhost".replace(" ::1 localhost", ""));
            }
            if ("All".equalsIgnoreCase(wsProps.getProperty("dclogs.dir.allow"))) {
                wsProps.setProperty("dclogs.dir.allow", "all");
            }
            WebServerUtil.includeFwsLicenseCheck(wsProps);
            WebServerUtil.includeGzipCompressionFiles(wsProps);
            WebServerUtil.includeOSDHTTPReposForNGINX(wsProps, serverHome);
            final String sslProtocolsFromWebSettings = wsProps.getProperty("apache.sslprotocol");
            final String wssSupportedProtocols = WebServerUtil.getWebSocketSupportedProtocols(sslProtocolsFromWebSettings, "SSLv2 SSLv3 TLSv1 TLSv1.1 TLSv1.2");
            final String nginxSupportedProtocols = wssSupportedProtocols.replaceAll(",", " ");
            wsProps.setProperty("server.home", serverHome);
            wsProps.setProperty("apache.sslprotocol", nginxSupportedProtocols);
            wsProps.setProperty("SERVER_HOST_NAME", InetAddress.getLocalHost().getHostName());
            NginxServerUtils.logger.log(Level.INFO, "Server Home from wsProps " + serverHome);
            NginxServerUtils.logger.log(Level.INFO, "Going to update nginx conf files with the following web setting properties " + wsProps);
            if (new File(nginxConfTemplateFileName).exists() && new File(nginxSSLTemplateFileName).exists()) {
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxConfTemplateFileName, nginxConfFileName, wsProps, "%");
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxSSLTemplateFileName, nginxSSLConfFileName, wsProps, "%");
            }
            if (new File(nginxProxyTemplateFileName).exists()) {
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxProxyTemplateFileName, nginxProxyConfFileName, wsProps, "%");
            }
        }
        catch (final Exception ex) {
            NginxServerUtils.logger.log(Level.WARNING, "Caught exception in generateNginxConfFiles()", ex);
            ex.printStackTrace();
        }
    }
    
    public static String getNginxVersion() {
        final String nginxDir = System.getProperty("server.home") + NginxServerUtils.FS + "nginx";
        final String nginxExe = nginxDir + NginxServerUtils.FS + "dcnginx.exe";
        String nginxVersion = "";
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { nginxExe, "-v" });
        processBuilder.environment().put("OPENSSL_CONF", getNginxConfDirPath());
        processBuilder.redirectErrorStream(true);
        try {
            final Process process = processBuilder.start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                if (buffer.contains("nginx version")) {
                    nginxVersion = buffer;
                    break;
                }
            }
        }
        catch (final Exception ex) {
            NginxServerUtils.logger.log(Level.INFO, "Exception while getting nginx version " + ex);
        }
        return nginxVersion;
    }
    
    public static void generateNginxConfFilesWithDefaultLoc(final Properties portProps, Properties wsProps) {
        try {
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            final String serverHomeForApache = serverHome.replaceAll("\\\\", "/");
            final String nginxConfFileName = serverHome + File.separator + NginxServerUtils.NGINX_CONF_FILE;
            final String nginxConfTemplateFileName = serverHome + File.separator + NginxServerUtils.NGINX_CONF_TEMPLATE_FILE;
            final String nginxSSLConfFileName = serverHome + File.separator + NginxServerUtils.NGINXSSL_CONF_FILE;
            final String nginxSSLTEmplateFileName = serverHome + File.separator + NginxServerUtils.NGINXSSL_CONF_TEMPLATE_FILE;
            final String nginxProxyConfFileName = serverHome + File.separator + NginxServerUtils.NGINX_PROXY_CONF_FILE;
            final String nginxProxyTemplateFileName = serverHome + File.separator + NginxServerUtils.NGINX_PROXY_CONF_TEMPLATE_FILE;
            if (wsProps == null) {
                wsProps = WebServerUtil.getWebServerSettings();
            }
            wsProps.setProperty("apache.xframe.options.key", WebServerUtil.getXframeHeader(wsProps));
            wsProps.setProperty("SERVER_HOME", serverHomeForApache);
            if ("All".equals(wsProps.getProperty("mdm.protected.dir.deny"))) {
                wsProps.setProperty("mdm.protected.dir.deny", "all");
            }
            if (wsProps.containsKey("dclogs.dir.deny")) {
                final String directoryDenyValue = WebServerUtil.getWebServerSettings().getProperty("dclogs.dir.deny");
                if ("All".equalsIgnoreCase(directoryDenyValue)) {
                    wsProps.setProperty("dclogs.dir.deny", "deny all;");
                }
                else if (directoryDenyValue.equalsIgnoreCase("none")) {
                    wsProps.setProperty("dclogs.dir.deny", "");
                }
                else if (SYMClientUtil.isIPAddress(directoryDenyValue)) {
                    wsProps.setProperty("dclogs.dir.deny", "deny " + directoryDenyValue + ";");
                }
                else {
                    wsProps.setProperty("dclogs.dir.deny", "");
                }
            }
            else {
                wsProps.setProperty("dclogs.dir.deny", "");
            }
            if (wsProps.containsKey("server.ip") && !wsProps.getProperty("server.ip").isEmpty() && !wsProps.getProperty("server.ip").equalsIgnoreCase("localhost")) {
                wsProps.setProperty("server.ip.ajp", wsProps.getProperty("server.ip"));
            }
            else {
                wsProps.setProperty("server.ip.ajp", "127.0.0.1");
            }
            if ("127.0.0.1 ::1 localhost".equals(wsProps.getProperty("dclogs.dir.allow"))) {
                wsProps.setProperty("dclogs.dir.allow", "127.0.0.1 ::1 localhost".replace(" ::1 localhost", ""));
            }
            if ("All".equalsIgnoreCase("dclogs.dir.allow")) {
                wsProps.setProperty("dclogs.dir.allow", "all");
            }
            WebServerUtil.includeGzipCompressionFiles(wsProps);
            WebServerUtil.includeFwsLicenseCheck(wsProps);
            WebServerUtil.includeOSDHTTPReposForNGINX(wsProps, serverHome);
            final String sslProtocolsFromWebSettings = WebServerUtil.getSSLProtocol(wsProps);
            final String wssSupportedProtocols = WebServerUtil.getWebSocketSupportedProtocols(sslProtocolsFromWebSettings, "SSLv2 SSLv3 TLSv1 TLSv1.1 TLSv1.2");
            final String nginxSupportedProtocols = wssSupportedProtocols.replaceAll(",", " ");
            wsProps.setProperty("server.home", serverHome);
            wsProps.setProperty("apache.sslprotocol", nginxSupportedProtocols);
            wsProps.setProperty("SERVER_HOST_NAME", InetAddress.getLocalHost().getHostName());
            final String httpPortValue = portProps.getProperty("http.port");
            final String httpsPortValue = portProps.getProperty("https.port");
            NginxServerUtils.logger.log(Level.INFO, "Port properties are : " + portProps);
            final Boolean isMSP = InstallUtil.isMSP();
            String freePortHttp = null;
            if (httpPortValue != null) {
                int httpPort = Integer.parseInt(httpPortValue);
                if (isMSP) {
                    httpPort += 20;
                }
                freePortHttp = String.valueOf(WebServerUtil.getFreePort(httpPort));
                wsProps.setProperty("http.port", freePortHttp);
                NginxServerUtils.logger.log(Level.INFO, "HTTP PORT : " + freePortHttp);
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxConfTemplateFileName, nginxConfFileName, wsProps, "%");
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxProxyTemplateFileName, nginxProxyConfFileName, wsProps, "%");
            }
            if (httpsPortValue != null) {
                String freePort = null;
                if (isMSP) {
                    final int freePortHttps = Integer.parseInt(freePortHttp);
                    freePort = String.valueOf(WebServerUtil.getFreePort(freePortHttps));
                }
                else {
                    final int httpsPort = Integer.parseInt(httpsPortValue);
                    freePort = String.valueOf(WebServerUtil.getFreePort(httpsPort));
                }
                wsProps.setProperty("https.port", freePort);
                NginxServerUtils.logger.log(Level.INFO, "HTTPS PORT " + freePort);
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxSSLTEmplateFileName, nginxSSLConfFileName, wsProps, "%");
            }
        }
        catch (final Exception ex) {
            NginxServerUtils.logger.log(Level.WARNING, "Caught exception in generating Apache Httpd and http ssl Conf Files with Default Location ", ex);
            ex.printStackTrace();
        }
    }
    
    public static Properties getNginxConfFilesPropertiesWithDefaultLoc(final Properties shareAccessStatus) {
        try {
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            final String serverHomeForNginx = serverHome.replaceAll("\\\\", "/");
            final String storeTempLocForNginx = (serverHome + NginxServerUtils.PATCH_STORE_DEFAULT_LOCATION).replaceAll("\\\\", "/");
            final String swTempLocForNginx = (serverHome + NginxServerUtils.SWREPOSITROY_DEFAULT_LOCATION).replaceAll("\\\\", "/");
            final String httpdFileName = serverHome + File.separator + NginxServerUtils.NGINX_CONF_FILE;
            final String httpdTemplateFileName = serverHome + File.separator + NginxServerUtils.NGINXSSL_CONF_TEMPLATE_FILE;
            final Properties wsProps = WebServerUtil.getWebServerSettings();
            wsProps.setProperty(serverHome, serverHomeForNginx);
            WebServerUtil.includeFwsLicenseCheck(wsProps);
            WebServerUtil.includeGzipCompressionFiles(wsProps);
            final Boolean storeStatus = Boolean.valueOf(shareAccessStatus.getProperty("store_access_status"));
            final Boolean swStatus = Boolean.valueOf(shareAccessStatus.getProperty("swrepository_access_status"));
            if (!storeStatus) {
                wsProps.setProperty("store.loc", storeTempLocForNginx);
            }
            if (!swStatus) {
                wsProps.setProperty("swrepository.loc", swTempLocForNginx);
            }
            return wsProps;
        }
        catch (final Exception ex) {
            NginxServerUtils.logger.log(Level.WARNING, "Caught exception in generateApacheHttpdConfFileswithDefaultLoc()", ex);
            ex.printStackTrace();
            return null;
        }
    }
    
    public static void startNginxInDifferentPort(final String serverHome) {
        startNginxInDifferentPort(serverHome, null);
    }
    
    public static void startNginxInDifferentPort(final String serverHome, final Properties wsProps) {
        try {
            NginxServerUtils.logger.log(Level.INFO, "Starting Nginx in Different Port");
            final Properties webServerProps = WebServerUtil.getWebServerSettings();
            final String httpPort = webServerProps.getProperty("http.port");
            final String httpsPort = webServerProps.getProperty("https.port");
            final Properties portProps = new Properties();
            portProps.setProperty("http.port", httpPort);
            portProps.setProperty("https.port", httpsPort);
            generateNginxConfFilesWithDefaultLoc(portProps, wsProps);
            NginxServerUtils.logger.log(Level.INFO, "Web Server Props from argument " + wsProps);
            NginxServerUtils.logger.log(Level.INFO, "Port Props " + portProps);
            final String stopResultOfNginx = stopNginxServer(serverHome);
            NginxServerUtils.logger.log(Level.INFO, "startStandAloneNginx :: Stop Nginx Server: " + stopResultOfNginx);
            final String startResultOfNginxWithDeaultPort = startNginxServer(serverHome);
            NginxServerUtils.logger.log(Level.INFO, "startStandAloneNginx :: Start Nginx Service: " + startResultOfNginxWithDeaultPort);
            NginxServerUtils.logger.log(Level.INFO, " Going to delete ws.modtime file ");
            final String wsModifiedTime = serverHome + File.separator + "conf" + File.separator + "ws.modtime";
            final File wsModifiedTimeFile = new File(wsModifiedTime);
            if (wsModifiedTimeFile.exists()) {
                NginxServerUtils.logger.log(Level.INFO, "ws.modtime file deleted status : " + wsModifiedTimeFile.delete());
            }
        }
        catch (final Exception e) {
            NginxServerUtils.logger.log(Level.WARNING, "Exception while starting nginx in different port", e);
        }
    }
    
    public static void trackNginxStartupError(String error) {
        final String serverHome = System.getProperty("server.home");
        final File nginxErrorTrackFile = new File(serverHome + File.separator + "logs" + File.separator + "NginxErrorFile.json");
        if (!nginxErrorTrackFile.exists()) {
            try {
                nginxErrorTrackFile.createNewFile();
            }
            catch (final IOException ex) {
                NginxServerUtils.logger.log(Level.INFO, "Exception in creating nginx tracker file " + ex);
            }
        }
        try {
            NginxServerUtils.logger.log(Level.INFO, "Reading file contents from Nginx error tracking file");
            final String fileContents = StartupUtil.readFileAsString(nginxErrorTrackFile, "UTF-8");
            NginxServerUtils.logger.log(Level.INFO, "File contents from the error file " + fileContents);
            JSONObject jsonData = new JSONObject();
            if (null != fileContents && !"".equals(fileContents)) {
                final JSONParser jsonParser = new JSONParser();
                jsonData = (JSONObject)jsonParser.parse(fileContents);
            }
            final String[] ngnixTestCommand = { serverHome + File.separator + "nginx" + File.separator + "dcnginx.exe", "-t" };
            NginxServerUtils.logger.log(Level.INFO, "Nginx test command " + Arrays.toString(ngnixTestCommand));
            String result = "";
            final ProcessBuilder processBuilder = new ProcessBuilder(ngnixTestCommand);
            processBuilder.environment().put("OPENSSL_CONF", getNginxConfDirPath());
            processBuilder.directory(new File(serverHome + File.separator + "nginx"));
            final Process process = processBuilder.start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                result = result + buffer + "\n";
            }
            error = processResultForPIIData(error);
            result = processResultForPIIData(result);
            jsonData.put((Object)"Nginx_Error", (Object)error);
            jsonData.put((Object)"Nginx_Test_Result", (Object)result);
            jsonData.put((Object)"OS_Version", (Object)System.getProperty("os.name"));
            jsonData.put((Object)"OS_Architecture", (Object)System.getProperty("os.arch"));
            NginxServerUtils.logger.log(Level.INFO, "JSON Data to be written in the file " + jsonData);
            StartupUtil.writeStringToFile(nginxErrorTrackFile, jsonData.toString(), "UTF-8");
        }
        catch (final Exception ex2) {
            NginxServerUtils.logger.log(Level.INFO, "Exception in reading or modyfing nginx tracker file " + ex2);
        }
    }
    
    private static String processResultForPIIData(String result) throws Exception {
        result = result.replace("\\", "").replace("/", "");
        return result.replace(WebServerUtil.getServerHomeCanonicalPath().replace("\\", "").replace("/", ""), "");
    }
    
    public static void addHTTPRepositoriesDynamically(final Hashtable httpRepositories) throws Exception {
        try {
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            addHTTPRepositoriesToConf(serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_CONF_FILE, httpRepositories, false);
            addHTTPRepositoriesToConf(serverHome + NginxServerUtils.FS + NginxServerUtils.NGINXSSL_CONF_FILE, httpRepositories, true);
        }
        catch (final Exception e) {
            NginxServerUtils.logger.log(Level.INFO, "Exception in adding http repositories " + e.getStackTrace());
        }
    }
    
    public static void removeHTTPRepositoriesDynamically(final String[] httpRepositories) throws Exception {
        try {
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            removeHTTPRepositoriesFromConf(serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_CONF_FILE, httpRepositories, false);
            removeHTTPRepositoriesFromConf(serverHome + NginxServerUtils.FS + NginxServerUtils.NGINXSSL_CONF_FILE, httpRepositories, true);
        }
        catch (final Exception e) {
            NginxServerUtils.logger.log(Level.INFO, "Exception in removing http repositories " + e.getStackTrace());
        }
    }
    
    private static void addHTTPRepositoriesToConf(final String confFilePath, final Hashtable httpRepositories, final boolean isSSL) throws Exception {
        FileOutputStream fileOutputStream = null;
        try {
            final NgxConfig conf = NgxConfig.read(confFilePath);
            String[] blockParams;
            if (isSSL) {
                blockParams = new String[] { "server" };
            }
            else {
                blockParams = new String[] { "http", "server" };
            }
            final NgxBlock rtmpServers = (NgxBlock)conf.find((Class)NgxBlock.class, blockParams);
            for (final Object repoObject : httpRepositories.keySet()) {
                final String repoName = repoObject.toString();
                String repoPath = httpRepositories.get(repoName).toString();
                repoPath = repoPath.replaceAll("\\\\", "/");
                repoPath = ((repoPath.lastIndexOf("/") == repoPath.length() - 1) ? repoPath : (repoPath + "/"));
                repoPath = "\"" + repoPath + "\"";
                final NgxBlock ngxBlock = new NgxBlock(new String[] { "location \"/" + repoName + "/\"" });
                ngxBlock.addEntry((NgxEntry)new NgxParam(new String[] { "alias", repoPath }));
                ngxBlock.addEntry((NgxEntry)new NgxParam(new String[] { "autoindex", "off" }));
                ngxBlock.addEntry((NgxEntry)new NgxParam(new String[] { "allow", "all" }));
                rtmpServers.addEntry((NgxEntry)ngxBlock);
            }
            final NgxDumper nginxDumper = new NgxDumper(conf);
            fileOutputStream = new FileOutputStream(confFilePath);
            nginxDumper.dump((OutputStream)fileOutputStream);
        }
        catch (final Exception e) {
            NginxServerUtils.logger.log(Level.INFO, "Exception while adding entries to nginx conf file " + e.getStackTrace());
        }
        finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }
    
    private static void removeHTTPRepositoriesFromConf(final String confFilePath, final String[] httpRepositories, final boolean isSSL) throws Exception {
        FileOutputStream fileOutputStream = null;
        try {
            final NgxConfig conf = NgxConfig.read(confFilePath);
            String[] blockParams;
            if (isSSL) {
                blockParams = new String[] { "server", "location" };
            }
            else {
                blockParams = new String[] { "http", "server", "location" };
            }
            final List<NgxEntry> location = conf.findAll((Class)NgxBlock.class, blockParams);
            for (final String repoName : httpRepositories) {
                for (final NgxEntry repo : location) {
                    if (repo.toString().equalsIgnoreCase("location \"/" + repoName + "/\" {")) {
                        conf.remove(repo);
                    }
                }
            }
            final NgxDumper nginxDumper = new NgxDumper(conf);
            fileOutputStream = new FileOutputStream(confFilePath);
            nginxDumper.dump((OutputStream)fileOutputStream);
        }
        catch (final Exception e) {
            NginxServerUtils.logger.log(Level.INFO, "Exception while removing entries from nginx conf file " + e.getStackTrace());
        }
        finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }
    
    public static void generateNginxStandaloneConf(Properties wsProps) {
        try {
            final String httpPortValue = wsProps.getProperty("http.port");
            final String httpsPortValue = wsProps.getProperty("https.port");
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            final String serverHomeForWebServer = serverHome.replaceAll("\\\\", "/");
            final String nginxConfFileName = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_CONF_FILE;
            final String originalConf = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_CONF_DIR + "nginx_temp.conf";
            final String tempConf = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_CONF_DIR + "nginx_standalone.conf.template";
            final File nginxConf = new File(nginxConfFileName);
            if (nginxConf.exists()) {
                nginxConf.renameTo(new File(originalConf));
            }
            wsProps = WebServerUtil.getWebServerSettings();
            if (httpPortValue != null) {
                final int httpPort = Integer.parseInt(httpPortValue);
                final String freePortHttp = String.valueOf(WebServerUtil.getFreePort(httpPort));
                wsProps.setProperty("http.port", freePortHttp);
                NginxServerUtils.usedPort = Integer.parseInt(freePortHttp);
                NginxServerUtils.logger.log(Level.INFO, "HTTP PORT : " + freePortHttp);
            }
            if (httpsPortValue != null) {
                final int httpsPort = Integer.parseInt(httpsPortValue);
                final String freePortHttps = String.valueOf(WebServerUtil.getFreePort(httpsPort));
                wsProps.setProperty("https.port", freePortHttps);
                NginxServerUtils.logger.log(Level.INFO, "HTTPS PORT " + freePortHttps);
            }
            wsProps.setProperty("server.home", serverHomeForWebServer);
            wsProps.setProperty("SERVER_HOST_NAME", InetAddress.getLocalHost().getHostName());
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(tempConf, nginxConfFileName, wsProps, "%");
        }
        catch (final Exception e) {
            NginxServerUtils.logger.log(Level.WARNING, "Exception while generating server trouble shooter nginx conf !", e);
        }
    }
    
    public static void resetNginxConf() {
        NginxServerUtils.logger.log(Level.WARNING, "Entering into Reset Nginx Conf");
        final String serverHome = System.getProperty("server.home");
        final String nginxConfFileName = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_CONF_FILE;
        final String tempConf = serverHome + NginxServerUtils.FS + NginxServerUtils.NGINX_CONF_DIR + "nginx_temp.conf";
        final File nginxTempConf = new File(tempConf);
        final File nginxconf = new File(nginxConfFileName);
        try {
            if (nginxTempConf.exists()) {
                if (nginxconf.exists()) {
                    nginxconf.delete();
                }
                nginxTempConf.renameTo(nginxconf);
            }
        }
        catch (final Exception e) {
            NginxServerUtils.logger.log(Level.WARNING, "Exception While Resetting the Server troubleShooter Conf File ", e);
        }
        finally {
            if (!nginxconf.exists()) {
                deleteWsModifiedTimeFile();
            }
        }
    }
    
    public static void deleteWsModifiedTimeFile() {
        final String serverHome = System.getProperty("server.home");
        NginxServerUtils.logger.log(Level.INFO, " Going to delete ws.modtime file ");
        final String wsModifiedTime = serverHome + File.separator + "conf" + File.separator + "ws.modtime";
        final File wsModifiedTimeFile = new File(wsModifiedTime);
        if (wsModifiedTimeFile.exists()) {
            NginxServerUtils.logger.log(Level.INFO, "ws.modtime file deleted status : " + wsModifiedTimeFile.delete());
        }
    }
    
    public static void setServerLimitingSettings(final Properties wsProps) {
        final String serverHome = System.getProperty("server.home");
        final String serverLimiting = serverHome + File.separator + NginxServerUtils.SERVER_LIMITING_SETTINGS;
        final String default_serverLimiting = serverHome + File.separator + NginxServerUtils.DEFAULT_SERVER_LIMITING_SETTINGS;
        Properties serverLimiting_props = new Properties();
        try {
            serverLimiting_props = StartupUtil.getProperties(serverLimiting);
            if (serverLimiting_props.size() > 0 && serverLimiting_props.getProperty("enable.server.limiting") != null) {
                final String serverLimitEnabled = serverLimiting_props.getProperty("enable.server.limiting").trim();
                if (serverLimitEnabled.equals("#") || serverLimitEnabled.equalsIgnoreCase("true")) {
                    wsProps.putAll(serverLimiting_props);
                    NginxServerUtils.logger.log(Level.INFO, "Server Limiting Settings set into web server properties list!");
                    return;
                }
            }
            NginxServerUtils.logger.log(Level.WARNING, "File is empty or limiting disabled! So loading the default settings!");
            NginxServerUtils.logger.log(Level.WARNING, "***** Going to load the default server limiting settings *****");
            serverLimiting_props = StartupUtil.getProperties(default_serverLimiting);
            wsProps.putAll(serverLimiting_props);
        }
        catch (final Exception e) {
            NginxServerUtils.logger.log(Level.WARNING, "Exception while setting the server limiting for Bandwidth!!!");
        }
    }
    
    @Deprecated
    public static void updateNginxConfForServerLimitingSettings() {
        try {
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            final String nginxConfFileName = serverHome + File.separator + NginxServerUtils.NGINX_CONF_FILE;
            final File file = new File(serverHome + File.separator + "conf" + File.separator + "serverlimitingsettings.conf");
            if (!file.exists()) {
                NginxServerUtils.logger.log(Level.INFO, "File not found here");
                throw new FileNotFoundException();
            }
            if (file.length() == 0L) {
                NginxServerUtils.logger.log(Level.INFO, "File is empty! So loading from back up file");
                throw new Exception();
            }
            final Properties serverLimitingProperties = getServerLimitingSettings(serverHome, "serverlimitingsettings.conf");
            final String isServerLimitingEnabled = serverLimitingProperties.getProperty("enable.server.limiting");
            if (isServerLimitingEnabled.equalsIgnoreCase("true")) {
                final Properties wsProps = WebServerUtil.getWebServerSettings();
                generateNginxConfFiles(wsProps);
                NginxServerUtils.logger.log(Level.INFO, "wsProps before updating from serverLimitingSettings: " + wsProps.toString());
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxConfFileName, nginxConfFileName, serverLimitingProperties, "%");
            }
            else {
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxConfFileName, nginxConfFileName, serverLimitingProperties, "%");
            }
        }
        catch (final Exception e) {
            NginxServerUtils.logger.log(Level.INFO, "Exception while updating nginx conf file");
            NginxServerUtils.logger.log(Level.INFO, "Loading from backup settings file!");
            reloadNginxConfDueToFileCorruption();
        }
    }
    
    @Deprecated
    public static void reloadNginxConfDueToFileCorruption() {
        try {
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            final String nginxConfFileName = serverHome + File.separator + NginxServerUtils.NGINX_CONF_FILE;
            final Properties serverLimitingProperties = getServerLimitingSettings(serverHome, "serverlimitingsettings_default.conf");
            final Properties wsProps = WebServerUtil.getWebServerSettings();
            generateNginxConfFiles(wsProps);
            NginxServerUtils.logger.log(Level.INFO, "wsProps before updating from serverLimitingSettings: " + wsProps.toString());
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(nginxConfFileName, nginxConfFileName, serverLimitingProperties, "%");
        }
        catch (final Exception e) {
            NginxServerUtils.logger.log(Level.INFO, "Exception while updating nginx conf file from backup serverlimitingsettings file");
        }
    }
    
    public static Properties getServerLimitingSettings(final String serverHome, final String fileName) throws Exception {
        final String serverLimitingSettingsFile = serverHome + File.separator + "conf" + File.separator + fileName;
        FileInputStream fis = null;
        Properties props;
        try {
            fis = new FileInputStream(serverLimitingSettingsFile);
            props = new Properties();
            props.load(fis);
        }
        catch (final Exception ex) {
            throw new Exception(ex);
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        return props;
    }
    
    public static Long getServerLimitSettingsLastModifiedTime() {
        long lastModTime = -1L;
        String serverLimitFileName = null;
        try {
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            serverLimitFileName = serverHome + File.separator + NginxServerUtils.SERVER_LIMITING_SETTINGS;
            final File serverLimitSettingsFile = new File(serverLimitFileName);
            lastModTime = serverLimitSettingsFile.lastModified();
        }
        catch (final Exception ex) {
            NginxServerUtils.logger.log(Level.WARNING, "Caught error while retrieviing last modified time of file: " + serverLimitFileName, ex);
        }
        return lastModTime;
    }
    
    static {
        FS = File.separator;
        NGINX_CONF_DIR = "nginx" + NginxServerUtils.FS + "conf" + NginxServerUtils.FS;
        NGINX_CONF_FILE = NginxServerUtils.NGINX_CONF_DIR + "nginx.conf";
        NGINXSSL_CONF_FILE = NginxServerUtils.NGINX_CONF_DIR + "nginx-ssl.conf";
        NGINX_PROXY_CONF_FILE = NginxServerUtils.NGINX_CONF_DIR + "nginx-proxy.conf";
        NGINX_CONF_TEMPLATE_FILE = NginxServerUtils.NGINX_CONF_DIR + "nginx.conf.template";
        NGINXSSL_CONF_TEMPLATE_FILE = NginxServerUtils.NGINX_CONF_DIR + "nginx-ssl.conf.template";
        NGINX_PROXY_CONF_TEMPLATE_FILE = NginxServerUtils.NGINX_CONF_DIR + "nginx-proxy.conf.template";
        SERVER_LIMITING_SETTINGS = "conf" + File.separator + "serverlimitingsettings.conf";
        DEFAULT_SERVER_LIMITING_SETTINGS = "conf" + File.separator + "serverlimitingsettings_default.conf";
    }
}
