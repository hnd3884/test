package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;
import java.util.Collections;
import java.util.List;

public final class MqttUnsubscribePayload
{
    private final List<String> topics;
    
    public MqttUnsubscribePayload(final List<String> topics) {
        this.topics = Collections.unmodifiableList((List<? extends String>)topics);
    }
    
    public List<String> topics() {
        return this.topics;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(StringUtil.simpleClassName(this)).append('[');
        for (int i = 0; i < this.topics.size(); ++i) {
            builder.append("topicName = ").append(this.topics.get(i)).append(", ");
        }
        if (!this.topics.isEmpty()) {
            builder.setLength(builder.length() - 2);
        }
        return builder.append("]").toString();
    }
}
