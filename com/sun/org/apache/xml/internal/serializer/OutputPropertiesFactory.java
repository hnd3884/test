package com.sun.org.apache.xml.internal.serializer;

import java.util.Hashtable;
import java.util.Enumeration;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.BufferedInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.InputStream;
import java.io.IOException;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.util.Properties;

public final class OutputPropertiesFactory
{
    private static final String S_BUILTIN_EXTENSIONS_URL = "http://xml.apache.org/xalan";
    private static final String S_BUILTIN_OLD_EXTENSIONS_URL = "http://xml.apache.org/xslt";
    public static final String S_BUILTIN_EXTENSIONS_UNIVERSAL = "{http://xml.apache.org/xalan}";
    public static final String S_KEY_INDENT_AMOUNT = "{http://xml.apache.org/xalan}indent-amount";
    public static final String S_KEY_LINE_SEPARATOR = "{http://xml.apache.org/xalan}line-separator";
    public static final String S_KEY_CONTENT_HANDLER = "{http://xml.apache.org/xalan}content-handler";
    public static final String S_KEY_ENTITIES = "{http://xml.apache.org/xalan}entities";
    public static final String S_USE_URL_ESCAPING = "{http://xml.apache.org/xalan}use-url-escaping";
    public static final String S_OMIT_META_TAG = "{http://xml.apache.org/xalan}omit-meta-tag";
    public static final String S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL = "{http://xml.apache.org/xslt}";
    public static final int S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL_LEN;
    public static final String ORACLE_IS_STANDALONE = "http://www.oracle.com/xml/is-standalone";
    private static final String S_XSLT_PREFIX = "xslt.output.";
    private static final int S_XSLT_PREFIX_LEN;
    private static final String S_XALAN_PREFIX = "org.apache.xslt.";
    private static final int S_XALAN_PREFIX_LEN;
    private static Integer m_synch_object;
    private static final String PROP_DIR = "com/sun/org/apache/xml/internal/serializer/";
    private static final String PROP_FILE_XML = "output_xml.properties";
    private static final String PROP_FILE_TEXT = "output_text.properties";
    private static final String PROP_FILE_HTML = "output_html.properties";
    private static final String PROP_FILE_UNKNOWN = "output_unknown.properties";
    private static Properties m_xml_properties;
    private static Properties m_html_properties;
    private static Properties m_text_properties;
    private static Properties m_unknown_properties;
    private static final Class ACCESS_CONTROLLER_CLASS;
    
