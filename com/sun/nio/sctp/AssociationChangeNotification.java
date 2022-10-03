package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public abstract class AssociationChangeNotification implements Notification
{
    protected AssociationChangeNotification() {
    }
    
    @Override
    public abstract Association association();
    
    public abstract AssocChangeEvent event();
    
    @Exported
    public enum AssocChangeEvent
    {
        COMM_UP, 
        COMM_LOST, 
        RESTART, 
        SHUTDOWN, 
        CANT_START;
    }
}
