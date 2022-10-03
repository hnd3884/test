package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.DecoderException;
import io.netty.util.Attribute;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

final class MqttCodecUtil
{
    private static final char[] TOPIC_WILDCARDS;
    static final AttributeKey<MqttVersion> MQTT_VERSION_KEY;
    
    static MqttVersion getMqttVersion(final ChannelHandlerContext ctx) {
        final Attribute<MqttVersion> attr = ctx.channel().attr(MqttCodecUtil.MQTT_VERSION_KEY);
        final MqttVersion version = attr.get();
        if (version == null) {
            return MqttVersion.MQTT_3_1_1;
        }
        return version;
    }
    
    static void setMqttVersion(final ChannelHandlerContext ctx, final MqttVersion version) {
        final Attribute<MqttVersion> attr = ctx.channel().attr(MqttCodecUtil.MQTT_VERSION_KEY);
        attr.set(version);
    }
    
    static boolean isValidPublishTopicName(final String topicName) {
        for (final char c : MqttCodecUtil.TOPIC_WILDCARDS) {
            if (topicName.indexOf(c) >= 0) {
                return false;
            }
        }
        return true;
    }
    
    static boolean isValidMessageId(final int messageId) {
        return messageId != 0;
    }
    
    static boolean isValidClientId(final MqttVersion mqttVersion, final int maxClientIdLength, final String clientId) {
        if (mqttVersion == MqttVersion.MQTT_3_1) {
            return clientId != null && clientId.length() >= 1 && clientId.length() <= maxClientIdLength;
        }
        if (mqttVersion == MqttVersion.MQTT_3_1_1 || mqttVersion == MqttVersion.MQTT_5) {
            return clientId != null;
        }
        throw new IllegalArgumentException(mqttVersion + " is unknown mqtt version");
    }
    
    static MqttFixedHeader validateFixedHeader(final ChannelHandlerContext ctx, final MqttFixedHeader mqttFixedHeader) {
        switch (mqttFixedHeader.messageType()) {
            case PUBREL:
            case SUBSCRIBE:
            case UNSUBSCRIBE: {
                if (mqttFixedHeader.qosLevel() != MqttQoS.AT_LEAST_ONCE) {
                    throw new DecoderException(mqttFixedHeader.messageType().name() + " message must have QoS 1");
                }
                return mqttFixedHeader;
            }
            case AUTH: {
                if (getMqttVersion(ctx) != MqttVersion.MQTT_5) {
                    throw new DecoderException("AUTH message requires at least MQTT 5");
                }
                return mqttFixedHeader;
            }
            default: {
                return mqttFixedHeader;
            }
        }
    }
    
    static MqttFixedHeader resetUnusedFields(final MqttFixedHeader mqttFixedHeader) {
        switch (mqttFixedHeader.messageType()) {
            case CONNECT:
            case CONNACK:
            case PUBACK:
            case PUBREC:
            case PUBCOMP:
            case SUBACK:
            case UNSUBACK:
            case PINGREQ:
            case PINGRESP:
            case DISCONNECT: {
                if (mqttFixedHeader.isDup() || mqttFixedHeader.qosLevel() != MqttQoS.AT_MOST_ONCE || mqttFixedHeader.isRetain()) {
                    return new MqttFixedHeader(mqttFixedHeader.messageType(), false, MqttQoS.AT_MOST_ONCE, false, mqttFixedHeader.remainingLength());
                }
                return mqttFixedHeader;
            }
            case PUBREL:
            case SUBSCRIBE:
            case UNSUBSCRIBE: {
                if (mqttFixedHeader.isRetain()) {
                    return new MqttFixedHeader(mqttFixedHeader.messageType(), mqttFixedHeader.isDup(), mqttFixedHeader.qosLevel(), false, mqttFixedHeader.remainingLength());
                }
                return mqttFixedHeader;
            }
            default: {
                return mqttFixedHeader;
            }
        }
    }
    
    private MqttCodecUtil() {
    }
    
    static {
        TOPIC_WILDCARDS = new char[] { '#', '+' };
        MQTT_VERSION_KEY = AttributeKey.valueOf("NETTY_CODEC_MQTT_VERSION");
    }
}
