package com.adventnet.management.i18n;

import java.util.Hashtable;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.applet.Applet;
import java.io.IOException;
import java.util.Enumeration;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class AdventNetResourceBundle extends ResourceBundle
{
    private static PropertyResourceBundle resBund;
    private static AdventNetResourceBundle adventNetResourceBindle;
    
    public static AdventNetResourceBundle getInstance() {
        return AdventNetResourceBundle.adventNetResourceBindle;
    }
    
    public AdventNetResourceBundle(final InputStream inputStream) throws IOException {
        if (inputStream != null && AdventNetResourceBundle.resBund == null) {
            final Properties properties = new Properties();
            properties.load(inputStream);
            final Properties properties2 = new Properties();
            final Enumeration<?> propertyNames = properties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String substring = (String)propertyNames.nextElement();
                final String property = properties.getProperty(substring);
                if (substring.indexOf("<PROMINENT_KEY>") != -1) {
                    substring = substring.substring(substring.indexOf("<PROMINENT_KEY>") + 15);
                }
                if (!property.equals("")) {
                    ((Hashtable<String, String>)properties2).put(substring, property);
                }
            }
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            properties2.store(byteArrayOutputStream, "null");
            AdventNetResourceBundle.resBund = new PropertyResourceBundle(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        }
        AdventNetResourceBundle.adventNetResourceBindle = this;
    }
    
    public AdventNetResourceBundle(final Applet applet) {
        if (AdventNetResourceBundle.resBund != null) {
            return;
        }
        final String s = "EnglishToNative";
        String s2 = "";
        final String string = s + ".properties";
        if (applet != null) {
            if (applet.getParameter("COUNTRY") != null && applet.getParameter("LANGUAGE") != null) {
                s2 = s + "_" + applet.getParameter("LANGUAGE").trim() + "_" + applet.getParameter("COUNTRY").trim() + ".properties";
            }
            else {
                s2 = s + ".properties";
            }
        }
        try {
            if (applet != null) {
                final URL url = new URL(applet.getDocumentBase(), applet.getParameter("SEARCH_PATH") + "/" + s2);
                InputStream inputStream;
                try {
                    if (((HttpURLConnection)url.openConnection()).getResponseCode() == 200) {
                        inputStream = url.openStream();
                    }
                    else {
                        inputStream = this.getStream(new URL(applet.getDocumentBase(), "../html/" + string));
                    }
                }
                catch (final Exception ex) {
                    inputStream = this.getStream(new URL(applet.getDocumentBase(), "../html/" + string));
                }
                try {
                    if (inputStream != null && AdventNetResourceBundle.resBund == null) {
                        final Properties properties = new Properties();
                        properties.load(inputStream);
                        final Properties properties2 = new Properties();
                        final Enumeration<?> propertyNames = properties.propertyNames();
                        while (propertyNames.hasMoreElements()) {
                            String substring = (String)propertyNames.nextElement();
                            final String property = properties.getProperty(substring);
                            if (substring.indexOf("<PROMINENT_KEY>") != -1) {
                                substring = substring.substring(substring.indexOf("<PROMINENT_KEY>") + 15);
                            }
                            if (!property.equals("")) {
                                ((Hashtable<String, String>)properties2).put(substring.trim(), property);
                            }
                        }
                        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        properties2.store(byteArrayOutputStream, "null");
                        AdventNetResourceBundle.resBund = new PropertyResourceBundle(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                    }
                }
                catch (final Exception ex2) {
                    ex2.printStackTrace();
                }
            }
        }
        catch (final Exception ex3) {
            System.err.println("Unable to initialize ResourceBundle ...");
            ex3.printStackTrace();
        }
    }
    
    public Object handleGetObject(final String s) {
        if (AdventNetResourceBundle.resBund == null) {
            return s;
        }
        if (AdventNetResourceBundle.resBund.handleGetObject(s) == null || "".equals(AdventNetResourceBundle.resBund.handleGetObject(s))) {
            return s;
        }
        return AdventNetResourceBundle.resBund.handleGetObject(s);
    }
    
    public Enumeration getKeys() {
        return AdventNetResourceBundle.resBund.getKeys();
    }
    
    private InputStream getStream(final URL url) {
        try {
            if (((HttpURLConnection)url.openConnection()).getResponseCode() == 200) {
                return url.openStream();
            }
            System.err.println("There is no .properties file for internationalization. This will result in no internationalization");
            return null;
        }
        catch (final MalformedURLException ex) {
            return null;
        }
        catch (final IOException ex2) {
            if (ex2 instanceof FileNotFoundException) {
                System.err.println("File html/EnglishToNative.properties not found. This will result in no internationalization");
            }
            return null;
        }
    }
    
    static {
        AdventNetResourceBundle.resBund = null;
        AdventNetResourceBundle.adventNetResourceBindle = null;
    }
}
