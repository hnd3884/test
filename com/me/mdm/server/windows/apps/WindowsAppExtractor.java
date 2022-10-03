package com.me.mdm.server.windows.apps;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPathConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathFactory;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONException;
import com.me.mdm.server.apps.AppDependencyHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.File;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;
import com.me.mdm.server.factory.MDMWinAppExtractorAPI;
import com.adventnet.sym.server.mdm.apps.EnterpriseAppExtractor;

public class WindowsAppExtractor extends EnterpriseAppExtractor implements MDMWinAppExtractorAPI
{
    private static final String PRODUCT_EXPRESSION = "//App/@ProductID";
    private static final String VERSION_EXPRESSION = "//App/@Version";
    private static final String WINDOWS_MANIFEST_XML = "WMAppManifest.xml";
    private static final String PHONE_PRODUCT_EXPRESSION = "/*/*[local-name()='PhoneIdentity']/@PhoneProductId";
    private static final String APPX_PRODUCT_EXPRESSION = "/*/*[local-name()='Identity']/@Name";
    private static final String APPX_MINVERSION_EXPRESSION = "/*/*[local-name()='Dependencies']/*[local-name()='TargetDeviceFamily']/@MinVersion";
    private static final String APPX_AUMID_EXPRESSION = "/*/*[local-name()='Applications']/*[local-name()='Application']/@Id";
    private static final String APPX_ALT_MINVERSION_EXPRESSION = "/*/*[local-name()='Prerequisites']/*[local-name()='OSMinVersion']/text()";
    private static final String APPX_SUPPORTED_DEVICE_EXPRESSION = "/*/*[local-name()='Dependencies']/*[local-name()='TargetDeviceFamily']/@Name";
    private static final String APPX_APP_NAME_EXPRESSION = "/*/*[local-name()='Properties']/*[local-name()='DisplayName']/text()";
    private static final String XAP_MINVERSION_EXPRESSION = "/*/@AppPlatformVersion";
    private static final String APPX_VERSION_EXPRESSION = "/*/*[local-name()='Identity']/@Version";
    private static final String APPX_ARCH_EXPRESSION = "/*/*[local-name()='Identity']/@ProcessorArchitecture";
    private static final String APPX_BUNDLE_FILE_EXPRESSION = "/*/*[local-name()='Packages']/*[@Type='application']/@FileName";
    private static final String WINDOWS_APPX_MANIFEST_XML = "AppxManifest.xml";
    private static final String WINDOWS_APPX_BUNDLE_MANIFEST_FOLDER = "AppxMetadata";
    private static final String WINDOWS_APPX_BUNDLE_MANIFEST_FILE = "AppxBundleManifest.xml";
    public Document xmlDoc;
    public XPath xpath;
    public String manifestXmlPath;
    private String sourceClass;
    private static Logger logger;
    
    public WindowsAppExtractor() {
        this.xmlDoc = null;
        this.xpath = null;
        this.manifestXmlPath = null;
        this.sourceClass = "WindowsXAPExtractor";
        final String sourceMethod = "WindowsAPPExtractor";
        SyMLogger.info(WindowsAppExtractor.logger, this.sourceClass, sourceMethod, "Creating instance...");
    }
    
