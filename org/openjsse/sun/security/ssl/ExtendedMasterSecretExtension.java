package org.openjsse.sun.security.ssl;

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
        
        private ExtendedMasterSecretSpec(final ByteBuffer m) throws IOException {
            if (m.hasRemaining()) {
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
        public String toString(final ByteBuffer buffer) {
            try {
                return new ExtendedMasterSecretSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHExtendedMasterSecretProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_EXTENDED_MASTER_SECRET) || !SSLConfiguration.useExtendedMasterSecret || !chc.conContext.protocolVersion.useTLS10PlusSpec()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extended_master_secret extension", new Object[0]);
                }
                return null;
            }
            if (chc.handshakeSession == null || chc.handshakeSession.useExtendedMasterSecret) {
                final byte[] extData = new byte[0];
                chc.handshakeExtensions.put(SSLExtension.CH_EXTENDED_MASTER_SECRET, ExtendedMasterSecretSpec.NOMINAL);
                return extData;
            }
            return null;
        }
    }
    
    private static final class CHExtendedMasterSecretConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_EXTENDED_MASTER_SECRET) || !SSLConfiguration.useExtendedMasterSecret || !shc.negotiatedProtocol.useTLS10PlusSpec()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_EXTENDED_MASTER_SECRET.name, new Object[0]);
                }
                return;
            }
            try {
                final ExtendedMasterSecretSpec spec = new ExtendedMasterSecretSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (shc.isResumption && shc.resumingSession != null && !shc.resumingSession.useExtendedMasterSecret) {
                shc.isResumption = false;
                shc.resumingSession = null;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("abort session resumption which did not use Extended Master Secret extension", new Object[0]);
                }
            }
            shc.handshakeExtensions.put(SSLExtension.CH_EXTENDED_MASTER_SECRET, ExtendedMasterSecretSpec.NOMINAL);
        }
    }
    
    private static final class CHExtendedMasterSecretAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_EXTENDED_MASTER_SECRET) || !SSLConfiguration.useExtendedMasterSecret) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_EXTENDED_MASTER_SECRET.name, new Object[0]);
                }
                return;
            }
            if (shc.negotiatedProtocol.useTLS10PlusSpec() && !SSLConfiguration.allowLegacyMasterSecret) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Extended Master Secret extension is required");
            }
            if (shc.isResumption && shc.resumingSession != null) {
                if (shc.resumingSession.useExtendedMasterSecret) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing Extended Master Secret extension on session resumption");
                }
                if (!SSLConfiguration.allowLegacyResumption) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing Extended Master Secret extension on session resumption");
                }
                shc.isResumption = false;
                shc.resumingSession = null;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("abort session resumption, missing Extended Master Secret extension", new Object[0]);
                }
            }
        }
    }
    
    private static final class SHExtendedMasterSecretProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.handshakeSession.useExtendedMasterSecret) {
                final byte[] extData = new byte[0];
                shc.handshakeExtensions.put(SSLExtension.SH_EXTENDED_MASTER_SECRET, ExtendedMasterSecretSpec.NOMINAL);
                return extData;
            }
            return null;
        }
    }
    
    private static final class SHExtendedMasterSecretConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final ExtendedMasterSecretSpec requstedSpec = chc.handshakeExtensions.get(SSLExtension.CH_EXTENDED_MASTER_SECRET);
            if (requstedSpec == null) {
                throw chc.conContext.fatal(Alert.UNSUPPORTED_EXTENSION, "Server sent the extended_master_secret extension improperly");
            }
            try {
                final ExtendedMasterSecretSpec spec = new ExtendedMasterSecretSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (chc.isResumption && chc.resumingSession != null && !chc.resumingSession.useExtendedMasterSecret) {
                throw chc.conContext.fatal(Alert.UNSUPPORTED_EXTENSION, "Server sent an unexpected extended_master_secret extension on session resumption");
            }
            chc.handshakeExtensions.put(SSLExtension.SH_EXTENDED_MASTER_SECRET, ExtendedMasterSecretSpec.NOMINAL);
        }
    }
    
    private static final class SHExtendedMasterSecretAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (SSLConfiguration.useExtendedMasterSecret && !SSLConfiguration.allowLegacyMasterSecret) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Extended Master Secret extension is required");
            }
            if (chc.isResumption && chc.resumingSession != null) {
                if (chc.resumingSession.useExtendedMasterSecret) {
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing Extended Master Secret extension on session resumption");
                }
                if (SSLConfiguration.useExtendedMasterSecret && !SSLConfiguration.allowLegacyResumption && chc.negotiatedProtocol.useTLS10PlusSpec()) {
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Extended Master Secret extension is required");
                }
            }
        }
    }
}
