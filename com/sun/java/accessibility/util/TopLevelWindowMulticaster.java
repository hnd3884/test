package com.sun.java.accessibility.util;

import java.awt.Window;
import java.util.EventListener;
import jdk.Exported;
import java.awt.AWTEventMulticaster;

@Exported(false)
public class TopLevelWindowMulticaster extends AWTEventMulticaster implements TopLevelWindowListener
{
    protected TopLevelWindowMulticaster(final EventListener eventListener, final EventListener eventListener2) {
        super(eventListener, eventListener2);
    }
    
    @Override
    public void topLevelWindowCreated(final Window window) {
        ((TopLevelWindowListener)this.a).topLevelWindowCreated(window);
        ((TopLevelWindowListener)this.b).topLevelWindowCreated(window);
    }
    
    @Override
    public void topLevelWindowDestroyed(final Window window) {
        ((TopLevelWindowListener)this.a).topLevelWindowDestroyed(window);
        ((TopLevelWindowListener)this.b).topLevelWindowDestroyed(window);
    }
    
    public static TopLevelWindowListener add(final TopLevelWindowListener topLevelWindowListener, final TopLevelWindowListener topLevelWindowListener2) {
        return (TopLevelWindowListener)addInternal(topLevelWindowListener, topLevelWindowListener2);
    }
    
    public static TopLevelWindowListener remove(final TopLevelWindowListener topLevelWindowListener, final TopLevelWindowListener topLevelWindowListener2) {
        return (TopLevelWindowListener)removeInternal(topLevelWindowListener, topLevelWindowListener2);
    }
    
    protected static EventListener addInternal(final EventListener eventListener, final EventListener eventListener2) {
        if (eventListener == null) {
            return eventListener2;
        }
        if (eventListener2 == null) {
            return eventListener;
        }
        return new TopLevelWindowMulticaster(eventListener, eventListener2);
    }
    
    protected static EventListener removeInternal(final EventListener eventListener, final EventListener eventListener2) {
        if (eventListener == eventListener2 || eventListener == null) {
            return null;
        }
        if (eventListener instanceof TopLevelWindowMulticaster) {
            return ((TopLevelWindowMulticaster)eventListener).remove(eventListener2);
        }
        return eventListener;
    }
}
