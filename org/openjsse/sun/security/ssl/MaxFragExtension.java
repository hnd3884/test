package org.openjsse.sun.security.ssl;

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
        
        private MaxFragLenSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() != 1) {
                throw new SSLProtocolException("Invalid max_fragment_length extension data");
            }
            this.id = buffer.get();
        }
        
        @Override
        public String toString() {
            return nameOf(this.id);
        }
    }
    
    private static final class MaxFragLenStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new MaxFragLenSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
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
        
        private static MaxFragLenEnum valueOf(final byte id) {
            for (final MaxFragLenEnum mfl : values()) {
                if (mfl.id == id) {
                    return mfl;
                }
            }
            return null;
        }
        
        private static String nameOf(final byte id) {
            for (final MaxFragLenEnum mfl : values()) {
                if (mfl.id == id) {
                    return mfl.description;
                }
            }
            return "UNDEFINED-MAX-FRAGMENT-LENGTH(" + id + ")";
        }
        
        static MaxFragLenEnum valueOf(final int fragmentSize) {
            if (fragmentSize <= 0) {
                return null;
            }
            if (fragmentSize < 1024) {
                return MaxFragLenEnum.MFL_512;
            }
            if (fragmentSize < 2048) {
                return MaxFragLenEnum.MFL_1024;
            }
            if (fragmentSize < 4096) {
                return MaxFragLenEnum.MFL_2048;
            }
            if (fragmentSize == 4096) {
                return MaxFragLenEnum.MFL_4096;
            }
            return null;
        }
    }
    
    private static final class CHMaxFragmentLengthProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_MAX_FRAGMENT_LENGTH)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable max_fragment_length extension", new Object[0]);
                }
                return null;
            }
            int requestedMFLength;
            if (chc.isResumption && chc.resumingSession != null) {
                requestedMFLength = chc.resumingSession.getNegotiatedMaxFragSize();
            }
            else if (chc.sslConfig.maximumPacketSize != 0) {
                requestedMFLength = chc.sslConfig.maximumPacketSize;
                if (chc.sslContext.isDTLS()) {
                    requestedMFLength -= 333;
                }
                else {
                    requestedMFLength -= 325;
                }
            }
            else {
                requestedMFLength = -1;
            }
            final MaxFragLenEnum mfl = MaxFragLenEnum.valueOf(requestedMFLength);
            if (mfl != null) {
                chc.handshakeExtensions.put(SSLExtension.CH_MAX_FRAGMENT_LENGTH, new MaxFragLenSpec(mfl.id));
                return new byte[] { mfl.id };
            }
            chc.maxFragmentLength = -1;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("No available max_fragment_length extension can be used for fragment size of " + requestedMFLength + "bytes", new Object[0]);
            }
            return null;
        }
    }
    
    private static final class CHMaxFragmentLengthConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_MAX_FRAGMENT_LENGTH)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable max_fragment_length extension", new Object[0]);
                }
                return;
            }
            MaxFragLenSpec spec;
            try {
                spec = new MaxFragLenSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            final MaxFragLenEnum mfle = valueOf(spec.id);
            if (mfle == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "the requested maximum fragment length is other than the allowed values");
            }
            shc.maxFragmentLength = mfle.fragmentSize;
            shc.handshakeExtensions.put(SSLExtension.CH_MAX_FRAGMENT_LENGTH, spec);
        }
    }
    
    private static final class SHMaxFragmentLengthProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final MaxFragLenSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_MAX_FRAGMENT_LENGTH);
            if (spec == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable max_fragment_length extension", new Object[0]);
                }
                return null;
            }
            if (shc.maxFragmentLength > 0 && shc.sslConfig.maximumPacketSize != 0) {
                final int estimatedMaxFragSize = shc.negotiatedCipherSuite.calculatePacketSize(shc.maxFragmentLength, shc.negotiatedProtocol, shc.sslContext.isDTLS());
                if (estimatedMaxFragSize > shc.sslConfig.maximumPacketSize) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Abort the maximum fragment length negotiation, may overflow the maximum packet size limit.", new Object[0]);
                    }
                    shc.maxFragmentLength = -1;
                }
            }
            if (shc.maxFragmentLength > 0) {
                shc.handshakeSession.setNegotiatedMaxFragSize(shc.maxFragmentLength);
                shc.conContext.inputRecord.changeFragmentSize(shc.maxFragmentLength);
                shc.conContext.outputRecord.changeFragmentSize(shc.maxFragmentLength);
                shc.handshakeExtensions.put(SSLExtension.SH_MAX_FRAGMENT_LENGTH, spec);
                return new byte[] { spec.id };
            }
            return null;
        }
    }
    
    private static final class SHMaxFragmentLengthConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final MaxFragLenSpec requestedSpec = chc.handshakeExtensions.get(SSLExtension.CH_MAX_FRAGMENT_LENGTH);
            if (requestedSpec == null) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected max_fragment_length extension in ServerHello");
            }
            MaxFragLenSpec spec;
            try {
                spec = new MaxFragLenSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (spec.id != requestedSpec.id) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "The maximum fragment length response is not requested");
            }
            final MaxFragLenEnum mfle = valueOf(spec.id);
            if (mfle == null) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "the requested maximum fragment length is other than the allowed values");
            }
            chc.maxFragmentLength = mfle.fragmentSize;
            chc.handshakeExtensions.put(SSLExtension.SH_MAX_FRAGMENT_LENGTH, spec);
        }
    }
    
    private static final class SHMaxFragmentLengthUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final MaxFragLenSpec spec = chc.handshakeExtensions.get(SSLExtension.SH_MAX_FRAGMENT_LENGTH);
            if (spec == null) {
                return;
            }
            if (chc.maxFragmentLength > 0 && chc.sslConfig.maximumPacketSize != 0) {
                final int estimatedMaxFragSize = chc.negotiatedCipherSuite.calculatePacketSize(chc.maxFragmentLength, chc.negotiatedProtocol, chc.sslContext.isDTLS());
                if (estimatedMaxFragSize > chc.sslConfig.maximumPacketSize) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Abort the maximum fragment length negotiation, may overflow the maximum packet size limit.", new Object[0]);
                    }
                    chc.maxFragmentLength = -1;
                }
            }
            if (chc.maxFragmentLength > 0) {
                chc.handshakeSession.setNegotiatedMaxFragSize(chc.maxFragmentLength);
                chc.conContext.inputRecord.changeFragmentSize(chc.maxFragmentLength);
                chc.conContext.outputRecord.changeFragmentSize(chc.maxFragmentLength);
            }
        }
    }
    
    private static final class EEMaxFragmentLengthProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final MaxFragLenSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_MAX_FRAGMENT_LENGTH);
            if (spec == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable max_fragment_length extension", new Object[0]);
                }
                return null;
            }
            if (shc.maxFragmentLength > 0 && shc.sslConfig.maximumPacketSize != 0) {
                final int estimatedMaxFragSize = shc.negotiatedCipherSuite.calculatePacketSize(shc.maxFragmentLength, shc.negotiatedProtocol, shc.sslContext.isDTLS());
                if (estimatedMaxFragSize > shc.sslConfig.maximumPacketSize) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Abort the maximum fragment length negotiation, may overflow the maximum packet size limit.", new Object[0]);
                    }
                    shc.maxFragmentLength = -1;
                }
            }
            if (shc.maxFragmentLength > 0) {
                shc.handshakeSession.setNegotiatedMaxFragSize(shc.maxFragmentLength);
                shc.conContext.inputRecord.changeFragmentSize(shc.maxFragmentLength);
                shc.conContext.outputRecord.changeFragmentSize(shc.maxFragmentLength);
                shc.handshakeExtensions.put(SSLExtension.EE_MAX_FRAGMENT_LENGTH, spec);
                return new byte[] { spec.id };
            }
            return null;
        }
    }
    
    private static final class EEMaxFragmentLengthConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final MaxFragLenSpec requestedSpec = chc.handshakeExtensions.get(SSLExtension.CH_MAX_FRAGMENT_LENGTH);
            if (requestedSpec == null) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected max_fragment_length extension in ServerHello");
            }
            MaxFragLenSpec spec;
            try {
                spec = new MaxFragLenSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (spec.id != requestedSpec.id) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "The maximum fragment length response is not requested");
            }
            final MaxFragLenEnum mfle = valueOf(spec.id);
            if (mfle == null) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "the requested maximum fragment length is other than the allowed values");
            }
            chc.maxFragmentLength = mfle.fragmentSize;
            chc.handshakeExtensions.put(SSLExtension.EE_MAX_FRAGMENT_LENGTH, spec);
        }
    }
    
    private static final class EEMaxFragmentLengthUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final MaxFragLenSpec spec = chc.handshakeExtensions.get(SSLExtension.EE_MAX_FRAGMENT_LENGTH);
            if (spec == null) {
                return;
            }
            if (chc.maxFragmentLength > 0 && chc.sslConfig.maximumPacketSize != 0) {
                final int estimatedMaxFragSize = chc.negotiatedCipherSuite.calculatePacketSize(chc.maxFragmentLength, chc.negotiatedProtocol, chc.sslContext.isDTLS());
                if (estimatedMaxFragSize > chc.sslConfig.maximumPacketSize) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Abort the maximum fragment length negotiation, may overflow the maximum packet size limit.", new Object[0]);
                    }
                    chc.maxFragmentLength = -1;
                }
            }
            if (chc.maxFragmentLength > 0) {
                chc.handshakeSession.setNegotiatedMaxFragSize(chc.maxFragmentLength);
                chc.conContext.inputRecord.changeFragmentSize(chc.maxFragmentLength);
                chc.conContext.outputRecord.changeFragmentSize(chc.maxFragmentLength);
            }
        }
    }
}
