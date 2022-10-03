package io.netty.buffer;

public interface SizeClassesMetric
{
    int sizeIdx2size(final int p0);
    
    int sizeIdx2sizeCompute(final int p0);
    
    long pageIdx2size(final int p0);
    
    long pageIdx2sizeCompute(final int p0);
    
    int size2SizeIdx(final int p0);
    
    int pages2pageIdx(final int p0);
    
    int pages2pageIdxFloor(final int p0);
    
    int normalizeSize(final int p0);
}
