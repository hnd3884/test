package io.netty.handler.codec.mqtt;

public final class MqttSubscriptionOption
{
    private final MqttQoS qos;
    private final boolean noLocal;
    private final boolean retainAsPublished;
    private final RetainedHandlingPolicy retainHandling;
    
    public static MqttSubscriptionOption onlyFromQos(final MqttQoS qos) {
        return new MqttSubscriptionOption(qos, false, false, RetainedHandlingPolicy.SEND_AT_SUBSCRIBE);
    }
    
    public MqttSubscriptionOption(final MqttQoS qos, final boolean noLocal, final boolean retainAsPublished, final RetainedHandlingPolicy retainHandling) {
        this.qos = qos;
        this.noLocal = noLocal;
        this.retainAsPublished = retainAsPublished;
        this.retainHandling = retainHandling;
    }
    
    public MqttQoS qos() {
        return this.qos;
    }
    
    public boolean isNoLocal() {
        return this.noLocal;
    }
    
    public boolean isRetainAsPublished() {
        return this.retainAsPublished;
    }
    
    public RetainedHandlingPolicy retainHandling() {
        return this.retainHandling;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MqttSubscriptionOption that = (MqttSubscriptionOption)o;
        return this.noLocal == that.noLocal && this.retainAsPublished == that.retainAsPublished && this.qos == that.qos && this.retainHandling == that.retainHandling;
    }
    
    @Override
    public int hashCode() {
        int result = this.qos.hashCode();
        result = 31 * result + (this.noLocal ? 1 : 0);
        result = 31 * result + (this.retainAsPublished ? 1 : 0);
        result = 31 * result + this.retainHandling.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "SubscriptionOption[qos=" + this.qos + ", noLocal=" + this.noLocal + ", retainAsPublished=" + this.retainAsPublished + ", retainHandling=" + this.retainHandling + ']';
    }
    
    public enum RetainedHandlingPolicy
    {
        SEND_AT_SUBSCRIBE(0), 
        SEND_AT_SUBSCRIBE_IF_NOT_YET_EXISTS(1), 
        DONT_SEND_AT_SUBSCRIBE(2);
        
        private final int value;
        
        private RetainedHandlingPolicy(final int value) {
            this.value = value;
        }
        
        public int value() {
            return this.value;
        }
        
        public static RetainedHandlingPolicy valueOf(final int value) {
            switch (value) {
                case 0: {
                    return RetainedHandlingPolicy.SEND_AT_SUBSCRIBE;
                }
                case 1: {
                    return RetainedHandlingPolicy.SEND_AT_SUBSCRIBE_IF_NOT_YET_EXISTS;
                }
                case 2: {
                    return RetainedHandlingPolicy.DONT_SEND_AT_SUBSCRIBE;
                }
                default: {
                    throw new IllegalArgumentException("invalid RetainedHandlingPolicy: " + value);
                }
            }
        }
    }
}
