package com.lowagie.text.pdf;

public class StampContent extends PdfContentByte
{
    PdfStamperImp.PageStamp ps;
    PageResources pageResources;
    
    StampContent(final PdfStamperImp stamper, final PdfStamperImp.PageStamp ps) {
        super(stamper);
        this.ps = ps;
        this.pageResources = ps.pageResources;
    }
    
    @Override
    public void setAction(final PdfAction action, final float llx, final float lly, final float urx, final float ury) {
        ((PdfStamperImp)this.writer).addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action), this.ps.pageN);
    }
    
    @Override
    public PdfContentByte getDuplicate() {
        return new StampContent((PdfStamperImp)this.writer, this.ps);
    }
    
    @Override
    PageResources getPageResources() {
        return this.pageResources;
    }
    
    @Override
    void addAnnotation(final PdfAnnotation annot) {
        ((PdfStamperImp)this.writer).addAnnotation(annot, this.ps.pageN);
    }
}
