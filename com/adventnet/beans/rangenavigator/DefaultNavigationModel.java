package com.adventnet.beans.rangenavigator;

import com.adventnet.beans.rangenavigator.events.NavigationEvent;
import java.util.EventListener;
import com.adventnet.beans.rangenavigator.events.NavigationListener;
import javax.swing.event.EventListenerList;

public class DefaultNavigationModel implements NavigationModel
{
    private long from;
    private long to;
    private long total;
    private long pageLength;
    private EventListenerList listeners;
    
    public DefaultNavigationModel(final long total) {
        this.pageLength = 10L;
        this.listeners = new EventListenerList();
        this.total = total;
        this.init();
    }
    
    private void init() {
        if (this.total == 0L) {
            this.from = 0L;
            this.to = 0L;
        }
        else {
            this.from = 1L;
            this.to = this.from + this.pageLength - 1L;
            if (this.to > this.total) {
                this.to = this.total;
            }
        }
    }
    
    public void addNavigationListener(final NavigationListener navigationListener) {
        this.listeners.add(NavigationListener.class, navigationListener);
    }
    
    public long getEndIndex() {
        return this.to;
    }
    
    public long getPageLength() {
        return this.pageLength;
    }
    
    public long getStartIndex() {
        return this.from;
    }
    
    public long getTotalRecordsCount() {
        return this.total;
    }
    
    public void removeNavigationListener(final NavigationListener navigationListener) {
        this.listeners.remove(NavigationListener.class, navigationListener);
    }
    
    public void setPageLength(final long pageLength) {
        if (this.pageLength == pageLength) {
            return;
        }
        if (this.validate(this.from, this.to, this.total, pageLength)) {
            this.pageLength = pageLength;
            this.to = this.from + pageLength - 1L;
            if (this.to > this.total) {
                this.to = this.total;
            }
            this.adjustValues();
        }
        this.fireNavigationEvent();
    }
    
    public void showRange(final long from, final long to) {
        if (this.from == from && this.to == to) {
            return;
        }
        if (this.validate(from, to, this.total, this.pageLength)) {
            this.from = from;
            this.to = to;
            if (this.to > this.total) {
                this.to = this.total;
            }
            this.pageLength = this.to - this.from + 1L;
            this.adjustValues();
        }
        this.fireNavigationEvent();
    }
    
    private void fireNavigationEvent() {
        final EventListener[] listeners = this.listeners.getListeners((Class<EventListener>)NavigationListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            ((NavigationListener)listeners[i]).navigationChanged(new NavigationEvent(this));
        }
    }
    
    public boolean validate(final long n, final long n2, final long n3, final long n4) {
        return n >= 0L && n2 >= 0L && n4 > 0L && n3 >= 0L && ((n != 0L && n2 != 0L) || n3 == 0L) && n <= n2 && n3 >= n;
    }
    
    protected void adjustValues() {
        if (this.getTotalRecordsCount() == 0L) {
            this.from = 0L;
            this.to = 0L;
            return;
        }
        if (this.from <= 0L && this.getTotalRecordsCount() > 0L) {
            this.from = 1L;
            this.to = this.from + this.pageLength - 1L;
        }
        if (this.to > this.getTotalRecordsCount()) {
            this.to = this.getTotalRecordsCount();
            this.from = this.to - this.pageLength;
            if (this.from < 0L) {
                this.from = ((this.getTotalRecordsCount() > 0L) ? 1 : 0);
            }
        }
        if (this.to - this.from + 1L < this.pageLength && this.getTotalRecordsCount() - this.pageLength < this.from) {
            this.to = this.getTotalRecordsCount();
        }
    }
}
