package com.turo.pushy.apns;

import java.util.Objects;
import io.netty.util.AttributeKey;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.Channel;

class AugmentingReflectiveChannelFactory<T extends Channel, A> extends ReflectiveChannelFactory<T>
{
    private final AttributeKey<A> attributeKey;
    private final A attributeValue;
    
    AugmentingReflectiveChannelFactory(final Class<? extends T> channelClass, final AttributeKey<A> attributeKey, final A attributeValue) {
        super((Class)channelClass);
        Objects.requireNonNull(attributeKey, "Attribute key must not be null.");
        this.attributeKey = attributeKey;
        this.attributeValue = attributeValue;
    }
    
    public T newChannel() {
        final T channel = (T)super.newChannel();
        channel.attr((AttributeKey)this.attributeKey).set((Object)this.attributeValue);
        return channel;
    }
}
