package io.netty.handler.codec.mqtt;

import io.netty.util.CharsetUtil;
import java.util.ArrayList;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.ReplayingDecoder;

public final class MqttDecoder extends ReplayingDecoder<DecoderState>
{
    private MqttFixedHeader mqttFixedHeader;
    private Object variableHeader;
    private int bytesRemainingInVariablePart;
    private final int maxBytesInMessage;
    private final int maxClientIdLength;
    
    public MqttDecoder() {
        this(8092, 23);
    }
    
    public MqttDecoder(final int maxBytesInMessage) {
        this(maxBytesInMessage, 23);
    }
    
    public MqttDecoder(final int maxBytesInMessage, final int maxClientIdLength) {
        super(DecoderState.READ_FIXED_HEADER);
        this.maxBytesInMessage = ObjectUtil.checkPositive(maxBytesInMessage, "maxBytesInMessage");
        this.maxClientIdLength = ObjectUtil.checkPositive(maxClientIdLength, "maxClientIdLength");
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) throws Exception {
        switch (this.state()) {
            case READ_FIXED_HEADER: {
                try {
                    this.mqttFixedHeader = decodeFixedHeader(ctx, buffer);
                    this.bytesRemainingInVariablePart = this.mqttFixedHeader.remainingLength();
                    this.checkpoint(DecoderState.READ_VARIABLE_HEADER);
                }
                catch (final Exception cause) {
                    out.add(this.invalidMessage(cause));
                }
            }
            case READ_VARIABLE_HEADER: {
                try {
                    final Result<?> decodedVariableHeader = this.decodeVariableHeader(ctx, buffer, this.mqttFixedHeader);
                    this.variableHeader = ((Result<Object>)decodedVariableHeader).value;
                    if (this.bytesRemainingInVariablePart > this.maxBytesInMessage) {
                        buffer.skipBytes(this.actualReadableBytes());
                        throw new TooLongFrameException("too large message: " + this.bytesRemainingInVariablePart + " bytes");
                    }
                    this.bytesRemainingInVariablePart -= ((Result<Object>)decodedVariableHeader).numberOfBytesConsumed;
                    this.checkpoint(DecoderState.READ_PAYLOAD);
                }
                catch (final Exception cause) {
                    out.add(this.invalidMessage(cause));
                }
            }
            case READ_PAYLOAD: {
                try {
                    final Result<?> decodedPayload = decodePayload(ctx, buffer, this.mqttFixedHeader.messageType(), this.bytesRemainingInVariablePart, this.maxClientIdLength, this.variableHeader);
                    this.bytesRemainingInVariablePart -= ((Result<Object>)decodedPayload).numberOfBytesConsumed;
                    if (this.bytesRemainingInVariablePart != 0) {
                        throw new DecoderException("non-zero remaining payload bytes: " + this.bytesRemainingInVariablePart + " (" + this.mqttFixedHeader.messageType() + ')');
                    }
                    this.checkpoint(DecoderState.READ_FIXED_HEADER);
                    final MqttMessage message = MqttMessageFactory.newMessage(this.mqttFixedHeader, this.variableHeader, ((Result<Object>)decodedPayload).value);
                    this.mqttFixedHeader = null;
                    this.variableHeader = null;
                    out.add(message);
                    break;
                }
                catch (final Exception cause) {
                    out.add(this.invalidMessage(cause));
                }
            }
            case BAD_MESSAGE: {
                buffer.skipBytes(this.actualReadableBytes());
                break;
            }
            default: {
                throw new Error();
            }
        }
    }
    
    private MqttMessage invalidMessage(final Throwable cause) {
        this.checkpoint(DecoderState.BAD_MESSAGE);
        return MqttMessageFactory.newInvalidMessage(this.mqttFixedHeader, this.variableHeader, cause);
    }
    
