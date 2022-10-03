package com.unboundid.ldap.sdk.migrate.ldapjdk;

import java.net.InetAddress;
import java.io.IOException;
import com.unboundid.util.Debug;
import java.net.Socket;
import javax.net.SocketFactory;

final class LDAPToJavaSocketFactory extends SocketFactory
{
    private final LDAPSocketFactory f;
    
    LDAPToJavaSocketFactory(final LDAPSocketFactory f) {
        this.f = f;
    }
    
    @Override
    public Socket createSocket(final String host, final int port) throws IOException {
        if (this.f instanceof SocketFactory) {
            synchronized (this.f) {
                return ((SocketFactory)this.f).createSocket(host, port);
            }
        }
        try {
            synchronized (this.f) {
                return this.f.makeSocket(host, port);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new IOException(null, e);
        }
    }
    
    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort) throws IOException {
        if (this.f instanceof SocketFactory) {
            synchronized (this.f) {
                return ((SocketFactory)this.f).createSocket(host, port, localAddress, localPort);
            }
        }
        return this.createSocket(host, port);
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port) throws IOException {
        if (this.f instanceof SocketFactory) {
            synchronized (this.f) {
                return ((SocketFactory)this.f).createSocket(address, port);
            }
        }
        return this.createSocket(address.getHostAddress(), port);
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort) throws IOException {
        if (this.f instanceof SocketFactory) {
            synchronized (this.f) {
                return ((SocketFactory)this.f).createSocket(address, port, localAddress, localPort);
            }
        }
        return this.createSocket(address.getHostAddress(), port);
    }
}
