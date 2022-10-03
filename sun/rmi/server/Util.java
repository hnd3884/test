package sun.rmi.server;

import java.util.Collections;
import java.util.WeakHashMap;
import sun.security.action.GetBooleanAction;
import java.rmi.server.LogStream;
import sun.security.action.GetPropertyAction;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.io.ByteArrayOutputStream;
import java.rmi.server.SkeletonNotFoundException;
import java.rmi.server.Skeleton;
import java.lang.reflect.InvocationTargetException;
import java.rmi.server.RemoteStub;
import java.rmi.RemoteException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.security.AccessController;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.security.PrivilegedAction;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.StubNotFoundException;
import java.rmi.Remote;
import java.rmi.server.RemoteRef;
import java.util.Map;
import sun.rmi.runtime.Log;

public final class Util
{
    static final int logLevel;
    public static final Log serverRefLog;
    private static final boolean ignoreStubClasses;
    private static final Map<Class<?>, Void> withoutStubs;
    private static final Class<?>[] stubConsParamTypes;
    
    private Util() {
    }
    
    public static Remote createProxy(final Class<?> clazz, final RemoteRef remoteRef, final boolean b) throws StubNotFoundException {
        Class<?> remoteClass;
        try {
            remoteClass = getRemoteClass(clazz);
        }
        catch (final ClassNotFoundException ex) {
            throw new StubNotFoundException("object does not implement a remote interface: " + clazz.getName());
        }
        if (b || (!Util.ignoreStubClasses && stubClassExists(remoteClass))) {
            return createStub(remoteClass, remoteRef);
        }
        final ClassLoader classLoader = clazz.getClassLoader();
        final Class<?>[] remoteInterfaces = getRemoteInterfaces(clazz);
        final RemoteObjectInvocationHandler remoteObjectInvocationHandler = new RemoteObjectInvocationHandler(remoteRef);
        try {
            return AccessController.doPrivileged((PrivilegedAction<Remote>)new PrivilegedAction<Remote>() {
                @Override
                public Remote run() {
                    return (Remote)Proxy.newProxyInstance(classLoader, remoteInterfaces, remoteObjectInvocationHandler);
                }
            });
        }
        catch (final IllegalArgumentException ex2) {
            throw new StubNotFoundException("unable to create proxy", ex2);
        }
    }
    
    private static boolean stubClassExists(final Class<?> clazz) {
        if (!Util.withoutStubs.containsKey(clazz)) {
            try {
                Class.forName(clazz.getName() + "_Stub", false, clazz.getClassLoader());
                return true;
            }
            catch (final ClassNotFoundException ex) {
                Util.withoutStubs.put(clazz, null);
            }
        }
        return false;
    }
    
    private static Class<?> getRemoteClass(Class<?> superclass) throws ClassNotFoundException {
        while (superclass != null) {
            final Class[] interfaces = superclass.getInterfaces();
            for (int i = interfaces.length - 1; i >= 0; --i) {
                if (Remote.class.isAssignableFrom(interfaces[i])) {
                    return superclass;
                }
            }
            superclass = superclass.getSuperclass();
        }
        throw new ClassNotFoundException("class does not implement java.rmi.Remote");
    }
    
    private static Class<?>[] getRemoteInterfaces(final Class<?> clazz) {
        final ArrayList list = new ArrayList();
        getRemoteInterfaces(list, clazz);
        return list.toArray(new Class[list.size()]);
    }
    