    private static MqttFixedHeader decodeFixedHeader(final ChannelHandlerContext ctx, final ByteBuf buffer) {
        final short b1 = buffer.readUnsignedByte();
        final MqttMessageType messageType = MqttMessageType.valueOf(b1 >> 4);
        final boolean dupFlag = (b1 & 0x8) == 0x8;
        final int qosLevel = (b1 & 0x6) >> 1;
        final boolean retain = (b1 & 0x1) != 0x0;
        switch (messageType) {
            case PUBLISH: {
                if (qosLevel == 3) {
                    throw new DecoderException("Illegal QOS Level in fixed header of PUBLISH message (" + qosLevel + ')');
                }
                break;
            }
            case PUBREL:
            case SUBSCRIBE:
            case UNSUBSCRIBE: {
                if (dupFlag) {
                    throw new DecoderException("Illegal BIT 3 in fixed header of " + messageType + " message, must be 0, found 1");
                }
                if (qosLevel != 1) {
                    throw new DecoderException("Illegal QOS Level in fixed header of " + messageType + " message, must be 1, found " + qosLevel);
                }
                if (retain) {
                    throw new DecoderException("Illegal BIT 0 in fixed header of " + messageType + " message, must be 0, found 1");
                }
                break;
            }
            case AUTH:
            case CONNACK:
            case CONNECT:
            case DISCONNECT:
            case PINGREQ:
            case PINGRESP:
            case PUBACK:
            case PUBCOMP:
            case PUBREC:
            case SUBACK:
            case UNSUBACK: {
                if (dupFlag) {
                    throw new DecoderException("Illegal BIT 3 in fixed header of " + messageType + " message, must be 0, found 1");
                }
                if (qosLevel != 0) {
                    throw new DecoderException("Illegal BIT 2 or 1 in fixed header of " + messageType + " message, must be 0, found " + qosLevel);
                }
                if (retain) {
                    throw new DecoderException("Illegal BIT 0 in fixed header of " + messageType + " message, must be 0, found 1");
                }
                break;
            }
            default: {
                throw new DecoderException("Unknown message type, do not know how to validate fixed header");
            }
        }
        int remainingLength = 0;
        int multiplier = 1;
        int loops = 0;
        short digit;
        do {
            digit = buffer.readUnsignedByte();
            remainingLength += (digit & 0x7F) * multiplier;
            multiplier *= 128;
            ++loops;
        } while ((digit & 0x80) != 0x0 && loops < 4);
        if (loops == 4 && (digit & 0x80) != 0x0) {
            throw new DecoderException("remaining length exceeds 4 digits (" + messageType + ')');
        }
        final MqttFixedHeader decodedFixedHeader = new MqttFixedHeader(messageType, dupFlag, MqttQoS.valueOf(qosLevel), retain, remainingLength);
        return MqttCodecUtil.validateFixedHeader(ctx, MqttCodecUtil.resetUnusedFields(decodedFixedHeader));
    }
    
    private Result<?> decodeVariableHeader(final ChannelHandlerContext ctx, final ByteBuf buffer, final MqttFixedHeader mqttFixedHeader) {
        switch (mqttFixedHeader.messageType()) {
            case CONNECT: {
                return decodeConnectionVariableHeader(ctx, buffer);
            }
            case CONNACK: {
                return decodeConnAckVariableHeader(ctx, buffer);
            }
            case SUBSCRIBE:
            case UNSUBSCRIBE:
            case SUBACK:
            case UNSUBACK: {
                return decodeMessageIdAndPropertiesVariableHeader(ctx, buffer);
            }
            case PUBREL:
            case PUBACK:
            case PUBCOMP:
            case PUBREC: {
                return this.decodePubReplyMessage(buffer);
            }
            case PUBLISH: {
                return this.decodePublishVariableHeader(ctx, buffer, mqttFixedHeader);
            }
            case AUTH:
            case DISCONNECT: {
                return this.decodeReasonCodeAndPropertiesVariableHeader(buffer);
            }
            case PINGREQ:
            case PINGRESP: {
                return new Result<Object>(null, 0);
            }
            default: {
                throw new DecoderException("Unknown message type: " + mqttFixedHeader.messageType());
            }
        }
    }
    
