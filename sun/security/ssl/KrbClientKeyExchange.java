package sun.security.ssl;

import java.util.Iterator;
import javax.crypto.SecretKey;
import java.security.spec.AlgorithmParameterSpec;
import javax.net.ssl.SNIHostName;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.security.Principal;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.security.AccessControlContext;
import java.lang.reflect.InvocationTargetException;

final class KrbClientKeyExchange
{
    static final SSLConsumer krbHandshakeConsumer;
    static final HandshakeProducer krbHandshakeProducer;
    
    static {
        krbHandshakeConsumer = new KrbClientKeyExchangeConsumer();
        krbHandshakeProducer = new KrbClientKeyExchangeProducer();
    }
    
    private static final class KrbClientKeyExchangeMessage extends SSLHandshake.HandshakeMessage
    {
        private static final String KRB5_CLASS_NAME = "sun.security.ssl.krb5.KrbClientKeyExchangeHelperImpl";
        private static final Class<?> krb5Class;
        private final KrbClientKeyExchangeHelper krb5Helper;
        
        private static KrbClientKeyExchangeHelper newKrb5Instance() {
            if (KrbClientKeyExchangeMessage.krb5Class != null) {
                try {
                    return (KrbClientKeyExchangeHelper)KrbClientKeyExchangeMessage.krb5Class.getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                }
                catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                    throw new AssertionError(ex);
                }
            }
            return null;
        }
        
        private KrbClientKeyExchangeMessage(final HandshakeContext handshakeContext) {
            super(handshakeContext);
            final KrbClientKeyExchangeHelper krb5Instance = newKrb5Instance();
            this.krb5Helper = krb5Instance;
            if (krb5Instance == null) {
                throw new IllegalStateException("Kerberos is unavailable");
            }
        }
        
        KrbClientKeyExchangeMessage(final HandshakeContext handshakeContext, final byte[] array, final String s, final AccessControlContext accessControlContext) throws IOException {
            this(handshakeContext);
            this.krb5Helper.init(array, s, accessControlContext);
        }
        
        KrbClientKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer, final Object o, final AccessControlContext accessControlContext) throws IOException {
            this(handshakeContext);
            final byte[] bytes16 = Record.getBytes16(byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("encoded Kerberos service ticket", bytes16);
            }
            Record.getBytes16(byteBuffer);
            final byte[] bytes17 = Record.getBytes16(byteBuffer);
            if (bytes17 != null && SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("encrypted Kerberos pre-master secret", bytes17);
            }
            this.krb5Helper.init(bytes16, bytes17, o, accessControlContext);
        }
        
        @Override
        SSLHandshake handshakeType() {
            return SSLHandshake.CLIENT_KEY_EXCHANGE;
        }
        
        @Override
        int messageLength() {
            return 6 + this.krb5Helper.getEncodedTicket().length + this.krb5Helper.getEncryptedPreMasterSecret().length;
        }
        
        @Override
        void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putBytes16(this.krb5Helper.getEncodedTicket());
            handshakeOutStream.putBytes16(null);
            handshakeOutStream.putBytes16(this.krb5Helper.getEncryptedPreMasterSecret());
        }
        
        byte[] getPlainPreMasterSecret() {
            return this.krb5Helper.getPlainPreMasterSecret();
        }
        
        Principal getPeerPrincipal() {
            return this.krb5Helper.getPeerPrincipal();
        }
        
        Principal getLocalPrincipal() {
            return this.krb5Helper.getLocalPrincipal();
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"KRB5 ClientKeyExchange\": '{'\n  \"ticket\": '{'\n{0}\n  '}'\n  \"pre-master\": '{'\n    \"plain\": '{'\n{1}\n    '}'\n    \"encrypted\": '{'\n{2}\n    '}'\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
            return messageFormat.format(new Object[] { Utilities.indent(hexDumpEncoder.encodeBuffer(this.krb5Helper.getEncodedTicket()), "  "), Utilities.indent(hexDumpEncoder.encodeBuffer(this.krb5Helper.getPlainPreMasterSecret()), "      "), Utilities.indent(hexDumpEncoder.encodeBuffer(this.krb5Helper.getEncryptedPreMasterSecret()), "      ") });
        }
        
        static {
            krb5Class = AccessController.doPrivileged((PrivilegedAction<Class<?>>)new PrivilegedAction<Class<?>>() {
                @Override
                public Class<?> run() {
                    try {
                        return Class.forName("sun.security.ssl.krb5.KrbClientKeyExchangeHelperImpl", true, null);
                    }
                    catch (final ClassNotFoundException ex) {
                        return null;
                    }
                }
            });
        }
    }
    
    private static final class KrbClientKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            String s = null;
            if (clientHandshakeContext.negotiatedServerName != null) {
                if (clientHandshakeContext.negotiatedServerName.getType() == 0) {
                    SNIHostName sniHostName = null;
                    if (clientHandshakeContext.negotiatedServerName instanceof SNIHostName) {
                        sniHostName = (SNIHostName)clientHandshakeContext.negotiatedServerName;
                    }
                    else {
                        try {
                            sniHostName = new SNIHostName(clientHandshakeContext.negotiatedServerName.getEncoded());
                        }
                        catch (final IllegalArgumentException ex) {}
                    }
                    if (sniHostName != null) {
                        s = sniHostName.getAsciiName();
                    }
                }
            }
            else {
                s = clientHandshakeContext.handshakeSession.getPeerHost();
            }
            KrbClientKeyExchangeMessage krbClientKeyExchangeMessage;
            try {
                final KrbKeyExchange.KrbPremasterSecret premasterSecret = KrbKeyExchange.KrbPremasterSecret.createPremasterSecret(clientHandshakeContext.negotiatedProtocol, clientHandshakeContext.sslContext.getSecureRandom());
                krbClientKeyExchangeMessage = new KrbClientKeyExchangeMessage(clientHandshakeContext, premasterSecret.preMaster, s, clientHandshakeContext.conContext.acc);
                clientHandshakeContext.handshakePossessions.add(premasterSecret);
            }
            catch (final IOException ex2) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Error generating KRB premaster secret. Hostname: " + s + " - Negotiated server name: " + clientHandshakeContext.negotiatedServerName, new Object[0]);
                }
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Cannot generate KRB premaster secret", ex2);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced KRB5 ClientKeyExchange handshake message", krbClientKeyExchangeMessage);
            }
            clientHandshakeContext.handshakeSession.setPeerPrincipal(krbClientKeyExchangeMessage.getPeerPrincipal());
            clientHandshakeContext.handshakeSession.setLocalPrincipal(krbClientKeyExchangeMessage.getLocalPrincipal());
            krbClientKeyExchangeMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            final SSLKeyExchange value = SSLKeyExchange.valueOf(clientHandshakeContext.negotiatedCipherSuite.keyExchange, clientHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final SecretKey deriveKey = value.createKeyDerivation(clientHandshakeContext).deriveKey("MasterSecret", null);
            clientHandshakeContext.handshakeSession.setMasterSecret(deriveKey);
            final SSLTrafficKeyDerivation value2 = SSLTrafficKeyDerivation.valueOf(clientHandshakeContext.negotiatedProtocol);
            if (value2 == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + clientHandshakeContext.negotiatedProtocol);
            }
            clientHandshakeContext.handshakeKeyDerivation = value2.createKeyDerivation(clientHandshakeContext, deriveKey);
            return null;
        }
    }
    
    private static final class KrbClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            Object serviceCreds = null;
            for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof KrbKeyExchange.KrbServiceCreds) {
                    serviceCreds = ((KrbKeyExchange.KrbServiceCreds)sslPossession).serviceCreds;
                    break;
                }
            }
            if (serviceCreds == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No Kerberos service credentials for KRB Client Key Exchange");
            }
            final KrbClientKeyExchangeMessage krbClientKeyExchangeMessage = new KrbClientKeyExchangeMessage(serverHandshakeContext, byteBuffer, serviceCreds, serverHandshakeContext.conContext.acc);
            final KrbKeyExchange.KrbPremasterSecret decode = KrbKeyExchange.KrbPremasterSecret.decode(serverHandshakeContext.negotiatedProtocol, ProtocolVersion.valueOf(serverHandshakeContext.clientHelloVersion), krbClientKeyExchangeMessage.getPlainPreMasterSecret(), serverHandshakeContext.sslContext.getSecureRandom());
            serverHandshakeContext.handshakeSession.setPeerPrincipal(krbClientKeyExchangeMessage.getPeerPrincipal());
            serverHandshakeContext.handshakeSession.setLocalPrincipal(krbClientKeyExchangeMessage.getLocalPrincipal());
            serverHandshakeContext.handshakeCredentials.add(decode);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming KRB5 ClientKeyExchange handshake message", krbClientKeyExchangeMessage);
            }
            final SSLKeyExchange value = SSLKeyExchange.valueOf(serverHandshakeContext.negotiatedCipherSuite.keyExchange, serverHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final SecretKey deriveKey = value.createKeyDerivation(serverHandshakeContext).deriveKey("MasterSecret", null);
            serverHandshakeContext.handshakeSession.setMasterSecret(deriveKey);
            final SSLTrafficKeyDerivation value2 = SSLTrafficKeyDerivation.valueOf(serverHandshakeContext.negotiatedProtocol);
            if (value2 == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + serverHandshakeContext.negotiatedProtocol);
            }
            serverHandshakeContext.handshakeKeyDerivation = value2.createKeyDerivation(serverHandshakeContext, deriveKey);
        }
    }
}
