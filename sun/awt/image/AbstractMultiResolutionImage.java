package sun.awt.image;

import java.awt.Graphics;
import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;
import java.awt.Image;

public abstract class AbstractMultiResolutionImage extends Image implements MultiResolutionImage
{
    @Override
    public int getWidth(final ImageObserver imageObserver) {
        return this.getBaseImage().getWidth(null);
    }
    
    @Override
    public int getHeight(final ImageObserver imageObserver) {
        return this.getBaseImage().getHeight(null);
    }
    
    @Override
    public ImageProducer getSource() {
        return this.getBaseImage().getSource();
    }
    
    @Override
    public Graphics getGraphics() {
        return this.getBaseImage().getGraphics();
    }
    
    @Override
    public Object getProperty(final String s, final ImageObserver imageObserver) {
        return this.getBaseImage().getProperty(s, imageObserver);
    }
    
    protected abstract Image getBaseImage();
}
