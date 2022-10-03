package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.net.InetAddress;
import java.io.IOException;
import java.net.Socket;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import javax.net.SocketFactory;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JavaToLDAPSocketFactory extends SocketFactory implements LDAPSocketFactory
{
    private final SocketFactory f;
    
    public JavaToLDAPSocketFactory(final SocketFactory f) {
        this.f = f;
    }
    
    @Override
    public Socket createSocket(final String host, final int port) throws IOException {
        synchronized (this.f) {
            return this.f.createSocket(host, port);
        }
    }
    
    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort) throws IOException {
        synchronized (this.f) {
            return this.f.createSocket(host, port, localAddress, localPort);
        }
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port) throws IOException {
        synchronized (this.f) {
            return this.f.createSocket(address, port);
        }
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort) throws IOException {
        synchronized (this.f) {
            return this.f.createSocket(address, port, localAddress, localPort);
        }
    }
    
    @Override
    public Socket makeSocket(final String host, final int port) throws LDAPException {
        try {
            synchronized (this.f) {
                return this.f.createSocket(host, port);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(StaticUtils.getExceptionMessage(e), 91);
        }
    }
}
