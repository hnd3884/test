package com.adventnet.db.persistence.metadata.extended;

public interface PiiValueHandler
{
    String getMaskedValue(final Object p0, final String p1);
    
    String getMaskedValue(final Object p0, final String p1, final Object p2);
}
