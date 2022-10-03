package com.sun.java.accessibility.util;

import java.util.EventListener;
import jdk.Exported;
import java.awt.AWTEventMulticaster;

@Exported(false)
public class GUIInitializedMulticaster extends AWTEventMulticaster implements GUIInitializedListener
{
    protected GUIInitializedMulticaster(final EventListener eventListener, final EventListener eventListener2) {
        super(eventListener, eventListener2);
    }
    
    @Override
    public void guiInitialized() {
        ((GUIInitializedListener)this.a).guiInitialized();
        ((GUIInitializedListener)this.b).guiInitialized();
    }
    
    public static GUIInitializedListener add(final GUIInitializedListener guiInitializedListener, final GUIInitializedListener guiInitializedListener2) {
        return (GUIInitializedListener)addInternal(guiInitializedListener, guiInitializedListener2);
    }
    
    public static GUIInitializedListener remove(final GUIInitializedListener guiInitializedListener, final GUIInitializedListener guiInitializedListener2) {
        return (GUIInitializedListener)removeInternal(guiInitializedListener, guiInitializedListener2);
    }
    
    protected static EventListener addInternal(final EventListener eventListener, final EventListener eventListener2) {
        if (eventListener == null) {
            return eventListener2;
        }
        if (eventListener2 == null) {
            return eventListener;
        }
        return new GUIInitializedMulticaster(eventListener, eventListener2);
    }
    
    protected static EventListener removeInternal(final EventListener eventListener, final EventListener eventListener2) {
        if (eventListener == eventListener2 || eventListener == null) {
            return null;
        }
        if (eventListener instanceof GUIInitializedMulticaster) {
            return ((GUIInitializedMulticaster)eventListener).remove(eventListener2);
        }
        return eventListener;
    }
}
