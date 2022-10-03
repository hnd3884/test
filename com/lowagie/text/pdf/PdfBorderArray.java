package com.lowagie.text.pdf;

public class PdfBorderArray extends PdfArray
{
    public PdfBorderArray(final float hRadius, final float vRadius, final float width) {
        this(hRadius, vRadius, width, null);
    }
    
    public PdfBorderArray(final float hRadius, final float vRadius, final float width, final PdfDashPattern dash) {
        super(new PdfNumber(hRadius));
        this.add(new PdfNumber(vRadius));
        this.add(new PdfNumber(width));
        if (dash != null) {
            this.add(dash);
        }
    }
}
