package org.apache.poi.hssf.record;

public abstract class RecordBase
{
    public abstract int serialize(final int p0, final byte[] p1);
    
    public abstract int getRecordSize();
}
