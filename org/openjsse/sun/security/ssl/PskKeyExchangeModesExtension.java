package org.openjsse.sun.security.ssl;

import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;

final class PskKeyExchangeModesExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeAbsence chOnLoadAbsence;
    static final HandshakeAbsence chOnTradeAbsence;
    static final SSLStringizer pkemStringizer;
    
    static {
        chNetworkProducer = new PskKeyExchangeModesProducer();
        chOnLoadConsumer = new PskKeyExchangeModesConsumer();
        chOnLoadAbsence = new PskKeyExchangeModesOnLoadAbsence();
        chOnTradeAbsence = new PskKeyExchangeModesOnTradeAbsence();
        pkemStringizer = new PskKeyExchangeModesStringizer();
    }
    
    enum PskKeyExchangeMode
    {
        PSK_KE((byte)0, "psk_ke"), 
        PSK_DHE_KE((byte)1, "psk_dhe_ke");
        
        final byte id;
        final String name;
        
        private PskKeyExchangeMode(final byte id, final String name) {
            this.id = id;
            this.name = name;
        }
        
        static PskKeyExchangeMode valueOf(final byte id) {
            for (final PskKeyExchangeMode pkem : values()) {
                if (pkem.id == id) {
                    return pkem;
                }
            }
            return null;
        }
        
        static String nameOf(final byte id) {
            for (final PskKeyExchangeMode pkem : values()) {
                if (pkem.id == id) {
                    return pkem.name;
                }
            }
            return "<UNKNOWN PskKeyExchangeMode TYPE: " + (id & 0xFF) + ">";
        }
    }
    
    static final class PskKeyExchangeModesSpec implements SSLExtension.SSLExtensionSpec
    {
        private static final PskKeyExchangeModesSpec DEFAULT;
        final byte[] modes;
        
        PskKeyExchangeModesSpec(final byte[] modes) {
            this.modes = modes;
        }
        
        PskKeyExchangeModesSpec(final ByteBuffer m) throws IOException {
            if (m.remaining() < 2) {
                throw new SSLProtocolException("Invalid psk_key_exchange_modes extension: insufficient data");
            }
            this.modes = Record.getBytes8(m);
        }
        
        boolean contains(final PskKeyExchangeMode mode) {
            if (this.modes != null) {
                for (final byte m : this.modes) {
                    if (mode.id == m) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"ke_modes\": '['{0}']'", Locale.ENGLISH);
            if (this.modes == null || this.modes.length == 0) {
                final Object[] messageFields = { "<no PSK key exchange modes specified>" };
                return messageFormat.format(messageFields);
            }
            final StringBuilder builder = new StringBuilder(64);
            boolean isFirst = true;
            for (final byte mode : this.modes) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    builder.append(", ");
                }
                builder.append(PskKeyExchangeMode.nameOf(mode));
            }
            final Object[] messageFields2 = { builder.toString() };
            return messageFormat.format(messageFields2);
        }
        
        static {
            DEFAULT = new PskKeyExchangeModesSpec(new byte[] { PskKeyExchangeMode.PSK_DHE_KE.id });
        }
    }
    
    private static final class PskKeyExchangeModesStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new PskKeyExchangeModesSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class PskKeyExchangeModesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.PSK_KEY_EXCHANGE_MODES)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable psk_key_exchange_modes extension", new Object[0]);
                }
                if (shc.isResumption && shc.resumingSession != null) {
                    shc.isResumption = false;
                    shc.resumingSession = null;
                }
                return;
            }
            PskKeyExchangeModesSpec spec;
            try {
                spec = new PskKeyExchangeModesSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            shc.handshakeExtensions.put(SSLExtension.PSK_KEY_EXCHANGE_MODES, spec);
            if (shc.isResumption && !spec.contains(PskKeyExchangeMode.PSK_DHE_KE)) {
                shc.isResumption = false;
                shc.resumingSession = null;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("abort session resumption, no supported psk_dhe_ke PSK key exchange mode", new Object[0]);
                }
            }
        }
    }
    
    private static final class PskKeyExchangeModesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.PSK_KEY_EXCHANGE_MODES)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore unavailable psk_key_exchange_modes extension", new Object[0]);
                }
                return null;
            }
            final byte[] extData = { 1, 1 };
            chc.handshakeExtensions.put(SSLExtension.PSK_KEY_EXCHANGE_MODES, PskKeyExchangeModesSpec.DEFAULT);
            return extData;
        }
    }
    
    private static final class PskKeyExchangeModesOnLoadAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.isResumption) {
                shc.isResumption = false;
                shc.resumingSession = null;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("abort session resumption, no supported psk_dhe_ke PSK key exchange mode", new Object[0]);
                }
            }
        }
    }
    
    private static final class PskKeyExchangeModesOnTradeAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final SSLExtension.SSLExtensionSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_PRE_SHARED_KEY);
            if (spec != null) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "pre_shared_key key extension is offered without a psk_key_exchange_modes extension");
            }
        }
    }
}
