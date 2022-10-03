package com.oracle.net;

import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import sun.nio.ch.Secrets;
import sun.net.sdp.SdpSupport;
import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.net.Socket;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.AccessibleObject;
import java.net.SocketImpl;
import java.net.ServerSocket;
import java.lang.reflect.Constructor;

public final class Sdp
{
    private static final Constructor<ServerSocket> serverSocketCtor;
    private static final Constructor<SocketImpl> socketImplCtor;
    
    private Sdp() {
    }
    
    private static void setAccessible(final AccessibleObject accessibleObject) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                accessibleObject.setAccessible(true);
                return null;
            }
        });
    }
    
    private static SocketImpl createSocketImpl() {
        try {
            return Sdp.socketImplCtor.newInstance(new Object[0]);
        }
        catch (final InstantiationException ex) {
            throw new AssertionError((Object)ex);
        }
        catch (final IllegalAccessException ex2) {
            throw new AssertionError((Object)ex2);
        }
        catch (final InvocationTargetException ex3) {
            throw new AssertionError((Object)ex3);
        }
    }
    
    public static Socket openSocket() throws IOException {
        return new SdpSocket(createSocketImpl());
    }
    
    public static ServerSocket openServerSocket() throws IOException {
        final SocketImpl socketImpl = createSocketImpl();
        try {
            return Sdp.serverSocketCtor.newInstance(socketImpl);
        }
        catch (final IllegalAccessException ex) {
            throw new AssertionError((Object)ex);
        }
        catch (final InstantiationException ex2) {
            throw new AssertionError((Object)ex2);
        }
        catch (final InvocationTargetException ex3) {
            final Throwable cause = ex3.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new RuntimeException(ex3);
        }
    }
    
    public static SocketChannel openSocketChannel() throws IOException {
        return Secrets.newSocketChannel(SdpSupport.createSocket());
    }
    
    public static ServerSocketChannel openServerSocketChannel() throws IOException {
        return Secrets.newServerSocketChannel(SdpSupport.createSocket());
    }
    
    static {
        try {
            setAccessible(serverSocketCtor = ServerSocket.class.getDeclaredConstructor(SocketImpl.class));
        }
        catch (final NoSuchMethodException ex) {
            throw new AssertionError((Object)ex);
        }
        try {
            setAccessible(socketImplCtor = Class.forName("java.net.SdpSocketImpl", true, null).getDeclaredConstructor((Class<?>[])new Class[0]));
        }
        catch (final ClassNotFoundException ex2) {
            throw new AssertionError((Object)ex2);
        }
        catch (final NoSuchMethodException ex3) {
            throw new AssertionError((Object)ex3);
        }
    }
    
    private static class SdpSocket extends Socket
    {
        SdpSocket(final SocketImpl socketImpl) throws SocketException {
            super(socketImpl);
        }
    }
}
