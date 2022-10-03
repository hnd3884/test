package sun.java2d.pipe;

import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.awt.Color;
import java.awt.Image;
import java.awt.font.GlyphVector;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public class ValidatePipe implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, TextPipe, DrawImagePipe
{
    public boolean validate(final SunGraphics2D sunGraphics2D) {
        sunGraphics2D.validatePipe();
        return true;
    }
    
    @Override
    public void drawLine(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.drawpipe.drawLine(sunGraphics2D, n, n2, n3, n4);
        }
    }
    
    @Override
    public void drawRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.drawpipe.drawRect(sunGraphics2D, n, n2, n3, n4);
        }
    }
    
    @Override
    public void fillRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.fillpipe.fillRect(sunGraphics2D, n, n2, n3, n4);
        }
    }
    
    @Override
    public void drawRoundRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.drawpipe.drawRoundRect(sunGraphics2D, n, n2, n3, n4, n5, n6);
        }
    }
    
    @Override
    public void fillRoundRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.fillpipe.fillRoundRect(sunGraphics2D, n, n2, n3, n4, n5, n6);
        }
    }
    
    @Override
    public void drawOval(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.drawpipe.drawOval(sunGraphics2D, n, n2, n3, n4);
        }
    }
    
    @Override
    public void fillOval(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.fillpipe.fillOval(sunGraphics2D, n, n2, n3, n4);
        }
    }
    
    @Override
    public void drawArc(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.drawpipe.drawArc(sunGraphics2D, n, n2, n3, n4, n5, n6);
        }
    }
    
    @Override
    public void fillArc(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.fillpipe.fillArc(sunGraphics2D, n, n2, n3, n4, n5, n6);
        }
    }
    
    @Override
    public void drawPolyline(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.drawpipe.drawPolyline(sunGraphics2D, array, array2, n);
        }
    }
    
    @Override
    public void drawPolygon(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.drawpipe.drawPolygon(sunGraphics2D, array, array2, n);
        }
    }
    
    @Override
    public void fillPolygon(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.fillpipe.fillPolygon(sunGraphics2D, array, array2, n);
        }
    }
    
    @Override
    public void draw(final SunGraphics2D sunGraphics2D, final Shape shape) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.shapepipe.draw(sunGraphics2D, shape);
        }
    }
    
    @Override
    public void fill(final SunGraphics2D sunGraphics2D, final Shape shape) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.shapepipe.fill(sunGraphics2D, shape);
        }
    }
    
    @Override
    public void drawString(final SunGraphics2D sunGraphics2D, final String s, final double n, final double n2) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.textpipe.drawString(sunGraphics2D, s, n, n2);
        }
    }
    
    @Override
    public void drawGlyphVector(final SunGraphics2D sunGraphics2D, final GlyphVector glyphVector, final float n, final float n2) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.textpipe.drawGlyphVector(sunGraphics2D, glyphVector, n, n2);
        }
    }
    
    @Override
    public void drawChars(final SunGraphics2D sunGraphics2D, final char[] array, final int n, final int n2, final int n3, final int n4) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.textpipe.drawChars(sunGraphics2D, array, n, n2, n3, n4);
        }
    }
    
    @Override
    public boolean copyImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final Color color, final ImageObserver imageObserver) {
        return this.validate(sunGraphics2D) && sunGraphics2D.imagepipe.copyImage(sunGraphics2D, image, n, n2, color, imageObserver);
    }
    
    @Override
    public boolean copyImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Color color, final ImageObserver imageObserver) {
        return this.validate(sunGraphics2D) && sunGraphics2D.imagepipe.copyImage(sunGraphics2D, image, n, n2, n3, n4, n5, n6, color, imageObserver);
    }
    
    @Override
    public boolean scaleImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final int n3, final int n4, final Color color, final ImageObserver imageObserver) {
        return this.validate(sunGraphics2D) && sunGraphics2D.imagepipe.scaleImage(sunGraphics2D, image, n, n2, n3, n4, color, imageObserver);
    }
    
    @Override
    public boolean scaleImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Color color, final ImageObserver imageObserver) {
        return this.validate(sunGraphics2D) && sunGraphics2D.imagepipe.scaleImage(sunGraphics2D, image, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
    }
    
    @Override
    public boolean transformImage(final SunGraphics2D sunGraphics2D, final Image image, final AffineTransform affineTransform, final ImageObserver imageObserver) {
        return this.validate(sunGraphics2D) && sunGraphics2D.imagepipe.transformImage(sunGraphics2D, image, affineTransform, imageObserver);
    }
    
    @Override
    public void transformImage(final SunGraphics2D sunGraphics2D, final BufferedImage bufferedImage, final BufferedImageOp bufferedImageOp, final int n, final int n2) {
        if (this.validate(sunGraphics2D)) {
            sunGraphics2D.imagepipe.transformImage(sunGraphics2D, bufferedImage, bufferedImageOp, n, n2);
        }
    }
}
