package com.sun.nio.sctp;

import java.nio.ByteBuffer;
import java.net.SocketAddress;
import jdk.Exported;

@Exported
public abstract class SendFailedNotification implements Notification
{
    protected SendFailedNotification() {
    }
    
    @Override
    public abstract Association association();
    
    public abstract SocketAddress address();
    
    public abstract ByteBuffer buffer();
    
    public abstract int errorCode();
    
    public abstract int streamNumber();
}
