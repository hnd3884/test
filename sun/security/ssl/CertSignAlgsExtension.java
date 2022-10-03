package sun.security.ssl;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;
import java.nio.ByteBuffer;

final class CertSignAlgsExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeConsumer chOnTradeConsumer;
    static final HandshakeProducer crNetworkProducer;
    static final SSLExtension.ExtensionConsumer crOnLoadConsumer;
    static final HandshakeConsumer crOnTradeConsumer;
    static final SSLStringizer ssStringizer;
    
    static {
        chNetworkProducer = new CHCertSignatureSchemesProducer();
        chOnLoadConsumer = new CHCertSignatureSchemesConsumer();
        chOnTradeConsumer = new CHCertSignatureSchemesUpdate();
        crNetworkProducer = new CRCertSignatureSchemesProducer();
        crOnLoadConsumer = new CRCertSignatureSchemesConsumer();
        crOnTradeConsumer = new CRCertSignatureSchemesUpdate();
        ssStringizer = new CertSignatureSchemesStringizer();
    }
    
    private static final class CertSignatureSchemesStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new SignatureAlgorithmsExtension.SignatureSchemesSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHCertSignatureSchemesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms_cert extension", new Object[0]);
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
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT, new SignatureAlgorithmsExtension.SignatureSchemesSpec(clientHandshakeContext.localSupportedSignAlgs));
            return array;
        }
    }
    
    private static final class CHCertSignatureSchemesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms_cert extension", new Object[0]);
                }
                return;
            }
            SignatureAlgorithmsExtension.SignatureSchemesSpec signatureSchemesSpec;
            try {
                signatureSchemesSpec = new SignatureAlgorithmsExtension.SignatureSchemesSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT, signatureSchemesSpec);
        }
    }
    
    private static final class CHCertSignatureSchemesUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final SignatureAlgorithmsExtension.SignatureSchemesSpec signatureSchemesSpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT);
            if (signatureSchemesSpec == null) {
                return;
            }
            final List<SignatureScheme> supportedAlgorithms = SignatureScheme.getSupportedAlgorithms(serverHandshakeContext.sslConfig, serverHandshakeContext.algorithmConstraints, serverHandshakeContext.negotiatedProtocol, signatureSchemesSpec.signatureSchemes);
            serverHandshakeContext.peerRequestedCertSignSchemes = supportedAlgorithms;
            serverHandshakeContext.handshakeSession.setPeerSupportedSignatureAlgorithms(supportedAlgorithms);
            if (!serverHandshakeContext.isResumption && serverHandshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
                if (serverHandshakeContext.sslConfig.clientAuthType != ClientAuthType.CLIENT_AUTH_NONE) {
                    serverHandshakeContext.handshakeProducers.putIfAbsent(SSLHandshake.CERTIFICATE_REQUEST.id, SSLHandshake.CERTIFICATE_REQUEST);
                }
                serverHandshakeContext.handshakeProducers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
                serverHandshakeContext.handshakeProducers.putIfAbsent(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            }
        }
    }
    
    private static final class CRCertSignatureSchemesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms_cert extension", new Object[0]);
                }
                return null;
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
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CR_SIGNATURE_ALGORITHMS_CERT, new SignatureAlgorithmsExtension.SignatureSchemesSpec(serverHandshakeContext.localSupportedSignAlgs));
            return array;
        }
    }
    
    private static final class CRCertSignatureSchemesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms_cert extension", new Object[0]);
                }
                return;
            }
            SignatureAlgorithmsExtension.SignatureSchemesSpec signatureSchemesSpec;
            try {
                signatureSchemesSpec = new SignatureAlgorithmsExtension.SignatureSchemesSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CR_SIGNATURE_ALGORITHMS_CERT, signatureSchemesSpec);
        }
    }
    
    private static final class CRCertSignatureSchemesUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final SignatureAlgorithmsExtension.SignatureSchemesSpec signatureSchemesSpec = clientHandshakeContext.handshakeExtensions.get(SSLExtension.CR_SIGNATURE_ALGORITHMS_CERT);
            if (signatureSchemesSpec == null) {
                return;
            }
            final List<SignatureScheme> supportedAlgorithms = SignatureScheme.getSupportedAlgorithms(clientHandshakeContext.sslConfig, clientHandshakeContext.algorithmConstraints, clientHandshakeContext.negotiatedProtocol, signatureSchemesSpec.signatureSchemes);
            clientHandshakeContext.peerRequestedCertSignSchemes = supportedAlgorithms;
            clientHandshakeContext.handshakeSession.setPeerSupportedSignatureAlgorithms(supportedAlgorithms);
        }
    }
}
