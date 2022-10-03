package org.w3c.dom.events;

import org.w3c.dom.views.AbstractView;

public interface UIEvent extends Event
{
    AbstractView getView();
    
    int getDetail();
    
    void initUIEvent(final String p0, final boolean p1, final boolean p2, final AbstractView p3, final int p4);
}
