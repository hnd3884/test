package javax.xml.validation;

import java.util.NoSuchElementException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.IOException;
import java.net.URL;
import java.io.InputStream;
import java.io.File;
import java.util.Properties;

final class SchemaFactoryFinder
{
    private static final String W3C_XML_SCHEMA10_NS_URI = "http://www.w3.org/XML/XMLSchema/v1.0";
    private static final String W3C_XML_SCHEMA11_NS_URI = "http://www.w3.org/XML/XMLSchema/v1.1";
    private static boolean debug;
    private static Properties cacheProps;
    private static boolean firstTime;
    private static final int DEFAULT_LINE_LENGTH = 80;
    private final ClassLoader classLoader;
    private static final Class SERVICE_CLASS;
    private static final String SERVICE_ID;
    
    private static void debugPrintln(final String s) {
        if (SchemaFactoryFinder.debug) {
            System.err.println("JAXP: " + s);
        }
    }
    
    public SchemaFactoryFinder(final ClassLoader classLoader) {
        this.classLoader = classLoader;
        if (SchemaFactoryFinder.debug) {
            this.debugDisplayClassLoader();
        }
    }
    
    private void debugDisplayClassLoader() {
        try {
            if (this.classLoader == SecuritySupport.getContextClassLoader()) {
                debugPrintln("using thread context class loader (" + this.classLoader + ") for search");
                return;
            }
        }
        catch (final VirtualMachineError virtualMachineError) {
            throw virtualMachineError;
        }
        catch (final ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (final Throwable t) {}
        if (this.classLoader == ClassLoader.getSystemClassLoader()) {
            debugPrintln("using system class loader (" + this.classLoader + ") for search");
            return;
        }
        debugPrintln("using class loader (" + this.classLoader + ") for search");
    }
    
    public SchemaFactory newFactory(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        final SchemaFactory newFactory = this._newFactory(s);
        if (SchemaFactoryFinder.debug) {
            if (newFactory != null) {
                debugPrintln("factory '" + newFactory.getClass().getName() + "' was found for " + s);
            }
            else {
                debugPrintln("unable to find a factory for " + s);
            }
        }
        return newFactory;
    }
    
    private SchemaFactory _newFactory(final String s) {
        final String string = SchemaFactoryFinder.SERVICE_CLASS.getName() + ":" + s;
        try {
            if (SchemaFactoryFinder.debug) {
                debugPrintln("Looking up system property '" + string + "'");
            }
            final String systemProperty = SecuritySupport.getSystemProperty(string);
            if (systemProperty != null && systemProperty.length() > 0) {
                if (SchemaFactoryFinder.debug) {
                    debugPrintln("The value is '" + systemProperty + "'");
                }
                final SchemaFactory instance = this.createInstance(systemProperty);
                if (instance != null) {
                    return instance;
                }
            }
            else if (SchemaFactoryFinder.debug) {
                debugPrintln("The property is undefined.");
            }
        }
        catch (final VirtualMachineError virtualMachineError) {
            throw virtualMachineError;
        }
        catch (final ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (final Throwable t) {
            if (SchemaFactoryFinder.debug) {
                debugPrintln("failed to look up system property '" + string + "'");
                t.printStackTrace();
            }
        }
        final String string2 = SecuritySupport.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
        try {
            if (SchemaFactoryFinder.firstTime) {
                synchronized (SchemaFactoryFinder.cacheProps) {
                    if (SchemaFactoryFinder.firstTime) {
                        final File file = new File(string2);
                        SchemaFactoryFinder.firstTime = false;
                        if (SecuritySupport.doesFileExist(file)) {
                            if (SchemaFactoryFinder.debug) {
                                debugPrintln("Read properties file " + file);
                            }
                            SchemaFactoryFinder.cacheProps.load(SecuritySupport.getFileInputStream(file));
                        }
                    }
                }
            }
            final String property = SchemaFactoryFinder.cacheProps.getProperty(string);
            if (SchemaFactoryFinder.debug) {
                debugPrintln("found " + property + " in $java.home/jaxp.properties");
            }
            if (property != null) {
                final SchemaFactory instance2 = this.createInstance(property);
                if (instance2 != null) {
                    return instance2;
                }
            }
        }
        catch (final Exception ex) {
            if (SchemaFactoryFinder.debug) {
                ex.printStackTrace();
            }
        }
        final Iterator serviceFileIterator = this.createServiceFileIterator();
        while (serviceFileIterator.hasNext()) {
            final URL url = serviceFileIterator.next();
            if (SchemaFactoryFinder.debug) {
                debugPrintln("looking into " + url);
            }
            try {
                final SchemaFactory loadFromServicesFile = this.loadFromServicesFile(s, url.toExternalForm(), SecuritySupport.getURLInputStream(url));
                if (loadFromServicesFile != null) {
                    return loadFromServicesFile;
                }
                continue;
            }
            catch (final IOException ex2) {
                if (!SchemaFactoryFinder.debug) {
                    continue;
                }
                debugPrintln("failed to read " + url);
                ex2.printStackTrace();
            }
        }
        if (s.equals("http://www.w3.org/2001/XMLSchema") || s.equals("http://www.w3.org/XML/XMLSchema/v1.0")) {
            if (SchemaFactoryFinder.debug) {
                debugPrintln("attempting to use the platform default XML Schema 1.0 validator");
            }
            return this.createInstance("org.apache.xerces.jaxp.validation.XMLSchemaFactory");
        }
        if (s.equals("http://www.w3.org/XML/XMLSchema/v1.1")) {
            if (SchemaFactoryFinder.debug) {
                debugPrintln("attempting to use the platform default XML Schema 1.1 validator");
            }
            return this.createInstance("org.apache.xerces.jaxp.validation.XMLSchema11Factory");
        }
        if (SchemaFactoryFinder.debug) {
            debugPrintln("all things were tried, but none was found. bailing out.");
        }
        return null;
    }
    
    SchemaFactory createInstance(final String s) {
        try {
            if (SchemaFactoryFinder.debug) {
                debugPrintln("instanciating " + s);
            }
            Class<?> clazz;
            if (this.classLoader != null) {
                clazz = this.classLoader.loadClass(s);
            }
            else {
                clazz = Class.forName(s);
            }
            if (SchemaFactoryFinder.debug) {
                debugPrintln("loaded it from " + which(clazz));
            }
            final Object instance = clazz.newInstance();
            if (instance instanceof SchemaFactory) {
                return (SchemaFactory)instance;
            }
            if (SchemaFactoryFinder.debug) {
                debugPrintln(s + " is not assignable to " + SchemaFactoryFinder.SERVICE_CLASS.getName());
            }
        }
        catch (final VirtualMachineError virtualMachineError) {
            throw virtualMachineError;
        }
        catch (final ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (final Throwable t) {
            debugPrintln("failed to instanciate " + s);
            if (SchemaFactoryFinder.debug) {
                t.printStackTrace();
            }
        }
        return null;
    }
    
    private Iterator createServiceFileIterator() {
        if (this.classLoader == null) {
            return new SingleIterator() {
                static /* synthetic */ Class class$javax$xml$validation$SchemaFactoryFinder;
                
                protected Object value() {
                    return SecuritySupport.getResourceAsURL(((SchemaFactoryFinder$1.class$javax$xml$validation$SchemaFactoryFinder == null) ? (SchemaFactoryFinder$1.class$javax$xml$validation$SchemaFactoryFinder = class$("javax.xml.validation.SchemaFactoryFinder")) : SchemaFactoryFinder$1.class$javax$xml$validation$SchemaFactoryFinder).getClassLoader(), SchemaFactoryFinder.SERVICE_ID);
                }
                
                static /* synthetic */ Class class$(final String s) {
                    try {
                        return Class.forName(s);
                    }
                    catch (final ClassNotFoundException ex) {
                        throw new NoClassDefFoundError(ex.getMessage());
                    }
                }
            };
        }
        try {
            final Enumeration resources = SecuritySupport.getResources(this.classLoader, SchemaFactoryFinder.SERVICE_ID);
            if (SchemaFactoryFinder.debug && !resources.hasMoreElements()) {
                debugPrintln("no " + SchemaFactoryFinder.SERVICE_ID + " file was found");
            }
            return new Iterator() {
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                
                public boolean hasNext() {
                    return resources.hasMoreElements();
                }
                
                public Object next() {
                    return resources.nextElement();
                }
            };
        }
        catch (final IOException ex) {
            if (SchemaFactoryFinder.debug) {
                debugPrintln("failed to enumerate resources " + SchemaFactoryFinder.SERVICE_ID);
                ex.printStackTrace();
            }
            return new ArrayList().iterator();
        }
    }
    
    private SchemaFactory loadFromServicesFile(final String s, final String s2, final InputStream inputStream) {
        if (SchemaFactoryFinder.debug) {
            debugPrintln("Reading " + s2);
        }
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 80);
        }
        catch (final UnsupportedEncodingException ex) {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 80);
        }
        SchemaFactory schemaFactory = null;
        while (true) {
            String s3;
            try {
                s3 = bufferedReader.readLine();
            }
            catch (final IOException ex2) {
                break;
            }
            if (s3 == null) {
                break;
            }
            final int index = s3.indexOf(35);
            if (index != -1) {
                s3 = s3.substring(0, index);
            }
            final String trim = s3.trim();
            if (trim.length() == 0) {
                continue;
            }
            try {
                final SchemaFactory instance = this.createInstance(trim);
                if (instance.isSchemaLanguageSupported(s)) {
                    schemaFactory = instance;
                    break;
                }
                continue;
            }
            catch (final Exception ex3) {}
        }
        try {
            bufferedReader.close();
        }
        catch (final IOException ex4) {}
        return schemaFactory;
    }
    
