package org.openjsse.sun.security.ssl;

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
        public String toString(final ByteBuffer buffer) {
            try {
                return new SignatureAlgorithmsExtension.SignatureSchemesSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHCertSignatureSchemesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms_cert extension", new Object[0]);
                }
                return null;
            }
            if (chc.localSupportedSignAlgs == null) {
                chc.localSupportedSignAlgs = SignatureScheme.getSupportedAlgorithms(chc.sslConfig, chc.algorithmConstraints, chc.activeProtocols);
            }
            final int vectorLen = SignatureScheme.sizeInRecord() * chc.localSupportedSignAlgs.size();
            final byte[] extData = new byte[vectorLen + 2];
            final ByteBuffer m = ByteBuffer.wrap(extData);
            Record.putInt16(m, vectorLen);
            for (final SignatureScheme ss : chc.localSupportedSignAlgs) {
                Record.putInt16(m, ss.id);
            }
            chc.handshakeExtensions.put(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT, new SignatureAlgorithmsExtension.SignatureSchemesSpec(chc.localSupportedSignAlgs));
            return extData;
        }
    }
    
    private static final class CHCertSignatureSchemesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms_cert extension", new Object[0]);
                }
                return;
            }
            SignatureAlgorithmsExtension.SignatureSchemesSpec spec;
            try {
                spec = new SignatureAlgorithmsExtension.SignatureSchemesSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            shc.handshakeExtensions.put(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT, spec);
        }
    }
    
    private static final class CHCertSignatureSchemesUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final SignatureAlgorithmsExtension.SignatureSchemesSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT);
            if (spec == null) {
                return;
            }
            final List<SignatureScheme> schemes = SignatureScheme.getSupportedAlgorithms(shc.sslConfig, shc.algorithmConstraints, shc.negotiatedProtocol, spec.signatureSchemes);
            shc.peerRequestedCertSignSchemes = schemes;
            shc.handshakeSession.setPeerSupportedSignatureAlgorithms(schemes);
            if (!shc.isResumption && shc.negotiatedProtocol.useTLS13PlusSpec()) {
                if (shc.sslConfig.clientAuthType != ClientAuthType.CLIENT_AUTH_NONE) {
                    shc.handshakeProducers.putIfAbsent(SSLHandshake.CERTIFICATE_REQUEST.id, SSLHandshake.CERTIFICATE_REQUEST);
                }
                shc.handshakeProducers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
                shc.handshakeProducers.putIfAbsent(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            }
        }
    }
    
    private static final class CRCertSignatureSchemesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms_cert extension", new Object[0]);
                }
                return null;
            }
            List<ProtocolVersion> protocols = Arrays.asList(shc.negotiatedProtocol);
            protocols = Collections.unmodifiableList((List<? extends ProtocolVersion>)protocols);
            final List<SignatureScheme> sigAlgs = SignatureScheme.getSupportedAlgorithms(shc.sslConfig, shc.algorithmConstraints, protocols);
            final int vectorLen = SignatureScheme.sizeInRecord() * sigAlgs.size();
            final byte[] extData = new byte[vectorLen + 2];
            final ByteBuffer m = ByteBuffer.wrap(extData);
            Record.putInt16(m, vectorLen);
            for (final SignatureScheme ss : sigAlgs) {
                Record.putInt16(m, ss.id);
            }
            shc.handshakeExtensions.put(SSLExtension.CR_SIGNATURE_ALGORITHMS_CERT, new SignatureAlgorithmsExtension.SignatureSchemesSpec(shc.localSupportedSignAlgs));
            return extData;
        }
    }
    
    private static final class CRCertSignatureSchemesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms_cert extension", new Object[0]);
                }
                return;
            }
            SignatureAlgorithmsExtension.SignatureSchemesSpec spec;
            try {
                spec = new SignatureAlgorithmsExtension.SignatureSchemesSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            chc.handshakeExtensions.put(SSLExtension.CR_SIGNATURE_ALGORITHMS_CERT, spec);
        }
    }
    
    private static final class CRCertSignatureSchemesUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final SignatureAlgorithmsExtension.SignatureSchemesSpec spec = chc.handshakeExtensions.get(SSLExtension.CR_SIGNATURE_ALGORITHMS_CERT);
            if (spec == null) {
                return;
            }
            final List<SignatureScheme> schemes = SignatureScheme.getSupportedAlgorithms(chc.sslConfig, chc.algorithmConstraints, chc.negotiatedProtocol, spec.signatureSchemes);
            chc.peerRequestedCertSignSchemes = schemes;
            chc.handshakeSession.setPeerSupportedSignatureAlgorithms(schemes);
        }
    }
}
