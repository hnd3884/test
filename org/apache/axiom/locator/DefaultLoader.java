package org.apache.axiom.locator;

final class DefaultLoader extends Loader
{
    private final ClassLoader classLoader;
    
    DefaultLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    @Override
    Class<?> load(final String className) throws ClassNotFoundException {
        return this.classLoader.loadClass(className);
    }
}
