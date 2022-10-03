package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

public final class MqttMessageIdAndPropertiesVariableHeader extends MqttMessageIdVariableHeader
{
    private final MqttProperties properties;
    
    public MqttMessageIdAndPropertiesVariableHeader(final int messageId, final MqttProperties properties) {
        super(messageId);
        if (messageId < 1 || messageId > 65535) {
            throw new IllegalArgumentException("messageId: " + messageId + " (expected: 1 ~ 65535)");
        }
        this.properties = MqttProperties.withEmptyDefaults(properties);
    }
    
    public MqttProperties properties() {
        return this.properties;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "[messageId=" + this.messageId() + ", properties=" + this.properties + ']';
    }
    
    @Override
    MqttMessageIdAndPropertiesVariableHeader withDefaultEmptyProperties() {
        return this;
    }
}
