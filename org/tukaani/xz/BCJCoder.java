package org.tukaani.xz;

abstract class BCJCoder implements FilterCoder
{
    public static final long X86_FILTER_ID = 4L;
    public static final long POWERPC_FILTER_ID = 5L;
    public static final long IA64_FILTER_ID = 6L;
    public static final long ARM_FILTER_ID = 7L;
    public static final long ARMTHUMB_FILTER_ID = 8L;
    public static final long SPARC_FILTER_ID = 9L;
    
    public static boolean isBCJFilterID(final long n) {
        return n >= 4L && n <= 9L;
    }
    
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
