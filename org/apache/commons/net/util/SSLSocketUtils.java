package org.apache.commons.net.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import javax.net.ssl.SSLSocket;

public class SSLSocketUtils
{
    private SSLSocketUtils() {
    }
    
    public static boolean enableEndpointNameVerification(final SSLSocket socket) {
        try {
            final Class<?> cls = Class.forName("javax.net.ssl.SSLParameters");
            final Method setEndpointIdentificationAlgorithm = cls.getDeclaredMethod("setEndpointIdentificationAlgorithm", String.class);
            final Method getSSLParameters = SSLSocket.class.getDeclaredMethod("getSSLParameters", (Class<?>[])new Class[0]);
            final Method setSSLParameters = SSLSocket.class.getDeclaredMethod("setSSLParameters", cls);
            if (setEndpointIdentificationAlgorithm != null && getSSLParameters != null && setSSLParameters != null) {
                final Object sslParams = getSSLParameters.invoke(socket, new Object[0]);
                if (sslParams != null) {
                    setEndpointIdentificationAlgorithm.invoke(sslParams, "HTTPS");
                    setSSLParameters.invoke(socket, sslParams);
                    return true;
                }
            }
        }
        catch (final SecurityException e) {}
        catch (final ClassNotFoundException e2) {}
        catch (final NoSuchMethodException e3) {}
        catch (final IllegalArgumentException e4) {}
        catch (final IllegalAccessException e5) {}
        catch (final InvocationTargetException ex) {}
        return false;
    }
}
