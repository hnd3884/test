package org.openjsse.sun.security.ssl;

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
        
        private ECPointFormatsSpec(final ByteBuffer m) throws IOException {
            if (!m.hasRemaining()) {
                throw new SSLProtocolException("Invalid ec_point_formats extension: insufficient data");
            }
            this.formats = Record.getBytes8(m);
        }
        
        private boolean hasUncompressedFormat() {
            for (final byte format : this.formats) {
                if (format == ECPointFormat.UNCOMPRESSED.id) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"formats\": '['{0}']'", Locale.ENGLISH);
            if (this.formats == null || this.formats.length == 0) {
                final Object[] messageFields = { "<no EC point format specified>" };
                return messageFormat.format(messageFields);
            }
            final StringBuilder builder = new StringBuilder(512);
            boolean isFirst = true;
            for (final byte pf : this.formats) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    builder.append(", ");
                }
                builder.append(ECPointFormat.nameOf(pf));
            }
            final Object[] messageFields2 = { builder.toString() };
            return messageFormat.format(messageFields2);
        }
        
        static {
            DEFAULT = new ECPointFormatsSpec(new byte[] { ECPointFormat.UNCOMPRESSED.id });
        }
    }
    
    private static final class ECPointFormatsStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new ECPointFormatsSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
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
        
        static String nameOf(final int id) {
            for (final ECPointFormat pf : values()) {
                if (pf.id == id) {
                    return pf.name;
                }
            }
            return "UNDEFINED-EC-POINT-FORMAT(" + id + ")";
        }
    }
    
    private static final class CHECPointFormatsProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_EC_POINT_FORMATS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable ec_point_formats extension", new Object[0]);
                }
                return null;
            }
            if (SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE.isSupported(chc.activeCipherSuites)) {
                final byte[] extData = { 1, 0 };
                chc.handshakeExtensions.put(SSLExtension.CH_EC_POINT_FORMATS, ECPointFormatsSpec.DEFAULT);
                return extData;
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
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_EC_POINT_FORMATS)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable ec_point_formats extension", new Object[0]);
                }
                return;
            }
            ECPointFormatsSpec spec;
            try {
                spec = new ECPointFormatsSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (!spec.hasUncompressedFormat()) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid ec_point_formats extension data: peer does not support uncompressed points");
            }
            shc.handshakeExtensions.put(SSLExtension.CH_EC_POINT_FORMATS, spec);
        }
    }
    
    private static final class SHECPointFormatsConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final ECPointFormatsSpec requestedSpec = chc.handshakeExtensions.get(SSLExtension.CH_EC_POINT_FORMATS);
            if (requestedSpec == null) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ec_point_formats extension in ServerHello");
            }
            ECPointFormatsSpec spec;
            try {
                spec = new ECPointFormatsSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (!spec.hasUncompressedFormat()) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid ec_point_formats extension data: peer does not support uncompressed points");
            }
            chc.handshakeExtensions.put(SSLExtension.CH_EC_POINT_FORMATS, spec);
        }
    }
}
