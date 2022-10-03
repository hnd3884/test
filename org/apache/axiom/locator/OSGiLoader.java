package org.apache.axiom.locator;

import org.osgi.framework.Bundle;

final class OSGiLoader extends Loader
{
    private final Bundle bundle;
    
    OSGiLoader(final Bundle bundle) {
        this.bundle = bundle;
    }
    
    @Override
    Class<?> load(final String className) throws ClassNotFoundException {
        return this.bundle.loadClass(className);
    }
}
