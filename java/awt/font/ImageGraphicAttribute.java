package java.awt.font;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.awt.Image;

public final class ImageGraphicAttribute extends GraphicAttribute
{
    private Image fImage;
    private float fImageWidth;
    private float fImageHeight;
    private float fOriginX;
    private float fOriginY;
    
    public ImageGraphicAttribute(final Image image, final int n) {
        this(image, n, 0.0f, 0.0f);
    }
    
    public ImageGraphicAttribute(final Image fImage, final int n, final float fOriginX, final float fOriginY) {
        super(n);
        this.fImage = fImage;
        this.fImageWidth = (float)fImage.getWidth(null);
        this.fImageHeight = (float)fImage.getHeight(null);
        this.fOriginX = fOriginX;
        this.fOriginY = fOriginY;
    }
    
    @Override
    public float getAscent() {
        return Math.max(0.0f, this.fOriginY);
    }
    
    @Override
    public float getDescent() {
        return Math.max(0.0f, this.fImageHeight - this.fOriginY);
    }
    
    @Override
    public float getAdvance() {
        return Math.max(0.0f, this.fImageWidth - this.fOriginX);
    }
    
    @Override
    public Rectangle2D getBounds() {
        return new Rectangle2D.Float(-this.fOriginX, -this.fOriginY, this.fImageWidth, this.fImageHeight);
    }
    
    @Override
    public void draw(final Graphics2D graphics2D, final float n, final float n2) {
        graphics2D.drawImage(this.fImage, (int)(n - this.fOriginX), (int)(n2 - this.fOriginY), null);
    }
    
    @Override
    public int hashCode() {
        return this.fImage.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            return this.equals((ImageGraphicAttribute)o);
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    public boolean equals(final ImageGraphicAttribute imageGraphicAttribute) {
        return imageGraphicAttribute != null && (this == imageGraphicAttribute || (this.fOriginX == imageGraphicAttribute.fOriginX && this.fOriginY == imageGraphicAttribute.fOriginY && this.getAlignment() == imageGraphicAttribute.getAlignment() && this.fImage.equals(imageGraphicAttribute.fImage)));
    }
}
