package org.openjsse.sun.security.ssl;

import javax.net.ssl.SNIMatcher;
import java.util.Objects;
import java.util.Iterator;
import java.io.IOException;
import java.util.Map;
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
        
        private CHServerNamesSpec(final List<SNIServerName> serverNames) {
            this.serverNames = Collections.unmodifiableList((List<? extends SNIServerName>)new ArrayList<SNIServerName>(serverNames));
        }
        
        private CHServerNamesSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid server_name extension: insufficient data");
            }
            final int sniLen = Record.getInt16(buffer);
            if (sniLen == 0 || sniLen != buffer.remaining()) {
                throw new SSLProtocolException("Invalid server_name extension: incomplete data");
            }
            final Map<Integer, SNIServerName> sniMap = new LinkedHashMap<Integer, SNIServerName>();
            while (buffer.hasRemaining()) {
                final int nameType = Record.getInt8(buffer);
                final byte[] encoded = Record.getBytes16(buffer);
                SNIServerName serverName = null;
                Label_0267: {
                    if (nameType == 0) {
                        if (encoded.length == 0) {
                            throw new SSLProtocolException("Empty HostName in server_name extension");
                        }
                        try {
                            serverName = new SNIHostName(encoded);
                            break Label_0267;
                        }
                        catch (final IllegalArgumentException iae) {
                            final SSLProtocolException spe = new SSLProtocolException("Illegal server name, type=host_name(" + nameType + "), name=" + new String(encoded, StandardCharsets.UTF_8) + ", value={" + Utilities.toHexString(encoded) + "}");
                            throw (SSLProtocolException)spe.initCause(iae);
                        }
                    }
                    try {
                        serverName = new UnknownServerName(nameType, encoded);
                    }
                    catch (final IllegalArgumentException iae) {
                        final SSLProtocolException spe = new SSLProtocolException("Illegal server name, type=(" + nameType + "), value={" + Utilities.toHexString(encoded) + "}");
                        throw (SSLProtocolException)spe.initCause(iae);
                    }
                }
                if (sniMap.put(serverName.getType(), serverName) != null) {
                    throw new SSLProtocolException("Duplicated server name of type " + serverName.getType());
                }
            }
            this.serverNames = new ArrayList<SNIServerName>(sniMap.values());
        }
        
        @Override
        public String toString() {
            if (this.serverNames == null || this.serverNames.isEmpty()) {
                return "<no server name indicator specified>";
            }
            final StringBuilder builder = new StringBuilder(512);
            for (final SNIServerName sn : this.serverNames) {
                builder.append(sn.toString());
                builder.append("\n");
            }
            return builder.toString();
        }
        
        private static class UnknownServerName extends SNIServerName
        {
            UnknownServerName(final int code, final byte[] encoded) {
                super(code, encoded);
            }
        }
    }
    
    private static final class CHServerNamesStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new CHServerNamesSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHServerNameProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_SERVER_NAME)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore unavailable server_name extension", new Object[0]);
                }
                return null;
            }
            List<SNIServerName> serverNames;
            if (chc.isResumption && chc.resumingSession != null) {
                serverNames = chc.resumingSession.getRequestedServerNames();
            }
            else {
                serverNames = chc.sslConfig.serverNames;
            }
            if (serverNames != null && !serverNames.isEmpty()) {
                int sniLen = 0;
                for (final SNIServerName sniName : serverNames) {
                    sniLen += 3;
                    sniLen += sniName.getEncoded().length;
                }
                final byte[] extData = new byte[sniLen + 2];
                final ByteBuffer m = ByteBuffer.wrap(extData);
                Record.putInt16(m, sniLen);
                for (final SNIServerName sniName2 : serverNames) {
                    Record.putInt8(m, sniName2.getType());
                    Record.putBytes16(m, sniName2.getEncoded());
                }
                chc.requestedServerNames = serverNames;
                chc.handshakeExtensions.put(SSLExtension.CH_SERVER_NAME, new CHServerNamesSpec((List)serverNames));
                return extData;
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
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_SERVER_NAME)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_SERVER_NAME.name, new Object[0]);
                }
                return;
            }
            CHServerNamesSpec spec;
            try {
                spec = new CHServerNamesSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            shc.handshakeExtensions.put(SSLExtension.CH_SERVER_NAME, spec);
            SNIServerName sni = null;
            if (!shc.sslConfig.sniMatchers.isEmpty()) {
                sni = chooseSni(shc.sslConfig.sniMatchers, spec.serverNames);
                if (sni == null) {
                    throw shc.conContext.fatal(Alert.UNRECOGNIZED_NAME, "Unrecognized server name indication");
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("server name indication (" + sni + ") is accepted", new Object[0]);
                }
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("no server name matchers, ignore server name indication", new Object[0]);
            }
            if (shc.isResumption && shc.resumingSession != null && !Objects.equals(sni, shc.resumingSession.serverNameIndication)) {
                shc.isResumption = false;
                shc.resumingSession = null;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("abort session resumption, different server name indication used", new Object[0]);
                }
            }
            shc.requestedServerNames = spec.serverNames;
            shc.negotiatedServerName = sni;
        }
        
        private static SNIServerName chooseSni(final Collection<SNIMatcher> matchers, final List<SNIServerName> sniNames) {
            if (sniNames != null && !sniNames.isEmpty()) {
                for (final SNIMatcher matcher : matchers) {
                    final int matcherType = matcher.getType();
                    for (final SNIServerName sniName : sniNames) {
                        if (sniName.getType() == matcherType) {
                            if (matcher.matches(sniName)) {
                                return sniName;
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
        
        private SHServerNamesSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() != 0) {
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
        public String toString(final ByteBuffer buffer) {
            try {
                return new SHServerNamesSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class SHServerNameProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final CHServerNamesSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_SERVER_NAME);
            if (spec == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable extension: " + SSLExtension.SH_SERVER_NAME.name, new Object[0]);
                }
                return null;
            }
            if (shc.isResumption || shc.negotiatedServerName == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("No expected server name indication response", new Object[0]);
                }
                return null;
            }
            shc.handshakeExtensions.put(SSLExtension.SH_SERVER_NAME, SHServerNamesSpec.DEFAULT);
            return new byte[0];
        }
    }
    
    private static final class SHServerNameConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final CHServerNamesSpec spec = chc.handshakeExtensions.get(SSLExtension.CH_SERVER_NAME);
            if (spec == null) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ServerHello server_name extension");
            }
            if (buffer.remaining() != 0) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid ServerHello server_name extension");
            }
            chc.handshakeExtensions.put(SSLExtension.SH_SERVER_NAME, SHServerNamesSpec.DEFAULT);
            chc.negotiatedServerName = spec.serverNames.get(0);
        }
    }
    
    private static final class EEServerNameProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final CHServerNamesSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_SERVER_NAME);
            if (spec == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable extension: " + SSLExtension.EE_SERVER_NAME.name, new Object[0]);
                }
                return null;
            }
            if (shc.isResumption || shc.negotiatedServerName == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("No expected server name indication response", new Object[0]);
                }
                return null;
            }
            shc.handshakeExtensions.put(SSLExtension.EE_SERVER_NAME, SHServerNamesSpec.DEFAULT);
            return new byte[0];
        }
    }
    
    private static final class EEServerNameConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final CHServerNamesSpec spec = chc.handshakeExtensions.get(SSLExtension.CH_SERVER_NAME);
            if (spec == null) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected EncryptedExtensions server_name extension");
            }
            if (buffer.remaining() != 0) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid EncryptedExtensions server_name extension");
            }
            chc.handshakeExtensions.put(SSLExtension.EE_SERVER_NAME, SHServerNamesSpec.DEFAULT);
            chc.negotiatedServerName = spec.serverNames.get(0);
        }
    }
}
