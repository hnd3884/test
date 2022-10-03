package org.tukaani.xz;

abstract class DeltaCoder implements FilterCoder
{
    public static final long FILTER_ID = 3L;
    
    @Override
    public boolean changesSize() {
        return false;
    }
    
    @Override
    public boolean nonLastOK() {
        return true;
    }
    
    @Override
    public boolean lastOK() {
        return false;
    }
}
