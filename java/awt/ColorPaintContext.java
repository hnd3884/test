package java.awt;

import java.util.Arrays;
import sun.awt.image.IntegerComponentRaster;
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

class ColorPaintContext implements PaintContext
{
    int color;
    WritableRaster savedTile;
    
    protected ColorPaintContext(final int color, final ColorModel colorModel) {
        this.color = color;
    }
    
    @Override
    public void dispose() {
    }
    
    int getRGB() {
        return this.color;
    }
    
    @Override
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }
    
    @Override
    public synchronized Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        WritableRaster savedTile = this.savedTile;
        if (savedTile == null || n3 > savedTile.getWidth() || n4 > savedTile.getHeight()) {
            savedTile = this.getColorModel().createCompatibleWritableRaster(n3, n4);
            final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)savedTile;
            Arrays.fill(integerComponentRaster.getDataStorage(), this.color);
            integerComponentRaster.markDirty();
            if (n3 <= 64 && n4 <= 64) {
                this.savedTile = savedTile;
            }
        }
        return savedTile;
    }
}
