package com.me.mdm.chrome.agent.core.communication;

public class CommunicationManager
{
    private static CommunicationManager manager;
    
    public static CommunicationManager getInstance() {
        return CommunicationManager.manager;
    }
    
    public CommunicationHandler getCommunicationHandler() {
        return new InServerCommunicationHandler();
    }
    
    static {
        CommunicationManager.manager = new CommunicationManager();
    }
}
