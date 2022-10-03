package org.apache.tomcat.util.compat;

import java.net.URLConnection;
import org.apache.juli.logging.LogFactory;
import java.lang.reflect.AccessibleObject;
import java.io.File;
import java.util.Iterator;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Set;
import java.net.URL;
import java.util.Deque;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.JarFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

class Jre9Compat extends Jre8Compat
{
    private static final Log log;
    private static final StringManager sm;
    private static final Class<?> inaccessibleObjectExceptionClazz;
    private static final Method setDefaultUseCachesMethod;
    private static final Method bootMethod;
    private static final Method configurationMethod;
    private static final Method modulesMethod;
    private static final Method referenceMethod;
    private static final Method locationMethod;
    private static final Method isPresentMethod;
    private static final Method getMethod;
    private static final Constructor<JarFile> jarFileConstructor;
    private static final Method isMultiReleaseMethod;
    private static final Object RUNTIME_VERSION;
    private static final int RUNTIME_MAJOR_VERSION;
    private static final Method canAccessMethod;
    private static final Method getModuleMethod;
    private static final Method isExportedMethod;
    private static final Method getNameMethod;
    
    static boolean isSupported() {
        return Jre9Compat.inaccessibleObjectExceptionClazz != null;
    }
    
    @Override
    public boolean isInstanceOfInaccessibleObjectException(final Throwable t) {
        return t != null && Jre9Compat.inaccessibleObjectExceptionClazz.isAssignableFrom(t.getClass());
    }
    
    @Override
    public void disableCachingForJarUrlConnections() throws IOException {
        try {
            Jre9Compat.setDefaultUseCachesMethod.invoke(null, "JAR", Boolean.FALSE);
        }
        catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }
    
    @Override
    public void addBootModulePath(final Deque<URL> classPathUrlsToProcess) {
        try {
            final Object bootLayer = Jre9Compat.bootMethod.invoke(null, new Object[0]);
            final Object bootConfiguration = Jre9Compat.configurationMethod.invoke(bootLayer, new Object[0]);
            final Set<?> resolvedModules = (Set<?>)Jre9Compat.modulesMethod.invoke(bootConfiguration, new Object[0]);
            for (final Object resolvedModule : resolvedModules) {
                final Object moduleReference = Jre9Compat.referenceMethod.invoke(resolvedModule, new Object[0]);
                final Object optionalURI = Jre9Compat.locationMethod.invoke(moduleReference, new Object[0]);
                final Boolean isPresent = (Boolean)Jre9Compat.isPresentMethod.invoke(optionalURI, new Object[0]);
                if (isPresent) {
                    final URI uri = (URI)Jre9Compat.getMethod.invoke(optionalURI, new Object[0]);
                    try {
                        final URL url = uri.toURL();
                        classPathUrlsToProcess.add(url);
                    }
                    catch (final MalformedURLException e) {
                        Jre9Compat.log.warn((Object)Jre9Compat.sm.getString("jre9Compat.invalidModuleUri", uri), (Throwable)e);
                    }
                }
            }
        }
        catch (final ReflectiveOperationException e2) {
            throw new UnsupportedOperationException(e2);
        }
    }
    
