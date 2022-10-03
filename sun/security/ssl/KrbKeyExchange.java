package sun.security.ssl;

import javax.net.ssl.SSLHandshakeException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.AlgorithmParameterSpec;
import java.io.IOException;
import java.util.Iterator;
import java.security.SecureRandom;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedExceptionAction;

final class KrbKeyExchange
{
    static final SSLPossessionGenerator poGenerator;
    static final SSLKeyAgreementGenerator kaGenerator;
    
    static {
        poGenerator = new KrbPossessionGenerator();
        kaGenerator = new KrbKAGenerator();
    }
    
    static final class KrbPossessionGenerator implements SSLPossessionGenerator
    {
        @Override
        public SSLPossession createPossession(final HandshakeContext handshakeContext) {
            try {
                final AccessControlContext acc = handshakeContext.conContext.acc;
                final Object doPrivileged = AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                    @Override
                    public Object run() throws Exception {
                        return Krb5Helper.getServiceCreds(acc);
                    }
                });
                if (doPrivileged != null) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Using Kerberos creds", new Object[0]);
                    }
                    final String serverPrincipalName = Krb5Helper.getServerPrincipalName(doPrivileged);
                    if (serverPrincipalName != null) {
                        final SecurityManager securityManager = System.getSecurityManager();
                        try {
                            if (securityManager != null) {
                                securityManager.checkPermission(Krb5Helper.getServicePermission(serverPrincipalName, "accept"), acc);
                            }
                        }
                        catch (final SecurityException ex) {
                            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                                SSLLogger.fine("Permission to access Kerberos secret key denied", new Object[0]);
                            }
                            return null;
                        }
                    }
                    return new KrbServiceCreds(doPrivileged);
                }
            }
            catch (final PrivilegedActionException ex2) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Attempt to obtain Kerberos key failed: " + ex2.toString(), new Object[0]);
                }
            }
            return null;
        }
    }
    
    static final class KrbServiceCreds implements SSLPossession
    {
        final Object serviceCreds;
        
        KrbServiceCreds(final Object serviceCreds) {
            this.serviceCreds = serviceCreds;
        }
    }
    
    static final class KrbPremasterSecret implements SSLPossession, SSLCredentials
    {
        final byte[] preMaster;
        
        KrbPremasterSecret(final byte[] preMaster) {
            this.preMaster = preMaster;
        }
        
        static KrbPremasterSecret createPremasterSecret(final ProtocolVersion protocolVersion, final SecureRandom secureRandom) {
            final byte[] array = new byte[48];
            secureRandom.nextBytes(array);
            array[0] = protocolVersion.major;
            array[1] = protocolVersion.minor;
            return new KrbPremasterSecret(array);
        }
        
        static KrbPremasterSecret decode(final ProtocolVersion protocolVersion, final ProtocolVersion protocolVersion2, final byte[] array, final SecureRandom secureRandom) {
            boolean b = true;
            if (array != null && array.length == 48) {
                final ProtocolVersion value = ProtocolVersion.valueOf(array[0], array[1]);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Kerberos pre-master secret protocol version: " + value, new Object[0]);
                }
                b = (value.compare(protocolVersion2) != 0);
                if (b && protocolVersion2.compare(ProtocolVersion.TLS10) <= 0) {
                    b = (value.compare(protocolVersion) != 0);
                }
            }
            KrbPremasterSecret premasterSecret;
            if (b) {
                premasterSecret = createPremasterSecret(protocolVersion2, secureRandom);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Kerberos pre-master secret error, generating random secret for safe failure.", new Object[0]);
                }
            }
            else {
                premasterSecret = new KrbPremasterSecret(array);
            }
            return premasterSecret;
        }
    }
    
    private static final class KrbKAGenerator implements SSLKeyAgreementGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext) throws IOException {
            KrbPremasterSecret krbPremasterSecret = null;
            if (handshakeContext instanceof ClientHandshakeContext) {
                for (final SSLPossession sslPossession : handshakeContext.handshakePossessions) {
                    if (sslPossession instanceof KrbPremasterSecret) {
                        krbPremasterSecret = (KrbPremasterSecret)sslPossession;
                        break;
                    }
                }
            }
            else {
                for (final SSLCredentials sslCredentials : handshakeContext.handshakeCredentials) {
                    if (sslCredentials instanceof KrbPremasterSecret) {
                        krbPremasterSecret = (KrbPremasterSecret)sslCredentials;
                        break;
                    }
                }
            }
            if (krbPremasterSecret == null) {
                throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient KRB key agreement parameters negotiated");
            }
            return new KRBKAKeyDerivation(handshakeContext, krbPremasterSecret.preMaster);
        }
        
        private static final class KRBKAKeyDerivation implements SSLKeyDerivation
        {
            private final HandshakeContext context;
            private final byte[] secretBytes;
            
            KRBKAKeyDerivation(final HandshakeContext context, final byte[] secretBytes) {
                this.context = context;
                this.secretBytes = secretBytes;
            }
            
            @Override
            public SecretKey deriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
                try {
                    final SecretKeySpec secretKeySpec = new SecretKeySpec(this.secretBytes, "TlsPremasterSecret");
                    final SSLMasterKeyDerivation value = SSLMasterKeyDerivation.valueOf(this.context.negotiatedProtocol);
                    if (value == null) {
                        throw new SSLHandshakeException("No expected master key derivation for protocol: " + this.context.negotiatedProtocol.name);
                    }
                    return value.createKeyDerivation(this.context, secretKeySpec).deriveKey("MasterSecret", algorithmParameterSpec);
                }
                catch (final Exception ex) {
                    throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(ex);
                }
            }
        }
    }
}
