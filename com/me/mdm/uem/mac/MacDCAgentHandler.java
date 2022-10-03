package com.me.mdm.uem.mac;

import com.me.mdm.apps.handler.AppsAutoDeployment;
import java.util.List;
import com.dd.plist.NSObject;
import com.dd.plist.NSArray;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import com.dd.plist.NSDictionary;
import com.me.mdm.uem.actionconstants.DeviceAction;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.dd.plist.Base64;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.regex.Pattern;
import java.util.logging.Level;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.apps.handler.AppAutoDeploymentHandler;

public class MacDCAgentHandler implements AppAutoDeploymentHandler
{
    public Logger logger;
    private static final String DY_DC_AGENT_URL = "%dcagenturl%";
    private static final String DY_DC_AGENT_SHA = "%dcagentsha%";
    private static final String DY_DC_AGENT_MD5 = "%dcagentmd5%";
    private static final String DY_DC_AGENT_SIZE = "%dcagentsize%";
    private static final String DY_DC_AGENT_CONFIGURATION = "%agentconfiguration%";
    
    public MacDCAgentHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public JSONObject getAgentAppData(final Long customerID) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final String fileLocation = "%dcagenturl%";
            final String shaHash = "%dcagentsha%";
            final String md5Hash = "%dcagentmd5%";
            final String fileSize = "%dcagentsize%";
            final JSONObject packageAppDataJson = new JSONObject();
            packageAppDataJson.put("SUPPORTED_DEVICES", 16);
            jsonObject.put("MdPackageToAppDataFrom", (Object)packageAppDataJson);
            jsonObject.put("MdPackageToAppGroupForm", (Object)new JSONObject());
            jsonObject.put("PLATFORM_TYPE", 1);
            jsonObject.put("APP_TITLE", (Object)"DC_Agent_Title");
            jsonObject.put("APP_NAME", (Object)"DC_AGENT");
            jsonObject.put("APP_VERSION", (Object)"1.0");
            jsonObject.put("APP_NAME_SHORT_VERSION", (Object)"1.0");
            jsonObject.put("APP_TITLE", (Object)"DC Agent");
            jsonObject.put("IS_MODERN_APP", (Object)Boolean.TRUE);
            jsonObject.put("PACKAGE_ADDED_BY", (Object)MDMUtil.getAdminUserId());
            jsonObject.put("IDENTIFIER", (Object)"com.manageengine.ems");
            jsonObject.put("BUNDLE_IDENTIFIER", (Object)"com.manageengine.ems");
            jsonObject.put("packageIdentifier", (Object)"com.manageengine.ems");
            jsonObject.put("SECURITY_TYPE", -1);
            jsonObject.put("PROFILE_DESCRIPTION", (Object)"Profile for MacOS DC Agent");
            jsonObject.put("BUNDLE_SIZE", 0L);
            jsonObject.put("APP_CATEGORY_ID", 1);
            jsonObject.put("APP_CATEGORY_NAME", (Object)"Business");
            jsonObject.put("COUNTRY_CODE", (Object)"US");
            jsonObject.put("PACKAGE_TYPE", 0);
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            jsonObject.put("AGENT_CONFIGURATION", (Object)"%agentconfiguration%");
            jsonObject.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.AppMgmtFormBean");
            jsonObject.put("PACKAGE_SHA", (Object)shaHash);
            jsonObject.put("PACKAGE_MD5", (Object)md5Hash);
            jsonObject.put("PACKAGE_SIZE", (Object)fileSize);
            jsonObject.put("STATIC_SERVER_URL", (Object)fileLocation);
            jsonObject.put("SUPPORTED_DEVICES", 16);
            jsonObject.put("DISPLAY_IMAGE_LOC", (Object)"");
            jsonObject.put("FULL_IMAGE", (Object)"");
            jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            jsonObject.put("PACKAGE_TYPE", 2);
            jsonObject.put("APP_CONFIG", (Object)Boolean.TRUE);
            jsonObject.put("HAS_APP_CONFIGURATION", (Object)Boolean.TRUE);
            jsonObject.put("IS_PACKAGE_DEPLOY", (Object)Boolean.TRUE);
            jsonObject.put("TABLE_NAME", (Object)"MdPackage");
            jsonObject.put("IS_NATIVE_AGENT", (Object)Boolean.TRUE);
            final JSONObject appPolicyJSON = new JSONObject();
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
            appPolicyJSON.put("CONFIG_DATA_IDENTIFIER", (Object)"DC_Agent");
            jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            jsonObject.put("APP_POLICY", (Object)appPolicyJSON);
            final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(1).isCurrentPackageNewToAppRepo("com.manageengine.ems", customerID);
            if (isCurrentPackageNew) {
                jsonObject.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Excepion at MACDCAgentHandler", e);
        }
        return jsonObject;
    }
    
    private String replaceVariable(final String replaceIn, final String replaceWhat, final String replaceWith) {
        return Pattern.compile(replaceWhat, 2).matcher(replaceIn).replaceAll(replaceWith);
    }
    
    private String escapeMetaCharAndReplace(final String replaceIn, final String replaceWhat, final String replaceWith) {
        final MDMStringUtils util = new MDMStringUtils();
        return this.replaceVariable(replaceIn, replaceWhat, util.escapeMetaCharacters(replaceWith));
    }
    
    private String base64Encode(final String data) {
        final String encodedString = Base64.encodeBytes(data.getBytes());
        return encodedString;
    }
    
    @Override
    public String replaceDynamicVariables(String payload, final Long customerID, final String strUDID) {
        try {
            final JSONObject inputJSON = new JSONObject();
            inputJSON.put("CUSTOMER_ID", (Object)customerID);
            inputJSON.put("uemPlatformType", 1);
            final JSONObject agentDetails = MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.GETLEGACY_AGENT_DETAILS, inputJSON);
            final String url = String.valueOf(agentDetails.get("AgentDownloadUrl"));
            final String sha = String.valueOf(agentDetails.get("SHA256FileHash"));
            final String md5 = String.valueOf(agentDetails.get("MD5FileHash"));
            final String size = String.valueOf(agentDetails.get("PackageSize"));
            final String commandLine = String.valueOf(agentDetails.get("CommandLineParams"));
            payload = this.escapeMetaCharAndReplace(payload, "%dcagenturl%", url);
            payload = this.replaceVariable(payload, "%dcagentsha%", sha);
            payload = this.replaceVariable(payload, "%dcagentmd5%", md5);
            payload = this.replaceVariable(payload, "<string>%dcagentsize%</string>", "<integer>" + size + "</integer>");
            final String finalPayload = this.getConfiguration(commandLine);
            payload = this.replaceVariable(payload, this.base64Encode("%agentconfiguration%"), this.base64Encode(finalPayload));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to replace dynamic variable. Error at MacDCAgentHandler", e);
        }
        return payload;
    }
    
    public String getConfiguration(final String configuration) {
        try {
            final NSDictionary rootDict = (NSDictionary)DMSecurityUtil.parsePropertyList((InputStream)new ByteArrayInputStream(configuration.getBytes(Charset.forName("UTF-8"))));
            rootDict.put("PayloadType", (Object)"com.manageengine.ems");
            rootDict.put("PayloadVersion", (Object)1);
            rootDict.put("PayloadDisplayName", (Object)"Agent Configuration");
            rootDict.put("PayloadIdentifier", (Object)"com.manageengine.ems");
            rootDict.put("PayloadUUID", (Object)"com.manageengine.ems--agent");
            final NSArray configArray = new NSArray(1);
            configArray.setValue(0, (Object)rootDict);
            final NSDictionary finalDict = new NSDictionary();
            finalDict.put("PayloadContent", (NSObject)configArray);
            finalDict.put("PayloadRemovalDisallowed", (Object)Boolean.TRUE);
            finalDict.put("PayloadScope", (Object)"System");
            finalDict.put("PayloadType", (Object)"Configuration");
            finalDict.put("PayloadOrganization", (Object)"MDM");
            finalDict.put("PayloadVersion", (Object)1);
            finalDict.put("PayloadDisplayName", (Object)"Agent Configuration");
            finalDict.put("PayloadIdentifier", (Object)"com.manageengine.ems");
            finalDict.put("PayloadUUID", (Object)"com.manageengine.ems");
            final String finalConfig = finalDict.toXMLPropertyList();
            return finalConfig;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to fetch DC Configuration. Error at MacDCAgentHandler", e);
            return null;
        }
    }
    
    @Override
    public List filterDevices(final List resourceList) {
        return resourceList;
    }
    
    public void addDCAgentToMDMRepoIfNotExistAlready(final Long customerID) {
        try {
            this.logger.log(Level.INFO, "Adding DC agent app for the customer: {0}", new Object[] { customerID });
            AppsAutoDeployment.getInstance().addAgentApp(1, customerID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to install agent for Mac devices", e);
        }
    }
    
    @Override
    public int getPlatformType() {
        return 1;
    }
    
    @Override
    public int getSupportedDevices() {
        return 16;
    }
    
    @Override
    public String getBundleIdentifier() {
        return "com.manageengine.ems";
    }
}
