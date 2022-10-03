package com.lowagie.text.pdf.draw;

import com.lowagie.text.pdf.PdfContentByte;

public class DottedLineSeparator extends LineSeparator
{
    protected float gap;
    
    public DottedLineSeparator() {
        this.gap = 5.0f;
    }
    
    @Override
    public void draw(final PdfContentByte canvas, final float llx, final float lly, final float urx, final float ury, final float y) {
        canvas.saveState();
        canvas.setLineWidth(this.lineWidth);
        canvas.setLineCap(1);
        canvas.setLineDash(0.0f, this.gap, this.gap / 2.0f);
        this.drawLine(canvas, llx, urx, y);
        canvas.restoreState();
    }
    
    public float getGap() {
        return this.gap;
    }
    
    public void setGap(final float gap) {
        this.gap = gap;
    }
}
