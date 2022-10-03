package com.microsoft.sqlserver.jdbc.spatialdatatypes;

public class Segment
{
    private byte segmentType;
    
    public Segment(final byte segmentType) {
        this.segmentType = segmentType;
    }
    
    public byte getSegmentType() {
        return this.segmentType;
    }
}
