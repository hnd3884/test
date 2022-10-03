package com.lowagie.text.pdf;

import java.util.Iterator;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PdfStructureTreeRoot extends PdfDictionary
{
    private Map<Integer, PdfArray> parentTree;
    private PdfIndirectReference reference;
    private PdfWriter writer;
    
    PdfStructureTreeRoot(final PdfWriter writer) {
        super(PdfName.STRUCTTREEROOT);
        this.parentTree = new HashMap<Integer, PdfArray>();
        this.writer = writer;
        this.reference = writer.getPdfIndirectReference();
    }
    
    public void mapRole(final PdfName used, final PdfName standard) {
        PdfDictionary rm = (PdfDictionary)this.get(PdfName.ROLEMAP);
        if (rm == null) {
            rm = new PdfDictionary();
            this.put(PdfName.ROLEMAP, rm);
        }
        rm.put(used, standard);
    }
    
    public PdfWriter getWriter() {
        return this.writer;
    }
    
    public PdfIndirectReference getReference() {
        return this.reference;
    }
    
    void setPageMark(final int page, final PdfIndirectReference struc) {
        final Integer i = page;
        PdfArray ar = this.parentTree.get(i);
        if (ar == null) {
            ar = new PdfArray();
            this.parentTree.put(i, ar);
        }
        ar.add(struc);
    }
    
    private void nodeProcess(final PdfDictionary struc, final PdfIndirectReference reference) throws IOException {
        final PdfObject obj = struc.get(PdfName.K);
        if (obj != null && obj.isArray() && !((PdfArray)obj).getElements().get(0).isNumber()) {
            final PdfArray ar = (PdfArray)obj;
            final List<PdfObject> a = ar.getElements();
            for (int k = 0; k < a.size(); ++k) {
                final PdfStructureElement e = a.get(k);
                a.set(k, e.getReference());
                this.nodeProcess(e, e.getReference());
            }
        }
        if (reference != null) {
            this.writer.addToBody(struc, reference);
        }
    }
    
    void buildTree() throws IOException {
        final Map<Integer, PdfIndirectReference> numTree = new HashMap<Integer, PdfIndirectReference>();
        for (final Integer i : this.parentTree.keySet()) {
            final PdfArray ar = this.parentTree.get(i);
            numTree.put(i, this.writer.addToBody(ar).getIndirectReference());
        }
        final PdfDictionary dicTree = PdfNumberTree.writeTree(numTree, this.writer);
        if (dicTree != null) {
            this.put(PdfName.PARENTTREE, this.writer.addToBody(dicTree).getIndirectReference());
        }
        this.nodeProcess(this, this.reference);
    }
}
