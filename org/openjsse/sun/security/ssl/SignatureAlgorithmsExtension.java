package org.openjsse.sun.security.ssl;

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
        
        SignatureSchemesSpec(final List<SignatureScheme> schemes) {
            if (schemes != null) {
                this.signatureSchemes = new int[schemes.size()];
                int i = 0;
                for (final SignatureScheme scheme : schemes) {
                    this.signatureSchemes[i++] = scheme.id;
                }
            }
            else {
                this.signatureSchemes = new int[0];
            }
        }
        
        SignatureSchemesSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid signature_algorithms: insufficient data");
            }
            final byte[] algs = Record.getBytes16(buffer);
            if (buffer.hasRemaining()) {
                throw new SSLProtocolException("Invalid signature_algorithms: unknown extra data");
            }
            if (algs == null || algs.length == 0 || (algs.length & 0x1) != 0x0) {
                throw new SSLProtocolException("Invalid signature_algorithms: incomplete data");
            }
            final int[] schemes = new int[algs.length / 2];
            byte hash;
            byte sign;
            for (int i = 0, j = 0; i < algs.length; hash = algs[i++], sign = algs[i++], schemes[j++] = ((hash & 0xFF) << 8 | (sign & 0xFF))) {}
            this.signatureSchemes = schemes;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"signature schemes\": '['{0}']'", Locale.ENGLISH);
            if (this.signatureSchemes == null || this.signatureSchemes.length == 0) {
                final Object[] messageFields = { "<no supported signature schemes specified>" };
                return messageFormat.format(messageFields);
            }
            final StringBuilder builder = new StringBuilder(512);
            boolean isFirst = true;
            for (final int pv : this.signatureSchemes) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    builder.append(", ");
                }
                builder.append(SignatureScheme.nameOf(pv));
            }
            final Object[] messageFields2 = { builder.toString() };
            return messageFormat.format(messageFields2);
        }
    }
    
    private static final class SignatureSchemesStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new SignatureSchemesSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHSignatureSchemesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms extension", new Object[0]);
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
            chc.handshakeExtensions.put(SSLExtension.CH_SIGNATURE_ALGORITHMS, new SignatureSchemesSpec(chc.localSupportedSignAlgs));
            return extData;
        }
    }
    
    private static final class CHSignatureSchemesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_SIGNATURE_ALGORITHMS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable signature_algorithms extension", new Object[0]);
                }
                return;
            }
            SignatureSchemesSpec spec;
            try {
                spec = new SignatureSchemesSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            shc.handshakeExtensions.put(SSLExtension.CH_SIGNATURE_ALGORITHMS, spec);
        }
    }
    
    private static final class CHSignatureSchemesUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final SignatureSchemesSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_SIGNATURE_ALGORITHMS);
            if (spec == null) {
                return;
            }
            final List<SignatureScheme> sss = SignatureScheme.getSupportedAlgorithms(shc.sslConfig, shc.algorithmConstraints, shc.negotiatedProtocol, spec.signatureSchemes);
            shc.peerRequestedSignatureSchemes = sss;
            final SignatureSchemesSpec certSpec = shc.handshakeExtensions.get(SSLExtension.CH_SIGNATURE_ALGORITHMS_CERT);
            if (certSpec == null) {
                shc.peerRequestedCertSignSchemes = sss;
                shc.handshakeSession.setPeerSupportedSignatureAlgorithms(sss);
            }
            if (!shc.isResumption && shc.negotiatedProtocol.useTLS13PlusSpec()) {
                if (shc.sslConfig.clientAuthType != ClientAuthType.CLIENT_AUTH_NONE) {
                    shc.handshakeProducers.putIfAbsent(SSLHandshake.CERTIFICATE_REQUEST.id, SSLHandshake.CERTIFICATE_REQUEST);
                }
                shc.handshakeProducers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
                shc.handshakeProducers.putIfAbsent(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            }
        }
    }
    
    private static final class CHSignatureSchemesOnLoadAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.negotiatedProtocol.useTLS13PlusSpec()) {
                throw shc.conContext.fatal(Alert.MISSING_EXTENSION, "No mandatory signature_algorithms extension in the received CertificateRequest handshake message");
            }
        }
    }
    
    private static final class CHSignatureSchemesOnTradeAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.negotiatedProtocol.useTLS12PlusSpec()) {
                final List<SignatureScheme> schemes = Arrays.asList(SignatureScheme.RSA_PKCS1_SHA1, SignatureScheme.DSA_SHA1, SignatureScheme.ECDSA_SHA1);
                shc.peerRequestedSignatureSchemes = schemes;
                if (shc.peerRequestedCertSignSchemes == null || shc.peerRequestedCertSignSchemes.isEmpty()) {
                    shc.peerRequestedCertSignSchemes = schemes;
                }
                shc.handshakeSession.setUseDefaultPeerSignAlgs();
            }
        }
    }
    
    private static final class CRSignatureSchemesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CR_SIGNATURE_ALGORITHMS)) {
                throw shc.conContext.fatal(Alert.MISSING_EXTENSION, "No available signature_algorithms extension for client certificate authentication");
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
            shc.handshakeExtensions.put(SSLExtension.CR_SIGNATURE_ALGORITHMS, new SignatureSchemesSpec(shc.localSupportedSignAlgs));
            return extData;
        }
    }
    
    private static final class CRSignatureSchemesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CR_SIGNATURE_ALGORITHMS)) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No available signature_algorithms extension for client certificate authentication");
            }
            SignatureSchemesSpec spec;
            try {
                spec = new SignatureSchemesSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            final List<SignatureScheme> knownSignatureSchemes = new LinkedList<SignatureScheme>();
            for (final int id : spec.signatureSchemes) {
                final SignatureScheme ss = SignatureScheme.valueOf(id);
                if (ss != null) {
                    knownSignatureSchemes.add(ss);
                }
            }
            chc.handshakeExtensions.put(SSLExtension.CR_SIGNATURE_ALGORITHMS, spec);
        }
    }
    
    private static final class CRSignatureSchemesUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final SignatureSchemesSpec spec = chc.handshakeExtensions.get(SSLExtension.CR_SIGNATURE_ALGORITHMS);
            if (spec == null) {
                return;
            }
            final List<SignatureScheme> sss = SignatureScheme.getSupportedAlgorithms(chc.sslConfig, chc.algorithmConstraints, chc.negotiatedProtocol, spec.signatureSchemes);
            chc.peerRequestedSignatureSchemes = sss;
            final SignatureSchemesSpec certSpec = chc.handshakeExtensions.get(SSLExtension.CR_SIGNATURE_ALGORITHMS_CERT);
            if (certSpec == null) {
                chc.peerRequestedCertSignSchemes = sss;
                chc.handshakeSession.setPeerSupportedSignatureAlgorithms(sss);
            }
        }
    }
    
    private static final class CRSignatureSchemesAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            throw chc.conContext.fatal(Alert.MISSING_EXTENSION, "No mandatory signature_algorithms extension in the received CertificateRequest handshake message");
        }
    }
}
