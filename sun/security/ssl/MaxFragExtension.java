package sun.security.ssl;

import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;

final class MaxFragExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeProducer shNetworkProducer;
    static final SSLExtension.ExtensionConsumer shOnLoadConsumer;
    static final HandshakeConsumer shOnTradeConsumer;
    static final HandshakeProducer eeNetworkProducer;
    static final SSLExtension.ExtensionConsumer eeOnLoadConsumer;
    static final HandshakeConsumer eeOnTradeConsumer;
    static final SSLStringizer maxFragLenStringizer;
    
    static {
        chNetworkProducer = new CHMaxFragmentLengthProducer();
        chOnLoadConsumer = new CHMaxFragmentLengthConsumer();
        shNetworkProducer = new SHMaxFragmentLengthProducer();
        shOnLoadConsumer = new SHMaxFragmentLengthConsumer();
        shOnTradeConsumer = new SHMaxFragmentLengthUpdate();
        eeNetworkProducer = new EEMaxFragmentLengthProducer();
        eeOnLoadConsumer = new EEMaxFragmentLengthConsumer();
        eeOnTradeConsumer = new EEMaxFragmentLengthUpdate();
        maxFragLenStringizer = new MaxFragLenStringizer();
    }
    
    static final class MaxFragLenSpec implements SSLExtension.SSLExtensionSpec
    {
        byte id;
        
        private MaxFragLenSpec(final byte id) {
            this.id = id;
        }
        
        private MaxFragLenSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() != 1) {
                throw new SSLProtocolException("Invalid max_fragment_length extension data");
            }
            this.id = byteBuffer.get();
        }
        
        @Override
        public String toString() {
            return nameOf(this.id);
        }
    }
    
    private static final class MaxFragLenStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new MaxFragLenSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    enum MaxFragLenEnum
    {
        MFL_512((byte)1, 512, "2^9"), 
        MFL_1024((byte)2, 1024, "2^10"), 
        MFL_2048((byte)3, 2048, "2^11"), 
        MFL_4096((byte)4, 4096, "2^12");
        
        final byte id;
        final int fragmentSize;
        final String description;
        
        private MaxFragLenEnum(final byte id, final int fragmentSize, final String description) {
            this.id = id;
            this.fragmentSize = fragmentSize;
            this.description = description;
        }
        
        private static MaxFragLenEnum valueOf(final byte b) {
            for (final MaxFragLenEnum maxFragLenEnum : values()) {
                if (maxFragLenEnum.id == b) {
                    return maxFragLenEnum;
                }
            }
            return null;
        }
        
        private static String nameOf(final byte b) {
            for (final MaxFragLenEnum maxFragLenEnum : values()) {
                if (maxFragLenEnum.id == b) {
                    return maxFragLenEnum.description;
                }
            }
            return "UNDEFINED-MAX-FRAGMENT-LENGTH(" + b + ")";
        }
        
        static MaxFragLenEnum valueOf(final int n) {
            if (n <= 0) {
                return null;
            }
            if (n < 1024) {
                return MaxFragLenEnum.MFL_512;
            }
            if (n < 2048) {
                return MaxFragLenEnum.MFL_1024;
            }
            if (n < 4096) {
                return MaxFragLenEnum.MFL_2048;
            }
            if (n == 4096) {
                return MaxFragLenEnum.MFL_4096;
            }
            return null;
        }
    }
    
    private static final class CHMaxFragmentLengthProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_MAX_FRAGMENT_LENGTH)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable max_fragment_length extension", new Object[0]);
                }
                return null;
            }
            int negotiatedMaxFragSize;
            if (clientHandshakeContext.isResumption && clientHandshakeContext.resumingSession != null) {
                negotiatedMaxFragSize = clientHandshakeContext.resumingSession.getNegotiatedMaxFragSize();
            }
            else if (clientHandshakeContext.sslConfig.maximumPacketSize != 0) {
                negotiatedMaxFragSize = clientHandshakeContext.sslConfig.maximumPacketSize - 325;
            }
            else {
                negotiatedMaxFragSize = -1;
            }
            final MaxFragLenEnum value = MaxFragLenEnum.valueOf(negotiatedMaxFragSize);
            if (value != null) {
                clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_MAX_FRAGMENT_LENGTH, new MaxFragLenSpec(value.id));
                return new byte[] { value.id };
            }
            clientHandshakeContext.maxFragmentLength = -1;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("No available max_fragment_length extension can be used for fragment size of " + negotiatedMaxFragSize + "bytes", new Object[0]);
            }
            return null;
        }
    }
    
    private static final class CHMaxFragmentLengthConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_MAX_FRAGMENT_LENGTH)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable max_fragment_length extension", new Object[0]);
                }
                return;
            }
            MaxFragLenSpec maxFragLenSpec;
            try {
                maxFragLenSpec = new MaxFragLenSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            final MaxFragLenEnum access$1200 = valueOf(maxFragLenSpec.id);
            if (access$1200 == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "the requested maximum fragment length is other than the allowed values");
            }
            serverHandshakeContext.maxFragmentLength = access$1200.fragmentSize;
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_MAX_FRAGMENT_LENGTH, maxFragLenSpec);
        }
    }
    
    private static final class SHMaxFragmentLengthProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final MaxFragLenSpec maxFragLenSpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_MAX_FRAGMENT_LENGTH);
            if (maxFragLenSpec == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable max_fragment_length extension", new Object[0]);
                }
                return null;
            }
            if (serverHandshakeContext.maxFragmentLength > 0 && serverHandshakeContext.sslConfig.maximumPacketSize != 0 && serverHandshakeContext.negotiatedCipherSuite.calculatePacketSize(serverHandshakeContext.maxFragmentLength, serverHandshakeContext.negotiatedProtocol) > serverHandshakeContext.sslConfig.maximumPacketSize) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Abort the maximum fragment length negotiation, may overflow the maximum packet size limit.", new Object[0]);
                }
                serverHandshakeContext.maxFragmentLength = -1;
            }
            if (serverHandshakeContext.maxFragmentLength > 0) {
                serverHandshakeContext.handshakeSession.setNegotiatedMaxFragSize(serverHandshakeContext.maxFragmentLength);
                serverHandshakeContext.conContext.inputRecord.changeFragmentSize(serverHandshakeContext.maxFragmentLength);
                serverHandshakeContext.conContext.outputRecord.changeFragmentSize(serverHandshakeContext.maxFragmentLength);
                serverHandshakeContext.handshakeExtensions.put(SSLExtension.SH_MAX_FRAGMENT_LENGTH, maxFragLenSpec);
                return new byte[] { maxFragLenSpec.id };
            }
            return null;
        }
    }
    
    private static final class SHMaxFragmentLengthConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final MaxFragLenSpec maxFragLenSpec = clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_MAX_FRAGMENT_LENGTH);
            if (maxFragLenSpec == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected max_fragment_length extension in ServerHello");
            }
            MaxFragLenSpec maxFragLenSpec2;
            try {
                maxFragLenSpec2 = new MaxFragLenSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (maxFragLenSpec2.id != maxFragLenSpec.id) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "The maximum fragment length response is not requested");
            }
            final MaxFragLenEnum access$1200 = valueOf(maxFragLenSpec2.id);
            if (access$1200 == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "the requested maximum fragment length is other than the allowed values");
            }
            clientHandshakeContext.maxFragmentLength = access$1200.fragmentSize;
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.SH_MAX_FRAGMENT_LENGTH, maxFragLenSpec2);
        }
    }
    
    private static final class SHMaxFragmentLengthUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.handshakeExtensions.get(SSLExtension.SH_MAX_FRAGMENT_LENGTH) == null) {
                return;
            }
            if (clientHandshakeContext.maxFragmentLength > 0 && clientHandshakeContext.sslConfig.maximumPacketSize != 0 && clientHandshakeContext.negotiatedCipherSuite.calculatePacketSize(clientHandshakeContext.maxFragmentLength, clientHandshakeContext.negotiatedProtocol) > clientHandshakeContext.sslConfig.maximumPacketSize) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Abort the maximum fragment length negotiation, may overflow the maximum packet size limit.", new Object[0]);
                }
                clientHandshakeContext.maxFragmentLength = -1;
            }
            if (clientHandshakeContext.maxFragmentLength > 0) {
                clientHandshakeContext.handshakeSession.setNegotiatedMaxFragSize(clientHandshakeContext.maxFragmentLength);
                clientHandshakeContext.conContext.inputRecord.changeFragmentSize(clientHandshakeContext.maxFragmentLength);
                clientHandshakeContext.conContext.outputRecord.changeFragmentSize(clientHandshakeContext.maxFragmentLength);
            }
        }
    }
    
    private static final class EEMaxFragmentLengthProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final MaxFragLenSpec maxFragLenSpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_MAX_FRAGMENT_LENGTH);
            if (maxFragLenSpec == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable max_fragment_length extension", new Object[0]);
                }
                return null;
            }
            if (serverHandshakeContext.maxFragmentLength > 0 && serverHandshakeContext.sslConfig.maximumPacketSize != 0 && serverHandshakeContext.negotiatedCipherSuite.calculatePacketSize(serverHandshakeContext.maxFragmentLength, serverHandshakeContext.negotiatedProtocol) > serverHandshakeContext.sslConfig.maximumPacketSize) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Abort the maximum fragment length negotiation, may overflow the maximum packet size limit.", new Object[0]);
                }
                serverHandshakeContext.maxFragmentLength = -1;
            }
            if (serverHandshakeContext.maxFragmentLength > 0) {
                serverHandshakeContext.handshakeSession.setNegotiatedMaxFragSize(serverHandshakeContext.maxFragmentLength);
                serverHandshakeContext.conContext.inputRecord.changeFragmentSize(serverHandshakeContext.maxFragmentLength);
                serverHandshakeContext.conContext.outputRecord.changeFragmentSize(serverHandshakeContext.maxFragmentLength);
                serverHandshakeContext.handshakeExtensions.put(SSLExtension.EE_MAX_FRAGMENT_LENGTH, maxFragLenSpec);
                return new byte[] { maxFragLenSpec.id };
            }
            return null;
        }
    }
    
    private static final class EEMaxFragmentLengthConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final MaxFragLenSpec maxFragLenSpec = clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_MAX_FRAGMENT_LENGTH);
            if (maxFragLenSpec == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected max_fragment_length extension in ServerHello");
            }
            MaxFragLenSpec maxFragLenSpec2;
            try {
                maxFragLenSpec2 = new MaxFragLenSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (maxFragLenSpec2.id != maxFragLenSpec.id) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "The maximum fragment length response is not requested");
            }
            final MaxFragLenEnum access$1200 = valueOf(maxFragLenSpec2.id);
            if (access$1200 == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "the requested maximum fragment length is other than the allowed values");
            }
            clientHandshakeContext.maxFragmentLength = access$1200.fragmentSize;
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.EE_MAX_FRAGMENT_LENGTH, maxFragLenSpec2);
        }
    }
    
    private static final class EEMaxFragmentLengthUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.handshakeExtensions.get(SSLExtension.EE_MAX_FRAGMENT_LENGTH) == null) {
                return;
            }
            if (clientHandshakeContext.maxFragmentLength > 0 && clientHandshakeContext.sslConfig.maximumPacketSize != 0 && clientHandshakeContext.negotiatedCipherSuite.calculatePacketSize(clientHandshakeContext.maxFragmentLength, clientHandshakeContext.negotiatedProtocol) > clientHandshakeContext.sslConfig.maximumPacketSize) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Abort the maximum fragment length negotiation, may overflow the maximum packet size limit.", new Object[0]);
                }
                clientHandshakeContext.maxFragmentLength = -1;
            }
            if (clientHandshakeContext.maxFragmentLength > 0) {
                clientHandshakeContext.handshakeSession.setNegotiatedMaxFragSize(clientHandshakeContext.maxFragmentLength);
                clientHandshakeContext.conContext.inputRecord.changeFragmentSize(clientHandshakeContext.maxFragmentLength);
                clientHandshakeContext.conContext.outputRecord.changeFragmentSize(clientHandshakeContext.maxFragmentLength);
            }
        }
    }
}
