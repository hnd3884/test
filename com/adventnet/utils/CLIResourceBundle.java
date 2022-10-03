package com.adventnet.utils;

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

class CLIResourceBundle
{
    static String bundleName;
    private String fileName;
    public static String searchPath;
    private PropertyResourceBundle bundle;
    
    public CLIResourceBundle(final Locale bundle) {
        this.fileName = "";
        this.bundle = null;
        this.setBundle(bundle);
    }
    
    void setBundle(final Locale locale) {
        final String string = locale.toString();
        try {
            if (string.length() > 0) {
                this.fileName = CLIResourceBundle.bundleName + "_" + string + ".properties";
            }
            else if (locale.getVariant().length() > 0) {
                this.fileName = CLIResourceBundle.bundleName + "___" + ".properties";
            }
            else if (string.length() == 0) {
                this.fileName = CLIResourceBundle.bundleName + ".properties";
            }
            if (!CLIResourceBundle.searchPath.equals("") && !CLIResourceBundle.searchPath.endsWith(File.separator)) {
                CLIResourceBundle.searchPath += File.separator;
            }
            File file = new File(CLIResourceBundle.searchPath + this.fileName);
            if (!file.exists()) {
                file = new File(CLIResourceBundle.searchPath + CLIResourceBundle.bundleName + ".properties");
            }
            final InputStream trimmedStream = this.getTrimmedStream(new FileInputStream(file));
            if (trimmedStream != null) {
                this.bundle = new PropertyResourceBundle(trimmedStream);
            }
        }
        catch (final Exception ex) {
            System.out.println("The Internationalization could not done due this exception " + ex);
        }
    }
    
    private InputStream getTrimmedStream(final InputStream inputStream) {
        try {
            final Properties properties = new Properties();
            properties.load(inputStream);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final Enumeration<Object> keys = ((Hashtable<Object, V>)properties).keys();
            while (keys.hasMoreElements()) {
                final Object nextElement = keys.nextElement();
                final Object value = properties.get(nextElement);
                if (value == null || value.toString().equals("")) {
                    properties.remove(nextElement);
                }
            }
            properties.store(byteArrayOutputStream, CLIResourceBundle.bundleName + ".properties");
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public String returnString(final String s) {
        return this.bundle.getString(s);
    }
    
    static {
        CLIResourceBundle.bundleName = "CLIBrowser";
        CLIResourceBundle.searchPath = "";
    }
}
