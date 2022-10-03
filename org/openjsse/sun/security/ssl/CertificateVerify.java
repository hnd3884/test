package org.openjsse.sun.security.ssl;

import java.util.Arrays;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Map;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import org.openjsse.sun.security.util.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.security.Signature;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;

final class CertificateVerify
{
    static final SSLConsumer s30HandshakeConsumer;
    static final HandshakeProducer s30HandshakeProducer;
    static final SSLConsumer t10HandshakeConsumer;
    static final HandshakeProducer t10HandshakeProducer;
    static final SSLConsumer t12HandshakeConsumer;
    static final HandshakeProducer t12HandshakeProducer;
    static final SSLConsumer t13HandshakeConsumer;
    static final HandshakeProducer t13HandshakeProducer;
    
    static {
        s30HandshakeConsumer = new S30CertificateVerifyConsumer();
        s30HandshakeProducer = new S30CertificateVerifyProducer();
        t10HandshakeConsumer = new T10CertificateVerifyConsumer();
        t10HandshakeProducer = new T10CertificateVerifyProducer();
        t12HandshakeConsumer = new T12CertificateVerifyConsumer();
        t12HandshakeProducer = new T12CertificateVerifyProducer();
        t13HandshakeConsumer = new T13CertificateVerifyConsumer();
        t13HandshakeProducer = new T13CertificateVerifyProducer();
    }
    
