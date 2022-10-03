package sun.reflect.misc;

import java.security.Permission;
import java.security.AllPermission;
import java.security.PermissionCollection;
import java.net.URLConnection;
import java.io.EOFException;
import java.io.InputStream;
import sun.misc.IOUtils;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.net.URL;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.security.SecureClassLoader;

public final class MethodUtil extends SecureClassLoader
{
    private static final String MISC_PKG = "sun.reflect.misc.";
    private static final String TRAMPOLINE = "sun.reflect.misc.Trampoline";
    private static final Method bounce;
    
    private MethodUtil() {
    }
    
    public static Method getMethod(final Class<?> clazz, final String s, final Class<?>[] array) throws NoSuchMethodException {
        ReflectUtil.checkPackageAccess(clazz);
        return clazz.getMethod(s, (Class[])array);
    }
    
    public static Method[] getMethods(final Class<?> clazz) {
        ReflectUtil.checkPackageAccess(clazz);
        return clazz.getMethods();
    }
    
    public static Method[] getPublicMethods(Class<?> superclass) {
        if (System.getSecurityManager() == null) {
            return superclass.getMethods();
        }
        HashMap hashMap;
        for (hashMap = new HashMap(); superclass != null && !getInternalPublicMethods(superclass, hashMap); superclass = superclass.getSuperclass()) {
            getInterfaceMethods(superclass, hashMap);
        }
        return (Method[])hashMap.values().toArray(new Method[hashMap.size()]);
    }
    
    private static void getInterfaceMethods(final Class<?> clazz, final Map<Signature, Method> map) {
        final Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            final Class<?> clazz2 = interfaces[i];
            if (!getInternalPublicMethods(clazz2, map)) {
                getInterfaceMethods(clazz2, map);
            }
        }
    }
    
    private static boolean getInternalPublicMethods(final Class<?> clazz, final Map<Signature, Method> map) {
        Method[] methods;
        try {
            if (!Modifier.isPublic(clazz.getModifiers())) {
                return false;
            }
            if (!ReflectUtil.isPackageAccessible(clazz)) {
                return false;
            }
            methods = clazz.getMethods();
        }
        catch (final SecurityException ex) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < methods.length; ++i) {
            if (!Modifier.isPublic(methods[i].getDeclaringClass().getModifiers())) {
                b = false;
                break;
            }
        }
        if (b) {
            for (int j = 0; j < methods.length; ++j) {
                addMethod(map, methods[j]);
            }
        }
        else {
            for (int k = 0; k < methods.length; ++k) {
                if (clazz.equals(methods[k].getDeclaringClass())) {
                    addMethod(map, methods[k]);
                }
            }
        }
        return b;
    }
    
    private static void addMethod(final Map<Signature, Method> map, final Method method) {
        final Signature signature = new Signature(method);
        if (!map.containsKey(signature)) {
            map.put(signature, method);
        }
        else if (!method.getDeclaringClass().isInterface() && map.get(signature).getDeclaringClass().isInterface()) {
            map.put(signature, method);
        }
    }
    
    public static Object invoke(final Method method, final Object o, final Object[] array) throws InvocationTargetException, IllegalAccessException {
        try {
            return MethodUtil.bounce.invoke(null, method, o, array);
        }
        catch (final InvocationTargetException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof InvocationTargetException) {
                throw (InvocationTargetException)cause;
            }
            if (cause instanceof IllegalAccessException) {
                throw (IllegalAccessException)cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new Error("Unexpected invocation error", cause);
        }
        catch (final IllegalAccessException ex2) {
            throw new Error("Unexpected invocation error", ex2);
        }
    }
    
    private static Method getTrampoline() {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    final Method declaredMethod = getTrampolineClass().getDeclaredMethod("invoke", Method.class, Object.class, Object[].class);
                    declaredMethod.setAccessible(true);
                    return declaredMethod;
                }
            });
        }
        catch (final Exception ex) {
            throw new InternalError("bouncer cannot be found", ex);
        }
    }
    
    @Override
    protected synchronized Class<?> loadClass(final String s, final boolean b) throws ClassNotFoundException {
        ReflectUtil.checkPackageAccess(s);
        Class<?> clazz = this.findLoadedClass(s);
        if (clazz == null) {
            try {
                clazz = this.findClass(s);
            }
            catch (final ClassNotFoundException ex) {}
            if (clazz == null) {
                clazz = this.getParent().loadClass(s);
            }
        }
        if (b) {
            this.resolveClass(clazz);
        }
        return clazz;
    }
    
    @Override
    protected Class<?> findClass(final String s) throws ClassNotFoundException {
        if (!s.startsWith("sun.reflect.misc.")) {
            throw new ClassNotFoundException(s);
        }
        final URL resource = this.getResource(s.replace('.', '/').concat(".class"));
        if (resource != null) {
            try {
                return this.defineClass(s, resource);
            }
            catch (final IOException ex) {
                throw new ClassNotFoundException(s, ex);
            }
        }
        throw new ClassNotFoundException(s);
    }
    
    private Class<?> defineClass(final String s, final URL url) throws IOException {
        final byte[] bytes = getBytes(url);
        final CodeSource codeSource = new CodeSource(null, (Certificate[])null);
        if (!s.equals("sun.reflect.misc.Trampoline")) {
            throw new IOException("MethodUtil: bad name " + s);
        }
        return this.defineClass(s, bytes, 0, bytes.length, codeSource);
    }
    
    private static byte[] getBytes(final URL url) throws IOException {
        final URLConnection openConnection = url.openConnection();
        if (openConnection instanceof HttpURLConnection && ((HttpURLConnection)openConnection).getResponseCode() >= 400) {
            throw new IOException("open HTTP connection failed.");
        }
        final int contentLength = openConnection.getContentLength();
        try (final BufferedInputStream bufferedInputStream = new BufferedInputStream(openConnection.getInputStream())) {
            final byte[] allBytes = IOUtils.readAllBytes(bufferedInputStream);
            if (contentLength != -1 && allBytes.length != contentLength) {
                throw new EOFException("Expected:" + contentLength + ", read:" + allBytes.length);
            }
            return allBytes;
        }
    }
    
    @Override
    protected PermissionCollection getPermissions(final CodeSource codeSource) {
        final PermissionCollection permissions = super.getPermissions(codeSource);
        permissions.add(new AllPermission());
        return permissions;
    }
    
    private static Class<?> getTrampolineClass() {
        try {
            return Class.forName("sun.reflect.misc.Trampoline", true, new MethodUtil());
        }
        catch (final ClassNotFoundException ex) {
            return null;
        }
    }
    
    static {
        bounce = getTrampoline();
    }
    
    private static class Signature
    {
        private String methodName;
        private Class<?>[] argClasses;
        private volatile int hashCode;
        
        Signature(final Method method) {
            this.hashCode = 0;
            this.methodName = method.getName();
            this.argClasses = method.getParameterTypes();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            final Signature signature = (Signature)o;
            if (!this.methodName.equals(signature.methodName)) {
                return false;
            }
            if (this.argClasses.length != signature.argClasses.length) {
                return false;
            }
            for (int i = 0; i < this.argClasses.length; ++i) {
                if (this.argClasses[i] != signature.argClasses[i]) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                int hashCode = 37 * 17 + this.methodName.hashCode();
                if (this.argClasses != null) {
                    for (int i = 0; i < this.argClasses.length; ++i) {
                        hashCode = 37 * hashCode + ((this.argClasses[i] == null) ? 0 : this.argClasses[i].hashCode());
                    }
                }
                this.hashCode = hashCode;
            }
            return this.hashCode;
        }
    }
}
