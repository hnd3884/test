package org.apache.catalina.loader;

import org.apache.catalina.LifecycleException;

public class WebappClassLoader extends WebappClassLoaderBase
{
    public WebappClassLoader() {
    }
    
    public WebappClassLoader(final ClassLoader parent) {
        super(parent);
    }
    
    public WebappClassLoader copyWithoutTransformers() {
        final WebappClassLoader result = new WebappClassLoader(this.getParent());
        super.copyStateWithoutTransformers(result);
        try {
            result.start();
        }
        catch (final LifecycleException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }
    
    protected Object getClassLoadingLock(final String className) {
        return this;
    }
}