    @Override
    public synchronized JSONObject getAppDetails(final String xapPath) throws Exception {
        JSONObject xapProps = null;
        if (xapPath.toLowerCase().endsWith("msi")) {
            xapProps = MDMApiFactoryProvider.getMDMWinAppExtractorAPI().getMSIProperties(xapPath);
        }
        else {
            String encryptedAppxXmlPath = this.extractWindowsAppxManifesXML(xapPath);
            final String[] appxFiles = encryptedAppxXmlPath.split(",");
            encryptedAppxXmlPath = appxFiles[0];
            final File encryptedXMLFile = new File(encryptedAppxXmlPath);
            if (encryptedXMLFile.exists()) {
                xapProps = this.getAPPXPropertiesFromXML(encryptedAppxXmlPath);
                final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
                final JSONObject jsonObject = new AppDependencyHandler(3).getListofDependenceies(encryptedAppxXmlPath, customerID);
                xapProps.put("dependencies", jsonObject.get("requiredDependencies"));
                xapProps.put("availableDependencies", jsonObject.get("availableDependencies"));
                if (xapPath.toLowerCase().contains("appxbundle")) {
                    xapProps = this.getAllPropertiesForAppxbundle(xapProps, appxFiles);
                }
            }
            else {
                final String encrytedXMLPath = this.extractWindowsXML(xapPath);
                final File xapXML = new File(encrytedXMLPath);
                if (xapXML.exists()) {
                    xapProps = this.getXAPPropertiesFromXML(encrytedXMLPath);
                }
                else {
                    xapProps = new JSONObject();
                    xapProps.put("extractError", (Object)"errorParsing");
                    xapProps.put("errorMsg", (Object)"dc.mdm.app.app_file_corrupted");
                    xapProps.put("errorParams", (Object)"dc.mdm.app.manifest_file_not_found");
                }
            }
            if (xapProps != null && xapProps.has("PackageName") && String.valueOf(xapProps.get("PackageName")).trim().equalsIgnoreCase("")) {
                xapProps.remove("VersionName");
                xapProps.remove("PackageName");
                xapProps.put("extractError", (Object)"Bundle Identifier is empty");
                xapProps.put("errorMsg", (Object)"dc.mdm.app.app_file_corrupted");
                xapProps.put("errorParams", (Object)"dc.mdm.app.bundle_identifier_empty");
            }
        }
        xapProps.put("file_name", (Object)xapPath);
        return xapProps;
    }
    
    public synchronized JSONObject getWindowsAppsDetails(final String xapPath) throws JSONException {
        final String encrytedXMLPath = this.extractWindowsXML(xapPath);
        JSONObject xapProps = null;
        final File encryptedXMLFile = new File(encrytedXMLPath);
        if (encryptedXMLFile.exists()) {
            xapProps = this.getXAPPropertiesFromXML(encrytedXMLPath);
        }
        else {
            xapProps = new JSONObject();
            xapProps.put("extractError", (Object)"errorParsing");
        }
        return xapProps;
    }
    
    private JSONObject getXAPPropertiesFromXML(final String windowsXMLPath) throws JSONException {
        final JSONObject xapProp = new JSONObject();
        try {
            xapProp.put("PackageName", (Object)"//App/@ProductID");
            xapProp.put("VersionName", (Object)"//App/@Version");
            xapProp.put("MIN_OS", (Object)"/*/@AppPlatformVersion");
            final JSONObject extractedProperties = this.getXAPProperties(xapProp, windowsXMLPath);
            extractedProperties.put("SUPPORTED_DEVICES", (Object)"8");
            extractedProperties.put("SUPPORTED_ARCH", (Object)"1");
            return extractedProperties;
        }
        catch (final Exception exp) {
            WindowsAppExtractor.logger.log(Level.WARNING, "Unable to get the xap properties {0}", exp);
            xapProp.remove("PackageName");
            xapProp.remove("VersionName");
            xapProp.remove("MIN_OS");
            xapProp.put("errorMsg", (Object)"dc.mdm.app.app_file_corrupted");
            xapProp.put("errorParams", (Object)"dc.mdm.app.app_details_could_not_be_found");
            return xapProp;
        }
    }
    
    public static String getSupportedDeviceCode(final String name) {
        int codeVal = 0;
        String code = null;
        final String[] supporttedDevices = name.split(",");
        if (supporttedDevices.length == 0 || (supporttedDevices.length == 1 && supporttedDevices[0] == "")) {
            codeVal = 32;
        }
        else {
            for (int i = 0; i < supporttedDevices.length; ++i) {
                if (supporttedDevices[i].toLowerCase().contains("universal")) {
                    codeVal += 24;
                }
                else if (supporttedDevices[i].toLowerCase().contains("mobile") || supporttedDevices[i].toLowerCase().contains("windowsphone8x")) {
                    codeVal += 8;
                }
                else if (supporttedDevices[i].toLowerCase().contains("desktop") || supporttedDevices[i].toLowerCase().contains("windows8x")) {
                    codeVal += 16;
                }
            }
        }
        code = "" + codeVal;
        return code;
    }
    
