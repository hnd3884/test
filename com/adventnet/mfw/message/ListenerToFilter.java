package com.adventnet.mfw.message;

public class ListenerToFilter
{
    public MessageListener listener;
    public MessageFilter filter;
    
    public ListenerToFilter(final MessageListener lis, final MessageFilter fil) {
        this.listener = null;
        this.filter = null;
        this.listener = lis;
        this.filter = fil;
    }
    
    public MessageListener getListener() {
        return this.listener;
    }
    
    public MessageFilter getFilter() {
        return this.filter;
    }
}
