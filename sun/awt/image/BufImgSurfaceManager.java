package sun.awt.image;

import sun.java2d.SurfaceData;
import java.awt.image.BufferedImage;

public class BufImgSurfaceManager extends SurfaceManager
{
    protected BufferedImage bImg;
    protected SurfaceData sdDefault;
    
    public BufImgSurfaceManager(final BufferedImage bImg) {
        this.bImg = bImg;
        this.sdDefault = BufImgSurfaceData.createData(bImg);
    }
    
    @Override
    public SurfaceData getPrimarySurfaceData() {
        return this.sdDefault;
    }
    
    @Override
    public SurfaceData restoreContents() {
        return this.sdDefault;
    }
}
