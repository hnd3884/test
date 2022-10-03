package com.turo.pushy.apns.util.concurrent;

import com.turo.pushy.apns.PushNotificationResponse;
import io.netty.util.concurrent.GenericFutureListener;
import com.turo.pushy.apns.ApnsPushNotification;

public interface PushNotificationResponseListener<T extends ApnsPushNotification> extends GenericFutureListener<PushNotificationFuture<T, PushNotificationResponse<T>>>
{
}