    protected String getSupportedArchCode(final String name) {
        int codeVal = 0;
        String code = null;
        final String[] supporttedDevices = name.split(",");
        if (supporttedDevices.length == 0 || (supporttedDevices.length == 1 && supporttedDevices[0] == "")) {
            codeVal = 14;
        }
        else {
            for (int i = 0; i < supporttedDevices.length; ++i) {
                if (supporttedDevices[i].toLowerCase().contains("arm")) {
                    codeVal += 2;
                }
                if (supporttedDevices[i].toLowerCase().contains("x86")) {
                    codeVal += 4;
                }
                if (supporttedDevices[i].toLowerCase().contains("x64")) {
                    codeVal += 8;
                }
                if (supporttedDevices[i].toLowerCase().contains("neutral")) {
                    codeVal = 1;
                }
            }
        }
        code = "" + codeVal;
        return code;
    }
    
    private JSONObject getAPPXPropertiesFromXML(final String windowsAppxXMLPath) throws JSONException {
        JSONObject xapProp = new JSONObject();
        try {
            xapProp.put("PackageName", (Object)"/*/*[local-name()='Identity']/@Name");
            xapProp.put("PhoneProductID", (Object)"/*/*[local-name()='PhoneIdentity']/@PhoneProductId");
            xapProp.put("VersionName", (Object)"/*/*[local-name()='Identity']/@Version");
            xapProp.put("MIN_OS", (Object)"/*/*[local-name()='Dependencies']/*[local-name()='TargetDeviceFamily']/@MinVersion");
            xapProp.put("SUPPORTED_DEVICES", (Object)"/*/*[local-name()='Dependencies']/*[local-name()='TargetDeviceFamily']/@Name");
            xapProp.put("SUPPORTED_ARCH", (Object)"/*/*[local-name()='Identity']/@ProcessorArchitecture");
            xapProp.put("AUMID", (Object)"/*/*[local-name()='Applications']/*[local-name()='Application']/@Id");
            xapProp.put("APP_NAME", (Object)"/*/*[local-name()='Properties']/*[local-name()='DisplayName']/text()");
            xapProp = this.getXAPProperties(xapProp, windowsAppxXMLPath);
            final String deviceCode = getSupportedDeviceCode(String.valueOf(xapProp.get("SUPPORTED_DEVICES")));
            final String archCode = this.getSupportedArchCode(String.valueOf(xapProp.get("SUPPORTED_ARCH")));
            xapProp.put("SUPPORTED_DEVICES", (Object)deviceCode);
            xapProp.put("SUPPORTED_ARCH", (Object)archCode);
            xapProp.put("MIN_OS", (Object)this.getMinOS(String.valueOf(xapProp.get("MIN_OS")), windowsAppxXMLPath));
        }
        catch (final Exception exp) {
            WindowsAppExtractor.logger.log(Level.WARNING, "Unable to get the xap properties {0}", exp);
            xapProp.remove("PackageName");
            xapProp.remove("VersionName");
            xapProp.remove("MIN_OS");
            xapProp.put("errorMsg", (Object)"dc.mdm.app.app_file_corrupted");
            xapProp.put("errorParams", (Object)"dc.mdm.app.app_details_could_not_be_found");
        }
        return xapProp;
    }
    
    private JSONObject getXAPProperties(final String windowsXMLPath) throws JSONException {
        final JSONObject xapProp = new JSONObject();
        try {
            xapProp.put("IDENTIFIER", (Object)"/*/*[local-name()='PhoneIdentity']/@PhoneProductId");
            xapProp.put("APP_VERSION", (Object)"/*/*[local-name()='Identity']/@Version");
            return this.getXAPProperties(xapProp, windowsXMLPath);
        }
        catch (final Exception exp) {
            WindowsAppExtractor.logger.log(Level.WARNING, "Unable to get the xap properties {0}", exp);
            xapProp.remove("IDENTIFIER");
            xapProp.remove("APP_VERSION");
            xapProp.put("errorMsg", (Object)"dc.mdm.app.app_file_corrupted");
            xapProp.put("errorParams", (Object)"dc.mdm.app.app_details_could_not_be_found");
            return xapProp;
        }
    }
    
