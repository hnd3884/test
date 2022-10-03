package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;
import io.netty.handler.codec.DecoderResult;

public class MqttMessage
{
    private final MqttFixedHeader mqttFixedHeader;
    private final Object variableHeader;
    private final Object payload;
    private final DecoderResult decoderResult;
    public static final MqttMessage PINGREQ;
    public static final MqttMessage PINGRESP;
    public static final MqttMessage DISCONNECT;
    
    public MqttMessage(final MqttFixedHeader mqttFixedHeader) {
        this(mqttFixedHeader, null, null);
    }
    
    public MqttMessage(final MqttFixedHeader mqttFixedHeader, final Object variableHeader) {
        this(mqttFixedHeader, variableHeader, null);
    }
    
    public MqttMessage(final MqttFixedHeader mqttFixedHeader, final Object variableHeader, final Object payload) {
        this(mqttFixedHeader, variableHeader, payload, DecoderResult.SUCCESS);
    }
    
    public MqttMessage(final MqttFixedHeader mqttFixedHeader, final Object variableHeader, final Object payload, final DecoderResult decoderResult) {
        this.mqttFixedHeader = mqttFixedHeader;
        this.variableHeader = variableHeader;
        this.payload = payload;
        this.decoderResult = decoderResult;
    }
    
    public MqttFixedHeader fixedHeader() {
        return this.mqttFixedHeader;
    }
    
    public Object variableHeader() {
        return this.variableHeader;
    }
    
    public Object payload() {
        return this.payload;
    }
    
    public DecoderResult decoderResult() {
        return this.decoderResult;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "fixedHeader=" + ((this.fixedHeader() != null) ? this.fixedHeader().toString() : "") + ", variableHeader=" + ((this.variableHeader() != null) ? this.variableHeader.toString() : "") + ", payload=" + ((this.payload() != null) ? this.payload.toString() : "") + ']';
    }
    
    static {
        PINGREQ = new MqttMessage(new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0));
        PINGRESP = new MqttMessage(new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0));
        DISCONNECT = new MqttMessage(new MqttFixedHeader(MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0));
    }
}
