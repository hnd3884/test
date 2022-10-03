package jdk.jfr;

import jdk.Exported;

@Enabled(true)
@StackTrace(true)
@Registered(true)
@Exported
public abstract class Event
{
    protected Event() {
    }
    
    public final void begin() {
    }
    
    public final void end() {
    }
    
    public final void commit() {
    }
    
    public final boolean isEnabled() {
        return false;
    }
    
    public final boolean shouldCommit() {
        return false;
    }
    
    public final void set(final int n, final Object o) {
    }
}
