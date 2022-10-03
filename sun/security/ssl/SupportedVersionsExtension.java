package sun.security.ssl;

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
        
        private CHSupportedVersionsSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 3) {
                throw new SSLProtocolException("Invalid supported_versions extension: insufficient data");
            }
            final byte[] bytes8 = Record.getBytes8(byteBuffer);
            if (byteBuffer.hasRemaining()) {
                throw new SSLProtocolException("Invalid supported_versions extension: unknown extra data");
            }
            if (bytes8 == null || bytes8.length == 0 || (bytes8.length & 0x1) != 0x0) {
                throw new SSLProtocolException("Invalid supported_versions extension: incomplete data");
            }
            final int[] requestedProtocols = new int[bytes8.length >> 1];
            for (int i = 0, n = 0; i < bytes8.length; requestedProtocols[n++] = ((bytes8[i++] & 0xFF) << 8 | (bytes8[i++] & 0xFF))) {}
            this.requestedProtocols = requestedProtocols;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"versions\": '['{0}']'", Locale.ENGLISH);
            if (this.requestedProtocols == null || this.requestedProtocols.length == 0) {
                return messageFormat.format(new Object[] { "<no supported version specified>" });
            }
            final StringBuilder sb = new StringBuilder(512);
            int n = 1;
            for (final int n2 : this.requestedProtocols) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append(", ");
                }
                sb.append(ProtocolVersion.nameOf(n2));
            }
            return messageFormat.format(new Object[] { sb.toString() });
        }
    }
    
    private static final class CHSupportedVersionsStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new CHSupportedVersionsSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHSupportedVersionsProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return null;
            }
            final int[] array = new int[clientHandshakeContext.activeProtocols.size()];
            final int n = array.length * 2;
            final byte[] array2 = new byte[n + 1];
            array2[0] = (byte)(n & 0xFF);
            int n2 = 0;
            int n3 = 1;
            for (final ProtocolVersion protocolVersion : clientHandshakeContext.activeProtocols) {
                array[n2++] = protocolVersion.id;
                array2[n3++] = protocolVersion.major;
                array2[n3++] = protocolVersion.minor;
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_SUPPORTED_VERSIONS, new CHSupportedVersionsSpec(array));
            return array2;
        }
    }
    
    private static final class CHSupportedVersionsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return;
            }
            CHSupportedVersionsSpec chSupportedVersionsSpec;
            try {
                chSupportedVersionsSpec = new CHSupportedVersionsSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_SUPPORTED_VERSIONS, chSupportedVersionsSpec);
        }
    }
    
    static final class SHSupportedVersionsSpec implements SSLExtension.SSLExtensionSpec
    {
        final int selectedVersion;
        
        private SHSupportedVersionsSpec(final ProtocolVersion protocolVersion) {
            this.selectedVersion = protocolVersion.id;
        }
        
        private SHSupportedVersionsSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() != 2) {
                throw new SSLProtocolException("Invalid supported_versions: insufficient data");
            }
            this.selectedVersion = ((byteBuffer.get() & 0xFF) << 8 | (byteBuffer.get() & 0xFF));
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"selected version\": '['{0}']'", Locale.ENGLISH).format(new Object[] { ProtocolVersion.nameOf(this.selectedVersion) });
        }
    }
    
    private static final class SHSupportedVersionsStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new SHSupportedVersionsSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class SHSupportedVersionsProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_SUPPORTED_VERSIONS) == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore unavailable supported_versions extension", new Object[0]);
                }
                return null;
            }
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.SH_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.SH_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return null;
            }
            final byte[] array = { serverHandshakeContext.negotiatedProtocol.major, serverHandshakeContext.negotiatedProtocol.minor };
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.SH_SUPPORTED_VERSIONS, new SHSupportedVersionsSpec(serverHandshakeContext.negotiatedProtocol));
            return array;
        }
    }
    
    private static final class SHSupportedVersionsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.SH_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.SH_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return;
            }
            SHSupportedVersionsSpec shSupportedVersionsSpec;
            try {
                shSupportedVersionsSpec = new SHSupportedVersionsSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.SH_SUPPORTED_VERSIONS, shSupportedVersionsSpec);
        }
    }
    
    private static final class HRRSupportedVersionsProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.HRR_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.HRR_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return null;
            }
            final byte[] array = { serverHandshakeContext.negotiatedProtocol.major, serverHandshakeContext.negotiatedProtocol.minor };
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.HRR_SUPPORTED_VERSIONS, new SHSupportedVersionsSpec(serverHandshakeContext.negotiatedProtocol));
            return array;
        }
    }
    
    private static final class HRRSupportedVersionsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.HRR_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.HRR_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return;
            }
            SHSupportedVersionsSpec shSupportedVersionsSpec;
            try {
                shSupportedVersionsSpec = new SHSupportedVersionsSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.HRR_SUPPORTED_VERSIONS, shSupportedVersionsSpec);
        }
    }
    
    private static final class HRRSupportedVersionsReproducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.HRR_SUPPORTED_VERSIONS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("[Reproduce] Ignore unavailable extension: " + SSLExtension.HRR_SUPPORTED_VERSIONS.name, new Object[0]);
                }
                return null;
            }
            return new byte[] { serverHandshakeContext.negotiatedProtocol.major, serverHandshakeContext.negotiatedProtocol.minor };
        }
    }
}
