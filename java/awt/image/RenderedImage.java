package java.awt.image;

import java.awt.Rectangle;
import java.util.Vector;

public interface RenderedImage
{
    Vector<RenderedImage> getSources();
    
    Object getProperty(final String p0);
    
    String[] getPropertyNames();
    
    ColorModel getColorModel();
    
    SampleModel getSampleModel();
    
    int getWidth();
    
    int getHeight();
    
    int getMinX();
    
    int getMinY();
    
    int getNumXTiles();
    
    int getNumYTiles();
    
    int getMinTileX();
    
    int getMinTileY();
    
    int getTileWidth();
    
    int getTileHeight();
    
    int getTileGridXOffset();
    
    int getTileGridYOffset();
    
    Raster getTile(final int p0, final int p1);
    
    Raster getData();
    
    Raster getData(final Rectangle p0);
    
    WritableRaster copyData(final WritableRaster p0);
}
