package sun.management;

import java.lang.management.MemoryUsage;

public abstract class Sensor
{
    private Object lock;
    private String name;
    private long count;
    private boolean on;
    
    public Sensor(final String name) {
        this.name = name;
        this.count = 0L;
        this.on = false;
        this.lock = new Object();
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getCount() {
        synchronized (this.lock) {
            return this.count;
        }
    }
    
    public boolean isOn() {
        synchronized (this.lock) {
            return this.on;
        }
    }
    
    public void trigger() {
        synchronized (this.lock) {
            this.on = true;
            ++this.count;
        }
        this.triggerAction();
    }
    
    public void trigger(final int n) {
        synchronized (this.lock) {
            this.on = true;
            this.count += n;
        }
        this.triggerAction();
    }
    
    public void trigger(final int n, final MemoryUsage memoryUsage) {
        synchronized (this.lock) {
            this.on = true;
            this.count += n;
        }
        this.triggerAction(memoryUsage);
    }
    
    public void clear() {
        synchronized (this.lock) {
            this.on = false;
        }
        this.clearAction();
    }
    
    public void clear(final int n) {
        synchronized (this.lock) {
            this.on = false;
            this.count += n;
        }
        this.clearAction();
    }
    
    @Override
    public String toString() {
        return "Sensor - " + this.getName() + (this.isOn() ? " on " : " off ") + " count = " + this.getCount();
    }
    
    abstract void triggerAction();
    
    abstract void triggerAction(final MemoryUsage p0);
    
    abstract void clearAction();
}
