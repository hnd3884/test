package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.image.Raster;
import java.awt.RenderingHints;
import java.awt.image.BandCombineOp;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ShadowFilter extends AbstractBufferedImageOp
{
    private float radius;
    private float angle;
    private float distance;
    private float opacity;
    private boolean addMargins;
    private boolean shadowOnly;
    private int shadowColor;
    
    public ShadowFilter() {
        this.radius = 5.0f;
        this.angle = 4.712389f;
        this.distance = 5.0f;
        this.opacity = 0.5f;
        this.addMargins = false;
        this.shadowOnly = false;
        this.shadowColor = -16777216;
    }
    
    public ShadowFilter(final float radius, final float xOffset, final float yOffset, final float opacity) {
        this.radius = 5.0f;
        this.angle = 4.712389f;
        this.distance = 5.0f;
        this.opacity = 0.5f;
        this.addMargins = false;
        this.shadowOnly = false;
        this.shadowColor = -16777216;
        this.radius = radius;
        this.angle = (float)Math.atan2(yOffset, xOffset);
        this.distance = (float)Math.sqrt(xOffset * xOffset + yOffset * yOffset);
        this.opacity = opacity;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setDistance(final float distance) {
        this.distance = distance;
    }
    
    public float getDistance() {
        return this.distance;
    }
    
    public void setRadius(final float radius) {
        this.radius = radius;
    }
    
    public float getRadius() {
        return this.radius;
    }
    
    public void setOpacity(final float opacity) {
        this.opacity = opacity;
    }
    
    public float getOpacity() {
        return this.opacity;
    }
    
    public void setShadowColor(final int shadowColor) {
        this.shadowColor = shadowColor;
    }
    
    public int getShadowColor() {
        return this.shadowColor;
    }
    
    public void setAddMargins(final boolean addMargins) {
        this.addMargins = addMargins;
    }
    
    public boolean getAddMargins() {
        return this.addMargins;
    }
    
    public void setShadowOnly(final boolean shadowOnly) {
        this.shadowOnly = shadowOnly;
    }
    
    public boolean getShadowOnly() {
        return this.shadowOnly;
    }
    
    @Override
    public Rectangle2D getBounds2D(final BufferedImage src) {
        final Rectangle r = new Rectangle(0, 0, src.getWidth(), src.getHeight());
        if (this.addMargins) {
            final float xOffset = this.distance * (float)Math.cos(this.angle);
            final float yOffset = -this.distance * (float)Math.sin(this.angle);
            final Rectangle rectangle = r;
            rectangle.width += (int)(Math.abs(xOffset) + 2.0f * this.radius);
            final Rectangle rectangle2 = r;
            rectangle2.height += (int)(Math.abs(yOffset) + 2.0f * this.radius);
        }
        return r;
    }
    
    @Override
    public Point2D getPoint2D(final Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        if (this.addMargins) {
            final float xOffset = this.distance * (float)Math.cos(this.angle);
            final float yOffset = -this.distance * (float)Math.sin(this.angle);
            final float topShadow = Math.max(0.0f, this.radius - yOffset);
            final float leftShadow = Math.max(0.0f, this.radius - xOffset);
            dstPt.setLocation(srcPt.getX() + leftShadow, srcPt.getY() + topShadow);
        }
        else {
            dstPt.setLocation(srcPt.getX(), srcPt.getY());
        }
        return dstPt;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            if (this.addMargins) {
                final ColorModel cm = src.getColorModel();
                dst = new BufferedImage(cm, cm.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), cm.isAlphaPremultiplied(), null);
            }
            else {
                dst = this.createCompatibleDestImage(src, null);
            }
        }
        final float shadowR = (this.shadowColor >> 16 & 0xFF) / 255.0f;
        final float shadowG = (this.shadowColor >> 8 & 0xFF) / 255.0f;
        final float shadowB = (this.shadowColor & 0xFF) / 255.0f;
        final float[][] extractAlpha = { { 0.0f, 0.0f, 0.0f, shadowR }, { 0.0f, 0.0f, 0.0f, shadowG }, { 0.0f, 0.0f, 0.0f, shadowB }, { 0.0f, 0.0f, 0.0f, this.opacity } };
        BufferedImage shadow = new BufferedImage(width, height, 2);
        new BandCombineOp(extractAlpha, null).filter(src.getRaster(), shadow.getRaster());
        shadow = new GaussianFilter(this.radius).filter(shadow, null);
        final float xOffset = this.distance * (float)Math.cos(this.angle);
        final float yOffset = -this.distance * (float)Math.sin(this.angle);
        final Graphics2D g = dst.createGraphics();
        g.setComposite(AlphaComposite.getInstance(3, this.opacity));
        if (this.addMargins) {
            final float radius2 = this.radius / 2.0f;
            final float topShadow = Math.max(0.0f, this.radius - yOffset);
            final float leftShadow = Math.max(0.0f, this.radius - xOffset);
            g.translate(topShadow, leftShadow);
        }
        g.drawRenderedImage(shadow, AffineTransform.getTranslateInstance(xOffset, yOffset));
        if (!this.shadowOnly) {
            g.setComposite(AlphaComposite.SrcOver);
            g.drawRenderedImage(src, null);
        }
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Stylize/Drop Shadow...";
    }
}
