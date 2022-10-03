package jdk.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.MulticastSocket;
import java.util.Collections;
import java.util.Collection;
import sun.net.ExtendedOptionsHelper;
import java.net.StandardSocketOptions;
import java.util.HashSet;
import sun.net.ExtendedOptionsImpl;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramSocket;
import java.lang.reflect.Method;
import java.net.SocketOption;
import java.util.Set;
import java.util.HashMap;
import jdk.Exported;

@Exported
public class Sockets
{
    private static final HashMap<Class<?>, Set<SocketOption<?>>> options;
    private static Method siSetOption;
    private static Method siGetOption;
    private static Method dsiSetOption;
    private static Method dsiGetOption;
    
    private static void initMethods() {
        try {
            final Class<?> forName = Class.forName("java.net.SocketSecrets");
            (Sockets.siSetOption = forName.getDeclaredMethod("setOption", Object.class, SocketOption.class, Object.class)).setAccessible(true);
            (Sockets.siGetOption = forName.getDeclaredMethod("getOption", Object.class, SocketOption.class)).setAccessible(true);
            (Sockets.dsiSetOption = forName.getDeclaredMethod("setOption", DatagramSocket.class, SocketOption.class, Object.class)).setAccessible(true);
            (Sockets.dsiGetOption = forName.getDeclaredMethod("getOption", DatagramSocket.class, SocketOption.class)).setAccessible(true);
        }
        catch (final ReflectiveOperationException ex) {
            throw new InternalError(ex);
        }
    }
    
    private static <T> void invokeSet(final Method method, final Object o, final SocketOption<T> socketOption, final T t) throws IOException {
        try {
            method.invoke(null, o, socketOption, t);
        }
        catch (final Exception ex) {
            if (ex instanceof InvocationTargetException) {
                final Throwable targetException = ((InvocationTargetException)ex).getTargetException();
                if (targetException instanceof IOException) {
                    throw (IOException)targetException;
                }
                if (targetException instanceof RuntimeException) {
                    throw (RuntimeException)targetException;
                }
            }
            throw new RuntimeException(ex);
        }
    }
    
    private static <T> T invokeGet(final Method method, final Object o, final SocketOption<T> socketOption) throws IOException {
        try {
            return (T)method.invoke(null, o, socketOption);
        }
        catch (final Exception ex) {
            if (ex instanceof InvocationTargetException) {
                final Throwable targetException = ((InvocationTargetException)ex).getTargetException();
                if (targetException instanceof IOException) {
                    throw (IOException)targetException;
                }
                if (targetException instanceof RuntimeException) {
                    throw (RuntimeException)targetException;
                }
            }
            throw new RuntimeException(ex);
        }
    }
    
    private Sockets() {
    }
    
    public static <T> void setOption(final Socket socket, final SocketOption<T> socketOption, final T t) throws IOException {
        if (!isSupported(Socket.class, socketOption)) {
            throw new UnsupportedOperationException(socketOption.name());
        }
        invokeSet(Sockets.siSetOption, socket, socketOption, t);
    }
    
    public static <T> T getOption(final Socket socket, final SocketOption<T> socketOption) throws IOException {
        if (!isSupported(Socket.class, socketOption)) {
            throw new UnsupportedOperationException(socketOption.name());
        }
        return invokeGet(Sockets.siGetOption, socket, socketOption);
    }
    
    public static <T> void setOption(final ServerSocket serverSocket, final SocketOption<T> socketOption, final T t) throws IOException {
        if (!isSupported(ServerSocket.class, socketOption)) {
            throw new UnsupportedOperationException(socketOption.name());
        }
        invokeSet(Sockets.siSetOption, serverSocket, socketOption, t);
    }
    
    public static <T> T getOption(final ServerSocket serverSocket, final SocketOption<T> socketOption) throws IOException {
        if (!isSupported(ServerSocket.class, socketOption)) {
            throw new UnsupportedOperationException(socketOption.name());
        }
        return invokeGet(Sockets.siGetOption, serverSocket, socketOption);
    }
    
