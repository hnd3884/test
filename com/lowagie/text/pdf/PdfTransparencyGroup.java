package com.lowagie.text.pdf;

public class PdfTransparencyGroup extends PdfDictionary
{
    public PdfTransparencyGroup() {
        this.put(PdfName.S, PdfName.TRANSPARENCY);
    }
    
    public void setIsolated(final boolean isolated) {
        if (isolated) {
            this.put(PdfName.I, PdfBoolean.PDFTRUE);
        }
        else {
            this.remove(PdfName.I);
        }
    }
    
    public void setKnockout(final boolean knockout) {
        if (knockout) {
            this.put(PdfName.K, PdfBoolean.PDFTRUE);
        }
        else {
            this.remove(PdfName.K);
        }
    }
}
