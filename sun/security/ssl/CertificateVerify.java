package sun.security.ssl;

import java.util.Arrays;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Map;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import sun.misc.HexDumpEncoder;
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
        
        S30CertificateVerifyMessage(final HandshakeContext handshakeContext, final X509Authentication.X509Possession x509Possession) throws IOException {
            super(handshakeContext);
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)handshakeContext;
            final String algorithm = x509Possession.popPrivateKey.getAlgorithm();
            byte[] sign;
            try {
                final Signature signature = getSignature(algorithm, x509Possession.popPrivateKey);
                signature.update(clientHandshakeContext.handshakeHash.digest(algorithm, clientHandshakeContext.handshakeSession.getMasterSecret()));
                sign = signature.sign();
            }
            catch (final NoSuchAlgorithmException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + algorithm + ") used in CertificateVerify handshake message", ex);
            }
            catch (final GeneralSecurityException ex2) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot produce CertificateVerify signature", ex2);
            }
            this.signature = sign;
        }
        
        S30CertificateVerifyMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)handshakeContext;
            if (byteBuffer.remaining() < 2) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateVerify message: no sufficient data");
            }
            this.signature = Record.getBytes16(byteBuffer);
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials sslCredentials : serverHandshakeContext.handshakeCredentials) {
                if (sslCredentials instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)sslCredentials;
                    break;
                }
            }
            if (x509Credentials == null || x509Credentials.popPublicKey == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X509 credentials negotiated for CertificateVerify");
            }
            final String algorithm = x509Credentials.popPublicKey.getAlgorithm();
            try {
                final Signature signature = getSignature(algorithm, x509Credentials.popPublicKey);
                signature.update(serverHandshakeContext.handshakeHash.digest(algorithm, serverHandshakeContext.handshakeSession.getMasterSecret()));
                if (!signature.verify(this.signature)) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid CertificateVerify message: invalid signature");
                }
            }
            catch (final NoSuchAlgorithmException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + algorithm + ") used in CertificateVerify handshake message", ex);
            }
            catch (final GeneralSecurityException ex2) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify CertificateVerify signature", ex2);
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_VERIFY;
        }
        
        public int messageLength() {
            return 2 + this.signature.length;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putBytes16(this.signature);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"CertificateVerify\": '{'\n  \"signature\": '{'\n{0}\n  '}'\n'}'", Locale.ENGLISH).format(new Object[] { Utilities.indent(new HexDumpEncoder().encodeBuffer(this.signature), "    ") });
        }
        
        private static Signature getSignature(final String s, final Key key) throws GeneralSecurityException {
            Signature signature = null;
            switch (s) {
                case "RSA": {
                    signature = JsseJce.getSignature("NONEwithRSA");
                    break;
                }
                case "DSA": {
                    signature = JsseJce.getSignature("RawDSA");
                    break;
                }
                case "EC": {
                    signature = JsseJce.getSignature("NONEwithECDSA");
                    break;
                }
                default: {
                    throw new SignatureException("Unrecognized algorithm: " + s);
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
    }
    
    private static final class S30CertificateVerifyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : clientHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)sslPossession;
                    break;
                }
            }
            if (x509Possession == null || x509Possession.popPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No X.509 credentials negotiated for CertificateVerify", new Object[0]);
                }
                return null;
            }
            final S30CertificateVerifyMessage s30CertificateVerifyMessage = new S30CertificateVerifyMessage(clientHandshakeContext, x509Possession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateVerify handshake message", s30CertificateVerifyMessage);
            }
            s30CertificateVerifyMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class S30CertificateVerifyConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            serverHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            if (serverHandshakeContext.handshakeConsumers.containsKey(SSLHandshake.CLIENT_KEY_EXCHANGE.id)) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected CertificateVerify handshake message");
            }
            final S30CertificateVerifyMessage s30CertificateVerifyMessage = new S30CertificateVerifyMessage(serverHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateVerify handshake message", s30CertificateVerifyMessage);
            }
        }
    }
    
    static final class T10CertificateVerifyMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] signature;
        
        T10CertificateVerifyMessage(final HandshakeContext handshakeContext, final X509Authentication.X509Possession x509Possession) throws IOException {
            super(handshakeContext);
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)handshakeContext;
            final String algorithm = x509Possession.popPrivateKey.getAlgorithm();
            byte[] sign;
            try {
                final Signature signature = getSignature(algorithm, x509Possession.popPrivateKey);
                signature.update(clientHandshakeContext.handshakeHash.digest(algorithm));
                sign = signature.sign();
            }
            catch (final NoSuchAlgorithmException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + algorithm + ") used in CertificateVerify handshake message", ex);
            }
            catch (final GeneralSecurityException ex2) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot produce CertificateVerify signature", ex2);
            }
            this.signature = sign;
        }
        
        T10CertificateVerifyMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)handshakeContext;
            if (byteBuffer.remaining() < 2) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateVerify message: no sufficient data");
            }
            this.signature = Record.getBytes16(byteBuffer);
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials sslCredentials : serverHandshakeContext.handshakeCredentials) {
                if (sslCredentials instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)sslCredentials;
                    break;
                }
            }
            if (x509Credentials == null || x509Credentials.popPublicKey == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X509 credentials negotiated for CertificateVerify");
            }
            final String algorithm = x509Credentials.popPublicKey.getAlgorithm();
            try {
                final Signature signature = getSignature(algorithm, x509Credentials.popPublicKey);
                signature.update(serverHandshakeContext.handshakeHash.digest(algorithm));
                if (!signature.verify(this.signature)) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid CertificateVerify message: invalid signature");
                }
            }
            catch (final NoSuchAlgorithmException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + algorithm + ") used in CertificateVerify handshake message", ex);
            }
            catch (final GeneralSecurityException ex2) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify CertificateVerify signature", ex2);
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_VERIFY;
        }
        
        public int messageLength() {
            return 2 + this.signature.length;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putBytes16(this.signature);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"CertificateVerify\": '{'\n  \"signature\": '{'\n{0}\n  '}'\n'}'", Locale.ENGLISH).format(new Object[] { Utilities.indent(new HexDumpEncoder().encodeBuffer(this.signature), "    ") });
        }
        
        private static Signature getSignature(final String s, final Key key) throws GeneralSecurityException {
            Signature signature = null;
            switch (s) {
                case "RSA": {
                    signature = JsseJce.getSignature("NONEwithRSA");
                    break;
                }
                case "DSA": {
                    signature = JsseJce.getSignature("RawDSA");
                    break;
                }
                case "EC": {
                    signature = JsseJce.getSignature("NONEwithECDSA");
                    break;
                }
                default: {
                    throw new SignatureException("Unrecognized algorithm: " + s);
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
    }
    
    private static final class T10CertificateVerifyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : clientHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)sslPossession;
                    break;
                }
            }
            if (x509Possession == null || x509Possession.popPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No X.509 credentials negotiated for CertificateVerify", new Object[0]);
                }
                return null;
            }
            final T10CertificateVerifyMessage t10CertificateVerifyMessage = new T10CertificateVerifyMessage(clientHandshakeContext, x509Possession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateVerify handshake message", t10CertificateVerifyMessage);
            }
            t10CertificateVerifyMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class T10CertificateVerifyConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            serverHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            if (serverHandshakeContext.handshakeConsumers.containsKey(SSLHandshake.CLIENT_KEY_EXCHANGE.id)) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected CertificateVerify handshake message");
            }
            final T10CertificateVerifyMessage t10CertificateVerifyMessage = new T10CertificateVerifyMessage(serverHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateVerify handshake message", t10CertificateVerifyMessage);
            }
        }
    }
    
    static final class T12CertificateVerifyMessage extends SSLHandshake.HandshakeMessage
    {
        private final SignatureScheme signatureScheme;
        private final byte[] signature;
        
        T12CertificateVerifyMessage(final HandshakeContext handshakeContext, final X509Authentication.X509Possession x509Possession) throws IOException {
            super(handshakeContext);
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)handshakeContext;
            final Map.Entry<SignatureScheme, Signature> signerOfPreferableAlgorithm = SignatureScheme.getSignerOfPreferableAlgorithm(clientHandshakeContext.algorithmConstraints, clientHandshakeContext.peerRequestedSignatureSchemes, x509Possession, clientHandshakeContext.negotiatedProtocol);
            if (signerOfPreferableAlgorithm == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "No supported CertificateVerify signature algorithm for " + x509Possession.popPrivateKey.getAlgorithm() + "  key");
            }
            this.signatureScheme = signerOfPreferableAlgorithm.getKey();
            byte[] sign;
            try {
                final Signature signature = signerOfPreferableAlgorithm.getValue();
                signature.update(clientHandshakeContext.handshakeHash.archived());
                sign = signature.sign();
            }
            catch (final SignatureException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot produce CertificateVerify signature", ex);
            }
            this.signature = sign;
        }
        
        T12CertificateVerifyMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)handshakeContext;
            if (byteBuffer.remaining() < 4) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateVerify message: no sufficient data");
            }
            final int int16 = Record.getInt16(byteBuffer);
            this.signatureScheme = SignatureScheme.valueOf(int16);
            if (this.signatureScheme == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature algorithm (" + int16 + ") used in CertificateVerify handshake message");
            }
            if (!serverHandshakeContext.localSupportedSignAlgs.contains(this.signatureScheme)) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in CertificateVerify handshake message");
            }
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials sslCredentials : serverHandshakeContext.handshakeCredentials) {
                if (sslCredentials instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)sslCredentials;
                    break;
                }
            }
            if (x509Credentials == null || x509Credentials.popPublicKey == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X509 credentials negotiated for CertificateVerify");
            }
            this.signature = Record.getBytes16(byteBuffer);
            try {
                final Signature verifier = this.signatureScheme.getVerifier(x509Credentials.popPublicKey);
                verifier.update(serverHandshakeContext.handshakeHash.archived());
                if (!verifier.verify(this.signature)) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid CertificateVerify signature");
                }
            }
            catch (final NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in CertificateVerify handshake message", (Throwable)ex);
            }
            catch (final InvalidKeyException | SignatureException ex2) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify CertificateVerify signature", (Throwable)ex2);
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_VERIFY;
        }
        
        public int messageLength() {
            return 4 + this.signature.length;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putInt16(this.signatureScheme.id);
            handshakeOutStream.putBytes16(this.signature);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"CertificateVerify\": '{'\n  \"signature algorithm\": {0}\n  \"signature\": '{'\n{1}\n  '}'\n'}'", Locale.ENGLISH).format(new Object[] { this.signatureScheme.name, Utilities.indent(new HexDumpEncoder().encodeBuffer(this.signature), "    ") });
        }
    }
    
    private static final class T12CertificateVerifyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : clientHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)sslPossession;
                    break;
                }
            }
            if (x509Possession == null || x509Possession.popPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No X.509 credentials negotiated for CertificateVerify", new Object[0]);
                }
                return null;
            }
            final T12CertificateVerifyMessage t12CertificateVerifyMessage = new T12CertificateVerifyMessage(clientHandshakeContext, x509Possession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateVerify handshake message", t12CertificateVerifyMessage);
            }
            t12CertificateVerifyMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class T12CertificateVerifyConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            serverHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            if (serverHandshakeContext.handshakeConsumers.containsKey(SSLHandshake.CLIENT_KEY_EXCHANGE.id)) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected CertificateVerify handshake message");
            }
            final T12CertificateVerifyMessage t12CertificateVerifyMessage = new T12CertificateVerifyMessage(serverHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateVerify handshake message", t12CertificateVerifyMessage);
            }
        }
    }
    
    static final class T13CertificateVerifyMessage extends SSLHandshake.HandshakeMessage
    {
        private static final byte[] serverSignHead;
        private static final byte[] clientSignHead;
        private final SignatureScheme signatureScheme;
        private final byte[] signature;
        
        T13CertificateVerifyMessage(final HandshakeContext handshakeContext, final X509Authentication.X509Possession x509Possession) throws IOException {
            super(handshakeContext);
            final Map.Entry<SignatureScheme, Signature> signerOfPreferableAlgorithm = SignatureScheme.getSignerOfPreferableAlgorithm(handshakeContext.algorithmConstraints, handshakeContext.peerRequestedSignatureSchemes, x509Possession, handshakeContext.negotiatedProtocol);
            if (signerOfPreferableAlgorithm == null) {
                throw handshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "No supported CertificateVerify signature algorithm for " + x509Possession.popPrivateKey.getAlgorithm() + "  key");
            }
            this.signatureScheme = signerOfPreferableAlgorithm.getKey();
            final byte[] digest = handshakeContext.handshakeHash.digest();
            byte[] array;
            if (handshakeContext.sslConfig.isClientMode) {
                array = Arrays.copyOf(T13CertificateVerifyMessage.clientSignHead, T13CertificateVerifyMessage.clientSignHead.length + digest.length);
                System.arraycopy(digest, 0, array, T13CertificateVerifyMessage.clientSignHead.length, digest.length);
            }
            else {
                array = Arrays.copyOf(T13CertificateVerifyMessage.serverSignHead, T13CertificateVerifyMessage.serverSignHead.length + digest.length);
                System.arraycopy(digest, 0, array, T13CertificateVerifyMessage.serverSignHead.length, digest.length);
            }
            byte[] sign;
            try {
                final Signature signature = signerOfPreferableAlgorithm.getValue();
                signature.update(array);
                sign = signature.sign();
            }
            catch (final SignatureException ex) {
                throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot produce CertificateVerify signature", ex);
            }
            this.signature = sign;
        }
        
        T13CertificateVerifyMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.remaining() < 4) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateVerify message: no sufficient data");
            }
            final int int16 = Record.getInt16(byteBuffer);
            this.signatureScheme = SignatureScheme.valueOf(int16);
            if (this.signatureScheme == null) {
                throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature algorithm (" + int16 + ") used in CertificateVerify handshake message");
            }
            if (!handshakeContext.localSupportedSignAlgs.contains(this.signatureScheme)) {
                throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in CertificateVerify handshake message");
            }
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials sslCredentials : handshakeContext.handshakeCredentials) {
                if (sslCredentials instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)sslCredentials;
                    break;
                }
            }
            if (x509Credentials == null || x509Credentials.popPublicKey == null) {
                throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X509 credentials negotiated for CertificateVerify");
            }
            this.signature = Record.getBytes16(byteBuffer);
            final byte[] digest = handshakeContext.handshakeHash.digest();
            byte[] array;
            if (handshakeContext.sslConfig.isClientMode) {
                array = Arrays.copyOf(T13CertificateVerifyMessage.serverSignHead, T13CertificateVerifyMessage.serverSignHead.length + digest.length);
                System.arraycopy(digest, 0, array, T13CertificateVerifyMessage.serverSignHead.length, digest.length);
            }
            else {
                array = Arrays.copyOf(T13CertificateVerifyMessage.clientSignHead, T13CertificateVerifyMessage.clientSignHead.length + digest.length);
                System.arraycopy(digest, 0, array, T13CertificateVerifyMessage.clientSignHead.length, digest.length);
            }
            try {
                final Signature verifier = this.signatureScheme.getVerifier(x509Credentials.popPublicKey);
                verifier.update(array);
                if (!verifier.verify(this.signature)) {
                    throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid CertificateVerify signature");
                }
            }
            catch (final NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
                throw handshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Unsupported signature algorithm (" + this.signatureScheme.name + ") used in CertificateVerify handshake message", (Throwable)ex);
            }
            catch (final InvalidKeyException | SignatureException ex2) {
                throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Cannot verify CertificateVerify signature", (Throwable)ex2);
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_VERIFY;
        }
        
        public int messageLength() {
            return 4 + this.signature.length;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putInt16(this.signatureScheme.id);
            handshakeOutStream.putBytes16(this.signature);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"CertificateVerify\": '{'\n  \"signature algorithm\": {0}\n  \"signature\": '{'\n{1}\n  '}'\n'}'", Locale.ENGLISH).format(new Object[] { this.signatureScheme.name, Utilities.indent(new HexDumpEncoder().encodeBuffer(this.signature), "    ") });
        }
        
        static {
            serverSignHead = new byte[] { 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 84, 76, 83, 32, 49, 46, 51, 44, 32, 115, 101, 114, 118, 101, 114, 32, 67, 101, 114, 116, 105, 102, 105, 99, 97, 116, 101, 86, 101, 114, 105, 102, 121, 0 };
            clientSignHead = new byte[] { 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 84, 76, 83, 32, 49, 46, 51, 44, 32, 99, 108, 105, 101, 110, 116, 32, 67, 101, 114, 116, 105, 102, 105, 99, 97, 116, 101, 86, 101, 114, 105, 102, 121, 0 };
        }
    }
    
    private static final class T13CertificateVerifyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final HandshakeContext handshakeContext = (HandshakeContext)connectionContext;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : handshakeContext.handshakePossessions) {
                if (sslPossession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)sslPossession;
                    break;
                }
            }
            if (x509Possession == null || x509Possession.popPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No X.509 credentials negotiated for CertificateVerify", new Object[0]);
                }
                return null;
            }
            if (handshakeContext.sslConfig.isClientMode) {
                return this.onProduceCertificateVerify((ClientHandshakeContext)connectionContext, x509Possession);
            }
            return this.onProduceCertificateVerify((ServerHandshakeContext)connectionContext, x509Possession);
        }
        
        private byte[] onProduceCertificateVerify(final ServerHandshakeContext serverHandshakeContext, final X509Authentication.X509Possession x509Possession) throws IOException {
            final T13CertificateVerifyMessage t13CertificateVerifyMessage = new T13CertificateVerifyMessage(serverHandshakeContext, x509Possession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server CertificateVerify handshake message", t13CertificateVerifyMessage);
            }
            t13CertificateVerifyMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            return null;
        }
        
        private byte[] onProduceCertificateVerify(final ClientHandshakeContext clientHandshakeContext, final X509Authentication.X509Possession x509Possession) throws IOException {
            final T13CertificateVerifyMessage t13CertificateVerifyMessage = new T13CertificateVerifyMessage(clientHandshakeContext, x509Possession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced client CertificateVerify handshake message", t13CertificateVerifyMessage);
            }
            t13CertificateVerifyMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class T13CertificateVerifyConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final HandshakeContext handshakeContext = (HandshakeContext)connectionContext;
            handshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            final T13CertificateVerifyMessage t13CertificateVerifyMessage = new T13CertificateVerifyMessage(handshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateVerify handshake message", t13CertificateVerifyMessage);
            }
        }
    }
}
