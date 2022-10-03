package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

public final class MqttTopicSubscription
{
    private final String topicFilter;
    private final MqttSubscriptionOption option;
    
    public MqttTopicSubscription(final String topicFilter, final MqttQoS qualityOfService) {
        this.topicFilter = topicFilter;
        this.option = MqttSubscriptionOption.onlyFromQos(qualityOfService);
    }
    
    public MqttTopicSubscription(final String topicFilter, final MqttSubscriptionOption option) {
        this.topicFilter = topicFilter;
        this.option = option;
    }
    
    public String topicName() {
        return this.topicFilter;
    }
    
    public MqttQoS qualityOfService() {
        return this.option.qos();
    }
    
    public MqttSubscriptionOption option() {
        return this.option;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "topicFilter=" + this.topicFilter + ", option=" + this.option + ']';
    }
}
