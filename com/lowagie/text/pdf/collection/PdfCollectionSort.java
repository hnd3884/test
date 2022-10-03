package com.lowagie.text.pdf.collection;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfDictionary;

public class PdfCollectionSort extends PdfDictionary
{
    public PdfCollectionSort(final String key) {
        super(PdfName.COLLECTIONSORT);
        this.put(PdfName.S, new PdfName(key));
    }
    
    public PdfCollectionSort(final String[] keys) {
        super(PdfName.COLLECTIONSORT);
        final PdfArray array = new PdfArray();
        for (int i = 0; i < keys.length; ++i) {
            array.add(new PdfName(keys[i]));
        }
        this.put(PdfName.S, array);
    }
    
    public void setSortOrder(final boolean ascending) {
        final PdfObject o = this.get(PdfName.S);
        if (o instanceof PdfName) {
            this.put(PdfName.A, new PdfBoolean(ascending));
            return;
        }
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.have.to.define.a.boolean.array.for.this.collection.sort.dictionary"));
    }
    
    public void setSortOrder(final boolean[] ascending) {
        final PdfObject o = this.get(PdfName.S);
        if (!(o instanceof PdfArray)) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.need.a.single.boolean.for.this.collection.sort.dictionary"));
        }
        if (((PdfArray)o).size() != ascending.length) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.number.of.booleans.in.this.array.doesn.t.correspond.with.the.number.of.fields"));
        }
        final PdfArray array = new PdfArray();
        for (int i = 0; i < ascending.length; ++i) {
            array.add(new PdfBoolean(ascending[i]));
        }
        this.put(PdfName.A, array);
    }
}
