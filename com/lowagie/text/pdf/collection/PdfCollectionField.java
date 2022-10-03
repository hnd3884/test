package com.lowagie.text.pdf.collection;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfDictionary;

public class PdfCollectionField extends PdfDictionary
{
    public static final int TEXT = 0;
    public static final int DATE = 1;
    public static final int NUMBER = 2;
    public static final int FILENAME = 3;
    public static final int DESC = 4;
    public static final int MODDATE = 5;
    public static final int CREATIONDATE = 6;
    public static final int SIZE = 7;
    protected int fieldType;
    
    public PdfCollectionField(final String name, final int type) {
        super(PdfName.COLLECTIONFIELD);
        this.put(PdfName.N, new PdfString(name, "UnicodeBig"));
        switch (this.fieldType = type) {
            default: {
                this.put(PdfName.SUBTYPE, PdfName.S);
                break;
            }
            case 1: {
                this.put(PdfName.SUBTYPE, PdfName.D);
                break;
            }
            case 2: {
                this.put(PdfName.SUBTYPE, PdfName.N);
                break;
            }
            case 3: {
                this.put(PdfName.SUBTYPE, PdfName.F);
                break;
            }
            case 4: {
                this.put(PdfName.SUBTYPE, PdfName.DESC);
                break;
            }
            case 5: {
                this.put(PdfName.SUBTYPE, PdfName.MODDATE);
                break;
            }
            case 6: {
                this.put(PdfName.SUBTYPE, PdfName.CREATIONDATE);
                break;
            }
            case 7: {
                this.put(PdfName.SUBTYPE, PdfName.SIZE);
                break;
            }
        }
    }
    
    public void setOrder(final int i) {
        this.put(PdfName.O, new PdfNumber(i));
    }
    
    public void setVisible(final boolean visible) {
        this.put(PdfName.V, new PdfBoolean(visible));
    }
    
    public void setEditable(final boolean editable) {
        this.put(PdfName.E, new PdfBoolean(editable));
    }
    
    public boolean isCollectionItem() {
        switch (this.fieldType) {
            case 0:
            case 1:
            case 2: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public PdfObject getValue(final String v) {
        switch (this.fieldType) {
            case 0: {
                return new PdfString(v, "UnicodeBig");
            }
            case 1: {
                return new PdfDate(PdfDate.decode(v));
            }
            case 2: {
                return new PdfNumber(v);
            }
            default: {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("1.is.not.an.acceptable.value.for.the.field.2", v, this.get(PdfName.N).toString()));
            }
        }
    }
}
