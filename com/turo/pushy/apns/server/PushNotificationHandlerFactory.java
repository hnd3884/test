package com.turo.pushy.apns.server;

import javax.net.ssl.SSLSession;

public interface PushNotificationHandlerFactory
{
    PushNotificationHandler buildHandler(final SSLSession p0);
}
