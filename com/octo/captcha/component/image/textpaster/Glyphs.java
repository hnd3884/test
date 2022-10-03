package com.octo.captcha.component.image.textpaster;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.font.GlyphMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.awt.font.GlyphVector;
import java.util.List;

public class Glyphs
{
    List<GlyphVector> vectors;
    
    public Glyphs() {
        this.vectors = new ArrayList<GlyphVector>();
    }
    
    public void addGlyphVector(final GlyphVector glyphVector) {
        this.vectors.add(glyphVector);
    }
    
    public int size() {
        return this.vectors.size();
    }
    
    public GlyphVector get(final int n) {
        return this.vectors.get(n);
    }
    
    public double getBoundsX(final int n) {
        return this.getBounds(n).getX();
    }
    
    public double getBoundsY(final int n) {
        return this.getBounds(n).getY();
    }
    
    public double getBoundsWidth(final int n) {
        return this.getBounds(n).getWidth();
    }
    
    public double getBoundsHeight(final int n) {
        return this.getBounds(n).getHeight();
    }
    
    public double getX(final int n) {
        return this.get(n).getGlyphPosition(0).getX();
    }
    
    public double getY(final int n) {
        return this.get(n).getGlyphPosition(0).getY();
    }
    
    public Shape getOutline(final int n) {
        return this.get(n).getOutline();
    }
    
    public double getBoundsX() {
        return this.getBounds().getX();
    }
    
    public double getBoundsY() {
        return this.getBounds().getY();
    }
    
    public double getBoundsWidth() {
        return this.getBounds().getWidth();
    }
    
    public double getBoundsHeight() {
        return this.getBounds().getHeight();
    }
    
    public double getMaxX(final int n) {
        return this.getBounds(n).getMaxX();
    }
    
    public double getMaxY(final int n) {
        return this.getBounds(n).getMaxY();
    }
    
    public double getMinX(final int n) {
        return this.getBounds(n).getMinX();
    }
    
    public double getMinY(final int n) {
        return this.getBounds(n).getMinX();
    }
    
    public GlyphVector getGlyphVector(final int n) {
        return this.vectors.get(n);
    }
    
    public Rectangle2D getBounds(final int n) {
        return this.vectors.get(n).getVisualBounds();
    }
    
    public Rectangle2D getBounds() {
        Rectangle2D union = (this.size() > 0) ? this.getBounds(0) : new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);
        for (int i = 1; i < this.size(); ++i) {
            union = union.createUnion(this.getBounds(i));
        }
        return union;
    }
    
    public GlyphMetrics getMetrics(final int n) {
        return this.get(n).getGlyphMetrics(0);
    }
    
    public double getLSB(final int n) {
        return this.getMetrics(n).getLSB();
    }
    
    public double getRSB(final int n) {
        return this.getMetrics(n).getRSB();
    }
    
    public double getAdvance(final int n) {
        return this.getMetrics(n).getAdvance();
    }
    
    public double getInternalWidth(final int n) {
        return this.getAdvance(n) - this.getRSB(n) - this.getLSB(n);
    }
    
    public Rectangle2D getInternalBounds(final int n) {
        return this.getMetrics(n).getBounds2D();
    }
    
    public double getInternalBoundsX(final int n) {
        return this.getInternalBounds(n).getX();
    }
    
    public double getInternalBoundsY(final int n) {
        return this.getInternalBounds(n).getY();
    }
    
    public double getInternalBoundsWidth(final int n) {
        return this.getInternalBounds(n).getWidth();
    }
    
    public double getInternalBoundsHeigth(final int n) {
        return this.getInternalBounds(n).getHeight();
    }
    
    public double getAdvanceX(final int n) {
        return this.getMetrics(n).getAdvanceX();
    }
    
    public double getAdvanceY(final int n) {
        return this.getMetrics(n).getAdvanceY();
    }
    
    public double getMaxHeight() {
        double max = 0.0;
        for (int i = 1; i < this.size(); ++i) {
            max = Math.max(this.getBoundsHeight(i), max);
        }
        return max;
    }
    
    public double getMaxWidth() {
        double max = 0.0;
        for (int i = 1; i < this.size(); ++i) {
            max = Math.max(this.getBoundsWidth(i), max);
        }
        return max;
    }
    
    public void translate(final double n, final double n2) {
        for (int i = 0; i < this.size(); ++i) {
            this.translate(i, n, n2);
        }
    }
    
    public void translate(final int n, final double n2, final double n3) {
        this.setPosition(n, n2 + this.getX(n), n3 + this.getY(n));
    }
    
    public void setPosition(final int n, final double n2, final double n3) {
        this.vectors.get(n).setGlyphPosition(0, new Point2D.Double(n2, n3));
    }
    
    public void addAffineTransform(final AffineTransform affineTransform) {
        for (int i = 0; i < this.size(); ++i) {
            this.addAffineTransform(i, affineTransform);
        }
    }
    
    public void addAffineTransform(final int n, final AffineTransform affineTransform) {
        AffineTransform glyphTransform = this.vectors.get(n).getGlyphTransform(0);
        if (glyphTransform == null) {
            glyphTransform = affineTransform;
        }
        else {
            glyphTransform.concatenate(affineTransform);
        }
        this.vectors.get(n).setGlyphTransform(0, glyphTransform);
    }
    
    public void rotate(final int n, final double n2) {
        this.get(n).setGlyphTransform(0, AffineTransform.getRotateInstance(n2, this.getBoundsX(n) + this.getBoundsWidth(n) / 2.0, this.getBoundsY(n) + this.getBoundsHeight(n) / 2.0));
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("{Glyphs=");
        for (int i = 0; i < this.size(); ++i) {
            sb.append("\n\t");
            sb.append("{GlyphVector=" + i + " : ");
            for (int j = 0; j < this.get(i).getNumGlyphs(); ++j) {
                sb.append("Glyph=" + j);
                sb.append("; Bounds=");
                sb.append(this.get(i).getGlyphVisualBounds(j).getBounds2D());
                sb.append("; Font=");
                sb.append(this.get(i).getFont());
            }
            sb.append("}");
        }
        sb.append("\n");
        sb.append("Bounds : ");
        sb.append(this.getBounds());
        sb.append("}");
        return sb.toString();
    }
}
