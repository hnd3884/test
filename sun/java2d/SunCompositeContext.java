package sun.java2d;

import sun.java2d.pipe.Region;
import sun.java2d.loops.Blit;
import sun.awt.image.BufImgSurfaceData;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import sun.java2d.loops.XORComposite;
import java.awt.AlphaComposite;
import sun.java2d.loops.CompositeType;
import java.awt.Composite;
import java.awt.image.ColorModel;
import java.awt.CompositeContext;

public class SunCompositeContext implements CompositeContext
{
    ColorModel srcCM;
    ColorModel dstCM;
    Composite composite;
    CompositeType comptype;
    
    public SunCompositeContext(final AlphaComposite composite, final ColorModel srcCM, final ColorModel dstCM) {
        if (srcCM == null) {
            throw new NullPointerException("Source color model cannot be null");
        }
        if (dstCM == null) {
            throw new NullPointerException("Destination color model cannot be null");
        }
        this.srcCM = srcCM;
        this.dstCM = dstCM;
        this.composite = composite;
        this.comptype = CompositeType.forAlphaComposite(composite);
    }
    
    public SunCompositeContext(final XORComposite composite, final ColorModel srcCM, final ColorModel dstCM) {
        if (srcCM == null) {
            throw new NullPointerException("Source color model cannot be null");
        }
        if (dstCM == null) {
            throw new NullPointerException("Destination color model cannot be null");
        }
        this.srcCM = srcCM;
        this.dstCM = dstCM;
        this.composite = composite;
        this.comptype = CompositeType.Xor;
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void compose(final Raster raster, final Raster raster2, final WritableRaster writableRaster) {
        if (raster2 != writableRaster) {
            writableRaster.setDataElements(0, 0, raster2);
        }
        WritableRaster compatibleWritableRaster;
        if (raster instanceof WritableRaster) {
            compatibleWritableRaster = (WritableRaster)raster;
        }
        else {
            compatibleWritableRaster = raster.createCompatibleWritableRaster();
            compatibleWritableRaster.setDataElements(0, 0, raster);
        }
        final int min = Math.min(compatibleWritableRaster.getWidth(), raster2.getWidth());
        final int min2 = Math.min(compatibleWritableRaster.getHeight(), raster2.getHeight());
        final BufferedImage bufferedImage = new BufferedImage(this.srcCM, compatibleWritableRaster, this.srcCM.isAlphaPremultiplied(), null);
        final BufferedImage bufferedImage2 = new BufferedImage(this.dstCM, writableRaster, this.dstCM.isAlphaPremultiplied(), null);
        final SurfaceData data = BufImgSurfaceData.createData(bufferedImage);
        final SurfaceData data2 = BufImgSurfaceData.createData(bufferedImage2);
        Blit.getFromCache(data.getSurfaceType(), this.comptype, data2.getSurfaceType()).Blit(data, data2, this.composite, null, 0, 0, 0, 0, min, min2);
    }
}
