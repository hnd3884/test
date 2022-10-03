package com.turo.pushy.apns;

import io.netty.util.concurrent.EventExecutor;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;
import io.netty.util.concurrent.DefaultPromise;

class PushNotificationPromise<P extends ApnsPushNotification, V> extends DefaultPromise<V> implements PushNotificationFuture<P, V>
{
    private final P pushNotification;
    
    PushNotificationPromise(final EventExecutor eventExecutor, final P pushNotification) {
        super(eventExecutor);
        this.pushNotification = pushNotification;
    }
    
    public P getPushNotification() {
        return this.pushNotification;
    }
}
