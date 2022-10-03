package sun.java2d.pipe;

public interface AATileGenerator
{
    int getTileWidth();
    
    int getTileHeight();
    
    int getTypicalAlpha();
    
    void nextTile();
    
    void getAlpha(final byte[] p0, final int p1, final int p2);
    
    void dispose();
}
