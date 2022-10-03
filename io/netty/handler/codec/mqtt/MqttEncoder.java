package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBufAllocator;
import java.util.Iterator;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.EmptyArrays;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.EncoderException;
import io.netty.buffer.ByteBuf;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.MessageToMessageEncoder;

@ChannelHandler.Sharable
public final class MqttEncoder extends MessageToMessageEncoder<MqttMessage>
{
    public static final MqttEncoder INSTANCE;
    
    private MqttEncoder() {
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final MqttMessage msg, final List<Object> out) throws Exception {
        out.add(doEncode(ctx, msg));
    }
    
    static ByteBuf doEncode(final ChannelHandlerContext ctx, final MqttMessage message) {
        switch (message.fixedHeader().messageType()) {
            case CONNECT: {
                return encodeConnectMessage(ctx, (MqttConnectMessage)message);
            }
            case CONNACK: {
                return encodeConnAckMessage(ctx, (MqttConnAckMessage)message);
            }
            case PUBLISH: {
                return encodePublishMessage(ctx, (MqttPublishMessage)message);
            }
            case SUBSCRIBE: {
                return encodeSubscribeMessage(ctx, (MqttSubscribeMessage)message);
            }
            case UNSUBSCRIBE: {
                return encodeUnsubscribeMessage(ctx, (MqttUnsubscribeMessage)message);
            }
            case SUBACK: {
                return encodeSubAckMessage(ctx, (MqttSubAckMessage)message);
            }
            case UNSUBACK: {
                if (message instanceof MqttUnsubAckMessage) {
                    return encodeUnsubAckMessage(ctx, (MqttUnsubAckMessage)message);
                }
                return encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(ctx.alloc(), message);
            }
            case PUBACK:
            case PUBREC:
            case PUBREL:
            case PUBCOMP: {
                return encodePubReplyMessage(ctx, message);
            }
            case DISCONNECT:
            case AUTH: {
                return encodeReasonCodePlusPropertiesMessage(ctx, message);
            }
            case PINGREQ:
            case PINGRESP: {
                return encodeMessageWithOnlySingleByteFixedHeader(ctx.alloc(), message);
            }
            default: {
                throw new IllegalArgumentException("Unknown message type: " + message.fixedHeader().messageType().value());
            }
        }
    }
    
