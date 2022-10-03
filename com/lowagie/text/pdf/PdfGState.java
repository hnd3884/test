package com.lowagie.text.pdf;

public class PdfGState extends PdfDictionary
{
    public static final PdfName BM_NORMAL;
    public static final PdfName BM_COMPATIBLE;
    public static final PdfName BM_MULTIPLY;
    public static final PdfName BM_SCREEN;
    public static final PdfName BM_OVERLAY;
    public static final PdfName BM_DARKEN;
    public static final PdfName BM_LIGHTEN;
    public static final PdfName BM_COLORDODGE;
    public static final PdfName BM_COLORBURN;
    public static final PdfName BM_HARDLIGHT;
    public static final PdfName BM_SOFTLIGHT;
    public static final PdfName BM_DIFFERENCE;
    public static final PdfName BM_EXCLUSION;
    
    public void setOverPrintStroking(final boolean ov) {
        this.put(PdfName.OP, ov ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }
    
    public void setOverPrintNonStroking(final boolean ov) {
        this.put(PdfName.op, ov ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }
    
    public void setOverPrintMode(final int ov) {
        this.put(PdfName.OPM, new PdfNumber((int)((ov != 0) ? 1 : 0)));
    }
    
    public void setStrokeOpacity(final float n) {
        this.put(PdfName.CA, new PdfNumber(n));
    }
    
    public void setFillOpacity(final float n) {
        this.put(PdfName.ca, new PdfNumber(n));
    }
    
    public void setAlphaIsShape(final boolean v) {
        this.put(PdfName.AIS, v ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }
    
    public void setTextKnockout(final boolean v) {
        this.put(PdfName.TK, v ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }
    
    public void setBlendMode(final PdfName bm) {
        this.put(PdfName.BM, bm);
    }
    
    static {
        BM_NORMAL = new PdfName("Normal");
        BM_COMPATIBLE = new PdfName("Compatible");
        BM_MULTIPLY = new PdfName("Multiply");
        BM_SCREEN = new PdfName("Screen");
        BM_OVERLAY = new PdfName("Overlay");
        BM_DARKEN = new PdfName("Darken");
        BM_LIGHTEN = new PdfName("Lighten");
        BM_COLORDODGE = new PdfName("ColorDodge");
        BM_COLORBURN = new PdfName("ColorBurn");
        BM_HARDLIGHT = new PdfName("HardLight");
        BM_SOFTLIGHT = new PdfName("SoftLight");
        BM_DIFFERENCE = new PdfName("Difference");
        BM_EXCLUSION = new PdfName("Exclusion");
    }
}
