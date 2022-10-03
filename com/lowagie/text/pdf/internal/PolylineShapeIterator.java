package com.lowagie.text.pdf.internal;

import java.util.NoSuchElementException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

public class PolylineShapeIterator implements PathIterator
{
    protected PolylineShape poly;
    protected AffineTransform affine;
    protected int index;
    
    PolylineShapeIterator(final PolylineShape l, final AffineTransform at) {
        this.poly = l;
        this.affine = at;
    }
    
    @Override
    public int currentSegment(final double[] coords) {
        if (this.isDone()) {
            throw new NoSuchElementException(MessageLocalization.getComposedMessage("line.iterator.out.of.bounds"));
        }
        final int type = (this.index != 0) ? 1 : 0;
        coords[0] = this.poly.x[this.index];
        coords[1] = this.poly.y[this.index];
        if (this.affine != null) {
            this.affine.transform(coords, 0, coords, 0, 1);
        }
        return type;
    }
    
    @Override
    public int currentSegment(final float[] coords) {
        if (this.isDone()) {
            throw new NoSuchElementException(MessageLocalization.getComposedMessage("line.iterator.out.of.bounds"));
        }
        final int type = (this.index != 0) ? 1 : 0;
        coords[0] = (float)this.poly.x[this.index];
        coords[1] = (float)this.poly.y[this.index];
        if (this.affine != null) {
            this.affine.transform(coords, 0, coords, 0, 1);
        }
        return type;
    }
    
    @Override
    public int getWindingRule() {
        return 1;
    }
    
    @Override
    public boolean isDone() {
        return this.index >= this.poly.np;
    }
    
    @Override
    public void next() {
        ++this.index;
    }
}