    static final class S30CertificateVerifyMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] signature;
        
        S30CertificateVerifyMessage(final HandshakeContext context, final X509Authentication.X509Possession x509Possession) throws IOException {
            super(context);
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            byte[] temproary = null;
            final String algorithm = x509Possession.popPrivateKey.getAlgorithm();
            try {
                final Signature signer = getSignature(algorithm, x509Possession.popPrivateKey);
                final byte[] hashes = chc.handshakeHash.digest(algorithm, chc.handshakeSession.getMasterSecret());
                signer.update(hashes);
                temproary = signer.sign();
            }
            catch (final NoSuchAlgorithmException nsae) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + algorithm + ") used in CertificateVerify handshake message", nsae);
            }
            catch (final GeneralSecurityException gse) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot produce CertificateVerify signature", gse);
            }
            this.signature = temproary;
        }
        
        S30CertificateVerifyMessage(final HandshakeContext context, final ByteBuffer m) throws IOException {
            super(context);
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (m.remaining() < 2) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateVerify message: no sufficient data");
            }
            this.signature = Record.getBytes16(m);
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials cd : shc.handshakeCredentials) {
                if (cd instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)cd;
                    break;
                }
            }
            if (x509Credentials == null || x509Credentials.popPublicKey == null) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X509 credentials negotiated for CertificateVerify");
            }
            final String algorithm = x509Credentials.popPublicKey.getAlgorithm();
            try {
                final Signature signer = getSignature(algorithm, x509Credentials.popPublicKey);
                final byte[] hashes = shc.handshakeHash.digest(algorithm, shc.handshakeSession.getMasterSecret());
                signer.update(hashes);
                if (!signer.verify(this.signature)) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid CertificateVerify message: invalid signature");
                }
            }
            catch (final NoSuchAlgorithmException nsae) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + algorithm + ") used in CertificateVerify handshake message", nsae);
            }
            catch (final GeneralSecurityException gse) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify CertificateVerify signature", gse);
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_VERIFY;
        }
        
        public int messageLength() {
            return 2 + this.signature.length;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putBytes16(this.signature);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"CertificateVerify\": '{'\n  \"signature\": '{'\n{0}\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { Utilities.indent(hexEncoder.encodeBuffer(this.signature), "    ") };
            return messageFormat.format(messageFields);
        }
        
        private static Signature getSignature(final String algorithm, final Key key) throws GeneralSecurityException {
            Signature signer = null;
            switch (algorithm) {
                case "RSA": {
                    signer = JsseJce.getSignature("NONEwithRSA");
                    break;
                }
                case "DSA": {
                    signer = JsseJce.getSignature("RawDSA");
                    break;
                }
                case "EC": {
                    signer = JsseJce.getSignature("NONEwithECDSA");
                    break;
                }
                default: {
                    throw new SignatureException("Unrecognized algorithm: " + algorithm);
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
    }
    
    private static final class S30CertificateVerifyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : chc.handshakePossessions) {
                if (possession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)possession;
                    break;
                }
            }
            if (x509Possession == null || x509Possession.popPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No X.509 credentials negotiated for CertificateVerify", new Object[0]);
                }
                return null;
            }
            final S30CertificateVerifyMessage cvm = new S30CertificateVerifyMessage(chc, x509Possession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateVerify handshake message", cvm);
            }
            cvm.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class S30CertificateVerifyConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            shc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            if (shc.handshakeConsumers.containsKey(SSLHandshake.CLIENT_KEY_EXCHANGE.id)) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected CertificateVerify handshake message");
            }
            final S30CertificateVerifyMessage cvm = new S30CertificateVerifyMessage(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateVerify handshake message", cvm);
            }
        }
    }
    
    static final class T10CertificateVerifyMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] signature;
        
        T10CertificateVerifyMessage(final HandshakeContext context, final X509Authentication.X509Possession x509Possession) throws IOException {
            super(context);
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            byte[] temproary = null;
            final String algorithm = x509Possession.popPrivateKey.getAlgorithm();
            try {
                final Signature signer = getSignature(algorithm, x509Possession.popPrivateKey);
                final byte[] hashes = chc.handshakeHash.digest(algorithm);
                signer.update(hashes);
                temproary = signer.sign();
            }
            catch (final NoSuchAlgorithmException nsae) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + algorithm + ") used in CertificateVerify handshake message", nsae);
            }
            catch (final GeneralSecurityException gse) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot produce CertificateVerify signature", gse);
            }
            this.signature = temproary;
        }
        
        T10CertificateVerifyMessage(final HandshakeContext context, final ByteBuffer m) throws IOException {
            super(context);
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (m.remaining() < 2) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateVerify message: no sufficient data");
            }
            this.signature = Record.getBytes16(m);
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials cd : shc.handshakeCredentials) {
                if (cd instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)cd;
                    break;
                }
            }
            if (x509Credentials == null || x509Credentials.popPublicKey == null) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X509 credentials negotiated for CertificateVerify");
            }
            final String algorithm = x509Credentials.popPublicKey.getAlgorithm();
            try {
                final Signature signer = getSignature(algorithm, x509Credentials.popPublicKey);
                final byte[] hashes = shc.handshakeHash.digest(algorithm);
                signer.update(hashes);
                if (!signer.verify(this.signature)) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid CertificateVerify message: invalid signature");
                }
            }
            catch (final NoSuchAlgorithmException nsae) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + algorithm + ") used in CertificateVerify handshake message", nsae);
            }
            catch (final GeneralSecurityException gse) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify CertificateVerify signature", gse);
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_VERIFY;
        }
        
        public int messageLength() {
            return 2 + this.signature.length;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putBytes16(this.signature);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"CertificateVerify\": '{'\n  \"signature\": '{'\n{0}\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { Utilities.indent(hexEncoder.encodeBuffer(this.signature), "    ") };
            return messageFormat.format(messageFields);
        }
        
        private static Signature getSignature(final String algorithm, final Key key) throws GeneralSecurityException {
            Signature signer = null;
            switch (algorithm) {
                case "RSA": {
                    signer = JsseJce.getSignature("NONEwithRSA");
                    break;
                }
                case "DSA": {
                    signer = JsseJce.getSignature("RawDSA");
                    break;
                }
                case "EC": {
                    signer = JsseJce.getSignature("NONEwithECDSA");
                    break;
                }
                default: {
                    throw new SignatureException("Unrecognized algorithm: " + algorithm);
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
    }
    
    private static final class T10CertificateVerifyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : chc.handshakePossessions) {
                if (possession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)possession;
                    break;
                }
            }
            if (x509Possession == null || x509Possession.popPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No X.509 credentials negotiated for CertificateVerify", new Object[0]);
                }
                return null;
            }
            final T10CertificateVerifyMessage cvm = new T10CertificateVerifyMessage(chc, x509Possession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateVerify handshake message", cvm);
            }
            cvm.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class T10CertificateVerifyConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            shc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            if (shc.handshakeConsumers.containsKey(SSLHandshake.CLIENT_KEY_EXCHANGE.id)) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected CertificateVerify handshake message");
            }
            final T10CertificateVerifyMessage cvm = new T10CertificateVerifyMessage(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateVerify handshake message", cvm);
            }
        }
    }
    
    static final class T12CertificateVerifyMessage extends SSLHandshake.HandshakeMessage
    {
        private final SignatureScheme signatureScheme;
        private final byte[] signature;
        
        T12CertificateVerifyMessage(final HandshakeContext context, final X509Authentication.X509Possession x509Possession) throws IOException {
            super(context);
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final Map.Entry<SignatureScheme, Signature> schemeAndSigner = SignatureScheme.getSignerOfPreferableAlgorithm(chc.peerRequestedSignatureSchemes, x509Possession, chc.negotiatedProtocol);
            if (schemeAndSigner == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "No supported CertificateVerify signature algorithm for " + x509Possession.popPrivateKey.getAlgorithm() + "  key");
            }
            this.signatureScheme = schemeAndSigner.getKey();
            byte[] temproary = null;
            try {
                final Signature signer = schemeAndSigner.getValue();
                signer.update(chc.handshakeHash.archived());
                temproary = signer.sign();
            }
            catch (final SignatureException ikse) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot produce CertificateVerify signature", ikse);
            }
            this.signature = temproary;
        }
        
        T12CertificateVerifyMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            final ServerHandshakeContext shc = (ServerHandshakeContext)handshakeContext;
            if (m.remaining() < 4) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateVerify message: no sufficient data");
            }
            final int ssid = Record.getInt16(m);
            this.signatureScheme = SignatureScheme.valueOf(ssid);
            if (this.signatureScheme == null) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature algorithm (" + ssid + ") used in CertificateVerify handshake message");
            }
            if (!shc.localSupportedSignAlgs.contains(this.signatureScheme)) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in CertificateVerify handshake message");
            }
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials cd : shc.handshakeCredentials) {
                if (cd instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)cd;
                    break;
                }
            }
            if (x509Credentials == null || x509Credentials.popPublicKey == null) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X509 credentials negotiated for CertificateVerify");
            }
            this.signature = Record.getBytes16(m);
            try {
                final Signature signer = this.signatureScheme.getVerifier(x509Credentials.popPublicKey);
                signer.update(shc.handshakeHash.archived());
                if (!signer.verify(this.signature)) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid CertificateVerify signature");
                }
            }
            catch (final NoSuchAlgorithmException | InvalidAlgorithmParameterException nsae) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in CertificateVerify handshake message", nsae);
            }
            catch (final InvalidKeyException | SignatureException ikse) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify CertificateVerify signature", ikse);
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_VERIFY;
        }
        
        public int messageLength() {
            return 4 + this.signature.length;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putInt16(this.signatureScheme.id);
            hos.putBytes16(this.signature);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"CertificateVerify\": '{'\n  \"signature algorithm\": {0}\n  \"signature\": '{'\n{1}\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { this.signatureScheme.name, Utilities.indent(hexEncoder.encodeBuffer(this.signature), "    ") };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class T12CertificateVerifyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : chc.handshakePossessions) {
                if (possession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)possession;
                    break;
                }
            }
            if (x509Possession == null || x509Possession.popPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No X.509 credentials negotiated for CertificateVerify", new Object[0]);
                }
                return null;
            }
            final T12CertificateVerifyMessage cvm = new T12CertificateVerifyMessage(chc, x509Possession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateVerify handshake message", cvm);
            }
            cvm.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class T12CertificateVerifyConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            shc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            if (shc.handshakeConsumers.containsKey(SSLHandshake.CLIENT_KEY_EXCHANGE.id)) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected CertificateVerify handshake message");
            }
            final T12CertificateVerifyMessage cvm = new T12CertificateVerifyMessage(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateVerify handshake message", cvm);
            }
        }
    }
    
    static final class T13CertificateVerifyMessage extends SSLHandshake.HandshakeMessage
    {
        private static final byte[] serverSignHead;
        private static final byte[] clientSignHead;
        private final SignatureScheme signatureScheme;
        private final byte[] signature;
        
        T13CertificateVerifyMessage(final HandshakeContext context, final X509Authentication.X509Possession x509Possession) throws IOException {
            super(context);
            final Map.Entry<SignatureScheme, Signature> schemeAndSigner = SignatureScheme.getSignerOfPreferableAlgorithm(context.peerRequestedSignatureSchemes, x509Possession, context.negotiatedProtocol);
            if (schemeAndSigner == null) {
                throw context.conContext.fatal(Alert.INTERNAL_ERROR, "No supported CertificateVerify signature algorithm for " + x509Possession.popPrivateKey.getAlgorithm() + "  key");
            }
            this.signatureScheme = schemeAndSigner.getKey();
            final byte[] hashValue = context.handshakeHash.digest();
            byte[] contentCovered;
            if (context.sslConfig.isClientMode) {
                contentCovered = Arrays.copyOf(T13CertificateVerifyMessage.clientSignHead, T13CertificateVerifyMessage.clientSignHead.length + hashValue.length);
                System.arraycopy(hashValue, 0, contentCovered, T13CertificateVerifyMessage.clientSignHead.length, hashValue.length);
            }
            else {
                contentCovered = Arrays.copyOf(T13CertificateVerifyMessage.serverSignHead, T13CertificateVerifyMessage.serverSignHead.length + hashValue.length);
                System.arraycopy(hashValue, 0, contentCovered, T13CertificateVerifyMessage.serverSignHead.length, hashValue.length);
            }
            byte[] temproary = null;
            try {
                final Signature signer = schemeAndSigner.getValue();
                signer.update(contentCovered);
                temproary = signer.sign();
            }
            catch (final SignatureException ikse) {
                throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot produce CertificateVerify signature", ikse);
            }
            this.signature = temproary;
        }
        
        T13CertificateVerifyMessage(final HandshakeContext context, final ByteBuffer m) throws IOException {
            super(context);
            if (m.remaining() < 4) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateVerify message: no sufficient data");
            }
            final int ssid = Record.getInt16(m);
            this.signatureScheme = SignatureScheme.valueOf(ssid);
            if (this.signatureScheme == null) {
                throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature algorithm (" + ssid + ") used in CertificateVerify handshake message");
            }
            if (!context.localSupportedSignAlgs.contains(this.signatureScheme)) {
                throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in CertificateVerify handshake message");
            }
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials cd : context.handshakeCredentials) {
                if (cd instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)cd;
                    break;
                }
            }
            if (x509Credentials == null || x509Credentials.popPublicKey == null) {
                throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X509 credentials negotiated for CertificateVerify");
            }
            this.signature = Record.getBytes16(m);
            final byte[] hashValue = context.handshakeHash.digest();
            byte[] contentCovered;
            if (context.sslConfig.isClientMode) {
                contentCovered = Arrays.copyOf(T13CertificateVerifyMessage.serverSignHead, T13CertificateVerifyMessage.serverSignHead.length + hashValue.length);
                System.arraycopy(hashValue, 0, contentCovered, T13CertificateVerifyMessage.serverSignHead.length, hashValue.length);
            }
            else {
                contentCovered = Arrays.copyOf(T13CertificateVerifyMessage.clientSignHead, T13CertificateVerifyMessage.clientSignHead.length + hashValue.length);
                System.arraycopy(hashValue, 0, contentCovered, T13CertificateVerifyMessage.clientSignHead.length, hashValue.length);
            }
            try {
                final Signature signer = this.signatureScheme.getVerifier(x509Credentials.popPublicKey);
                signer.update(contentCovered);
                if (!signer.verify(this.signature)) {
                    throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid CertificateVerify signature");
                }
            }
            catch (final NoSuchAlgorithmException | InvalidAlgorithmParameterException nsae) {
                throw context.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in CertificateVerify handshake message", nsae);
            }
            catch (final InvalidKeyException | SignatureException ikse) {
                throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify CertificateVerify signature", ikse);
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_VERIFY;
        }
        
        public int messageLength() {
            return 4 + this.signature.length;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putInt16(this.signatureScheme.id);
            hos.putBytes16(this.signature);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"CertificateVerify\": '{'\n  \"signature algorithm\": {0}\n  \"signature\": '{'\n{1}\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { this.signatureScheme.name, Utilities.indent(hexEncoder.encodeBuffer(this.signature), "    ") };
            return messageFormat.format(messageFields);
        }
        
        static {
            serverSignHead = new byte[] { 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 84, 76, 83, 32, 49, 46, 51, 44, 32, 115, 101, 114, 118, 101, 114, 32, 67, 101, 114, 116, 105, 102, 105, 99, 97, 116, 101, 86, 101, 114, 105, 102, 121, 0 };
            clientSignHead = new byte[] { 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 84, 76, 83, 32, 49, 46, 51, 44, 32, 99, 108, 105, 101, 110, 116, 32, 67, 101, 114, 116, 105, 102, 105, 99, 97, 116, 101, 86, 101, 114, 105, 102, 121, 0 };
        }
    }
    
    private static final class T13CertificateVerifyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : hc.handshakePossessions) {
                if (possession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)possession;
                    break;
                }
            }
            if (x509Possession == null || x509Possession.popPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No X.509 credentials negotiated for CertificateVerify", new Object[0]);
                }
                return null;
            }
            if (hc.sslConfig.isClientMode) {
                return this.onProduceCertificateVerify((ClientHandshakeContext)context, x509Possession);
            }
            return this.onProduceCertificateVerify((ServerHandshakeContext)context, x509Possession);
        }
        
        private byte[] onProduceCertificateVerify(final ServerHandshakeContext shc, final X509Authentication.X509Possession x509Possession) throws IOException {
            final T13CertificateVerifyMessage cvm = new T13CertificateVerifyMessage(shc, x509Possession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server CertificateVerify handshake message", cvm);
            }
            cvm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            return null;
        }
        
        private byte[] onProduceCertificateVerify(final ClientHandshakeContext chc, final X509Authentication.X509Possession x509Possession) throws IOException {
            final T13CertificateVerifyMessage cvm = new T13CertificateVerifyMessage(chc, x509Possession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced client CertificateVerify handshake message", cvm);
            }
            cvm.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class T13CertificateVerifyConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            hc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            final T13CertificateVerifyMessage cvm = new T13CertificateVerifyMessage(hc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateVerify handshake message", cvm);
            }
        }
    }
}
