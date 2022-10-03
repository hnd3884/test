package org.tukaani.xz;

abstract class LZMA2Coder implements FilterCoder
{
    public static final long FILTER_ID = 33L;
    
    @Override
    public boolean changesSize() {
        return true;
    }
    
    @Override
    public boolean nonLastOK() {
        return false;
    }
    
    @Override
    public boolean lastOK() {
        return true;
    }
}
