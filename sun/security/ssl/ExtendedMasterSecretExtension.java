package sun.security.ssl;

import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;

final class ExtendedMasterSecretExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeAbsence chOnLoadAbsence;
    static final HandshakeProducer shNetworkProducer;
    static final SSLExtension.ExtensionConsumer shOnLoadConsumer;
    static final HandshakeAbsence shOnLoadAbsence;
    static final SSLStringizer emsStringizer;
    
    static {
        chNetworkProducer = new CHExtendedMasterSecretProducer();
        chOnLoadConsumer = new CHExtendedMasterSecretConsumer();
        chOnLoadAbsence = new CHExtendedMasterSecretAbsence();
        shNetworkProducer = new SHExtendedMasterSecretProducer();
        shOnLoadConsumer = new SHExtendedMasterSecretConsumer();
        shOnLoadAbsence = new SHExtendedMasterSecretAbsence();
        emsStringizer = new ExtendedMasterSecretStringizer();
    }
    
    static final class ExtendedMasterSecretSpec implements SSLExtension.SSLExtensionSpec
    {
        static final ExtendedMasterSecretSpec NOMINAL;
        
        private ExtendedMasterSecretSpec() {
        }
        
        private ExtendedMasterSecretSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.hasRemaining()) {
                throw new SSLProtocolException("Invalid extended_master_secret extension data: not empty");
            }
        }
        
        @Override
        public String toString() {
            return "<empty>";
        }
        
        static {
            NOMINAL = new ExtendedMasterSecretSpec();
        }
    }
    
    private static final class ExtendedMasterSecretStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new ExtendedMasterSecretSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHExtendedMasterSecretProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_EXTENDED_MASTER_SECRET) || !SSLConfiguration.useExtendedMasterSecret || !clientHandshakeContext.conContext.protocolVersion.useTLS10PlusSpec()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extended_master_secret extension", new Object[0]);
                }
                return null;
            }
            if (clientHandshakeContext.handshakeSession == null || clientHandshakeContext.handshakeSession.useExtendedMasterSecret) {
                final byte[] array = new byte[0];
                clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_EXTENDED_MASTER_SECRET, ExtendedMasterSecretSpec.NOMINAL);
                return array;
            }
            return null;
        }
    }
    
    private static final class CHExtendedMasterSecretConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_EXTENDED_MASTER_SECRET) || !SSLConfiguration.useExtendedMasterSecret || !serverHandshakeContext.negotiatedProtocol.useTLS10PlusSpec()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_EXTENDED_MASTER_SECRET.name, new Object[0]);
                }
                return;
            }
            try {
                final ExtendedMasterSecretSpec extendedMasterSecretSpec = new ExtendedMasterSecretSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (serverHandshakeContext.isResumption && serverHandshakeContext.resumingSession != null && !serverHandshakeContext.resumingSession.useExtendedMasterSecret) {
                serverHandshakeContext.isResumption = false;
                serverHandshakeContext.resumingSession = null;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("abort session resumption which did not use Extended Master Secret extension", new Object[0]);
                }
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_EXTENDED_MASTER_SECRET, ExtendedMasterSecretSpec.NOMINAL);
        }
    }
    
    private static final class CHExtendedMasterSecretAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_EXTENDED_MASTER_SECRET) || !SSLConfiguration.useExtendedMasterSecret) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_EXTENDED_MASTER_SECRET.name, new Object[0]);
                }
                return;
            }
            if (serverHandshakeContext.negotiatedProtocol.useTLS10PlusSpec() && !SSLConfiguration.allowLegacyMasterSecret) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Extended Master Secret extension is required");
            }
            if (serverHandshakeContext.isResumption && serverHandshakeContext.resumingSession != null) {
                if (serverHandshakeContext.resumingSession.useExtendedMasterSecret) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing Extended Master Secret extension on session resumption");
                }
                if (!SSLConfiguration.allowLegacyResumption) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing Extended Master Secret extension on session resumption");
                }
                serverHandshakeContext.isResumption = false;
                serverHandshakeContext.resumingSession = null;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("abort session resumption, missing Extended Master Secret extension", new Object[0]);
                }
            }
        }
    }
    
    private static final class SHExtendedMasterSecretProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.handshakeSession.useExtendedMasterSecret) {
                final byte[] array = new byte[0];
                serverHandshakeContext.handshakeExtensions.put(SSLExtension.SH_EXTENDED_MASTER_SECRET, ExtendedMasterSecretSpec.NOMINAL);
                return array;
            }
            return null;
        }
    }
    
    private static final class SHExtendedMasterSecretConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_EXTENDED_MASTER_SECRET) == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNSUPPORTED_EXTENSION, "Server sent the extended_master_secret extension improperly");
            }
            try {
                final ExtendedMasterSecretSpec extendedMasterSecretSpec = new ExtendedMasterSecretSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (clientHandshakeContext.isResumption && clientHandshakeContext.resumingSession != null && !clientHandshakeContext.resumingSession.useExtendedMasterSecret) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNSUPPORTED_EXTENSION, "Server sent an unexpected extended_master_secret extension on session resumption");
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.SH_EXTENDED_MASTER_SECRET, ExtendedMasterSecretSpec.NOMINAL);
        }
    }
    
    private static final class SHExtendedMasterSecretAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (SSLConfiguration.useExtendedMasterSecret && !SSLConfiguration.allowLegacyMasterSecret) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Extended Master Secret extension is required");
            }
            if (clientHandshakeContext.isResumption && clientHandshakeContext.resumingSession != null) {
                if (clientHandshakeContext.resumingSession.useExtendedMasterSecret) {
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing Extended Master Secret extension on session resumption");
                }
                if (SSLConfiguration.useExtendedMasterSecret && !SSLConfiguration.allowLegacyResumption && clientHandshakeContext.negotiatedProtocol.useTLS10PlusSpec()) {
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Extended Master Secret extension is required");
                }
            }
        }
    }
}
