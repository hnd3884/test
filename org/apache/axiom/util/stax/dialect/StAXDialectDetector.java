package org.apache.axiom.util.stax.dialect;

import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import java.io.InputStream;
import java.util.Locale;
import java.io.IOException;
import java.util.jar.Manifest;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.jar.Attributes;
import org.apache.commons.logging.Log;

public class StAXDialectDetector
{
    private static final Log log;
    private static final Attributes.Name IMPLEMENTATION_TITLE;
    private static final Attributes.Name IMPLEMENTATION_VENDOR;
    private static final Attributes.Name IMPLEMENTATION_VERSION;
    private static final Attributes.Name BUNDLE_SYMBOLIC_NAME;
    private static final Attributes.Name BUNDLE_VENDOR;
    private static final Attributes.Name BUNDLE_VERSION;
    private static final JBossFactoryUnwrapper jbossXMLInputFactoryUnwrapper;
    private static final JBossFactoryUnwrapper jbossXMLOutputFactoryUnwrapper;
    private static final Map dialectByUrl;
    
    private StAXDialectDetector() {
    }
    
    private static URL getRootUrlForResource(ClassLoader classLoader, final String resource) {
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        final URL url = classLoader.getResource(resource);
        if (url == null) {
            return null;
        }
        final String file = url.getFile();
        if (file.endsWith(resource)) {
            try {
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), file.substring(0, file.length() - resource.length()));
            }
            catch (final MalformedURLException ex) {
                return null;
            }
        }
        return null;
    }
    
    private static URL getRootUrlForClass(final Class cls) {
        return getRootUrlForResource(cls.getClassLoader(), cls.getName().replace('.', '/') + ".class");
    }
    
    public static XMLInputFactory normalize(final XMLInputFactory factory) {
        return getDialect(factory).normalize(factory);
    }
    
    public static XMLOutputFactory normalize(final XMLOutputFactory factory) {
        return getDialect(factory).normalize(factory);
    }
    
    public static StAXDialect getDialect(final Class implementationClass) {
        final URL rootUrl = getRootUrlForClass(implementationClass);
        if (rootUrl == null) {
            StAXDialectDetector.log.warn((Object)("Unable to determine location of StAX implementation containing class " + implementationClass.getName() + "; using default dialect"));
            return UnknownStAXDialect.INSTANCE;
        }
        return getDialect(implementationClass.getClassLoader(), rootUrl);
    }
    
    public static StAXDialect getDialect(XMLInputFactory factory) {
        if (StAXDialectDetector.jbossXMLInputFactoryUnwrapper != null) {
            factory = (XMLInputFactory)StAXDialectDetector.jbossXMLInputFactoryUnwrapper.unwrap(factory);
        }
        return getDialect(factory.getClass());
    }
    
    public static StAXDialect getDialect(XMLOutputFactory factory) {
        if (StAXDialectDetector.jbossXMLOutputFactoryUnwrapper != null) {
            factory = (XMLOutputFactory)StAXDialectDetector.jbossXMLOutputFactoryUnwrapper.unwrap(factory);
        }
        return getDialect(factory.getClass());
    }
    
    private static StAXDialect getDialect(final ClassLoader classLoader, final URL rootUrl) {
        StAXDialect dialect = StAXDialectDetector.dialectByUrl.get(rootUrl);
        if (dialect != null) {
            return dialect;
        }
        dialect = detectDialect(classLoader, rootUrl);
        StAXDialectDetector.dialectByUrl.put(rootUrl, dialect);
        return dialect;
    }
    
    private static StAXDialect detectDialect(final ClassLoader classLoader, final URL rootUrl) {
        StAXDialect dialect = detectDialectFromJarManifest(rootUrl);
        if (dialect == null) {
            dialect = detectDialectFromClasses(classLoader, rootUrl);
        }
        if (dialect == null) {
            StAXDialectDetector.log.warn((Object)("Unable to determine dialect of the StAX implementation at " + rootUrl));
            return UnknownStAXDialect.INSTANCE;
        }
        if (StAXDialectDetector.log.isDebugEnabled()) {
            StAXDialectDetector.log.debug((Object)("Detected StAX dialect: " + dialect.getName()));
        }
        return dialect;
    }
    
    private static StAXDialect detectDialectFromJarManifest(final URL rootUrl) {
        if (rootUrl.getProtocol().equals("jrt")) {
            return null;
        }
        Manifest manifest;
        try {
            final URL metaInfUrl = new URL(rootUrl, "META-INF/MANIFEST.MF");
            final InputStream is = metaInfUrl.openStream();
            try {
                manifest = new Manifest(is);
            }
            finally {
                is.close();
            }
        }
        catch (final IOException ex) {
            StAXDialectDetector.log.warn((Object)("Unable to load manifest for StAX implementation at " + rootUrl));
            return UnknownStAXDialect.INSTANCE;
        }
        final Attributes attrs = manifest.getMainAttributes();
        final String title = attrs.getValue(StAXDialectDetector.IMPLEMENTATION_TITLE);
        String symbolicName = attrs.getValue(StAXDialectDetector.BUNDLE_SYMBOLIC_NAME);
        if (symbolicName != null) {
            final int i = symbolicName.indexOf(59);
            if (i != -1) {
                symbolicName = symbolicName.substring(0, i);
            }
        }
        String vendor = attrs.getValue(StAXDialectDetector.IMPLEMENTATION_VENDOR);
        if (vendor == null) {
            vendor = attrs.getValue(StAXDialectDetector.BUNDLE_VENDOR);
        }
        String versionString = attrs.getValue(StAXDialectDetector.IMPLEMENTATION_VERSION);
        if (versionString == null) {
            versionString = attrs.getValue(StAXDialectDetector.BUNDLE_VERSION);
        }
        if (StAXDialectDetector.log.isDebugEnabled()) {
            StAXDialectDetector.log.debug((Object)("StAX implementation at " + rootUrl + " is:\n  Title:         " + title + "\n  Symbolic name: " + symbolicName + "\n  Vendor:        " + vendor + "\n  Version:       " + versionString));
        }
        if (title != null && title.toLowerCase(Locale.ENGLISH).contains("woodstox")) {
            final Version version = new Version(versionString);
            switch (version.getComponent(0)) {
                case 3: {
                    return Woodstox3Dialect.INSTANCE;
                }
                case 4: {
                    return new Woodstox4Dialect((version.getComponent(1) == 0 && version.getComponent(2) < 11) || (version.getComponent(1) == 1 && version.getComponent(2) < 3));
                }
                case 5: {
                    return new Woodstox4Dialect(false);
                }
                default: {
                    return null;
                }
            }
        }
        else {
            if (title != null && title.indexOf("SJSXP") != -1) {
                return new SJSXPDialect(false);
            }
            if ("com.bea.core.weblogic.stax".equals(symbolicName)) {
                StAXDialectDetector.log.warn((Object)"Weblogic's StAX implementation is unsupported and some Axiom features will not work as expected! Please use Woodstox instead.");
                return BEADialect.INSTANCE;
            }
            if ("BEA".equals(vendor)) {
                return BEADialect.INSTANCE;
            }
            if ("com.ibm.ws.prereq.banshee".equals(symbolicName)) {
                return XLXP2Dialect.INSTANCE;
            }
            return null;
        }
    }
    
    private static Class loadClass(ClassLoader classLoader, final URL rootUrl, final String name) {
        try {
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            final Class cls = classLoader.loadClass(name);
            return rootUrl.equals(getRootUrlForClass(cls)) ? cls : null;
        }
        catch (final ClassNotFoundException ex) {
            return null;
        }
    }
    
    private static StAXDialect detectDialectFromClasses(final ClassLoader classLoader, final URL rootUrl) {
        Class cls = loadClass(classLoader, rootUrl, "com.sun.xml.internal.stream.XMLOutputFactoryImpl");
        Label_0063: {
            if (cls != null) {
                final Class superClass = cls.getSuperclass();
                if (superClass != XMLOutputFactory.class) {
                    if (!superClass.getName().startsWith("com.sun.")) {
                        break Label_0063;
                    }
                }
                boolean isUnsafeStreamResult;
                try {
                    cls.getDeclaredField("fStreamResult");
                    isUnsafeStreamResult = true;
                }
                catch (final NoSuchFieldException ex) {
                    isUnsafeStreamResult = false;
                }
                return new SJSXPDialect(isUnsafeStreamResult);
            }
        }
        cls = loadClass(classLoader, rootUrl, "com.ibm.xml.xlxp.api.stax.StAXImplConstants");
        if (cls != null) {
            boolean isSetPrefixBroken;
            try {
                cls.getField("IS_SETPREFIX_BEFORE_STARTELEMENT");
                isSetPrefixBroken = false;
            }
            catch (final NoSuchFieldException ex2) {
                isSetPrefixBroken = true;
            }
            return new XLXP1Dialect(isSetPrefixBroken);
        }
        cls = loadClass(classLoader, rootUrl, "com.ibm.xml.xlxp2.api.stax.StAXImplConstants");
        if (cls != null) {
            return new XLXP2Dialect();
        }
        return null;
    }
    
    static {
        log = LogFactory.getLog((Class)StAXDialectDetector.class);
        IMPLEMENTATION_TITLE = new Attributes.Name("Implementation-Title");
        IMPLEMENTATION_VENDOR = new Attributes.Name("Implementation-Vendor");
        IMPLEMENTATION_VERSION = new Attributes.Name("Implementation-Version");
        BUNDLE_SYMBOLIC_NAME = new Attributes.Name("Bundle-SymbolicName");
        BUNDLE_VENDOR = new Attributes.Name("Bundle-Vendor");
        BUNDLE_VERSION = new Attributes.Name("Bundle-Version");
        jbossXMLInputFactoryUnwrapper = JBossFactoryUnwrapper.create(XMLInputFactory.class);
        jbossXMLOutputFactoryUnwrapper = JBossFactoryUnwrapper.create(XMLOutputFactory.class);
        dialectByUrl = Collections.synchronizedMap(new HashMap<Object, Object>());
    }
}