    private static String which(final Class clazz) {
        return which(clazz.getName(), clazz.getClassLoader());
    }
    
    private static String which(final String s, ClassLoader systemClassLoader) {
        final String string = s.replace('.', '/') + ".class";
        if (systemClassLoader == null) {
            systemClassLoader = ClassLoader.getSystemClassLoader();
        }
        final URL resourceAsURL = SecuritySupport.getResourceAsURL(systemClassLoader, string);
        if (resourceAsURL != null) {
            return resourceAsURL.toString();
        }
        return null;
    }
    
    static {
        SchemaFactoryFinder.debug = false;
        SchemaFactoryFinder.cacheProps = new Properties();
        SchemaFactoryFinder.firstTime = true;
        try {
            final String systemProperty = SecuritySupport.getSystemProperty("jaxp.debug");
            SchemaFactoryFinder.debug = (systemProperty != null && !"false".equals(systemProperty));
        }
        catch (final Exception ex) {
            SchemaFactoryFinder.debug = false;
        }
        SERVICE_CLASS = SchemaFactory.class;
        SERVICE_ID = "META-INF/services/" + SchemaFactoryFinder.SERVICE_CLASS.getName();
    }
    
    private abstract static class SingleIterator implements Iterator
    {
        private boolean seen;
        
        private SingleIterator() {
            this.seen = false;
        }
        
        public final void remove() {
            throw new UnsupportedOperationException();
        }
        
        public final boolean hasNext() {
            return !this.seen;
        }
        
        public final Object next() {
            if (this.seen) {
                throw new NoSuchElementException();
            }
            this.seen = true;
            return this.value();
        }
        
        protected abstract Object value();
    }
}
