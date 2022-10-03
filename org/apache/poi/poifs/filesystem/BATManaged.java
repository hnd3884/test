package org.apache.poi.poifs.filesystem;

public interface BATManaged
{
    int countBlocks();
    
    void setStartBlock(final int p0);
}
