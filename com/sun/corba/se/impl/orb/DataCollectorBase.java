package com.sun.corba.se.impl.orb;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.net.MalformedURLException;
import java.net.URL;
import java.applet.Applet;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orb.DataCollector;

public abstract class DataCollectorBase implements DataCollector
{
    private PropertyParser parser;
    private Set propertyNames;
    private Set propertyPrefixes;
    private Set URLPropertyNames;
    protected String localHostName;
    protected String configurationHostName;
    private boolean setParserCalled;
    private Properties originalProps;
    private Properties resultProps;
    
    public DataCollectorBase(final Properties originalProps, final String localHostName, final String configurationHostName) {
        (this.URLPropertyNames = new HashSet()).add("org.omg.CORBA.ORBInitialServices");
        (this.propertyNames = new HashSet()).add("org.omg.CORBA.ORBInitRef");
        this.propertyPrefixes = new HashSet();
        this.originalProps = originalProps;
        this.localHostName = localHostName;
        this.configurationHostName = configurationHostName;
        this.setParserCalled = false;
        this.resultProps = new Properties();
    }
    
    @Override
    public boolean initialHostIsLocal() {
        this.checkSetParserCalled();
        return this.localHostName.equals(this.resultProps.getProperty("org.omg.CORBA.ORBInitialHost"));
    }
    
    @Override
    public void setParser(final PropertyParser propertyParser) {
        for (final ParserAction parserAction : propertyParser) {
            if (parserAction.isPrefix()) {
                this.propertyPrefixes.add(parserAction.getPropertyName());
            }
            else {
                this.propertyNames.add(parserAction.getPropertyName());
            }
        }
        this.collect();
        this.setParserCalled = true;
    }
    
    @Override
    public Properties getProperties() {
        this.checkSetParserCalled();
        return this.resultProps;
    }
    
    @Override
    public abstract boolean isApplet();
    
    protected abstract void collect();
    
    protected void checkPropertyDefaults() {
        final String property = this.resultProps.getProperty("org.omg.CORBA.ORBInitialHost");
        if (property == null || property.equals("")) {
            this.setProperty("org.omg.CORBA.ORBInitialHost", this.configurationHostName);
        }
        final String property2 = this.resultProps.getProperty("com.sun.CORBA.ORBServerHost");
        if (property2 == null || property2.equals("") || property2.equals("0.0.0.0") || property2.equals("::") || property2.toLowerCase().equals("::ffff:0.0.0.0")) {
            this.setProperty("com.sun.CORBA.ORBServerHost", this.localHostName);
            this.setProperty("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", "com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces");
        }
    }
    
