package org.apache.xerces.dom.events;

import org.w3c.dom.views.AbstractView;
import org.w3c.dom.events.UIEvent;

public class UIEventImpl extends EventImpl implements UIEvent
{
    private AbstractView fView;
    private int fDetail;
    
    public AbstractView getView() {
        return this.fView;
    }
    
    public int getDetail() {
        return this.fDetail;
    }
    
    public void initUIEvent(final String s, final boolean b, final boolean b2, final AbstractView fView, final int fDetail) {
        this.fView = fView;
        this.fDetail = fDetail;
        super.initEvent(s, b, b2);
    }
}
