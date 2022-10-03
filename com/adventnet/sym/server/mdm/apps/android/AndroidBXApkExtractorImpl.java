package com.adventnet.sym.server.mdm.apps.android;

import javax.xml.xpath.XPathExpression;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPathConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathFactory;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import java.util.Iterator;
import com.pras.abx.BXCallback;
import com.pras.abx.Android_BX2;
import com.pras.GenXML;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONException;
import java.util.logging.Level;
import java.io.File;
import org.json.JSONObject;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;

public class AndroidBXApkExtractorImpl extends AndroidAPKExtractor
{
    private static final String ANDROID_MANIFEST_XML = "AndroidManifest.xml";
    private String manifestXmlPath;
    private Document xmlDoc;
    private XPath xpath;
    
    public AndroidBXApkExtractorImpl() {
        this.manifestXmlPath = null;
        this.xmlDoc = null;
        this.xpath = null;
        AndroidBXApkExtractorImpl.PACKAGE_EXPRESSION = "//manifest/@package";
        AndroidBXApkExtractorImpl.VERSION_EXPRESSION = "//manifest/@versionName";
        AndroidBXApkExtractorImpl.MINIMUM_SDK_EXPRESSION = "MinSdkVersion";
    }
    
    @Override
    public synchronized JSONObject getAndroidAppsDetails(final String apkPath) throws JSONException {
        final String encrytedXMLPath = this.extractAndroidXML(apkPath);
        final File file = new File(encrytedXMLPath);
        if (file.isFile()) {
            final String decryptedXMLPath = this.decryptAndroidXML(encrytedXMLPath);
            if (decryptedXMLPath != null) {
                try {
                    this.manifestXmlPath = decryptedXMLPath;
                    final JSONObject apkProps = this.getAPKProperties(this.getReqPropsForAddApp());
                    if ("{}".equalsIgnoreCase(apkProps.toString())) {
                        return this.getErrorProps("App details could not be extracted from app bundle");
                    }
                    return apkProps;
                }
                catch (final Exception exp) {
                    this.logger.log(Level.WARNING, "Unable to get the apk properties {0}", exp);
                    return this.getErrorProps("App details could not be extracted from app bundle");
                }
            }
            return this.getErrorProps("Decryption of manifest file failed");
        }
        return this.getErrorProps("Manifest file not found in apk file");
    }
    
    private String extractAndroidXML(final String apkFileName) {
        final File file = new File(apkFileName);
        final File apkDirectory = new File(file.getParent());
        ApiFactoryProvider.getZipUtilAPI().unzip(apkFileName, apkDirectory.toString(), true, true, new String[] { "AndroidManifest.xml" });
        return apkDirectory.toString() + File.separator + "AndroidManifest.xml";
    }
    
    private String decryptAndroidXML(final String xmlPath) {
        String decryptedAndroidXML = null;
        try {
            final Android_BX2 abx2 = new Android_BX2((BXCallback)new GenXML());
            abx2.parse(xmlPath);
            decryptedAndroidXML = xmlPath + ".xml";
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Fail to parse - " + s);
        }
        return decryptedAndroidXML;
    }
    
    @Override
    protected JSONObject getAPKProperties(final JSONObject requiredProperties) throws JSONException {
        final JSONObject apkProp = new JSONObject();
        this.initalize();
        final Iterator<String> myIter = requiredProperties.keys();
        while (myIter.hasNext()) {
            final String key = myIter.next();
            final String value = String.valueOf(requiredProperties.get(key));
            apkProp.put(key, (Object)this.getPropertyValue(value));
        }
        return apkProp;
    }
    
    private void initalize() {
        try {
            final DocumentBuilder builder = DMSecurityUtil.getDocumentBuilder();
            this.xmlDoc = builder.parse(this.manifestXmlPath);
            this.xpath = XPathFactory.newInstance().newXPath();
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in initalize {0}", exp);
        }
    }
    
    @Override
    protected String getPropertyValue(final String expression) {
        String nodeName = null;
        try {
            final XPathExpression expr = this.xpath.compile(expression);
            final Object result = expr.evaluate(this.xmlDoc, XPathConstants.NODESET);
            final NodeList nodes = (NodeList)result;
            nodeName = nodes.item(0).getNodeValue();
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception ocurred in querying the values {0}", exp);
        }
        return nodeName;
    }
}
