package sun.security.ssl;

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
        
        private RenegotiationInfoSpec(final byte[] array) {
            this.renegotiatedConnection = Arrays.copyOf(array, array.length);
        }
        
        private RenegotiationInfoSpec(final ByteBuffer byteBuffer) throws IOException {
            if (!byteBuffer.hasRemaining() || byteBuffer.remaining() < 1) {
                throw new SSLProtocolException("Invalid renegotiation_info extension data: insufficient data");
            }
            this.renegotiatedConnection = Record.getBytes8(byteBuffer);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"renegotiated connection\": '['{0}']'", Locale.ENGLISH);
            if (this.renegotiatedConnection.length == 0) {
                return messageFormat.format(new Object[] { "<no renegotiated connection>" });
            }
            return messageFormat.format(new Object[] { Utilities.toHexString(this.renegotiatedConnection) });
        }
        
        static {
            NOMINAL = new RenegotiationInfoSpec(new byte[0]);
        }
    }
    
    private static final class RenegotiationInfoStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new RenegotiationInfoSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHRenegotiationInfoProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_RENEGOTIATION_INFO)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable renegotiation_info extension", new Object[0]);
                }
                return null;
            }
            if (!clientHandshakeContext.conContext.isNegotiated) {
                if (clientHandshakeContext.activeCipherSuites.contains(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV)) {
                    return null;
                }
                final byte[] array = { 0 };
                clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
                return array;
            }
            else {
                if (clientHandshakeContext.conContext.secureRenegotiation) {
                    final byte[] array2 = new byte[clientHandshakeContext.conContext.clientVerifyData.length + 1];
                    Record.putBytes8(ByteBuffer.wrap(array2), clientHandshakeContext.conContext.clientVerifyData);
                    clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
                    return array2;
                }
                if (HandshakeContext.allowUnsafeRenegotiation) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.warning("Using insecure renegotiation", new Object[0]);
                    }
                    return null;
                }
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "insecure renegotiation is not allowed");
            }
        }
    }
    
    private static final class CHRenegotiationInfoConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_RENEGOTIATION_INFO)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_RENEGOTIATION_INFO.name, new Object[0]);
                }
                return;
            }
            RenegotiationInfoSpec renegotiationInfoSpec;
            try {
                renegotiationInfoSpec = new RenegotiationInfoSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (!serverHandshakeContext.conContext.isNegotiated) {
                if (renegotiationInfoSpec.renegotiatedConnection.length != 0) {
                    throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid renegotiation_info extension data: not empty");
                }
                serverHandshakeContext.conContext.secureRenegotiation = true;
            }
            else {
                if (!serverHandshakeContext.conContext.secureRenegotiation) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "The renegotiation_info is present in a insecure renegotiation");
                }
                if (!Arrays.equals(serverHandshakeContext.conContext.clientVerifyData, renegotiationInfoSpec.renegotiatedConnection)) {
                    throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid renegotiation_info extension data: incorrect verify data in ClientHello");
                }
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
        }
    }
    
    private static final class CHRenegotiationInfoAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ClientHello.ClientHelloMessage clientHelloMessage = (ClientHello.ClientHelloMessage)handshakeMessage;
            if (!serverHandshakeContext.conContext.isNegotiated) {
                final int[] cipherSuiteIds = clientHelloMessage.cipherSuiteIds;
                for (int length = cipherSuiteIds.length, i = 0; i < length; ++i) {
                    if (cipherSuiteIds[i] == CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV.id) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                            SSLLogger.finest("Safe renegotiation, using the SCSV signgling", new Object[0]);
                        }
                        serverHandshakeContext.conContext.secureRenegotiation = true;
                        return;
                    }
                }
                if (!HandshakeContext.allowLegacyHelloMessages) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Failed to negotiate the use of secure renegotiation");
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Warning: No renegotiation indication in ClientHello, allow legacy ClientHello", new Object[0]);
                }
                serverHandshakeContext.conContext.secureRenegotiation = false;
            }
            else {
                if (serverHandshakeContext.conContext.secureRenegotiation) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Inconsistent secure renegotiation indication");
                }
                if (!HandshakeContext.allowUnsafeRenegotiation) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Terminate insecure renegotiation", new Object[0]);
                    }
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsafe renegotiation is not allowed");
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
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_RENEGOTIATION_INFO) == null && !serverHandshakeContext.conContext.secureRenegotiation) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable renegotiation_info extension", new Object[0]);
                }
                return null;
            }
            if (!serverHandshakeContext.conContext.secureRenegotiation) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("No secure renegotiation has been negotiated", new Object[0]);
                }
                return null;
            }
            if (!serverHandshakeContext.conContext.isNegotiated) {
                final byte[] array = { 0 };
                serverHandshakeContext.handshakeExtensions.put(SSLExtension.SH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
                return array;
            }
            final int n = serverHandshakeContext.conContext.clientVerifyData.length + serverHandshakeContext.conContext.serverVerifyData.length;
            final byte[] array2 = new byte[n + 1];
            final ByteBuffer wrap = ByteBuffer.wrap(array2);
            Record.putInt8(wrap, n);
            wrap.put(serverHandshakeContext.conContext.clientVerifyData);
            wrap.put(serverHandshakeContext.conContext.serverVerifyData);
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.SH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
            return array2;
        }
    }
    
    private static final class SHRenegotiationInfoConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_RENEGOTIATION_INFO) == null && !clientHandshakeContext.activeCipherSuites.contains(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV)) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Missing renegotiation_info and SCSV detected in ClientHello");
            }
            RenegotiationInfoSpec renegotiationInfoSpec;
            try {
                renegotiationInfoSpec = new RenegotiationInfoSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (!clientHandshakeContext.conContext.isNegotiated) {
                if (renegotiationInfoSpec.renegotiatedConnection.length != 0) {
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid renegotiation_info in ServerHello: not empty renegotiated_connection");
                }
                clientHandshakeContext.conContext.secureRenegotiation = true;
            }
            else {
                final int n = clientHandshakeContext.conContext.clientVerifyData.length + clientHandshakeContext.conContext.serverVerifyData.length;
                if (renegotiationInfoSpec.renegotiatedConnection.length != n) {
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid renegotiation_info in ServerHello: invalid renegotiated_connection length (" + renegotiationInfoSpec.renegotiatedConnection.length + ")");
                }
                final byte[] clientVerifyData = clientHandshakeContext.conContext.clientVerifyData;
                if (!Utilities.equals(renegotiationInfoSpec.renegotiatedConnection, 0, clientVerifyData.length, clientVerifyData, 0, clientVerifyData.length)) {
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid renegotiation_info in ServerHello: unmatched client_verify_data value");
                }
                final byte[] serverVerifyData = clientHandshakeContext.conContext.serverVerifyData;
                if (!Utilities.equals(renegotiationInfoSpec.renegotiatedConnection, clientVerifyData.length, n, serverVerifyData, 0, serverVerifyData.length)) {
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid renegotiation_info in ServerHello: unmatched server_verify_data value");
                }
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.SH_RENEGOTIATION_INFO, RenegotiationInfoSpec.NOMINAL);
        }
    }
    
    private static final class SHRenegotiationInfoAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_RENEGOTIATION_INFO) == null && !clientHandshakeContext.activeCipherSuites.contains(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV)) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Missing renegotiation_info and SCSV detected in ClientHello");
            }
            if (!clientHandshakeContext.conContext.isNegotiated) {
                if (!HandshakeContext.allowLegacyHelloMessages) {
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Failed to negotiate the use of secure renegotiation");
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Warning: No renegotiation indication in ServerHello, allow legacy ServerHello", new Object[0]);
                }
                clientHandshakeContext.conContext.secureRenegotiation = false;
            }
            else {
                if (clientHandshakeContext.conContext.secureRenegotiation) {
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Inconsistent secure renegotiation indication");
                }
                if (!HandshakeContext.allowUnsafeRenegotiation) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Terminate insecure renegotiation", new Object[0]);
                    }
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsafe renegotiation is not allowed");
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Using insecure renegotiation", new Object[0]);
                }
            }
        }
    }
}
