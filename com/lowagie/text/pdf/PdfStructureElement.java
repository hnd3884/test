package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;

public class PdfStructureElement extends PdfDictionary
{
    private PdfStructureElement parent;
    private PdfStructureTreeRoot top;
    private PdfIndirectReference reference;
    
    public PdfStructureElement(final PdfStructureElement parent, final PdfName structureType) {
        this.top = parent.top;
        this.init(parent, structureType);
        this.parent = parent;
        this.put(PdfName.P, parent.reference);
    }
    
    public PdfStructureElement(final PdfStructureTreeRoot parent, final PdfName structureType) {
        this.init(this.top = parent, structureType);
        this.put(PdfName.P, parent.getReference());
    }
    
    private void init(final PdfDictionary parent, final PdfName structureType) {
        final PdfObject kido = parent.get(PdfName.K);
        PdfArray kids = null;
        if (kido != null && !kido.isArray()) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.parent.has.already.another.function"));
        }
        if (kido == null) {
            kids = new PdfArray();
            parent.put(PdfName.K, kids);
        }
        else {
            kids = (PdfArray)kido;
        }
        kids.add(this);
        this.put(PdfName.S, structureType);
        this.reference = this.top.getWriter().getPdfIndirectReference();
    }
    
    public PdfDictionary getParent() {
        return this.parent;
    }
    
    void setPageMark(final int page, final int mark) {
        if (mark >= 0) {
            this.put(PdfName.K, new PdfNumber(mark));
        }
        this.top.setPageMark(page, this.reference);
    }
    
    public PdfIndirectReference getReference() {
        return this.reference;
    }
}
