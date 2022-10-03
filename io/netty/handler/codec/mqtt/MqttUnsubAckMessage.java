package io.netty.handler.codec.mqtt;

public final class MqttUnsubAckMessage extends MqttMessage
{
    public MqttUnsubAckMessage(final MqttFixedHeader mqttFixedHeader, final MqttMessageIdAndPropertiesVariableHeader variableHeader, final MqttUnsubAckPayload payload) {
        super(mqttFixedHeader, variableHeader, MqttUnsubAckPayload.withEmptyDefaults(payload));
    }
    
    public MqttUnsubAckMessage(final MqttFixedHeader mqttFixedHeader, final MqttMessageIdVariableHeader variableHeader, final MqttUnsubAckPayload payload) {
        this(mqttFixedHeader, fallbackVariableHeader(variableHeader), payload);
    }
    
    public MqttUnsubAckMessage(final MqttFixedHeader mqttFixedHeader, final MqttMessageIdVariableHeader variableHeader) {
        this(mqttFixedHeader, variableHeader, null);
    }
    
    private static MqttMessageIdAndPropertiesVariableHeader fallbackVariableHeader(final MqttMessageIdVariableHeader variableHeader) {
        if (variableHeader instanceof MqttMessageIdAndPropertiesVariableHeader) {
            return (MqttMessageIdAndPropertiesVariableHeader)variableHeader;
        }
        return new MqttMessageIdAndPropertiesVariableHeader(variableHeader.messageId(), MqttProperties.NO_PROPERTIES);
    }
    
    @Override
    public MqttMessageIdVariableHeader variableHeader() {
        return (MqttMessageIdVariableHeader)super.variableHeader();
    }
    
    public MqttMessageIdAndPropertiesVariableHeader idAndPropertiesVariableHeader() {
        return (MqttMessageIdAndPropertiesVariableHeader)super.variableHeader();
    }
    
    @Override
    public MqttUnsubAckPayload payload() {
        return (MqttUnsubAckPayload)super.payload();
    }
}
