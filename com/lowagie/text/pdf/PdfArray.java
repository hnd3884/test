package com.lowagie.text.pdf;

import java.util.ListIterator;
import java.io.IOException;
import java.util.Iterator;
import java.io.OutputStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class PdfArray extends PdfObject
{
    protected List<PdfObject> arrayList;
    
    public PdfArray() {
        super(5);
        this.arrayList = new ArrayList<PdfObject>();
    }
    
    public PdfArray(final PdfObject object) {
        this();
        this.arrayList.add(object);
    }
    
    public PdfArray(final float[] values) {
        this();
        this.add(values);
    }
    
    public PdfArray(final int[] values) {
        this();
        this.add(values);
    }
    
    public PdfArray(final List<PdfObject> pdfObjectList) {
        this();
        if (pdfObjectList != null) {
            this.arrayList.addAll(pdfObjectList);
        }
    }
    
    public PdfArray(final PdfArray array) {
        this(array.getElements());
    }
    
    @Override
    public void toPdf(final PdfWriter writer, final OutputStream os) throws IOException {
        os.write(91);
        final Iterator<PdfObject> i = this.arrayList.iterator();
        if (i.hasNext()) {
            PdfObject object = i.next();
            if (object == null) {
                object = PdfNull.PDFNULL;
            }
            object.toPdf(writer, os);
        }
        while (i.hasNext()) {
            PdfObject object = i.next();
            if (object == null) {
                object = PdfNull.PDFNULL;
            }
            final int type = object.type();
            if (type != 5 && type != 6 && type != 4 && type != 3) {
                os.write(32);
            }
            object.toPdf(writer, os);
        }
        os.write(93);
    }
    
    @Override
    public String toString() {
        return this.arrayList.toString();
    }
    
    public PdfObject set(final int idx, final PdfObject obj) {
        return this.arrayList.set(idx, obj);
    }
    
    public PdfObject remove(final int idx) {
        return this.arrayList.remove(idx);
    }
    
    @Deprecated
    public List<PdfObject> getArrayList() {
        return this.getElements();
    }
    
    public List<PdfObject> getElements() {
        return new ArrayList<PdfObject>(this.arrayList);
    }
    
    public int size() {
        return this.arrayList.size();
    }
    
    public boolean isEmpty() {
        return this.arrayList.isEmpty();
    }
    
    public boolean add(final PdfObject object) {
        return this.arrayList.add(object);
    }
    
    public boolean add(final float[] values) {
        for (final float value : values) {
            this.arrayList.add(new PdfNumber(value));
        }
        return true;
    }
    
    public boolean add(final int[] values) {
        for (final int value : values) {
            this.arrayList.add(new PdfNumber(value));
        }
        return true;
    }
    
    public void add(final int index, final PdfObject element) {
        this.arrayList.add(index, element);
    }
    
    public void addFirst(final PdfObject object) {
        this.arrayList.add(0, object);
    }
    
    public boolean contains(final PdfObject object) {
        return this.arrayList.contains(object);
    }
    
    public ListIterator<PdfObject> listIterator() {
        return this.arrayList.listIterator();
    }
    
    public PdfObject getPdfObject(final int idx) {
        return this.arrayList.get(idx);
    }
    
    public PdfObject getDirectObject(final int idx) {
        return PdfReader.getPdfObject(this.getPdfObject(idx));
    }
    
    public PdfDictionary getAsDict(final int idx) {
        PdfDictionary dict = null;
        final PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isDictionary()) {
            dict = (PdfDictionary)orig;
        }
        return dict;
    }
    
    public PdfArray getAsArray(final int idx) {
        PdfArray array = null;
        final PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isArray()) {
            array = (PdfArray)orig;
        }
        return array;
    }
    
    public PdfStream getAsStream(final int idx) {
        PdfStream stream = null;
        final PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isStream()) {
            stream = (PdfStream)orig;
        }
        return stream;
    }
    
    public PdfString getAsString(final int idx) {
        PdfString string = null;
        final PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isString()) {
            string = (PdfString)orig;
        }
        return string;
    }
    
    public PdfNumber getAsNumber(final int idx) {
        PdfNumber number = null;
        final PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isNumber()) {
            number = (PdfNumber)orig;
        }
        return number;
    }
    
    public PdfName getAsName(final int idx) {
        PdfName name = null;
        final PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isName()) {
            name = (PdfName)orig;
        }
        return name;
    }
    
    public PdfBoolean getAsBoolean(final int idx) {
        PdfBoolean bool = null;
        final PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isBoolean()) {
            bool = (PdfBoolean)orig;
        }
        return bool;
    }
    
    public PdfIndirectReference getAsIndirectObject(final int idx) {
        PdfIndirectReference ref = null;
        final PdfObject orig = this.getPdfObject(idx);
        if (orig != null && orig.isIndirect()) {
            ref = (PdfIndirectReference)orig;
        }
        return ref;
    }
}
