package com.adventnet.beans.rangenavigator.events;

import java.util.EventObject;

public class ActionComboEvent extends EventObject
{
    public long totalValue;
    public long fromValue;
    public long toValue;
    public long pageNo;
    public long totalPages;
    public long pageLength;
    public Object actionItem;
    
    public ActionComboEvent(final Object o) {
        super(o);
    }
    
    public long getTotalValue() {
        return this.totalValue;
    }
    
    public long getFromValue() {
        return this.fromValue;
    }
    
    public long getToValue() {
        return this.toValue;
    }
    
    public long getPageNumber() {
        return this.pageNo;
    }
    
    public long getTotalPages() {
        return this.totalPages;
    }
    
    public long getPageLength() {
        return this.pageLength;
    }
    
    public Object getActionItem() {
        return this.actionItem;
    }
}
