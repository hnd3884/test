package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Paint;

public class BorderFilter extends AbstractBufferedImageOp
{
    private int leftBorder;
    private int rightBorder;
    private int topBorder;
    private int bottomBorder;
    private Paint borderPaint;
    
    public BorderFilter() {
    }
    
    public BorderFilter(final int leftBorder, final int topBorder, final int rightBorder, final int bottomBorder, final Paint borderPaint) {
        this.leftBorder = leftBorder;
        this.topBorder = topBorder;
        this.rightBorder = rightBorder;
        this.bottomBorder = bottomBorder;
        this.borderPaint = borderPaint;
    }
    
    public void setLeftBorder(final int leftBorder) {
        this.leftBorder = leftBorder;
    }
    
    public int getLeftBorder() {
        return this.leftBorder;
    }
    
    public void setRightBorder(final int rightBorder) {
        this.rightBorder = rightBorder;
    }
    
    public int getRightBorder() {
        return this.rightBorder;
    }
    
    public void setTopBorder(final int topBorder) {
        this.topBorder = topBorder;
    }
    
    public int getTopBorder() {
        return this.topBorder;
    }
    
    public void setBottomBorder(final int bottomBorder) {
        this.bottomBorder = bottomBorder;
    }
    
    public int getBottomBorder() {
        return this.bottomBorder;
    }
    
    public void setBorderPaint(final Paint borderPaint) {
        this.borderPaint = borderPaint;
    }
    
    public Paint getBorderPaint() {
        return this.borderPaint;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            dst = new BufferedImage(width + this.leftBorder + this.rightBorder, height + this.topBorder + this.bottomBorder, src.getType());
        }
        final Graphics2D g = dst.createGraphics();
        if (this.borderPaint != null) {
            g.setPaint(this.borderPaint);
            if (this.leftBorder > 0) {
                g.fillRect(0, 0, this.leftBorder, height);
            }
            if (this.rightBorder > 0) {
                g.fillRect(width - this.rightBorder, 0, this.rightBorder, height);
            }
            if (this.topBorder > 0) {
                g.fillRect(this.leftBorder, 0, width - this.leftBorder - this.rightBorder, this.topBorder);
            }
            if (this.bottomBorder > 0) {
                g.fillRect(this.leftBorder, height - this.bottomBorder, width - this.leftBorder - this.rightBorder, this.bottomBorder);
            }
        }
        g.drawRenderedImage(src, AffineTransform.getTranslateInstance(this.leftBorder, this.rightBorder));
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Distort/Border...";
    }
}
