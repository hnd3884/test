package com.adventnet.mfw.message;

import java.util.ArrayList;

public class MessageAggregator
{
    private Object message;
    
    public void addMessage(final Object message) {
        if (this.message == null) {
            this.message = new ArrayList();
        }
        ((ArrayList)this.message).add(message);
    }
    
    public void setMessage(final Object message) {
        this.message = message;
    }
    
    public Object getMessage() {
        return this.message;
    }
}
