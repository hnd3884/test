package org.glassfish.hk2.osgiresourcelocator;

import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.Bundle;
import java.net.URL;
import org.osgi.framework.BundleReference;
import org.osgi.framework.BundleContext;

public class ResourceFinderImpl extends ResourceFinder
{
    private BundleContext bundleContext;
    
    public ResourceFinderImpl() {
        final ClassLoader cl = this.getClass().getClassLoader();
        if (cl instanceof BundleReference) {
            this.bundleContext = BundleReference.class.cast(cl).getBundle().getBundleContext();
        }
        if (this.bundleContext == null) {
            throw new RuntimeException("There is no bundle context available yet. Instatiate this class in STARTING or ACTIVE state only");
        }
    }
    
    @Override
    URL findEntry1(final String path) {
        for (final Bundle bundle : this.bundleContext.getBundles()) {
            final URL url = bundle.getEntry(path);
            if (url != null) {
                return url;
            }
        }
        return null;
    }
    
    @Override
    List<URL> findEntries1(final String path) {
        final List<URL> urls = new ArrayList<URL>();
        for (final Bundle bundle : this.bundleContext.getBundles()) {
            final URL url = bundle.getEntry(path);
            if (url != null) {
                urls.add(url);
            }
        }
        return urls;
    }
}
