package sun.awt;

import java.awt.AWTEvent;

public class EventQueueItem
{
    public AWTEvent event;
    public EventQueueItem next;
    
    public EventQueueItem(final AWTEvent event) {
        this.event = event;
    }
}
