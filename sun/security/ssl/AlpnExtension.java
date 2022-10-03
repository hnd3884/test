package sun.security.ssl;

import java.util.Collection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

final class AlpnExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeAbsence chOnLoadAbsence;
    static final HandshakeProducer shNetworkProducer;
    static final SSLExtension.ExtensionConsumer shOnLoadConsumer;
    static final HandshakeAbsence shOnLoadAbsence;
    static final HandshakeProducer eeNetworkProducer;
    static final SSLExtension.ExtensionConsumer eeOnLoadConsumer;
    static final HandshakeAbsence eeOnLoadAbsence;
    static final SSLStringizer alpnStringizer;
    
    static {
        chNetworkProducer = new CHAlpnProducer();
        chOnLoadConsumer = new CHAlpnConsumer();
        chOnLoadAbsence = new CHAlpnAbsence();
        shNetworkProducer = new SHAlpnProducer();
        shOnLoadConsumer = new SHAlpnConsumer();
        shOnLoadAbsence = new SHAlpnAbsence();
        eeNetworkProducer = new SHAlpnProducer();
        eeOnLoadConsumer = new SHAlpnConsumer();
        eeOnLoadAbsence = new SHAlpnAbsence();
        alpnStringizer = new AlpnStringizer();
    }
    
    static final class AlpnSpec implements SSLExtension.SSLExtensionSpec
    {
        final List<String> applicationProtocols;
        
        private AlpnSpec(final String[] array) {
            this.applicationProtocols = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])array));
        }
        
        private AlpnSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid application_layer_protocol_negotiation: insufficient data (length=" + byteBuffer.remaining() + ")");
            }
            final int int16 = Record.getInt16(byteBuffer);
            if (int16 < 2 || int16 != byteBuffer.remaining()) {
                throw new SSLProtocolException("Invalid application_layer_protocol_negotiation: incorrect list length (length=" + int16 + ")");
            }
            final LinkedList list = new LinkedList();
            while (byteBuffer.hasRemaining()) {
                final byte[] bytes8 = Record.getBytes8(byteBuffer);
                if (bytes8.length == 0) {
                    throw new SSLProtocolException("Invalid application_layer_protocol_negotiation extension: empty application protocol name");
                }
                list.add(new String(bytes8, StandardCharsets.UTF_8));
            }
            this.applicationProtocols = (List<String>)Collections.unmodifiableList((List<?>)list);
        }
        
        @Override
        public String toString() {
            return this.applicationProtocols.toString();
        }
    }
    
    private static final class AlpnStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new AlpnSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHAlpnProducer implements HandshakeProducer
    {
        static final int MAX_AP_LENGTH = 255;
        static final int MAX_AP_LIST_LENGTH = 65535;
        
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_ALPN)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.info("Ignore client unavailable extension: " + SSLExtension.CH_ALPN.name, new Object[0]);
                }
                clientHandshakeContext.applicationProtocol = "";
                clientHandshakeContext.conContext.applicationProtocol = "";
                return null;
            }
            final String[] applicationProtocols = clientHandshakeContext.sslConfig.applicationProtocols;
            if (applicationProtocols == null || applicationProtocols.length == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.info("No available application protocols", new Object[0]);
                }
                return null;
            }
            int n = 0;
            for (final String s : applicationProtocols) {
                final int length2 = s.getBytes(StandardCharsets.UTF_8).length;
                if (length2 == 0) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.severe("Application protocol name cannot be empty", new Object[0]);
                    }
                    throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Application protocol name cannot be empty");
                }
                if (length2 > 255) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.severe("Application protocol name (" + s + ") exceeds the size limit (" + 255 + " bytes)", new Object[0]);
                    }
                    throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Application protocol name (" + s + ") exceeds the size limit (" + 255 + " bytes)");
                }
                n += length2 + 1;
                if (n > 65535) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.severe("The configured application protocols (" + Arrays.toString(applicationProtocols) + ") exceed the size limit (" + 65535 + " bytes)", new Object[0]);
                    }
                    throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "The configured application protocols (" + Arrays.toString(applicationProtocols) + ") exceed the size limit (" + 65535 + " bytes)");
                }
            }
            final byte[] array2 = new byte[n + 2];
            final ByteBuffer wrap = ByteBuffer.wrap(array2);
            Record.putInt16(wrap, n);
            final String[] array3 = applicationProtocols;
            for (int length3 = array3.length, j = 0; j < length3; ++j) {
                Record.putBytes8(wrap, array3[j].getBytes(StandardCharsets.UTF_8));
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_ALPN, new AlpnSpec(clientHandshakeContext.sslConfig.applicationProtocols));
            return array2;
        }
    }
    
    private static final class CHAlpnConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_ALPN)) {
                serverHandshakeContext.applicationProtocol = "";
                serverHandshakeContext.conContext.applicationProtocol = "";
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.info("Ignore server unavailable extension: " + SSLExtension.CH_ALPN.name, new Object[0]);
                }
                return;
            }
            boolean b;
            if (serverHandshakeContext.conContext.transport instanceof SSLEngine) {
                b = (serverHandshakeContext.sslConfig.engineAPSelector == null);
            }
            else {
                b = (serverHandshakeContext.sslConfig.socketAPSelector == null);
            }
            final boolean b2 = serverHandshakeContext.sslConfig.applicationProtocols == null || serverHandshakeContext.sslConfig.applicationProtocols.length == 0;
            if (b && b2) {
                serverHandshakeContext.applicationProtocol = "";
                serverHandshakeContext.conContext.applicationProtocol = "";
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore server unenabled extension: " + SSLExtension.CH_ALPN.name, new Object[0]);
                }
                return;
            }
            AlpnSpec alpnSpec;
            try {
                alpnSpec = new AlpnSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (b) {
                final List<String> applicationProtocols = alpnSpec.applicationProtocols;
                boolean b3 = false;
                for (final String s : serverHandshakeContext.sslConfig.applicationProtocols) {
                    if (applicationProtocols.contains(s)) {
                        serverHandshakeContext.applicationProtocol = s;
                        serverHandshakeContext.conContext.applicationProtocol = s;
                        b3 = true;
                        break;
                    }
                }
                if (!b3) {
                    throw serverHandshakeContext.conContext.fatal(Alert.NO_APPLICATION_PROTOCOL, "No matching application layer protocol values");
                }
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_ALPN, alpnSpec);
        }
    }
    
    private static final class CHAlpnAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            serverHandshakeContext.applicationProtocol = "";
            serverHandshakeContext.conContext.applicationProtocol = "";
        }
    }
    
    private static final class SHAlpnProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final AlpnSpec alpnSpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_ALPN);
            if (alpnSpec == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.SH_ALPN.name, new Object[0]);
                }
                serverHandshakeContext.applicationProtocol = "";
                serverHandshakeContext.conContext.applicationProtocol = "";
                return null;
            }
            final List<String> applicationProtocols = alpnSpec.applicationProtocols;
            if (serverHandshakeContext.conContext.transport instanceof SSLEngine) {
                if (serverHandshakeContext.sslConfig.engineAPSelector != null) {
                    serverHandshakeContext.applicationProtocol = serverHandshakeContext.sslConfig.engineAPSelector.apply((SSLEngine)serverHandshakeContext.conContext.transport, applicationProtocols);
                    if (serverHandshakeContext.applicationProtocol == null || (!serverHandshakeContext.applicationProtocol.isEmpty() && !applicationProtocols.contains(serverHandshakeContext.applicationProtocol))) {
                        throw serverHandshakeContext.conContext.fatal(Alert.NO_APPLICATION_PROTOCOL, "No matching application layer protocol values");
                    }
                }
            }
            else if (serverHandshakeContext.sslConfig.socketAPSelector != null) {
                serverHandshakeContext.applicationProtocol = serverHandshakeContext.sslConfig.socketAPSelector.apply((SSLSocket)serverHandshakeContext.conContext.transport, applicationProtocols);
                if (serverHandshakeContext.applicationProtocol == null || (!serverHandshakeContext.applicationProtocol.isEmpty() && !applicationProtocols.contains(serverHandshakeContext.applicationProtocol))) {
                    throw serverHandshakeContext.conContext.fatal(Alert.NO_APPLICATION_PROTOCOL, "No matching application layer protocol values");
                }
            }
            if (serverHandshakeContext.applicationProtocol == null || serverHandshakeContext.applicationProtocol.isEmpty()) {
                serverHandshakeContext.applicationProtocol = "";
                serverHandshakeContext.conContext.applicationProtocol = "";
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore, no negotiated application layer protocol", new Object[0]);
                }
                return null;
            }
            final int n = serverHandshakeContext.applicationProtocol.length() + 1;
            final byte[] array = new byte[n + 2];
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            Record.putInt16(wrap, n);
            Record.putBytes8(wrap, serverHandshakeContext.applicationProtocol.getBytes(StandardCharsets.UTF_8));
            serverHandshakeContext.conContext.applicationProtocol = serverHandshakeContext.applicationProtocol;
            serverHandshakeContext.handshakeExtensions.remove(SSLExtension.CH_ALPN);
            return array;
        }
    }
    
    private static final class SHAlpnConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final AlpnSpec alpnSpec = clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_ALPN);
            if (alpnSpec == null || alpnSpec.applicationProtocols == null || alpnSpec.applicationProtocols.isEmpty()) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected " + SSLExtension.CH_ALPN.name + " extension");
            }
            AlpnSpec alpnSpec2;
            try {
                alpnSpec2 = new AlpnSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (alpnSpec2.applicationProtocols.size() != 1) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid " + SSLExtension.CH_ALPN.name + " extension: Only one application protocol name is allowed in ServerHello message");
            }
            if (!alpnSpec.applicationProtocols.containsAll(alpnSpec2.applicationProtocols)) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid " + SSLExtension.CH_ALPN.name + " extension: Only client specified application protocol is allowed in ServerHello message");
            }
            clientHandshakeContext.applicationProtocol = alpnSpec2.applicationProtocols.get(0);
            clientHandshakeContext.conContext.applicationProtocol = clientHandshakeContext.applicationProtocol;
            clientHandshakeContext.handshakeExtensions.remove(SSLExtension.CH_ALPN);
        }
    }
    
    private static final class SHAlpnAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            clientHandshakeContext.applicationProtocol = "";
            clientHandshakeContext.conContext.applicationProtocol = "";
        }
    }
}
