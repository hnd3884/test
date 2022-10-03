package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;

public class PdfRectangle extends PdfArray
{
    private float llx;
    private float lly;
    private float urx;
    private float ury;
    
    public PdfRectangle(final float llx, final float lly, final float urx, final float ury, final int rotation) {
        this.llx = 0.0f;
        this.lly = 0.0f;
        this.urx = 0.0f;
        this.ury = 0.0f;
        if (rotation == 90 || rotation == 270) {
            this.llx = lly;
            this.lly = llx;
            this.urx = ury;
            this.ury = urx;
        }
        else {
            this.llx = llx;
            this.lly = lly;
            this.urx = urx;
            this.ury = ury;
        }
        super.add(new PdfNumber(this.llx));
        super.add(new PdfNumber(this.lly));
        super.add(new PdfNumber(this.urx));
        super.add(new PdfNumber(this.ury));
    }
    
    public PdfRectangle(final float llx, final float lly, final float urx, final float ury) {
        this(llx, lly, urx, ury, 0);
    }
    
    public PdfRectangle(final float urx, final float ury, final int rotation) {
        this(0.0f, 0.0f, urx, ury, rotation);
    }
    
    public PdfRectangle(final float urx, final float ury) {
        this(0.0f, 0.0f, urx, ury, 0);
    }
    
    public PdfRectangle(final Rectangle rectangle, final int rotation) {
        this(rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), rotation);
    }
    
    public PdfRectangle(final Rectangle rectangle) {
        this(rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), 0);
    }
    
    public Rectangle getRectangle() {
        return new Rectangle(this.left(), this.bottom(), this.right(), this.top());
    }
    
    @Override
    public boolean add(final PdfObject object) {
        return false;
    }
    
    @Override
    public boolean add(final float[] values) {
        return false;
    }
    
    @Override
    public boolean add(final int[] values) {
        return false;
    }
    
    @Override
    public void addFirst(final PdfObject object) {
    }
    
    public float left() {
        return this.llx;
    }
    
    public float right() {
        return this.urx;
    }
    
    public float top() {
        return this.ury;
    }
    
    public float bottom() {
        return this.lly;
    }
    
    public float left(final int margin) {
        return this.llx + margin;
    }
    
    public float right(final int margin) {
        return this.urx - margin;
    }
    
    public float top(final int margin) {
        return this.ury - margin;
    }
    
    public float bottom(final int margin) {
        return this.lly + margin;
    }
    
    public float width() {
        return this.urx - this.llx;
    }
    
    public float height() {
        return this.ury - this.lly;
    }
    
    public PdfRectangle rotate() {
        return new PdfRectangle(this.lly, this.llx, this.ury, this.urx, 0);
    }
}
