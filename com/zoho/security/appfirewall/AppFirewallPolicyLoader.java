package com.zoho.security.appfirewall;

import java.util.Arrays;
import com.adventnet.iam.security.SecurityResponseWrapper;
import javax.xml.validation.Validator;
import javax.xml.validation.Schema;
import javax.xml.transform.Source;
import javax.xml.validation.SchemaFactory;
import javax.xml.transform.stream.StreamSource;
import com.zoho.security.agent.AppSenseAgent;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Document;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.nio.file.Path;
import java.util.logging.Level;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Paths;
import java.io.File;
import java.net.HttpURLConnection;
import org.json.JSONException;
import java.net.URLEncoder;
import org.json.JSONObject;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_AFW_RULE_FETCH;
import org.json.JSONArray;
import com.adventnet.iam.security.SecurityUtil;
import com.zoho.security.agent.LocalConfigurations;
import java.util.AbstractMap;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import com.adventnet.iam.security.SecurityFilterProperties;
import java.util.List;
import java.util.logging.Logger;

public class AppFirewallPolicyLoader
{
    private static final Logger LOGGER;
    private static final String APPFIREWALLPOLICYFILE = "appfirewall-policy.xml";
    private static final String APPFIREWALLSCHEMAFILE = "appfirewall-schema.xsd";
    public static final String IS_REQUEST_FIREWALL_ENABLED = "enable.app.firewall";
    private static final String APP_FIREWALL_POLICY = "app.firewall.policy";
    private static final String APP_FIREWALL_SCHEMA = "app.firewall.schema";
    public static final List<String> REQUEST_FIREWALL_PATTERN_NAMES_LIST;
    public static boolean isReqFirewallEnabled;
    public static boolean isAppFirewallPolicyInitialized;
    private static boolean isFileBasedLoadingEnabled;
    private static long firewallPolicyLoadTime;
    private static long firewallSchemaLoadtime;
    static final long FIREWALL_POLICY_RELOADING_COUNTER = 900000L;
    
    private static synchronized void initializeRequestFirewallRules(final SecurityFilterProperties sFConfig, final FirewallStage stage) {
        if (!AppFirewallPolicyLoader.isAppFirewallPolicyInitialized && stage == FirewallStage.PRE_STAGE) {
            AppFirewallPolicyLoader.isFileBasedLoadingEnabled = SecurityFrameworkUtil.isFileBasedLoadingEnabled(sFConfig);
            if (AppFirewallPolicyLoader.isFileBasedLoadingEnabled) {
                if (AppFirewallPolicyLoader.isReqFirewallEnabled) {
                    loadAppFireWallPolicyFromFile();
                }
            }
            else {
                loadAppFirewallPolicyFromIAM(sFConfig);
            }
        }
    }
    
    public static AbstractMap.SimpleEntry<Boolean, Boolean> loadAFWPolicyFromAppSenseLocalFile() {
        try {
            final String jsonStr = SecurityFrameworkUtil.readFile(LocalConfigurations.getAppFirewallFileName());
            if (SecurityUtil.isValid((Object)jsonStr)) {
                final JSONArray jsonArr = new JSONArray(jsonStr);
                AppFirewallInitializer.initializeAppFirewallRule(jsonArr);
                final int noOfRules = AppFirewallInitializer.getNoOfRules();
                ZSEC_AFW_RULE_FETCH.pushRuleFetchSuccess(noOfRules, "LOCALFILE", (ExecutionTimer)null);
                AppFirewallPolicyLoader.isAppFirewallPolicyInitialized = true;
                return new AbstractMap.SimpleEntry<Boolean, Boolean>(true, false);
            }
        }
        catch (final Exception e) {
            ZSEC_AFW_RULE_FETCH.pushException("LOCALFILE", e.getMessage(), (ExecutionTimer)null);
        }
        catch (final Throwable e2) {
            ZSEC_AFW_RULE_FETCH.pushException("LOCALFILE", e2.getMessage(), (ExecutionTimer)null);
        }
        return new AbstractMap.SimpleEntry<Boolean, Boolean>(false, true);
    }
    
