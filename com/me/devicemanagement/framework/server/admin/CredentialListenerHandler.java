package com.me.devicemanagement.framework.server.admin;

import java.util.ArrayList;
import java.util.List;

public class CredentialListenerHandler
{
    private List<CredentialListener> credentialListenerList;
    private static CredentialListenerHandler credentialListenerHandler;
    
    private CredentialListenerHandler() {
        this.credentialListenerList = new ArrayList<CredentialListener>();
    }
    
    public static synchronized CredentialListenerHandler getInstance() {
        if (CredentialListenerHandler.credentialListenerHandler == null) {
            CredentialListenerHandler.credentialListenerHandler = new CredentialListenerHandler();
        }
        return CredentialListenerHandler.credentialListenerHandler;
    }
    
    public void addCredentialListener(final CredentialListener listener) {
        this.credentialListenerList.add(listener);
    }
    
    public void removeCredentialListener(final CredentialListener listener) {
        this.credentialListenerList.remove(listener);
    }
    
    public void invokeCredentialAddedListeners(final Long credentialID) {
        for (int listenerInt = 0; listenerInt < this.credentialListenerList.size(); ++listenerInt) {
            final CredentialListener listener = this.credentialListenerList.get(listenerInt);
            listener.credentialAdded(credentialID);
        }
    }
    
    public void invokeCredentialModifiedListeners(final Long credentialID) {
        for (int linstenerInt = 0; linstenerInt < this.credentialListenerList.size(); ++linstenerInt) {
            final CredentialListener listener = this.credentialListenerList.get(linstenerInt);
            listener.credentialModified(credentialID);
        }
    }
    
    static {
        CredentialListenerHandler.credentialListenerHandler = null;
    }
}
