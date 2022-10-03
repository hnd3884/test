package com.sun.jndi.ldap;

import java.util.Arrays;
import java.util.Locale;
import java.lang.reflect.Method;
import javax.net.SocketFactory;
import java.io.OutputStream;
import javax.naming.ldap.Control;

class ClientId
{
    private final int version;
    private final String hostname;
    private final int port;
    private final String protocol;
    private final Control[] bindCtls;
    private final OutputStream trace;
    private final String socketFactory;
    private final int myHash;
    private final int ctlHash;
    private SocketFactory factory;
    private Method sockComparator;
    private boolean isDefaultSockFactory;
    public static final boolean debug = false;
    
    ClientId(final int version, final String s, final int port, final String protocol, final Control[] array, final OutputStream trace, final String socketFactory) {
        this.factory = null;
        this.sockComparator = null;
        this.isDefaultSockFactory = false;
        this.version = version;
        this.hostname = s.toLowerCase(Locale.ENGLISH);
        this.port = port;
        this.protocol = protocol;
        this.bindCtls = (Control[])((array != null) ? ((Control[])array.clone()) : null);
        this.trace = trace;
        this.socketFactory = socketFactory;
        if (socketFactory != null && !socketFactory.equals("javax.net.ssl.SSLSocketFactory")) {
            try {
                final Class<?> loadClass = Obj.helper.loadClass(socketFactory);
                final Class<?> forName = Class.forName("java.lang.Object");
                this.sockComparator = loadClass.getMethod("compare", forName, forName);
                this.factory = (SocketFactory)loadClass.getMethod("getDefault", (Class[])new Class[0]).invoke(null, new Object[0]);
            }
            catch (final Exception ex) {}
        }
        else {
            this.isDefaultSockFactory = true;
        }
        final int n = version + port + ((trace != null) ? trace.hashCode() : 0) + ((this.hostname != null) ? this.hostname.hashCode() : 0) + ((protocol != null) ? protocol.hashCode() : 0);
        final int hashCodeControls = hashCodeControls(array);
        this.ctlHash = hashCodeControls;
        this.myHash = n + hashCodeControls;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ClientId)) {
            return false;
        }
        final ClientId clientId = (ClientId)o;
        return this.myHash == clientId.myHash && this.version == clientId.version && this.port == clientId.port && this.trace == clientId.trace && (this.hostname == clientId.hostname || (this.hostname != null && this.hostname.equals(clientId.hostname))) && (this.protocol == clientId.protocol || (this.protocol != null && this.protocol.equals(clientId.protocol))) && this.ctlHash == clientId.ctlHash && equalsControls(this.bindCtls, clientId.bindCtls) && this.equalsSockFactory(clientId);
    }
    
    @Override
    public int hashCode() {
        return this.myHash;
    }
    
    private static int hashCodeControls(final Control[] array) {
        if (array == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            n = n * 31 + array[i].getID().hashCode();
        }
        return n;
    }
    
    private static boolean equalsControls(final Control[] array, final Control[] array2) {
        if (array == array2) {
            return true;
        }
        if (array == null || array2 == null) {
            return false;
        }
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].getID().equals(array2[i].getID()) || array[i].isCritical() != array2[i].isCritical() || !Arrays.equals(array[i].getEncodedValue(), array2[i].getEncodedValue())) {
                return false;
            }
        }
        return true;
    }
    
    private boolean equalsSockFactory(final ClientId clientId) {
        if (this.isDefaultSockFactory && clientId.isDefaultSockFactory) {
            return true;
        }
        if (!clientId.isDefaultSockFactory) {
            return this.invokeComparator(clientId, this);
        }
        return this.invokeComparator(this, clientId);
    }
    
    private boolean invokeComparator(final ClientId clientId, final ClientId clientId2) {
        Object invoke;
        try {
            invoke = clientId.sockComparator.invoke(clientId.factory, clientId.socketFactory, clientId2.socketFactory);
        }
        catch (final Exception ex) {
            return false;
        }
        return (int)invoke == 0;
    }
    
    private static String toStringControls(final Control[] array) {
        if (array == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(array[i].getID());
            sb.append(' ');
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.hostname + ":" + this.port + ":" + ((this.protocol != null) ? this.protocol : "") + ":" + toStringControls(this.bindCtls) + ":" + this.socketFactory;
    }
}
