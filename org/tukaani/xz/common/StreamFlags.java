package org.tukaani.xz.common;

public class StreamFlags
{
    public int checkType;
    public long backwardSize;
    
    public StreamFlags() {
        this.checkType = -1;
        this.backwardSize = -1L;
    }
}
