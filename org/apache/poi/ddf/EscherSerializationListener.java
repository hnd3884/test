package org.apache.poi.ddf;

public interface EscherSerializationListener
{
    void beforeRecordSerialize(final int p0, final short p1, final EscherRecord p2);
    
    void afterRecordSerialize(final int p0, final short p1, final int p2, final EscherRecord p3);
}
