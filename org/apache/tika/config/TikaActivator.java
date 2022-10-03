package org.apache.tika.config;

import org.osgi.framework.ServiceReference;
import org.apache.tika.parser.Parser;
import org.apache.tika.detect.Detector;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.osgi.framework.BundleActivator;

public class TikaActivator implements BundleActivator, ServiceTrackerCustomizer
{
    private ServiceTracker detectorTracker;
    private ServiceTracker parserTracker;
    private BundleContext bundleContext;
    
    public void start(final BundleContext context) throws Exception {
        this.bundleContext = context;
        this.detectorTracker = new ServiceTracker(context, Detector.class.getName(), (ServiceTrackerCustomizer)this);
        this.parserTracker = new ServiceTracker(context, Parser.class.getName(), (ServiceTrackerCustomizer)this);
        this.detectorTracker.open();
        this.parserTracker.open();
    }
    
    public void stop(final BundleContext context) throws Exception {
        this.parserTracker.close();
        this.detectorTracker.close();
    }
    
    public Object addingService(final ServiceReference reference) {
        int rank = 0;
        final Object property = reference.getProperty("service.ranking");
        if (property instanceof Integer) {
            rank = (int)property;
        }
        final Object service = this.bundleContext.getService(reference);
        ServiceLoader.addService(reference, service, rank);
        return service;
    }
    
    public void modifiedService(final ServiceReference reference, final Object service) {
    }
    
    public void removedService(final ServiceReference reference, final Object service) {
        ServiceLoader.removeService(reference);
        this.bundleContext.ungetService(reference);
    }
}
