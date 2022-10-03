package com.adventnet.i18n;

import java.util.Hashtable;
import java.io.File;
import java.net.URLConnection;
import java.net.URL;
import java.io.Reader;
import java.util.PropertyResourceBundle;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;
import java.util.Vector;
import java.util.Locale;
import java.io.IOException;
import java.util.Iterator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.ResourceBundle;

public class MultiplePropertiesResourceBundleControl extends ResourceBundle.Control
{
    private static final String PERIOD = ".";
    private static final String ORDER_CONF_FILE;
    private static final Logger LOG;
    private static final byte[] NEWLINE;
    
    public static Map loadPropertiesFile(final String file) throws IOException {
        final Map<String, String> map = new TreeMap<String, String>();
        InputStream inputStream = null;
        final Properties prop = new Properties();
        inputStream = new FileInputStream(file);
        prop.load(inputStream);
        for (final String key : ((Hashtable<Object, V>)prop).keySet()) {
            final String value = ((Hashtable<K, String>)prop).get(key);
            map.put(key, value);
        }
        return map;
    }
    
    @Override
    public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        ResourceBundle bundle = null;
        final Map prop = loadPropertiesFile(MultiplePropertiesResourceBundleControl.ORDER_CONF_FILE);
        final Vector<InputStream> inputStreams = new Vector<InputStream>();
        int i = 1;
        while (true) {
            final String prefix = prop.get("order" + i++);
            if (prefix == null) {
                break;
            }
            final String newBaseName = prefix + "." + baseName;
            final InputStream inputStream = this.getInputStream(newBaseName, locale, loader, reload);
            if (inputStream == null) {
                continue;
            }
            inputStreams.add(inputStream);
            inputStreams.add(new ByteArrayInputStream(MultiplePropertiesResourceBundleControl.NEWLINE));
        }
        final SequenceInputStream stream = new SequenceInputStream(inputStreams.elements());
        if (stream != null) {
            try {
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            }
            finally {
                stream.close();
            }
        }
        return bundle;
    }
    
    public ResourceBundle newCombinedBundle(final Locale locale, final ClassLoader loader, final boolean reload) throws IOException {
        ResourceBundle bundle = null;
        final Map prop = loadPropertiesFile(MultiplePropertiesResourceBundleControl.ORDER_CONF_FILE);
        final Vector<InputStream> inputStreams = new Vector<InputStream>();
        int i = 1;
        final String[] baseNames = { "ApplicationResources", "JSApplicationResources" };
        while (true) {
            final String prefix = prop.get("order" + i++);
            if (prefix == null) {
                break;
            }
            for (final String baseName : baseNames) {
                final String newBaseName = prefix + "." + baseName;
                final InputStream inputStream = this.getInputStream(newBaseName, locale, loader, reload);
                if (inputStream != null) {
                    inputStreams.add(inputStream);
                    inputStreams.add(new ByteArrayInputStream(MultiplePropertiesResourceBundleControl.NEWLINE));
                }
            }
        }
        final String newBaseName2 = "resources.custom.ApplicationResources";
        final InputStream inputStream2 = this.getInputStream(newBaseName2, locale, loader, reload);
        if (inputStream2 != null) {
            inputStreams.add(inputStream2);
            inputStreams.add(new ByteArrayInputStream(MultiplePropertiesResourceBundleControl.NEWLINE));
        }
        final SequenceInputStream stream = new SequenceInputStream(inputStreams.elements());
        if (stream != null) {
            try {
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            }
            finally {
                stream.close();
            }
        }
        return bundle;
    }
    
    public InputStream getInputStream(final String baseName, final Locale locale, final ClassLoader loader, final boolean reload) throws IOException {
        final String bundleName = this.toBundleName(baseName, locale);
        final String resourceName = this.toResourceName(bundleName, "properties");
        InputStream stream = null;
        if (reload) {
            final URL url = loader.getResource(resourceName);
            if (url != null) {
                final URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }
        }
        else {
            stream = loader.getResourceAsStream(resourceName);
        }
        return stream;
    }
    
    static {
        ORDER_CONF_FILE = System.getProperty("server.home") + File.separator + "conf" + File.separator + "resourceBundleOrder.properties";
        LOG = Logger.getLogger(MultiplePropertiesResourceBundleControl.class.getName());
        NEWLINE = new byte[] { 10 };
    }
}
