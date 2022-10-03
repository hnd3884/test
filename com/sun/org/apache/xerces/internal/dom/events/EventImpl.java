package com.sun.org.apache.xerces.internal.dom.events;

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
    
    @Override
    public void initEvent(final String eventTypeArg, final boolean canBubbleArg, final boolean cancelableArg) {
        this.type = eventTypeArg;
        this.bubbles = canBubbleArg;
        this.cancelable = cancelableArg;
        this.initialized = true;
    }
    
    @Override
    public boolean getBubbles() {
        return this.bubbles;
    }
    
    @Override
    public boolean getCancelable() {
        return this.cancelable;
    }
    
    @Override
    public EventTarget getCurrentTarget() {
        return this.currentTarget;
    }
    
    @Override
    public short getEventPhase() {
        return this.eventPhase;
    }
    
    @Override
    public EventTarget getTarget() {
        return this.target;
    }
    
    @Override
    public String getType() {
        return this.type;
    }
    
    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }
    
    @Override
    public void stopPropagation() {
        this.stopPropagation = true;
    }
    
    @Override
    public void preventDefault() {
        this.preventDefault = true;
    }
}
