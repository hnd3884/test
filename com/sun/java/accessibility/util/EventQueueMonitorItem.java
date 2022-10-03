package com.sun.java.accessibility.util;

import java.awt.AWTEvent;

class EventQueueMonitorItem
{
    AWTEvent event;
    EventQueueMonitorItem next;
    
    EventQueueMonitorItem(final AWTEvent event) {
        this.event = event;
        this.next = null;
    }
}
