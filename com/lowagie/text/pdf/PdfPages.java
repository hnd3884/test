package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Document;
import java.io.IOException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.ExceptionConverter;
import java.util.ArrayList;

public class PdfPages
{
    private ArrayList pages;
    private ArrayList parents;
    private int leafSize;
    private PdfWriter writer;
    private PdfIndirectReference topParent;
    
    PdfPages(final PdfWriter writer) {
        this.pages = new ArrayList();
        this.parents = new ArrayList();
        this.leafSize = 10;
        this.writer = writer;
    }
    
    void addPage(final PdfDictionary page) {
        try {
            if (this.pages.size() % this.leafSize == 0) {
                this.parents.add(this.writer.getPdfIndirectReference());
            }
            final PdfIndirectReference parent = this.parents.get(this.parents.size() - 1);
            page.put(PdfName.PARENT, parent);
            final PdfIndirectReference current = this.writer.getCurrentPage();
            this.writer.addToBody(page, current);
            this.pages.add(current);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    PdfIndirectReference addPageRef(final PdfIndirectReference pageRef) {
        try {
            if (this.pages.size() % this.leafSize == 0) {
                this.parents.add(this.writer.getPdfIndirectReference());
            }
            this.pages.add(pageRef);
            return this.parents.get(this.parents.size() - 1);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    PdfIndirectReference writePageTree() throws IOException {
        if (this.pages.isEmpty()) {
            throw new IOException(MessageLocalization.getComposedMessage("the.document.has.no.pages"));
        }
        int leaf = 1;
        ArrayList tParents = this.parents;
        ArrayList tPages = this.pages;
        ArrayList nextParents = new ArrayList();
        while (true) {
            leaf *= this.leafSize;
            final int stdCount = this.leafSize;
            int rightCount = tPages.size() % this.leafSize;
            if (rightCount == 0) {
                rightCount = this.leafSize;
            }
            for (int p = 0; p < tParents.size(); ++p) {
                int thisLeaf = leaf;
                int count;
                if (p == tParents.size() - 1) {
                    count = rightCount;
                    thisLeaf = this.pages.size() % leaf;
                    if (thisLeaf == 0) {
                        thisLeaf = leaf;
                    }
                }
                else {
                    count = stdCount;
                }
                final PdfDictionary top = new PdfDictionary(PdfName.PAGES);
                top.put(PdfName.COUNT, new PdfNumber(thisLeaf));
                final PdfArray kids = new PdfArray(tPages.subList(p * stdCount, p * stdCount + count));
                top.put(PdfName.KIDS, kids);
                if (tParents.size() > 1) {
                    if (p % this.leafSize == 0) {
                        nextParents.add(this.writer.getPdfIndirectReference());
                    }
                    top.put(PdfName.PARENT, nextParents.get(p / this.leafSize));
                }
                else {
                    top.put(PdfName.ITXT, new PdfString(Document.getRelease()));
                }
                this.writer.addToBody(top, tParents.get(p));
            }
            if (tParents.size() == 1) {
                break;
            }
            tPages = tParents;
            tParents = nextParents;
            nextParents = new ArrayList();
        }
        return this.topParent = tParents.get(0);
    }
    
    PdfIndirectReference getTopParent() {
        return this.topParent;
    }
    
    void setLinearMode(final PdfIndirectReference topParent) {
        if (this.parents.size() > 1) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("linear.page.mode.can.only.be.called.with.a.single.parent"));
        }
        if (topParent != null) {
            this.topParent = topParent;
            this.parents.clear();
            this.parents.add(topParent);
        }
        this.leafSize = 10000000;
    }
    
    void addPage(final PdfIndirectReference page) {
        this.pages.add(page);
    }
    
    int reorderPages(final int[] order) throws DocumentException {
        if (order == null) {
            return this.pages.size();
        }
        if (this.parents.size() > 1) {
            throw new DocumentException(MessageLocalization.getComposedMessage("page.reordering.requires.a.single.parent.in.the.page.tree.call.pdfwriter.setlinearmode.after.open"));
        }
        if (order.length != this.pages.size()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("page.reordering.requires.an.array.with.the.same.size.as.the.number.of.pages"));
        }
        final int max = this.pages.size();
        final boolean[] temp = new boolean[max];
        for (final int p : order) {
            if (p < 1 || p > max) {
                throw new DocumentException(MessageLocalization.getComposedMessage("page.reordering.requires.pages.between.1.and.1.found.2", String.valueOf(max), String.valueOf(p)));
            }
            if (temp[p - 1]) {
                throw new DocumentException(MessageLocalization.getComposedMessage("page.reordering.requires.no.page.repetition.page.1.is.repeated", p));
            }
            temp[p - 1] = true;
        }
        final Object[] copy = this.pages.toArray();
        for (int i = 0; i < max; ++i) {
            this.pages.set(i, copy[order[i] - 1]);
        }
        return max;
    }
}