    private static Result<MqttConnectVariableHeader> decodeConnectionVariableHeader(final ChannelHandlerContext ctx, final ByteBuf buffer) {
        final Result<String> protoString = decodeString(buffer);
        int numberOfBytesConsumed = ((Result<Object>)protoString).numberOfBytesConsumed;
        final byte protocolLevel = buffer.readByte();
        ++numberOfBytesConsumed;
        final MqttVersion version = MqttVersion.fromProtocolNameAndLevel((String)((Result<Object>)protoString).value, protocolLevel);
        MqttCodecUtil.setMqttVersion(ctx, version);
        final int b1 = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        final int keepAlive = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;
        final boolean hasUserName = (b1 & 0x80) == 0x80;
        final boolean hasPassword = (b1 & 0x40) == 0x40;
        final boolean willRetain = (b1 & 0x20) == 0x20;
        final int willQos = (b1 & 0x18) >> 3;
        final boolean willFlag = (b1 & 0x4) == 0x4;
        final boolean cleanSession = (b1 & 0x2) == 0x2;
        if (version == MqttVersion.MQTT_3_1_1 || version == MqttVersion.MQTT_5) {
            final boolean zeroReservedFlag = (b1 & 0x1) == 0x0;
            if (!zeroReservedFlag) {
                throw new DecoderException("non-zero reserved flag");
            }
        }
        MqttProperties properties;
        if (version == MqttVersion.MQTT_5) {
            final Result<MqttProperties> propertiesResult = decodeProperties(buffer);
            properties = (MqttProperties)((Result<Object>)propertiesResult).value;
            numberOfBytesConsumed += ((Result<Object>)propertiesResult).numberOfBytesConsumed;
        }
        else {
            properties = MqttProperties.NO_PROPERTIES;
        }
        final MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(version.protocolName(), version.protocolLevel(), hasUserName, hasPassword, willRetain, willQos, willFlag, cleanSession, keepAlive, properties);
        return new Result<MqttConnectVariableHeader>(mqttConnectVariableHeader, numberOfBytesConsumed);
    }
    
