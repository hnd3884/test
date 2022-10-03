package sun.security.ssl;

import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import sun.misc.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.KeySpec;
import sun.security.util.KeyUtil;
import javax.crypto.spec.DHPublicKeySpec;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Map;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.interfaces.DHPublicKey;
import java.util.Iterator;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.security.Signature;

final class DHServerKeyExchange
{
    static final SSLConsumer dhHandshakeConsumer;
    static final HandshakeProducer dhHandshakeProducer;
    
    static {
        dhHandshakeConsumer = new DHServerKeyExchangeConsumer();
        dhHandshakeProducer = new DHServerKeyExchangeProducer();
    }
    
    private static final class DHServerKeyExchangeMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] p;
        private final byte[] g;
        private final byte[] y;
        private final boolean useExplicitSigAlgorithm;
        private final SignatureScheme signatureScheme;
        private final byte[] paramsSignature;
        
        DHServerKeyExchangeMessage(final HandshakeContext handshakeContext) throws IOException {
            super(handshakeContext);
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)handshakeContext;
            DHKeyExchange.DHEPossession dhePossession = null;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof DHKeyExchange.DHEPossession) {
                    dhePossession = (DHKeyExchange.DHEPossession)sslPossession;
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
                    if (dhePossession != null) {
                        break;
                    }
                    continue;
                }
            }
            if (dhePossession == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No DHE credentials negotiated for server key exchange");
            }
            final DHPublicKey publicKey = dhePossession.publicKey;
            final DHParameterSpec params = publicKey.getParams();
            this.p = Utilities.toByteArray(params.getP());
            this.g = Utilities.toByteArray(params.getG());
            this.y = Utilities.toByteArray(publicKey.getY());
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
                    this.updateSignature(signature, serverHandshakeContext.clientHelloRandom.randomBytes, serverHandshakeContext.serverHelloRandom.randomBytes);
                    sign = signature.sign();
                }
                catch (final SignatureException ex2) {
                    throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Failed to sign dhe parameters: " + x509Possession.popPrivateKey.getAlgorithm(), ex2);
                }
                this.paramsSignature = sign;
            }
        }
        
        DHServerKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)handshakeContext;
            this.p = Record.getBytes16(byteBuffer);
            this.g = Record.getBytes16(byteBuffer);
            this.y = Record.getBytes16(byteBuffer);
            try {
                KeyUtil.validate(new DHPublicKeySpec(new BigInteger(1, this.y), new BigInteger(1, this.p), new BigInteger(1, this.p)));
            }
            catch (final InvalidKeyException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid DH ServerKeyExchange: invalid parameters", ex);
            }
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
                    final int int16 = Record.getInt16(byteBuffer);
                    this.signatureScheme = SignatureScheme.valueOf(int16);
                    if (this.signatureScheme == null) {
                        throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature algorithm (" + int16 + ") used in DH ServerKeyExchange handshake message");
                    }
                    if (!clientHandshakeContext.localSupportedSignAlgs.contains(this.signatureScheme)) {
                        throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in DH ServerKeyExchange handshake message");
                    }
                }
                else {
                    this.signatureScheme = null;
                }
                this.paramsSignature = Record.getBytes16(byteBuffer);
                Label_0477: {
                    if (this.useExplicitSigAlgorithm) {
                        try {
                            final Signature signature = this.signatureScheme.getVerifier(x509Credentials.popPublicKey);
                            break Label_0477;
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
                        this.updateSignature(signature, clientHandshakeContext.clientHelloRandom.randomBytes, clientHandshakeContext.serverHelloRandom.randomBytes);
                        if (!signature.verify(this.paramsSignature)) {
                            throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature on DH ServerKeyExchange message");
                        }
                    }
                    catch (final SignatureException ex4) {
                        throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify DH ServerKeyExchange signature", ex4);
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
            return 6 + this.p.length + this.g.length + this.y.length + n;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putBytes16(this.p);
            handshakeOutStream.putBytes16(this.g);
            handshakeOutStream.putBytes16(this.y);
            if (this.paramsSignature != null) {
                if (this.useExplicitSigAlgorithm) {
                    handshakeOutStream.putInt16(this.signatureScheme.id);
                }
                handshakeOutStream.putBytes16(this.paramsSignature);
            }
        }
        
        @Override
        public String toString() {
            if (this.paramsSignature == null) {
                final MessageFormat messageFormat = new MessageFormat("\"DH ServerKeyExchange\": '{'\n  \"parameters\": '{'\n    \"dh_p\": '{'\n{0}\n    '}',\n    \"dh_g\": '{'\n{1}\n    '}',\n    \"dh_Ys\": '{'\n{2}\n    '}',\n  '}'\n'}'", Locale.ENGLISH);
                final HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
                return messageFormat.format(new Object[] { Utilities.indent(hexDumpEncoder.encodeBuffer(this.p), "      "), Utilities.indent(hexDumpEncoder.encodeBuffer(this.g), "      "), Utilities.indent(hexDumpEncoder.encodeBuffer(this.y), "      ") });
            }
            if (this.useExplicitSigAlgorithm) {
                final MessageFormat messageFormat2 = new MessageFormat("\"DH ServerKeyExchange\": '{'\n  \"parameters\": '{'\n    \"dh_p\": '{'\n{0}\n    '}',\n    \"dh_g\": '{'\n{1}\n    '}',\n    \"dh_Ys\": '{'\n{2}\n    '}',\n  '}',\n  \"digital signature\":  '{'\n    \"signature algorithm\": \"{3}\"\n    \"signature\": '{'\n{4}\n    '}',\n  '}'\n'}'", Locale.ENGLISH);
                final HexDumpEncoder hexDumpEncoder2 = new HexDumpEncoder();
                return messageFormat2.format(new Object[] { Utilities.indent(hexDumpEncoder2.encodeBuffer(this.p), "      "), Utilities.indent(hexDumpEncoder2.encodeBuffer(this.g), "      "), Utilities.indent(hexDumpEncoder2.encodeBuffer(this.y), "      "), this.signatureScheme.name, Utilities.indent(hexDumpEncoder2.encodeBuffer(this.paramsSignature), "      ") });
            }
            final MessageFormat messageFormat3 = new MessageFormat("\"DH ServerKeyExchange\": '{'\n  \"parameters\": '{'\n    \"dh_p\": '{'\n{0}\n    '}',\n    \"dh_g\": '{'\n{1}\n    '}',\n    \"dh_Ys\": '{'\n{2}\n    '}',\n  '}',\n  \"signature\": '{'\n{3}\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexDumpEncoder3 = new HexDumpEncoder();
            return messageFormat3.format(new Object[] { Utilities.indent(hexDumpEncoder3.encodeBuffer(this.p), "      "), Utilities.indent(hexDumpEncoder3.encodeBuffer(this.g), "      "), Utilities.indent(hexDumpEncoder3.encodeBuffer(this.y), "      "), Utilities.indent(hexDumpEncoder3.encodeBuffer(this.paramsSignature), "    ") });
        }
        
        private static Signature getSignature(final String s, final Key key) throws NoSuchAlgorithmException, InvalidKeyException {
            Signature signature = null;
            switch (s) {
                case "DSA": {
                    signature = JsseJce.getSignature("DSA");
                    break;
                }
                case "RSA": {
                    signature = RSASignature.getInstance();
                    break;
                }
                default: {
                    throw new NoSuchAlgorithmException("neither an RSA or a DSA key : " + s);
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
        
        private void updateSignature(final Signature signature, final byte[] array, final byte[] array2) throws SignatureException {
            signature.update(array);
            signature.update(array2);
            signature.update((byte)(this.p.length >> 8));
            signature.update((byte)(this.p.length & 0xFF));
            signature.update(this.p);
            signature.update((byte)(this.g.length >> 8));
            signature.update((byte)(this.g.length & 0xFF));
            signature.update(this.g);
            signature.update((byte)(this.y.length >> 8));
            signature.update((byte)(this.y.length & 0xFF));
            signature.update(this.y);
        }
    }
    
    static final class DHServerKeyExchangeProducer implements HandshakeProducer
    {
        private DHServerKeyExchangeProducer() {
        }
        
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final DHServerKeyExchangeMessage dhServerKeyExchangeMessage = new DHServerKeyExchangeMessage(serverHandshakeContext);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced DH ServerKeyExchange handshake message", dhServerKeyExchangeMessage);
            }
            dhServerKeyExchangeMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    static final class DHServerKeyExchangeConsumer implements SSLConsumer
    {
        private DHServerKeyExchangeConsumer() {
        }
        
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final DHServerKeyExchangeMessage dhServerKeyExchangeMessage = new DHServerKeyExchangeMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming DH ServerKeyExchange handshake message", dhServerKeyExchangeMessage);
            }
            DHPublicKey dhPublicKey;
            try {
                dhPublicKey = (DHPublicKey)JsseJce.getKeyFactory("DiffieHellman").generatePublic(new DHPublicKeySpec(new BigInteger(1, dhServerKeyExchangeMessage.y), new BigInteger(1, dhServerKeyExchangeMessage.p), new BigInteger(1, dhServerKeyExchangeMessage.g)));
            }
            catch (final GeneralSecurityException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.INSUFFICIENT_SECURITY, "Could not generate DHPublicKey", ex);
            }
            if (!clientHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), dhPublicKey)) {
                throw clientHandshakeContext.conContext.fatal(Alert.INSUFFICIENT_SECURITY, "DH ServerKeyExchange does not comply to algorithm constraints");
            }
            clientHandshakeContext.handshakeCredentials.add(new DHKeyExchange.DHECredentials(dhPublicKey, SupportedGroupsExtension.NamedGroup.valueOf(dhPublicKey.getParams())));
        }
    }
}