    private static ByteBuf encodeConnectMessage(final ChannelHandlerContext ctx, final MqttConnectMessage message) {
        int payloadBufferSize = 0;
        final MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        final MqttConnectVariableHeader variableHeader = message.variableHeader();
        final MqttConnectPayload payload = message.payload();
        final MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(variableHeader.name(), (byte)variableHeader.version());
        MqttCodecUtil.setMqttVersion(ctx, mqttVersion);
        if (!variableHeader.hasUserName() && variableHeader.hasPassword()) {
            throw new EncoderException("Without a username, the password MUST be not set");
        }
        final String clientIdentifier = payload.clientIdentifier();
        if (!MqttCodecUtil.isValidClientId(mqttVersion, 23, clientIdentifier)) {
            throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + clientIdentifier);
        }
        final int clientIdentifierBytes = ByteBufUtil.utf8Bytes(clientIdentifier);
        payloadBufferSize += 2 + clientIdentifierBytes;
        final String willTopic = payload.willTopic();
        final int willTopicBytes = nullableUtf8Bytes(willTopic);
        final byte[] willMessage = payload.willMessageInBytes();
        final byte[] willMessageBytes = (willMessage != null) ? willMessage : EmptyArrays.EMPTY_BYTES;
        if (variableHeader.isWillFlag()) {
            payloadBufferSize += 2 + willTopicBytes;
            payloadBufferSize += 2 + willMessageBytes.length;
        }
        final String userName = payload.userName();
        final int userNameBytes = nullableUtf8Bytes(userName);
        if (variableHeader.hasUserName()) {
            payloadBufferSize += 2 + userNameBytes;
        }
        final byte[] password = payload.passwordInBytes();
        final byte[] passwordBytes = (password != null) ? password : EmptyArrays.EMPTY_BYTES;
        if (variableHeader.hasPassword()) {
            payloadBufferSize += 2 + passwordBytes.length;
        }
        final byte[] protocolNameBytes = mqttVersion.protocolNameBytes();
        final ByteBuf propertiesBuf = encodePropertiesIfNeeded(mqttVersion, ctx.alloc(), message.variableHeader().properties());
        try {
            ByteBuf willPropertiesBuf;
            if (variableHeader.isWillFlag()) {
                willPropertiesBuf = encodePropertiesIfNeeded(mqttVersion, ctx.alloc(), payload.willProperties());
                payloadBufferSize += willPropertiesBuf.readableBytes();
            }
            else {
                willPropertiesBuf = Unpooled.EMPTY_BUFFER;
            }
            try {
                final int variableHeaderBufferSize = 2 + protocolNameBytes.length + 4 + propertiesBuf.readableBytes();
                final int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
                final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
                final ByteBuf buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize);
                buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
                writeVariableLengthInt(buf, variablePartSize);
                buf.writeShort(protocolNameBytes.length);
                buf.writeBytes(protocolNameBytes);
                buf.writeByte(variableHeader.version());
                buf.writeByte(getConnVariableHeaderFlag(variableHeader));
                buf.writeShort(variableHeader.keepAliveTimeSeconds());
                buf.writeBytes(propertiesBuf);
                writeExactUTF8String(buf, clientIdentifier, clientIdentifierBytes);
                if (variableHeader.isWillFlag()) {
                    buf.writeBytes(willPropertiesBuf);
                    writeExactUTF8String(buf, willTopic, willTopicBytes);
                    buf.writeShort(willMessageBytes.length);
                    buf.writeBytes(willMessageBytes, 0, willMessageBytes.length);
                }
                if (variableHeader.hasUserName()) {
                    writeExactUTF8String(buf, userName, userNameBytes);
                }
                if (variableHeader.hasPassword()) {
                    buf.writeShort(passwordBytes.length);
                    buf.writeBytes(passwordBytes, 0, passwordBytes.length);
                }
                return buf;
            }
            finally {
                willPropertiesBuf.release();
            }
        }
        finally {
            propertiesBuf.release();
        }
    }
    
    private static int getConnVariableHeaderFlag(final MqttConnectVariableHeader variableHeader) {
        int flagByte = 0;
        if (variableHeader.hasUserName()) {
            flagByte |= 0x80;
        }
        if (variableHeader.hasPassword()) {
            flagByte |= 0x40;
        }
        if (variableHeader.isWillRetain()) {
            flagByte |= 0x20;
        }
        flagByte |= (variableHeader.willQos() & 0x3) << 3;
        if (variableHeader.isWillFlag()) {
            flagByte |= 0x4;
        }
        if (variableHeader.isCleanSession()) {
            flagByte |= 0x2;
        }
        return flagByte;
    }
    
    private static ByteBuf encodeConnAckMessage(final ChannelHandlerContext ctx, final MqttConnAckMessage message) {
        final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
        final ByteBuf propertiesBuf = encodePropertiesIfNeeded(mqttVersion, ctx.alloc(), message.variableHeader().properties());
        try {
            final ByteBuf buf = ctx.alloc().buffer(4 + propertiesBuf.readableBytes());
            buf.writeByte(getFixedHeaderByte1(message.fixedHeader()));
            writeVariableLengthInt(buf, 2 + propertiesBuf.readableBytes());
            buf.writeByte(message.variableHeader().isSessionPresent() ? 1 : 0);
            buf.writeByte(message.variableHeader().connectReturnCode().byteValue());
            buf.writeBytes(propertiesBuf);
            return buf;
        }
        finally {
            propertiesBuf.release();
        }
    }
    
    private static ByteBuf encodeSubscribeMessage(final ChannelHandlerContext ctx, final MqttSubscribeMessage message) {
        final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
        final ByteBuf propertiesBuf = encodePropertiesIfNeeded(mqttVersion, ctx.alloc(), message.idAndPropertiesVariableHeader().properties());
        try {
            final int variableHeaderBufferSize = 2 + propertiesBuf.readableBytes();
            int payloadBufferSize = 0;
            final MqttFixedHeader mqttFixedHeader = message.fixedHeader();
            final MqttMessageIdVariableHeader variableHeader = message.variableHeader();
            final MqttSubscribePayload payload = message.payload();
            for (final MqttTopicSubscription topic : payload.topicSubscriptions()) {
                final String topicName = topic.topicName();
                final int topicNameBytes = ByteBufUtil.utf8Bytes(topicName);
                payloadBufferSize += 2 + topicNameBytes;
                ++payloadBufferSize;
            }
            final int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
            final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
            final ByteBuf buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize);
            buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
            writeVariableLengthInt(buf, variablePartSize);
            final int messageId = variableHeader.messageId();
            buf.writeShort(messageId);
            buf.writeBytes(propertiesBuf);
            for (final MqttTopicSubscription topic2 : payload.topicSubscriptions()) {
                writeUnsafeUTF8String(buf, topic2.topicName());
                if (mqttVersion == MqttVersion.MQTT_3_1_1 || mqttVersion == MqttVersion.MQTT_3_1) {
                    buf.writeByte(topic2.qualityOfService().value());
                }
                else {
                    final MqttSubscriptionOption option = topic2.option();
                    int optionEncoded = option.retainHandling().value() << 4;
                    if (option.isRetainAsPublished()) {
                        optionEncoded |= 0x8;
                    }
                    if (option.isNoLocal()) {
                        optionEncoded |= 0x4;
                    }
                    optionEncoded |= option.qos().value();
                    buf.writeByte(optionEncoded);
                }
            }
            return buf;
        }
        finally {
            propertiesBuf.release();
        }
    }
    
    private static ByteBuf encodeUnsubscribeMessage(final ChannelHandlerContext ctx, final MqttUnsubscribeMessage message) {
        final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
        final ByteBuf propertiesBuf = encodePropertiesIfNeeded(mqttVersion, ctx.alloc(), message.idAndPropertiesVariableHeader().properties());
        try {
            final int variableHeaderBufferSize = 2 + propertiesBuf.readableBytes();
            int payloadBufferSize = 0;
            final MqttFixedHeader mqttFixedHeader = message.fixedHeader();
            final MqttMessageIdVariableHeader variableHeader = message.variableHeader();
            final MqttUnsubscribePayload payload = message.payload();
            for (final String topicName : payload.topics()) {
                final int topicNameBytes = ByteBufUtil.utf8Bytes(topicName);
                payloadBufferSize += 2 + topicNameBytes;
            }
            final int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
            final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
            final ByteBuf buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize);
            buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
            writeVariableLengthInt(buf, variablePartSize);
            final int messageId = variableHeader.messageId();
            buf.writeShort(messageId);
            buf.writeBytes(propertiesBuf);
            for (final String topicName2 : payload.topics()) {
                writeUnsafeUTF8String(buf, topicName2);
            }
            return buf;
        }
        finally {
            propertiesBuf.release();
        }
    }
    
    private static ByteBuf encodeSubAckMessage(final ChannelHandlerContext ctx, final MqttSubAckMessage message) {
        final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
        final ByteBuf propertiesBuf = encodePropertiesIfNeeded(mqttVersion, ctx.alloc(), message.idAndPropertiesVariableHeader().properties());
        try {
            final int variableHeaderBufferSize = 2 + propertiesBuf.readableBytes();
            final int payloadBufferSize = message.payload().grantedQoSLevels().size();
            final int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
            final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
            final ByteBuf buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize);
            buf.writeByte(getFixedHeaderByte1(message.fixedHeader()));
            writeVariableLengthInt(buf, variablePartSize);
            buf.writeShort(message.variableHeader().messageId());
            buf.writeBytes(propertiesBuf);
            for (final int code : message.payload().reasonCodes()) {
                buf.writeByte(code);
            }
            return buf;
        }
        finally {
            propertiesBuf.release();
        }
    }
    
    private static ByteBuf encodeUnsubAckMessage(final ChannelHandlerContext ctx, final MqttUnsubAckMessage message) {
        if (message.variableHeader() instanceof MqttMessageIdAndPropertiesVariableHeader) {
            final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
            final ByteBuf propertiesBuf = encodePropertiesIfNeeded(mqttVersion, ctx.alloc(), message.idAndPropertiesVariableHeader().properties());
            try {
                final int variableHeaderBufferSize = 2 + propertiesBuf.readableBytes();
                final MqttUnsubAckPayload payload = message.payload();
                final int payloadBufferSize = (payload == null) ? 0 : payload.unsubscribeReasonCodes().size();
                final int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
                final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
                final ByteBuf buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize);
                buf.writeByte(getFixedHeaderByte1(message.fixedHeader()));
                writeVariableLengthInt(buf, variablePartSize);
                buf.writeShort(message.variableHeader().messageId());
                buf.writeBytes(propertiesBuf);
                if (payload != null) {
                    for (final Short reasonCode : payload.unsubscribeReasonCodes()) {
                        buf.writeByte(reasonCode);
                    }
                }
                return buf;
            }
            finally {
                propertiesBuf.release();
            }
        }
        return encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(ctx.alloc(), message);
    }
    
    private static ByteBuf encodePublishMessage(final ChannelHandlerContext ctx, final MqttPublishMessage message) {
        final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
        final MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        final MqttPublishVariableHeader variableHeader = message.variableHeader();
        final ByteBuf payload = message.payload().duplicate();
        final String topicName = variableHeader.topicName();
        final int topicNameBytes = ByteBufUtil.utf8Bytes(topicName);
        final ByteBuf propertiesBuf = encodePropertiesIfNeeded(mqttVersion, ctx.alloc(), message.variableHeader().properties());
        try {
            final int variableHeaderBufferSize = 2 + topicNameBytes + ((mqttFixedHeader.qosLevel().value() > 0) ? 2 : 0) + propertiesBuf.readableBytes();
            final int payloadBufferSize = payload.readableBytes();
            final int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
            final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
            final ByteBuf buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize);
            buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
            writeVariableLengthInt(buf, variablePartSize);
            writeExactUTF8String(buf, topicName, topicNameBytes);
            if (mqttFixedHeader.qosLevel().value() > 0) {
                buf.writeShort(variableHeader.packetId());
            }
            buf.writeBytes(propertiesBuf);
            buf.writeBytes(payload);
            return buf;
        }
        finally {
            propertiesBuf.release();
        }
    }
    
    private static ByteBuf encodePubReplyMessage(final ChannelHandlerContext ctx, final MqttMessage message) {
        if (message.variableHeader() instanceof MqttPubReplyMessageVariableHeader) {
            final MqttFixedHeader mqttFixedHeader = message.fixedHeader();
            final MqttPubReplyMessageVariableHeader variableHeader = (MqttPubReplyMessageVariableHeader)message.variableHeader();
            final int msgId = variableHeader.messageId();
            final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
            ByteBuf propertiesBuf;
            boolean includeReasonCode;
            int variableHeaderBufferSize;
            if (mqttVersion == MqttVersion.MQTT_5 && (variableHeader.reasonCode() != 0 || !variableHeader.properties().isEmpty())) {
                propertiesBuf = encodeProperties(ctx.alloc(), variableHeader.properties());
                includeReasonCode = true;
                variableHeaderBufferSize = 3 + propertiesBuf.readableBytes();
            }
            else {
                propertiesBuf = Unpooled.EMPTY_BUFFER;
                includeReasonCode = false;
                variableHeaderBufferSize = 2;
            }
            try {
                final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variableHeaderBufferSize);
                final ByteBuf buf = ctx.alloc().buffer(fixedHeaderBufferSize + variableHeaderBufferSize);
                buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
                writeVariableLengthInt(buf, variableHeaderBufferSize);
                buf.writeShort(msgId);
                if (includeReasonCode) {
                    buf.writeByte(variableHeader.reasonCode());
                }
                buf.writeBytes(propertiesBuf);
                return buf;
            }
            finally {
                propertiesBuf.release();
            }
        }
        return encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(ctx.alloc(), message);
    }
    
    private static ByteBuf encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(final ByteBufAllocator byteBufAllocator, final MqttMessage message) {
        final MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        final MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader)message.variableHeader();
        final int msgId = variableHeader.messageId();
        final int variableHeaderBufferSize = 2;
        final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variableHeaderBufferSize);
        final ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variableHeaderBufferSize);
        buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
        writeVariableLengthInt(buf, variableHeaderBufferSize);
        buf.writeShort(msgId);
        return buf;
    }
    
    private static ByteBuf encodeReasonCodePlusPropertiesMessage(final ChannelHandlerContext ctx, final MqttMessage message) {
        if (message.variableHeader() instanceof MqttReasonCodeAndPropertiesVariableHeader) {
            final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
            final MqttFixedHeader mqttFixedHeader = message.fixedHeader();
            final MqttReasonCodeAndPropertiesVariableHeader variableHeader = (MqttReasonCodeAndPropertiesVariableHeader)message.variableHeader();
            ByteBuf propertiesBuf;
            boolean includeReasonCode;
            int variableHeaderBufferSize;
            if (mqttVersion == MqttVersion.MQTT_5 && (variableHeader.reasonCode() != 0 || !variableHeader.properties().isEmpty())) {
                propertiesBuf = encodeProperties(ctx.alloc(), variableHeader.properties());
                includeReasonCode = true;
                variableHeaderBufferSize = 1 + propertiesBuf.readableBytes();
            }
            else {
                propertiesBuf = Unpooled.EMPTY_BUFFER;
                includeReasonCode = false;
                variableHeaderBufferSize = 0;
            }
            try {
                final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variableHeaderBufferSize);
                final ByteBuf buf = ctx.alloc().buffer(fixedHeaderBufferSize + variableHeaderBufferSize);
                buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
                writeVariableLengthInt(buf, variableHeaderBufferSize);
                if (includeReasonCode) {
                    buf.writeByte(variableHeader.reasonCode());
                }
                buf.writeBytes(propertiesBuf);
                return buf;
            }
            finally {
                propertiesBuf.release();
            }
        }
        return encodeMessageWithOnlySingleByteFixedHeader(ctx.alloc(), message);
    }
    
    private static ByteBuf encodeMessageWithOnlySingleByteFixedHeader(final ByteBufAllocator byteBufAllocator, final MqttMessage message) {
        final MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        final ByteBuf buf = byteBufAllocator.buffer(2);
        buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
        buf.writeByte(0);
        return buf;
    }
    
    private static ByteBuf encodePropertiesIfNeeded(final MqttVersion mqttVersion, final ByteBufAllocator byteBufAllocator, final MqttProperties mqttProperties) {
        if (mqttVersion == MqttVersion.MQTT_5) {
            return encodeProperties(byteBufAllocator, mqttProperties);
        }
        return Unpooled.EMPTY_BUFFER;
    }
    
    private static ByteBuf encodeProperties(final ByteBufAllocator byteBufAllocator, final MqttProperties mqttProperties) {
        final ByteBuf propertiesHeaderBuf = byteBufAllocator.buffer();
        try {
            final ByteBuf propertiesBuf = byteBufAllocator.buffer();
            try {
                for (final MqttProperties.MqttProperty property : mqttProperties.listAll()) {
                    final MqttProperties.MqttPropertyType propertyType = MqttProperties.MqttPropertyType.valueOf(property.propertyId);
                    switch (propertyType) {
                        case PAYLOAD_FORMAT_INDICATOR:
                        case REQUEST_PROBLEM_INFORMATION:
                        case REQUEST_RESPONSE_INFORMATION:
                        case MAXIMUM_QOS:
                        case RETAIN_AVAILABLE:
                        case WILDCARD_SUBSCRIPTION_AVAILABLE:
                        case SUBSCRIPTION_IDENTIFIER_AVAILABLE:
                        case SHARED_SUBSCRIPTION_AVAILABLE: {
                            writeVariableLengthInt(propertiesBuf, property.propertyId);
                            final byte bytePropValue = ((Integer)((MqttProperties.IntegerProperty)property).value).byteValue();
                            propertiesBuf.writeByte(bytePropValue);
                            continue;
                        }
                        case SERVER_KEEP_ALIVE:
                        case RECEIVE_MAXIMUM:
                        case TOPIC_ALIAS_MAXIMUM:
                        case TOPIC_ALIAS: {
                            writeVariableLengthInt(propertiesBuf, property.propertyId);
                            final short twoBytesInPropValue = ((Integer)((MqttProperties.IntegerProperty)property).value).shortValue();
                            propertiesBuf.writeShort(twoBytesInPropValue);
                            continue;
                        }
                        case PUBLICATION_EXPIRY_INTERVAL:
                        case SESSION_EXPIRY_INTERVAL:
                        case WILL_DELAY_INTERVAL:
                        case MAXIMUM_PACKET_SIZE: {
                            writeVariableLengthInt(propertiesBuf, property.propertyId);
                            final int fourBytesIntPropValue = (int)((MqttProperties.IntegerProperty)property).value;
                            propertiesBuf.writeInt(fourBytesIntPropValue);
                            continue;
                        }
                        case SUBSCRIPTION_IDENTIFIER: {
                            writeVariableLengthInt(propertiesBuf, property.propertyId);
                            final int vbi = (int)((MqttProperties.IntegerProperty)property).value;
                            writeVariableLengthInt(propertiesBuf, vbi);
                            continue;
                        }
                        case CONTENT_TYPE:
                        case RESPONSE_TOPIC:
                        case ASSIGNED_CLIENT_IDENTIFIER:
                        case AUTHENTICATION_METHOD:
                        case RESPONSE_INFORMATION:
                        case SERVER_REFERENCE:
                        case REASON_STRING: {
                            writeVariableLengthInt(propertiesBuf, property.propertyId);
                            writeEagerUTF8String(propertiesBuf, (String)((MqttProperties.StringProperty)property).value);
                            continue;
                        }
                        case USER_PROPERTY: {
                            final List<MqttProperties.StringPair> pairs = (List<MqttProperties.StringPair>)((MqttProperties.UserProperties)property).value;
                            for (final MqttProperties.StringPair pair : pairs) {
                                writeVariableLengthInt(propertiesBuf, property.propertyId);
                                writeEagerUTF8String(propertiesBuf, pair.key);
                                writeEagerUTF8String(propertiesBuf, pair.value);
                            }
                            continue;
                        }
                        case CORRELATION_DATA:
                        case AUTHENTICATION_DATA: {
                            writeVariableLengthInt(propertiesBuf, property.propertyId);
                            final byte[] binaryPropValue = (Object)((MqttProperties.BinaryProperty)property).value;
                            propertiesBuf.writeShort(binaryPropValue.length);
                            propertiesBuf.writeBytes(binaryPropValue, 0, binaryPropValue.length);
                            continue;
                        }
                        default: {
                            throw new EncoderException("Unknown property type: " + propertyType);
                        }
                    }
                }
                writeVariableLengthInt(propertiesHeaderBuf, propertiesBuf.readableBytes());
                propertiesHeaderBuf.writeBytes(propertiesBuf);
                return propertiesHeaderBuf;
            }
            finally {
                propertiesBuf.release();
            }
        }
        catch (final RuntimeException e) {
            propertiesHeaderBuf.release();
            throw e;
        }
    }
    
    private static int getFixedHeaderByte1(final MqttFixedHeader header) {
        int ret = 0;
        ret |= header.messageType().value() << 4;
        if (header.isDup()) {
            ret |= 0x8;
        }
        ret |= header.qosLevel().value() << 1;
        if (header.isRetain()) {
            ret |= 0x1;
        }
        return ret;
    }
    
    private static void writeVariableLengthInt(final ByteBuf buf, int num) {
        do {
            int digit = num % 128;
            num /= 128;
            if (num > 0) {
                digit |= 0x80;
            }
            buf.writeByte(digit);
        } while (num > 0);
    }
    
    private static int nullableUtf8Bytes(final String s) {
        return (s == null) ? 0 : ByteBufUtil.utf8Bytes(s);
    }
    
    private static int nullableMaxUtf8Bytes(final String s) {
        return (s == null) ? 0 : ByteBufUtil.utf8MaxBytes(s);
    }
    
    private static void writeExactUTF8String(final ByteBuf buf, final String s, final int utf8Length) {
        buf.ensureWritable(utf8Length + 2);
        buf.writeShort(utf8Length);
        if (utf8Length > 0) {
            final int writtenUtf8Length = ByteBufUtil.reserveAndWriteUtf8(buf, s, utf8Length);
            assert writtenUtf8Length == utf8Length;
        }
    }
    
    private static void writeEagerUTF8String(final ByteBuf buf, final String s) {
        final int maxUtf8Length = nullableMaxUtf8Bytes(s);
        buf.ensureWritable(maxUtf8Length + 2);
        final int writerIndex = buf.writerIndex();
        final int startUtf8String = writerIndex + 2;
        buf.writerIndex(startUtf8String);
        final int utf8Length = (s != null) ? ByteBufUtil.reserveAndWriteUtf8(buf, s, maxUtf8Length) : 0;
        buf.setShort(writerIndex, utf8Length);
    }
    
    private static void writeUnsafeUTF8String(final ByteBuf buf, final String s) {
        final int writerIndex = buf.writerIndex();
        final int startUtf8String = writerIndex + 2;
        buf.writerIndex(startUtf8String);
        final int utf8Length = (s != null) ? ByteBufUtil.reserveAndWriteUtf8(buf, s, 0) : 0;
        buf.setShort(writerIndex, utf8Length);
    }
    
    private static int getVariableLengthInt(int num) {
        int count = 0;
        do {
            num /= 128;
            ++count;
        } while (num > 0);
        return count;
    }
    
    static {
        INSTANCE = new MqttEncoder();
    }
}