    private JSONObject getXAPProperties(final JSONObject requiredProperties, final String windowsXMLPath) throws JSONException {
        final JSONObject xapProp = new JSONObject();
        this.manifestXmlPath = windowsXMLPath;
        this.initalize();
        final Iterator<String> myIter = requiredProperties.keys();
        while (myIter.hasNext()) {
            final String key = myIter.next().toString();
            final String value = String.valueOf(requiredProperties.get(key));
            String nodeValue = this.getNodeName(value);
            if (nodeValue == null) {
                nodeValue = "";
            }
            nodeValue = nodeValue.replaceAll("[{}]", " ");
            xapProp.put(key, (Object)nodeValue);
        }
        return xapProp;
    }
    
    private void initalize() {
        try {
            final DocumentBuilder builder = DMSecurityUtil.getDocumentBuilder();
            this.xmlDoc = builder.parse(this.manifestXmlPath);
            this.xpath = XPathFactory.newInstance().newXPath();
        }
        catch (final Exception exp) {
            WindowsAppExtractor.logger.log(Level.WARNING, "Exception occurred in initalize {0}", exp);
        }
    }
    
    private String getNodeName(final String expression) {
        String nodeName = null;
        try {
            XPathExpression expr = null;
            expr = this.xpath.compile(expression);
            final Object result = expr.evaluate(this.xmlDoc, XPathConstants.NODESET);
            final NodeList nodes = (NodeList)result;
            if (nodes.getLength() == 1) {
                nodeName = nodes.item(0).getNodeValue();
            }
            else {
                nodeName = "";
                for (int i = 0; i < nodes.getLength(); ++i) {
                    if (nodeName == "") {
                        nodeName = nodes.item(i).getNodeValue();
                    }
                    else {
                        nodeName = nodeName + "," + nodes.item(i).getNodeValue();
                    }
                }
            }
            WindowsAppExtractor.logger.log(Level.INFO, "Node Name is {0}", nodeName);
        }
        catch (final Exception exp) {
            WindowsAppExtractor.logger.log(Level.WARNING, "Exception ocurred in querying the values {0}", exp);
        }
        return nodeName;
    }
    
    private String extractWindowsXML(final String xapFileName) {
        final File file = new File(xapFileName);
        final File xapDirectory = new File(file.getParent());
        ApiFactoryProvider.getZipUtilAPI().unzip(xapFileName, xapDirectory.toString(), true, true, new String[] { "WMAppManifest.xml" });
        return xapDirectory.toString() + File.separator.toString() + "WMAppManifest.xml";
    }
    
    private String extractWindowsAppxManifesXML(String appFileName) throws JSONException {
        final File file = new File(appFileName);
        final File xapDirectory = new File(file.getParent());
        String manifestFileLocation = null;
        String appxBundlefiles = "";
        if (appFileName.toLowerCase().endsWith("appxbundle")) {
            ApiFactoryProvider.getZipUtilAPI().unzip(appFileName, xapDirectory.toString(), true, true, new String[] { "AppxMetadata/AppxBundleManifest.xml" });
            final JSONObject packageName = this.getAPPXBundlePropertiesFromXML(xapDirectory.toString() + File.separator.toString() + "AppxBundleManifest.xml");
            final String appxFileName = String.valueOf(packageName.get("packageName"));
            final String[] appxFiles = appxFileName.split(",");
            ApiFactoryProvider.getZipUtilAPI().unzip(appFileName, xapDirectory.toString(), true, true, appxFiles);
            appFileName = xapDirectory + File.separator + appxFiles[0];
            if (appxFiles.length > 1) {
                for (int i = 1; i < appxFiles.length; ++i) {
                    appxBundlefiles = appxBundlefiles + "," + xapDirectory.toString() + File.separator.toString() + appxFiles[i];
                }
            }
        }
        ApiFactoryProvider.getZipUtilAPI().unzip(appFileName, xapDirectory.toString(), true, true, new String[] { "AppxManifest.xml" });
        manifestFileLocation = xapDirectory.toString() + File.separator.toString() + "AppxManifest.xml";
        return manifestFileLocation + appxBundlefiles;
    }
    
