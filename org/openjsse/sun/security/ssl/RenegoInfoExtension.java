package org.openjsse.sun.security.ssl;

import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;
import java.util.Arrays;

final class RenegoInfoExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeAbsence chOnLoadAbsence;
    static final HandshakeProducer shNetworkProducer;
    static final SSLExtension.ExtensionConsumer shOnLoadConsumer;
    static final HandshakeAbsence shOnLoadAbsence;
    static final SSLStringizer rniStringizer;
    
    static {
        chNetworkProducer = new CHRenegotiationInfoProducer();
        chOnLoadConsumer = new CHRenegotiationInfoConsumer();
        chOnLoadAbsence = new CHRenegotiationInfoAbsence();
        shNetworkProducer = new SHRenegotiationInfoProducer();
        shOnLoadConsumer = new SHRenegotiationInfoConsumer();
        shOnLoadAbsence = new SHRenegotiationInfoAbsence();
        rniStringizer = new RenegotiationInfoStringizer();
    }
    
    static final class RenegotiationInfoSpec implements SSLExtension.SSLExtensionSpec
    {
        static final RenegotiationInfoSpec NOMINAL;
        private final byte[] renegotiatedConnection;
        
        private RenegotiationInfoSpec(final byte[] renegotiatedConnection) {
            this.renegotiatedConnection = Arrays.copyOf(renegotiatedConnection, renegotiatedConnection.length);
        }
        
        private RenegotiationInfoSpec(final ByteBuffer m) throws IOException {
            if (!m.hasRemaining() || m.remaining() < 1) {
                throw new SSLProtocolException("Invalid renegotiation_info extension data: insufficient data");
            }
            this.renegotiatedConnection = Record.getBytes8(m);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"renegotiated connection\": '['{0}']'", Locale.ENGLISH);
            if (this.renegotiatedConnection.length == 0) {
                final Object[] messageFields = { "<no renegotiated connection>" };
                return messageFormat.format(messageFields);
            }
            final Object[] messageFields = { Utilities.toHexString(this.renegotiatedConnection) };
            return messageFormat.format(messageFields);
        }
        
        static {
            NOMINAL = new RenegotiationInfoSpec(new byte[0]);
        }
    }
    
    private static final class RenegotiationInfoStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new RenegotiationInfoSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHRenegotiationInfoProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_RENEGOTIATION_INFO)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable renegotiation_info extension", new Object[0]);
                }
                return null;
            }
            if (!chc.conContext.isNegotiated) {
                if (chc.activeCipherSuites.contains(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV)) {
                    return null;
                }
                final byte[] extData = { 0 };
                chc.handshakeExtensions.put(SSLExtension.CH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
                return extData;
            }
            else {
                if (chc.conContext.secureRenegotiation) {
                    final byte[] extData = new byte[chc.conContext.clientVerifyData.length + 1];
                    final ByteBuffer m = ByteBuffer.wrap(extData);
                    Record.putBytes8(m, chc.conContext.clientVerifyData);
                    chc.handshakeExtensions.put(SSLExtension.CH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
                    return extData;
                }
                if (HandshakeContext.allowUnsafeRenegotiation) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.warning("Using insecure renegotiation", new Object[0]);
                    }
                    return null;
                }
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "insecure renegotiation is not allowed");
            }
        }
    }
    
    private static final class CHRenegotiationInfoConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_RENEGOTIATION_INFO)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_RENEGOTIATION_INFO.name, new Object[0]);
                }
                return;
            }
            RenegotiationInfoSpec spec;
            try {
                spec = new RenegotiationInfoSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (!shc.conContext.isNegotiated) {
                if (spec.renegotiatedConnection.length != 0) {
                    throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid renegotiation_info extension data: not empty");
                }
                shc.conContext.secureRenegotiation = true;
            }
            else {
                if (!shc.conContext.secureRenegotiation) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "The renegotiation_info is present in a insecure renegotiation");
                }
                if (!Arrays.equals(shc.conContext.clientVerifyData, spec.renegotiatedConnection)) {
                    throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid renegotiation_info extension data: incorrect verify data in ClientHello");
                }
            }
            shc.handshakeExtensions.put(SSLExtension.CH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
        }
    }
    
    private static final class CHRenegotiationInfoAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            if (!shc.conContext.isNegotiated) {
                for (final int id : clientHello.cipherSuiteIds) {
                    if (id == CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV.id) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                            SSLLogger.finest("Safe renegotiation, using the SCSV signgling", new Object[0]);
                        }
                        shc.conContext.secureRenegotiation = true;
                        return;
                    }
                }
                if (!HandshakeContext.allowLegacyHelloMessages) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Failed to negotiate the use of secure renegotiation");
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Warning: No renegotiation indication in ClientHello, allow legacy ClientHello", new Object[0]);
                }
                shc.conContext.secureRenegotiation = false;
            }
            else {
                if (shc.conContext.secureRenegotiation) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Inconsistent secure renegotiation indication");
                }
                if (!HandshakeContext.allowUnsafeRenegotiation) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Terminate insecure renegotiation", new Object[0]);
                    }
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsafe renegotiation is not allowed");
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Using insecure renegotiation", new Object[0]);
                }
            }
        }
    }
    
    private static final class SHRenegotiationInfoProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final RenegotiationInfoSpec requestedSpec = shc.handshakeExtensions.get(SSLExtension.CH_RENEGOTIATION_INFO);
            if (requestedSpec == null && !shc.conContext.secureRenegotiation) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable renegotiation_info extension", new Object[0]);
                }
                return null;
            }
            if (!shc.conContext.secureRenegotiation) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("No secure renegotiation has been negotiated", new Object[0]);
                }
                return null;
            }
            if (!shc.conContext.isNegotiated) {
                final byte[] extData = { 0 };
                shc.handshakeExtensions.put(SSLExtension.SH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
                return extData;
            }
            final int infoLen = shc.conContext.clientVerifyData.length + shc.conContext.serverVerifyData.length;
            final byte[] extData2 = new byte[infoLen + 1];
            final ByteBuffer m = ByteBuffer.wrap(extData2);
            Record.putInt8(m, infoLen);
            m.put(shc.conContext.clientVerifyData);
            m.put(shc.conContext.serverVerifyData);
            shc.handshakeExtensions.put(SSLExtension.SH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
            return extData2;
        }
    }
    
    private static final class SHRenegotiationInfoConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final RenegotiationInfoSpec requestedSpec = chc.handshakeExtensions.get(SSLExtension.CH_RENEGOTIATION_INFO);
            if (requestedSpec == null && !chc.activeCipherSuites.contains(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV)) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Missing renegotiation_info and SCSV detected in ClientHello");
            }
            RenegotiationInfoSpec spec;
            try {
                spec = new RenegotiationInfoSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (!chc.conContext.isNegotiated) {
                if (spec.renegotiatedConnection.length != 0) {
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid renegotiation_info in ServerHello: not empty renegotiated_connection");
                }
                chc.conContext.secureRenegotiation = true;
            }
            else {
                final int infoLen = chc.conContext.clientVerifyData.length + chc.conContext.serverVerifyData.length;
                if (spec.renegotiatedConnection.length != infoLen) {
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid renegotiation_info in ServerHello: invalid renegotiated_connection length (" + spec.renegotiatedConnection.length + ")");
                }
                final byte[] cvd = chc.conContext.clientVerifyData;
                if (!Utilities.equals(spec.renegotiatedConnection, 0, cvd.length, cvd, 0, cvd.length)) {
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid renegotiation_info in ServerHello: unmatched client_verify_data value");
                }
                final byte[] svd = chc.conContext.serverVerifyData;
                if (!Utilities.equals(spec.renegotiatedConnection, cvd.length, infoLen, svd, 0, svd.length)) {
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid renegotiation_info in ServerHello: unmatched server_verify_data value");
                }
            }
            chc.handshakeExtensions.put(SSLExtension.SH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
        }
    }
    
    private static final class SHRenegotiationInfoAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final RenegotiationInfoSpec requestedSpec = chc.handshakeExtensions.get(SSLExtension.CH_RENEGOTIATION_INFO);
            if (requestedSpec == null && !chc.activeCipherSuites.contains(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV)) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Missing renegotiation_info and SCSV detected in ClientHello");
            }
            if (!chc.conContext.isNegotiated) {
                if (!HandshakeContext.allowLegacyHelloMessages) {
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Failed to negotiate the use of secure renegotiation");
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Warning: No renegotiation indication in ServerHello, allow legacy ServerHello", new Object[0]);
                }
                chc.conContext.secureRenegotiation = false;
            }
            else {
                if (chc.conContext.secureRenegotiation) {
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Inconsistent secure renegotiation indication");
                }
                if (!HandshakeContext.allowUnsafeRenegotiation) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Terminate insecure renegotiation", new Object[0]);
                    }
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsafe renegotiation is not allowed");
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Using insecure renegotiation", new Object[0]);
                }
            }
        }
    }
}
