package com.sun.xml.internal.ws.spi.db;

import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.ws.db.glassfish.JAXBRIContextFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.Iterator;
import java.util.logging.Logger;

public abstract class BindingContextFactory
{
    public static final String DefaultDatabindingMode = "glassfish.jaxb";
    public static final String JAXB_CONTEXT_FACTORY_PROPERTY;
    public static final Logger LOGGER;
    
    public static Iterator<BindingContextFactory> serviceIterator() {
        final ServiceFinder<BindingContextFactory> sf = ServiceFinder.find(BindingContextFactory.class);
        final Iterator<BindingContextFactory> ibcf = sf.iterator();
        return new Iterator<BindingContextFactory>() {
            private BindingContextFactory bcf;
            
            @Override
            public boolean hasNext() {
                while (true) {
                    try {
                        if (ibcf.hasNext()) {
                            this.bcf = ibcf.next();
                            return true;
                        }
                        return false;
                    }
                    catch (final ServiceConfigurationError e) {
                        BindingContextFactory.LOGGER.warning("skipping factory: ServiceConfigurationError: " + e.getMessage());
                        continue;
                    }
                    catch (final NoClassDefFoundError ncdfe) {
                        BindingContextFactory.LOGGER.fine("skipping factory: NoClassDefFoundError: " + ncdfe.getMessage());
                        continue;
                    }
                    break;
                }
            }
            
            @Override
            public BindingContextFactory next() {
                if (BindingContextFactory.LOGGER.isLoggable(Level.FINER)) {
                    BindingContextFactory.LOGGER.finer("SPI found provider: " + this.bcf.getClass().getName());
                }
                return this.bcf;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private static List<BindingContextFactory> factories() {
        final List<BindingContextFactory> factories = new ArrayList<BindingContextFactory>();
        final Iterator<BindingContextFactory> ibcf = serviceIterator();
        while (ibcf.hasNext()) {
            factories.add(ibcf.next());
        }
        if (factories.isEmpty()) {
            if (BindingContextFactory.LOGGER.isLoggable(Level.FINER)) {
                BindingContextFactory.LOGGER.log(Level.FINER, "No SPI providers for BindingContextFactory found, adding: " + JAXBRIContextFactory.class.getName());
            }
            factories.add(new JAXBRIContextFactory());
        }
        return factories;
    }
    
    protected abstract BindingContext newContext(final JAXBContext p0);
    
    protected abstract BindingContext newContext(final BindingInfo p0);
    
    protected abstract boolean isFor(final String p0);
    
    @Deprecated
    protected abstract BindingContext getContext(final Marshaller p0);
    
    private static BindingContextFactory getFactory(final String mode) {
        for (final BindingContextFactory f : factories()) {
            if (f.isFor(mode)) {
                return f;
            }
        }
        return null;
    }
    
    public static BindingContext create(final JAXBContext context) throws DatabindingException {
        return getJAXBFactory(context).newContext(context);
    }
    
    public static BindingContext create(final BindingInfo bi) {
        String mode = bi.getDatabindingMode();
        if (mode != null) {
            if (BindingContextFactory.LOGGER.isLoggable(Level.FINE)) {
                BindingContextFactory.LOGGER.log(Level.FINE, "Using SEI-configured databindng mode: " + mode);
            }
        }
        else if ((mode = System.getProperty("BindingContextFactory")) != null) {
            bi.setDatabindingMode(mode);
            if (BindingContextFactory.LOGGER.isLoggable(Level.FINE)) {
                BindingContextFactory.LOGGER.log(Level.FINE, "Using databindng: " + mode + " based on 'BindingContextFactory' System property");
            }
        }
        else if ((mode = System.getProperty(BindingContextFactory.JAXB_CONTEXT_FACTORY_PROPERTY)) != null) {
            bi.setDatabindingMode(mode);
            if (BindingContextFactory.LOGGER.isLoggable(Level.FINE)) {
                BindingContextFactory.LOGGER.log(Level.FINE, "Using databindng: " + mode + " based on '" + BindingContextFactory.JAXB_CONTEXT_FACTORY_PROPERTY + "' System property");
            }
        }
        else {
            final Iterator<BindingContextFactory> iterator = factories().iterator();
            if (iterator.hasNext()) {
                final BindingContextFactory factory = iterator.next();
                if (BindingContextFactory.LOGGER.isLoggable(Level.FINE)) {
                    BindingContextFactory.LOGGER.log(Level.FINE, "Using SPI-determined databindng mode: " + factory.getClass().getName());
                }
                return factory.newContext(bi);
            }
            BindingContextFactory.LOGGER.log(Level.SEVERE, "No Binding Context Factories found.");
            throw new DatabindingException("No Binding Context Factories found.");
        }
        final BindingContextFactory f = getFactory(mode);
        if (f != null) {
            return f.newContext(bi);
        }
        BindingContextFactory.LOGGER.severe("Unknown Databinding mode: " + mode);
        throw new DatabindingException("Unknown Databinding mode: " + mode);
    }
    
    public static boolean isContextSupported(final Object o) {
        if (o == null) {
            return false;
        }
        final String pkgName = o.getClass().getPackage().getName();
        for (final BindingContextFactory f : factories()) {
            if (f.isFor(pkgName)) {
                return true;
            }
        }
        return false;
    }
    
    static BindingContextFactory getJAXBFactory(final Object o) {
        final String pkgName = o.getClass().getPackage().getName();
        final BindingContextFactory f = getFactory(pkgName);
        if (f != null) {
            return f;
        }
        throw new DatabindingException("Unknown JAXBContext implementation: " + o.getClass());
    }
    
    @Deprecated
    public static BindingContext getBindingContext(final Marshaller m) {
        return getJAXBFactory(m).getContext(m);
    }
    
    static {
        JAXB_CONTEXT_FACTORY_PROPERTY = BindingContextFactory.class.getName();
        LOGGER = Logger.getLogger(BindingContextFactory.class.getName());
    }
}
