package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Principal;
import java.io.PrintStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.AccessControlContext;

public class KerberosClientKeyExchange extends HandshakeMessage
{
    private static final String IMPL_CLASS = "org.openjsse.legacy8ujsse.sun.security.ssl.krb5.KerberosClientKeyExchangeImpl";
    private static final Class<?> implClass;
    private final KerberosClientKeyExchange impl;
    
    private KerberosClientKeyExchange createImpl() {
        if (KerberosClientKeyExchange.implClass != null && this.getClass() == KerberosClientKeyExchange.class) {
            try {
                return (KerberosClientKeyExchange)KerberosClientKeyExchange.implClass.newInstance();
            }
            catch (final InstantiationException e) {
                throw new AssertionError((Object)e);
            }
            catch (final IllegalAccessException e2) {
                throw new AssertionError((Object)e2);
            }
        }
        return null;
    }
    
    protected KerberosClientKeyExchange() {
        this.impl = this.createImpl();
    }
    
    public KerberosClientKeyExchange(final String serverName, final AccessControlContext acc, final ProtocolVersion protocolVersion, final SecureRandom rand) throws IOException {
        this.impl = this.createImpl();
        if (this.impl != null) {
            this.init(serverName, acc, protocolVersion, rand);
            return;
        }
        throw new IllegalStateException("Kerberos is unavailable");
    }
    
    public KerberosClientKeyExchange(final ProtocolVersion protocolVersion, final ProtocolVersion clientVersion, final SecureRandom rand, final HandshakeInStream input, final AccessControlContext acc, final Object serverKeys) throws IOException {
        this.impl = this.createImpl();
        if (this.impl != null) {
            this.init(protocolVersion, clientVersion, rand, input, acc, serverKeys);
            return;
        }
        throw new IllegalStateException("Kerberos is unavailable");
    }
    
    @Override
    int messageType() {
        return 16;
    }
    
    public int messageLength() {
        return this.impl.messageLength();
    }
    
    public void send(final HandshakeOutStream s) throws IOException {
        this.impl.send(s);
    }
    
    public void print(final PrintStream p) throws IOException {
        this.impl.print(p);
    }
    
    public void init(final String serverName, final AccessControlContext acc, final ProtocolVersion protocolVersion, final SecureRandom rand) throws IOException {
        if (this.impl != null) {
            this.impl.init(serverName, acc, protocolVersion, rand);
        }
    }
    
    public void init(final ProtocolVersion protocolVersion, final ProtocolVersion clientVersion, final SecureRandom rand, final HandshakeInStream input, final AccessControlContext acc, final Object ServiceCreds) throws IOException {
        if (this.impl != null) {
            this.impl.init(protocolVersion, clientVersion, rand, input, acc, ServiceCreds);
        }
    }
    
    public byte[] getUnencryptedPreMasterSecret() {
        return this.impl.getUnencryptedPreMasterSecret();
    }
    
    public Principal getPeerPrincipal() {
        return this.impl.getPeerPrincipal();
    }
    
    public Principal getLocalPrincipal() {
        return this.impl.getLocalPrincipal();
    }
    
    static {
        implClass = AccessController.doPrivileged((PrivilegedAction<Class<?>>)new PrivilegedAction<Class<?>>() {
            @Override
            public Class<?> run() {
                try {
                    return Class.forName("org.openjsse.legacy8ujsse.sun.security.ssl.krb5.KerberosClientKeyExchangeImpl");
                }
                catch (final ClassNotFoundException cnf) {
                    return null;
                }
            }
        });
    }
}
