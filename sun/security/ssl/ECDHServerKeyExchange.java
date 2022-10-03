package sun.security.ssl;

import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.PrivateKey;
import java.security.PublicKey;
import sun.misc.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.ECPublicKeySpec;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Map;
import java.security.spec.ECParameterSpec;
import java.util.Iterator;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;

final class ECDHServerKeyExchange
{
    static final SSLConsumer ecdheHandshakeConsumer;
    static final HandshakeProducer ecdheHandshakeProducer;
    
    static {
        ecdheHandshakeConsumer = new ECDHServerKeyExchangeConsumer();
        ecdheHandshakeProducer = new ECDHServerKeyExchangeProducer();
    }
    
    private static final class ECDHServerKeyExchangeMessage extends SSLHandshake.HandshakeMessage
    {
        private static final byte CURVE_NAMED_CURVE = 3;
        private final SupportedGroupsExtension.NamedGroup namedGroup;
        private final byte[] publicPoint;
        private final byte[] paramsSignature;
        private final ECPublicKey publicKey;
        private final boolean useExplicitSigAlgorithm;
        private final SignatureScheme signatureScheme;
        
        ECDHServerKeyExchangeMessage(final HandshakeContext handshakeContext) throws IOException {
            super(handshakeContext);
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)handshakeContext;
            ECDHKeyExchange.ECDHEPossession ecdhePossession = null;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof ECDHKeyExchange.ECDHEPossession) {
                    ecdhePossession = (ECDHKeyExchange.ECDHEPossession)sslPossession;
                    if (x509Possession != null) {
                        break;
                    }
                    continue;
                }
                else {
                    if (!(sslPossession instanceof X509Authentication.X509Possession)) {
                        continue;
                    }
                    x509Possession = (X509Authentication.X509Possession)sslPossession;
                    if (ecdhePossession != null) {
                        break;
                    }
                    continue;
                }
            }
            if (ecdhePossession == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No ECDHE credentials negotiated for server key exchange");
            }
            this.publicKey = ecdhePossession.publicKey;
            final ECParameterSpec params = this.publicKey.getParams();
            this.publicPoint = JsseJce.encodePoint(this.publicKey.getW(), params.getCurve());
            this.namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(params);
            if (this.namedGroup == null || this.namedGroup.oid == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unnamed EC parameter spec: " + params);
            }
            if (x509Possession == null) {
                this.paramsSignature = null;
                this.signatureScheme = null;
                this.useExplicitSigAlgorithm = false;
            }
            else {
                this.useExplicitSigAlgorithm = serverHandshakeContext.negotiatedProtocol.useTLS12PlusSpec();
                Signature signature;
                if (this.useExplicitSigAlgorithm) {
                    final Map.Entry<SignatureScheme, Signature> signerOfPreferableAlgorithm = SignatureScheme.getSignerOfPreferableAlgorithm(serverHandshakeContext.algorithmConstraints, serverHandshakeContext.peerRequestedSignatureSchemes, x509Possession, serverHandshakeContext.negotiatedProtocol);
                    if (signerOfPreferableAlgorithm == null) {
                        throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "No supported signature algorithm for " + x509Possession.popPrivateKey.getAlgorithm() + "  key");
                    }
                    this.signatureScheme = signerOfPreferableAlgorithm.getKey();
                    signature = signerOfPreferableAlgorithm.getValue();
                }
                else {
                    this.signatureScheme = null;
                    try {
                        signature = getSignature(x509Possession.popPrivateKey.getAlgorithm(), x509Possession.popPrivateKey);
                    }
                    catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
                        throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm: " + x509Possession.popPrivateKey.getAlgorithm(), (Throwable)ex);
                    }
                }
                byte[] sign;
                try {
                    updateSignature(signature, serverHandshakeContext.clientHelloRandom.randomBytes, serverHandshakeContext.serverHelloRandom.randomBytes, this.namedGroup.id, this.publicPoint);
                    sign = signature.sign();
                }
                catch (final SignatureException ex2) {
                    throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Failed to sign ecdhe parameters: " + x509Possession.popPrivateKey.getAlgorithm(), ex2);
                }
                this.paramsSignature = sign;
            }
        }
        
        ECDHServerKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)handshakeContext;
            final byte b = (byte)Record.getInt8(byteBuffer);
            if (b != 3) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported ECCurveType: " + b);
            }
            final int int16 = Record.getInt16(byteBuffer);
            this.namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(int16);
            if (this.namedGroup == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unknown named group ID: " + int16);
            }
            if (!SupportedGroupsExtension.SupportedGroups.isSupported(this.namedGroup)) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported named group: " + this.namedGroup);
            }
            if (this.namedGroup.oid == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unknown named EC curve: " + this.namedGroup);
            }
            final ECParameterSpec ecParameterSpec = JsseJce.getECParameterSpec(this.namedGroup.oid);
            if (ecParameterSpec == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No supported EC parameter: " + this.namedGroup);
            }
            this.publicPoint = Record.getBytes8(byteBuffer);
            if (this.publicPoint.length == 0) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Insufficient ECPoint data: " + this.namedGroup);
            }
            ECPublicKey publicKey;
            try {
                publicKey = (ECPublicKey)JsseJce.getKeyFactory("EC").generatePublic(new ECPublicKeySpec(JsseJce.decodePoint(this.publicPoint, ecParameterSpec.getCurve()), ecParameterSpec));
            }
            catch (final NoSuchAlgorithmException | InvalidKeySpecException | IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid ECPoint: " + this.namedGroup, (Throwable)ex);
            }
            this.publicKey = publicKey;
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials sslCredentials : clientHandshakeContext.handshakeCredentials) {
                if (sslCredentials instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)sslCredentials;
                    break;
                }
            }
            if (x509Credentials != null) {
                this.useExplicitSigAlgorithm = clientHandshakeContext.negotiatedProtocol.useTLS12PlusSpec();
                if (this.useExplicitSigAlgorithm) {
                    final int int17 = Record.getInt16(byteBuffer);
                    this.signatureScheme = SignatureScheme.valueOf(int17);
                    if (this.signatureScheme == null) {
                        throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature algorithm (" + int17 + ") used in ECDH ServerKeyExchange handshake message");
                    }
                    if (!clientHandshakeContext.localSupportedSignAlgs.contains(this.signatureScheme)) {
                        throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in ECDH ServerKeyExchange handshake message");
                    }
                }
                else {
                    this.signatureScheme = null;
                }
                this.paramsSignature = Record.getBytes16(byteBuffer);
                Label_0760: {
                    if (this.useExplicitSigAlgorithm) {
                        try {
                            final Signature signature = this.signatureScheme.getVerifier(x509Credentials.popPublicKey);
                            break Label_0760;
                        }
                        catch (final NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException ex2) {
                            throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm: " + this.signatureScheme.name, (Throwable)ex2);
                        }
                    }
                    Signature signature;
                    try {
                        signature = getSignature(x509Credentials.popPublicKey.getAlgorithm(), x509Credentials.popPublicKey);
                    }
                    catch (final NoSuchAlgorithmException | InvalidKeyException ex3) {
                        throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm: " + x509Credentials.popPublicKey.getAlgorithm(), (Throwable)ex3);
                    }
                    try {
                        updateSignature(signature, clientHandshakeContext.clientHelloRandom.randomBytes, clientHandshakeContext.serverHelloRandom.randomBytes, this.namedGroup.id, this.publicPoint);
                        if (!signature.verify(this.paramsSignature)) {
                            throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid ECDH ServerKeyExchange signature");
                        }
                    }
                    catch (final SignatureException ex4) {
                        throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify ECDH ServerKeyExchange signature", ex4);
                    }
                }
                return;
            }
            if (byteBuffer.hasRemaining()) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid DH ServerKeyExchange: unknown extra data");
            }
            this.signatureScheme = null;
            this.paramsSignature = null;
            this.useExplicitSigAlgorithm = false;
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.SERVER_KEY_EXCHANGE;
        }
        
        public int messageLength() {
            int n = 0;
            if (this.paramsSignature != null) {
                n = 2 + this.paramsSignature.length;
                if (this.useExplicitSigAlgorithm) {
                    n += SignatureScheme.sizeInRecord();
                }
            }
            return 4 + this.publicPoint.length + n;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putInt8(3);
            handshakeOutStream.putInt16(this.namedGroup.id);
            handshakeOutStream.putBytes8(this.publicPoint);
            if (this.paramsSignature != null) {
                if (this.useExplicitSigAlgorithm) {
                    handshakeOutStream.putInt16(this.signatureScheme.id);
                }
                handshakeOutStream.putBytes16(this.paramsSignature);
            }
        }
        
        @Override
        public String toString() {
            if (this.useExplicitSigAlgorithm) {
                final MessageFormat messageFormat = new MessageFormat("\"ECDH ServerKeyExchange\": '{'\n  \"parameters\": '{'\n    \"named group\": \"{0}\"\n    \"ecdh public\": '{'\n{1}\n    '}',\n  '}',\n  \"digital signature\":  '{'\n    \"signature algorithm\": \"{2}\"\n    \"signature\": '{'\n{3}\n    '}',\n  '}'\n'}'", Locale.ENGLISH);
                final HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
                return messageFormat.format(new Object[] { this.namedGroup.name, Utilities.indent(hexDumpEncoder.encodeBuffer(this.publicPoint), "      "), this.signatureScheme.name, Utilities.indent(hexDumpEncoder.encodeBuffer(this.paramsSignature), "      ") });
            }
            if (this.paramsSignature != null) {
                final MessageFormat messageFormat2 = new MessageFormat("\"ECDH ServerKeyExchange\": '{'\n  \"parameters\":  '{'\n    \"named group\": \"{0}\"\n    \"ecdh public\": '{'\n{1}\n    '}',\n  '}',\n  \"signature\": '{'\n{2}\n  '}'\n'}'", Locale.ENGLISH);
                final HexDumpEncoder hexDumpEncoder2 = new HexDumpEncoder();
                return messageFormat2.format(new Object[] { this.namedGroup.name, Utilities.indent(hexDumpEncoder2.encodeBuffer(this.publicPoint), "      "), Utilities.indent(hexDumpEncoder2.encodeBuffer(this.paramsSignature), "    ") });
            }
            return new MessageFormat("\"ECDH ServerKeyExchange\": '{'\n  \"parameters\":  '{'\n    \"named group\": \"{0}\"\n    \"ecdh public\": '{'\n{1}\n    '}',\n  '}'\n'}'", Locale.ENGLISH).format(new Object[] { this.namedGroup.name, Utilities.indent(new HexDumpEncoder().encodeBuffer(this.publicPoint), "      ") });
        }
        
        private static Signature getSignature(final String s, final Key key) throws NoSuchAlgorithmException, InvalidKeyException {
            Signature signature = null;
            switch (s) {
                case "EC": {
                    signature = JsseJce.getSignature("SHA1withECDSA");
                    break;
                }
                case "RSA": {
                    signature = RSASignature.getInstance();
                    break;
                }
                default: {
                    throw new NoSuchAlgorithmException("neither an RSA or a EC key : " + s);
                }
            }
            if (signature != null) {
                if (key instanceof PublicKey) {
                    signature.initVerify((PublicKey)key);
                }
                else {
                    signature.initSign((PrivateKey)key);
                }
            }
            return signature;
        }
        
        private static void updateSignature(final Signature signature, final byte[] array, final byte[] array2, final int n, final byte[] array3) throws SignatureException {
            signature.update(array);
            signature.update(array2);
            signature.update((byte)3);
            signature.update((byte)(n >> 8 & 0xFF));
            signature.update((byte)(n & 0xFF));
            signature.update((byte)array3.length);
            signature.update(array3);
        }
    }
    
    private static final class ECDHServerKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ECDHServerKeyExchangeMessage ecdhServerKeyExchangeMessage = new ECDHServerKeyExchangeMessage(serverHandshakeContext);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ECDH ServerKeyExchange handshake message", ecdhServerKeyExchangeMessage);
            }
            ecdhServerKeyExchangeMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class ECDHServerKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final ECDHServerKeyExchangeMessage ecdhServerKeyExchangeMessage = new ECDHServerKeyExchangeMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ECDH ServerKeyExchange handshake message", ecdhServerKeyExchangeMessage);
            }
            if (!clientHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), ecdhServerKeyExchangeMessage.publicKey)) {
                throw clientHandshakeContext.conContext.fatal(Alert.INSUFFICIENT_SECURITY, "ECDH ServerKeyExchange does not comply to algorithm constraints");
            }
            clientHandshakeContext.handshakeCredentials.add(new ECDHKeyExchange.ECDHECredentials(ecdhServerKeyExchangeMessage.publicKey, ecdhServerKeyExchangeMessage.namedGroup));
        }
    }
}
