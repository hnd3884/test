package io.netty.handler.codec.mqtt;

public final class MqttSubscribeMessage extends MqttMessage
{
    public MqttSubscribeMessage(final MqttFixedHeader mqttFixedHeader, final MqttMessageIdAndPropertiesVariableHeader variableHeader, final MqttSubscribePayload payload) {
        super(mqttFixedHeader, variableHeader, payload);
    }
    
    public MqttSubscribeMessage(final MqttFixedHeader mqttFixedHeader, final MqttMessageIdVariableHeader variableHeader, final MqttSubscribePayload payload) {
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
    public MqttSubscribePayload payload() {
        return (MqttSubscribePayload)super.payload();
    }
}
