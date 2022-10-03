package org.openjsse.sun.security.ssl;

import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.openjsse.sun.security.util.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.security.KeyFactory;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.ECPublicKeySpec;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.security.spec.ECPoint;
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
            final ServerHandshakeContext shc = (ServerHandshakeContext)handshakeContext;
            ECDHKeyExchange.ECDHEPossession ecdhePossession = null;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : shc.handshakePossessions) {
                if (possession instanceof ECDHKeyExchange.ECDHEPossession) {
                    ecdhePossession = (ECDHKeyExchange.ECDHEPossession)possession;
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
                    if (ecdhePossession != null) {
                        break;
                    }
                    continue;
                }
            }
            if (ecdhePossession == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No ECDHE credentials negotiated for server key exchange");
            }
            this.publicKey = ecdhePossession.publicKey;
            final ECParameterSpec params = this.publicKey.getParams();
            final ECPoint point = this.publicKey.getW();
            this.publicPoint = JsseJce.encodePoint(point, params.getCurve());
            this.namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(params);
            if (this.namedGroup == null || this.namedGroup.oid == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unnamed EC parameter spec: " + params);
            }
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
                    updateSignature(signer, shc.clientHelloRandom.randomBytes, shc.serverHelloRandom.randomBytes, this.namedGroup.id, this.publicPoint);
                    signature = signer.sign();
                }
                catch (final SignatureException ex) {
                    throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Failed to sign ecdhe parameters: " + x509Possession.popPrivateKey.getAlgorithm(), ex);
                }
                this.paramsSignature = signature;
            }
        }
        
        ECDHServerKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            final ClientHandshakeContext chc = (ClientHandshakeContext)handshakeContext;
            final byte curveType = (byte)Record.getInt8(m);
            if (curveType != 3) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported ECCurveType: " + curveType);
            }
            final int namedGroupId = Record.getInt16(m);
            this.namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(namedGroupId);
            if (this.namedGroup == null) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unknown named group ID: " + namedGroupId);
            }
            if (!SupportedGroupsExtension.SupportedGroups.isSupported(this.namedGroup)) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported named group: " + this.namedGroup);
            }
            if (this.namedGroup.oid == null) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unknown named EC curve: " + this.namedGroup);
            }
            final ECParameterSpec parameters = JsseJce.getECParameterSpec(this.namedGroup.oid);
            if (parameters == null) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No supported EC parameter: " + this.namedGroup);
            }
            this.publicPoint = Record.getBytes8(m);
            if (this.publicPoint.length == 0) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Insufficient ECPoint data: " + this.namedGroup);
            }
            ECPublicKey ecPublicKey = null;
            try {
                final ECPoint point = JsseJce.decodePoint(this.publicPoint, parameters.getCurve());
                final KeyFactory factory = JsseJce.getKeyFactory("EC");
                ecPublicKey = (ECPublicKey)factory.generatePublic(new ECPublicKeySpec(point, parameters));
            }
            catch (final NoSuchAlgorithmException | InvalidKeySpecException | IOException ex) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid ECPoint: " + this.namedGroup, ex);
            }
            this.publicKey = ecPublicKey;
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
                        throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature algorithm (" + ssid + ") used in ECDH ServerKeyExchange handshake message");
                    }
                    if (!chc.localSupportedSignAlgs.contains(this.signatureScheme)) {
                        throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in ECDH ServerKeyExchange handshake message");
                    }
                }
                else {
                    this.signatureScheme = null;
                }
                this.paramsSignature = Record.getBytes16(m);
                Label_0760: {
                    if (this.useExplicitSigAlgorithm) {
                        try {
                            final Signature signer = this.signatureScheme.getVerifier(x509Credentials.popPublicKey);
                            break Label_0760;
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
                        updateSignature(signer, chc.clientHelloRandom.randomBytes, chc.serverHelloRandom.randomBytes, this.namedGroup.id, this.publicPoint);
                        if (!signer.verify(this.paramsSignature)) {
                            throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid ECDH ServerKeyExchange signature");
                        }
                    }
                    catch (final SignatureException ex2) {
                        throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify ECDH ServerKeyExchange signature", ex2);
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
            return 4 + this.publicPoint.length + sigLen;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putInt8(3);
            hos.putInt16(this.namedGroup.id);
            hos.putBytes8(this.publicPoint);
            if (this.paramsSignature != null) {
                if (this.useExplicitSigAlgorithm) {
                    hos.putInt16(this.signatureScheme.id);
                }
                hos.putBytes16(this.paramsSignature);
            }
        }
        
        @Override
        public String toString() {
            if (this.useExplicitSigAlgorithm) {
                final MessageFormat messageFormat = new MessageFormat("\"ECDH ServerKeyExchange\": '{'\n  \"parameters\": '{'\n    \"named group\": \"{0}\"\n    \"ecdh public\": '{'\n{1}\n    '}',\n  '}',\n  \"digital signature\":  '{'\n    \"signature algorithm\": \"{2}\"\n    \"signature\": '{'\n{3}\n    '}',\n  '}'\n'}'", Locale.ENGLISH);
                final HexDumpEncoder hexEncoder = new HexDumpEncoder();
                final Object[] messageFields = { this.namedGroup.name, Utilities.indent(hexEncoder.encodeBuffer(this.publicPoint), "      "), this.signatureScheme.name, Utilities.indent(hexEncoder.encodeBuffer(this.paramsSignature), "      ") };
                return messageFormat.format(messageFields);
            }
            if (this.paramsSignature != null) {
                final MessageFormat messageFormat = new MessageFormat("\"ECDH ServerKeyExchange\": '{'\n  \"parameters\":  '{'\n    \"named group\": \"{0}\"\n    \"ecdh public\": '{'\n{1}\n    '}',\n  '}',\n  \"signature\": '{'\n{2}\n  '}'\n'}'", Locale.ENGLISH);
                final HexDumpEncoder hexEncoder = new HexDumpEncoder();
                final Object[] messageFields = { this.namedGroup.name, Utilities.indent(hexEncoder.encodeBuffer(this.publicPoint), "      "), Utilities.indent(hexEncoder.encodeBuffer(this.paramsSignature), "    ") };
                return messageFormat.format(messageFields);
            }
            final MessageFormat messageFormat = new MessageFormat("\"ECDH ServerKeyExchange\": '{'\n  \"parameters\":  '{'\n    \"named group\": \"{0}\"\n    \"ecdh public\": '{'\n{1}\n    '}',\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { this.namedGroup.name, Utilities.indent(hexEncoder.encodeBuffer(this.publicPoint), "      ") };
            return messageFormat.format(messageFields);
        }
        
        private static Signature getSignature(final String keyAlgorithm, final Key key) throws NoSuchAlgorithmException, InvalidKeyException {
            Signature signer = null;
            switch (keyAlgorithm) {
                case "EC": {
                    signer = JsseJce.getSignature("SHA1withECDSA");
                    break;
                }
                case "RSA": {
                    signer = RSASignature.getInstance();
                    break;
                }
                default: {
                    throw new NoSuchAlgorithmException("neither an RSA or a EC key : " + keyAlgorithm);
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
        
        private static void updateSignature(final Signature sig, final byte[] clntNonce, final byte[] svrNonce, final int namedGroupId, final byte[] publicPoint) throws SignatureException {
            sig.update(clntNonce);
            sig.update(svrNonce);
            sig.update((byte)3);
            sig.update((byte)(namedGroupId >> 8 & 0xFF));
            sig.update((byte)(namedGroupId & 0xFF));
            sig.update((byte)publicPoint.length);
            sig.update(publicPoint);
        }
    }
    
    private static final class ECDHServerKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ECDHServerKeyExchangeMessage skem = new ECDHServerKeyExchangeMessage(shc);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ECDH ServerKeyExchange handshake message", skem);
            }
            skem.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class ECDHServerKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final ECDHServerKeyExchangeMessage skem = new ECDHServerKeyExchangeMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ECDH ServerKeyExchange handshake message", skem);
            }
            if (!chc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), skem.publicKey)) {
                throw chc.conContext.fatal(Alert.INSUFFICIENT_SECURITY, "ECDH ServerKeyExchange does not comply to algorithm constraints");
            }
            chc.handshakeCredentials.add(new ECDHKeyExchange.ECDHECredentials(skem.publicKey, skem.namedGroup));
        }
    }
}
