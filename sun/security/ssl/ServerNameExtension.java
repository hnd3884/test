package sun.security.ssl;

import javax.net.ssl.SNIMatcher;
import java.util.Objects;
import java.util.Iterator;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SNIHostName;
import java.util.LinkedHashMap;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import javax.net.ssl.SNIServerName;
import java.util.List;

final class ServerNameExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final SSLStringizer chStringizer;
    static final HandshakeProducer shNetworkProducer;
    static final SSLExtension.ExtensionConsumer shOnLoadConsumer;
    static final SSLStringizer shStringizer;
    static final HandshakeProducer eeNetworkProducer;
    static final SSLExtension.ExtensionConsumer eeOnLoadConsumer;
    
    static {
        chNetworkProducer = new CHServerNameProducer();
        chOnLoadConsumer = new CHServerNameConsumer();
        chStringizer = new CHServerNamesStringizer();
        shNetworkProducer = new SHServerNameProducer();
        shOnLoadConsumer = new SHServerNameConsumer();
        shStringizer = new SHServerNamesStringizer();
        eeNetworkProducer = new EEServerNameProducer();
        eeOnLoadConsumer = new EEServerNameConsumer();
    }
    
    static final class CHServerNamesSpec implements SSLExtension.SSLExtensionSpec
    {
        static final int NAME_HEADER_LENGTH = 3;
        final List<SNIServerName> serverNames;
        
        private CHServerNamesSpec(final List<SNIServerName> list) {
            this.serverNames = Collections.unmodifiableList((List<? extends SNIServerName>)new ArrayList<SNIServerName>(list));
        }
        
        private CHServerNamesSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid server_name extension: insufficient data");
            }
            final int int16 = Record.getInt16(byteBuffer);
            if (int16 == 0 || int16 != byteBuffer.remaining()) {
                throw new SSLProtocolException("Invalid server_name extension: incomplete data");
            }
            final LinkedHashMap linkedHashMap = new LinkedHashMap();
            while (byteBuffer.hasRemaining()) {
                final int int17 = Record.getInt8(byteBuffer);
                final byte[] bytes16 = Record.getBytes16(byteBuffer);
                SNIServerName sniServerName = null;
                Label_0267: {
                    if (int17 == 0) {
                        if (bytes16.length == 0) {
                            throw new SSLProtocolException("Empty HostName in server_name extension");
                        }
                        try {
                            sniServerName = new SNIHostName(bytes16);
                            break Label_0267;
                        }
                        catch (final IllegalArgumentException ex) {
                            throw (SSLProtocolException)new SSLProtocolException("Illegal server name, type=host_name(" + int17 + "), name=" + new String(bytes16, StandardCharsets.UTF_8) + ", value={" + Utilities.toHexString(bytes16) + "}").initCause(ex);
                        }
                    }
                    try {
                        sniServerName = new UnknownServerName(int17, bytes16);
                    }
                    catch (final IllegalArgumentException ex2) {
                        throw (SSLProtocolException)new SSLProtocolException("Illegal server name, type=(" + int17 + "), value={" + Utilities.toHexString(bytes16) + "}").initCause(ex2);
                    }
                }
                if (linkedHashMap.put(sniServerName.getType(), sniServerName) != null) {
                    throw new SSLProtocolException("Duplicated server name of type " + sniServerName.getType());
                }
            }
            this.serverNames = new ArrayList<SNIServerName>(linkedHashMap.values());
        }
        
        @Override
        public String toString() {
            if (this.serverNames == null || this.serverNames.isEmpty()) {
                return "<no server name indicator specified>";
            }
            final StringBuilder sb = new StringBuilder(512);
            final Iterator<SNIServerName> iterator = this.serverNames.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next().toString());
                sb.append("\n");
            }
            return sb.toString();
        }
        
        private static class UnknownServerName extends SNIServerName
        {
            UnknownServerName(final int n, final byte[] array) {
                super(n, array);
            }
        }
    }
    
    private static final class CHServerNamesStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new CHServerNamesSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHServerNameProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SERVER_NAME)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore unavailable server_name extension", new Object[0]);
                }
                return null;
            }
            List<SNIServerName> requestedServerNames;
            if (clientHandshakeContext.isResumption && clientHandshakeContext.resumingSession != null) {
                requestedServerNames = clientHandshakeContext.resumingSession.getRequestedServerNames();
            }
            else {
                requestedServerNames = clientHandshakeContext.sslConfig.serverNames;
            }
            if (requestedServerNames != null && !requestedServerNames.isEmpty()) {
                int n = 0;
                for (final SNIServerName sniServerName : requestedServerNames) {
                    n += 3;
                    n += sniServerName.getEncoded().length;
                }
                final byte[] array = new byte[n + 2];
                final ByteBuffer wrap = ByteBuffer.wrap(array);
                Record.putInt16(wrap, n);
                for (final SNIServerName sniServerName2 : requestedServerNames) {
                    Record.putInt8(wrap, sniServerName2.getType());
                    Record.putBytes16(wrap, sniServerName2.getEncoded());
                }
                clientHandshakeContext.requestedServerNames = requestedServerNames;
                clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_SERVER_NAME, new CHServerNamesSpec((List)requestedServerNames));
                return array;
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.warning("Unable to indicate server name", new Object[0]);
            }
            return null;
        }
    }
    
    private static final class CHServerNameConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_SERVER_NAME)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_SERVER_NAME.name, new Object[0]);
                }
                return;
            }
            CHServerNamesSpec chServerNamesSpec;
            try {
                chServerNamesSpec = new CHServerNamesSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_SERVER_NAME, chServerNamesSpec);
            SNIServerName chooseSni = null;
            if (!serverHandshakeContext.sslConfig.sniMatchers.isEmpty()) {
                chooseSni = chooseSni(serverHandshakeContext.sslConfig.sniMatchers, chServerNamesSpec.serverNames);
                if (chooseSni == null) {
                    throw serverHandshakeContext.conContext.fatal(Alert.UNRECOGNIZED_NAME, "Unrecognized server name indication");
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("server name indication (" + chooseSni + ") is accepted", new Object[0]);
                }
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("no server name matchers, ignore server name indication", new Object[0]);
            }
            if (serverHandshakeContext.isResumption && serverHandshakeContext.resumingSession != null && !Objects.equals(chooseSni, serverHandshakeContext.resumingSession.serverNameIndication)) {
                serverHandshakeContext.isResumption = false;
                serverHandshakeContext.resumingSession = null;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("abort session resumption, different server name indication used", new Object[0]);
                }
            }
            serverHandshakeContext.requestedServerNames = chServerNamesSpec.serverNames;
            serverHandshakeContext.negotiatedServerName = chooseSni;
        }
        
        private static SNIServerName chooseSni(final Collection<SNIMatcher> collection, final List<SNIServerName> list) {
            if (list != null && !list.isEmpty()) {
                for (final SNIMatcher sniMatcher : collection) {
                    final int type = sniMatcher.getType();
                    for (final SNIServerName sniServerName : list) {
                        if (sniServerName.getType() == type) {
                            if (sniMatcher.matches(sniServerName)) {
                                return sniServerName;
                            }
                            break;
                        }
                    }
                }
            }
            return null;
        }
    }
    
    static final class SHServerNamesSpec implements SSLExtension.SSLExtensionSpec
    {
        static final SHServerNamesSpec DEFAULT;
        
        private SHServerNamesSpec() {
        }
        
        private SHServerNamesSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() != 0) {
                throw new SSLProtocolException("Invalid ServerHello server_name extension: not empty");
            }
        }
        
        @Override
        public String toString() {
            return "<empty extension_data field>";
        }
        
        static {
            DEFAULT = new SHServerNamesSpec();
        }
    }
    
    private static final class SHServerNamesStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new SHServerNamesSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class SHServerNameProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_SERVER_NAME) == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable extension: " + SSLExtension.SH_SERVER_NAME.name, new Object[0]);
                }
                return null;
            }
            if (serverHandshakeContext.isResumption || serverHandshakeContext.negotiatedServerName == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("No expected server name indication response", new Object[0]);
                }
                return null;
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.SH_SERVER_NAME, SHServerNamesSpec.DEFAULT);
            return new byte[0];
        }
    }
    
    private static final class SHServerNameConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final CHServerNamesSpec chServerNamesSpec = clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_SERVER_NAME);
            if (chServerNamesSpec == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ServerHello server_name extension");
            }
            if (byteBuffer.remaining() != 0) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid ServerHello server_name extension");
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.SH_SERVER_NAME, SHServerNamesSpec.DEFAULT);
            clientHandshakeContext.negotiatedServerName = chServerNamesSpec.serverNames.get(0);
        }
    }
    
    private static final class EEServerNameProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_SERVER_NAME) == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable extension: " + SSLExtension.EE_SERVER_NAME.name, new Object[0]);
                }
                return null;
            }
            if (serverHandshakeContext.isResumption || serverHandshakeContext.negotiatedServerName == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("No expected server name indication response", new Object[0]);
                }
                return null;
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.EE_SERVER_NAME, SHServerNamesSpec.DEFAULT);
            return new byte[0];
        }
    }
    
    private static final class EEServerNameConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final CHServerNamesSpec chServerNamesSpec = clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_SERVER_NAME);
            if (chServerNamesSpec == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected EncryptedExtensions server_name extension");
            }
            if (byteBuffer.remaining() != 0) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid EncryptedExtensions server_name extension");
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.EE_SERVER_NAME, SHServerNamesSpec.DEFAULT);
            clientHandshakeContext.negotiatedServerName = chServerNamesSpec.serverNames.get(0);
        }
    }
}
