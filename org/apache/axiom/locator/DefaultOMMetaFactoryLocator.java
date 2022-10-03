package org.apache.axiom.locator;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.OMMetaFactory;
import java.util.Enumeration;
import java.util.List;
import java.util.Collection;
import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.logging.Log;

public final class DefaultOMMetaFactoryLocator extends PriorityBasedOMMetaFactoryLocator
{
    private static final Log log;
    
    public DefaultOMMetaFactoryLocator() {
        ClassLoader classLoader = DefaultOMMetaFactoryLocator.class.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        final Loader loader = new DefaultLoader(classLoader);
        final List<Implementation> implementations = new ArrayList<Implementation>();
        String metaFactoryClassName = null;
        try {
            metaFactoryClassName = System.getProperty("org.apache.axiom.om.OMMetaFactory");
            if ("".equals(metaFactoryClassName)) {
                metaFactoryClassName = null;
            }
        }
        catch (final SecurityException ex2) {}
        if (metaFactoryClassName != null) {
            if (DefaultOMMetaFactoryLocator.log.isDebugEnabled()) {
                DefaultOMMetaFactoryLocator.log.debug((Object)("org.apache.axiom.om.OMMetaFactory system property is set; value=" + metaFactoryClassName));
            }
            final Implementation implementation = ImplementationFactory.createDefaultImplementation(loader, metaFactoryClassName);
            if (implementation != null) {
                implementations.add(implementation);
            }
        }
        DefaultOMMetaFactoryLocator.log.debug((Object)"Starting class path based discovery");
        Enumeration<URL> e;
        try {
            e = classLoader.getResources("META-INF/axiom.xml");
        }
        catch (final IOException ex) {
            DefaultOMMetaFactoryLocator.log.error((Object)"Failed to look up META-INF/axiom.xml from class loader", (Throwable)ex);
            e = null;
        }
        if (e != null) {
            while (e.hasMoreElements()) {
                implementations.addAll(ImplementationFactory.parseDescriptor(loader, e.nextElement()));
            }
        }
        this.loadImplementations(implementations);
    }
    
    static {
        log = LogFactory.getLog((Class)DefaultOMMetaFactoryLocator.class);
    }
}
