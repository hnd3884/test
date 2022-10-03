package org.apache.poi.hssf.record;

public interface CellValueRecordInterface
{
    int getRow();
    
    short getColumn();
    
    void setRow(final int p0);
    
    void setColumn(final short p0);
    
    void setXFIndex(final short p0);
    
    short getXFIndex();
}
