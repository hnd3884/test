package io.netty.handler.codec.mqtt;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;

public enum MqttVersion
{
    MQTT_3_1("MQIsdp", (byte)3), 
    MQTT_3_1_1("MQTT", (byte)4), 
    MQTT_5("MQTT", (byte)5);
    
    private final String name;
    private final byte level;
    
    private MqttVersion(final String protocolName, final byte protocolLevel) {
        this.name = ObjectUtil.checkNotNull(protocolName, "protocolName");
        this.level = protocolLevel;
    }
    
    public String protocolName() {
        return this.name;
    }
    
    public byte[] protocolNameBytes() {
        return this.name.getBytes(CharsetUtil.UTF_8);
    }
    
    public byte protocolLevel() {
        return this.level;
    }
    
    public static MqttVersion fromProtocolNameAndLevel(final String protocolName, final byte protocolLevel) {
        MqttVersion mv = null;
        switch (protocolLevel) {
            case 3: {
                mv = MqttVersion.MQTT_3_1;
                break;
            }
            case 4: {
                mv = MqttVersion.MQTT_3_1_1;
                break;
            }
            case 5: {
                mv = MqttVersion.MQTT_5;
                break;
            }
        }
        if (mv == null) {
            throw new MqttUnacceptableProtocolVersionException(protocolName + " is an unknown protocol name");
        }
        if (mv.name.equals(protocolName)) {
            return mv;
        }
        throw new MqttUnacceptableProtocolVersionException(protocolName + " and " + protocolLevel + " don't match");
    }
}
