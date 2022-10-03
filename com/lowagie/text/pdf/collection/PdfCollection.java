package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfDictionary;

public class PdfCollection extends PdfDictionary
{
    public static final int DETAILS = 0;
    public static final int TILE = 1;
    public static final int HIDDEN = 2;
    
    public PdfCollection(final int type) {
        super(PdfName.COLLECTION);
        switch (type) {
            case 1: {
                this.put(PdfName.VIEW, PdfName.T);
                break;
            }
            case 2: {
                this.put(PdfName.VIEW, PdfName.H);
                break;
            }
            default: {
                this.put(PdfName.VIEW, PdfName.D);
                break;
            }
        }
    }
    
    public void setInitialDocument(final String description) {
        this.put(PdfName.D, new PdfString(description, null));
    }
    
    public void setSchema(final PdfCollectionSchema schema) {
        this.put(PdfName.SCHEMA, schema);
    }
    
    public PdfCollectionSchema getSchema() {
        return (PdfCollectionSchema)this.get(PdfName.SCHEMA);
    }
    
    public void setSort(final PdfCollectionSort sort) {
        this.put(PdfName.SORT, sort);
    }
}
