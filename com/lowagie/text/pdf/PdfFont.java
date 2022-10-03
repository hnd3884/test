package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;

class PdfFont implements Comparable
{
    private BaseFont font;
    private float size;
    protected Image image;
    protected float hScale;
    
    PdfFont(final BaseFont bf, final float size) {
        this.hScale = 1.0f;
        this.size = size;
        this.font = bf;
    }
    
    @Override
    public int compareTo(final Object object) {
        if (this.image != null) {
            return 0;
        }
        if (object == null) {
            return -1;
        }
        try {
            final PdfFont pdfFont = (PdfFont)object;
            if (this.font != pdfFont.font) {
                return 1;
            }
            if (this.size() != pdfFont.size()) {
                return 2;
            }
            return 0;
        }
        catch (final ClassCastException cce) {
            return -2;
        }
    }
    
    float size() {
        if (this.image == null) {
            return this.size;
        }
        return this.image.getScaledHeight();
    }
    
    float width() {
        return this.width(32);
    }
    
    float width(final int character) {
        if (this.image == null) {
            return this.font.getWidthPoint(character, this.size) * this.hScale;
        }
        return this.image.getScaledWidth();
    }
    
    float width(final String s) {
        if (this.image == null) {
            return this.font.getWidthPoint(s, this.size) * this.hScale;
        }
        return this.image.getScaledWidth();
    }
    
    BaseFont getFont() {
        return this.font;
    }
    
    void setImage(final Image image) {
        this.image = image;
    }
    
    static PdfFont getDefaultFont() {
        try {
            final BaseFont bf = BaseFont.createFont("Helvetica", "Cp1252", false);
            return new PdfFont(bf, 12.0f);
        }
        catch (final Exception ee) {
            throw new ExceptionConverter(ee);
        }
    }
    
    void setHorizontalScaling(final float hScale) {
        this.hScale = hScale;
    }
}
