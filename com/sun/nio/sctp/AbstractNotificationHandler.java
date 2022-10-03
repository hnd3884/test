package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class AbstractNotificationHandler<T> implements NotificationHandler<T>
{
    protected AbstractNotificationHandler() {
    }
    
    @Override
    public HandlerResult handleNotification(final Notification notification, final T t) {
        return HandlerResult.CONTINUE;
    }
    
    public HandlerResult handleNotification(final AssociationChangeNotification associationChangeNotification, final T t) {
        return HandlerResult.CONTINUE;
    }
    
    public HandlerResult handleNotification(final PeerAddressChangeNotification peerAddressChangeNotification, final T t) {
        return HandlerResult.CONTINUE;
    }
    
    public HandlerResult handleNotification(final SendFailedNotification sendFailedNotification, final T t) {
        return HandlerResult.CONTINUE;
    }
    
    public HandlerResult handleNotification(final ShutdownNotification shutdownNotification, final T t) {
        return HandlerResult.CONTINUE;
    }
}
