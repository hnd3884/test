package com.zoho.security.agent;

import java.util.ArrayList;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import com.adventnet.iam.security.SecurityUtil;
import java.util.Iterator;
import org.json.JSONObject;
import com.zoho.security.agent.notification.DefaultNotificationReceiver;
import java.util.HashMap;
import com.adventnet.iam.security.IAMSecurityException;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_APPSENSE_NOTIFICATION;
import com.adventnet.iam.security.SecurityRequestWrapper;
import com.adventnet.iam.security.SecurityFilterProperties;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.AbstractMap;
import com.adventnet.iam.security.SecurityFilter;
import java.util.logging.Logger;
import java.io.File;
import java.util.List;
import com.adventnet.iam.security.SecurityXML;
import java.util.Map;
import org.json.JSONArray;
import java.util.Properties;

public class AppSenseAgent
{
    public static Properties wafProperties;
    private static JSONArray fileHashArray;
    private static Map<String, SecurityXML> xmlFilesAsObject;
    private static List<File> securityFiles;
    private static String milestoneVersion;
    private static String cacertHash;
    public static final String MD_5 = "MD5";
    public static volatile boolean enableCSPReport;
    public static volatile String cspReportURI;
    public static volatile boolean enableReqInfoFileHash;
    public static volatile String reqInfoFileHashAlgorithm;
    public static boolean enableSecurityXMLPush;
    public static boolean enableCACertPush;
    public static boolean milestoneVersionPush;
    private static List<String> defaultconfigFiles;
    public static final Logger LOGGER;
    
    public static void init() {
        initMileStoneVersion();
        registerService();
    }
    
    private static void initMileStoneVersion() {
        AppSenseAgent.milestoneVersion = SecurityFilter.class.getPackage().getImplementationVersion();
    }
    
    private static void registerService() {
        final AbstractMap.SimpleEntry<Boolean, String> tmpFileExist = LocalConfigurations.isTmpFileExists();
        if (tmpFileExist.getKey()) {
            ServiceRegistration.notifyAgentError("TMP_FILE_EXISTS", tmpFileExist.getValue());
            ServiceRegistration.setRegistrationStatus(true);
        }
        else if (LocalConfigurations.isExists()) {
            ServiceRegistration.setRegistrationStatus(true);
        }
        else {
            ServiceRegistration.register();
        }
    }
    
    public static boolean isRegisteredInAppSense() {
        return ServiceRegistration.isRegisteredInAppSense();
    }
    
    public static void handleAppConfigurationsOnServerRestart() {
        final AbstractMap.SimpleEntry<Boolean, Boolean> isAppfirewallLoaded = LocalConfigurations.loadAppFireWallConfigurations();
        final boolean isConfigLoaded = LocalConfigurations.loadConfigAndInventory();
        final boolean isWafInstrumnetInfoLoaded = LocalConfigurations.loadWAFAttackDiscoveryInfos();
        LocalConfigurations.notifyServerOnModifiedConfigurations();
        if (isWafInstrumnetInfoLoaded) {
            AppSenseAgent.LOGGER.log(Level.SEVERE, "WAF instrumentation info loaded successfully");
        }
        if (isAppfirewallLoaded.getKey() || isConfigLoaded) {
            AppSenseAgent.LOGGER.log(Level.SEVERE, "Appfirewall & Inventory file loaded successfully . appfirewall : {0}, Inventory : {1} ", new Object[] { isAppfirewallLoaded.getKey(), isConfigLoaded });
        }
        if (isAppfirewallLoaded.getValue()) {
            AppSenseAgent.LOGGER.log(Level.SEVERE, "Exception ocurred while loading the appfirewall   ");
            ServiceRegistration.notifyAgentError("APPFIREWALL_NOT_LOADED", " Exception occurred while loading appfirewall error");
        }
    }
    
