package com.sun.java.accessibility.util;

import java.awt.event.WindowEvent;
import java.awt.event.MouseEvent;
import java.awt.AWTEvent;

class ComponentEvtDispatchThread extends Thread
{
    public ComponentEvtDispatchThread(final String s) {
        super(s);
    }
    
    @Override
    public void run() {
        AWTEvent awtEvent = null;
        while (true) {
            synchronized (EventQueueMonitor.componentEventQueueLock) {
                while (EventQueueMonitor.componentEventQueue == null) {
                    try {
                        EventQueueMonitor.componentEventQueueLock.wait();
                    }
                    catch (final InterruptedException ex) {}
                }
                awtEvent = EventQueueMonitor.componentEventQueue.event;
                EventQueueMonitor.componentEventQueue = EventQueueMonitor.componentEventQueue.next;
            }
            switch (awtEvent.getID()) {
                case 503:
                case 506: {
                    EventQueueMonitor.updateCurrentMousePosition((MouseEvent)awtEvent);
                    continue;
                }
                case 205: {
                    EventQueueMonitor.maybeNotifyAssistiveTechnologies();
                    EventQueueMonitor.topLevelWindowWithFocus = ((WindowEvent)awtEvent).getWindow();
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
    }
}
