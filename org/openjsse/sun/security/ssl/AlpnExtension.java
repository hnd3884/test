package org.openjsse.sun.security.ssl;

import java.util.Collection;
import org.openjsse.javax.net.ssl.SSLSocket;
import org.openjsse.javax.net.ssl.SSLEngine;
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
        
        private AlpnSpec(final String[] applicationProtocols) {
            this.applicationProtocols = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])applicationProtocols));
        }
        
        private AlpnSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid application_layer_protocol_negotiation: insufficient data (length=" + buffer.remaining() + ")");
            }
            final int listLen = Record.getInt16(buffer);
            if (listLen < 2 || listLen != buffer.remaining()) {
                throw new SSLProtocolException("Invalid application_layer_protocol_negotiation: incorrect list length (length=" + listLen + ")");
            }
            final List<String> protocolNames = new LinkedList<String>();
            while (buffer.hasRemaining()) {
                final byte[] bytes = Record.getBytes8(buffer);
                if (bytes.length == 0) {
                    throw new SSLProtocolException("Invalid application_layer_protocol_negotiation extension: empty application protocol name");
                }
                final String appProtocol = new String(bytes, StandardCharsets.UTF_8);
                protocolNames.add(appProtocol);
            }
            this.applicationProtocols = Collections.unmodifiableList((List<? extends String>)protocolNames);
        }
        
        @Override
        public String toString() {
            return this.applicationProtocols.toString();
        }
    }
    
    private static final class AlpnStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new AlpnSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHAlpnProducer implements HandshakeProducer
    {
        static final int MAX_AP_LENGTH = 255;
        static final int MAX_AP_LIST_LENGTH = 65535;
        
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_ALPN)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.info("Ignore client unavailable extension: " + SSLExtension.CH_ALPN.name, new Object[0]);
                }
                chc.applicationProtocol = "";
                chc.conContext.applicationProtocol = "";
                return null;
            }
            final String[] laps = chc.sslConfig.applicationProtocols;
            if (laps == null || laps.length == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.info("No available application protocols", new Object[0]);
                }
                return null;
            }
            int listLength = 0;
            for (final String ap : laps) {
                final int length = ap.getBytes(StandardCharsets.UTF_8).length;
                if (length == 0) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.severe("Application protocol name cannot be empty", new Object[0]);
                    }
                    throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Application protocol name cannot be empty");
                }
                if (length > 255) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.severe("Application protocol name (" + ap + ") exceeds the size limit (" + 255 + " bytes)", new Object[0]);
                    }
                    throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Application protocol name (" + ap + ") exceeds the size limit (" + 255 + " bytes)");
                }
                listLength += length + 1;
                if (listLength > 65535) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.severe("The configured application protocols (" + Arrays.toString(laps) + ") exceed the size limit (" + 65535 + " bytes)", new Object[0]);
                    }
                    throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "The configured application protocols (" + Arrays.toString(laps) + ") exceed the size limit (" + 65535 + " bytes)");
                }
            }
            final byte[] extData = new byte[listLength + 2];
            final ByteBuffer m = ByteBuffer.wrap(extData);
            Record.putInt16(m, listLength);
            for (final String ap2 : laps) {
                Record.putBytes8(m, ap2.getBytes(StandardCharsets.UTF_8));
            }
            chc.handshakeExtensions.put(SSLExtension.CH_ALPN, new AlpnSpec(chc.sslConfig.applicationProtocols));
            return extData;
        }
    }
    
    private static final class CHAlpnConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_ALPN)) {
                shc.applicationProtocol = "";
                shc.conContext.applicationProtocol = "";
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.info("Ignore server unavailable extension: " + SSLExtension.CH_ALPN.name, new Object[0]);
                }
                return;
            }
            boolean noAPSelector;
            if (shc.conContext.transport instanceof SSLEngine) {
                noAPSelector = (shc.sslConfig.engineAPSelector == null);
            }
            else {
                noAPSelector = (shc.sslConfig.socketAPSelector == null);
            }
            final boolean noAlpnProtocols = shc.sslConfig.applicationProtocols == null || shc.sslConfig.applicationProtocols.length == 0;
            if (noAPSelector && noAlpnProtocols) {
                shc.applicationProtocol = "";
                shc.conContext.applicationProtocol = "";
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore server unenabled extension: " + SSLExtension.CH_ALPN.name, new Object[0]);
                }
                return;
            }
            AlpnSpec spec;
            try {
                spec = new AlpnSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (noAPSelector) {
                final List<String> protocolNames = spec.applicationProtocols;
                boolean matched = false;
                for (final String ap : shc.sslConfig.applicationProtocols) {
                    if (protocolNames.contains(ap)) {
                        shc.applicationProtocol = ap;
                        shc.conContext.applicationProtocol = ap;
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    throw shc.conContext.fatal(Alert.NO_APPLICATION_PROTOCOL, "No matching application layer protocol values");
                }
            }
            shc.handshakeExtensions.put(SSLExtension.CH_ALPN, spec);
        }
    }
    
    private static final class CHAlpnAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            shc.applicationProtocol = "";
            shc.conContext.applicationProtocol = "";
        }
    }
    
    private static final class SHAlpnProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final AlpnSpec requestedAlps = shc.handshakeExtensions.get(SSLExtension.CH_ALPN);
            if (requestedAlps == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.SH_ALPN.name, new Object[0]);
                }
                shc.applicationProtocol = "";
                shc.conContext.applicationProtocol = "";
                return null;
            }
            final List<String> alps = requestedAlps.applicationProtocols;
            if (shc.conContext.transport instanceof SSLEngine) {
                if (shc.sslConfig.engineAPSelector != null) {
                    final SSLEngine engine = (SSLEngine)shc.conContext.transport;
                    shc.applicationProtocol = shc.sslConfig.engineAPSelector.apply(engine, alps);
                    if (shc.applicationProtocol == null || (!shc.applicationProtocol.isEmpty() && !alps.contains(shc.applicationProtocol))) {
                        throw shc.conContext.fatal(Alert.NO_APPLICATION_PROTOCOL, "No matching application layer protocol values");
                    }
                }
            }
            else if (shc.sslConfig.socketAPSelector != null) {
                final SSLSocket socket = (SSLSocket)shc.conContext.transport;
                shc.applicationProtocol = shc.sslConfig.socketAPSelector.apply(socket, alps);
                if (shc.applicationProtocol == null || (!shc.applicationProtocol.isEmpty() && !alps.contains(shc.applicationProtocol))) {
                    throw shc.conContext.fatal(Alert.NO_APPLICATION_PROTOCOL, "No matching application layer protocol values");
                }
            }
            if (shc.applicationProtocol == null || shc.applicationProtocol.isEmpty()) {
                shc.applicationProtocol = "";
                shc.conContext.applicationProtocol = "";
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore, no negotiated application layer protocol", new Object[0]);
                }
                return null;
            }
            final int listLen = shc.applicationProtocol.length() + 1;
            final byte[] extData = new byte[listLen + 2];
            final ByteBuffer m = ByteBuffer.wrap(extData);
            Record.putInt16(m, listLen);
            Record.putBytes8(m, shc.applicationProtocol.getBytes(StandardCharsets.UTF_8));
            shc.conContext.applicationProtocol = shc.applicationProtocol;
            shc.handshakeExtensions.remove(SSLExtension.CH_ALPN);
            return extData;
        }
    }
    
    private static final class SHAlpnConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final AlpnSpec requestedAlps = chc.handshakeExtensions.get(SSLExtension.CH_ALPN);
            if (requestedAlps == null || requestedAlps.applicationProtocols == null || requestedAlps.applicationProtocols.isEmpty()) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected " + SSLExtension.CH_ALPN.name + " extension");
            }
            AlpnSpec spec;
            try {
                spec = new AlpnSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (spec.applicationProtocols.size() != 1) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid " + SSLExtension.CH_ALPN.name + " extension: Only one application protocol name is allowed in ServerHello message");
            }
            if (!requestedAlps.applicationProtocols.containsAll(spec.applicationProtocols)) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid " + SSLExtension.CH_ALPN.name + " extension: Only client specified application protocol is allowed in ServerHello message");
            }
            chc.applicationProtocol = spec.applicationProtocols.get(0);
            chc.conContext.applicationProtocol = chc.applicationProtocol;
            chc.handshakeExtensions.remove(SSLExtension.CH_ALPN);
        }
    }
    
    private static final class SHAlpnAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            chc.applicationProtocol = "";
            chc.conContext.applicationProtocol = "";
        }
    }
}