    public static boolean handleAppSenseNotification(final HttpServletRequest request) {
        String serviceNameFromParam = null;
        final String actualServiceName = SecurityFilterProperties.getServiceName();
        try {
            final JSONArray dataParam = ((SecurityRequestWrapper)request).getJSONArrayParameter("data");
            serviceNameFromParam = request.getParameter("service");
            if (!serviceNameFromParam.equalsIgnoreCase(actualServiceName)) {
                ZSEC_APPSENSE_NOTIFICATION.pushServicenameMistmatch("SERVICE_NAME_MISMATCH", actualServiceName, serviceNameFromParam, (ExecutionTimer)null);
                throw new IAMSecurityException("UNAUTHORISED");
            }
            if (dataParam.length() > 0) {
                final Map<Components.COMPONENT, JSONArray> notifiedObject = new HashMap<Components.COMPONENT, JSONArray>();
                for (int i = 0; i < dataParam.length(); ++i) {
                    final JSONObject jo = dataParam.getJSONObject(i);
                    final Components.COMPONENT component = Components.COMPONENT.valueOf(jo.getString("COMPONENT").toUpperCase());
                    if (component != null) {
                        if (!notifiedObject.containsKey(component)) {
                            notifiedObject.put(component, new JSONArray());
                        }
                        notifiedObject.get(component).put((Object)jo);
                    }
                }
                for (final Map.Entry<Components.COMPONENT, JSONArray> nobj : notifiedObject.entrySet()) {
                    final Components.COMPONENT component = nobj.getKey();
                    final JSONArray subcomponentArray = nobj.getValue();
                    for (int j = 0; j < subcomponentArray.length(); ++j) {
                        final JSONObject subcomponentObj = subcomponentArray.getJSONObject(j);
                        final String subcomponentStr = subcomponentObj.getString("NAME").toLowerCase();
                        final Components.COMPONENT_NAME subComponent = Components.COMPONENT_NAME.getEnumByProperty(subcomponentStr);
                        if (subComponent != null) {
                            DefaultNotificationReceiver.getInstance(component).receive(component, subComponent, subcomponentObj);
                            ZSEC_APPSENSE_NOTIFICATION.pushSuccess(component.name(), subcomponentObj.toString(), (ExecutionTimer)null);
                        }
                        else {
                            ZSEC_APPSENSE_NOTIFICATION.pushExceptionWithComponent(subcomponentStr, "COMPONENT NOT SUPPORTED IN AGENT", (ExecutionTimer)null);
                        }
                    }
                    if (nobj.getKey() != Components.COMPONENT.HASH) {
                        LocalConfigurations.saveToFile(component.name());
                    }
                }
                return true;
            }
        }
        catch (final Exception e) {
            ZSEC_APPSENSE_NOTIFICATION.pushException(e.getMessage(), (ExecutionTimer)null);
        }
        return false;
    }
    
    public static String getFireWallRulesLoaderURL() {
        return ServiceRegistration.getAppSenseAppFireWallRulesLoaderURL();
    }
    
    public static void setxmlFilesAsObject(final File file) {
        final SecurityXML xml = new SecurityXML(file.getName(), file);
        AppSenseAgent.xmlFilesAsObject.put(xml.getHash(), xml);
        AppSenseAgent.fileHashArray.put((Object)xml.toJSONObject());
    }
    
    public static JSONArray getFileHashArray() {
        if (AppSenseAgent.fileHashArray.length() == 0) {
            generateXMLHash();
        }
        return AppSenseAgent.fileHashArray;
    }
    
    public static Map<String, SecurityXML> getXmlFilesAsObject() {
        return AppSenseAgent.xmlFilesAsObject;
    }
    
    public static String getMilestoneVersion() {
        return AppSenseAgent.milestoneVersion;
    }
    
    public static String getFileHashAlgorithm() {
        if (AppSenseAgent.reqInfoFileHashAlgorithm != null) {
            return AppSenseAgent.reqInfoFileHashAlgorithm;
        }
        return "MD5";
    }
    
    public static String getCACertsHash() {
        if (AppSenseAgent.cacertHash == null) {
            final ExecutionTimer timer = ExecutionTimer.startInstance();
            final String cacertFilePath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts";
            final File cacertFile = new File(cacertFilePath);
            final SecurityXML xml = new SecurityXML(cacertFile.getName(), cacertFile);
            AppSenseAgent.cacertHash = xml.getHash();
            AppSenseAgent.LOGGER.log(Level.WARNING, "CACert , File Hash Array Time taken   : {0}  ", new Object[] { timer.getExecutionTime() });
        }
        return AppSenseAgent.cacertHash;
    }
    
    public static boolean isEnableSecurityXMLPush() {
        return AppSenseAgent.enableSecurityXMLPush;
    }
    
    public static void setEnableSecurityXMLPush(final boolean value) {
        AppSenseAgent.enableSecurityXMLPush = value;
    }
    
    public static boolean isMilestoneVersionPush() {
        return AppSenseAgent.milestoneVersionPush;
    }
    
    public static void setMilestoneVersionPush(final boolean value) {
        AppSenseAgent.milestoneVersionPush = value;
    }
    
    public static boolean isEnableCACertPush() {
        return AppSenseAgent.enableCACertPush;
    }
    
    public static void setEnableCACertPush(final boolean value) {
        AppSenseAgent.enableCACertPush = value;
    }
    
