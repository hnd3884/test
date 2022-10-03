package java.lang;

import java.security.PrivilegedExceptionAction;

class SystemClassLoaderAction implements PrivilegedExceptionAction<ClassLoader>
{
    private ClassLoader parent;
    
    SystemClassLoaderAction(final ClassLoader parent) {
        this.parent = parent;
    }
    
    @Override
    public ClassLoader run() throws Exception {
        final String property = System.getProperty("java.system.class.loader");
        if (property == null) {
            return this.parent;
        }
        final ClassLoader contextClassLoader = (ClassLoader)Class.forName(property, true, this.parent).getDeclaredConstructor(ClassLoader.class).newInstance(this.parent);
        Thread.currentThread().setContextClassLoader(contextClassLoader);
        return contextClassLoader;
    }
}
