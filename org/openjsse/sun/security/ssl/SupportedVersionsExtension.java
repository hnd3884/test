package org.openjsse.sun.security.ssl;

import java.util.Iterator;
import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;

final class SupportedVersionsExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final SSLStringizer chStringizer;
    static final HandshakeProducer shNetworkProducer;
    static final SSLExtension.ExtensionConsumer shOnLoadConsumer;
    static final SSLStringizer shStringizer;
    static final HandshakeProducer hrrNetworkProducer;
    static final SSLExtension.ExtensionConsumer hrrOnLoadConsumer;
    static final HandshakeProducer hrrReproducer;
    static final SSLStringizer hrrStringizer;
    
    static {
        chNetworkProducer = new CHSupportedVersionsProducer();
        chOnLoadConsumer = new CHSupportedVersionsConsumer();
        chStringizer = new CHSupportedVersionsStringizer();
        shNetworkProducer = new SHSupportedVersionsProducer();
        shOnLoadConsumer = new SHSupportedVersionsConsumer();
        shStringizer = new SHSupportedVersionsStringizer();
        hrrNetworkProducer = new HRRSupportedVersionsProducer();
        hrrOnLoadConsumer = new HRRSupportedVersionsConsumer();
        hrrReproducer = new HRRSupportedVersionsReproducer();
        hrrStringizer = new SHSupportedVersionsStringizer();
    }
    
    static final class CHSupportedVersionsSpec implements SSLExtension.SSLExtensionSpec
    {
        final int[] requestedProtocols;
        
        private CHSupportedVersionsSpec(final int[] requestedProtocols) {
            this.requestedProtocols = requestedProtocols;
        }
        
        private CHSupportedVersionsSpec(final ByteBuffer m) throws IOException {
            if (m.remaining() < 3) {
                throw new SSLProtocolException("Invalid supported_versions extension: insufficient data");
            }
            final byte[] vbs = Record.getBytes8(m);
            if (m.hasRemaining()) {
                throw new SSLProtocolException("Invalid supported_versions extension: unknown extra data");
            }
            if (vbs == null || vbs.length == 0 || (vbs.length & 0x1) != 0x0) {
                throw new SSLProtocolException("Invalid supported_versions extension: incomplete data");
            }
            final int[] protocols = new int[vbs.length >> 1];
            byte major;
            byte minor;
            for (int i = 0, j = 0; i < vbs.length; major = vbs[i++], minor = vbs[i++], protocols[j++] = ((major & 0xFF) << 8 | (minor & 0xFF))) {}
            this.requestedProtocols = protocols;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"versions\": '['{0}']'", Locale.ENGLISH);
            if (this.requestedProtocols == null || this.requestedProtocols.length == 0) {
                final Object[] messageFields = { "<no supported version specified>" };
                return messageFormat.format(messageFields);
            }
            final StringBuilder builder = new StringBuilder(512);
            boolean isFirst = true;
            for (final int pv : this.requestedProtocols) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    builder.append(", ");
                }
                builder.append(ProtocolVersion.nameOf(pv));
            }
            final Object[] messageFields2 = { builder.toString() };
            return messageFormat.format(messageFields2);
        }
    }
    
    private static final class CHSupportedVersionsStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new CHSupportedVersionsSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHSupportedVersionsProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return null;
            }
            final int[] protocols = new int[chc.activeProtocols.size()];
            final int verLen = protocols.length * 2;
            final byte[] extData = new byte[verLen + 1];
            extData[0] = (byte)(verLen & 0xFF);
            int i = 0;
            int j = 1;
            for (final ProtocolVersion pv : chc.activeProtocols) {
                protocols[i++] = pv.id;
                extData[j++] = pv.major;
                extData[j++] = pv.minor;
            }
            chc.handshakeExtensions.put(SSLExtension.CH_SUPPORTED_VERSIONS, new CHSupportedVersionsSpec(protocols));
            return extData;
        }
    }
    
    private static final class CHSupportedVersionsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return;
            }
            CHSupportedVersionsSpec spec;
            try {
                spec = new CHSupportedVersionsSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            shc.handshakeExtensions.put(SSLExtension.CH_SUPPORTED_VERSIONS, spec);
        }
    }
    
    static final class SHSupportedVersionsSpec implements SSLExtension.SSLExtensionSpec
    {
        final int selectedVersion;
        
        private SHSupportedVersionsSpec(final ProtocolVersion selectedVersion) {
            this.selectedVersion = selectedVersion.id;
        }
        
        private SHSupportedVersionsSpec(final ByteBuffer m) throws IOException {
            if (m.remaining() != 2) {
                throw new SSLProtocolException("Invalid supported_versions: insufficient data");
            }
            final byte major = m.get();
            final byte minor = m.get();
            this.selectedVersion = ((major & 0xFF) << 8 | (minor & 0xFF));
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"selected version\": '['{0}']'", Locale.ENGLISH);
            final Object[] messageFields = { ProtocolVersion.nameOf(this.selectedVersion) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class SHSupportedVersionsStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new SHSupportedVersionsSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class SHSupportedVersionsProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final CHSupportedVersionsSpec svs = shc.handshakeExtensions.get(SSLExtension.CH_SUPPORTED_VERSIONS);
            if (svs == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore unavailable supported_versions extension", new Object[0]);
                }
                return null;
            }
            if (!shc.sslConfig.isAvailable(SSLExtension.SH_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.SH_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return null;
            }
            final byte[] extData = { shc.negotiatedProtocol.major, shc.negotiatedProtocol.minor };
            shc.handshakeExtensions.put(SSLExtension.SH_SUPPORTED_VERSIONS, new SHSupportedVersionsSpec(shc.negotiatedProtocol));
            return extData;
        }
    }
    
    private static final class SHSupportedVersionsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.SH_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.SH_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return;
            }
            SHSupportedVersionsSpec spec;
            try {
                spec = new SHSupportedVersionsSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            chc.handshakeExtensions.put(SSLExtension.SH_SUPPORTED_VERSIONS, spec);
        }
    }
    
    private static final class HRRSupportedVersionsProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.HRR_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.HRR_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return null;
            }
            final byte[] extData = { shc.negotiatedProtocol.major, shc.negotiatedProtocol.minor };
            shc.handshakeExtensions.put(SSLExtension.HRR_SUPPORTED_VERSIONS, new SHSupportedVersionsSpec(shc.negotiatedProtocol));
            return extData;
        }
    }
    
    private static final class HRRSupportedVersionsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.HRR_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.HRR_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return;
            }
            SHSupportedVersionsSpec spec;
            try {
                spec = new SHSupportedVersionsSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            chc.handshakeExtensions.put(SSLExtension.HRR_SUPPORTED_VERSIONS, spec);
        }
    }
    
    private static final class HRRSupportedVersionsReproducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.HRR_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("[Reproduce] Ignore unavailable extension: " + SSLExtension.HRR_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return null;
            }
            final byte[] extData = { shc.negotiatedProtocol.major, shc.negotiatedProtocol.minor };
            return extData;
        }
    }
}