    private static Result<MqttConnAckVariableHeader> decodeConnAckVariableHeader(final ChannelHandlerContext ctx, final ByteBuf buffer) {
        final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
        final boolean sessionPresent = (buffer.readUnsignedByte() & 0x1) == 0x1;
        final byte returnCode = buffer.readByte();
        int numberOfBytesConsumed = 2;
        MqttProperties properties;
        if (mqttVersion == MqttVersion.MQTT_5) {
            final Result<MqttProperties> propertiesResult = decodeProperties(buffer);
            properties = (MqttProperties)((Result<Object>)propertiesResult).value;
            numberOfBytesConsumed += ((Result<Object>)propertiesResult).numberOfBytesConsumed;
        }
        else {
            properties = MqttProperties.NO_PROPERTIES;
        }
        final MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.valueOf(returnCode), sessionPresent, properties);
        return new Result<MqttConnAckVariableHeader>(mqttConnAckVariableHeader, numberOfBytesConsumed);
    }
    
    private static Result<MqttMessageIdAndPropertiesVariableHeader> decodeMessageIdAndPropertiesVariableHeader(final ChannelHandlerContext ctx, final ByteBuf buffer) {
        final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
        final int packetId = decodeMessageId(buffer);
        MqttMessageIdAndPropertiesVariableHeader mqttVariableHeader;
        int mqtt5Consumed;
        if (mqttVersion == MqttVersion.MQTT_5) {
            final Result<MqttProperties> properties = decodeProperties(buffer);
            mqttVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(packetId, (MqttProperties)((Result<Object>)properties).value);
            mqtt5Consumed = ((Result<Object>)properties).numberOfBytesConsumed;
        }
        else {
            mqttVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(packetId, MqttProperties.NO_PROPERTIES);
            mqtt5Consumed = 0;
        }
        return new Result<MqttMessageIdAndPropertiesVariableHeader>(mqttVariableHeader, 2 + mqtt5Consumed);
    }
    
    private Result<MqttPubReplyMessageVariableHeader> decodePubReplyMessage(final ByteBuf buffer) {
        final int packetId = decodeMessageId(buffer);
        final int packetIdNumberOfBytesConsumed = 2;
        MqttPubReplyMessageVariableHeader mqttPubAckVariableHeader;
        int consumed;
        if (this.bytesRemainingInVariablePart > 3) {
            final byte reasonCode = buffer.readByte();
            final Result<MqttProperties> properties = decodeProperties(buffer);
            mqttPubAckVariableHeader = new MqttPubReplyMessageVariableHeader(packetId, reasonCode, (MqttProperties)((Result<Object>)properties).value);
            consumed = 3 + ((Result<Object>)properties).numberOfBytesConsumed;
        }
        else if (this.bytesRemainingInVariablePart > 2) {
            final byte reasonCode = buffer.readByte();
            mqttPubAckVariableHeader = new MqttPubReplyMessageVariableHeader(packetId, reasonCode, MqttProperties.NO_PROPERTIES);
            consumed = 3;
        }
        else {
            mqttPubAckVariableHeader = new MqttPubReplyMessageVariableHeader(packetId, (byte)0, MqttProperties.NO_PROPERTIES);
            consumed = 2;
        }
        return new Result<MqttPubReplyMessageVariableHeader>(mqttPubAckVariableHeader, consumed);
    }
    
    private Result<MqttReasonCodeAndPropertiesVariableHeader> decodeReasonCodeAndPropertiesVariableHeader(final ByteBuf buffer) {
        byte reasonCode;
        MqttProperties properties;
        int consumed;
        if (this.bytesRemainingInVariablePart > 1) {
            reasonCode = buffer.readByte();
            final Result<MqttProperties> propertiesResult = decodeProperties(buffer);
            properties = (MqttProperties)((Result<Object>)propertiesResult).value;
            consumed = 1 + ((Result<Object>)propertiesResult).numberOfBytesConsumed;
        }
        else if (this.bytesRemainingInVariablePart > 0) {
            reasonCode = buffer.readByte();
            properties = MqttProperties.NO_PROPERTIES;
            consumed = 1;
        }
        else {
            reasonCode = 0;
            properties = MqttProperties.NO_PROPERTIES;
            consumed = 0;
        }
        final MqttReasonCodeAndPropertiesVariableHeader mqttReasonAndPropsVariableHeader = new MqttReasonCodeAndPropertiesVariableHeader(reasonCode, properties);
        return new Result<MqttReasonCodeAndPropertiesVariableHeader>(mqttReasonAndPropsVariableHeader, consumed);
    }
    
    private Result<MqttPublishVariableHeader> decodePublishVariableHeader(final ChannelHandlerContext ctx, final ByteBuf buffer, final MqttFixedHeader mqttFixedHeader) {
        final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
        final Result<String> decodedTopic = decodeString(buffer);
        if (!MqttCodecUtil.isValidPublishTopicName((String)((Result<Object>)decodedTopic).value)) {
            throw new DecoderException("invalid publish topic name: " + (String)((Result<Object>)decodedTopic).value + " (contains wildcards)");
        }
        int numberOfBytesConsumed = ((Result<Object>)decodedTopic).numberOfBytesConsumed;
        int messageId = -1;
        if (mqttFixedHeader.qosLevel().value() > 0) {
            messageId = decodeMessageId(buffer);
            numberOfBytesConsumed += 2;
        }
        MqttProperties properties;
        if (mqttVersion == MqttVersion.MQTT_5) {
            final Result<MqttProperties> propertiesResult = decodeProperties(buffer);
            properties = (MqttProperties)((Result<Object>)propertiesResult).value;
            numberOfBytesConsumed += ((Result<Object>)propertiesResult).numberOfBytesConsumed;
        }
        else {
            properties = MqttProperties.NO_PROPERTIES;
        }
        final MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader((String)((Result<Object>)decodedTopic).value, messageId, properties);
        return new Result<MqttPublishVariableHeader>(mqttPublishVariableHeader, numberOfBytesConsumed);
    }
    
    private static int decodeMessageId(final ByteBuf buffer) {
        final int messageId = decodeMsbLsb(buffer);
        if (!MqttCodecUtil.isValidMessageId(messageId)) {
            throw new DecoderException("invalid messageId: " + messageId);
        }
        return messageId;
    }
    
    private static Result<?> decodePayload(final ChannelHandlerContext ctx, final ByteBuf buffer, final MqttMessageType messageType, final int bytesRemainingInVariablePart, final int maxClientIdLength, final Object variableHeader) {
        switch (messageType) {
            case CONNECT: {
                return decodeConnectionPayload(buffer, maxClientIdLength, (MqttConnectVariableHeader)variableHeader);
            }
            case SUBSCRIBE: {
                return decodeSubscribePayload(buffer, bytesRemainingInVariablePart);
            }
            case SUBACK: {
                return decodeSubackPayload(buffer, bytesRemainingInVariablePart);
            }
            case UNSUBSCRIBE: {
                return decodeUnsubscribePayload(buffer, bytesRemainingInVariablePart);
            }
            case UNSUBACK: {
                return decodeUnsubAckPayload(ctx, buffer, bytesRemainingInVariablePart);
            }
            case PUBLISH: {
                return decodePublishPayload(buffer, bytesRemainingInVariablePart);
            }
            default: {
                return new Result<Object>(null, 0);
            }
        }
    }
    
    private static Result<MqttConnectPayload> decodeConnectionPayload(final ByteBuf buffer, final int maxClientIdLength, final MqttConnectVariableHeader mqttConnectVariableHeader) {
        final Result<String> decodedClientId = decodeString(buffer);
        final String decodedClientIdValue = (String)((Result<Object>)decodedClientId).value;
        final MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(mqttConnectVariableHeader.name(), (byte)mqttConnectVariableHeader.version());
        if (!MqttCodecUtil.isValidClientId(mqttVersion, maxClientIdLength, decodedClientIdValue)) {
            throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + decodedClientIdValue);
        }
        int numberOfBytesConsumed = ((Result<Object>)decodedClientId).numberOfBytesConsumed;
        Result<String> decodedWillTopic = null;
        byte[] decodedWillMessage = null;
        MqttProperties willProperties;
        if (mqttConnectVariableHeader.isWillFlag()) {
            if (mqttVersion == MqttVersion.MQTT_5) {
                final Result<MqttProperties> propertiesResult = decodeProperties(buffer);
                willProperties = (MqttProperties)((Result<Object>)propertiesResult).value;
                numberOfBytesConsumed += ((Result<Object>)propertiesResult).numberOfBytesConsumed;
            }
            else {
                willProperties = MqttProperties.NO_PROPERTIES;
            }
            decodedWillTopic = decodeString(buffer, 0, 32767);
            numberOfBytesConsumed += ((Result<Object>)decodedWillTopic).numberOfBytesConsumed;
            decodedWillMessage = decodeByteArray(buffer);
            numberOfBytesConsumed += decodedWillMessage.length + 2;
        }
        else {
            willProperties = MqttProperties.NO_PROPERTIES;
        }
        Result<String> decodedUserName = null;
        byte[] decodedPassword = null;
        if (mqttConnectVariableHeader.hasUserName()) {
            decodedUserName = decodeString(buffer);
            numberOfBytesConsumed += ((Result<Object>)decodedUserName).numberOfBytesConsumed;
        }
        if (mqttConnectVariableHeader.hasPassword()) {
            decodedPassword = decodeByteArray(buffer);
            numberOfBytesConsumed += decodedPassword.length + 2;
        }
        final MqttConnectPayload mqttConnectPayload = new MqttConnectPayload((String)((Result<Object>)decodedClientId).value, willProperties, (decodedWillTopic != null) ? ((String)((Result<Object>)decodedWillTopic).value) : null, decodedWillMessage, (decodedUserName != null) ? ((String)((Result<Object>)decodedUserName).value) : null, decodedPassword);
        return new Result<MqttConnectPayload>(mqttConnectPayload, numberOfBytesConsumed);
    }
    
    private static Result<MqttSubscribePayload> decodeSubscribePayload(final ByteBuf buffer, final int bytesRemainingInVariablePart) {
        final List<MqttTopicSubscription> subscribeTopics = new ArrayList<MqttTopicSubscription>();
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            final Result<String> decodedTopicName = decodeString(buffer);
            numberOfBytesConsumed += ((Result<Object>)decodedTopicName).numberOfBytesConsumed;
            final short optionByte = buffer.readUnsignedByte();
            final MqttQoS qos = MqttQoS.valueOf(optionByte & 0x3);
            final boolean noLocal = (optionByte & 0x4) >> 2 == 1;
            final boolean retainAsPublished = (optionByte & 0x8) >> 3 == 1;
            final MqttSubscriptionOption.RetainedHandlingPolicy retainHandling = MqttSubscriptionOption.RetainedHandlingPolicy.valueOf((optionByte & 0x30) >> 4);
            final MqttSubscriptionOption subscriptionOption = new MqttSubscriptionOption(qos, noLocal, retainAsPublished, retainHandling);
            ++numberOfBytesConsumed;
            subscribeTopics.add(new MqttTopicSubscription((String)((Result<Object>)decodedTopicName).value, subscriptionOption));
        }
        return new Result<MqttSubscribePayload>(new MqttSubscribePayload(subscribeTopics), numberOfBytesConsumed);
    }
    
    private static Result<MqttSubAckPayload> decodeSubackPayload(final ByteBuf buffer, final int bytesRemainingInVariablePart) {
        final List<Integer> grantedQos = new ArrayList<Integer>(bytesRemainingInVariablePart);
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            final int reasonCode = buffer.readUnsignedByte();
            ++numberOfBytesConsumed;
            grantedQos.add(reasonCode);
        }
        return new Result<MqttSubAckPayload>(new MqttSubAckPayload(grantedQos), numberOfBytesConsumed);
    }
    
    private static Result<MqttUnsubAckPayload> decodeUnsubAckPayload(final ChannelHandlerContext ctx, final ByteBuf buffer, final int bytesRemainingInVariablePart) {
        final List<Short> reasonCodes = new ArrayList<Short>(bytesRemainingInVariablePart);
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            final short reasonCode = buffer.readUnsignedByte();
            ++numberOfBytesConsumed;
            reasonCodes.add(reasonCode);
        }
        return new Result<MqttUnsubAckPayload>(new MqttUnsubAckPayload(reasonCodes), numberOfBytesConsumed);
    }
    
    private static Result<MqttUnsubscribePayload> decodeUnsubscribePayload(final ByteBuf buffer, final int bytesRemainingInVariablePart) {
        final List<String> unsubscribeTopics = new ArrayList<String>();
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            final Result<String> decodedTopicName = decodeString(buffer);
            numberOfBytesConsumed += ((Result<Object>)decodedTopicName).numberOfBytesConsumed;
            unsubscribeTopics.add((String)((Result<Object>)decodedTopicName).value);
        }
        return new Result<MqttUnsubscribePayload>(new MqttUnsubscribePayload(unsubscribeTopics), numberOfBytesConsumed);
    }
    
    private static Result<ByteBuf> decodePublishPayload(final ByteBuf buffer, final int bytesRemainingInVariablePart) {
        final ByteBuf b = buffer.readRetainedSlice(bytesRemainingInVariablePart);
        return new Result<ByteBuf>(b, bytesRemainingInVariablePart);
    }
    
    private static Result<String> decodeString(final ByteBuf buffer) {
        return decodeString(buffer, 0, Integer.MAX_VALUE);
    }
    
    private static Result<String> decodeString(final ByteBuf buffer, final int minBytes, final int maxBytes) {
        final int size = decodeMsbLsb(buffer);
        int numberOfBytesConsumed = 2;
        if (size < minBytes || size > maxBytes) {
            buffer.skipBytes(size);
            numberOfBytesConsumed += size;
            return new Result<String>(null, numberOfBytesConsumed);
        }
        final String s = buffer.toString(buffer.readerIndex(), size, CharsetUtil.UTF_8);
        buffer.skipBytes(size);
        numberOfBytesConsumed += size;
        return new Result<String>(s, numberOfBytesConsumed);
    }
    
    private static byte[] decodeByteArray(final ByteBuf buffer) {
        final int size = decodeMsbLsb(buffer);
        final byte[] bytes = new byte[size];
        buffer.readBytes(bytes);
        return bytes;
    }
    
    private static long packInts(final int a, final int b) {
        return (long)a << 32 | ((long)b & 0xFFFFFFFFL);
    }
    
    private static int unpackA(final long ints) {
        return (int)(ints >> 32);
    }
    
    private static int unpackB(final long ints) {
        return (int)ints;
    }
    
    private static int decodeMsbLsb(final ByteBuf buffer) {
        final int min = 0;
        final int max = 65535;
        final short msbSize = buffer.readUnsignedByte();
        final short lsbSize = buffer.readUnsignedByte();
        int result = msbSize << 8 | lsbSize;
        if (result < min || result > max) {
            result = -1;
        }
        return result;
    }
    
    private static long decodeVariableByteInteger(final ByteBuf buffer) {
        int remainingLength = 0;
        int multiplier = 1;
        int loops = 0;
        short digit;
        do {
            digit = buffer.readUnsignedByte();
            remainingLength += (digit & 0x7F) * multiplier;
            multiplier *= 128;
            ++loops;
        } while ((digit & 0x80) != 0x0 && loops < 4);
        if (loops == 4 && (digit & 0x80) != 0x0) {
            throw new DecoderException("MQTT protocol limits Remaining Length to 4 bytes");
        }
        return packInts(remainingLength, loops);
    }
    
    private static Result<MqttProperties> decodeProperties(final ByteBuf buffer) {
        final long propertiesLength = decodeVariableByteInteger(buffer);
        final int totalPropertiesLength = unpackA(propertiesLength);
        int numberOfBytesConsumed = unpackB(propertiesLength);
        final MqttProperties decodedProperties = new MqttProperties();
        while (numberOfBytesConsumed < totalPropertiesLength) {
            final long propertyId = decodeVariableByteInteger(buffer);
            final int propertyIdValue = unpackA(propertyId);
            numberOfBytesConsumed += unpackB(propertyId);
            final MqttProperties.MqttPropertyType propertyType = MqttProperties.MqttPropertyType.valueOf(propertyIdValue);
            switch (propertyType) {
                case PAYLOAD_FORMAT_INDICATOR:
                case REQUEST_PROBLEM_INFORMATION:
                case REQUEST_RESPONSE_INFORMATION:
                case MAXIMUM_QOS:
                case RETAIN_AVAILABLE:
                case WILDCARD_SUBSCRIPTION_AVAILABLE:
                case SUBSCRIPTION_IDENTIFIER_AVAILABLE:
                case SHARED_SUBSCRIPTION_AVAILABLE: {
                    final int b1 = buffer.readUnsignedByte();
                    ++numberOfBytesConsumed;
                    decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, b1));
                    continue;
                }
                case SERVER_KEEP_ALIVE:
                case RECEIVE_MAXIMUM:
                case TOPIC_ALIAS_MAXIMUM:
                case TOPIC_ALIAS: {
                    final int int2BytesResult = decodeMsbLsb(buffer);
                    numberOfBytesConsumed += 2;
                    decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, int2BytesResult));
                    continue;
                }
                case PUBLICATION_EXPIRY_INTERVAL:
                case SESSION_EXPIRY_INTERVAL:
                case WILL_DELAY_INTERVAL:
                case MAXIMUM_PACKET_SIZE: {
                    final int maxPacketSize = buffer.readInt();
                    numberOfBytesConsumed += 4;
                    decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, maxPacketSize));
                    continue;
                }
                case SUBSCRIPTION_IDENTIFIER: {
                    final long vbIntegerResult = decodeVariableByteInteger(buffer);
                    numberOfBytesConsumed += unpackB(vbIntegerResult);
                    decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, unpackA(vbIntegerResult)));
                    continue;
                }
                case CONTENT_TYPE:
                case RESPONSE_TOPIC:
                case ASSIGNED_CLIENT_IDENTIFIER:
                case AUTHENTICATION_METHOD:
                case RESPONSE_INFORMATION:
                case SERVER_REFERENCE:
                case REASON_STRING: {
                    final Result<String> stringResult = decodeString(buffer);
                    numberOfBytesConsumed += ((Result<Object>)stringResult).numberOfBytesConsumed;
                    decodedProperties.add(new MqttProperties.StringProperty(propertyIdValue, (String)((Result<Object>)stringResult).value));
                    continue;
                }
                case USER_PROPERTY: {
                    final Result<String> keyResult = decodeString(buffer);
                    final Result<String> valueResult = decodeString(buffer);
                    numberOfBytesConsumed += ((Result<Object>)keyResult).numberOfBytesConsumed;
                    numberOfBytesConsumed += ((Result<Object>)valueResult).numberOfBytesConsumed;
                    decodedProperties.add(new MqttProperties.UserProperty((String)((Result<Object>)keyResult).value, (String)((Result<Object>)valueResult).value));
                    continue;
                }
                case CORRELATION_DATA:
                case AUTHENTICATION_DATA: {
                    final byte[] binaryDataResult = decodeByteArray(buffer);
                    numberOfBytesConsumed += binaryDataResult.length + 2;
                    decodedProperties.add(new MqttProperties.BinaryProperty(propertyIdValue, binaryDataResult));
                    continue;
                }
                default: {
                    throw new DecoderException("Unknown property type: " + propertyType);
                }
            }
        }
        return new Result<MqttProperties>(decodedProperties, numberOfBytesConsumed);
    }
    
    enum DecoderState
    {
        READ_FIXED_HEADER, 
        READ_VARIABLE_HEADER, 
        READ_PAYLOAD, 
        BAD_MESSAGE;
    }
    
    private static final class Result<T>
    {
        private final T value;
        private final int numberOfBytesConsumed;
        
        Result(final T value, final int numberOfBytesConsumed) {
            this.value = value;
            this.numberOfBytesConsumed = numberOfBytesConsumed;
        }
    }
}
