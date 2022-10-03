package com.adventnet.tools.prevalent;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Locale;
import java.util.PropertyResourceBundle;

class ToolsResourceBundle
{
    static String bundleName;
    private String fileName;
    public static String searchPath;
    private PropertyResourceBundle bundle;
    
    public ToolsResourceBundle(final Locale locale) {
        this.fileName = "";
        this.bundle = null;
        this.setBundle(locale);
    }
    
    void setBundle(final Locale locale) {
        final String localeSuffix = locale.toString();
        try {
            if (localeSuffix.length() > 0) {
                this.fileName = ToolsResourceBundle.bundleName + "_" + localeSuffix + ".properties";
            }
            else if (locale.getVariant().length() > 0) {
                this.fileName = ToolsResourceBundle.bundleName + "___" + ".properties";
            }
            else if (localeSuffix.length() == 0) {
                this.fileName = ToolsResourceBundle.bundleName + ".properties";
            }
            if (!ToolsResourceBundle.searchPath.equals("") && !ToolsResourceBundle.searchPath.endsWith(File.separator)) {
                ToolsResourceBundle.searchPath += File.separator;
            }
            File file = new File(ToolsResourceBundle.searchPath + this.fileName);
            if (!file.exists()) {
                file = new File(ToolsResourceBundle.searchPath + ToolsResourceBundle.bundleName + ".properties");
            }
            final InputStream stream = new FileInputStream(file);
            final InputStream trimmedstream = this.getTrimmedStream(stream);
            if (trimmedstream != null) {
                this.bundle = new PropertyResourceBundle(trimmedstream);
            }
        }
        catch (final Exception ex) {}
    }
    
    private InputStream getTrimmedStream(final InputStream inStream) {
        try {
            final Properties p = new Properties();
            p.load(inStream);
            final ByteArrayOutputStream boa = new ByteArrayOutputStream();
            final Enumeration en = p.keys();
            while (en.hasMoreElements()) {
                final Object key = en.nextElement();
                final Object value = ((Hashtable<K, Object>)p).get(key);
                if (value == null || value.toString().equals("")) {
                    p.remove(key);
                }
            }
            p.store(boa, ToolsResourceBundle.bundleName + ".properties");
            final byte[] buf = boa.toByteArray();
            return new ByteArrayInputStream(buf);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public String returnString(final String key) {
        return this.bundle.getString(key);
    }
    
    static {
        ToolsResourceBundle.bundleName = "Tools";
        ToolsResourceBundle.searchPath = "";
    }
}