    protected void findPropertiesFromArgs(final String[] array) {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            String s = null;
            String matchingPropertyName = null;
            if (array[i] != null && array[i].startsWith("-ORB")) {
                matchingPropertyName = this.findMatchingPropertyName(this.propertyNames, array[i].substring(1));
                if (matchingPropertyName != null && i + 1 < array.length && array[i + 1] != null) {
                    s = array[++i];
                }
            }
            if (s != null) {
                this.setProperty(matchingPropertyName, s);
            }
        }
    }
    
    protected void findPropertiesFromApplet(final Applet applet) {
        if (applet == null) {
            return;
        }
        this.findPropertiesByName(this.propertyNames.iterator(), new PropertyCallback() {
            @Override
            public String get(final String s) {
                return applet.getParameter(s);
            }
        });
        this.findPropertiesByName(this.URLPropertyNames.iterator(), new PropertyCallback() {
            @Override
            public String get(final String s) {
                final String property = DataCollectorBase.this.resultProps.getProperty(s);
                if (property == null) {
                    return null;
                }
                try {
                    return new URL(applet.getDocumentBase(), property).toExternalForm();
                }
                catch (final MalformedURLException ex) {
                    return property;
                }
            }
        });
    }
    
    private void doProperties(final Properties properties) {
        final PropertyCallback propertyCallback = new PropertyCallback() {
            @Override
            public String get(final String s) {
                return properties.getProperty(s);
            }
        };
        this.findPropertiesByName(this.propertyNames.iterator(), propertyCallback);
        this.findPropertiesByPrefix(this.propertyPrefixes, makeIterator(properties.propertyNames()), propertyCallback);
    }
    
    protected void findPropertiesFromFile() {
        final Properties fileProperties = this.getFileProperties();
        if (fileProperties == null) {
            return;
        }
        this.doProperties(fileProperties);
    }
    
    protected void findPropertiesFromProperties() {
        if (this.originalProps == null) {
            return;
        }
        this.doProperties(this.originalProps);
    }
    
    protected void findPropertiesFromSystem() {
        final Set corbaPrefixes = this.getCORBAPrefixes(this.propertyNames);
        final Set corbaPrefixes2 = this.getCORBAPrefixes(this.propertyPrefixes);
        final PropertyCallback propertyCallback = new PropertyCallback() {
            @Override
            public String get(final String s) {
                return getSystemProperty(s);
            }
        };
        this.findPropertiesByName(corbaPrefixes.iterator(), propertyCallback);
        this.findPropertiesByPrefix(corbaPrefixes2, getSystemPropertyNames(), propertyCallback);
    }
    
    private void setProperty(final String s, final String s2) {
        if (s.equals("org.omg.CORBA.ORBInitRef")) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s2, "=");
            if (stringTokenizer.countTokens() != 2) {
                throw new IllegalArgumentException();
            }
            this.resultProps.setProperty(s + "." + stringTokenizer.nextToken(), stringTokenizer.nextToken());
        }
        else {
            this.resultProps.setProperty(s, s2);
        }
    }
    
    private void checkSetParserCalled() {
        if (!this.setParserCalled) {
            throw new IllegalStateException("setParser not called.");
        }
    }
    
    private void findPropertiesByPrefix(final Set set, final Iterator iterator, final PropertyCallback propertyCallback) {
        while (iterator.hasNext()) {
            final String s = iterator.next();
            final Iterator iterator2 = set.iterator();
            while (iterator2.hasNext()) {
                if (s.startsWith((String)iterator2.next())) {
                    this.setProperty(s, propertyCallback.get(s));
                }
            }
        }
    }
    
    private void findPropertiesByName(final Iterator iterator, final PropertyCallback propertyCallback) {
        while (iterator.hasNext()) {
            final String s = iterator.next();
            final String value = propertyCallback.get(s);
            if (value != null) {
                this.setProperty(s, value);
            }
        }
    }
    
    private static String getSystemProperty(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(s));
    }
    
    private String findMatchingPropertyName(final Set set, final String s) {
        for (final String s2 : set) {
            if (s2.endsWith(s)) {
                return s2;
            }
        }
        return null;
    }
    
    private static Iterator makeIterator(final Enumeration enumeration) {
        return new Iterator() {
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }
            
            @Override
            public Object next() {
                return enumeration.nextElement();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private static Iterator getSystemPropertyNames() {
        return makeIterator(AccessController.doPrivileged((PrivilegedAction<Enumeration>)new PrivilegedAction() {
            @Override
            public Object run() {
                return System.getProperties().propertyNames();
            }
        }));
    }
    
    private void getPropertiesFromFile(final Properties properties, final String s) {
        try {
            final File file = new File(s);
            if (!file.exists()) {
                return;
            }
            final FileInputStream fileInputStream = new FileInputStream(file);
            try {
                properties.load(fileInputStream);
            }
            finally {
                fileInputStream.close();
            }
        }
        catch (final Exception ex) {}
    }
    
    private Properties getFileProperties() {
        final Properties properties = new Properties();
        this.getPropertiesFromFile(properties, getSystemProperty("java.home") + File.separator + "lib" + File.separator + "orb.properties");
        final Properties properties2 = new Properties(properties);
        this.getPropertiesFromFile(properties2, getSystemProperty("user.home") + File.separator + "orb.properties");
        return properties2;
    }
    
    private boolean hasCORBAPrefix(final String s) {
        return s.startsWith("org.omg.") || s.startsWith("com.sun.CORBA.") || s.startsWith("com.sun.corba.") || s.startsWith("com.sun.corba.se.");
    }
    
    private Set getCORBAPrefixes(final Set set) {
        final HashSet set2 = new HashSet();
        for (final String s : set) {
            if (this.hasCORBAPrefix(s)) {
                set2.add(s);
            }
        }
        return set2;
    }
}
