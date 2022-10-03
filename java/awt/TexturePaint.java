package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class TexturePaint implements Paint
{
    BufferedImage bufImg;
    double tx;
    double ty;
    double sx;
    double sy;
    
    public TexturePaint(final BufferedImage bufImg, final Rectangle2D rectangle2D) {
        this.bufImg = bufImg;
        this.tx = rectangle2D.getX();
        this.ty = rectangle2D.getY();
        this.sx = rectangle2D.getWidth() / this.bufImg.getWidth();
        this.sy = rectangle2D.getHeight() / this.bufImg.getHeight();
    }
    
    public BufferedImage getImage() {
        return this.bufImg;
    }
    
    public Rectangle2D getAnchorRect() {
        return new Rectangle2D.Double(this.tx, this.ty, this.sx * this.bufImg.getWidth(), this.sy * this.bufImg.getHeight());
    }
    
    @Override
    public PaintContext createContext(final ColorModel colorModel, final Rectangle rectangle, final Rectangle2D rectangle2D, AffineTransform affineTransform, final RenderingHints renderingHints) {
        if (affineTransform == null) {
            affineTransform = new AffineTransform();
        }
        else {
            affineTransform = (AffineTransform)affineTransform.clone();
        }
        affineTransform.translate(this.tx, this.ty);
        affineTransform.scale(this.sx, this.sy);
        return TexturePaintContext.getContext(this.bufImg, affineTransform, renderingHints, rectangle);
    }
    
    @Override
    public int getTransparency() {
        return this.bufImg.getColorModel().getTransparency();
    }
}
