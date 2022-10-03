package com.adventnet.sym.server.mdm.macos.event;

public class ComputerUserLoginEvent
{
    public Long resourceID;
    public Long loginEventTime;
    public Long customerID;
    public Long newUserID;
    public String userNameInEvent;
    public String displayNameInEvent;
    
    public ComputerUserLoginEvent(final Long resourceID, final Long customerID, final String userNameInEvent, final String displayNameInEvent, final Long loginEventTime) {
        this.resourceID = null;
        this.loginEventTime = null;
        this.customerID = null;
        this.newUserID = null;
        this.userNameInEvent = null;
        this.displayNameInEvent = null;
        this.resourceID = resourceID;
        this.customerID = customerID;
        this.userNameInEvent = userNameInEvent;
        this.displayNameInEvent = displayNameInEvent;
        this.loginEventTime = loginEventTime;
    }
}
