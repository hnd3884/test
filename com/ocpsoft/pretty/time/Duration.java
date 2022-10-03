package com.ocpsoft.pretty.time;

public class Duration
{
    private long quantity;
    private long delta;
    private TimeUnit unit;
    
    public long getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }
    
    public TimeUnit getUnit() {
        return this.unit;
    }
    
    public void setUnit(final TimeUnit unit) {
        this.unit = unit;
    }
    
    public long getDelta() {
        return this.delta;
    }
    
    public void setDelta(final long delta) {
        this.delta = delta;
    }
}
