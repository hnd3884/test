package org.openjsse.sun.security.ssl;

import java.security.KeyFactory;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.openjsse.sun.security.util.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.KeySpec;
import sun.security.util.KeyUtil;
import javax.crypto.spec.DHPublicKeySpec;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.security.GeneralSecurityException;
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
            final ServerHandshakeContext shc = (ServerHandshakeContext)handshakeContext;
            DHKeyExchange.DHEPossession dhePossession = null;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : shc.handshakePossessions) {
                if (possession instanceof DHKeyExchange.DHEPossession) {
                    dhePossession = (DHKeyExchange.DHEPossession)possession;
                    if (x509Possession != null) {
                        break;
                    }
                    continue;
                }
                else {
                    if (!(possession instanceof X509Authentication.X509Possession)) {
                        continue;
                    }
                    x509Possession = (X509Authentication.X509Possession)possession;
                    if (dhePossession != null) {
                        break;
                    }
                    continue;
                }
            }
            if (dhePossession == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No DHE credentials negotiated for server key exchange");
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
                this.useExplicitSigAlgorithm = shc.negotiatedProtocol.useTLS12PlusSpec();
                Signature signer = null;
                if (this.useExplicitSigAlgorithm) {
                    final Map.Entry<SignatureScheme, Signature> schemeAndSigner = SignatureScheme.getSignerOfPreferableAlgorithm(shc.peerRequestedSignatureSchemes, x509Possession, shc.negotiatedProtocol);
                    if (schemeAndSigner == null) {
                        throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "No supported signature algorithm for " + x509Possession.popPrivateKey.getAlgorithm() + "  key");
                    }
                    this.signatureScheme = schemeAndSigner.getKey();
                    signer = schemeAndSigner.getValue();
                }
                else {
                    this.signatureScheme = null;
                    try {
                        signer = getSignature(x509Possession.popPrivateKey.getAlgorithm(), x509Possession.popPrivateKey);
                    }
                    catch (final NoSuchAlgorithmException | InvalidKeyException e) {
                        throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm: " + x509Possession.popPrivateKey.getAlgorithm(), e);
                    }
                }
                byte[] signature = null;
                try {
                    this.updateSignature(signer, shc.clientHelloRandom.randomBytes, shc.serverHelloRandom.randomBytes);
                    signature = signer.sign();
                }
                catch (final SignatureException ex) {
                    throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Failed to sign dhe parameters: " + x509Possession.popPrivateKey.getAlgorithm(), ex);
                }
                this.paramsSignature = signature;
            }
        }
        
        DHServerKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            final ClientHandshakeContext chc = (ClientHandshakeContext)handshakeContext;
            this.p = Record.getBytes16(m);
            this.g = Record.getBytes16(m);
            this.y = Record.getBytes16(m);
            try {
                KeyUtil.validate(new DHPublicKeySpec(new BigInteger(1, this.y), new BigInteger(1, this.p), new BigInteger(1, this.p)));
            }
            catch (final InvalidKeyException ike) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid DH ServerKeyExchange: invalid parameters", ike);
            }
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials cd : chc.handshakeCredentials) {
                if (cd instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)cd;
                    break;
                }
            }
            if (x509Credentials != null) {
                this.useExplicitSigAlgorithm = chc.negotiatedProtocol.useTLS12PlusSpec();
                if (this.useExplicitSigAlgorithm) {
                    final int ssid = Record.getInt16(m);
                    this.signatureScheme = SignatureScheme.valueOf(ssid);
                    if (this.signatureScheme == null) {
                        throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature algorithm (" + ssid + ") used in DH ServerKeyExchange handshake message");
                    }
                    if (!chc.localSupportedSignAlgs.contains(this.signatureScheme)) {
                        throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in DH ServerKeyExchange handshake message");
                    }
                }
                else {
                    this.signatureScheme = null;
                }
                this.paramsSignature = Record.getBytes16(m);
                Label_0477: {
                    if (this.useExplicitSigAlgorithm) {
                        try {
                            final Signature signer = this.signatureScheme.getVerifier(x509Credentials.popPublicKey);
                            break Label_0477;
                        }
                        catch (final NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException nsae) {
                            throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm: " + this.signatureScheme.name, nsae);
                        }
                    }
                    Signature signer;
                    try {
                        signer = getSignature(x509Credentials.popPublicKey.getAlgorithm(), x509Credentials.popPublicKey);
                    }
                    catch (final NoSuchAlgorithmException | InvalidKeyException e) {
                        throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm: " + x509Credentials.popPublicKey.getAlgorithm(), e);
                    }
                    try {
                        this.updateSignature(signer, chc.clientHelloRandom.randomBytes, chc.serverHelloRandom.randomBytes);
                        if (!signer.verify(this.paramsSignature)) {
                            throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature on DH ServerKeyExchange message");
                        }
                    }
                    catch (final SignatureException ex) {
                        throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify DH ServerKeyExchange signature", ex);
                    }
                }
                return;
            }
            if (m.hasRemaining()) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid DH ServerKeyExchange: unknown extra data");
            }
            this.signatureScheme = null;
            this.paramsSignature = null;
            this.useExplicitSigAlgorithm = false;
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.SERVER_KEY_EXCHANGE;
        }
        
        public int messageLength() {
            int sigLen = 0;
            if (this.paramsSignature != null) {
                sigLen = 2 + this.paramsSignature.length;
                if (this.useExplicitSigAlgorithm) {
                    sigLen += SignatureScheme.sizeInRecord();
                }
            }
            return 6 + this.p.length + this.g.length + this.y.length + sigLen;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putBytes16(this.p);
            hos.putBytes16(this.g);
            hos.putBytes16(this.y);
            if (this.paramsSignature != null) {
                if (this.useExplicitSigAlgorithm) {
                    hos.putInt16(this.signatureScheme.id);
                }
                hos.putBytes16(this.paramsSignature);
            }
        }
        
        @Override
        public String toString() {
            if (this.paramsSignature == null) {
                final MessageFormat messageFormat = new MessageFormat("\"DH ServerKeyExchange\": '{'\n  \"parameters\": '{'\n    \"dh_p\": '{'\n{0}\n    '}',\n    \"dh_g\": '{'\n{1}\n    '}',\n    \"dh_Ys\": '{'\n{2}\n    '}',\n  '}'\n'}'", Locale.ENGLISH);
                final HexDumpEncoder hexEncoder = new HexDumpEncoder();
                final Object[] messageFields = { Utilities.indent(hexEncoder.encodeBuffer(this.p), "      "), Utilities.indent(hexEncoder.encodeBuffer(this.g), "      "), Utilities.indent(hexEncoder.encodeBuffer(this.y), "      ") };
                return messageFormat.format(messageFields);
            }
            if (this.useExplicitSigAlgorithm) {
                final MessageFormat messageFormat = new MessageFormat("\"DH ServerKeyExchange\": '{'\n  \"parameters\": '{'\n    \"dh_p\": '{'\n{0}\n    '}',\n    \"dh_g\": '{'\n{1}\n    '}',\n    \"dh_Ys\": '{'\n{2}\n    '}',\n  '}',\n  \"digital signature\":  '{'\n    \"signature algorithm\": \"{3}\"\n    \"signature\": '{'\n{4}\n    '}',\n  '}'\n'}'", Locale.ENGLISH);
                final HexDumpEncoder hexEncoder = new HexDumpEncoder();
                final Object[] messageFields = { Utilities.indent(hexEncoder.encodeBuffer(this.p), "      "), Utilities.indent(hexEncoder.encodeBuffer(this.g), "      "), Utilities.indent(hexEncoder.encodeBuffer(this.y), "      "), this.signatureScheme.name, Utilities.indent(hexEncoder.encodeBuffer(this.paramsSignature), "      ") };
                return messageFormat.format(messageFields);
            }
            final MessageFormat messageFormat = new MessageFormat("\"DH ServerKeyExchange\": '{'\n  \"parameters\": '{'\n    \"dh_p\": '{'\n{0}\n    '}',\n    \"dh_g\": '{'\n{1}\n    '}',\n    \"dh_Ys\": '{'\n{2}\n    '}',\n  '}',\n  \"signature\": '{'\n{3}\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { Utilities.indent(hexEncoder.encodeBuffer(this.p), "      "), Utilities.indent(hexEncoder.encodeBuffer(this.g), "      "), Utilities.indent(hexEncoder.encodeBuffer(this.y), "      "), Utilities.indent(hexEncoder.encodeBuffer(this.paramsSignature), "    ") };
            return messageFormat.format(messageFields);
        }
        
        private static Signature getSignature(final String keyAlgorithm, final Key key) throws NoSuchAlgorithmException, InvalidKeyException {
            Signature signer = null;
            switch (keyAlgorithm) {
                case "DSA": {
                    signer = JsseJce.getSignature("DSA");
                    break;
                }
                case "RSA": {
                    signer = RSASignature.getInstance();
                    break;
                }
                default: {
                    throw new NoSuchAlgorithmException("neither an RSA or a DSA key : " + keyAlgorithm);
                }
            }
            if (signer != null) {
                if (key instanceof PublicKey) {
                    signer.initVerify((PublicKey)key);
                }
                else {
                    signer.initSign((PrivateKey)key);
                }
            }
            return signer;
        }
        
        private void updateSignature(final Signature sig, final byte[] clntNonce, final byte[] svrNonce) throws SignatureException {
            sig.update(clntNonce);
            sig.update(svrNonce);
            sig.update((byte)(this.p.length >> 8));
            sig.update((byte)(this.p.length & 0xFF));
            sig.update(this.p);
            sig.update((byte)(this.g.length >> 8));
            sig.update((byte)(this.g.length & 0xFF));
            sig.update(this.g);
            sig.update((byte)(this.y.length >> 8));
            sig.update((byte)(this.y.length & 0xFF));
            sig.update(this.y);
        }
    }
    
    static final class DHServerKeyExchangeProducer implements HandshakeProducer
    {
        private DHServerKeyExchangeProducer() {
        }
        
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final DHServerKeyExchangeMessage skem = new DHServerKeyExchangeMessage(shc);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced DH ServerKeyExchange handshake message", skem);
            }
            skem.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            return null;
        }
    }
    
    static final class DHServerKeyExchangeConsumer implements SSLConsumer
    {
        private DHServerKeyExchangeConsumer() {
        }
        
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final DHServerKeyExchangeMessage skem = new DHServerKeyExchangeMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming DH ServerKeyExchange handshake message", skem);
            }
            DHPublicKey publicKey;
            try {
                final KeyFactory kf = JsseJce.getKeyFactory("DiffieHellman");
                final DHPublicKeySpec spec = new DHPublicKeySpec(new BigInteger(1, skem.y), new BigInteger(1, skem.p), new BigInteger(1, skem.g));
                publicKey = (DHPublicKey)kf.generatePublic(spec);
            }
            catch (final GeneralSecurityException gse) {
                throw chc.conContext.fatal(Alert.INSUFFICIENT_SECURITY, "Could not generate DHPublicKey", gse);
            }
            if (!chc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), publicKey)) {
                throw chc.conContext.fatal(Alert.INSUFFICIENT_SECURITY, "DH ServerKeyExchange does not comply to algorithm constraints");
            }
            final SupportedGroupsExtension.NamedGroup namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(publicKey.getParams());
            chc.handshakeCredentials.add(new DHKeyExchange.DHECredentials(publicKey, namedGroup));
        }
    }
}
