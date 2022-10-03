package org.apache.axiom.locator;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.apache.axiom.om.OMMetaFactoryLocator;
import org.apache.axiom.om.OMAbstractFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.BundleTracker;
import org.apache.commons.logging.Log;
import org.osgi.framework.BundleActivator;

public class Activator implements BundleActivator
{
    private static final Log log;
    private BundleTracker tracker;
    
    public void start(final BundleContext context) throws Exception {
        final OSGiOMMetaFactoryLocator locator = new OSGiOMMetaFactoryLocator(context);
        OMAbstractFactory.setMetaFactoryLocator(locator);
        (this.tracker = new BundleTracker(context, 40, (BundleTrackerCustomizer)locator)).open();
        StAXUtils.setFactoryPerClassLoader(false);
        Activator.log.debug((Object)"OSGi support enabled");
    }
    
    public void stop(final BundleContext context) throws Exception {
        this.tracker.close();
        OMAbstractFactory.setMetaFactoryLocator(null);
        StAXUtils.setFactoryPerClassLoader(true);
        Activator.log.debug((Object)"OSGi support disabled");
    }
    
    static {
        log = LogFactory.getLog((Class)Activator.class);
    }
}
