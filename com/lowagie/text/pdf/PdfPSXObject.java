package com.lowagie.text.pdf;

public class PdfPSXObject extends PdfTemplate
{
    protected PdfPSXObject() {
    }
    
    public PdfPSXObject(final PdfWriter wr) {
        super(wr);
    }
    
    @Override
    PdfStream getFormXObject(final int compressionLevel) {
        final PdfStream s = new PdfStream(this.content.toByteArray());
        s.put(PdfName.TYPE, PdfName.XOBJECT);
        s.put(PdfName.SUBTYPE, PdfName.PS);
        s.flateCompress(compressionLevel);
        return s;
    }
    
    @Override
    public PdfContentByte getDuplicate() {
        final PdfPSXObject tpl = new PdfPSXObject();
        tpl.writer = this.writer;
        tpl.pdf = this.pdf;
        tpl.thisReference = this.thisReference;
        tpl.pageResources = this.pageResources;
        tpl.separator = this.separator;
        return tpl;
    }
}