    private static Class findAccessControllerClass() {
        try {
            return Class.forName("java.security.AccessController");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public static final Properties getDefaultMethodProperties(final String method) {
        String fileName = null;
        Properties defaultProperties = null;
        try {
            synchronized (OutputPropertiesFactory.m_synch_object) {
                if (null == OutputPropertiesFactory.m_xml_properties) {
                    fileName = "output_xml.properties";
                    OutputPropertiesFactory.m_xml_properties = loadPropertiesFile(fileName, null);
                }
            }
            if (method.equals("xml")) {
                defaultProperties = OutputPropertiesFactory.m_xml_properties;
            }
            else if (method.equals("html")) {
                if (null == OutputPropertiesFactory.m_html_properties) {
                    fileName = "output_html.properties";
                    OutputPropertiesFactory.m_html_properties = loadPropertiesFile(fileName, OutputPropertiesFactory.m_xml_properties);
                }
                defaultProperties = OutputPropertiesFactory.m_html_properties;
            }
            else if (method.equals("text")) {
                if (null == OutputPropertiesFactory.m_text_properties) {
                    fileName = "output_text.properties";
                    OutputPropertiesFactory.m_text_properties = loadPropertiesFile(fileName, OutputPropertiesFactory.m_xml_properties);
                    if (null == OutputPropertiesFactory.m_text_properties.getProperty("encoding")) {
                        final String mimeEncoding = Encodings.getMimeEncoding(null);
                        ((Hashtable<String, String>)OutputPropertiesFactory.m_text_properties).put("encoding", mimeEncoding);
                    }
                }
                defaultProperties = OutputPropertiesFactory.m_text_properties;
            }
            else if (method.equals("")) {
                if (null == OutputPropertiesFactory.m_unknown_properties) {
                    fileName = "output_unknown.properties";
                    OutputPropertiesFactory.m_unknown_properties = loadPropertiesFile(fileName, OutputPropertiesFactory.m_xml_properties);
                }
                defaultProperties = OutputPropertiesFactory.m_unknown_properties;
            }
            else {
                defaultProperties = OutputPropertiesFactory.m_xml_properties;
            }
        }
        catch (final IOException ioe) {
            throw new WrappedRuntimeException(Utils.messages.createMessage("ER_COULD_NOT_LOAD_METHOD_PROPERTY", new Object[] { fileName, method }), ioe);
        }
        return new Properties(defaultProperties);
    }
    
    private static Properties loadPropertiesFile(final String resourceName, final Properties defaults) throws IOException {
        final Properties props = new Properties(defaults);
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            if (OutputPropertiesFactory.ACCESS_CONTROLLER_CLASS != null) {
                is = AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        return OutputPropertiesFactory.class.getResourceAsStream(resourceName);
                    }
                });
            }
            else {
                is = OutputPropertiesFactory.class.getResourceAsStream(resourceName);
            }
            bis = new BufferedInputStream(is);
            props.load(bis);
        }
        catch (final IOException ioe) {
            if (defaults == null) {
                throw ioe;
            }
            throw new WrappedRuntimeException(Utils.messages.createMessage("ER_COULD_NOT_LOAD_RESOURCE", new Object[] { resourceName }), ioe);
        }
        catch (final SecurityException se) {
            if (defaults == null) {
                throw se;
            }
            throw new WrappedRuntimeException(Utils.messages.createMessage("ER_COULD_NOT_LOAD_RESOURCE", new Object[] { resourceName }), se);
        }
        finally {
            if (bis != null) {
                bis.close();
            }
            if (is != null) {
                is.close();
            }
        }
        final Enumeration keys = ((Properties)props.clone()).keys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            String value = null;
            try {
                value = SecuritySupport.getSystemProperty(key);
            }
            catch (final SecurityException ex) {}
            if (value == null) {
                value = ((Hashtable<K, String>)props).get(key);
            }
            final String newKey = fixupPropertyString(key, true);
            String newValue = null;
            try {
                newValue = SecuritySupport.getSystemProperty(newKey);
            }
            catch (final SecurityException ex2) {}
            if (newValue == null) {
                newValue = fixupPropertyString(value, false);
            }
            else {
                newValue = fixupPropertyString(newValue, false);
            }
            if (key != newKey || value != newValue) {
                props.remove(key);
                ((Hashtable<String, String>)props).put(newKey, newValue);
            }
        }
        return props;
    }
    
    private static String fixupPropertyString(String s, final boolean doClipping) {
        if (doClipping && s.startsWith("xslt.output.")) {
            s = s.substring(OutputPropertiesFactory.S_XSLT_PREFIX_LEN);
        }
        if (s.startsWith("org.apache.xslt.")) {
            s = "{http://xml.apache.org/xalan}" + s.substring(OutputPropertiesFactory.S_XALAN_PREFIX_LEN);
        }
        final int index;
        if ((index = s.indexOf("\\u003a")) > 0) {
            final String temp = s.substring(index + 6);
            s = s.substring(0, index) + ":" + temp;
        }
        return s;
    }
    
    static {
        S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL_LEN = "{http://xml.apache.org/xslt}".length();
        S_XSLT_PREFIX_LEN = "xslt.output.".length();
        S_XALAN_PREFIX_LEN = "org.apache.xslt.".length();
        OutputPropertiesFactory.m_synch_object = new Integer(1);
        OutputPropertiesFactory.m_xml_properties = null;
        OutputPropertiesFactory.m_html_properties = null;
        OutputPropertiesFactory.m_text_properties = null;
        OutputPropertiesFactory.m_unknown_properties = null;
        ACCESS_CONTROLLER_CLASS = findAccessControllerClass();
    }
}
