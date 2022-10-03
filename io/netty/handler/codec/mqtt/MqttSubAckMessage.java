package io.netty.handler.codec.mqtt;

public final class MqttSubAckMessage extends MqttMessage
{
    public MqttSubAckMessage(final MqttFixedHeader mqttFixedHeader, final MqttMessageIdAndPropertiesVariableHeader variableHeader, final MqttSubAckPayload payload) {
        super(mqttFixedHeader, variableHeader, payload);
    }
    
    public MqttSubAckMessage(final MqttFixedHeader mqttFixedHeader, final MqttMessageIdVariableHeader variableHeader, final MqttSubAckPayload payload) {
        this(mqttFixedHeader, variableHeader.withDefaultEmptyProperties(), payload);
    }
    
    @Override
    public MqttMessageIdVariableHeader variableHeader() {
        return (MqttMessageIdVariableHeader)super.variableHeader();
    }
    
    public MqttMessageIdAndPropertiesVariableHeader idAndPropertiesVariableHeader() {
        return (MqttMessageIdAndPropertiesVariableHeader)super.variableHeader();
    }
    
    @Override
    public MqttSubAckPayload payload() {
        return (MqttSubAckPayload)super.payload();
    }
}