    @Override
    public JarFile jarFileNewInstance(final File f) throws IOException {
        try {
            return Jre9Compat.jarFileConstructor.newInstance(f, Boolean.TRUE, 1, Jre9Compat.RUNTIME_VERSION);
        }
        catch (final ReflectiveOperationException | IllegalArgumentException e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public boolean jarFileIsMultiRelease(final JarFile jarFile) {
        try {
            return (boolean)Jre9Compat.isMultiReleaseMethod.invoke(jarFile, new Object[0]);
        }
        catch (final ReflectiveOperationException | IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public int jarFileRuntimeMajorVersion() {
        return Jre9Compat.RUNTIME_MAJOR_VERSION;
    }
    
    @Override
    public boolean canAccess(final Object base, final AccessibleObject accessibleObject) {
        try {
            return (boolean)Jre9Compat.canAccessMethod.invoke(accessibleObject, base);
        }
        catch (final ReflectiveOperationException | IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public boolean isExported(final Class<?> type) {
        try {
            final String packageName = type.getPackage().getName();
            final Object module = Jre9Compat.getModuleMethod.invoke(type, new Object[0]);
            return (boolean)Jre9Compat.isExportedMethod.invoke(module, packageName);
        }
        catch (final ReflectiveOperationException e) {
            return false;
        }
    }
    
    @Override
    public String getModuleName(final Class<?> type) {
        try {
            final Object module = Jre9Compat.getModuleMethod.invoke(type, new Object[0]);
            return (String)Jre9Compat.getNameMethod.invoke(module, new Object[0]);
        }
        catch (final ReflectiveOperationException e) {
            return "ERROR";
        }
    }
    
    static {
        log = LogFactory.getLog((Class)Jre9Compat.class);
        sm = StringManager.getManager(Jre9Compat.class);
        Class<?> c1 = null;
        Method m4 = null;
        Method m5 = null;
        Method m6 = null;
        Method m7 = null;
        Method m8 = null;
        Method m9 = null;
        Method m10 = null;
        Method m11 = null;
        Constructor<JarFile> c2 = null;
        Method m12 = null;
        Object o14 = null;
        Object o15 = null;
        Method m13 = null;
        Method m14 = null;
        Method m15 = null;
        Method m16 = null;
        try {
            c1 = Class.forName("java.lang.reflect.InaccessibleObjectException");
            final Class<?> moduleLayerClazz = Class.forName("java.lang.ModuleLayer");
            final Class<?> configurationClazz = Class.forName("java.lang.module.Configuration");
            final Class<?> resolvedModuleClazz = Class.forName("java.lang.module.ResolvedModule");
            final Class<?> moduleReferenceClazz = Class.forName("java.lang.module.ModuleReference");
            final Class<?> optionalClazz = Class.forName("java.util.Optional");
            final Class<?> versionClazz = Class.forName("java.lang.Runtime$Version");
            final Method runtimeVersionMethod = JarFile.class.getMethod("runtimeVersion", (Class<?>[])new Class[0]);
            final Method majorMethod = versionClazz.getMethod("major", (Class<?>[])new Class[0]);
            m4 = URLConnection.class.getMethod("setDefaultUseCaches", String.class, Boolean.TYPE);
            m5 = moduleLayerClazz.getMethod("boot", (Class<?>[])new Class[0]);
            m6 = moduleLayerClazz.getMethod("configuration", (Class<?>[])new Class[0]);
            m7 = configurationClazz.getMethod("modules", (Class<?>[])new Class[0]);
            m8 = resolvedModuleClazz.getMethod("reference", (Class<?>[])new Class[0]);
            m9 = moduleReferenceClazz.getMethod("location", (Class<?>[])new Class[0]);
            m10 = optionalClazz.getMethod("isPresent", (Class<?>[])new Class[0]);
            m11 = optionalClazz.getMethod("get", (Class<?>[])new Class[0]);
            c2 = JarFile.class.getConstructor(File.class, Boolean.TYPE, Integer.TYPE, versionClazz);
            m12 = JarFile.class.getMethod("isMultiRelease", (Class<?>[])new Class[0]);
            o14 = runtimeVersionMethod.invoke(null, new Object[0]);
            o15 = majorMethod.invoke(o14, new Object[0]);
            m13 = AccessibleObject.class.getMethod("canAccess", Object.class);
            m14 = Class.class.getMethod("getModule", (Class<?>[])new Class[0]);
            final Class<?> moduleClass = Class.forName("java.lang.Module");
            m15 = moduleClass.getMethod("isExported", String.class);
            m16 = moduleClass.getMethod("getName", (Class<?>[])new Class[0]);
        }
        catch (final ClassNotFoundException e) {
            if (c1 == null) {
                Jre9Compat.log.debug((Object)Jre9Compat.sm.getString("jre9Compat.javaPre9"), (Throwable)e);
            }
            else {
                Jre9Compat.log.error((Object)Jre9Compat.sm.getString("jre9Compat.unexpected"), (Throwable)e);
            }
        }
        catch (final ReflectiveOperationException | IllegalArgumentException e2) {
            Jre9Compat.log.error((Object)Jre9Compat.sm.getString("jre9Compat.unexpected"), (Throwable)e2);
        }
        inaccessibleObjectExceptionClazz = c1;
        setDefaultUseCachesMethod = m4;
        bootMethod = m5;
        configurationMethod = m6;
        modulesMethod = m7;
        referenceMethod = m8;
        locationMethod = m9;
        isPresentMethod = m10;
        getMethod = m11;
        jarFileConstructor = c2;
        isMultiReleaseMethod = m12;
        RUNTIME_VERSION = o14;
        if (o15 != null) {
            RUNTIME_MAJOR_VERSION = (int)o15;
        }
        else {
            RUNTIME_MAJOR_VERSION = 8;
        }
        canAccessMethod = m13;
        getModuleMethod = m14;
        isExportedMethod = m15;
        getNameMethod = m16;
    }
}
