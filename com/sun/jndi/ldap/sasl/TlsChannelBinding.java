package com.sun.jndi.ldap.sasl;

import java.security.cert.CertificateEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import javax.security.sasl.SaslException;
import javax.naming.NamingException;

public class TlsChannelBinding
{
    public static final String CHANNEL_BINDING_TYPE = "com.sun.jndi.ldap.tls.cbtype";
    public static final String CHANNEL_BINDING = "jdk.internal.sasl.tlschannelbinding";
    private final TlsChannelBindingType cbType;
    private final byte[] cbData;
    
    public static TlsChannelBindingType parseType(final String s) throws NamingException {
        if (s == null) {
            return null;
        }
        if (s.equals(TlsChannelBindingType.TLS_UNIQUE.getName())) {
            throw new NamingException("Channel binding type " + TlsChannelBindingType.TLS_UNIQUE.getName() + " is not supported");
        }
        if (s.equals(TlsChannelBindingType.TLS_SERVER_END_POINT.getName())) {
            return TlsChannelBindingType.TLS_SERVER_END_POINT;
        }
        throw new NamingException("Illegal value for com.sun.jndi.ldap.tls.cbtype property.");
    }
    
    public static TlsChannelBinding create(final byte[] array) throws SaslException {
        throw new UnsupportedOperationException("tls-unique channel binding is not supported");
    }
    
    public static TlsChannelBinding create(final X509Certificate x509Certificate) throws SaslException {
        try {
            final byte[] bytes = "tls-server-end-point:".getBytes();
            final String upperCase = x509Certificate.getSigAlgName().replace("SHA", "SHA-").toUpperCase();
            final int index = upperCase.indexOf("WITH");
            String substring;
            if (index > 0) {
                substring = upperCase.substring(0, index);
                if (substring.equals("MD5") || substring.equals("SHA-1")) {
                    substring = "SHA-256";
                }
            }
            else {
                substring = "SHA-256";
            }
            final byte[] digest = MessageDigest.getInstance(substring).digest(x509Certificate.getEncoded());
            final byte[] copy = Arrays.copyOf(bytes, bytes.length + digest.length);
            System.arraycopy(digest, 0, copy, bytes.length, digest.length);
            return new TlsChannelBinding(TlsChannelBindingType.TLS_SERVER_END_POINT, copy);
        }
        catch (final NoSuchAlgorithmException | CertificateEncodingException ex) {
            throw new SaslException("Cannot create TLS channel binding data", (Throwable)ex);
        }
    }
    
    private TlsChannelBinding(final TlsChannelBindingType cbType, final byte[] cbData) {
        this.cbType = cbType;
        this.cbData = cbData;
    }
    
    public TlsChannelBindingType getType() {
        return this.cbType;
    }
    
    public byte[] getData() {
        return this.cbData;
    }
    
    public enum TlsChannelBindingType
    {
        TLS_UNIQUE("tls-unique"), 
        TLS_SERVER_END_POINT("tls-server-end-point");
        
        private final String name;
        
        public String getName() {
            return this.name;
        }
        
        private TlsChannelBindingType(final String name) {
            this.name = name;
        }
    }
}
