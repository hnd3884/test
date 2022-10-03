package sun.instrument;

import java.security.ProtectionDomain;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.AccessibleObject;
import java.util.jar.JarFile;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class InstrumentationImpl implements Instrumentation
{
    private final TransformerManager mTransformerManager;
    private TransformerManager mRetransfomableTransformerManager;
    private final long mNativeAgent;
    private final boolean mEnvironmentSupportsRedefineClasses;
    private volatile boolean mEnvironmentSupportsRetransformClassesKnown;
    private volatile boolean mEnvironmentSupportsRetransformClasses;
    private final boolean mEnvironmentSupportsNativeMethodPrefix;
    
    private InstrumentationImpl(final long mNativeAgent, final boolean mEnvironmentSupportsRedefineClasses, final boolean mEnvironmentSupportsNativeMethodPrefix) {
        this.mTransformerManager = new TransformerManager(false);
        this.mRetransfomableTransformerManager = null;
        this.mNativeAgent = mNativeAgent;
        this.mEnvironmentSupportsRedefineClasses = mEnvironmentSupportsRedefineClasses;
        this.mEnvironmentSupportsRetransformClassesKnown = false;
        this.mEnvironmentSupportsRetransformClasses = false;
        this.mEnvironmentSupportsNativeMethodPrefix = mEnvironmentSupportsNativeMethodPrefix;
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer classFileTransformer) {
        this.addTransformer(classFileTransformer, false);
    }
    
    @Override
    public synchronized void addTransformer(final ClassFileTransformer classFileTransformer, final boolean b) {
        if (classFileTransformer == null) {
            throw new NullPointerException("null passed as 'transformer' in addTransformer");
        }
        if (b) {
            if (!this.isRetransformClassesSupported()) {
                throw new UnsupportedOperationException("adding retransformable transformers is not supported in this environment");
            }
            if (this.mRetransfomableTransformerManager == null) {
                this.mRetransfomableTransformerManager = new TransformerManager(true);
            }
            this.mRetransfomableTransformerManager.addTransformer(classFileTransformer);
            if (this.mRetransfomableTransformerManager.getTransformerCount() == 1) {
                this.setHasRetransformableTransformers(this.mNativeAgent, true);
            }
        }
        else {
            this.mTransformerManager.addTransformer(classFileTransformer);
        }
    }
    
    @Override
    public synchronized boolean removeTransformer(final ClassFileTransformer classFileTransformer) {
        if (classFileTransformer == null) {
            throw new NullPointerException("null passed as 'transformer' in removeTransformer");
        }
        final TransformerManager transformerManager = this.findTransformerManager(classFileTransformer);
        if (transformerManager != null) {
            transformerManager.removeTransformer(classFileTransformer);
            if (transformerManager.isRetransformable() && transformerManager.getTransformerCount() == 0) {
                this.setHasRetransformableTransformers(this.mNativeAgent, false);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isModifiableClass(final Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("null passed as 'theClass' in isModifiableClass");
        }
        return this.isModifiableClass0(this.mNativeAgent, clazz);
    }
    
    @Override
    public boolean isRetransformClassesSupported() {
        if (!this.mEnvironmentSupportsRetransformClassesKnown) {
            this.mEnvironmentSupportsRetransformClasses = this.isRetransformClassesSupported0(this.mNativeAgent);
            this.mEnvironmentSupportsRetransformClassesKnown = true;
        }
        return this.mEnvironmentSupportsRetransformClasses;
    }
    
    @Override
    public void retransformClasses(final Class<?>... array) {
        if (!this.isRetransformClassesSupported()) {
            throw new UnsupportedOperationException("retransformClasses is not supported in this environment");
        }
        this.retransformClasses0(this.mNativeAgent, array);
    }
    
    @Override
    public boolean isRedefineClassesSupported() {
        return this.mEnvironmentSupportsRedefineClasses;
    }
    
    @Override
    public void redefineClasses(final ClassDefinition... array) throws ClassNotFoundException {
        if (!this.isRedefineClassesSupported()) {
            throw new UnsupportedOperationException("redefineClasses is not supported in this environment");
        }
        if (array == null) {
            throw new NullPointerException("null passed as 'definitions' in redefineClasses");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                throw new NullPointerException("element of 'definitions' is null in redefineClasses");
            }
        }
        if (array.length == 0) {
            return;
        }
        this.redefineClasses0(this.mNativeAgent, array);
    }
    
    @Override
    public Class[] getAllLoadedClasses() {
        return this.getAllLoadedClasses0(this.mNativeAgent);
    }
    
    @Override
    public Class[] getInitiatedClasses(final ClassLoader classLoader) {
        return this.getInitiatedClasses0(this.mNativeAgent, classLoader);
    }
    
    @Override
    public long getObjectSize(final Object o) {
        if (o == null) {
            throw new NullPointerException("null passed as 'objectToSize' in getObjectSize");
        }
        return this.getObjectSize0(this.mNativeAgent, o);
    }
    
    @Override
    public void appendToBootstrapClassLoaderSearch(final JarFile jarFile) {
        this.appendToClassLoaderSearch0(this.mNativeAgent, jarFile.getName(), true);
    }
    
    @Override
    public void appendToSystemClassLoaderSearch(final JarFile jarFile) {
        this.appendToClassLoaderSearch0(this.mNativeAgent, jarFile.getName(), false);
    }
    
    @Override
    public boolean isNativeMethodPrefixSupported() {
        return this.mEnvironmentSupportsNativeMethodPrefix;
    }
    
    @Override
    public synchronized void setNativeMethodPrefix(final ClassFileTransformer classFileTransformer, final String s) {
        if (!this.isNativeMethodPrefixSupported()) {
            throw new UnsupportedOperationException("setNativeMethodPrefix is not supported in this environment");
        }
        if (classFileTransformer == null) {
            throw new NullPointerException("null passed as 'transformer' in setNativeMethodPrefix");
        }
        final TransformerManager transformerManager = this.findTransformerManager(classFileTransformer);
        if (transformerManager == null) {
            throw new IllegalArgumentException("transformer not registered in setNativeMethodPrefix");
        }
        transformerManager.setNativeMethodPrefix(classFileTransformer, s);
        this.setNativeMethodPrefixes(this.mNativeAgent, transformerManager.getNativeMethodPrefixes(), transformerManager.isRetransformable());
    }
    
    private TransformerManager findTransformerManager(final ClassFileTransformer classFileTransformer) {
        if (this.mTransformerManager.includesTransformer(classFileTransformer)) {
            return this.mTransformerManager;
        }
        if (this.mRetransfomableTransformerManager != null && this.mRetransfomableTransformerManager.includesTransformer(classFileTransformer)) {
            return this.mRetransfomableTransformerManager;
        }
        return null;
    }
    
    private native boolean isModifiableClass0(final long p0, final Class<?> p1);
    
    private native boolean isRetransformClassesSupported0(final long p0);
    
    private native void setHasRetransformableTransformers(final long p0, final boolean p1);
    
    private native void retransformClasses0(final long p0, final Class<?>[] p1);
    
    private native void redefineClasses0(final long p0, final ClassDefinition[] p1) throws ClassNotFoundException;
    
    private native Class[] getAllLoadedClasses0(final long p0);
    
    private native Class[] getInitiatedClasses0(final long p0, final ClassLoader p1);
    
    private native long getObjectSize0(final long p0, final Object p1);
    
    private native void appendToClassLoaderSearch0(final long p0, final String p1, final boolean p2);
    
    private native void setNativeMethodPrefixes(final long p0, final String[] p1, final boolean p2);
    
    private static void setAccessible(final AccessibleObject accessibleObject, final boolean b) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                accessibleObject.setAccessible(b);
                return null;
            }
        });
    }
    
    private void loadClassAndStartAgent(final String s, final String s2, final String s3) throws Throwable {
        final Class<?> loadClass = ClassLoader.getSystemClassLoader().loadClass(s);
        Method method = null;
        NoSuchMethodException ex = null;
        boolean b = false;
        try {
            method = loadClass.getDeclaredMethod(s2, String.class, Instrumentation.class);
            b = true;
        }
        catch (final NoSuchMethodException ex2) {
            ex = ex2;
        }
        if (method == null) {
            try {
                method = loadClass.getDeclaredMethod(s2, String.class);
            }
            catch (final NoSuchMethodException ex3) {}
        }
        if (method == null) {
            try {
                method = loadClass.getMethod(s2, String.class, Instrumentation.class);
                b = true;
            }
            catch (final NoSuchMethodException ex4) {}
        }
        if (method == null) {
            try {
                method = loadClass.getMethod(s2, String.class);
            }
            catch (final NoSuchMethodException ex5) {
                throw ex;
            }
        }
        setAccessible(method, true);
        if (b) {
            method.invoke(null, s3, this);
        }
        else {
            method.invoke(null, s3);
        }
        setAccessible(method, false);
    }
    
    private void loadClassAndCallPremain(final String s, final String s2) throws Throwable {
        this.loadClassAndStartAgent(s, "premain", s2);
    }
    
    private void loadClassAndCallAgentmain(final String s, final String s2) throws Throwable {
        this.loadClassAndStartAgent(s, "agentmain", s2);
    }
    
    private byte[] transform(final ClassLoader classLoader, final String s, final Class<?> clazz, final ProtectionDomain protectionDomain, final byte[] array, final boolean b) {
        final TransformerManager transformerManager = b ? this.mRetransfomableTransformerManager : this.mTransformerManager;
        if (transformerManager == null) {
            return null;
        }
        return transformerManager.transform(classLoader, s, clazz, protectionDomain, array);
    }
    
    static {
        System.loadLibrary("instrument");
    }
}
