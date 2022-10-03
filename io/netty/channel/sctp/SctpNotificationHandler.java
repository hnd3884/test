package io.netty.channel.sctp;

import com.sun.nio.sctp.ShutdownNotification;
import com.sun.nio.sctp.SendFailedNotification;
import com.sun.nio.sctp.PeerAddressChangeNotification;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.AssociationChangeNotification;
import io.netty.util.internal.ObjectUtil;
import com.sun.nio.sctp.AbstractNotificationHandler;

public final class SctpNotificationHandler extends AbstractNotificationHandler<Object>
{
    private final SctpChannel sctpChannel;
    
    public SctpNotificationHandler(final SctpChannel sctpChannel) {
        this.sctpChannel = ObjectUtil.checkNotNull(sctpChannel, "sctpChannel");
    }
    
    @Override
    public HandlerResult handleNotification(final AssociationChangeNotification notification, final Object o) {
        this.fireEvent(notification);
        return HandlerResult.CONTINUE;
    }
    
    @Override
    public HandlerResult handleNotification(final PeerAddressChangeNotification notification, final Object o) {
        this.fireEvent(notification);
        return HandlerResult.CONTINUE;
    }
    
    @Override
    public HandlerResult handleNotification(final SendFailedNotification notification, final Object o) {
        this.fireEvent(notification);
        return HandlerResult.CONTINUE;
    }
    
    @Override
    public HandlerResult handleNotification(final ShutdownNotification notification, final Object o) {
        this.fireEvent(notification);
        this.sctpChannel.close();
        return HandlerResult.RETURN;
    }
    
    private void fireEvent(final Notification notification) {
        this.sctpChannel.pipeline().fireUserEventTriggered((Object)notification);
    }
}
