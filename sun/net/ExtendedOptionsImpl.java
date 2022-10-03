package sun.net;

import java.security.AccessController;
import java.net.SocketException;
import jdk.net.SocketFlow;
import java.io.FileDescriptor;
import java.security.Permission;
import jdk.net.NetworkPermission;
import java.net.SocketOption;

public class ExtendedOptionsImpl
{
    private ExtendedOptionsImpl() {
    }
    
    public static void checkSetOptionPermission(final SocketOption<?> socketOption) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            return;
        }
        securityManager.checkPermission(new NetworkPermission("setOption." + socketOption.name()));
    }
    
    public static void checkGetOptionPermission(final SocketOption<?> socketOption) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            return;
        }
        securityManager.checkPermission(new NetworkPermission("getOption." + socketOption.name()));
    }
    
    public static void checkValueType(final Object o, final Class<?> clazz) {
        if (!clazz.isAssignableFrom(o.getClass())) {
            throw new IllegalArgumentException("Found: " + o.getClass().toString() + " Expected: " + clazz.toString());
        }
    }
    
    private static native void init();
    
    public static native void setFlowOption(final FileDescriptor p0, final SocketFlow p1);
    
    public static native void getFlowOption(final FileDescriptor p0, final SocketFlow p1);
    
    public static native boolean flowSupported();
    
    public static native void setTcpKeepAliveProbes(final FileDescriptor p0, final int p1) throws SocketException;
    
    public static native void setTcpKeepAliveTime(final FileDescriptor p0, final int p1) throws SocketException;
    
    public static native void setTcpKeepAliveIntvl(final FileDescriptor p0, final int p1) throws SocketException;
    
    public static native int getTcpKeepAliveProbes(final FileDescriptor p0) throws SocketException;
    
    public static native int getTcpKeepAliveTime(final FileDescriptor p0) throws SocketException;
    
    public static native int getTcpKeepAliveIntvl(final FileDescriptor p0) throws SocketException;
    
    public static native boolean keepAliveOptionsSupported();
    
    static {
        AccessController.doPrivileged(() -> {
            System.loadLibrary("net");
            return null;
        });
        init();
    }
}
