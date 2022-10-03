package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public abstract class ShutdownNotification implements Notification
{
    protected ShutdownNotification() {
    }
    
    @Override
    public abstract Association association();
}
