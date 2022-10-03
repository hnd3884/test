package sun.security.ssl;

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
        
        static PskKeyExchangeMode valueOf(final byte b) {
            for (final PskKeyExchangeMode pskKeyExchangeMode : values()) {
                if (pskKeyExchangeMode.id == b) {
                    return pskKeyExchangeMode;
                }
            }
            return null;
        }
        
        static String nameOf(final byte b) {
            for (final PskKeyExchangeMode pskKeyExchangeMode : values()) {
                if (pskKeyExchangeMode.id == b) {
                    return pskKeyExchangeMode.name;
                }
            }
            return "<UNKNOWN PskKeyExchangeMode TYPE: " + (b & 0xFF) + ">";
        }
    }
    
    static final class PskKeyExchangeModesSpec implements SSLExtension.SSLExtensionSpec
    {
        private static final PskKeyExchangeModesSpec DEFAULT;
        final byte[] modes;
        
        PskKeyExchangeModesSpec(final byte[] modes) {
            this.modes = modes;
        }
        
        PskKeyExchangeModesSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid psk_key_exchange_modes extension: insufficient data");
            }
            this.modes = Record.getBytes8(byteBuffer);
        }
        
        boolean contains(final PskKeyExchangeMode pskKeyExchangeMode) {
            if (this.modes != null) {
                final byte[] modes = this.modes;
                for (int length = modes.length, i = 0; i < length; ++i) {
                    if (pskKeyExchangeMode.id == modes[i]) {
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
                return messageFormat.format(new Object[] { "<no PSK key exchange modes specified>" });
            }
            final StringBuilder sb = new StringBuilder(64);
            int n = 1;
            for (final byte b : this.modes) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append(", ");
                }
                sb.append(PskKeyExchangeMode.nameOf(b));
            }
            return messageFormat.format(new Object[] { sb.toString() });
        }
        
        static {
            DEFAULT = new PskKeyExchangeModesSpec(new byte[] { PskKeyExchangeMode.PSK_DHE_KE.id });
        }
    }
    
    private static final class PskKeyExchangeModesStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new PskKeyExchangeModesSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class PskKeyExchangeModesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.PSK_KEY_EXCHANGE_MODES)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable psk_key_exchange_modes extension", new Object[0]);
                }
                if (serverHandshakeContext.isResumption && serverHandshakeContext.resumingSession != null) {
                    serverHandshakeContext.isResumption = false;
                    serverHandshakeContext.resumingSession = null;
                }
                return;
            }
            PskKeyExchangeModesSpec pskKeyExchangeModesSpec;
            try {
                pskKeyExchangeModesSpec = new PskKeyExchangeModesSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.PSK_KEY_EXCHANGE_MODES, pskKeyExchangeModesSpec);
            if (serverHandshakeContext.isResumption && !pskKeyExchangeModesSpec.contains(PskKeyExchangeMode.PSK_DHE_KE)) {
                serverHandshakeContext.isResumption = false;
                serverHandshakeContext.resumingSession = null;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("abort session resumption, no supported psk_dhe_ke PSK key exchange mode", new Object[0]);
                }
            }
        }
    }
    
    private static final class PskKeyExchangeModesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.PSK_KEY_EXCHANGE_MODES)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore unavailable psk_key_exchange_modes extension", new Object[0]);
                }
                return null;
            }
            final byte[] array = { 1, 1 };
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.PSK_KEY_EXCHANGE_MODES, PskKeyExchangeModesSpec.DEFAULT);
            return array;
        }
    }
    
    private static final class PskKeyExchangeModesOnLoadAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.isResumption) {
                serverHandshakeContext.isResumption = false;
                serverHandshakeContext.resumingSession = null;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("abort session resumption, no supported psk_dhe_ke PSK key exchange mode", new Object[0]);
                }
            }
        }
    }
    
    private static final class PskKeyExchangeModesOnTradeAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_PRE_SHARED_KEY) != null) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "pre_shared_key key extension is offered without a psk_key_exchange_modes extension");
            }
        }
    }
}
