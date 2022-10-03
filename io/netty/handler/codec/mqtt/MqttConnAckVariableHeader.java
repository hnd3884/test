package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

public final class MqttConnAckVariableHeader
{
    private final MqttConnectReturnCode connectReturnCode;
    private final boolean sessionPresent;
    private final MqttProperties properties;
    
    public MqttConnAckVariableHeader(final MqttConnectReturnCode connectReturnCode, final boolean sessionPresent) {
        this(connectReturnCode, sessionPresent, MqttProperties.NO_PROPERTIES);
    }
    
    public MqttConnAckVariableHeader(final MqttConnectReturnCode connectReturnCode, final boolean sessionPresent, final MqttProperties properties) {
        this.connectReturnCode = connectReturnCode;
        this.sessionPresent = sessionPresent;
        this.properties = MqttProperties.withEmptyDefaults(properties);
    }
    
    public MqttConnectReturnCode connectReturnCode() {
        return this.connectReturnCode;
    }
    
    public boolean isSessionPresent() {
        return this.sessionPresent;
    }
    
    public MqttProperties properties() {
        return this.properties;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "connectReturnCode=" + this.connectReturnCode + ", sessionPresent=" + this.sessionPresent + ']';
    }
}
