package org.apache.xmlbeans.impl.jam.internal.javadoc;

import java.lang.reflect.Method;
import com.sun.javadoc.RootDoc;

public class JavadocResults
{
    private static final JavadocResults INSTANCE;
    private ThreadLocal mRootsPerThread;
    
    public JavadocResults() {
        this.mRootsPerThread = new ThreadLocal();
    }
    
    public static void prepare() {
        Thread.currentThread().setContextClassLoader(JavadocResults.class.getClassLoader());
    }
    
    public static void setRoot(final RootDoc root) {
        try {
            final Object holder = getHolder();
            final Method setter = holder.getClass().getMethod("_setRoot", RootDoc.class);
            setter.invoke(holder, root);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
    
    public static RootDoc getRoot() {
        try {
            final Object holder = getHolder();
            final Method getter = holder.getClass().getMethod("_getRoot", (Class<?>[])new Class[0]);
            return (RootDoc)getter.invoke(holder, (Object[])null);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
    
    public void _setRoot(final RootDoc root) {
        this.mRootsPerThread.set(root);
    }
    
    public RootDoc _getRoot() {
        return this.mRootsPerThread.get();
    }
    
    public static JavadocResults getInstance() {
        return JavadocResults.INSTANCE;
    }
    
    private static Object getHolder() throws Exception {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Class clazz = classLoader.loadClass(JavadocResults.class.getName());
        final Method method = clazz.getMethod("getInstance", (Class[])new Class[0]);
        return method.invoke(null, new Object[0]);
    }
    
    static {
        INSTANCE = new JavadocResults();
    }
}
