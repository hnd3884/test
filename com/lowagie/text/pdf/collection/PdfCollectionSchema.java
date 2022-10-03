package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfDictionary;

public class PdfCollectionSchema extends PdfDictionary
{
    public PdfCollectionSchema() {
        super(PdfName.COLLECTIONSCHEMA);
    }
    
    public void addField(final String name, final PdfCollectionField field) {
        this.put(new PdfName(name), field);
    }
}
