package com.turo.pushy.apns.util.concurrent;

import io.netty.util.concurrent.Future;
import com.turo.pushy.apns.ApnsPushNotification;

public interface PushNotificationFuture<P extends ApnsPushNotification, V> extends Future<V>
{
    P getPushNotification();
}