    public static JSONObject getAppFireWallJSONObject(final String urlString, final String service, final JSONArray ruleAutoID) throws Exception {
        try {
            String rId = "";
            if (ruleAutoID != null) {
                rId = "&ruleautoids=" + URLEncoder.encode(ruleAutoID.toString(), "UTF-8");
            }
            final HttpURLConnection conn = SecurityFrameworkUtil.getURLConnection(urlString + "?iscsignature=" + SecurityUtil.sign() + "&service=" + service + rId, (String)null, "GET");
            final int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                final String jsonString = SecurityUtil.convertInputStreamAsString(conn.getInputStream(), -1L);
                return new JSONObject(jsonString);
            }
            ZSEC_AFW_RULE_FETCH.pushRuleFetchError(responseCode, "INVALID_STATUS_CODE", (ExecutionTimer)null);
        }
        catch (final JSONException e) {
            ZSEC_AFW_RULE_FETCH.pushException("INVALID_STRUCTURE", e.getMessage(), (ExecutionTimer)null);
        }
        return null;
    }
    
    private static void loadAppFireWallPolicyFromFile() {
        boolean modificationDetected = false;
        final Path firewallPolicyPath = Paths.get(SecurityFrameworkUtil.getSecurityConfigurationDirectory() + File.separator + "appfirewall-policy.xml", new String[0]);
        final Path firewallSchemaPath = Paths.get(SecurityFrameworkUtil.getSecurityConfigurationDirectory() + File.separator + "appfirewall-schema.xsd", new String[0]);
        try {
            final BasicFileAttributes policyAttributes = Files.readAttributes(firewallPolicyPath, BasicFileAttributes.class, new LinkOption[0]);
            final BasicFileAttributes schemaAttributes = Files.readAttributes(firewallSchemaPath, BasicFileAttributes.class, new LinkOption[0]);
            if (policyAttributes.size() > 0L && schemaAttributes.size() > 0L) {
                final long lastModifiedStatus_firewallPolicy = policyAttributes.lastModifiedTime().toMillis();
                final long lastModifiedStatus_firewallSchema = schemaAttributes.lastModifiedTime().toMillis();
                if (lastModifiedStatus_firewallPolicy > AppFirewallPolicyLoader.firewallPolicyLoadTime) {
                    AppFirewallPolicyLoader.firewallPolicyLoadTime = lastModifiedStatus_firewallPolicy;
                    modificationDetected = true;
                }
                if (lastModifiedStatus_firewallSchema > AppFirewallPolicyLoader.firewallSchemaLoadtime) {
                    AppFirewallPolicyLoader.firewallSchemaLoadtime = lastModifiedStatus_firewallSchema;
                    modificationDetected = true;
                }
                if (modificationDetected) {
                    loadFirewallRules(new String(Files.readAllBytes(firewallPolicyPath)), new String(Files.readAllBytes(firewallSchemaPath)));
                }
            }
            else {
                AppFirewallPolicyLoader.LOGGER.log(Level.SEVERE, "APPFIREWALL'S POLICY/SCHEMA IS NOT_DEFINED OR IS EMPTY - MAKE SURE FILE PRESENT OR NOT");
            }
        }
        catch (final Exception ex) {
            AppFirewallPolicyLoader.LOGGER.log(Level.SEVERE, "APPFIREWALL_RESOURCE_NOT_FOUND", ex);
        }
    }
    
    private static void loadAppFirewallPolicyFromIAM(final SecurityFilterProperties sFConfig) {
        Map<String, String> systemConfigProps = null;
        if (SecurityFrameworkUtil.isAuthenticationProviderConfigured(sFConfig)) {
            systemConfigProps = SecurityFrameworkUtil.getSystemConfigPropFromIAM(sFConfig);
        }
        AppFirewallPolicyLoader.isReqFirewallEnabled = (systemConfigProps != null && systemConfigProps.get("enable.app.firewall") != null && "true".equalsIgnoreCase(systemConfigProps.get("enable.app.firewall")));
        if (AppFirewallPolicyLoader.isReqFirewallEnabled) {
            try {
                loadFirewallRules(systemConfigProps.get("app.firewall.policy"), systemConfigProps.get("app.firewall.schema"));
            }
            catch (final Exception e) {
                AppFirewallPolicyLoader.LOGGER.log(Level.SEVERE, "APPFIREWALL_RESOURCE_NOT_FOUND  WHILE LOADING FROM IAM", e);
                AppFirewallPolicyLoader.isAppFirewallPolicyInitialized = false;
            }
        }
        else {
            AppFirewallPolicyLoader.isAppFirewallPolicyInitialized = true;
        }
    }
    
    private static synchronized void loadFirewallRules(final String firewallPolicyContent, final String firewallSchemaContent) throws AppFirewallException, JSONException {
        if (SecurityFrameworkUtil.isValid(firewallPolicyContent) && SecurityFrameworkUtil.isValid(firewallSchemaContent)) {
            try {
                final byte[] firewallPolicyContentBytes = firewallPolicyContent.getBytes();
                isValidAppFirewallRule(new ByteArrayInputStream(firewallPolicyContentBytes), new ByteArrayInputStream(firewallSchemaContent.getBytes()));
                final Document document = parseAppFirewallRule(new ByteArrayInputStream(firewallPolicyContentBytes));
                AppFirewallInitializer.initializeAppFirewallRule(document);
                AppFirewallPolicyLoader.isAppFirewallPolicyInitialized = true;
                ZSEC_AFW_RULE_FETCH.pushRuleFetchSuccess(AppFirewallInitializer.getNoOfRules(), "XML", (ExecutionTimer)null);
            }
            catch (final SAXException ex) {
                AppFirewallPolicyLoader.isReqFirewallEnabled = false;
                ZSEC_AFW_RULE_FETCH.pushException(ex.getMessage(), "INVALID_APPFIREWALL_CONFIGURATION", (ExecutionTimer)null);
            }
            catch (final IOException ex2) {
                ZSEC_AFW_RULE_FETCH.pushException("APPFIREWALL'S POLICY/SCHEMA CONTENT LOADING FAILED " + ex2.getMessage(), "INVALID_APPFIREWALL_CONFIGURATION", (ExecutionTimer)null);
                AppFirewallPolicyLoader.isReqFirewallEnabled = false;
            }
        }
        else {
            ZSEC_AFW_RULE_FETCH.pushException("APPFIREWALL'S POLICY/SCHEMA IS NOT_DEFINED OR IS EMPTY ", "INVALID_APPFIREWALL_CONFIGURATION", (ExecutionTimer)null);
        }
    }
    
    public static void testRequestFirewall(final String appFirewallPolicyContent, final String appFirewallSchemaContent) {
        AppFirewallPolicyLoader.isReqFirewallEnabled = true;
        try {
            loadFirewallRules(appFirewallPolicyContent, appFirewallSchemaContent);
        }
        catch (final Exception e) {
            ZSEC_AFW_RULE_FETCH.pushException(e.getMessage(), "INVALID_APPFIREWALL_CONFIGURATION", (ExecutionTimer)null);
        }
    }
    
    public static boolean activateRequestFirewall(final SecurityFilterProperties sFConfig, final HttpServletRequest request, final FirewallStage stage) {
        if (!AppSenseAgent.isRegisteredInAppSense() && !AppFirewallPolicyLoader.isAppFirewallPolicyInitialized && stage == FirewallStage.PRE_STAGE) {
            initializeRequestFirewallRules(sFConfig, stage);
        }
        if (AppFirewallPolicyLoader.isReqFirewallEnabled && AppFirewallPolicyLoader.isAppFirewallPolicyInitialized) {
            if (AppFirewallPolicyLoader.isFileBasedLoadingEnabled) {
                loadAppFireWallPolicyFromFile();
            }
            if (AppFirewallInitializer.isRuleExist()) {
                return AppFirewallScanner.requestFirewallRegulator(request, stage);
            }
        }
        return false;
    }
    
    public static Document parseAppFirewallRule(final InputStream appFirewallPolicyStream) throws SAXException, IOException {
        return SecurityFrameworkUtil.getDocumentBuilder().parse(appFirewallPolicyStream);
    }
    
    public static void isValidAppFirewallRule(final InputStream appFirewallPolicyStream, final InputStream appFirewallSchemaStream) throws SAXException, IOException {
        final Source appFirewallPolicySource = new StreamSource(appFirewallPolicyStream);
        final Source appFirewallSchemaSource = new StreamSource(appFirewallSchemaStream);
        final SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        final Schema schema = schemaFactory.newSchema(appFirewallSchemaSource);
        final Validator validator = schema.newValidator();
        validator.validate(appFirewallPolicySource);
    }
    
    public static void doAction(final HttpServletRequest request, final SecurityResponseWrapper response) throws IOException {
        final Object appfirewallRule = request.getAttribute("ZSEC_MATCHED_APPFIREWALL_RULE");
        try {
            final Map<String, String> actionMap = ((AppFirewallRule)appfirewallRule).getActions();
            if (actionMap != null && actionMap.containsKey(AppFirewallRule.ACTIONS.REDIRECTION.action())) {
                final String redirecturl = actionMap.get(AppFirewallRule.ACTIONS.REDIRECTION.action());
                if (SecurityUtil.isValid((Object)redirecturl)) {
                    response.sendRedirect(redirecturl);
                    return;
                }
            }
            request.setAttribute(AppFirewallException.class.getName(), (Object)new AppFirewallException("BAD REQUEST"));
            response.sendError(400, "Bad Request");
        }
        finally {
            if (appfirewallRule != null) {
                request.setAttribute("ZSEC_MATCHED_APPFIREWALL_RULE", (Object)null);
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(AppFirewallPolicyLoader.class.getName());
        REQUEST_FIREWALL_PATTERN_NAMES_LIST = Arrays.asList("enable.app.firewall", "app.firewall.policy", "app.firewall.schema");
    }
}
