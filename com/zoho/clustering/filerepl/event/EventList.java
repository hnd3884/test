package com.zoho.clustering.filerepl.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventList
{
    private List<String> eventList;
    private EventLogPosition nextEventPos;
    private boolean mutable;
    
    public EventList() {
        this.eventList = null;
        this.mutable = true;
    }
    
    public List<String> getEvents() {
        return (this.eventList == null) ? Collections.EMPTY_LIST : this.eventList;
    }
    
    public EventLogPosition getNextPos() {
        if (this.nextEventPos == null) {
            throw new IllegalStateException("nextEventPos is Not set");
        }
        return this.nextEventPos;
    }
    
    public void makeImmutable() {
        this.mutable = false;
    }
    
    private void assertMutability() {
        if (!this.mutable) {
            throw new IllegalStateException("This EventList object is Not mutable");
        }
    }
    
    public void addEvent(final String event) {
        this.assertMutability();
        if (this.eventList == null) {
            this.eventList = new ArrayList<String>();
        }
        this.eventList.add(event);
    }
    
    public void setNextPos(final EventLogPosition nextEventPos) {
        this.assertMutability();
        this.nextEventPos = nextEventPos;
    }
    
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        buff.append("Events:" + this.eventList).append("\n");
        buff.append("Next:").append(this.nextEventPos);
        return buff.toString();
    }
}