    private JSONObject getAPPXBundlePropertiesFromXML(final String windowsAppxXMLPath) throws JSONException {
        final JSONObject xapProp = new JSONObject();
        try {
            xapProp.put("packageName", (Object)"/*/*[local-name()='Packages']/*[@Type='application']/@FileName");
            return this.getXAPProperties(xapProp, windowsAppxXMLPath);
        }
        catch (final Exception exp) {
            WindowsAppExtractor.logger.log(Level.WARNING, "Unable to get the xap properties {0}", exp);
            xapProp.remove("packageName");
            xapProp.put("errorMsg", (Object)"dc.mdm.app.app_file_corrupted");
            xapProp.put("errorParams", (Object)"dc.mdm.app.app_details_could_not_be_found");
            return xapProp;
        }
    }
    
    private JSONObject getAllPropertiesForAppxbundle(final JSONObject xapProps, final String[] appxFiles) {
        JSONObject allprops = null;
        if (appxFiles.length == 1) {
            allprops = xapProps;
        }
        else {
            final File file = new File(appxFiles[1]);
            final File xapDirectory = new File(file.getParent());
            for (int i = 1; i < appxFiles.length; ++i) {
                ApiFactoryProvider.getZipUtilAPI().unzip(appxFiles[i], xapDirectory.toString(), true, true, new String[] { "AppxManifest.xml" });
                try {
                    final JSONObject newProps = this.getAPPXPropertiesFromXML(xapDirectory.toString() + File.separator + "AppxManifest.xml");
                    allprops = this.compareAndGetProperties(xapProps, newProps);
                }
                catch (final JSONException e) {
                    return xapProps;
                }
            }
        }
        return allprops;
    }
    
    private JSONObject compareAndGetProperties(final JSONObject extractedProp, final JSONObject extractedPropToCompare) throws JSONException {
        final JSONObject bestProps = extractedProp;
        final String supportedDevice1 = extractedProp.optString("SUPPORTED_DEVICES");
        final String supportedArch1 = extractedProp.optString("SUPPORTED_ARCH");
        final String supportedDevice2 = extractedPropToCompare.optString("SUPPORTED_DEVICES");
        final String supportedArch2 = extractedPropToCompare.optString("SUPPORTED_ARCH");
        if (supportedDevice1 != null && supportedDevice2 != null) {
            if (Integer.parseInt(supportedDevice1) > Integer.parseInt(supportedDevice2)) {
                bestProps.put("SUPPORTED_DEVICES", (Object)supportedDevice1);
            }
            else {
                bestProps.put("SUPPORTED_DEVICES", (Object)supportedDevice2);
            }
        }
        if (supportedArch1 != null && supportedArch2 != null) {
            bestProps.put("SUPPORTED_ARCH", (Object)("" + (Integer.parseInt(supportedArch1) | Integer.parseInt(supportedArch2))));
        }
        return bestProps;
    }
    
    private String getMinOS(final String curMinOS, final String manifestFile) throws JSONException {
        String minOS = null;
        if (!curMinOS.equals("")) {
            minOS = curMinOS.split(",")[0];
        }
        else {
            JSONObject props = new JSONObject();
            props.put("MIN_OS", (Object)"/*/*[local-name()='Prerequisites']/*[local-name()='OSMinVersion']/text()");
            props = this.getXAPProperties(props, manifestFile);
            minOS = String.valueOf(props.get("MIN_OS"));
        }
        return minOS;
    }
    
    @Override
    public JSONObject getMSIProperties(final String msiPath) throws JSONException {
        final JSONObject xapProps = new JSONObject();
        xapProps.put("SUPPORTED_ARCH", (Object)this.getSupportedArchCode("x86"));
        xapProps.put("SUPPORTED_DEVICES", (Object)getSupportedDeviceCode("desktop"));
        return xapProps;
    }
    
    static {
        WindowsAppExtractor.logger = Logger.getLogger("MDMLogger");
    }
}
