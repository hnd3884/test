package com.sun.jndi.ldap.sasl;

import java.util.Vector;
import java.util.StringTokenizer;
import java.io.IOException;
import javax.security.sasl.SaslClient;
import java.security.cert.Certificate;
import java.io.OutputStream;
import java.io.InputStream;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import java.util.Map;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSocket;
import javax.security.auth.callback.CallbackHandler;
import com.sun.jndi.ldap.LdapResult;
import javax.naming.ldap.Control;
import com.sun.jndi.ldap.Connection;
import com.sun.jndi.ldap.LdapClient;
import javax.naming.NamingException;
import java.util.Hashtable;

public final class LdapSasl
{
    private static final String SASL_CALLBACK = "java.naming.security.sasl.callback";
    private static final String SASL_AUTHZ_ID = "java.naming.security.sasl.authorizationId";
    private static final String SASL_REALM = "java.naming.security.sasl.realm";
    private static final int LDAP_SUCCESS = 0;
    private static final int LDAP_SASL_BIND_IN_PROGRESS = 14;
    private static final byte[] NO_BYTES;
    
    private LdapSasl() {
    }
    
    public static void checkSaslParameters(final Hashtable<?, ?> hashtable) throws NamingException {
        if (TlsChannelBinding.parseType((String)hashtable.get("com.sun.jndi.ldap.tls.cbtype")) == TlsChannelBinding.TlsChannelBindingType.TLS_SERVER_END_POINT) {
            final Object value = hashtable.get("com.sun.jndi.ldap.connect.timeout");
            int int1 = -1;
            if (value != null) {
                int1 = Integer.parseInt((String)value);
            }
            if (int1 <= 0) {
                throw new NamingException("com.sun.jndi.ldap.tls.cbtype property requires com.sun.jndi.ldap.connect.timeout property is set.");
            }
        }
    }
    
    public static LdapResult saslBind(final LdapClient ldapClient, final Connection connection, final String s, final String s2, final Object o, final String s3, final Hashtable<?, ?> hashtable, final Control[] array) throws IOException, NamingException {
        boolean b = false;
        CallbackHandler callbackHandler = (hashtable != null) ? hashtable.get("java.naming.security.sasl.callback") : null;
        if (callbackHandler == null) {
            callbackHandler = new DefaultCallbackHandler(s2, o, (String)hashtable.get("java.naming.security.sasl.realm"));
            b = true;
        }
        final String s4 = (hashtable != null) ? ((String)hashtable.get("java.naming.security.sasl.authorizationId")) : null;
        final String[] saslMechanismNames = getSaslMechanismNames(s3);
        if (hashtable.get("jdk.internal.sasl.tlschannelbinding") != null) {
            throw new NamingException("jdk.internal.sasl.tlschannelbinding property cannot be set explicitly");
        }
        Hashtable<String, CallbackHandler> hashtable2 = (Hashtable<String, CallbackHandler>)hashtable;
        try {
            if (connection.sock instanceof SSLSocket && TlsChannelBinding.parseType((String)hashtable.get("com.sun.jndi.ldap.tls.cbtype")) == TlsChannelBinding.TlsChannelBindingType.TLS_SERVER_END_POINT) {
                final SSLSocket sslSocket = (SSLSocket)connection.sock;
                Certificate[] array2;
                if (sslSocket.getUseClientMode()) {
                    array2 = sslSocket.getSession().getPeerCertificates();
                }
                else {
                    array2 = sslSocket.getSession().getLocalCertificates();
                }
                if (array2 == null || array2.length <= 0 || !(array2[0] instanceof X509Certificate)) {
                    throw new SaslException("No suitable certificate to generate TLS Channel Binding data");
                }
                final TlsChannelBinding create = TlsChannelBinding.create((X509Certificate)array2[0]);
                hashtable2 = (Hashtable)hashtable.clone();
                hashtable2.put((Object)"jdk.internal.sasl.tlschannelbinding", (Object)create.getData());
            }
            final SaslClient saslClient = Sasl.createSaslClient(saslMechanismNames, s4, "ldap", s, hashtable2, callbackHandler);
            if (saslClient == null) {
                throw new AuthenticationNotSupportedException(s3);
            }
            final String mechanismName = saslClient.getMechanismName();
            LdapResult ldapResult = ldapClient.ldapBind(null, (byte[])(saslClient.hasInitialResponse() ? saslClient.evaluateChallenge(LdapSasl.NO_BYTES) : null), array, mechanismName, true);
            while (!saslClient.isComplete() && (ldapResult.status == 14 || ldapResult.status == 0)) {
                final byte[] evaluateChallenge = saslClient.evaluateChallenge((ldapResult.serverCreds != null) ? ldapResult.serverCreds : LdapSasl.NO_BYTES);
                if (ldapResult.status == 0) {
                    if (evaluateChallenge != null) {
                        throw new AuthenticationException("SASL client generated response after success");
                    }
                    break;
                }
                else {
                    ldapResult = ldapClient.ldapBind(null, evaluateChallenge, array, mechanismName, true);
                }
            }
            if (ldapResult.status == 0) {
                if (!saslClient.isComplete()) {
                    throw new AuthenticationException("SASL authentication not complete despite server claims");
                }
                final String s5 = (String)saslClient.getNegotiatedProperty("javax.security.sasl.qop");
                if (s5 != null && (s5.equalsIgnoreCase("auth-int") || s5.equalsIgnoreCase("auth-conf"))) {
                    connection.replaceStreams(new SaslInputStream(saslClient, connection.inStream), new SaslOutputStream(saslClient, connection.outStream));
                }
                else {
                    saslClient.dispose();
                }
            }
            return ldapResult;
        }
        catch (final SaslException rootCause) {
            final AuthenticationException ex = new AuthenticationException(s3);
            ex.setRootCause(rootCause);
            throw ex;
        }
        finally {
            if (b) {
                ((DefaultCallbackHandler)callbackHandler).clearPassword();
            }
        }
    }
    
    private static String[] getSaslMechanismNames(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s);
        final Vector vector = new Vector(10);
        while (stringTokenizer.hasMoreTokens()) {
            vector.addElement(stringTokenizer.nextToken());
        }
        final String[] array = new String[vector.size()];
        for (int i = 0; i < vector.size(); ++i) {
            array[i] = (String)vector.elementAt(i);
        }
        return array;
    }
    
    static {
        NO_BYTES = new byte[0];
    }
}
