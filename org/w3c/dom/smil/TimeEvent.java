package org.w3c.dom.smil;

import org.w3c.dom.views.AbstractView;
import org.w3c.dom.events.Event;

public interface TimeEvent extends Event
{
    AbstractView getView();
    
    int getDetail();
    
    void initTimeEvent(final String p0, final AbstractView p1, final int p2);
}
