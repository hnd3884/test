package java.awt.image;

import java.awt.Point;

public interface WritableRenderedImage extends RenderedImage
{
    void addTileObserver(final TileObserver p0);
    
    void removeTileObserver(final TileObserver p0);
    
    WritableRaster getWritableTile(final int p0, final int p1);
    
    void releaseWritableTile(final int p0, final int p1);
    
    boolean isTileWritable(final int p0, final int p1);
    
    Point[] getWritableTileIndices();
    
    boolean hasTileWriters();
    
    void setData(final Raster p0);
}