    public static boolean isEnableCSPReport() {
        return AppSenseAgent.enableCSPReport;
    }
    
    public static void setEnableCSPReport(final boolean val) {
        AppSenseAgent.enableCSPReport = val;
    }
    
    public static void setCSPReportURI(final String value) {
        AppSenseAgent.cspReportURI = value;
    }
    
    public static String getCSPReportURI() {
        return AppSenseAgent.cspReportURI;
    }
    
    public static boolean isEnableReqInfoFileHash() {
        return AppSenseAgent.enableReqInfoFileHash;
    }
    
    public static void setEnableReqInfoFileHash(final boolean value) {
        AppSenseAgent.enableReqInfoFileHash = value;
    }
    
    public static String getReqInfoFileHashAlgorithm() {
        return AppSenseAgent.reqInfoFileHashAlgorithm;
    }
    
    public static void setRequestInfoFileHashAlgorithm(final String value) {
        AppSenseAgent.reqInfoFileHashAlgorithm = value;
    }
    
    public static void notifyConfigChange(final JSONArray modifiedData) {
        final String service = SecurityFilterProperties.getServiceName();
        final String urlString = AppSenseConstants.getNotificationURL();
        try {
            String postParams = "iscsignature=" + SecurityUtil.sign() + "&service=" + service;
            postParams = postParams + "&data=" + modifiedData;
            final int statusCode = SecurityFrameworkUtil.getURLConnection(urlString, postParams, "POST").getResponseCode();
            if (statusCode == 200) {
                ZSEC_APPSENSE_NOTIFICATION.pushSuccess(urlString, modifiedData.toString(), (ExecutionTimer)null);
            }
            else {
                ZSEC_APPSENSE_NOTIFICATION.pushExceptionWithComponent(Integer.toString(statusCode), "INVALID STATUS CODE", (ExecutionTimer)null);
            }
        }
        catch (final Exception e) {
            ZSEC_APPSENSE_NOTIFICATION.pushExceptionWithComponent(urlString, e.getMessage(), (ExecutionTimer)null);
        }
    }
    
    public static void setSecurityFiles(final List<File> secFile, final List<String> configFile) {
        if (AppSenseAgent.defaultconfigFiles == null) {
            AppSenseAgent.defaultconfigFiles = configFile;
        }
        if (AppSenseAgent.securityFiles.size() == 0) {
            AppSenseAgent.securityFiles = secFile;
        }
    }
    
    public static void generateXMLHash() {
        if (AppSenseAgent.securityFiles.size() > 0) {
            if (isEnableSecurityXMLPush() && AppSenseAgent.fileHashArray.length() == 0) {
                final ExecutionTimer timer = ExecutionTimer.startInstance();
                for (final File file : AppSenseAgent.securityFiles) {
                    final String fileName = file.getName();
                    if (!AppSenseAgent.defaultconfigFiles.contains(fileName)) {
                        setxmlFilesAsObject(file);
                    }
                }
                AppSenseAgent.LOGGER.log(Level.WARNING, "Security xml , File Hash Array Time taken   : {0} , No of Files :  {1} ", new Object[] { timer.getExecutionTime(), AppSenseAgent.securityFiles.size() });
            }
        }
        else {
            AppSenseAgent.LOGGER.log(Level.WARNING, "Unable generate security xml file hash array , No files found ");
        }
    }
    
    public static void setProperty(final Components.COMPONENT_NAME component_name, final String value) {
        SecurityFilterProperties.setProperty(component_name, value);
    }
    
    public static void clearPropertyFromWAFProperties(final String value) {
        if (AppSenseAgent.wafProperties.containsKey(value)) {
            AppSenseAgent.wafProperties.remove(value);
        }
    }
    
    static {
        AppSenseAgent.wafProperties = new Properties();
        AppSenseAgent.fileHashArray = new JSONArray();
        AppSenseAgent.xmlFilesAsObject = new HashMap<String, SecurityXML>();
        AppSenseAgent.securityFiles = new ArrayList<File>();
        AppSenseAgent.enableCSPReport = true;
        AppSenseAgent.cspReportURI = "https://logsapi.localzoho.com/csplog";
        AppSenseAgent.enableReqInfoFileHash = true;
        AppSenseAgent.reqInfoFileHashAlgorithm = null;
        AppSenseAgent.enableSecurityXMLPush = false;
        AppSenseAgent.enableCACertPush = false;
        AppSenseAgent.milestoneVersionPush = true;
        AppSenseAgent.defaultconfigFiles = null;
        LOGGER = Logger.getLogger(AppSenseAgent.class.getName());
    }
}
