package com.lowagie.text.pdf.collection;

import com.lowagie.text.error_messages.MessageLocalization;
import java.util.Calendar;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfDictionary;

public class PdfCollectionItem extends PdfDictionary
{
    PdfCollectionSchema schema;
    
    public PdfCollectionItem(final PdfCollectionSchema schema) {
        super(PdfName.COLLECTIONITEM);
        this.schema = schema;
    }
    
    public void addItem(final String key, final String value) {
        final PdfName fieldname = new PdfName(key);
        final PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
        this.put(fieldname, field.getValue(value));
    }
    
    public void addItem(final String key, final PdfString value) {
        final PdfName fieldname = new PdfName(key);
        final PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
        if (field.fieldType == 0) {
            this.put(fieldname, value);
        }
    }
    
    public void addItem(final String key, final PdfDate d) {
        final PdfName fieldname = new PdfName(key);
        final PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
        if (field.fieldType == 1) {
            this.put(fieldname, d);
        }
    }
    
    public void addItem(final String key, final PdfNumber n) {
        final PdfName fieldname = new PdfName(key);
        final PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
        if (field.fieldType == 2) {
            this.put(fieldname, n);
        }
    }
    
    public void addItem(final String key, final Calendar c) {
        this.addItem(key, new PdfDate(c));
    }
    
    public void addItem(final String key, final int i) {
        this.addItem(key, new PdfNumber(i));
    }
    
    public void addItem(final String key, final float f) {
        this.addItem(key, new PdfNumber(f));
    }
    
    public void addItem(final String key, final double d) {
        this.addItem(key, new PdfNumber(d));
    }
    
    public void setPrefix(final String key, final String prefix) {
        final PdfName fieldname = new PdfName(key);
        final PdfObject o = this.get(fieldname);
        if (o == null) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.must.set.a.value.before.adding.a.prefix"));
        }
        final PdfDictionary dict = new PdfDictionary(PdfName.COLLECTIONSUBITEM);
        dict.put(PdfName.D, o);
        dict.put(PdfName.P, new PdfString(prefix, "UnicodeBig"));
        this.put(fieldname, dict);
    }
}
