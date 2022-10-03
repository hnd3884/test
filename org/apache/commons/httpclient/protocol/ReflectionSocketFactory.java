package org.apache.commons.httpclient.protocol;

import java.net.UnknownHostException;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import org.apache.commons.httpclient.ConnectTimeoutException;
import java.net.Socket;
import java.net.InetAddress;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public final class ReflectionSocketFactory
{
    private static boolean REFLECTION_FAILED;
    private static Constructor INETSOCKETADDRESS_CONSTRUCTOR;
    private static Method SOCKETCONNECT_METHOD;
    private static Method SOCKETBIND_METHOD;
    private static Class SOCKETTIMEOUTEXCEPTION_CLASS;
    
    private ReflectionSocketFactory() {
    }
    
    public static Socket createSocket(final String socketfactoryName, final String host, final int port, final InetAddress localAddress, final int localPort, final int timeout) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (ReflectionSocketFactory.REFLECTION_FAILED) {
            return null;
        }
        try {
            final Class socketfactoryClass = Class.forName(socketfactoryName);
            Method method = socketfactoryClass.getMethod("getDefault", (Class[])new Class[0]);
            final Object socketfactory = method.invoke(null, new Object[0]);
            method = socketfactoryClass.getMethod("createSocket", (Class[])new Class[0]);
            final Socket socket = (Socket)method.invoke(socketfactory, new Object[0]);
            if (ReflectionSocketFactory.INETSOCKETADDRESS_CONSTRUCTOR == null) {
                final Class addressClass = Class.forName("java.net.InetSocketAddress");
                ReflectionSocketFactory.INETSOCKETADDRESS_CONSTRUCTOR = addressClass.getConstructor(InetAddress.class, Integer.TYPE);
            }
            final Object remoteaddr = ReflectionSocketFactory.INETSOCKETADDRESS_CONSTRUCTOR.newInstance(InetAddress.getByName(host), new Integer(port));
            final Object localaddr = ReflectionSocketFactory.INETSOCKETADDRESS_CONSTRUCTOR.newInstance(localAddress, new Integer(localPort));
            if (ReflectionSocketFactory.SOCKETCONNECT_METHOD == null) {
                ReflectionSocketFactory.SOCKETCONNECT_METHOD = Socket.class.getMethod("connect", Class.forName("java.net.SocketAddress"), Integer.TYPE);
            }
            if (ReflectionSocketFactory.SOCKETBIND_METHOD == null) {
                ReflectionSocketFactory.SOCKETBIND_METHOD = Socket.class.getMethod("bind", Class.forName("java.net.SocketAddress"));
            }
            ReflectionSocketFactory.SOCKETBIND_METHOD.invoke(socket, localaddr);
            ReflectionSocketFactory.SOCKETCONNECT_METHOD.invoke(socket, remoteaddr, new Integer(timeout));
            return socket;
        }
        catch (final InvocationTargetException e) {
            final Throwable cause = e.getTargetException();
            if (ReflectionSocketFactory.SOCKETTIMEOUTEXCEPTION_CLASS == null) {
                try {
                    ReflectionSocketFactory.SOCKETTIMEOUTEXCEPTION_CLASS = Class.forName("java.net.SocketTimeoutException");
                }
                catch (final ClassNotFoundException ex) {
                    ReflectionSocketFactory.REFLECTION_FAILED = true;
                    return null;
                }
            }
            if (ReflectionSocketFactory.SOCKETTIMEOUTEXCEPTION_CLASS.isInstance(cause)) {
                throw new ConnectTimeoutException("The host did not accept the connection within timeout of " + timeout + " ms", cause);
            }
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            return null;
        }
        catch (final Exception e2) {
            ReflectionSocketFactory.REFLECTION_FAILED = true;
            return null;
        }
    }
    
    static {
        ReflectionSocketFactory.REFLECTION_FAILED = false;
        ReflectionSocketFactory.INETSOCKETADDRESS_CONSTRUCTOR = null;
        ReflectionSocketFactory.SOCKETCONNECT_METHOD = null;
        ReflectionSocketFactory.SOCKETBIND_METHOD = null;
        ReflectionSocketFactory.SOCKETTIMEOUTEXCEPTION_CLASS = null;
    }
}
