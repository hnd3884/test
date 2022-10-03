package com.me.devicemanagement.framework.server.util;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.filter.security.SecurityFilterChooserAPI;
import javax.servlet.FilterConfig;
import org.apache.xerces.util.SecurityManager;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.w3c.dom.Document;
import com.dd.plist.XMLPropertyListParser;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.dd.plist.NSObject;
import com.adventnet.iam.security.SecurityUtil;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;

public class DMSecurityUtil
{
    public static DocumentBuilder getDocumentBuilder() {
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("http://xml.org/sax/features/namespaces", true);
        final DocumentBuilder documentBuilder = SecurityUtil.getDocumentBuilder(false, false, properties);
        return documentBuilder;
    }
    
    public static NSObject parsePropertyList(final byte[] bytes) throws Exception {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        inputStream.reset();
        return parsePropertyList(inputStream);
    }
    
    public static NSObject parsePropertyList(final InputStream inputStream) throws Exception {
        final Document doc = getDocumentBuilder().parse(inputStream);
        return XMLPropertyListParser.parse(doc);
    }
    
    public static SAXParser getSAXParser(final boolean isValidating, final boolean isNamespaceAware) throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(isValidating);
        factory.setNamespaceAware(isNamespaceAware);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        final SAXParser parser = factory.newSAXParser();
        final SecurityManager manager = new SecurityManager();
        manager.setEntityExpansionLimit(1000);
        parser.setProperty("http://apache.org/xml/properties/security-manager", manager);
        return parser;
    }
    
    public static boolean checkAndEnableSecurityFilter(final FilterConfig fiterConfig) {
        boolean allow = false;
        final String[] classNames = ProductClassLoader.getMultiImplProductClass("DM_SECURITY_FILTER_SELECTION_CLASS");
        if (classNames.length > 0) {
            allow = true;
            SecurityFilterChooserAPI securityFilterChooserAPI = null;
            try {
                for (final String className : classNames) {
                    securityFilterChooserAPI = (SecurityFilterChooserAPI)Class.forName(className).newInstance();
                    allow = securityFilterChooserAPI.useSecurityFilter(fiterConfig);
                    if (!allow) {
                        break;
                    }
                }
            }
            catch (final Exception e) {
                Logger.getLogger(DMSecurityUtil.class.getName()).log(Level.SEVERE, "Exception in DMSecurityUtil.checkAndEnableSecurityFilter()... ", e);
            }
        }
        ApiFactoryProvider.getCacheAccessAPI().putCache("IS_SECURITY_FILTER_ENABLED", allow, 1);
        return allow;
    }
}
