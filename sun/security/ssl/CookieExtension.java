package sun.security.ssl;

import sun.misc.HexDumpEncoder;
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
        
        private CookieSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 3) {
                throw new SSLProtocolException("Invalid cookie extension: insufficient data");
            }
            this.cookie = Record.getBytes16(byteBuffer);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"cookie\": '{'\n{0}\n'}',", Locale.ENGLISH).format(new Object[] { Utilities.indent(new HexDumpEncoder().encode(this.cookie)) });
        }
    }
    
    private static final class CookieStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new CookieSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHCookieProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_COOKIE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable cookie extension", new Object[0]);
                }
                return null;
            }
            final CookieSpec cookieSpec = clientHandshakeContext.handshakeExtensions.get(SSLExtension.HRR_COOKIE);
            if (cookieSpec != null && cookieSpec.cookie != null && cookieSpec.cookie.length != 0) {
                final byte[] array = new byte[cookieSpec.cookie.length + 2];
                Record.putBytes16(ByteBuffer.wrap(array), cookieSpec.cookie);
                return array;
            }
            return null;
        }
    }
    
    private static final class CHCookieConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_COOKIE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable cookie extension", new Object[0]);
                }
                return;
            }
            CookieSpec cookieSpec;
            try {
                cookieSpec = new CookieSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_COOKIE, cookieSpec);
        }
    }
    
    private static final class CHCookieUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ClientHello.ClientHelloMessage clientHelloMessage = (ClientHello.ClientHelloMessage)handshakeMessage;
            final CookieSpec cookieSpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_COOKIE);
            if (cookieSpec == null) {
                return;
            }
            if (!serverHandshakeContext.sslContext.getHelloCookieManager(serverHandshakeContext.negotiatedProtocol).isCookieValid(serverHandshakeContext, clientHelloMessage, cookieSpec.cookie)) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "unrecognized cookie");
            }
        }
    }
    
    private static final class HRRCookieProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ServerHello.ServerHelloMessage serverHelloMessage = (ServerHello.ServerHelloMessage)handshakeMessage;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.HRR_COOKIE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable cookie extension", new Object[0]);
                }
                return null;
            }
            final byte[] cookie = serverHandshakeContext.sslContext.getHelloCookieManager(serverHandshakeContext.negotiatedProtocol).createCookie(serverHandshakeContext, serverHelloMessage.clientHello);
            final byte[] array = new byte[cookie.length + 2];
            Record.putBytes16(ByteBuffer.wrap(array), cookie);
            return array;
        }
    }
    
    private static final class HRRCookieConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.HRR_COOKIE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable cookie extension", new Object[0]);
                }
                return;
            }
            CookieSpec cookieSpec;
            try {
                cookieSpec = new CookieSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.HRR_COOKIE, cookieSpec);
        }
    }
    
    private static final class HRRCookieReproducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.HRR_COOKIE)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable cookie extension", new Object[0]);
                }
                return null;
            }
            final CookieSpec cookieSpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_COOKIE);
            if (cookieSpec != null && cookieSpec.cookie != null && cookieSpec.cookie.length != 0) {
                final byte[] array = new byte[cookieSpec.cookie.length + 2];
                Record.putBytes16(ByteBuffer.wrap(array), cookieSpec.cookie);
                return array;
            }
            return null;
        }
    }
}