    public static <T> void setOption(final DatagramSocket datagramSocket, final SocketOption<T> socketOption, final T t) throws IOException {
        if (!isSupported(datagramSocket.getClass(), socketOption)) {
            throw new UnsupportedOperationException(socketOption.name());
        }
        invokeSet(Sockets.dsiSetOption, datagramSocket, socketOption, t);
    }
    
    public static <T> T getOption(final DatagramSocket datagramSocket, final SocketOption<T> socketOption) throws IOException {
        if (!isSupported(datagramSocket.getClass(), socketOption)) {
            throw new UnsupportedOperationException(socketOption.name());
        }
        return invokeGet(Sockets.dsiGetOption, datagramSocket, socketOption);
    }
    
    public static Set<SocketOption<?>> supportedOptions(final Class<?> clazz) {
        final Set set = Sockets.options.get(clazz);
        if (set == null) {
            throw new IllegalArgumentException("unknown socket type");
        }
        return set;
    }
    
    private static boolean isSupported(final Class<?> clazz, final SocketOption<?> socketOption) {
        return supportedOptions(clazz).contains(socketOption);
    }
    
    private static void initOptionSets() {
        final boolean flowSupported = ExtendedOptionsImpl.flowSupported();
        final HashSet set = new HashSet();
        set.add(StandardSocketOptions.SO_KEEPALIVE);
        set.add(StandardSocketOptions.SO_SNDBUF);
        set.add(StandardSocketOptions.SO_RCVBUF);
        set.add(StandardSocketOptions.SO_REUSEADDR);
        set.add(StandardSocketOptions.SO_LINGER);
        set.add(StandardSocketOptions.IP_TOS);
        set.add(StandardSocketOptions.TCP_NODELAY);
        if (flowSupported) {
            set.add(ExtendedSocketOptions.SO_FLOW_SLA);
        }
        set.addAll(ExtendedOptionsHelper.keepAliveOptions());
        Sockets.options.put(Socket.class, (Set<SocketOption<?>>)Collections.unmodifiableSet((Set<?>)set));
        final HashSet set2 = new HashSet();
        set2.add(StandardSocketOptions.SO_RCVBUF);
        set2.add(StandardSocketOptions.SO_REUSEADDR);
        set2.add(StandardSocketOptions.IP_TOS);
        set2.addAll(ExtendedOptionsHelper.keepAliveOptions());
        Sockets.options.put(ServerSocket.class, (Set<SocketOption<?>>)Collections.unmodifiableSet((Set<?>)set2));
        final HashSet set3 = new HashSet();
        set3.add(StandardSocketOptions.SO_SNDBUF);
        set3.add(StandardSocketOptions.SO_RCVBUF);
        set3.add(StandardSocketOptions.SO_REUSEADDR);
        set3.add(StandardSocketOptions.IP_TOS);
        if (flowSupported) {
            set3.add(ExtendedSocketOptions.SO_FLOW_SLA);
        }
        Sockets.options.put(DatagramSocket.class, (Set<SocketOption<?>>)Collections.unmodifiableSet((Set<?>)set3));
        final HashSet set4 = new HashSet();
        set4.add(StandardSocketOptions.SO_SNDBUF);
        set4.add(StandardSocketOptions.SO_RCVBUF);
        set4.add(StandardSocketOptions.SO_REUSEADDR);
        set4.add(StandardSocketOptions.IP_TOS);
        set4.add(StandardSocketOptions.IP_MULTICAST_IF);
        set4.add(StandardSocketOptions.IP_MULTICAST_TTL);
        set4.add(StandardSocketOptions.IP_MULTICAST_LOOP);
        if (flowSupported) {
            set4.add(ExtendedSocketOptions.SO_FLOW_SLA);
        }
        Sockets.options.put(MulticastSocket.class, (Set<SocketOption<?>>)Collections.unmodifiableSet((Set<?>)set4));
    }
    
    static {
        options = new HashMap<Class<?>, Set<SocketOption<?>>>();
        initOptionSets();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                initMethods();
                return null;
            }
        });
    }
}
