package io.netty.handler.codec.mqtt;

public final class MqttUnsubscribeMessage extends MqttMessage
{
    public MqttUnsubscribeMessage(final MqttFixedHeader mqttFixedHeader, final MqttMessageIdAndPropertiesVariableHeader variableHeader, final MqttUnsubscribePayload payload) {
        super(mqttFixedHeader, variableHeader, payload);
    }
    
    public MqttUnsubscribeMessage(final MqttFixedHeader mqttFixedHeader, final MqttMessageIdVariableHeader variableHeader, final MqttUnsubscribePayload payload) {
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
    public MqttUnsubscribePayload payload() {
        return (MqttUnsubscribePayload)super.payload();
    }
}
