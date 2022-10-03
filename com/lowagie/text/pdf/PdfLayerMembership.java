package com.lowagie.text.pdf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PdfLayerMembership extends PdfDictionary implements PdfOCG
{
    public static final PdfName ALLON;
    public static final PdfName ANYON;
    public static final PdfName ANYOFF;
    public static final PdfName ALLOFF;
    PdfIndirectReference ref;
    PdfArray members;
    Set<PdfLayer> layers;
    
    public PdfLayerMembership(final PdfWriter writer) {
        super(PdfName.OCMD);
        this.members = new PdfArray();
        this.layers = new HashSet<PdfLayer>();
        this.put(PdfName.OCGS, this.members);
        this.ref = writer.getPdfIndirectReference();
    }
    
    @Override
    public PdfIndirectReference getRef() {
        return this.ref;
    }
    
    public void addMember(final PdfLayer layer) {
        if (!this.layers.contains(layer)) {
            this.members.add(layer.getRef());
            this.layers.add(layer);
        }
    }
    
    public Collection getLayers() {
        return this.layers;
    }
    
    public void setVisibilityPolicy(final PdfName type) {
        this.put(PdfName.P, type);
    }
    
    @Override
    public PdfObject getPdfObject() {
        return this;
    }
    
    static {
        ALLON = new PdfName("AllOn");
        ANYON = new PdfName("AnyOn");
        ANYOFF = new PdfName("AnyOff");
        ALLOFF = new PdfName("AllOff");
    }
}
