package com.me.devicemanagement.onpremise.server.settings.cca;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class CcaChangeEvent
{
    private static CcaChangeEvent ccaChangeEvent;
    private final List<CcaChangeListener> ccaChangeListenerList;
    
    private CcaChangeEvent() {
        this.ccaChangeListenerList = new ArrayList<CcaChangeListener>();
    }
    
    public static CcaChangeEvent getInstance() {
        if (CcaChangeEvent.ccaChangeEvent == null) {
            CcaChangeEvent.ccaChangeEvent = new CcaChangeEvent();
        }
        return CcaChangeEvent.ccaChangeEvent;
    }
    
    public void invokeCcaChangeListeners(final boolean isBeingEnabled) {
        for (final CcaChangeListener listener : this.ccaChangeListenerList) {
            listener.onClientCertAuthChange(isBeingEnabled);
        }
    }
    
    public void addListener(final CcaChangeListener listener) {
        if (listener != null) {
            this.ccaChangeListenerList.add(listener);
        }
    }
    
    public void removeListener(final CcaChangeListener listener) {
        if (listener != null) {
            this.ccaChangeListenerList.remove(listener);
        }
    }
    
    static {
        CcaChangeEvent.ccaChangeEvent = null;
    }
}
