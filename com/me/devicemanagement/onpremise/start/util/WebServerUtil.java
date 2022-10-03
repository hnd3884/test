package com.me.devicemanagement.onpremise.start.util;

import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import com.me.devicemanagement.framework.start.util.AgentAuthenticationConstants;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Arrays;
import com.me.devicemanagement.framework.start.util.DateTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.w3c.dom.NodeList;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import com.me.devicemanagement.framework.utils.XMLUtils;
import org.w3c.dom.Document;
import com.me.devicemanagement.onpremise.start.DCStarter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Enumeration;
import java.net.SocketException;
import java.util.Collections;
import java.net.NetworkInterface;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import java.util.Map;
import java.util.Stack;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;
import java.net.InetAddress;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import com.me.devicemanagement.onpremise.start.servertroubleshooter.util.ServerTroubleshooterUtil;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class WebServerUtil
{
    public static final String WEB_SETTINGS_CONF_FILE;
    public static final String WS_SETTINGS_CONF_FILE;
    public static final String HTPASSWD_FILE;
    public static final String HTPASSWD_MODTIME_PROPS_FILE;
    public static final String HTPASSWD_MODTIME = "lastModifiedTime";
    public static final String WRAPPER_CONF_FILE;
    public static final String WEBSERVER_NAME = "webserver.name";
    public static final String APACHE = "apache";
    public static final String TOMCAT = "tomcat";
    public static final String NGINX = "nginx";
    public static final String HTTP_PORT = "http.port";
    public static final String HTTP_NIO_PORT = "httpnio.port";
    public static final String HTTPS_PORT = "https.port";
    public static final String STORE_LOC = "store.loc";
    public static final String SWREPOSITORY_LOC = "swrepository.loc";
    public static final String AJP_PORT = "ajp.port";
    public static final String AJP_PORT_RANGE = "ajp.port.range";
    public static final String AGENT_TOMCAT_PORT = "tomcat.agent.port";
    public static final String AGENT_TOMCAT_PORT_RANGE = "tomcat.agent.port.range";
    public static final String UI_RESTRICTION = "restrict.ui.https";
    public static final String AGENT_GZIP_COMPRESSION = "agent.gzip.compression";
    public static final String SERVER_GZIP_COMPRESSION = "server.gzip.compression";
    public static final String FWS_LICENSE_CHECK = "fws.license.check";
    public static final String APACHE_EXE = "dcserverhttpd.exe";
    public static final String INSTALL = "install";
    public static final String UNINSTALL = "uninstall";
    public static final String START = "start";
    public static final String STOP = "stop";
    private static final String APACHE_SERVICE_NAME = "\"ManageEngine UEMS - Apache\"";
    private static final String HTTPD_EXE;
    private static final String VCREDIST_EXE;
    private static final String HTTPD_CONF_FILE;
    private static final String HTTPD_CONF_TEMPLATE_FILE;
    public static final String HTTPD_CONF_OSD_ALIAS_FILE;
    public static final String HTTPD_CONF_OSD_DIRECTORIES_FILE;
    public static final String NGINX_CONF_OSD_LOCATIONS_FILE;
    public static final String OSD_NOT_ACCESSIBLE_PATH_FILE = "notaccessibleosdpaths.temp";
    public static final String OSD_ALIAS_REPLACE_STRING = "osd replication directories aliases";
    public static final String OSD_DIRECTORIES_REPLACE_STRING = "osd replication directories";
    public static final String OSD_LOCATIONS_REPLACE_STRING = "osd replication locations";
    public static final String OSD_LOCATIONS_REPLACE_HTTP_STRING = "osd replication http locations";
    private static final String HTTPD_SSL_CONF_FILE;
    private static final String HTTPD_SSL_CONF_TEMPLATE_FILE;
    private static final String WORKERS_PROPERTIES_FILE;
    private static final String WORKERS_PROPERTIES_TEMPLATE_FILE;
    private static final String APACHE_ACCESS_LOG_FILENAME_PREFIX = "apache_accesslog";
    private static final String APACHE_ERROR_LOG_FILENAME_PREFIX = "apache_errorlog";
    private static final String APACHE_LOG_FILES_COUNT = "apache.logfile.count";
    private static final String APACHE_PING_INTERVAL = "apache.ping.interval";
    public static int apacheLogFilesCount;
    public static int apachePingInterval;
    private static final String SERVER_CONF_FILE;
    private static final String SERVER_CONF_TEMPLATE_FILE;
    private static final String SERVER_CONF_APACHE_TEMPLATE_FILE;
    private static final String SERVER_HOME = "SERVER_HOME";
    public static final String SERVER_HOST_NAME = "SERVER_HOST_NAME";
    public static final String CLIENT_ROOT_CA_PATH = "client.rootca.certificate.file";
    public static final String CLIENT_ROOT_CA_PATH_APACHE = "client.rootca.certificate.file.apache";
    public static final String KEY_PREFIX_SUFFIX = "%";
    public static final String COMMENT = "#";
    public static final String EMPTY = "";
    public static final String INCLUDE_AGENT_GZIP_CONF_FILE = "enable.apache.agent.gzip.compression";
    public static final String INCLUDE_SERVER_GZIP_CONF_FILE = "enable.apache.server.gzip.compression";
    public static final String INCLUDE_MODULE_DEFLATE_FOR_GZIP = "include.module.deflate.for.gzip.compression";
    public static final String INCLUDE_MODULE_FILTER_FOR_GZIP = "include.module.filter.for.gzip.compression";
    public static final String INCLUDE_SERVER_GZIP_NGINX = "nginx.server.gzip.compression";
    public static final String INCLUDE_AGENT_GZIP_NGINX = "nginx.agent.gzip.compression";
    public static final String INCLUDE_FWS_CONF_FILE = "enable.fws.license";
    public static final String THIRD_PARTY_SSL_ROOT_FILE_KEY = "apache.ssl.root.ca.file";
    public static final String THIRD_PARTY_SSL_ROOT_KEY = "apache.ssl.root.ca.key.file";
    public static final String INCLUDE_THIRD_PARTY_SSL_ROOT_FILE_KEY = "enable.apache.ssl.root.ca.file";
    public static final String THIRD_PARTY_SSL_INTERMEDIATE_FILE_KEY = "apache.ssl.intermediate.ca.file";
    public static final String INCLUDE_THIRD_PARTY_SSL_INTERMEDIATE_FILE_KEY = "enable.apache.ssl.intermediate.ca.file";
    public static final String SHARE_ACCESS_STATUS_FILE;
    public static final String SWREPOSITROY_DEFAULT_LOCATION;
    public static final String PATCH_STORE_DEFAULT_LOCATION;
    public static final String PATCH_STORE_ACCESS_STATUS = "store_access_status";
    public static final String SWREPOSITORY_ACCESS_STATUS = "swrepository_access_status";
    public static final String OSD_REPOSITORY_ACCESS_STATUS = "osd_repository_access_status";
    public static final String OSD_REPOSITORY_PATH = "osd_repository_path";
    public static final String MAINTENANCE_MODE_TEMPLATE_FILE;
    public static final String MAINTENANCE_MODE_FILE;
    public static final String WEBSERVER_CONFIGURATIONS_FILE;
    private static String sourceClass;
    public static final String APACHE_THREADSPERCHILD_32BIT = "apache.threadsperchild.32bit";
    public static final String APACHE_THREADSPERCHILD_64BIT = "apache.threadsperchild.64bit";
    public static final String APACHE_THREADSPERCHILD = "apache.threadsperchild";
    public static final String SERVERCRTLOC = "server.crt.loc";
    public static final String SERVERKEYLOC = "server.key.loc";
    public static final String APACHECRTLOC = "apache.crt.loc";
    public static final String APACHESERVERKEYLOC = "apache.serverKey.loc";
    public static final String WS_PORT = "ws.port";
    public static final String WSS_PORT = "wss.port";
    public static final String APACHE_SSLPROTOCOL = "apache.sslprotocol";
    private static final String APACHE_CIPHERS = "apache.sslciphersuite";
    private static final String DEFAULT_PROTOCOLS = "all -SSLv2 -SSLv3";
    private static final String DISABLE_TLS_PROTOCOL = " -TLSv1 -TLSv1.1";
    public static final String SUPPORTED_PROTOCOLS = "SSLv2 SSLv3 TLSv1 TLSv1.1 TLSv1.2";
    private static final String IS_TLSV2_ENABLED = "IsTLSV2Enabled";
    private static final String TOMCAT_CONNECTOR_SSL_PROTOCOLS = "sslEnabledProtocols";
    private static final String TOMCAT_CONNECTOR_SSL_CIPHERS = "ciphers";
    private static final String TOMCAT_SERVICE_NODE = "Service";
    private static final String TOMCAT_CATALINA_SERVICE = "Catalina";
    private static final String TOMCAT_SERVICE_NAME_PROPERTY = "name";
    private static final String TOMCAT_IS_WS_GATEWAY_ENABLED = "IsWSGatewayEnabled";
    private static final String DISABLE_INSECURE_COMMUNICATION = "enforce.https.communication";
    private static final String INSECURE_COMM_UI = "insecure.comm.ui";
    private static final String INSECURE_COMM_STATIC = "insecure.comm.static";
    private static final String UNBIND_HTTP_PORT = "unbind.http.port";
    private static final String DISABLE_HTTP_BASIC_AUTH = "disable.http.basic.auth";
    public static final String ENABLE_DOMAIN_FOLDER_AUTH = "enable.domain.folder.auth";
    public static final String DISABLE_DOMAIN_FOLDER_AUTH = "domain.folder.auth";
    private static final String ENABLE_CLIENT_CERT_AUTH = "client.cert.auth.enabled";
    private static final String CLIENT_CERT_AUTH_NGINX = "disable.client.cert.auth.nginx";
    private static final String CLIENT_CERT_AUTH_APACHE = "disable.client.cert.auth.apache";
    private static final String SSL_VERIFY_CLIENT = "ssl.verify.client";
    private static final String CLIENT_CERT_AUTH_LEVEL = "client.cert.auth.level";
    private static final String FORCE_DISABLE_CLIENT_CERT_AUTH = "force.disable.client.cert.auth";
    private static final String DISABLE_CLIENT_CERT_AUTH_IN_HTTP = "disable.client.cert.auth.in.http";
    public static final String APACHEXFRAMEHEADER = "apache.xframe.options";
    public static final String APACHEXFRAMEHEADERKEY = "apache.xframe.options.key";
    public static final String APACHEXFRAMEHEADERVALUE = "Header always append X-Frame-Options SAMEORIGIN";
    public static final String DCWINUTIL_PATH;
    private static boolean isWSGatewayEnabled;
    public static final String SERVER_IP = "server.ip";
    public static final String HTTPD_IP = "httpd.ip";
    public static final String LOCAL_HOST = "localhost";
    public static String server_ip_props;
    private static final String PROTOCOL_ATTR = "protocol";
    public static final String IP_NOT_REACHABLE = "IP_NOT_REACHABLE";
    public static final String BIND_IP_IS_NOT_SPECIFIED = "BIND_IP_IS_NOT_SPECIFIED";
    public static Logger logger;
    public static String webServerName;
    public static int usedPort;
    private static String productArch;
    private static String postgresArch;
    private static String apacheArch;
    private static String machineName;
    private static String serverProtocol;
    private static Properties webServerConfigurations;
    private static final String ENABLE = "enable";
    private static final String VALUE = "value";
    private static final String APPEND_DISABLE = "disable_";
    private static final String APPEND_VALUE = "_value";
    private static final String KILO = "k";
    private static final String NGINX_CLIENT_BODY_TIMEOUT = "nginx_client_body_timeout";
    private static final String NGINX_CLIENT_HEADER_TIMEOUT = "nginx_client_header_timeout";
    private static final String NGINX_SEND_TIMEOUT = "nginx_send_timeout";
    private static final String APACHE_TIMEOUT = "apache_timeout";
    private static final String NGINX_KEEPALIVE_TIMEOUT = "nginx_keepalive_timeout";
    private static final String APACHE_REQUESTREADTIMEOUT_BODY = "apache_RequestReadTimeout_body";
    private static final String APACHE_LIMITREQUEST_LINE = "apache_LimitRequestline";
    private static final String APACHE_LIMITREQUESTFIELDSIZE = "apache_LimitRequestFieldsize";
    private static final String NGINX_LARGE_CLIENT_HEADER_BUFFERS = "nginx_large_client_header_buffers";
    private static final String NGINX_CLIENT_BODY_BUFFER_SIZE = "nginx_client_body_buffer_size";
    private static final Integer NGINX_CLIENT_BODY_TIMEOUT_MIN_VALUE;
    private static final Integer NGINX_CLIENT_BODY_TIMEOUT_MAX_VALUE;
    private static final Integer NGINX_KEEPALIVE_TIMEOUT_MIN_VALUE;
    private static final Integer NGINX_KEEPALIVE_TIMEOUT_MAX_VALUE;
    private static final Integer APACHE_REQUESTREADTIMEOUT_BODY_MIN_VALUE;
    private static final Integer APACHE_LIMITREQUEST_LINE_MIN_VALUE;
    private static final Integer APACHE_LIMITREQUEST_LINE_MAX_VALUE;
    private static final Integer APACHE_LIMITREQUESTFIELDSIZE_MIN_VALUE;
    private static final Integer APACHE_LIMITREQUESTFIELDSIZE_MAX_VALUE;
    private static final Integer NGINX_LARGE_CLIENT_HEADER_BUFFERS_MIN_VALUE;
    private static final Integer NGINX_LARGE_CLIENT_HEADER_BUFFERS_MAX_VALUE;
    
    public static void main(final String[] args) {
        try {
            if (args.length < 1) {
                showSyntaxAndExit(true);
            }
            DCLogUtil.initLogger();
            final String webServerName = getWebServerName();
            final String serviceName = getApacheServiceName();
            final String action = args[0];
            final long timenow = System.currentTimeMillis();
            final String timenowStr = getDateString(timenow, "MMM-dd-yyyy-HH-mm");
            WebServerUtil.logger.log(Level.INFO, "\n\n\n-------------------------------------------------------");
            WebServerUtil.logger.log(Level.INFO, "WebServerUtil.main() invoked with action: " + action);
            WebServerUtil.logger.log(Level.INFO, "Current system time: " + timenowStr);
            WebServerUtil.logger.log(Level.INFO, "-------------------------------------------------------\n");
            performWebServerAction(webServerName, serviceName, action);
            WebServerUtil.logger.log(Level.INFO, "Requested operation is completed.");
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.INFO, "Requested operation is failed.");
            WebServerUtil.logger.log(Level.INFO, "Caught some error: ", ex);
            showSyntaxAndExit(true);
        }
    }
    
    private static void showSyntaxAndExit(final boolean exit) {
        WebServerUtil.logger.log(Level.INFO, "USAGE: com.adventnet.sym.start.util.WebServerUtil [ACTION]");
        WebServerUtil.logger.log(Level.INFO, "ACTION can be any one from install, reinstall, uninstall, start, stop");
        WebServerUtil.logger.log(Level.INFO, "server.home system property must be set to jvm");
        if (exit) {
            System.exit(1);
        }
    }
    
    private static void getApacheArch() {
        WebServerUtil.apacheArch = getApacheVersion();
        if (WebServerUtil.apacheArch != null && WebServerUtil.apacheArch.contains("Win32")) {
            WebServerUtil.apacheArch = "32-bit";
        }
        else if (WebServerUtil.apacheArch != null && WebServerUtil.apacheArch.contains("Win64")) {
            WebServerUtil.apacheArch = "64-bit";
        }
    }
    
    public static void copyFile(final String src, final String dst) throws IOException {
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
    
    private static void getPostgresArch() throws Exception {
        try {
            final String serverHome = System.getProperty("server.home");
            final String postgresArchFileName = serverHome + File.separator + "pgsql" + File.separator + "data" + File.separator + "PG_ARCH";
            final File postgresArchFile = new File(postgresArchFileName);
            if (postgresArchFile.exists()) {
                final BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(postgresArchFileName)));
                String arch = null;
                while ((arch = fileReader.readLine()) != null) {
                    if (arch != null && arch.trim().equals("32")) {
                        WebServerUtil.postgresArch = "32-bit";
                        break;
                    }
                    if (arch != null && arch.trim().equals("64")) {
                        WebServerUtil.postgresArch = "64-bit";
                        break;
                    }
                }
                WebServerUtil.logger.log(Level.FINE, "POSTGRES ARCH", WebServerUtil.postgresArch);
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while postgres architecture compatibility.", e);
            throw e;
        }
    }
    
    public static void ProductArchitectureIncompatibility(final String dbName) throws Exception {
        try {
            WebServerUtil.logger.log(Level.WARNING, "########################################################################");
            WebServerUtil.logger.log(Level.WARNING, "###################     N E E D   A T T E N T I O N    #################");
            WebServerUtil.logger.log(Level.WARNING, "Incompatible architecture found.....");
            WebServerUtil.logger.log(Level.WARNING, "Product Architecture :" + WebServerUtil.productArch);
            WebServerUtil.logger.log(Level.WARNING, "Apache Architecture :" + WebServerUtil.apacheArch);
            if (dbName.equals("postgres") || dbName.equals("pgsql")) {
                WebServerUtil.logger.log(Level.WARNING, "DB Architecture :" + WebServerUtil.postgresArch);
            }
            WebServerUtil.logger.log(Level.WARNING, "########################################################################");
            ServerTroubleshooterUtil.getInstance().serverStartupFailure("arch_incompatible");
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while checking product compatibility.", e);
            throw e;
        }
    }
    
    public static boolean isIncompatibleArchitectureFound(final String dbName) {
        try {
            WebServerUtil.productArch = StartupUtil.dcProductArch();
            if (WebServerUtil.productArch != null) {
                getApacheArch();
                if (WebServerUtil.apacheArch != null && !WebServerUtil.apacheArch.isEmpty()) {
                    if (WebServerUtil.productArch.equalsIgnoreCase(WebServerUtil.apacheArch)) {
                        if (dbName != null && (dbName.equalsIgnoreCase("postgres") || dbName.equalsIgnoreCase("pgsql"))) {
                            getPostgresArch();
                            if (WebServerUtil.postgresArch != null && !WebServerUtil.productArch.equalsIgnoreCase(WebServerUtil.postgresArch)) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while checking architecture compatibility.", ex);
        }
        return false;
    }
    
    public static void performWebServerAction(String webServerName, String serviceName, final String action) throws Exception {
        try {
            WebServerUtil.logger.log(Level.INFO, "WebServerUtil.performWebServerAction() invoked with webServerName: " + webServerName + " serviceName: " + serviceName + " action: " + action);
            if (!serviceName.startsWith("\"")) {
                serviceName = "\"" + serviceName + "\"";
            }
            String serviceNameTrimmed = serviceName;
            if (serviceName.equalsIgnoreCase(getApacheServiceName())) {
                serviceNameTrimmed = trimServiceNameApacheCompatible(serviceName);
                WebServerUtil.logger.log(Level.INFO, "Apache service name after removing spaces: " + serviceNameTrimmed);
            }
            final String serverHome = getServerHomeCanonicalPath();
            if (serverHome == null || serverHome.trim().length() == 0 || serviceName == null || serviceName.trim().length() == 0 || action == null || action.trim().length() == 0) {
                showSyntaxAndExit(false);
                return;
            }
            if (webServerName == null || webServerName.trim().length() == 0) {
                webServerName = "apache";
            }
            if (action.equalsIgnoreCase("install")) {
                generateWebServerConfFiles(webServerName);
                final String result = apacheHttpdInvoke(serverHome, "install", serviceName);
                WebServerUtil.logger.log(Level.INFO, "Result of install: " + result);
                final String result2 = StartupUtil.changeServiceStartupType(serviceNameTrimmed, "demand");
                WebServerUtil.logger.log(Level.INFO, "Result of changing apache service startup to manual: " + result2);
            }
            else if (action.equalsIgnoreCase("reinstall")) {
                boolean alreadyInstalled = true;
                final String result3 = apacheHttpdInvoke(serverHome, "stop", serviceName);
                WebServerUtil.logger.log(Level.INFO, "Result of stop: " + result3);
                if (result3.indexOf("No installed service") != -1) {
                    alreadyInstalled = false;
                }
                if (alreadyInstalled) {
                    Thread.currentThread();
                    Thread.sleep(5000L);
                    final String result4 = apacheHttpdInvoke(serverHome, "uninstall", serviceName);
                    WebServerUtil.logger.log(Level.INFO, "Result of uninstall: " + result4);
                    Thread.currentThread();
                    Thread.sleep(5000L);
                }
                generateWebServerConfFiles(webServerName);
                final String result5 = apacheHttpdInvoke(serverHome, "install", serviceName);
                WebServerUtil.logger.log(Level.INFO, "Result of install: " + result5);
                final String result6 = StartupUtil.changeServiceStartupType(serviceNameTrimmed, "demand");
                WebServerUtil.logger.log(Level.INFO, "Result of changing apache service startup to manual: " + result6);
                final String result7 = checkApacheService(serverHome);
                WebServerUtil.logger.log(Level.INFO, "Result of apache service check: " + result7);
            }
            else if (action.equalsIgnoreCase("uninstall")) {
                final String result = apacheHttpdInvoke(serverHome, "uninstall", serviceName);
                WebServerUtil.logger.log(Level.INFO, "Result: " + result);
            }
            else if (action.equalsIgnoreCase("start")) {
                final String result = apacheHttpdInvoke(serverHome, "start", serviceName);
                WebServerUtil.logger.log(Level.INFO, "Result: " + result);
            }
            else {
                if (!action.equalsIgnoreCase("stop")) {
                    WebServerUtil.logger.log(Level.INFO, "Wrong action, may not be supported: " + action);
                    showSyntaxAndExit(false);
                    return;
                }
                final String result = apacheHttpdInvoke(serverHome, "stop", serviceName);
                WebServerUtil.logger.log(Level.INFO, "Result: " + result);
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "Caught some error inside performWebServerAction(): " + ex);
            ex.printStackTrace();
            throw ex;
        }
    }
    
    public static int refreshWebServerSettings() {
        int status = 0;
        try {
            final Properties wsProps = getWebServerSettings();
            WebServerUtil.logger.log(Level.INFO, "WebServer settings configured is: " + wsProps);
            final String webServerName = wsProps.getProperty("webserver.name");
            generateWebServerConfFiles(webServerName);
            final String logfileCountStr = wsProps.getProperty("apache.logfile.count");
            if (logfileCountStr != null && logfileCountStr.trim().length() > 0) {
                try {
                    WebServerUtil.apacheLogFilesCount = Integer.parseInt(logfileCountStr);
                }
                catch (final Exception ex) {
                    WebServerUtil.logger.log(Level.WARNING, "Caught error while parsing apache log file count property...", ex);
                    ex.printStackTrace();
                    status = 8;
                }
            }
            final String pingIntervalStr = wsProps.getProperty("apache.ping.interval");
            if (pingIntervalStr != null && pingIntervalStr.trim().length() > 0) {
                try {
                    WebServerUtil.apachePingInterval = Integer.parseInt(pingIntervalStr);
                }
                catch (final Exception ex2) {
                    WebServerUtil.logger.log(Level.WARNING, "Caught error while parsing apache log file count property...", ex2);
                    ex2.printStackTrace();
                    status = 8;
                }
            }
        }
        catch (final Exception ex3) {
            WebServerUtil.logger.log(Level.WARNING, "Caught error while refreshing websettings.conf...", ex3);
            ex3.printStackTrace();
            status = 7;
        }
        return status;
    }
    
    public static Long getWebServerSettingsLastModifiedTime() {
        long lastModTime = -1L;
        String webSettingsFileName = null;
        try {
            final String serverHome = getServerHomeCanonicalPath();
            webSettingsFileName = serverHome + File.separator + WebServerUtil.WEB_SETTINGS_CONF_FILE;
            final File webSettingsFile = new File(webSettingsFileName);
            lastModTime = webSettingsFile.lastModified();
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught error while retrieviing last modified time of file: " + webSettingsFileName, ex);
        }
        return lastModTime;
    }
    
    public static Long getHtpasswdLastModifiedTime() {
        long lastModTime = -1L;
        String htpasswdFileName = null;
        try {
            final String serverHome = getServerHomeCanonicalPath();
            htpasswdFileName = serverHome + File.separator + WebServerUtil.HTPASSWD_FILE;
            final File htpasswdFile = new File(htpasswdFileName);
            lastModTime = htpasswdFile.lastModified();
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught error while retrieviing last modified time of file: " + htpasswdFileName, ex);
        }
        return lastModTime;
    }
    
    public static boolean isHtpasswdModified() {
        boolean changed = false;
        try {
            final Long prevModTime = getHtpasswdLastModifiedTimeProperty();
            WebServerUtil.logger.log(Level.INFO, "htpasswd Previous ModifiedTime: " + prevModTime);
            if (prevModTime == null) {
                WebServerUtil.logger.log(Level.INFO, "htpasswd Previous modified time is null. This might be the first server startup or first startup after restore...");
            }
            final long lastModTime = getHtpasswdLastModifiedTime();
            WebServerUtil.logger.log(Level.INFO, "Last modified time of " + WebServerUtil.HTPASSWD_FILE + " from file system: " + lastModTime);
            if (prevModTime == null || lastModTime != prevModTime) {
                changed = true;
                WebServerUtil.logger.log(Level.INFO, "htpasswd ModifiedTime: " + lastModTime);
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while checking the " + WebServerUtil.HTPASSWD_FILE + " is modified or not.", ex);
            changed = true;
        }
        return changed;
    }
    
    public static Long getHtpasswdLastModifiedTimeProperty() {
        Long lastModTime = null;
        try {
            final String fname = System.getProperty("server.home") + File.separator + WebServerUtil.HTPASSWD_MODTIME_PROPS_FILE;
            final Properties props = StartupUtil.getProperties(fname);
            if (props != null) {
                final String modTimeStr = props.getProperty("lastModifiedTime");
                if (modTimeStr != null) {
                    lastModTime = new Long(modTimeStr);
                }
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while retrieving .htpasswd prev modified time.", ex);
        }
        return lastModTime;
    }
    
    public static void saveHtpasswdLastModifiedTimeProperty() {
        try {
            final String fname = System.getProperty("server.home") + File.separator + WebServerUtil.HTPASSWD_MODTIME_PROPS_FILE;
            final Properties props = new Properties();
            props.setProperty("lastModifiedTime", String.valueOf(getHtpasswdLastModifiedTime()));
            StartupUtil.storeProperties(props, fname);
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while saving .htpasswd last modified time.", ex);
        }
    }
    
    public static void updateWebServerPorts(final int httpPort, final int httpsPort) throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final Properties portProps = new Properties();
            portProps.setProperty("http.port", String.valueOf(httpPort));
            portProps.setProperty("https.port", String.valueOf(httpsPort));
            storeProperWebServerSettings(portProps);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static void updateWebServerProps(final Properties wsProps) throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + WebServerUtil.WEB_SETTINGS_CONF_FILE;
            StartupUtil.storeProperties(wsProps, fname);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static void removeWebServerProps(final ArrayList<String> wsPropKeys) throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + WebServerUtil.WEB_SETTINGS_CONF_FILE;
            StartupUtil.removeProperties(wsPropKeys, fname);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static void changeWebServer(final String webServerName) throws Exception {
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final Properties portProps = new Properties();
            portProps.setProperty("webserver.name", webServerName);
            storeProperWebServerSettings(portProps);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    @Deprecated
    public static int getHttpPort() throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final Properties portProps = getWebServerSettings();
            final String httpPortStr = portProps.getProperty("http.port");
            return Integer.parseInt(httpPortStr);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static int getServerPort() throws Exception {
        return getHttpsPort();
    }
    
    public static int getWebServerPort() throws Exception {
        if (getServerProtocol().equalsIgnoreCase("https")) {
            return getHttpsPort();
        }
        return getHttpPort();
    }
    
    public static String getServerProtocol() throws Exception {
        if (WebServerUtil.serverProtocol == null) {
            final Properties wsProps = getWebServerSettings();
            final Properties infoProps = getServerInfoProps();
            if (Boolean.parseBoolean(wsProps.getProperty("enforce.https.communication")) || Boolean.parseBoolean(infoProps.getProperty("ENABLE_HTTPS"))) {
                WebServerUtil.serverProtocol = "https";
            }
            else {
                WebServerUtil.serverProtocol = "http";
            }
        }
        return WebServerUtil.serverProtocol;
    }
    
    public static void setServerProtocol(final String protocol) {
        WebServerUtil.serverProtocol = protocol;
    }
    
    public static Properties getServerInfoProps() throws IOException {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String serverInfoConf = serverHome + File.separator + "conf" + File.separator + "server_info.props";
            return StartupUtil.getProperties(serverInfoConf);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static int getHttpNioPort() throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final Properties portProps = getWebServerSettings();
            final String httpPortStr = portProps.getProperty("httpnio.port");
            return Integer.parseInt(httpPortStr);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static int getHttpsPort() throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final Properties portProps = getWebServerSettings();
            final String httpsPortStr = portProps.getProperty("https.port");
            return Integer.parseInt(httpsPortStr);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static String getWebServerName() throws Exception {
        String wsName = null;
        if (WebServerUtil.webServerName != null && !"".equals(WebServerUtil.webServerName)) {
            return WebServerUtil.webServerName;
        }
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final Properties wsProps = getWebServerSettings();
            wsName = wsProps.getProperty("webserver.name");
        }
        catch (final Exception ex) {
            throw ex;
        }
        return wsName;
    }
    
    public static void setWebserverName(final String webServer) {
        WebServerUtil.webServerName = webServer;
    }
    
    public static void setMachineName(final String name) {
        WebServerUtil.machineName = name;
    }
    
    public static String getMachineName() {
        try {
            if (WebServerUtil.machineName == null) {
                WebServerUtil.machineName = InetAddress.getLocalHost().getHostName();
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while getting machine host name.", ex);
            try {
                final Properties wsProps = getWebServerSettings();
                WebServerUtil.machineName = wsProps.getProperty("server.fqdn");
            }
            catch (final Exception e) {
                WebServerUtil.logger.log(Level.WARNING, "Caught exception while getting FQDN.", e);
            }
        }
        return (WebServerUtil.machineName != null && !WebServerUtil.machineName.isEmpty()) ? WebServerUtil.machineName : "localhost";
    }
    
    public static String getUIRestrictinHTTPS() throws Exception {
        String uirestiction = null;
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final Properties wsProps = getWebServerSettings();
            uirestiction = wsProps.getProperty("restrict.ui.https");
        }
        catch (final Exception ex) {
            throw ex;
        }
        return uirestiction;
    }
    
    public static Properties getWebServerSettings() throws Exception {
        try {
            return getWebServerSettings(Boolean.TRUE);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static void includeGzipCompressionFiles(final Properties wsProps) throws Exception {
        try {
            final String serverGzipFlag = wsProps.getProperty("server.gzip.compression");
            final Boolean agentGzipFlag = Boolean.valueOf(wsProps.getProperty("agent.gzip.compression"));
            System.setProperty("agent.gzip.compression", String.valueOf(agentGzipFlag));
            if (serverGzipFlag != null && !serverGzipFlag.equalsIgnoreCase("")) {
                final Boolean serverGzipFlagBoolean = Boolean.valueOf(wsProps.getProperty("server.gzip.compression"));
                if (serverGzipFlagBoolean) {
                    wsProps.setProperty("enable.apache.server.gzip.compression", "");
                    wsProps.setProperty("include.module.deflate.for.gzip.compression", "");
                    wsProps.setProperty("include.module.filter.for.gzip.compression", "");
                    wsProps.setProperty("nginx.server.gzip.compression", "");
                }
                else {
                    wsProps.setProperty("enable.apache.server.gzip.compression", "#");
                    wsProps.setProperty("include.module.deflate.for.gzip.compression", "#");
                    wsProps.setProperty("include.module.filter.for.gzip.compression", "#");
                    wsProps.setProperty("nginx.server.gzip.compression", "#");
                }
                if (agentGzipFlag) {
                    wsProps.setProperty("enable.apache.agent.gzip.compression", "");
                    wsProps.setProperty("nginx.agent.gzip.compression", "");
                }
                else {
                    wsProps.setProperty("enable.apache.agent.gzip.compression", "#");
                    wsProps.setProperty("nginx.agent.gzip.compression", "#");
                }
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static void includeFwsLicenseCheck(final Properties wsProps) {
        Boolean fwsCheckValue = Boolean.FALSE;
        final String fwsCheck = wsProps.getProperty("fws.license.check");
        if (fwsCheck != null && !fwsCheck.equalsIgnoreCase("")) {
            fwsCheckValue = Boolean.valueOf(fwsCheck);
        }
        if (fwsCheckValue) {
            wsProps.setProperty("enable.fws.license", "");
        }
        else {
            wsProps.setProperty("enable.fws.license", "#");
        }
    }
    
    private static void includeOSDHTTPReposForApache(final Properties wsProps, final String serverHome) {
        try {
            final File aliasFile = new File(serverHome + File.separator + WebServerUtil.HTTPD_CONF_OSD_ALIAS_FILE);
            if (aliasFile.exists()) {
                wsProps.setProperty("osd replication directories aliases", StartupUtil.readFileAsString(aliasFile, "UTF-8"));
            }
            else {
                wsProps.setProperty("osd replication directories aliases", "");
            }
            final File directoryFile = new File(serverHome + File.separator + WebServerUtil.HTTPD_CONF_OSD_DIRECTORIES_FILE);
            if (directoryFile.exists()) {
                wsProps.setProperty("osd replication directories", StartupUtil.readFileAsString(directoryFile, "UTF-8"));
            }
            else {
                wsProps.setProperty("osd replication directories", "");
            }
        }
        catch (final IOException e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while reading OSD HTTP repository files for Apache...", e.getStackTrace());
        }
    }
    
    public static void includeOSDHTTPReposForNGINX(final Properties wsProps, final String serverHome) {
        try {
            final File locationFile = new File(serverHome + File.separator + WebServerUtil.NGINX_CONF_OSD_LOCATIONS_FILE);
            if (locationFile.exists()) {
                wsProps.setProperty("osd replication locations", StartupUtil.readFileAsString(locationFile, "UTF-8"));
                wsProps.setProperty("osd replication http locations", StartupUtil.readFileAsString(locationFile, "UTF-8"));
                if (wsProps != null && wsProps.containsKey("enforce.https.communication") && Boolean.parseBoolean(wsProps.getProperty("enforce.https.communication")) && wsProps.containsKey("unbind.http.port")) {
                    wsProps.setProperty("osd replication http locations", "");
                }
            }
            else {
                wsProps.setProperty("osd replication locations", "");
                wsProps.setProperty("osd replication http locations", "");
            }
        }
        catch (final IOException e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while reading OSD HTTP repository files for NGINX...", e.getStackTrace());
        }
    }
    
    public static boolean isOSDHTTPConfFilesAvailable() {
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final File aliasFile = new File(serverHome + File.separator + WebServerUtil.HTTPD_CONF_OSD_ALIAS_FILE);
            final File directoryFile = new File(serverHome + File.separator + WebServerUtil.HTTPD_CONF_OSD_DIRECTORIES_FILE);
            if (aliasFile.exists()) {
                return true;
            }
            if (directoryFile.exists()) {
                return true;
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while checking OSD HTTP conf files availability...", e.getStackTrace());
        }
        return false;
    }
    
    private static Hashtable<String, String> getOSDHTTPRepos() {
        final Hashtable<String, String> repoList = new Hashtable<String, String>();
        try {
            final File aliasFile = new File(getServerHomeCanonicalPath() + File.separator + WebServerUtil.HTTPD_CONF_OSD_ALIAS_FILE);
            if (aliasFile.exists()) {
                final Iterator lines = StartupUtil.readAllLines(aliasFile, "UTF-8").iterator();
                while (lines.hasNext()) {
                    final String line = lines.next().toString();
                    final String repoName = line.substring(8, line.indexOf("\" \"//"));
                    final String repo = line.substring(line.indexOf("//"), line.lastIndexOf("\""));
                    if (!repo.isEmpty()) {
                        repoList.put(repoName, repo);
                    }
                }
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while getting OSD HTTP Repo List...", e);
        }
        return repoList;
    }
    
    public static void writeNotReachableOSDPathsToFile(final ArrayList<String> shareRepos) throws Exception {
        try {
            if (!shareRepos.isEmpty()) {
                WebServerUtil.logger.log(Level.INFO, "Writing not reachable OSD paths to file : " + shareRepos.toString());
                String shareRepoStr = "";
                for (final String shareRepo : shareRepos) {
                    shareRepoStr = shareRepoStr + shareRepo + ",";
                }
                StartupUtil.writeStringToFile(new File(getServerHomeCanonicalPath() + File.separator + "notaccessibleosdpaths.temp"), shareRepoStr, "UTF-8");
                removeNotReachablePathsFromOSDConfFiles(shareRepos);
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while generating not reachable OSD Share path file...", e.getStackTrace());
        }
    }
    
    public static String[] getNotReachableOSDPaths() throws Exception {
        String notReachablePathsStr = "";
        try {
            final File notReachablePathFile = new File(getServerHomeCanonicalPath() + File.separator + "notaccessibleosdpaths.temp");
            if (notReachablePathFile.exists()) {
                WebServerUtil.logger.log(Level.INFO, "Getting not reachable OSD paths file...");
                notReachablePathsStr = StartupUtil.readFileAsString(notReachablePathFile, "UTF-8");
                WebServerUtil.logger.log(Level.INFO, "Not reachable OSD paths : " + notReachablePathsStr);
                WebServerUtil.logger.log(Level.INFO, "Deleting not reachable OSD paths file...");
                notReachablePathFile.delete();
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while getting not reachable OSD Share paths...", e.getStackTrace());
        }
        return notReachablePathsStr.split(",");
    }
    
    private static void removeNotReachablePathsFromOSDConfFiles(final ArrayList<String> shareRepos) throws Exception {
        try {
            WebServerUtil.logger.log(Level.INFO, "Removing not reachable OSD paths from conf files...");
            for (final String shareRepo : shareRepos) {
                final File aliasFile = new File(getServerHomeCanonicalPath() + File.separator + WebServerUtil.HTTPD_CONF_OSD_ALIAS_FILE);
                if (aliasFile.exists()) {
                    final List<String> allAliasLines = StartupUtil.readAllLines(aliasFile, "UTF-8");
                    final Iterator<String> aliasLines = allAliasLines.iterator();
                    while (aliasLines.hasNext()) {
                        final String line = aliasLines.next().toString();
                        if (line.contains(shareRepo)) {
                            allAliasLines.remove(line);
                            final String repoPath = line.substring(line.indexOf("//"), line.lastIndexOf("\""));
                            removeEntryFromOSDHttpdConfFile(repoPath);
                            StartupUtil.writeLines(aliasFile, "UTF-8", allAliasLines);
                            removeEntryFromOSDNginxConfFile(shareRepo);
                            break;
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while removing not reachable OSD Share paths from conf files...", e.getStackTrace());
        }
    }
    
    private static void removeEntryFromOSDHttpdConfFile(final String repoPath) throws Exception {
        try {
            final File directoryFile = new File(getServerHomeCanonicalPath() + File.separator + WebServerUtil.HTTPD_CONF_OSD_DIRECTORIES_FILE);
            if (directoryFile.exists()) {
                final List<String> allDirectoryLines = StartupUtil.readAllLines(directoryFile, "UTF-8");
                if (!allDirectoryLines.isEmpty()) {
                    for (int i = 0; i < allDirectoryLines.size(); ++i) {
                        if (allDirectoryLines.get(i).contains(repoPath)) {
                            allDirectoryLines.remove(i);
                            while (!allDirectoryLines.get(i).contains("</Directory>")) {
                                allDirectoryLines.remove(i);
                            }
                            allDirectoryLines.remove(i);
                            StartupUtil.writeLines(directoryFile, "UTF-8", allDirectoryLines);
                            break;
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while removing not reachable OSD Share paths from httpd directories conf file...", e.getStackTrace());
        }
    }
    
    private static void removeEntryFromOSDNginxConfFile(final String shareRepo) throws Exception {
        try {
            final File locationFile = new File(getServerHomeCanonicalPath() + File.separator + WebServerUtil.NGINX_CONF_OSD_LOCATIONS_FILE);
            if (locationFile.exists()) {
                final List<String> allLocationLines = StartupUtil.readAllLines(locationFile, "UTF-8");
                if (!allLocationLines.isEmpty()) {
                    for (int i = 0; i < allLocationLines.size(); ++i) {
                        if (allLocationLines.get(i).contains(shareRepo)) {
                            Stack<Character> stack = null;
                            while (stack == null || !stack.isEmpty()) {
                                stack = ((stack == null) ? new Stack<Character>() : stack);
                                final String locationLine = allLocationLines.get(i);
                                final long openBracesCount = locationLine.chars().filter(ch -> ch == 123).count();
                                final long closeBracesCount = locationLine.chars().filter(ch -> ch == 125).count();
                                for (long j = 0L; j < openBracesCount; ++j) {
                                    stack.push('{');
                                }
                                for (long j = 0L; j < closeBracesCount; ++j) {
                                    stack.pop();
                                }
                                allLocationLines.remove(i);
                            }
                            StartupUtil.writeLines(locationFile, "UTF-8", allLocationLines);
                            break;
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while removing not reachable OSD Share paths from nginx locations conf file...", e.getStackTrace());
        }
    }
    
    public static void storeProperWebServerSettings(final Properties webServerProps) throws Exception {
        try {
            storeProperWebServerSettings(webServerProps, Boolean.TRUE);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static Properties getWebServerSettings(final Boolean convertWebSettingsPropsBasedOnArch) throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + WebServerUtil.WEB_SETTINGS_CONF_FILE;
            return getWebServerSettings(convertWebSettingsPropsBasedOnArch, fname);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static Properties getWSSettingsProps() throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String wsSettingsFilePath = serverHome + File.separator + WebServerUtil.WS_SETTINGS_CONF_FILE;
            return StartupUtil.getProperties(wsSettingsFilePath);
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "getWSSettingsProps - Exception while reading file", ex);
            throw ex;
        }
    }
    
    public static void storeWSSettingsProps(final Properties wsSettingsProps) throws Exception {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String wsSettingsFilePath = serverHome + File.separator + WebServerUtil.WS_SETTINGS_CONF_FILE;
            StartupUtil.storeProperties(wsSettingsProps, wsSettingsFilePath);
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "storeWSSettingsProps - Exception while writing to wsSettings.conf file", ex);
            throw ex;
        }
    }
    
    public static Properties getWebServerSettings(final Boolean convertWebSettingsPropsBasedOnArch, final String fname) throws Exception {
        try {
            Properties webServerProps = new Properties();
            webServerProps = StartupUtil.getProperties(fname);
            final Boolean isDCProduct64bit = StartupUtil.isDCProduct64bit();
            if (convertWebSettingsPropsBasedOnArch && webServerProps != null && !webServerProps.isEmpty()) {
                if (isDCProduct64bit != null && isDCProduct64bit && webServerProps.containsKey("apache.threadsperchild.64bit")) {
                    webServerProps.setProperty("apache.threadsperchild", webServerProps.getProperty("apache.threadsperchild.64bit"));
                    webServerProps.remove("apache.threadsperchild.64bit");
                    webServerProps.remove("apache.threadsperchild.32bit");
                }
                else if (isDCProduct64bit != null && !isDCProduct64bit && webServerProps.containsKey("apache.threadsperchild.32bit")) {
                    webServerProps.setProperty("apache.threadsperchild", webServerProps.getProperty("apache.threadsperchild.32bit"));
                    webServerProps.remove("apache.threadsperchild.64bit");
                    webServerProps.remove("apache.threadsperchild.32bit");
                }
                else if (isDCProduct64bit == null) {
                    throw new Exception("Unable to determine the DC Prodcut Architecture...");
                }
            }
            webServerProps = bindedIP(webServerProps);
            if (WebServerUtil.webServerConfigurations.isEmpty()) {
                initializeWebServerConfig();
            }
            webServerProps.putAll(WebServerUtil.webServerConfigurations);
            try {
                webServerProps.setProperty("help.url", GeneralPropertiesLoader.getInstance().getProperties().getProperty("help.url"));
            }
            catch (final Exception ex) {
                WebServerUtil.logger.log(Level.INFO, "Exception might be due to the GeneralProperties not ");
            }
            return webServerProps;
        }
        catch (final Exception ex2) {
            throw ex2;
        }
    }
    
    private static Properties initializeWebServerConfig() throws Exception {
        final String filePath = getServerHomeCanonicalPath() + File.separator + WebServerUtil.WEBSERVER_CONFIGURATIONS_FILE;
        if (new File(filePath).exists()) {
            final JSONObject jsonObject = (JSONObject)new JSONParser().parse(new String(getFileAsByteArray(filePath)));
            final Iterator iterator = jsonObject.keySet().iterator();
            while (iterator.hasNext()) {
                String valueStr = "";
                final String key = String.valueOf(iterator.next());
                if (key != null && !key.isEmpty()) {
                    final JSONObject value = (JSONObject)new JSONParser().parse(String.valueOf(jsonObject.get((Object)key)));
                    String disable = "";
                    if (!Boolean.parseBoolean(String.valueOf(value.get((Object)"enable")))) {
                        disable = "#";
                    }
                    ((Hashtable<String, String>)WebServerUtil.webServerConfigurations).put("disable_" + key, disable);
                    Integer valueInt = Integer.valueOf(String.valueOf(value.get((Object)"value")));
                    if (key.equalsIgnoreCase("nginx_client_body_timeout") || key.equalsIgnoreCase("nginx_client_header_timeout") || key.equalsIgnoreCase("nginx_send_timeout") || key.equalsIgnoreCase("apache_timeout")) {
                        if (valueInt < WebServerUtil.NGINX_CLIENT_BODY_TIMEOUT_MIN_VALUE) {
                            valueInt = WebServerUtil.NGINX_CLIENT_BODY_TIMEOUT_MIN_VALUE;
                        }
                        else if (valueInt > WebServerUtil.NGINX_CLIENT_BODY_TIMEOUT_MAX_VALUE) {
                            valueInt = WebServerUtil.NGINX_CLIENT_BODY_TIMEOUT_MAX_VALUE;
                        }
                    }
                    else if (key.equalsIgnoreCase("nginx_keepalive_timeout")) {
                        if (valueInt < WebServerUtil.NGINX_KEEPALIVE_TIMEOUT_MIN_VALUE) {
                            valueInt = WebServerUtil.NGINX_KEEPALIVE_TIMEOUT_MIN_VALUE;
                        }
                        if (valueInt > WebServerUtil.NGINX_KEEPALIVE_TIMEOUT_MAX_VALUE) {
                            valueInt = WebServerUtil.NGINX_KEEPALIVE_TIMEOUT_MAX_VALUE;
                        }
                    }
                    else if (key.equalsIgnoreCase("apache_RequestReadTimeout_body")) {
                        valueInt = WebServerUtil.APACHE_REQUESTREADTIMEOUT_BODY_MIN_VALUE;
                    }
                    else if (key.equalsIgnoreCase("apache_LimitRequestline")) {
                        if (valueInt < WebServerUtil.APACHE_LIMITREQUEST_LINE_MIN_VALUE) {
                            valueInt = WebServerUtil.APACHE_LIMITREQUEST_LINE_MIN_VALUE;
                        }
                        if (valueInt > WebServerUtil.APACHE_LIMITREQUEST_LINE_MAX_VALUE) {
                            valueInt = WebServerUtil.APACHE_LIMITREQUEST_LINE_MAX_VALUE;
                        }
                    }
                    else if (key.equalsIgnoreCase("apache_LimitRequestFieldsize")) {
                        if (valueInt < WebServerUtil.APACHE_LIMITREQUESTFIELDSIZE_MIN_VALUE) {
                            valueInt = WebServerUtil.APACHE_LIMITREQUESTFIELDSIZE_MIN_VALUE;
                        }
                        if (valueInt > WebServerUtil.APACHE_LIMITREQUESTFIELDSIZE_MAX_VALUE) {
                            valueInt = WebServerUtil.APACHE_LIMITREQUESTFIELDSIZE_MAX_VALUE;
                        }
                    }
                    else if (key.equalsIgnoreCase("nginx_large_client_header_buffers") || key.equalsIgnoreCase("nginx_client_body_buffer_size")) {
                        if (valueInt < WebServerUtil.NGINX_LARGE_CLIENT_HEADER_BUFFERS_MIN_VALUE) {
                            valueInt = WebServerUtil.NGINX_LARGE_CLIENT_HEADER_BUFFERS_MIN_VALUE;
                        }
                        if (valueInt > WebServerUtil.NGINX_LARGE_CLIENT_HEADER_BUFFERS_MAX_VALUE) {
                            valueInt = WebServerUtil.NGINX_LARGE_CLIENT_HEADER_BUFFERS_MAX_VALUE;
                        }
                        valueStr = valueInt + "k";
                    }
                    if (valueStr.isEmpty()) {
                        valueStr = String.valueOf(valueInt);
                    }
                    ((Hashtable<String, String>)WebServerUtil.webServerConfigurations).put(key + "_value", valueStr);
                }
            }
        }
        return WebServerUtil.webServerConfigurations;
    }
    
    private static Properties readProperties(final String confFileName) throws Exception {
        final Properties props = new Properties();
        InputStream ism = null;
        try {
            if (new File(confFileName).exists()) {
                ism = new FileInputStream(confFileName);
                props.load(ism);
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "Caught exception while reading properties from file: " + confFileName, ex);
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
    
    public static Properties bindedIP(final Properties wsProp) {
        Properties wraper_prop = new Properties();
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + WebServerUtil.WRAPPER_CONF_FILE;
            wraper_prop = StartupUtil.getProperties(fname);
            String bind_ip = null;
            WebServerUtil.logger.log(Level.INFO, "Properties of Wrapper-user.conf" + wraper_prop);
            for (final String key : wraper_prop.stringPropertyNames()) {
                final String tem_key_value = wraper_prop.getProperty(key);
                if (tem_key_value.contains("-Dbindaddress")) {
                    bind_ip = tem_key_value;
                }
            }
            WebServerUtil.logger.log(Level.INFO, "IP Read From Wrapper-user-conf" + bind_ip);
            if (bind_ip != null) {
                if (!bind_ip.trim().equals("")) {
                    bind_ip = bind_ip.split("\"")[1];
                }
                if (bind_ip != null && !bind_ip.trim().equals("")) {
                    if (isIPActive(bind_ip)) {
                        wsProp.setProperty("server.ip", bind_ip);
                        WebServerUtil.logger.log(Level.INFO, "Binded IP : " + wsProp.getProperty("server.ip"));
                        WebServerUtil.server_ip_props = bind_ip;
                        wsProp.setProperty("httpd.ip", bind_ip + ":");
                    }
                    else {
                        WebServerUtil.server_ip_props = "IP_NOT_REACHABLE";
                        WebServerUtil.logger.log(Level.SEVERE, "#####Given Ip Is not An Active IP#####");
                        wsProp.setProperty("server.ip", "localhost");
                        wsProp.setProperty("httpd.ip", "");
                        System.clearProperty("bindaddress");
                    }
                }
                else {
                    WebServerUtil.server_ip_props = "BIND_IP_IS_NOT_SPECIFIED";
                    WebServerUtil.logger.log(Level.WARNING, "####BIND IP IS NOT SPECIFIED####");
                    wsProp.setProperty("server.ip", "localhost");
                    wsProp.setProperty("httpd.ip", "");
                    System.clearProperty("bindaddress");
                }
            }
            else {
                WebServerUtil.server_ip_props = "BIND_IP_IS_NOT_SPECIFIED";
                WebServerUtil.logger.log(Level.WARNING, "####BIND IP IS NOT SPECIFIED####");
                wsProp.setProperty("server.ip", "localhost");
                wsProp.setProperty("httpd.ip", "");
            }
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            wsProp.setProperty("server.ip", "localhost");
            wsProp.setProperty("httpd.ip", "");
            System.clearProperty("bindaddress");
            WebServerUtil.logger.log(Level.INFO, "Error While loading BindAddress");
        }
        catch (final IOException e2) {
            WebServerUtil.logger.log(Level.INFO, "Error While loading server path");
            e2.printStackTrace();
        }
        catch (final NullPointerException e3) {
            WebServerUtil.logger.log(Level.INFO, "Error While loading wrapper-user.conf file");
            e3.printStackTrace();
        }
        catch (final Exception e4) {
            e4.printStackTrace();
        }
        return wsProp;
    }
    
    public static boolean isIPActive(final String ip) throws SocketException {
        try {
            final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (final NetworkInterface netint : Collections.list(nets)) {
                String tem_ip = getSystemIP(netint);
                if (!tem_ip.equals("empty")) {
                    tem_ip = tem_ip.split("/")[1];
                    if (tem_ip.equals(ip)) {
                        return true;
                    }
                    continue;
                }
            }
        }
        catch (final SocketException sock_exc) {
            WebServerUtil.logger.log(Level.INFO, "Error occured while creating or accessing a Socket...");
            sock_exc.printStackTrace();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static String getSystemIP(final NetworkInterface netint) throws SocketException {
        final Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        final Iterator<InetAddress> iterator = Collections.list(inetAddresses).iterator();
        if (iterator.hasNext()) {
            final InetAddress inetAddress = iterator.next();
            return inetAddress.toString();
        }
        return "empty";
    }
    
    public static void storeProperWebServerSettings(final Properties webServerProps, final Boolean convertWebSettingsPropsBasedOnArch) throws Exception {
        try {
            final Boolean isDCProduct64bit = StartupUtil.isDCProduct64bit();
            if (convertWebSettingsPropsBasedOnArch && webServerProps != null && !webServerProps.isEmpty()) {
                if (isDCProduct64bit != null && isDCProduct64bit && webServerProps.containsKey("apache.threadsperchild")) {
                    webServerProps.setProperty("apache.threadsperchild.64bit", webServerProps.getProperty("apache.threadsperchild"));
                    webServerProps.remove("apache.threadsperchild");
                }
                else if (isDCProduct64bit != null && !isDCProduct64bit && webServerProps.containsKey("apache.threadsperchild")) {
                    webServerProps.setProperty("apache.threadsperchild.32bit", webServerProps.getProperty("apache.threadsperchild"));
                    webServerProps.remove("apache.threadsperchild");
                }
                else if (isDCProduct64bit == null) {
                    throw new Exception("Unable to determine the DC Prodcut Architecture...");
                }
            }
            updateWebServerProps(webServerProps);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static String getWebServerConfDir() throws Exception {
        final String serverHome = System.getProperty("server.home");
        String confDir = null;
        if ("apache".equalsIgnoreCase(getWebServerSettings().getProperty("webserver.name"))) {
            confDir = serverHome + File.separator + "apache" + File.separator + "conf";
        }
        else {
            confDir = serverHome + File.separator + "nginx" + File.separator + "conf";
        }
        return confDir;
    }
    
    public static String getWebServerConfFilePath() throws Exception {
        final String webServerName = getWebServerName();
        if ("apache".equalsIgnoreCase(webServerName)) {
            return System.getProperty("server.home") + File.separator + WebServerUtil.HTTPD_CONF_FILE;
        }
        return System.getProperty("server.home") + File.separator + NginxServerUtils.NGINX_CONF_FILE;
    }
    
    public static String getServerCertificateFilePath() throws Exception {
        String certificateFile = null;
        final Properties wsProps = getWebServerSettings();
        final String serverCrtFile = wsProps.getProperty("server.crt.loc");
        if (null == serverCrtFile || "".equals(serverCrtFile)) {
            certificateFile = getWebServerConfDir() + File.separator + wsProps.getProperty("apache.crt.loc");
        }
        else {
            certificateFile = getWebServerConfDir() + File.separator + wsProps.getProperty("server.crt.loc");
        }
        return certificateFile;
    }
    
    public static String getServerPrivateKeyFilePath() throws Exception {
        String certificateKeyFile = null;
        final Properties wsProps = getWebServerSettings();
        final String serverKeyFile = wsProps.getProperty("server.key.loc");
        if (null == serverKeyFile || "".equals(serverKeyFile)) {
            certificateKeyFile = getWebServerConfDir() + File.separator + wsProps.getProperty("apache.serverKey.loc");
        }
        else {
            certificateKeyFile = getWebServerConfDir() + File.separator + wsProps.getProperty("server.key.loc");
        }
        return certificateKeyFile;
    }
    
    public static void addOrUpdateWebServerProps(final String key, final String value) throws Exception {
        final Properties wsProps = getWebServerSettings();
        ((Hashtable<String, String>)wsProps).put(key, value);
        updateWebServerProps(wsProps);
    }
    
    public static void stopServers() throws Exception {
        stopServers(Boolean.FALSE);
    }
    
    public static void stopServers(final Boolean forceStop) throws Exception {
        stopWebServers();
        if (forceStop != null && forceStop) {
            stopWebServersForcefully();
        }
    }
    
    private static void stopWebServers() throws Exception {
        WebServerUtil.logger.log(Level.INFO, "WebServerUtil.stopServers method starts");
        stopApacheService();
        NginxServerUtils.stopNginxServer(System.getProperty("server.home"));
        final String webServerName = getWebServerName();
        if ("apache".equalsIgnoreCase(webServerName)) {
            WebServerUtil.webServerName = "apache";
        }
        else if ("nginx".equalsIgnoreCase(webServerName)) {
            WebServerUtil.webServerName = "nginx";
        }
        WebServerUtil.logger.log(Level.INFO, "WebServerUtil.stopServers method ends");
    }
    
    public static void stopApacheService() throws Exception {
        stopApacheService(Boolean.FALSE);
    }
    
    public static void stopApacheService(final Boolean forceStop) throws Exception {
        stopApacheServer();
        if (forceStop != null && forceStop) {
            final Boolean status = apacheForceStop();
            WebServerUtil.logger.log(Level.INFO, "Force Stopping Status for Nginx Web Server " + status);
        }
    }
    
    private static void stopApacheServer() throws Exception {
        WebServerUtil.logger.log(Level.INFO, "WebServerUtil.stopApacheService method starts");
        if (isApacheServiceRunning()) {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String stopResult = apacheHttpdInvoke(serverHome, "stop");
            WebServerUtil.logger.log(Level.INFO, "Apache Server Stop Result - " + stopResult);
        }
        WebServerUtil.logger.log(Level.INFO, "WebServerUtil.stopApacheService method ends");
    }
    
    public HashMap getApacheCertificateFileNames() throws Exception {
        Properties webSettingsProp = new Properties();
        final HashMap apacheCertificateFileName = new HashMap();
        try {
            webSettingsProp = getWebServerSettings();
            final String oldServerCrtFileFromProperty = String.valueOf(webSettingsProp.getProperty("server.crt.loc", ""));
            final String oldServerKeyFileFromProperty = String.valueOf(webSettingsProp.getProperty("server.key.loc", ""));
            final String oldIntermediateFileFromProperty = String.valueOf(webSettingsProp.getProperty("apache.ssl.intermediate.ca.file", ""));
            final String oldRootFileFromProperty = String.valueOf(webSettingsProp.getProperty("apache.ssl.root.ca.file", ""));
            final String oldRootKeyFileFromProperty = "DMRootCA.key";
            apacheCertificateFileName.put("server.crt.loc", oldServerCrtFileFromProperty);
            apacheCertificateFileName.put("server.key.loc", oldServerKeyFileFromProperty);
            apacheCertificateFileName.put("apache.ssl.intermediate.ca.file", oldIntermediateFileFromProperty);
            apacheCertificateFileName.put("apache.ssl.root.ca.file", oldRootFileFromProperty);
            apacheCertificateFileName.put("apache.ssl.root.ca.key.file", oldRootKeyFileFromProperty);
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Exception in getApacheOldCertificateFiles... ", ex);
            throw ex;
        }
        return apacheCertificateFileName;
    }
    
    public HashMap getApacheOldCertificateFile(final String apacheFolder) throws Exception {
        final HashMap apacheOldCertificateFiles = new HashMap();
        HashMap apacheCertificateFileName = new HashMap();
        try {
            apacheCertificateFileName = this.getApacheCertificateFileNames();
            String oldServerCrtFileName = null;
            String oldServerKeyFileName = null;
            String oldIntermediateFileName = null;
            String oldRootFileName = null;
            String oldRootKeyFileName = null;
            final String serverHome = System.getProperty("server.home");
            final String confDirectory = serverHome + File.separator + apacheFolder + File.separator + "conf";
            if (apacheCertificateFileName != null && !apacheCertificateFileName.isEmpty()) {
                final String apacheServerCrt = String.valueOf(apacheCertificateFileName.get("server.crt.loc"));
                if (apacheServerCrt != null && !apacheServerCrt.isEmpty()) {
                    oldServerCrtFileName = confDirectory + File.separator + apacheCertificateFileName.get("server.crt.loc");
                    apacheOldCertificateFiles.put("server.crt.loc", oldServerCrtFileName);
                }
                final String apacheServerKey = String.valueOf(apacheCertificateFileName.get("server.key.loc"));
                if (apacheServerKey != null && !apacheServerKey.isEmpty()) {
                    oldServerKeyFileName = confDirectory + File.separator + apacheCertificateFileName.get("server.key.loc");
                    apacheOldCertificateFiles.put("server.key.loc", oldServerKeyFileName);
                }
                final String apacheIntermediateCrt = String.valueOf(apacheCertificateFileName.get("apache.ssl.intermediate.ca.file"));
                if (apacheIntermediateCrt != null && !apacheIntermediateCrt.isEmpty()) {
                    oldIntermediateFileName = confDirectory + File.separator + apacheCertificateFileName.get("apache.ssl.intermediate.ca.file");
                    apacheOldCertificateFiles.put("apache.ssl.intermediate.ca.file", oldIntermediateFileName);
                }
                final String apacheRootCrt = String.valueOf(apacheCertificateFileName.get("apache.ssl.root.ca.file"));
                if (apacheRootCrt != null && !apacheRootCrt.isEmpty()) {
                    oldRootFileName = confDirectory + File.separator + apacheCertificateFileName.get("apache.ssl.root.ca.file");
                    apacheOldCertificateFiles.put("apache.ssl.root.ca.file", oldRootFileName);
                }
                final String apacheRootKey = String.valueOf(apacheCertificateFileName.get("apache.ssl.root.ca.key.file"));
                if (apacheRootKey != null && !apacheRootKey.isEmpty()) {
                    oldRootKeyFileName = confDirectory + File.separator + apacheCertificateFileName.get("apache.ssl.root.ca.key.file");
                    apacheOldCertificateFiles.put("apache.ssl.root.ca.key.file", oldRootKeyFileName);
                }
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Exception in getApacheOldCertificateFiles... ", ex);
            throw ex;
        }
        return apacheOldCertificateFiles;
    }
    
    public static void generateWebServerConfFiles(final String webServerName) throws Exception {
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final Properties wsProps = getWebServerSettings();
            boolean isStaticFileServerEnabled = false;
            isStaticFileServerEnabled = (webServerName.equalsIgnoreCase("apache") || webServerName.equalsIgnoreCase("nginx"));
            setBasicAuth(wsProps);
            setDomainAuth(wsProps);
            if (wsProps != null) {
                setClientCertAuth(wsProps);
            }
            setInsecureCommunicationProps(wsProps);
            generateApacheHttpdConfFiles(wsProps);
            if (webServerName.equalsIgnoreCase("nginx")) {
                NginxServerUtils.setServerLimitingSettings(wsProps);
                NginxServerUtils.generateNginxConfFiles(wsProps);
            }
            WebServerUtil.logger.log(Level.INFO, "Server IP : " + wsProps.getProperty("server.ip"));
            generateTomcatConfFiles(wsProps, isStaticFileServerEnabled);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static void setDomainAuth(final Properties wsProps) {
        if (wsProps != null && wsProps.containsKey("enable.domain.folder.auth") && wsProps.getProperty("enable.domain.folder.auth").equals("true")) {
            wsProps.setProperty("domain.folder.auth", "");
        }
        else {
            wsProps.setProperty("domain.folder.auth", "#");
        }
    }
    
    private static void generateApacheHttpdConfFiles(final Properties wsProps) {
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final String serverHomeForApache = serverHome.replaceAll("\\\\", "/");
            final String clientRootCAPath = wsProps.getProperty("client.rootca.certificate.file");
            String clientRootCACertFileForApache = "";
            if (clientRootCAPath != null && clientRootCAPath != "") {
                clientRootCACertFileForApache = clientRootCAPath.replaceAll("\\\\", "/");
            }
            final String httpdFileName = serverHome + File.separator + WebServerUtil.HTTPD_CONF_FILE;
            final String httpdTemplateFileName = serverHome + File.separator + WebServerUtil.HTTPD_CONF_TEMPLATE_FILE;
            wsProps.setProperty("apache.xframe.options.key", getXframeHeader(wsProps));
            wsProps.setProperty("SERVER_HOME", serverHomeForApache);
            wsProps.setProperty("SERVER_HOST_NAME", InetAddress.getLocalHost().getHostName());
            wsProps.setProperty("apache.sslprotocol", getSSLProtocol(wsProps));
            wsProps.setProperty("client.rootca.certificate.file.apache", clientRootCACertFileForApache);
            includeThirdPartySslCertificates(wsProps, "apache.ssl.root.ca.file", "enable.apache.ssl.root.ca.file");
            includeThirdPartySslCertificates(wsProps, "apache.ssl.intermediate.ca.file", "enable.apache.ssl.intermediate.ca.file");
            WebServerUtil.logger.log(Level.INFO, "Web Settings Properties after adding third party files {0}", wsProps);
            includeGzipCompressionFiles(wsProps);
            includeFwsLicenseCheck(wsProps);
            includeOSDHTTPReposForApache(wsProps, serverHome);
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(httpdTemplateFileName, httpdFileName, wsProps, "%");
            final String httpdsslFileName = serverHome + File.separator + WebServerUtil.HTTPD_SSL_CONF_FILE;
            final String httpdsslTemplateFileName = serverHome + File.separator + WebServerUtil.HTTPD_SSL_CONF_TEMPLATE_FILE;
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(httpdsslTemplateFileName, httpdsslFileName, wsProps, "%");
            final String workerPropsFileName = serverHome + File.separator + WebServerUtil.WORKERS_PROPERTIES_FILE;
            final String workerPropsTemplateFileName = serverHome + File.separator + WebServerUtil.WORKERS_PROPERTIES_TEMPLATE_FILE;
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(workerPropsTemplateFileName, workerPropsFileName, wsProps, "%");
            checkAndAddHttpProtocolOptions();
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception in generateApacheHttpdConfFiles()", ex);
            ex.printStackTrace();
        }
    }
    
    private static void checkAndAddHttpProtocolOptions() throws Exception {
        String apacheVersion = getApacheVersion();
        final int startIndex = apacheVersion.indexOf("Apache/");
        apacheVersion = apacheVersion.substring(startIndex + 7);
        final String trimmedApacheVersion = apacheVersion.substring(0, apacheVersion.indexOf(" "));
        final String httpProtocolsOptions = "\nHttpProtocolOptions Unsafe";
        final String httpConfFile = getServerHomeCanonicalPath() + File.separator + WebServerUtil.HTTPD_CONF_FILE;
        if ("greater".equals(compareApacheVersion(trimmedApacheVersion, "2.4.23"))) {
            Files.write(Paths.get(httpConfFile, new String[0]), httpProtocolsOptions.getBytes(), StandardOpenOption.APPEND);
        }
    }
    
    public static String compareApacheVersion(final String apacheVersion1, final String apacheVersion2) {
        String result = null;
        try {
            String[] splittedApacheVersion1 = apacheVersion1.split("\\.");
            String[] splittedApacheVersion2 = apacheVersion2.split("\\.");
            int maxLength = splittedApacheVersion1.length;
            if (splittedApacheVersion1.length < splittedApacheVersion2.length) {
                maxLength = splittedApacheVersion2.length;
                splittedApacheVersion1 = getSplittedVersion(splittedApacheVersion1, maxLength);
            }
            else if (splittedApacheVersion1.length > splittedApacheVersion2.length) {
                splittedApacheVersion2 = getSplittedVersion(splittedApacheVersion2, maxLength);
            }
            int apacheVersion1AsInt = 0;
            int apacheVersion2AsInt = 0;
            for (int index = 0; index < maxLength; ++index) {
                apacheVersion1AsInt = Integer.parseInt(splittedApacheVersion1[index]);
                apacheVersion2AsInt = Integer.parseInt(splittedApacheVersion2[index]);
                if (apacheVersion1AsInt > apacheVersion2AsInt) {
                    result = "greater";
                    return result;
                }
                if (apacheVersion1AsInt < apacheVersion2AsInt) {
                    result = "less";
                    return result;
                }
            }
            result = "equal";
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while comparing Apache version. Exception : ", ex);
        }
        return result;
    }
    
    public static String[] getSplittedVersion(final String[] versionArray, final int length) {
        final String[] result = new String[length];
        try {
            final int existLength = versionArray.length;
            for (int index = 0; index < existLength; ++index) {
                result[index] = versionArray[index];
            }
            for (int index = existLength; index < length; ++index) {
                result[index] = "0";
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occured while splitting Apache version for comparing. Exception : ", ex);
        }
        return result;
    }
    
    public static void includeThirdPartySslCertificates(final Properties propFile, final String certificateKey, final String lineIncludeKey) {
        if (propFile.containsKey(certificateKey)) {
            final String intermediateFileName = String.valueOf(((Hashtable<K, Object>)propFile).get(certificateKey)).trim();
            if (intermediateFileName.isEmpty()) {
                propFile.setProperty(lineIncludeKey, "#");
            }
            else {
                propFile.setProperty(lineIncludeKey, "");
            }
        }
    }
    
    public static String apacheHttpdInvoke(final String serverHome, final String action) throws Exception {
        return apacheHttpdInvoke(serverHome, action, null);
    }
    
    public static String apacheHttpdInvoke(final String serverHome, final String action, String serviceName) throws Exception {
        String result = "";
        String httpdCmd = "";
        WebServerUtil.logger.log(Level.INFO, "apacheHttpdInvoke() invoked with arguments serverHome: " + serverHome + " action: " + action + " serviceName: " + serviceName);
        try {
            if (serviceName == null || serviceName.trim().length() == 0) {
                serviceName = getApacheServiceName();
            }
            if (!serviceName.startsWith("\"")) {
                serviceName = "\"" + serviceName + "\"";
            }
            final String httpdExe = "\"" + serverHome + File.separator + WebServerUtil.HTTPD_EXE + "\"";
            httpdCmd = httpdExe + " -k " + action + " -n " + serviceName;
            WebServerUtil.logger.log(Level.INFO, "Httpd command to be executed: " + httpdCmd);
            final ProcessBuilder p = new ProcessBuilder(new String[] { httpdExe, "-k", action, "-n", serviceName });
            final Process proc = p.start();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                result = result + str + "\n";
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.INFO, "Caught error while executing the command: " + httpdCmd + " exception: " + ex);
            throw ex;
        }
        return result;
    }
    
    public static boolean isApacheServiceRunning() {
        boolean result = false;
        String serviceName = getApacheServiceName();
        serviceName = trimServiceNameApacheCompatible(serviceName);
        result = StartupUtil.isServiceRunning(serviceName);
        return result;
    }
    
    public static boolean isApacheServiceStopped() {
        boolean result = false;
        String serviceName = getApacheServiceName();
        serviceName = trimServiceNameApacheCompatible(serviceName);
        result = StartupUtil.isServiceStopped(serviceName);
        return result;
    }
    
    public static String trimServiceNameApacheCompatible(final String serviceName) {
        return serviceName.replaceAll(" ", "");
    }
    
    public static String checkApacheService(final String serverHome) {
        String result = "";
        String httpdCmd = "";
        try {
            final String serviceName = getApacheServiceName();
            final String httpdExe = "\"" + serverHome + File.separator + WebServerUtil.HTTPD_EXE + "\"";
            httpdCmd = httpdExe + " -n " + serviceName + " -t";
            WebServerUtil.logger.log(Level.INFO, "Httpd Check Command to be executed: " + httpdCmd);
            final ProcessBuilder proc = new ProcessBuilder(new String[] { httpdExe, "-n", serviceName, "-t" });
            final Process p = proc.start();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                result = result + str + "\n";
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught error while executing the command: " + httpdCmd + " exception: ", ex);
        }
        return result;
    }
    
    private static void generateTomcatConfFiles(final Properties wsProps, final boolean isApacheEnabled) {
        try {
            final String httpPort = wsProps.getProperty("http.port");
            final String httpsPort = wsProps.getProperty("https.port");
            final Properties wsPropsSmall = new Properties();
            wsPropsSmall.setProperty("http.port", httpPort);
            wsPropsSmall.setProperty("https.port", httpsPort);
            final String serverHome = getServerHomeCanonicalPath();
            final String serverConfFile = serverHome + File.separator + WebServerUtil.SERVER_CONF_FILE;
            String serverConfTemplateFile = null;
            if (isApacheEnabled) {
                serverConfTemplateFile = serverHome + File.separator + WebServerUtil.SERVER_CONF_APACHE_TEMPLATE_FILE;
            }
            else {
                serverConfTemplateFile = serverHome + File.separator + WebServerUtil.SERVER_CONF_TEMPLATE_FILE;
            }
            final String apacheSslProtocols = wsProps.getProperty("apache.sslprotocol");
            final String wssSupportedProtocols = apacheSslProtocols.replaceAll(" ", ",");
            wsProps.setProperty("apache.sslprotocol", wssSupportedProtocols);
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(serverConfTemplateFile, serverConfFile, wsProps, "%");
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception in generateTomcatConfFiles()", ex);
            ex.printStackTrace();
        }
    }
    
    public static String getWebSocketSupportedProtocols(final String apacheSslProtocols, final String supportedProtocols) {
        final String[] protocolsInConf = apacheSslProtocols.split(" ");
        final List<String> wssSupportedProtocols = new ArrayList<String>();
        final StringBuilder finalSupportedProtocols = new StringBuilder();
        for (final String protocol : supportedProtocols.split(" ")) {
            wssSupportedProtocols.add(protocol);
        }
        for (String protocol : protocolsInConf) {
            if (protocol.startsWith("-")) {
                protocol = protocol.replace("-", "");
                wssSupportedProtocols.remove(protocol);
            }
        }
        for (final String protocol2 : wssSupportedProtocols) {
            finalSupportedProtocols.append(protocol2).append(",");
        }
        finalSupportedProtocols.deleteCharAt(finalSupportedProtocols.length() - 1);
        return finalSupportedProtocols.toString();
    }
    
    private static boolean checkWithServerPorts(final String portNo, final Properties webSettingsProps) {
        final String httpPort = webSettingsProps.getProperty("http.port");
        final String httpsPort = webSettingsProps.getProperty("https.port");
        final boolean status = !portNo.equals(httpPort) && !portNo.equals(httpsPort);
        return status;
    }
    
    public static void validateServerXmlConnector(final String webSettingsKey) {
        try {
            WebServerUtil.logger.log(Level.INFO, "validateServerXmlConnector - Validating " + webSettingsKey + " in Server.xml");
            final Properties webSettingsProps = getWebServerSettings();
            final String strPortToValidate = webSettingsProps.getProperty(webSettingsKey);
            if (strPortToValidate != null && checkWithServerPorts(strPortToValidate, webSettingsProps) && DCStarter.isPortFree(Integer.valueOf(strPortToValidate))) {
                if (!webSettingsKey.equals("wss.port") || DCStarter.isUdpPortFree(Integer.valueOf(strPortToValidate))) {
                    final String apacheSslProtocols = webSettingsProps.getProperty("apache.sslprotocol");
                    final String wssSupportedProtocols = getWebSocketSupportedProtocols(apacheSslProtocols, "SSLv2 SSLv3 TLSv1 TLSv1.1 TLSv1.2");
                    webSettingsProps.setProperty("apache.sslprotocol", wssSupportedProtocols);
                    addConnectorInTomcatServerConf(webSettingsKey, webSettingsProps);
                }
                else {
                    removeConnectorInTomcatServerConf(strPortToValidate);
                }
            }
            else if (strPortToValidate != null) {
                removeConnectorInTomcatServerConf(strPortToValidate);
            }
            else {
                WebServerUtil.logger.log(Level.SEVERE, "validateServerXmlConnector - Given property not found in WebSettings Conf");
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "validateServerXmlConnector - Exception while validation tomcat connector", ex);
        }
    }
    
    public static Document createDocumentObjectFromXmlFile(final String filePath) {
        Document xmlDoc = null;
        try {
            final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
            final File xmlFile = new File(filePath);
            if (xmlFile.exists()) {
                xmlDoc = docBuilder.parse(xmlFile);
            }
            else {
                WebServerUtil.logger.log(Level.SEVERE, "createDocumentObjectFromXmlFile - Given path does not exists ... returning null");
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "createDocumentObjectFromXmlFile - Exception while parsing XML File.", ex);
        }
        return xmlDoc;
    }
    
    public static void addConnectorInTomcatServerConf(final String portKeyInTemplate, final Properties findReplaceSet) {
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final String serverConfFilePath = serverHome + File.separator + WebServerUtil.SERVER_CONF_FILE;
            final Document serverXmlDoc = createDocumentObjectFromXmlFile(serverConfFilePath);
            final Element connectorInConf = getConnectorElementWithPortValue(serverXmlDoc, findReplaceSet.getProperty(portKeyInTemplate));
            if (connectorInConf == null) {
                final String serverConfTemplatePath = serverHome + File.separator + WebServerUtil.SERVER_CONF_APACHE_TEMPLATE_FILE;
                final Document serverTemplateDoc = createDocumentObjectFromXmlFile(serverConfTemplatePath);
                final String strPortFormatInTemplate = "%" + portKeyInTemplate + "%";
                final Element connectorInTemplate = getConnectorElementWithPortValue(serverTemplateDoc, strPortFormatInTemplate);
                if (connectorInTemplate != null) {
                    connectorInTemplate.setAttribute("port", findReplaceSet.getProperty(portKeyInTemplate));
                    if (findReplaceSet.getProperty("apache.sslprotocol") != null && connectorInTemplate.getAttribute("sslEnabledProtocols") != null && !connectorInTemplate.getAttribute("sslEnabledProtocols").equals("")) {
                        connectorInTemplate.setAttribute("sslEnabledProtocols", findReplaceSet.getProperty("apache.sslprotocol"));
                    }
                    if (findReplaceSet.getProperty("apache.sslciphersuite") != null && connectorInTemplate.getAttribute("ciphers") != null && !connectorInTemplate.getAttribute("ciphers").equals("")) {
                        connectorInTemplate.setAttribute("ciphers", findReplaceSet.getProperty("apache.sslciphersuite"));
                    }
                    final Node serviceNode = getCatalinaServiceNode(serverXmlDoc);
                    if (serviceNode != null) {
                        final Node importedNode = serverXmlDoc.importNode(connectorInTemplate, true);
                        serviceNode.appendChild(importedNode);
                        serverXmlDoc.getDocumentElement().appendChild(serviceNode);
                        WebServerUtil.logger.log(Level.INFO, "addConnectorInTomcatServerConf - Adding Connector for " + portKeyInTemplate + " in Server.xml");
                        writeDocumentToXmlFile(serverXmlDoc, serverConfFilePath);
                    }
                    else {
                        WebServerUtil.logger.log(Level.SEVERE, "addConnectorInTomcatServerConf - Unable to get Service node index.");
                    }
                }
                else {
                    WebServerUtil.logger.log(Level.SEVERE, "addConnectorInTomcatServerConf - Connector for given key not found in template");
                    if (!Boolean.parseBoolean(findReplaceSet.getProperty("enforce.https.communication")) && portKeyInTemplate.equals("ws.port")) {
                        final Element connector = serverXmlDoc.createElement("Connector");
                        connector.setAttribute("port", findReplaceSet.getProperty("ws.port"));
                        connector.setAttribute("protocol", "org.apache.coyote.http11.Http11NioProtocol");
                        connector.setAttribute("maxConnections", "5000");
                        connector.setAttribute("connectionTimeout", "300000");
                        connector.setAttribute("keepaliveTimeout", "300000");
                        connector.setAttribute("socket.bufferPool", "-1");
                        connector.setAttribute("maxKeepAliveRequests", "-1");
                        connector.setAttribute("socket.soLingerTime", "5");
                        connector.setAttribute("maxSavePostSize", "-1");
                        connector.setAttribute("enableLookups", "false");
                        connector.setAttribute("maxHttpHeaderSize", "8192");
                        final Node serviceNode2 = getCatalinaServiceNode(serverXmlDoc);
                        if (serviceNode2 != null) {
                            final Node importedNode2 = serverXmlDoc.importNode(connector, true);
                            serviceNode2.appendChild(importedNode2);
                            serverXmlDoc.getDocumentElement().appendChild(serviceNode2);
                            writeDocumentToXmlFile(serverXmlDoc, serverConfFilePath);
                            WebServerUtil.logger.log(Level.INFO, "WS Port added successfully in server xml for evaluation http customers");
                        }
                        else {
                            WebServerUtil.logger.log(Level.SEVERE, "addConnectorInTomcatServerConf - Unable to get Service node index.");
                        }
                    }
                }
            }
            else {
                WebServerUtil.logger.log(Level.INFO, "addConnectorInTomcatServerConf - Connector already present for given port value");
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "addConnectorInTomcatServerConf - Exception while adding connector in conf", ex);
        }
    }
    
    public static void removeConnectorInTomcatServerConf(final String strPortValue) {
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final String serverConfFilePath = serverHome + File.separator + WebServerUtil.SERVER_CONF_FILE;
            final Document serverXmlDoc = createDocumentObjectFromXmlFile(serverConfFilePath);
            final Element connectorInConf = getConnectorElementWithPortValue(serverXmlDoc, strPortValue);
            if (connectorInConf != null) {
                final Node serviceNode = getCatalinaServiceNode(serverXmlDoc);
                if (serviceNode != null) {
                    serviceNode.removeChild(connectorInConf);
                    serverXmlDoc.getDocumentElement().appendChild(serviceNode);
                    WebServerUtil.logger.log(Level.INFO, "removeConnectorInTomcatServerConf - Removing Connector entry with port value " + strPortValue + " from Server.xml");
                    writeDocumentToXmlFile(serverXmlDoc, serverConfFilePath);
                }
                else {
                    WebServerUtil.logger.log(Level.SEVERE, "removeConnectorInTomcatServerConf - Unable to find Catalina Service node");
                }
            }
            else {
                WebServerUtil.logger.log(Level.INFO, "removeConnectorInTomcatServerConf - Connector already absent");
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "Exception while removing connector from Server.xml", ex);
        }
    }
    
    private static void writeDocumentToXmlFile(final Document xmlDoc, final String filePath) throws Exception {
        try {
            final Transformer transformer = XMLUtils.getTransformerInstance();
            final Result output = new StreamResult(new File(filePath));
            final Source input = new DOMSource(xmlDoc);
            transformer.transform(input, output);
            WebServerUtil.logger.log(Level.INFO, "writeDocumentToXmlFile - Document object written to XML file successfully");
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "writeDocumentToXmlFile - Exception while writing Xml Document to file", ex);
            throw ex;
        }
    }
    
    private static Node getCatalinaServiceNode(final Document serverXmlDoc) {
        Node catServiceNode = null;
        try {
            final NodeList serviceNl = serverXmlDoc.getElementsByTagName("Service");
            for (int nlIndex = 0; nlIndex < serviceNl.getLength(); ++nlIndex) {
                final Node tempNode = serviceNl.item(nlIndex);
                if (tempNode.getNodeType() == 1 && ((Element)tempNode).getAttribute("name").equalsIgnoreCase("Catalina")) {
                    catServiceNode = tempNode;
                    break;
                }
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "getCatalinaServiceNode - Exception while getting Catalina service node");
            throw ex;
        }
        return catServiceNode;
    }
    
    private static Element getConnectorElementWithPortValue(final Document confFileDoc, final String portValue) {
        Element connectorElement = null;
        if (confFileDoc != null) {
            confFileDoc.getDocumentElement().normalize();
            final NodeList nlConnectors = confFileDoc.getElementsByTagName("Connector");
            for (int requiredConectorIndex = 0; requiredConectorIndex < nlConnectors.getLength(); ++requiredConectorIndex) {
                final Node connectorNode = nlConnectors.item(requiredConectorIndex);
                if (connectorNode.getNodeType() == 1) {
                    final Element tempElement = (Element)connectorNode;
                    if (portValue.equals(tempElement.getAttribute("port"))) {
                        WebServerUtil.logger.log(Level.INFO, "getConnectorElementWithPortValue - Connector node found for given port value");
                        connectorElement = tempElement;
                        break;
                    }
                }
            }
        }
        else {
            WebServerUtil.logger.log(Level.SEVERE, "getConnectorElementWithPortValue : Unable to create Document for XML file.");
        }
        return connectorElement;
    }
    
    public static boolean isWebSocketGatewayEnabled() {
        return WebServerUtil.isWSGatewayEnabled;
    }
    
    public static void validateTomcatConfForPortUsage() {
        try {
            final Properties wsSettingsProps = getWSSettingsProps();
            final Properties webSettingsProps = getWebServerSettings();
            final String isWebSocketGatewayEnabled = wsSettingsProps.getProperty("IsWSGatewayEnabled");
            if (isWebSocketGatewayEnabled != null && isWebSocketGatewayEnabled.equalsIgnoreCase("true")) {
                validateServerXmlConnector("ws.port");
                validateServerXmlConnector("wss.port");
                final String portToRemove = webSettingsProps.getProperty("httpnio.port");
                if (portToRemove != null) {
                    removeConnectorInTomcatServerConf(portToRemove);
                }
                WebServerUtil.isWSGatewayEnabled = true;
            }
            else {
                removeConnectorInTomcatServerConf(webSettingsProps.getProperty("ws.port"));
                removeConnectorInTomcatServerConf(webSettingsProps.getProperty("wss.port"));
                addConnectorInTomcatServerConf("httpnio.port", webSettingsProps);
                WebServerUtil.isWSGatewayEnabled = false;
            }
            removeConnectorInTomcatServerConf("ws.port");
            removeConnectorInTomcatServerConf("wss.port");
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "validateTomcatConfForPortUsage - Following exception occurred", ex);
        }
    }
    
    public static boolean isPortStarted(final String portNo) {
        boolean isPortStarted = false;
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final String serverConfFilePath = serverHome + File.separator + WebServerUtil.SERVER_CONF_FILE;
            final Document serverXmlDoc = createDocumentObjectFromXmlFile(serverConfFilePath);
            final Element connectorInConf = getConnectorElementWithPortValue(serverXmlDoc, portNo);
            if (connectorInConf != null) {
                isPortStarted = true;
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "Exception while checking if port has started", ex);
        }
        return isPortStarted;
    }
    
    public static String getConnectorProtocolByPort(final String portNo) {
        String protocol = null;
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final String serverConfFilePath = serverHome + File.separator + WebServerUtil.SERVER_CONF_FILE;
            final Document serverXmlDoc = createDocumentObjectFromXmlFile(serverConfFilePath);
            final Element connectorInConf = getConnectorElementWithPortValue(serverXmlDoc, portNo);
            if (connectorInConf != null) {
                protocol = connectorInConf.getAttribute("protocol");
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.SEVERE, "Exception while fetching the protocol of a connector", ex);
        }
        return protocol;
    }
    
    public static String getServerHomeCanonicalPath() throws Exception {
        String serverHome = System.getProperty("server.home");
        if (serverHome != null) {
            serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        }
        return serverHome;
    }
    
    public static String getApacheAccessLogFileNamePrefix() {
        return "apache_accesslog";
    }
    
    public static String getApacheErrorLogFileNamePrefix() {
        return "apache_errorlog";
    }
    
    public static int getApacheLogFilesCountToMaintain() {
        return WebServerUtil.apacheLogFilesCount;
    }
    
    public static String getDateString(final long dateVal, final String format) {
        final Date date = new Date(dateVal);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
    
    public static String getApacheServiceName() {
        String apacheServiceName = "\"ManageEngine UEMS - Apache\"";
        try {
            final Properties wsProps = getWebServerSettings();
            WebServerUtil.logger.log(Level.INFO, "WebServer settings configured is: " + wsProps);
            apacheServiceName = wsProps.getProperty("apache.service.name");
            if (apacheServiceName == null) {
                apacheServiceName = "\"ManageEngine UEMS - Apache\"";
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception in getApacheServiceName()", e);
        }
        return apacheServiceName;
    }
    
    public static String getApacheVersion() {
        String result = "";
        final String httpdCmd = "";
        try {
            final String httpdExe = getServerHomeCanonicalPath() + File.separator + WebServerUtil.HTTPD_EXE;
            final List<String> command = new ArrayList<String>();
            command.add(httpdExe);
            command.add("-v");
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            WebServerUtil.logger.log(Level.INFO, "Httpd Command to be executed: " + processBuilder.command());
            final File filepath = new File(getServerHomeCanonicalPath());
            processBuilder.directory(filepath);
            final Process process = processBuilder.start();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                result = result + str + "\t";
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught error while executing the command: " + httpdCmd, ex);
        }
        return result;
    }
    
    public static void createShareAccessPropsFile(final Properties statusprops) {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String shareAccessPropName = serverHome + File.separator + WebServerUtil.SHARE_ACCESS_STATUS_FILE;
            final File shareAccessConfFile = new File(shareAccessPropName);
            if (!shareAccessConfFile.exists()) {
                final boolean isCreated = shareAccessConfFile.createNewFile();
                WebServerUtil.logger.log(Level.INFO, "Share access Conf File path : " + shareAccessConfFile.getCanonicalPath() + " is Created? : " + isCreated);
            }
            StartupUtil.storeProperties(statusprops, shareAccessPropName);
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while creating share access conf", ex);
        }
    }
    
    public static void deleteShareAccessPropsFile() {
        try {
            final String shareAccessPropName = getServerHomeCanonicalPath() + File.separator + WebServerUtil.SHARE_ACCESS_STATUS_FILE;
            final File shareAccessConfFile = new File(shareAccessPropName);
            if (shareAccessConfFile.exists()) {
                final boolean isDeleted = shareAccessConfFile.delete();
                WebServerUtil.logger.log(Level.INFO, "shareAccess File ( " + shareAccessConfFile + " ) is Deleted ? : " + isDeleted);
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while deleting share access properties file", ex);
        }
    }
    
    public static Properties getRepositoriesAccessStatus() {
        final Properties pathStatusProps = new Properties();
        try {
            final Properties wsProps = getWebServerSettings();
            final String storePathVal = wsProps.getProperty("store.loc");
            final boolean storePathStatus = hasWriteAccess(storePathVal);
            pathStatusProps.setProperty("store_access_status", Boolean.toString(storePathStatus));
            final String swPathVal = wsProps.getProperty("swrepository.loc");
            final boolean swPathStatus = hasWriteAccess(swPathVal);
            pathStatusProps.setProperty("swrepository_access_status", Boolean.toString(swPathStatus));
            final Hashtable<String, String> osdhttpRepos = getOSDHTTPRepos();
            final Iterator<String> osdhttpRepoNames = osdhttpRepos.keySet().iterator();
            final ArrayList<String> notAccessibleRepos = new ArrayList<String>();
            String osdRepoName = "";
            String osdRepoPath = "";
            boolean osdhttpRepoStatus = true;
            while (osdhttpRepoNames.hasNext()) {
                osdRepoName = osdhttpRepoNames.next();
                osdRepoPath = osdhttpRepos.get(osdRepoName);
                osdhttpRepoStatus = hasWriteAccess(osdRepoPath);
                if (!osdhttpRepoStatus) {
                    notAccessibleRepos.add(osdRepoName);
                }
            }
            writeNotReachableOSDPathsToFile(notAccessibleRepos);
            pathStatusProps.setProperty("osd_repository_access_status", Boolean.toString(osdhttpRepoStatus));
            pathStatusProps.setProperty("osd_repository_path", notAccessibleRepos.isEmpty() ? "" : osdhttpRepos.get(notAccessibleRepos.get(0)));
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while creating share access conf", ex);
        }
        return pathStatusProps;
    }
    
    public static boolean hasWriteAccess(final String sharePath) {
        return hasWriteAccess(sharePath, false);
    }
    
    public static boolean hasWriteAccess(final String sharePath, final Boolean isEveryoneAccessNeeded) {
        boolean retVal = false;
        try {
            final File sharePathLocation = new File(sharePath);
            final boolean direxists = sharePathLocation.isDirectory();
            WebServerUtil.logger.log(Level.INFO, "Share path Location {0} exists ? :: {1}", new Object[] { sharePathLocation, direxists });
            if (isEveryoneAccessNeeded && !isEveryOnePermissionAvailableForAllFolder(sharePath)) {
                return false;
            }
            final String timeStamp = DateTime.getCurrentDate_Time("yyyy-MM-dd-HH-mm-ss-sss", "");
            final String tempDirName = "testAccess-" + timeStamp;
            final String targetDir = sharePathLocation.getAbsolutePath() + File.separator + tempDirName;
            WebServerUtil.logger.log(Level.INFO, "Temp File Path to be created :: {0}", targetDir.replace("\\", "\\\\"));
            final File testDir = new File(targetDir);
            retVal = testDir.mkdir();
            WebServerUtil.logger.log(Level.INFO, "Is the Share has Write Access ? :: {0}", retVal);
            if (retVal && testDir.isDirectory()) {
                testDir.delete();
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while creating share access conf", ex);
        }
        return retVal;
    }
    
    public static Properties getApacheHttpdConfFilesPropertiesWithDefaultLoc(final Properties shareAccessStatus) {
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final String serverHomeForApache = serverHome.replaceAll("\\\\", "/");
            final String storeTempLocForApache = (serverHome + WebServerUtil.PATCH_STORE_DEFAULT_LOCATION).replaceAll("\\\\", "/");
            final String swTempLocForApache = (serverHome + WebServerUtil.SWREPOSITROY_DEFAULT_LOCATION).replaceAll("\\\\", "/");
            final String httpdFileName = serverHome + File.separator + WebServerUtil.HTTPD_CONF_FILE;
            final String httpdTemplateFileName = serverHome + File.separator + WebServerUtil.HTTPD_CONF_TEMPLATE_FILE;
            final Properties wsProps = getWebServerSettings();
            wsProps.setProperty("SERVER_HOME", serverHomeForApache);
            wsProps.setProperty("apache.xframe.options.key", getXframeHeader(wsProps));
            includeGzipCompressionFiles(wsProps);
            includeFwsLicenseCheck(wsProps);
            final Boolean storeStatus = Boolean.valueOf(shareAccessStatus.getProperty("store_access_status"));
            final Boolean swStatus = Boolean.valueOf(shareAccessStatus.getProperty("swrepository_access_status"));
            if (!storeStatus) {
                wsProps.setProperty("store.loc", storeTempLocForApache);
            }
            if (!swStatus) {
                wsProps.setProperty("swrepository.loc", swTempLocForApache);
            }
            return wsProps;
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception in generateApacheHttpdConfFileswithDefaultLoc()", ex);
            ex.printStackTrace();
            return null;
        }
    }
    
    public static void generateApacheHttpdConfFilesWithDefaultLoc(final Properties shareAccessStatus) {
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final String serverHomeForApache = serverHome.replaceAll("\\\\", "/");
            final String storeTempLocForApache = (serverHome + WebServerUtil.PATCH_STORE_DEFAULT_LOCATION).replaceAll("\\\\", "/");
            final String swTempLocForApache = (serverHome + WebServerUtil.SWREPOSITROY_DEFAULT_LOCATION).replaceAll("\\\\", "/");
            final String httpdFileName = serverHome + File.separator + WebServerUtil.HTTPD_CONF_FILE;
            final String httpdTemplateFileName = serverHome + File.separator + WebServerUtil.HTTPD_CONF_TEMPLATE_FILE;
            final Properties wsProps = getWebServerSettings();
            wsProps.setProperty("SERVER_HOME", serverHomeForApache);
            wsProps.setProperty("apache.xframe.options.key", getXframeHeader(wsProps));
            final Boolean storeStatus = Boolean.valueOf(shareAccessStatus.getProperty("store_access_status"));
            final Boolean swStatus = Boolean.valueOf(shareAccessStatus.getProperty("swrepository_access_status"));
            if (!storeStatus) {
                wsProps.setProperty("store.loc", storeTempLocForApache);
            }
            if (!swStatus) {
                wsProps.setProperty("swrepository.loc", swTempLocForApache);
            }
            includeGzipCompressionFiles(wsProps);
            includeFwsLicenseCheck(wsProps);
            includeOSDHTTPReposForApache(wsProps, serverHome);
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(httpdTemplateFileName, httpdFileName, wsProps, "%");
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception in generateApacheHttpdConfFileswithDefaultLoc()", ex);
            ex.printStackTrace();
        }
    }
    
    public static void openCustomBrowser(final Boolean isBrowser, final String customMsg) {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String trayIconFilePath = serverHome + File.separator + "conf" + File.separator + "trayicon.conf";
            final Properties props = StartupUtil.getProperties(trayIconFilePath);
            props.setProperty("launchCustomBrowser", String.valueOf(isBrowser));
            props.setProperty("customMsg", customMsg);
            StartupUtil.storeProperties(props, trayIconFilePath);
            WebServerUtil.logger.log(Level.INFO, "Updated TrayIcon file ");
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.INFO, "Exception in updating trayicon.conf");
        }
    }
    
    public static void openBrowserUsingDCWinutil() {
        openBrowserUsingDCWinutil(null);
    }
    
    public static void openBrowserUsingDCWinutil(String url) {
        try {
            url = ((url == null) ? (getServerProtocol() + "://" + getMachineName() + ":" + getAvailablePort()) : url);
            WebServerUtil.logger.log(Level.INFO, "Going to launch console form WebServerUtil !!!");
            final String winUtilExe = System.getProperty("server.home") + File.separator + "bin" + File.separator + "dcwinutil.exe";
            executeCommand(winUtilExe, "-invokeBrowser", "-System", url);
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.INFO, "launch console form WebServerControllerService failed !!!");
        }
    }
    
    public static int getAvailablePort() {
        try {
            if (WebServerUtil.usedPort == 80) {
                WebServerUtil.usedPort = getWebServerPort();
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception while getting available port !", e);
        }
        return WebServerUtil.usedPort;
    }
    
    public static int executeCommand(final String... commandWithArgs) {
        int exitValue = -1;
        WebServerUtil.logger.log(Level.INFO, "----------------------- In Execute command ----------------------------");
        String output = "";
        BufferedReader commandOutput = null;
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
            processBuilder.environment().put("OPENSSL_CONF", NginxServerUtils.getNginxConfDirPath());
            WebServerUtil.logger.log(Level.INFO, "COMMAND: {0}", processBuilder.command());
            processBuilder.redirectErrorStream(true);
            final Process process = processBuilder.start();
            commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = "";
            while ((s = commandOutput.readLine()) != null) {
                WebServerUtil.logger.log(Level.INFO, s);
                output += s;
            }
            exitValue = process.waitFor();
            WebServerUtil.logger.log(Level.INFO, "EXIT VALUE :: {0}", exitValue);
            WebServerUtil.logger.log(Level.INFO, "OUT : " + output);
        }
        catch (final IOException ioe) {
            WebServerUtil.logger.log(Level.WARNING, "IOException while executing command " + Arrays.asList(commandWithArgs), ioe);
        }
        catch (final InterruptedException ie) {
            WebServerUtil.logger.log(Level.WARNING, "IOException while executing command " + Arrays.asList(commandWithArgs), ie);
        }
        finally {
            try {
                if (commandOutput != null) {
                    commandOutput.close();
                }
            }
            catch (final Exception exp) {
                WebServerUtil.logger.log(Level.WARNING, "Exception : ", exp);
            }
        }
        WebServerUtil.logger.log(Level.INFO, "---------------------- End of Execute command -------------------------");
        return exitValue;
    }
    
    public static void generateHtmlRedirectionFile(final String pageTitle, final String heading, final String unReachableMsg, final String solution, final String solutionDetails, final String solutionHeading, final String supportMsg) throws Exception {
        FileWriter writeObj = null;
        try {
            WebServerUtil.logger.log(Level.INFO, "Generate file");
            final String htmlFilename = getServerHomeCanonicalPath() + File.separator + WebServerUtil.MAINTENANCE_MODE_TEMPLATE_FILE;
            final String htmlnewFilename = getServerHomeCanonicalPath() + File.separator + WebServerUtil.MAINTENANCE_MODE_FILE;
            final FileReader readObj = new FileReader(htmlFilename);
            writeObj = new FileWriter(htmlnewFilename);
            final BufferedReader bf = new BufferedReader(readObj);
            final String productLogoPath = "%productLogoPath%";
            final String PageHeading = "%PageHeading%";
            final String UnableToStartHtmlPageHeading = "%UnableToStartHtmlPageHeading%";
            final String UnableToStartMessage = "%UnableToStartMessage%";
            final String solutionVariable = "%Solution%";
            final String SolutionDetails = "%SolutionDetails%";
            final String SolutionHeading = "%SolutionHeading%";
            final String ContactSupportMsg = "%ContactSupportMsg%";
            String line;
            while ((line = bf.readLine()) != null) {
                if (line.contains(PageHeading)) {
                    line = line.replaceAll(PageHeading, pageTitle);
                }
                else if (line.contains(UnableToStartHtmlPageHeading)) {
                    line = heading;
                }
                else if (line.contains(UnableToStartMessage)) {
                    line = unReachableMsg;
                }
                else if (line.contains(solutionVariable)) {
                    line = solution;
                }
                else if (line.contains(SolutionDetails)) {
                    line = solutionDetails;
                }
                else if (line.contains(SolutionHeading)) {
                    line = line.replaceAll(SolutionHeading, solutionHeading);
                }
                else if (line.contains(ContactSupportMsg)) {
                    line = line.replaceAll(ContactSupportMsg, supportMsg);
                }
                else if (line.contains(productLogoPath)) {
                    final String productcode = GeneralPropertiesLoader.getInstance().getProperties().getProperty("productcode");
                    if ("VMP".equalsIgnoreCase(productcode)) {
                        line = line.replaceAll(productLogoPath, "http://tools.manageengine.com/images/desktop-central/vmp-logo.png");
                    }
                    else if ("PMP".equalsIgnoreCase(productcode)) {
                        line = line.replaceAll(productLogoPath, "http://tools.manageengine.com/images/desktop-central/pmp-logo.png");
                    }
                    else if ("OSD".equalsIgnoreCase(productcode)) {
                        line = line.replaceAll(productLogoPath, "http://tools.manageengine.com/images/desktop-central/osd-logo.png");
                    }
                    else {
                        line = line.replaceAll(productLogoPath, "http://tools.manageengine.com/images/desktop-central/dc-logo.png");
                    }
                }
                writeObj.write(line);
                writeObj.write("\n");
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.SEVERE, "Exception occured while generating HtmlRedirection : ", e);
            throw e;
        }
        finally {
            try {
                writeObj.flush();
                writeObj.close();
            }
            catch (final IOException ex) {
                WebServerUtil.logger.log(Level.SEVERE, "Exception occured while generating HtmlRedirection : ", ex);
            }
        }
    }
    
    public static String getProductUrl() {
        String productUrl = "http://www.manageengine.com/products/desktop-central";
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + "conf" + File.separator + "general_properties.conf";
            final Properties generalProperties = StartupUtil.getProperties(fname);
            if (generalProperties != null) {
                productUrl = ((generalProperties.getProperty("prodUrl") != null) ? generalProperties.getProperty("prodUrl") : productUrl);
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.INFO, "Exception while fetching product url: " + e);
        }
        return productUrl;
    }
    
    public static String getUploadLogsUrl() {
        String productUrl = "";
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + "conf" + File.separator + "general_properties.conf";
            final Properties generalProperties = StartupUtil.getProperties(fname);
            if (generalProperties != null) {
                productUrl = ((generalProperties.getProperty("prodUploadLogsUrl") != null) ? generalProperties.getProperty("prodUploadLogsUrl") : productUrl);
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.INFO, "Exception while fetching getUploadLogsUrl: " + e);
        }
        return productUrl;
    }
    
    public static void deleteMaintenanceHtmlFile() {
        try {
            final String htmlFileName = getServerHomeCanonicalPath() + File.separator + WebServerUtil.MAINTENANCE_MODE_FILE;
            final File htmlFile = new File(htmlFileName);
            if (htmlFile.exists()) {
                final boolean isDeleted = htmlFile.delete();
                WebServerUtil.logger.log(Level.INFO, "shareAccess File ( " + htmlFile + " ) is Deleted ? : " + isDeleted);
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception while deleting maintanence html file", ex);
        }
    }
    
    public static String getHttpdSslFileName() {
        return WebServerUtil.HTTPD_SSL_CONF_FILE;
    }
    
    public static boolean isEveryOnePermissionAvailableForAllFolder(final String sharePathLocation) {
        try {
            final String sharePath = sharePathLocation.replace("\\", "/");
            if (sharePath.startsWith("//")) {
                final String[] strPathParts = sharePath.split("/");
                String path = "";
                for (int i = 0; i < strPathParts.length; ++i) {
                    path += strPathParts[i];
                    if (i > 2) {
                        WebServerUtil.logger.log(Level.INFO, " EveryOne permission is available in that Location : " + path);
                        final String sid = "*s-1-1-0";
                        final String everyOnePermissionAvailableResult = isPermissionAvailable(path, sid);
                        if (everyOnePermissionAvailableResult != null && everyOnePermissionAvailableResult.startsWith("No files with a matching SID") && !everyOnePermissionAvailableResult.startsWith("SID Found")) {
                            return false;
                        }
                    }
                    path += "/";
                }
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occured while finding Everyone permission available to all folder from the shared location : ", ex);
        }
        return true;
    }
    
    public static String isPermissionAvailable(String sharePath, final String sid) {
        String isPermissionAvailableForSharePath = null;
        final String homeDir = System.getProperty("server.home");
        final String binDir = homeDir + File.separator + "bin";
        final String systemRoot = System.getenv("SystemRoot") + File.separator + "system32";
        final String icaclsEXE = systemRoot + File.separator + "icacls.exe";
        sharePath = sharePath.replace("/", "\\");
        if (new File(icaclsEXE).exists()) {
            WebServerUtil.logger.log(Level.INFO, "icacls.exe exe Availbale in : " + icaclsEXE);
            final File filepath = new File(binDir);
            final List<String> command = new ArrayList<String>();
            BufferedReader in = null;
            try {
                command.add("icacls.exe");
                command.add(sharePath);
                command.add("/findsid");
                command.add(sid);
                final ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.directory(filepath);
                WebServerUtil.logger.log(Level.INFO, "COMMAND : {0}", processBuilder.command());
                final Process process = processBuilder.start();
                in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String outputLine = null;
                boolean flag = false;
                if (in != null) {
                    while ((outputLine = in.readLine()) != null) {
                        if (!flag) {
                            isPermissionAvailableForSharePath = outputLine;
                            flag = true;
                        }
                    }
                }
                WebServerUtil.logger.log(Level.INFO, "Result of permission available : " + isPermissionAvailableForSharePath);
            }
            catch (final Exception ex) {
                WebServerUtil.logger.log(Level.WARNING, "Exception occured while checking permission for that shared Location: ", ex);
            }
        }
        else {
            WebServerUtil.logger.log(Level.WARNING, "icacls.exe not avialble in this machine ");
        }
        return isPermissionAvailableForSharePath;
    }
    
    public static void generateApacheHttpdAndHttpdSSlConfFileWithDefaultLoc(final Properties portProps) {
        generateApacheHttpdAndHttpdSSlConfFileWithDefaultLoc(portProps, null);
    }
    
    public static void generateApacheHttpdAndHttpdSSlConfFileWithDefaultLoc(final Properties portProps, Properties wsProps) {
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final String serverHomeForApache = serverHome.replaceAll("\\\\", "/");
            final String httpdFileName = serverHome + File.separator + WebServerUtil.HTTPD_CONF_FILE;
            final String httpdTemplateFileName = serverHome + File.separator + WebServerUtil.HTTPD_CONF_TEMPLATE_FILE;
            final String httpdSSLFileName = serverHome + File.separator + WebServerUtil.HTTPD_SSL_CONF_FILE;
            final String httpdSSLTemplateFileName = serverHome + File.separator + WebServerUtil.HTTPD_SSL_CONF_TEMPLATE_FILE;
            if (wsProps == null) {
                wsProps = getWebServerSettings();
            }
            wsProps.setProperty("apache.xframe.options.key", getXframeHeader(wsProps));
            wsProps.setProperty("SERVER_HOME", serverHomeForApache);
            includeGzipCompressionFiles(wsProps);
            includeFwsLicenseCheck(wsProps);
            includeOSDHTTPReposForApache(wsProps, serverHome);
            final String httpPortValue = portProps.getProperty("http.port");
            final String httpsPortValue = portProps.getProperty("https.port");
            WebServerUtil.logger.log(Level.INFO, "Port properties are : " + portProps);
            final Boolean isMSP = InstallUtil.isMSP();
            String freePortHttp = null;
            if (httpPortValue != null) {
                int httpPort = Integer.parseInt(httpPortValue);
                if (isMSP) {
                    httpPort += 20;
                }
                freePortHttp = String.valueOf(getFreePort(httpPort));
                wsProps.setProperty("http.port", freePortHttp);
                WebServerUtil.logger.log(Level.INFO, "HTTP PORT : " + freePortHttp);
                WebServerUtil.usedPort = Integer.parseInt(freePortHttp);
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(httpdTemplateFileName, httpdFileName, wsProps, "%");
            }
            if (httpsPortValue != null) {
                String freePort = null;
                if (isMSP) {
                    final int freePortHttps = Integer.parseInt(freePortHttp);
                    freePort = String.valueOf(getFreePort(freePortHttps));
                }
                else {
                    final int httpsPort = Integer.parseInt(httpsPortValue);
                    freePort = String.valueOf(getFreePort(httpsPort));
                }
                wsProps.setProperty("https.port", freePort);
                WebServerUtil.logger.log(Level.INFO, "HTTPS PORT " + freePort);
                includeThirdPartySslCertificates(wsProps, "apache.ssl.root.ca.file", "enable.apache.ssl.root.ca.file");
                includeThirdPartySslCertificates(wsProps, "apache.ssl.intermediate.ca.file", "enable.apache.ssl.intermediate.ca.file");
                com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(httpdSSLTemplateFileName, httpdSSLFileName, wsProps, "%");
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Caught exception in generating Apache Httpd and http ssl Conf Files with Default Location ", ex);
            ex.printStackTrace();
        }
    }
    
    public static String getEXENameFromPort(final int port) {
        WebServerUtil.logger.log(Level.INFO, " Port number : " + port);
        WebServerUtil.logger.log(Level.INFO, " Port number in webserver util: " + port);
        String exeName = null;
        try {
            final String serverHome = getServerHomeCanonicalPath();
            final File filepath = new File(serverHome);
            final List<String> command = new ArrayList<String>();
            BufferedReader in = null;
            command.add("netstat");
            command.add("-anob");
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(filepath);
            final Process process = processBuilder.start();
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputLine = null;
            String[] outputArray = null;
            final String portValue = ":" + port;
            if (in != null) {
                while ((outputLine = in.readLine()) != null) {
                    if (outputLine.contains(portValue)) {
                        outputArray = outputLine.split("\\s+");
                        for (int i = 0; i < outputArray.length; ++i) {
                            if (outputArray[i].length() > 0 && exeName == null && outputArray[2].contains(portValue) && (exeName = in.readLine()) != null) {
                                if (!exeName.contains("[")) {
                                    String temp = null;
                                    if ((temp = in.readLine()) != null) {
                                        exeName = temp;
                                    }
                                }
                                exeName = exeName.trim();
                                WebServerUtil.logger.log(Level.INFO, " Exe name : " + exeName);
                                exeName = exeName.substring(1, exeName.length() - 1);
                                WebServerUtil.logger.log(Level.INFO, " Exe name : " + exeName);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.WARNING, "Exception occurred while  getting exe name from : ", ex);
        }
        return exeName;
    }
    
    public static int getFreePort(final int httpPort) {
        int tempPort;
        for (tempPort = httpPort + 1; tempPort <= httpPort + 20; ++tempPort) {
            final String exeName = getEXENameFromPort(tempPort);
            if (exeName == null) {
                return tempPort;
            }
        }
        return tempPort;
    }
    
    public static String getXframeHeader(final Properties props) {
        String XFrameHeader = "";
        if (props != null && props.containsKey("apache.xframe.options") && props.getProperty("apache.xframe.options").equalsIgnoreCase("enable")) {
            XFrameHeader = "Header always append X-Frame-Options SAMEORIGIN";
        }
        return XFrameHeader;
    }
    
    public static String getSSLProtocol(final Properties wsProps) {
        String sslProtocol = wsProps.getProperty("apache.sslprotocol");
        if (wsProps != null && wsProps.containsKey("IsTLSV2Enabled") && wsProps.getProperty("IsTLSV2Enabled").equalsIgnoreCase("true")) {
            sslProtocol += " -TLSv1 -TLSv1.1";
        }
        return sslProtocol;
    }
    
    public static void setBasicAuth(final Properties wsProps) {
        if (AgentAuthenticationConstants.ENABLE_AUTH_VERIFY) {
            wsProps.setProperty("disable.http.basic.auth", "");
        }
        else {
            wsProps.setProperty("disable.http.basic.auth", "#");
        }
    }
    
    private static void setClientCertAuth(final Properties wsProps) throws Exception {
        final boolean isForceDisableClientCertAuth = wsProps.containsKey("force.disable.client.cert.auth") && wsProps.getProperty("force.disable.client.cert.auth").equals("true");
        if (isForceDisableClientCertAuth) {
            wsProps.setProperty("ssl.verify.client", "off");
            wsProps.setProperty("disable.client.cert.auth.nginx", "");
            wsProps.setProperty("disable.client.cert.auth.apache", "#");
            wsProps.setProperty("client.cert.auth.enabled", "false");
            wsProps.setProperty("disable.client.cert.auth.in.http", "");
        }
        else {
            wsProps.setProperty("ssl.verify.client", "optional_no_ca");
            wsProps.setProperty("disable.client.cert.auth.nginx", "#");
            final boolean isEnableClientCertAuth = wsProps.containsKey("client.cert.auth.enabled") && wsProps.getProperty("client.cert.auth.enabled").equals("true");
            if (isEnableClientCertAuth) {
                wsProps.setProperty("disable.client.cert.auth.in.http", "#");
                wsProps.setProperty("client.cert.auth.level", "SUCCESS$");
                wsProps.setProperty("disable.client.cert.auth.apache", "");
            }
            else {
                wsProps.setProperty("disable.client.cert.auth.in.http", "");
                wsProps.setProperty("client.cert.auth.level", "SUCCESS$|NONE$");
                wsProps.setProperty("disable.client.cert.auth.apache", "#");
            }
        }
        updateWebServerProps(wsProps);
    }
    
    private static void setInsecureCommunicationProps(final Properties wsProps) {
        if (wsProps != null && wsProps.containsKey("enforce.https.communication") && Boolean.parseBoolean(wsProps.getProperty("enforce.https.communication"))) {
            wsProps.setProperty("insecure.comm.ui", "#");
            wsProps.setProperty("insecure.comm.static", "");
            if (!wsProps.containsKey("unbind.http.port")) {
                wsProps.setProperty("unbind.http.port", "");
            }
        }
        else {
            wsProps.setProperty("insecure.comm.ui", "");
            wsProps.setProperty("insecure.comm.static", "#");
            wsProps.setProperty("unbind.http.port", "");
        }
    }
    
    public static boolean startWebServer(final String serverHome) throws Exception {
        if (getWebServerName().equalsIgnoreCase("apache")) {
            final String startResult = apacheHttpdInvoke(serverHome, "start");
            WebServerUtil.logger.log(Level.INFO, "Result of start Apache Server " + startResult);
            Thread.sleep(15000L);
            if (isApacheServiceRunning()) {
                return true;
            }
        }
        else if (getWebServerName().equalsIgnoreCase("nginx")) {
            final String startResult = NginxServerUtils.startNginxServer(serverHome);
            WebServerUtil.logger.log(Level.INFO, "Result of start Nginx Server " + startResult);
            if ("Success".equalsIgnoreCase(startResult)) {
                return true;
            }
        }
        return false;
    }
    
    public static String installVCRedistributable(final String serverHome) throws Exception {
        String result = "";
        String cmd = "";
        WebServerUtil.logger.log(Level.INFO, "invoke install vcredist.exe ");
        try {
            final String vcredistExe = "\"" + serverHome + File.separator + WebServerUtil.VCREDIST_EXE + "\"";
            cmd = vcredistExe + "  /install" + " /passive " + " /norestart" + " /q ";
            WebServerUtil.logger.log(Level.INFO, "Httpd command to be executed: " + cmd);
            final ProcessBuilder p = new ProcessBuilder(new String[] { vcredistExe, " /install /passive  /norestart /q " });
            final Process proc = p.start();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                result = result + str + "\n";
            }
        }
        catch (final Exception ex) {
            WebServerUtil.logger.log(Level.INFO, "Caught error while executing the command: " + cmd + " exception: " + ex);
            throw ex;
        }
        return result;
    }
    
    public static void startApacheInDifferentPort(final String serverHome) {
        startApacheInDifferentPort(serverHome, null);
    }
    
    public static void startApacheInDifferentPort(final String serverHome, final Properties wsProps) {
        try {
            final Properties webServerProps = getWebServerSettings();
            final String httpPort = webServerProps.getProperty("http.port");
            final String httpsPort = webServerProps.getProperty("https.port");
            final Properties portProps = new Properties();
            portProps.setProperty("http.port", httpPort);
            portProps.setProperty("https.port", httpsPort);
            generateApacheHttpdAndHttpdSSlConfFileWithDefaultLoc(portProps, wsProps);
            final String stopResultofApache = apacheHttpdInvoke(serverHome, "stop");
            WebServerUtil.logger.log(Level.INFO, "startStandAloneApache :: Stop Apache Service: " + stopResultofApache);
            final String startResultOfApacheWithDeaultPort = apacheHttpdInvoke(serverHome, "start");
            WebServerUtil.logger.log(Level.INFO, "startStandAloneApache :: Start Apache Service: " + startResultOfApacheWithDeaultPort);
            WebServerUtil.logger.log(Level.INFO, " Going to delete ws.modtime file ");
            final String wsModifiedTime = serverHome + File.separator + "conf" + File.separator + "ws.modtime";
            final File wsModifiedTimeFile = new File(wsModifiedTime);
            if (wsModifiedTimeFile.exists()) {
                WebServerUtil.logger.log(Level.INFO, "ws.modtime file deleted status : " + wsModifiedTimeFile.delete());
            }
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "Exception while starting apache in different port", e);
        }
    }
    
    public static void stopWebServersForcefully() throws Exception {
        final String serverHome = System.getProperty("server.home");
        WebServerUtil.logger.log(Level.INFO, " *** Going to stop the web servers forcefully! *** ");
        Boolean status = NginxServerUtils.nginxForceStop(serverHome);
        WebServerUtil.logger.log(Level.INFO, "Force Stopping Status for Nginx Web Server " + status);
        status = apacheForceStop();
        WebServerUtil.logger.log(Level.INFO, "Force Stopping Status for Apache Web Server " + status);
    }
    
    public static Boolean apacheForceStop() throws IOException {
        final String serverHome = System.getProperty("server.home");
        final String apachePath = new File(serverHome + File.separator + WebServerUtil.HTTPD_EXE).getCanonicalPath();
        if (isTaskRunning("dcserverhttpd.exe", apachePath, serverHome)) {
            return forceStop("dcserverhttpd.exe", apachePath, serverHome);
        }
        WebServerUtil.logger.log(Level.INFO, "apacheForceStop() Apache Web Server Already Stopped!");
        return Boolean.TRUE;
    }
    
    public static Boolean forceStop(final String processName, final String processPath, final String serverHome) {
        final String retryCount = "3";
        String result = "";
        int exitValue = -1;
        ProcessBuilder builder = null;
        Process process = null;
        BufferedReader reader = null;
        try {
            final String dcWinUtilPath = new File(serverHome + File.separator + WebServerUtil.DCWINUTIL_PATH).getCanonicalPath();
            WebServerUtil.logger.log(Level.SEVERE, "Starting the forceStop()");
            builder = new ProcessBuilder(new String[] { dcWinUtilPath, "-kill", processName, "3", processPath });
            builder.environment().put("OPENSSL_CONF", NginxServerUtils.getNginxConfDirPath());
            process = builder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String buffer;
            while ((buffer = reader.readLine()) != null) {
                result = result + buffer + "\n";
            }
            exitValue = process.waitFor();
            if (reader != null) {
                reader.close();
            }
            process.destroy();
            WebServerUtil.logger.log(Level.INFO, "Web Server Force Stopping Process Exit value : {0} for the {1} Process Path", new Object[] { exitValue, processPath });
            WebServerUtil.logger.log(Level.SEVERE, "Web Server Force Stopping Process Result : {0}", new Object[] { result });
            builder = new ProcessBuilder(new String[] { dcWinUtilPath, "-exists", processName, processPath });
            process = builder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            result = "";
            while ((buffer = reader.readLine()) != null) {
                result = result + buffer + "\n";
            }
            exitValue = process.waitFor();
            result = result.trim();
            WebServerUtil.logger.log(Level.INFO, "Web Server Force Stop Result Process Exit value : {0}", new Object[] { exitValue });
            WebServerUtil.logger.log(Level.INFO, "Killing Process Running Status : {0}", new Object[] { result });
            result = result.replace(processName + " : ", "");
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "forceStop() Exception while stopping the web server forcefully! ", e);
            try {
                if (reader != null) {
                    reader.close();
                }
                process.destroy();
            }
            catch (final Exception e) {
                WebServerUtil.logger.log(Level.WARNING, "Exception while closing BufferedReader object! ", e);
            }
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                process.destroy();
            }
            catch (final Exception e2) {
                WebServerUtil.logger.log(Level.WARNING, "Exception while closing BufferedReader object! ", e2);
            }
        }
        return result.equalsIgnoreCase("false");
    }
    
    public static Boolean isTaskRunning(final String processName, final String processPath, final String serverHome) {
        String result = "";
        int exitValue = -1;
        ProcessBuilder builder = null;
        Process process = null;
        BufferedReader reader = null;
        try {
            final String dcWinUtilPath = new File(serverHome + File.separator + WebServerUtil.DCWINUTIL_PATH).getCanonicalPath();
            WebServerUtil.logger.log(Level.SEVERE, "Starting check isTaskRunning()");
            builder = new ProcessBuilder(new String[] { dcWinUtilPath, "-exists", processName, processPath });
            process = builder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            result = "";
            String buffer;
            while ((buffer = reader.readLine()) != null) {
                result = result + buffer + "\n";
            }
            exitValue = process.waitFor();
            result = result.trim();
            WebServerUtil.logger.log(Level.INFO, "Web Server Force Stop Result Process Exit value : {0}", new Object[] { exitValue });
            WebServerUtil.logger.log(Level.INFO, "Killing Process Running Status : {0}", new Object[] { result });
            result = result.replace(processName + " : ", "");
        }
        catch (final Exception e) {
            WebServerUtil.logger.log(Level.WARNING, "forceStop() Exception while stopping the web server forcefully! ", e);
            try {
                if (reader != null) {
                    reader.close();
                }
                process.destroy();
            }
            catch (final Exception e) {
                WebServerUtil.logger.log(Level.WARNING, "Exception while closing BufferedReader object! ", e);
            }
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                process.destroy();
            }
            catch (final Exception e2) {
                WebServerUtil.logger.log(Level.WARNING, "Exception while closing BufferedReader object! ", e2);
            }
        }
        return !result.equalsIgnoreCase("false");
    }
    
    private static byte[] getFileAsByteArray(final String filePath) {
        byte[] fileByte = null;
        try {
            final InputStream fileInput = new BufferedInputStream(new FileInputStream(filePath));
            fileByte = new byte[fileInput.available()];
            fileInput.read(fileByte);
        }
        catch (final FileNotFoundException ex) {
            WebServerUtil.logger.log(Level.WARNING, "FileNotFoundException while getFileAsInputStream", ex);
        }
        catch (final IOException ex2) {
            WebServerUtil.logger.log(Level.WARNING, "IOException while getFileAsInputStream", ex2);
        }
        return fileByte;
    }
    
    static {
        WEB_SETTINGS_CONF_FILE = "conf" + File.separator + "websettings.conf";
        WS_SETTINGS_CONF_FILE = "conf" + File.separator + "wsSettings.conf";
        HTPASSWD_FILE = "conf" + File.separator + "Tomcat" + File.separator + "Agent.key";
        HTPASSWD_MODTIME_PROPS_FILE = "conf" + File.separator + "Tomcat" + File.separator + "htpasswd.modtime";
        WRAPPER_CONF_FILE = "conf" + File.separator + "wrapper-user.conf";
        HTTPD_EXE = "apache" + File.separator + "bin" + File.separator + "dcserverhttpd.exe";
        VCREDIST_EXE = "apache" + File.separator + "dependencyfiles" + File.separator + "vc_redist.x64.exe";
        HTTPD_CONF_FILE = "apache" + File.separator + "conf" + File.separator + "httpd.conf";
        HTTPD_CONF_TEMPLATE_FILE = "apache" + File.separator + "conf" + File.separator + "httpd.conf.template";
        HTTPD_CONF_OSD_ALIAS_FILE = "apache" + File.separator + "conf" + File.separator + "httpd.conf.osdalias";
        HTTPD_CONF_OSD_DIRECTORIES_FILE = "apache" + File.separator + "conf" + File.separator + "httpd.conf.osddirectories";
        NGINX_CONF_OSD_LOCATIONS_FILE = "nginx" + File.separator + "conf" + File.separator + "nginx.conf.osdlocations";
        HTTPD_SSL_CONF_FILE = "apache" + File.separator + "conf" + File.separator + "httpd-ssl.conf";
        HTTPD_SSL_CONF_TEMPLATE_FILE = "apache" + File.separator + "conf" + File.separator + "httpd-ssl.conf.template";
        WORKERS_PROPERTIES_FILE = "apache" + File.separator + "conf" + File.separator + "tomcat" + File.separator + "workers.properties";
        WORKERS_PROPERTIES_TEMPLATE_FILE = "apache" + File.separator + "conf" + File.separator + "tomcat" + File.separator + "workers.properties.template";
        WebServerUtil.apacheLogFilesCount = 5;
        WebServerUtil.apachePingInterval = 120;
        SERVER_CONF_FILE = "conf" + File.separator + "server.xml";
        SERVER_CONF_TEMPLATE_FILE = "conf" + File.separator + "server.xml.template";
        SERVER_CONF_APACHE_TEMPLATE_FILE = "conf" + File.separator + "server.xml.apache.template";
        SHARE_ACCESS_STATUS_FILE = "conf" + File.separator + "share_access.properties";
        SWREPOSITROY_DEFAULT_LOCATION = "webapps" + File.separator + "DesktopCentral" + File.separator + "swrepository";
        PATCH_STORE_DEFAULT_LOCATION = "webapps" + File.separator + "DesktopCentral" + File.separator + "store";
        MAINTENANCE_MODE_TEMPLATE_FILE = "webapps" + File.separator + "DesktopCentral" + File.separator + "html" + File.separator + "maintenance_mode_template.html";
        MAINTENANCE_MODE_FILE = "webapps" + File.separator + "DesktopCentral" + File.separator + "html" + File.separator + "maintenance_mode.html";
        WEBSERVER_CONFIGURATIONS_FILE = "conf" + File.separator + "User-conf" + File.separator + "webserver_configurations.json";
        WebServerUtil.sourceClass = "WebServerUtil";
        DCWINUTIL_PATH = "bin" + File.separator + "dcwinutil.exe";
        WebServerUtil.server_ip_props = null;
        WebServerUtil.logger = Logger.getLogger(WebServerUtil.class.getName());
        WebServerUtil.webServerName = "";
        WebServerUtil.usedPort = 80;
        WebServerUtil.productArch = null;
        WebServerUtil.postgresArch = null;
        WebServerUtil.apacheArch = null;
        WebServerUtil.machineName = null;
        WebServerUtil.serverProtocol = null;
        WebServerUtil.webServerConfigurations = new Properties();
        NGINX_CLIENT_BODY_TIMEOUT_MIN_VALUE = 10;
        NGINX_CLIENT_BODY_TIMEOUT_MAX_VALUE = 60;
        NGINX_KEEPALIVE_TIMEOUT_MIN_VALUE = 55;
        NGINX_KEEPALIVE_TIMEOUT_MAX_VALUE = 65;
        APACHE_REQUESTREADTIMEOUT_BODY_MIN_VALUE = 20;
        APACHE_LIMITREQUEST_LINE_MIN_VALUE = 512;
        APACHE_LIMITREQUEST_LINE_MAX_VALUE = 8192;
        APACHE_LIMITREQUESTFIELDSIZE_MIN_VALUE = 1024;
        APACHE_LIMITREQUESTFIELDSIZE_MAX_VALUE = 8192;
        NGINX_LARGE_CLIENT_HEADER_BUFFERS_MIN_VALUE = 1;
        NGINX_LARGE_CLIENT_HEADER_BUFFERS_MAX_VALUE = 8192;
    }
}
