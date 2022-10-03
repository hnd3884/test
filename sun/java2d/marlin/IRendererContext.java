package sun.java2d.marlin;

interface IRendererContext extends MarlinConst
{
    RendererStats stats();
    
    OffHeapArray newOffHeapArray(final long p0);
    
    IntArrayCache.Reference newCleanIntArrayRef(final int p0);
}
