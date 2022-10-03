package io.netty.handler.codec.mqtt;

public enum MqttQoS
{
    AT_MOST_ONCE(0), 
    AT_LEAST_ONCE(1), 
    EXACTLY_ONCE(2), 
    FAILURE(128);
    
    private final int value;
    
    private MqttQoS(final int value) {
        this.value = value;
    }
    
    public int value() {
        return this.value;
    }
    
    public static MqttQoS valueOf(final int value) {
        switch (value) {
            case 0: {
                return MqttQoS.AT_MOST_ONCE;
            }
            case 1: {
                return MqttQoS.AT_LEAST_ONCE;
            }
            case 2: {
                return MqttQoS.EXACTLY_ONCE;
            }
            case 128: {
                return MqttQoS.FAILURE;
            }
            default: {
                throw new IllegalArgumentException("invalid QoS: " + value);
            }
        }
    }
}
