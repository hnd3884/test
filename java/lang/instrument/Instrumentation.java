package java.lang.instrument;

import java.util.jar.JarFile;

public interface Instrumentation
{
    void addTransformer(final ClassFileTransformer p0, final boolean p1);
    
    void addTransformer(final ClassFileTransformer p0);
    
    boolean removeTransformer(final ClassFileTransformer p0);
    
    boolean isRetransformClassesSupported();
    
    void retransformClasses(final Class<?>... p0) throws UnmodifiableClassException;
    
    boolean isRedefineClassesSupported();
    
    void redefineClasses(final ClassDefinition... p0) throws ClassNotFoundException, UnmodifiableClassException;
    
    boolean isModifiableClass(final Class<?> p0);
    
    Class[] getAllLoadedClasses();
    
    Class[] getInitiatedClasses(final ClassLoader p0);
    
    long getObjectSize(final Object p0);
    
    void appendToBootstrapClassLoaderSearch(final JarFile p0);
    
    void appendToSystemClassLoaderSearch(final JarFile p0);
    
    boolean isNativeMethodPrefixSupported();
    
    void setNativeMethodPrefix(final ClassFileTransformer p0, final String p1);
}
