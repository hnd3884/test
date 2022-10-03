package sun.security.ssl;

import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;

final class ECPointFormatsExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final SSLExtension.ExtensionConsumer shOnLoadConsumer;
    static final SSLStringizer epfStringizer;
    
    static {
        chNetworkProducer = new CHECPointFormatsProducer();
        chOnLoadConsumer = new CHECPointFormatsConsumer();
        shOnLoadConsumer = new SHECPointFormatsConsumer();
        epfStringizer = new ECPointFormatsStringizer();
    }
    
    static class ECPointFormatsSpec implements SSLExtension.SSLExtensionSpec
    {
        static final ECPointFormatsSpec DEFAULT;
        final byte[] formats;
        
        ECPointFormatsSpec(final byte[] formats) {
            this.formats = formats;
        }
        
        private ECPointFormatsSpec(final ByteBuffer byteBuffer) throws IOException {
            if (!byteBuffer.hasRemaining()) {
                throw new SSLProtocolException("Invalid ec_point_formats extension: insufficient data");
            }
            this.formats = Record.getBytes8(byteBuffer);
        }
        
        private boolean hasUncompressedFormat() {
            final byte[] formats = this.formats;
            for (int length = formats.length, i = 0; i < length; ++i) {
                if (formats[i] == ECPointFormat.UNCOMPRESSED.id) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"formats\": '['{0}']'", Locale.ENGLISH);
            if (this.formats == null || this.formats.length == 0) {
                return messageFormat.format(new Object[] { "<no EC point format specified>" });
            }
            final StringBuilder sb = new StringBuilder(512);
            int n = 1;
            for (final byte b : this.formats) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append(", ");
                }
                sb.append(ECPointFormat.nameOf(b));
            }
            return messageFormat.format(new Object[] { sb.toString() });
        }
        
        static {
            DEFAULT = new ECPointFormatsSpec(new byte[] { ECPointFormat.UNCOMPRESSED.id });
        }
    }
    
    private static final class ECPointFormatsStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new ECPointFormatsSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private enum ECPointFormat
    {
        UNCOMPRESSED((byte)0, "uncompressed"), 
        ANSIX962_COMPRESSED_PRIME((byte)1, "ansiX962_compressed_prime"), 
        FMT_ANSIX962_COMPRESSED_CHAR2((byte)2, "ansiX962_compressed_char2");
        
        final byte id;
        final String name;
        
        private ECPointFormat(final byte id, final String name) {
            this.id = id;
            this.name = name;
        }
        
        static String nameOf(final int n) {
            for (final ECPointFormat ecPointFormat : values()) {
                if (ecPointFormat.id == n) {
                    return ecPointFormat.name;
                }
            }
            return "UNDEFINED-EC-POINT-FORMAT(" + n + ")";
        }
    }
    
    private static final class CHECPointFormatsProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_EC_POINT_FORMATS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable ec_point_formats extension", new Object[0]);
                }
                return null;
            }
            if (SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE.isSupported(clientHandshakeContext.activeCipherSuites)) {
                final byte[] array = { 1, 0 };
                clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_EC_POINT_FORMATS, ECPointFormatsSpec.DEFAULT);
                return array;
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Need no ec_point_formats extension", new Object[0]);
            }
            return null;
        }
    }
    
    private static final class CHECPointFormatsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_EC_POINT_FORMATS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable ec_point_formats extension", new Object[0]);
                }
                return;
            }
            ECPointFormatsSpec ecPointFormatsSpec;
            try {
                ecPointFormatsSpec = new ECPointFormatsSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (!ecPointFormatsSpec.hasUncompressedFormat()) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid ec_point_formats extension data: peer does not support uncompressed points");
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_EC_POINT_FORMATS, ecPointFormatsSpec);
        }
    }
    
    private static final class SHECPointFormatsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_EC_POINT_FORMATS) == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ec_point_formats extension in ServerHello");
            }
            ECPointFormatsSpec ecPointFormatsSpec;
            try {
                ecPointFormatsSpec = new ECPointFormatsSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (!ecPointFormatsSpec.hasUncompressedFormat()) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid ec_point_formats extension data: peer does not support uncompressed points");
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_EC_POINT_FORMATS, ecPointFormatsSpec);
        }
    }
}