    private static void getRemoteInterfaces(final ArrayList<Class<?>> list, final Class<?> clazz) {
        final Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            getRemoteInterfaces(list, superclass);
        }
        final Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            final Class<?> clazz2 = interfaces[i];
            if (Remote.class.isAssignableFrom(clazz2) && !list.contains(clazz2)) {
                final Method[] methods = clazz2.getMethods();
                for (int j = 0; j < methods.length; ++j) {
                    checkMethod(methods[j]);
                }
                list.add(clazz2);
            }
        }
    }
    
    private static void checkMethod(final Method method) {
        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        for (int i = 0; i < exceptionTypes.length; ++i) {
            if (exceptionTypes[i].isAssignableFrom(RemoteException.class)) {
                return;
            }
        }
        throw new IllegalArgumentException("illegal remote method encountered: " + method);
    }
    
    private static RemoteStub createStub(final Class<?> clazz, final RemoteRef remoteRef) throws StubNotFoundException {
        final String string = clazz.getName() + "_Stub";
        try {
            return (RemoteStub)Class.forName(string, false, clazz.getClassLoader()).getConstructor(Util.stubConsParamTypes).newInstance(remoteRef);
        }
        catch (final ClassNotFoundException ex) {
            throw new StubNotFoundException("Stub class not found: " + string, ex);
        }
        catch (final NoSuchMethodException ex2) {
            throw new StubNotFoundException("Stub class missing constructor: " + string, ex2);
        }
        catch (final InstantiationException ex3) {
            throw new StubNotFoundException("Can't create instance of stub class: " + string, ex3);
        }
        catch (final IllegalAccessException ex4) {
            throw new StubNotFoundException("Stub class constructor not public: " + string, ex4);
        }
        catch (final InvocationTargetException ex5) {
            throw new StubNotFoundException("Exception creating instance of stub class: " + string, ex5);
        }
        catch (final ClassCastException ex6) {
            throw new StubNotFoundException("Stub class not instance of RemoteStub: " + string, ex6);
        }
    }
    
    static Skeleton createSkeleton(final Remote remote) throws SkeletonNotFoundException {
        Class<?> remoteClass;
        try {
            remoteClass = getRemoteClass(remote.getClass());
        }
        catch (final ClassNotFoundException ex) {
            throw new SkeletonNotFoundException("object does not implement a remote interface: " + remote.getClass().getName());
        }
        final String string = remoteClass.getName() + "_Skel";
        try {
            return (Skeleton)Class.forName(string, false, remoteClass.getClassLoader()).newInstance();
        }
        catch (final ClassNotFoundException ex2) {
            throw new SkeletonNotFoundException("Skeleton class not found: " + string, ex2);
        }
        catch (final InstantiationException ex3) {
            throw new SkeletonNotFoundException("Can't create skeleton: " + string, ex3);
        }
        catch (final IllegalAccessException ex4) {
            throw new SkeletonNotFoundException("No public constructor: " + string, ex4);
        }
        catch (final ClassCastException ex5) {
            throw new SkeletonNotFoundException("Skeleton not of correct class: " + string, ex5);
        }
    }
    
    public static long computeMethodHash(final Method method) {
        long n = 0L;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(127);
        try {
            final MessageDigest instance = MessageDigest.getInstance("SHA");
            final DataOutputStream dataOutputStream = new DataOutputStream(new DigestOutputStream(byteArrayOutputStream, instance));
            final String methodNameAndDescriptor = getMethodNameAndDescriptor(method);
            if (Util.serverRefLog.isLoggable(Log.VERBOSE)) {
                Util.serverRefLog.log(Log.VERBOSE, "string used for method hash: \"" + methodNameAndDescriptor + "\"");
            }
            dataOutputStream.writeUTF(methodNameAndDescriptor);
            dataOutputStream.flush();
            final byte[] digest = instance.digest();
            for (int i = 0; i < Math.min(8, digest.length); ++i) {
                n += (long)(digest[i] & 0xFF) << i * 8;
            }
        }
        catch (final IOException ex) {
            n = -1L;
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new SecurityException(ex2.getMessage());
        }
        return n;
    }
    
    private static String getMethodNameAndDescriptor(final Method method) {
        final StringBuffer sb = new StringBuffer(method.getName());
        sb.append('(');
        final Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            sb.append(getTypeDescriptor(parameterTypes[i]));
        }
        sb.append(')');
        final Class<?> returnType = method.getReturnType();
        if (returnType == Void.TYPE) {
            sb.append('V');
        }
        else {
            sb.append(getTypeDescriptor(returnType));
        }
        return sb.toString();
    }
    
    private static String getTypeDescriptor(final Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                return "I";
            }
            if (clazz == Boolean.TYPE) {
                return "Z";
            }
            if (clazz == Byte.TYPE) {
                return "B";
            }
            if (clazz == Character.TYPE) {
                return "C";
            }
            if (clazz == Short.TYPE) {
                return "S";
            }
            if (clazz == Long.TYPE) {
                return "J";
            }
            if (clazz == Float.TYPE) {
                return "F";
            }
            if (clazz == Double.TYPE) {
                return "D";
            }
            if (clazz == Void.TYPE) {
                return "V";
            }
            throw new Error("unrecognized primitive type: " + clazz);
        }
        else {
            if (clazz.isArray()) {
                return clazz.getName().replace('.', '/');
            }
            return "L" + clazz.getName().replace('.', '/') + ";";
        }
    }
    
    public static String getUnqualifiedName(final Class<?> clazz) {
        final String name = clazz.getName();
        return name.substring(name.lastIndexOf(46) + 1);
    }
    
    static {
        logLevel = LogStream.parseLevel(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.server.logLevel")));
        serverRefLog = Log.getLog("sun.rmi.server.ref", "transport", Util.logLevel);
        ignoreStubClasses = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("java.rmi.server.ignoreStubClasses"));
        withoutStubs = Collections.synchronizedMap(new WeakHashMap<Class<?>, Void>(11));
        stubConsParamTypes = new Class[] { RemoteRef.class };
    }
}
