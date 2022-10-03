package org.apache.commons.validator.util;

import java.io.Serializable;

public class Flags implements Serializable
{
    private long flags;
    
    public Flags() {
        this.flags = 0L;
    }
    
    public Flags(final long flags) {
        this.flags = 0L;
        this.flags = flags;
    }
    
    public long getFlags() {
        return this.flags;
    }
    
    public boolean isOn(final long flag) {
        return (this.flags & flag) > 0L;
    }
    
    public boolean isOff(final long flag) {
        return (this.flags & flag) == 0x0L;
    }
    
    public void turnOn(final long flag) {
        this.flags |= flag;
    }
    
    public void turnOff(final long flag) {
        this.flags &= ~flag;
    }
    
    public void turnOffAll() {
        this.flags = 0L;
    }
    
    public void clear() {
        this.flags = 0L;
    }
    
    public void turnOnAll() {
        this.flags = Long.MAX_VALUE;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException("Couldn't clone Flags object.");
        }
    }
    
    public boolean equals(final Object obj) {
        if (!(obj instanceof Flags)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        final Flags f = (Flags)obj;
        return this.flags == f.flags;
    }
    
    public int hashCode() {
        return (int)this.flags;
    }
    
    public String toString() {
        final StringBuffer bin = new StringBuffer(Long.toBinaryString(this.flags));
        for (int i = 64 - bin.length(); i > 0; --i) {
            bin.insert(0, "0");
        }
        return bin.toString();
    }
}
