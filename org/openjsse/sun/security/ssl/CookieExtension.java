package org.openjsse.sun.security.ssl;

import org.openjsse.sun.security.util.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;

public class CookieExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeConsumer chOnTradeConsumer;
    static final HandshakeProducer hrrNetworkProducer;
    static final SSLExtension.ExtensionConsumer hrrOnLoadConsumer;
    static final HandshakeProducer hrrNetworkReproducer;
    static final CookieStringizer cookieStringizer;
    
    static {
        chNetworkProducer = new CHCookieProducer();
        chOnLoadConsumer = new CHCookieConsumer();
        chOnTradeConsumer = new CHCookieUpdate();
        hrrNetworkProducer = new HRRCookieProducer();
        hrrOnLoadConsumer = new HRRCookieConsumer();
        hrrNetworkReproducer = new HRRCookieReproducer();
        cookieStringizer = new CookieStringizer();
    }
    
    static class CookieSpec implements SSLExtension.SSLExtensionSpec
    {
        final byte[] cookie;
        
        private CookieSpec(final ByteBuffer m) throws IOException {
            if (m.remaining() < 3) {
                throw new SSLProtocolException("Invalid cookie extension: insufficient data");
            }
            this.cookie = Record.getBytes16(m);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"cookie\": '{'\n{0}\n'}',", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { Utilities.indent(hexEncoder.encode(this.cookie)) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class CookieStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new CookieSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHCookieProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_COOKIE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable cookie extension", new Object[0]);
                }
                return null;
            }
            final CookieSpec spec = chc.handshakeExtensions.get(SSLExtension.HRR_COOKIE);
            if (spec != null && spec.cookie != null && spec.cookie.length != 0) {
                final byte[] extData = new byte[spec.cookie.length + 2];
                final ByteBuffer m = ByteBuffer.wrap(extData);
                Record.putBytes16(m, spec.cookie);
                return extData;
            }
            return null;
        }
    }
    
    private static final class CHCookieConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_COOKIE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable cookie extension", new Object[0]);
                }
                return;
            }
            CookieSpec spec;
            try {
                spec = new CookieSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            shc.handshakeExtensions.put(SSLExtension.CH_COOKIE, spec);
        }
    }
    
    private static final class CHCookieUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            final CookieSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_COOKIE);
            if (spec == null) {
                return;
            }
            final HelloCookieManager hcm = shc.sslContext.getHelloCookieManager(shc.negotiatedProtocol);
            if (!hcm.isCookieValid(shc, clientHello, spec.cookie)) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "unrecognized cookie");
            }
        }
    }
    
    private static final class HRRCookieProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ServerHello.ServerHelloMessage hrrm = (ServerHello.ServerHelloMessage)message;
            if (!shc.sslConfig.isAvailable(SSLExtension.HRR_COOKIE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable cookie extension", new Object[0]);
                }
                return null;
            }
            final HelloCookieManager hcm = shc.sslContext.getHelloCookieManager(shc.negotiatedProtocol);
            final byte[] cookie = hcm.createCookie(shc, hrrm.clientHello);
            final byte[] extData = new byte[cookie.length + 2];
            final ByteBuffer m = ByteBuffer.wrap(extData);
            Record.putBytes16(m, cookie);
            return extData;
        }
    }
    
    private static final class HRRCookieConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.HRR_COOKIE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable cookie extension", new Object[0]);
                }
                return;
            }
            CookieSpec spec;
            try {
                spec = new CookieSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            chc.handshakeExtensions.put(SSLExtension.HRR_COOKIE, spec);
        }
    }
    
    private static final class HRRCookieReproducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.HRR_COOKIE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable cookie extension", new Object[0]);
                }
                return null;
            }
            final CookieSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_COOKIE);
            if (spec != null && spec.cookie != null && spec.cookie.length != 0) {
                final byte[] extData = new byte[spec.cookie.length + 2];
                final ByteBuffer m = ByteBuffer.wrap(extData);
                Record.putBytes16(m, spec.cookie);
                return extData;
            }
            return null;
        }
    }
}
