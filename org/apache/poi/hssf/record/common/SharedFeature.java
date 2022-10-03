package org.apache.poi.hssf.record.common;

import org.apache.poi.util.LittleEndianOutput;

public interface SharedFeature
{
    String toString();
    
    void serialize(final LittleEndianOutput p0);
    
    int getDataSize();
    
    SharedFeature copy();
}
