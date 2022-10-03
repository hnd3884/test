package com.sun.media.sound;

public final class DLSSampleLoop
{
    public static final int LOOP_TYPE_FORWARD = 0;
    public static final int LOOP_TYPE_RELEASE = 1;
    long type;
    long start;
    long length;
    
    public long getLength() {
        return this.length;
    }
    
    public void setLength(final long length) {
        this.length = length;
    }
    
    public long getStart() {
        return this.start;
    }
    
    public void setStart(final long start) {
        this.start = start;
    }
    
    public long getType() {
        return this.type;
    }
    
    public void setType(final long type) {
        this.type = type;
    }
}
