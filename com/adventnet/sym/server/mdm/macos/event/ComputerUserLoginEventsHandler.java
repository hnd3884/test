package com.adventnet.sym.server.mdm.macos.event;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class ComputerUserLoginEventsHandler
{
    private static ComputerUserLoginEventsHandler cmpUserHandler;
    private List<ComputerUserLoginListener> cmpUserListernerList;
    
    public ComputerUserLoginEventsHandler() {
        this.cmpUserListernerList = new ArrayList<ComputerUserLoginListener>();
    }
    
    public static ComputerUserLoginEventsHandler getInstance() {
        if (ComputerUserLoginEventsHandler.cmpUserHandler == null) {
            ComputerUserLoginEventsHandler.cmpUserHandler = new ComputerUserLoginEventsHandler();
        }
        return ComputerUserLoginEventsHandler.cmpUserHandler;
    }
    
    public void addUserLoggedInComputerListener(final ComputerUserLoginListener userListener) {
        this.cmpUserListernerList.add(userListener);
    }
    
    public void invokeUserLoggedInComputerListener(final ComputerUserLoginEvent event) {
        for (final ComputerUserLoginListener listener : this.cmpUserListernerList) {
            listener.userLoggedInComputer(event);
        }
    }
    
    static {
        ComputerUserLoginEventsHandler.cmpUserHandler = null;
    }
}
