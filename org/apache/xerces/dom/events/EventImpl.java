package org.apache.xerces.dom.events;

import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.Event;

public class EventImpl implements Event
{
    public String type;
    public EventTarget target;
    public EventTarget currentTarget;
    public short eventPhase;
    public boolean initialized;
    public boolean bubbles;
    public boolean cancelable;
    public boolean stopPropagation;
    public boolean preventDefault;
    protected long timeStamp;
    
    public EventImpl() {
        this.type = null;
        this.initialized = false;
        this.bubbles = true;
        this.cancelable = false;
        this.stopPropagation = false;
        this.preventDefault = false;
        this.timeStamp = System.currentTimeMillis();
    }
    
    public void initEvent(final String type, final boolean bubbles, final boolean cancelable) {
        this.type = type;
        this.bubbles = bubbles;
        this.cancelable = cancelable;
        this.initialized = true;
    }
    
    public boolean getBubbles() {
        return this.bubbles;
    }
    
    public boolean getCancelable() {
        return this.cancelable;
    }
    
    public EventTarget getCurrentTarget() {
        return this.currentTarget;
    }
    
    public short getEventPhase() {
        return this.eventPhase;
    }
    
    public EventTarget getTarget() {
        return this.target;
    }
    
    public String getType() {
        return this.type;
    }
    
    public long getTimeStamp() {
        return this.timeStamp;
    }
    
    public void stopPropagation() {
        this.stopPropagation = true;
    }
    
    public void preventDefault() {
        this.preventDefault = true;
    }
}
