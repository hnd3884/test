package org.apache.axiom.locator;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import java.util.Iterator;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Collection;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Bundle;
import org.apache.axiom.om.OMMetaFactory;
import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.BundleTrackerCustomizer;

final class OSGiOMMetaFactoryLocator extends PriorityBasedOMMetaFactoryLocator implements BundleTrackerCustomizer
{
    private final BundleContext apiBundleContext;
    private final List<Implementation> implementations;
    
    OSGiOMMetaFactoryLocator(final BundleContext apiBundleContext) {
        this.implementations = new ArrayList<Implementation>();
        this.apiBundleContext = apiBundleContext;
    }
    
    @Override
    public synchronized OMMetaFactory getOMMetaFactory(final String feature) {
        return super.getOMMetaFactory(feature);
    }
    
    public Object addingBundle(final Bundle bundle, final BundleEvent event) {
        final URL descriptorUrl = bundle.getEntry("META-INF/axiom.xml");
        if (descriptorUrl != null) {
            final List<Implementation> discoveredImplementations = ImplementationFactory.parseDescriptor(new OSGiLoader(bundle), descriptorUrl);
            final List<RegisteredImplementation> registeredImplementations = new ArrayList<RegisteredImplementation>(discoveredImplementations.size());
            synchronized (this) {
                this.implementations.addAll(discoveredImplementations);
                this.loadImplementations(this.implementations);
            }
            for (final Implementation implementation : discoveredImplementations) {
                final Hashtable<String, String> properties = new Hashtable<String, String>();
                properties.put("implementationName", implementation.getName());
                final ServiceRegistration registration = bundle.getBundleContext().registerService(OMMetaFactory.class.getName(), (Object)implementation.getMetaFactory(), (Dictionary)properties);
                final ServiceReference reference = registration.getReference();
                this.apiBundleContext.getService(reference);
                registeredImplementations.add(new RegisteredImplementation(implementation, registration, reference));
            }
            return registeredImplementations;
        }
        return null;
    }
    
    public void modifiedBundle(final Bundle bundle, final BundleEvent event, final Object object) {
    }
    
    public void removedBundle(final Bundle bundle, final BundleEvent event, final Object object) {
        for (final RegisteredImplementation registeredImplementation : (List)object) {
            this.apiBundleContext.ungetService(registeredImplementation.getReference());
            registeredImplementation.getRegistration().unregister();
            synchronized (this) {
                this.implementations.remove(registeredImplementation.getImplementation());
            }
        }
        synchronized (this) {
            this.loadImplementations(this.implementations);
        }
    }
}
