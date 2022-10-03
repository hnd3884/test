package sun.security.ssl;

import java.util.LinkedList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Collection;
import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

final class SignatureAlgorithmsExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeAbsence chOnLoadAbsence;
    static final HandshakeConsumer chOnTradeConsumer;
    static final HandshakeAbsence chOnTradeAbsence;
    static final HandshakeProducer crNetworkProducer;
    static final SSLExtension.ExtensionConsumer crOnLoadConsumer;
    static final HandshakeAbsence crOnLoadAbsence;
    static final HandshakeConsumer crOnTradeConsumer;
    static final SSLStringizer ssStringizer;
    
    static {
        chNetworkProducer = new CHSignatureSchemesProducer();
        chOnLoadConsumer = new CHSignatureSchemesConsumer();
        chOnLoadAbsence = new CHSignatureSchemesOnLoadAbsence();
        chOnTradeConsumer = new CHSignatureSchemesUpdate();
        chOnTradeAbsence = new CHSignatureSchemesOnTradeAbsence();
        crNetworkProducer = new CRSignatureSchemesProducer();
        crOnLoadConsumer = new CRSignatureSchemesConsumer();
        crOnLoadAbsence = new CRSignatureSchemesAbsence();
        crOnTradeConsumer = new CRSignatureSchemesUpdate();
        ssStringizer = new SignatureSchemesStringizer();
    }
    
    static final class SignatureSchemesSpec implements SSLExtension.SSLExtensionSpec
    {
        final int[] signatureSchemes;
        
        SignatureSchemesSpec(final List<SignatureScheme> list) {
            if (list != null) {
                this.signatureSchemes = new int[list.size()];
                int n = 0;
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    this.signatureSchemes[n++] = ((SignatureScheme)iterator.next()).id;
                }
            }
            else {
                this.signatureSchemes = new int[0];
            }
        }
        
        SignatureSchemesSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid signature_algorithms: insufficient data");
            }
            final byte[] bytes16 = Record.getBytes16(byteBuffer);
            if (byteBuffer.hasRemaining()) {
                throw new SSLProtocolException("Invalid signature_algorithms: unknown extra data");
            }
            if (bytes16 == null || bytes16.length == 0 || (bytes16.length & 0x1) != 0x0) {
                throw new SSLProtocolException("Invalid signature_algorithms: incomplete data");
            }
            final int[] signatureSchemes = new int[bytes16.length / 2];
            for (int i = 0, n = 0; i < bytes16.length; signatureSchemes[n++] = ((bytes16[i++] & 0xFF) << 8 | (bytes16[i++] & 0xFF))) {}
            this.signatureSchemes = signatureSchemes;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"signature schemes\": '['{0}']'", Locale.ENGLISH);
            if (this.signatureSchemes == null || this.signatureSchemes.length == 0) {
                return messageFormat.format(new Object[] { "<no supported signature schemes specified>" });
            }
            final StringBuilder sb = new StringBuilder(512);
            int n = 1;
            for (final int n2 : this.signatureSchemes) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append(", ");
                }
                sb.append(SignatureScheme.nameOf(n2));
            }
            return messageFormat.format(new Object[] { sb.toString() });
        }
    }
    
    private static final class SignatureSchemesStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new SignatureSchemesSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHSignatureSchemesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms extension", new Object[0]);
                }
                return null;
            }
            if (clientHandshakeContext.localSupportedSignAlgs == null) {
                clientHandshakeContext.localSupportedSignAlgs = SignatureScheme.getSupportedAlgorithms(clientHandshakeContext.sslConfig, clientHandshakeContext.algorithmConstraints, clientHandshakeContext.activeProtocols);
            }
            final int n = SignatureScheme.sizeInRecord() * clientHandshakeContext.localSupportedSignAlgs.size();
            final byte[] array = new byte[n + 2];
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            Record.putInt16(wrap, n);
            final Iterator<SignatureScheme> iterator = clientHandshakeContext.localSupportedSignAlgs.iterator();
            while (iterator.hasNext()) {
                Record.putInt16(wrap, iterator.next().id);
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_SIGNATURE_ALGORITHMS, new SignatureSchemesSpec(clientHandshakeContext.localSupportedSignAlgs));
            return array;
        }
    }
    
    private static final class CHSignatureSchemesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms extension", new Object[0]);
                }
                return;
            }
            SignatureSchemesSpec signatureSchemesSpec;
            try {
                signatureSchemesSpec = new SignatureSchemesSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_SIGNATURE_ALGORITHMS, signatureSchemesSpec);
        }
    }
    
    private static final class CHSignatureSchemesUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final SignatureSchemesSpec signatureSchemesSpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_SIGNATURE_ALGORITHMS);
            if (signatureSchemesSpec == null) {
                return;
            }
            final List<SignatureScheme> supportedAlgorithms = SignatureScheme.getSupportedAlgorithms(serverHandshakeContext.sslConfig, serverHandshakeContext.algorithmConstraints, serverHandshakeContext.negotiatedProtocol, signatureSchemesSpec.signatureSchemes);
            serverHandshakeContext.peerRequestedSignatureSchemes = supportedAlgorithms;
            if (serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT) == null) {
                serverHandshakeContext.peerRequestedCertSignSchemes = supportedAlgorithms;
                serverHandshakeContext.handshakeSession.setPeerSupportedSignatureAlgorithms(supportedAlgorithms);
            }
            if (!serverHandshakeContext.isResumption && serverHandshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
                if (serverHandshakeContext.sslConfig.clientAuthType != ClientAuthType.CLIENT_AUTH_NONE) {
                    serverHandshakeContext.handshakeProducers.putIfAbsent(SSLHandshake.CERTIFICATE_REQUEST.id, SSLHandshake.CERTIFICATE_REQUEST);
                }
                serverHandshakeContext.handshakeProducers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
                serverHandshakeContext.handshakeProducers.putIfAbsent(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            }
        }
    }
    
    private static final class CHSignatureSchemesOnLoadAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
                throw serverHandshakeContext.conContext.fatal(Alert.MISSING_EXTENSION, "No mandatory signature_algorithms extension in the received CertificateRequest handshake message");
            }
        }
    }
    
    private static final class CHSignatureSchemesOnTradeAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.negotiatedProtocol.useTLS12PlusSpec()) {
                final List<SignatureScheme> list = Arrays.asList(SignatureScheme.RSA_PKCS1_SHA1, SignatureScheme.DSA_SHA1, SignatureScheme.ECDSA_SHA1);
                serverHandshakeContext.peerRequestedSignatureSchemes = list;
                if (serverHandshakeContext.peerRequestedCertSignSchemes == null || serverHandshakeContext.peerRequestedCertSignSchemes.isEmpty()) {
                    serverHandshakeContext.peerRequestedCertSignSchemes = list;
                }
                serverHandshakeContext.handshakeSession.setUseDefaultPeerSignAlgs();
            }
        }
    }
    
    private static final class CRSignatureSchemesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CR_SIGNATURE_ALGORITHMS)) {
                throw serverHandshakeContext.conContext.fatal(Alert.MISSING_EXTENSION, "No available signature_algorithms extension for client certificate authentication");
            }
            final List<SignatureScheme> supportedAlgorithms = SignatureScheme.getSupportedAlgorithms(serverHandshakeContext.sslConfig, serverHandshakeContext.algorithmConstraints, Collections.unmodifiableList((List<? extends ProtocolVersion>)Arrays.asList(serverHandshakeContext.negotiatedProtocol)));
            final int n = SignatureScheme.sizeInRecord() * supportedAlgorithms.size();
            final byte[] array = new byte[n + 2];
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            Record.putInt16(wrap, n);
            final Iterator iterator = supportedAlgorithms.iterator();
            while (iterator.hasNext()) {
                Record.putInt16(wrap, ((SignatureScheme)iterator.next()).id);
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CR_SIGNATURE_ALGORITHMS, new SignatureSchemesSpec(serverHandshakeContext.localSupportedSignAlgs));
            return array;
        }
    }
    
    private static final class CRSignatureSchemesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CR_SIGNATURE_ALGORITHMS)) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No available signature_algorithms extension for client certificate authentication");
            }
            SignatureSchemesSpec signatureSchemesSpec;
            try {
                signatureSchemesSpec = new SignatureSchemesSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            final LinkedList list = new LinkedList();
            final int[] signatureSchemes = signatureSchemesSpec.signatureSchemes;
            for (int length = signatureSchemes.length, i = 0; i < length; ++i) {
                final SignatureScheme value = SignatureScheme.valueOf(signatureSchemes[i]);
                if (value != null) {
                    list.add(value);
                }
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CR_SIGNATURE_ALGORITHMS, signatureSchemesSpec);
        }
    }
    
    private static final class CRSignatureSchemesUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final SignatureSchemesSpec signatureSchemesSpec = clientHandshakeContext.handshakeExtensions.get(SSLExtension.CR_SIGNATURE_ALGORITHMS);
            if (signatureSchemesSpec == null) {
                return;
            }
            final List<SignatureScheme> supportedAlgorithms = SignatureScheme.getSupportedAlgorithms(clientHandshakeContext.sslConfig, clientHandshakeContext.algorithmConstraints, clientHandshakeContext.negotiatedProtocol, signatureSchemesSpec.signatureSchemes);
            clientHandshakeContext.peerRequestedSignatureSchemes = supportedAlgorithms;
            if (clientHandshakeContext.handshakeExtensions.get(SSLExtension.CR_SIGNATURE_ALGORITHMS_CERT) == null) {
                clientHandshakeContext.peerRequestedCertSignSchemes = supportedAlgorithms;
                clientHandshakeContext.handshakeSession.setPeerSupportedSignatureAlgorithms(supportedAlgorithms);
            }
        }
    }
    
    private static final class CRSignatureSchemesAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            throw ((ClientHandshakeContext)connectionContext).conContext.fatal(Alert.MISSING_EXTENSION, "No mandatory signature_algorithms extension in the received CertificateRequest handshake message");
        }
    }
}
