package com.lowagie.text.pdf;

import java.util.Set;
import java.io.IOException;
import java.util.Iterator;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class PdfDictionary extends PdfObject
{
    public static final PdfName FONT;
    public static final PdfName OUTLINES;
    public static final PdfName PAGE;
    public static final PdfName PAGES;
    public static final PdfName CATALOG;
    private PdfName dictionaryType;
    protected Map<PdfName, PdfObject> hashMap;
    
    public PdfDictionary() {
        super(6);
        this.dictionaryType = null;
        this.hashMap = new HashMap<PdfName, PdfObject>();
    }
    
    public PdfDictionary(final PdfName type) {
        this();
        this.dictionaryType = type;
        this.put(PdfName.TYPE, this.dictionaryType);
    }
    
    @Override
    public void toPdf(final PdfWriter writer, final OutputStream os) throws IOException {
        os.write(60);
        os.write(60);
        for (final PdfName pdfName : this.hashMap.keySet()) {
            final PdfObject value = this.hashMap.get(pdfName);
            pdfName.toPdf(writer, os);
            final int type = value.type();
            if (type != 5 && type != 6 && type != 4 && type != 3) {
                os.write(32);
            }
            value.toPdf(writer, os);
        }
        os.write(62);
        os.write(62);
    }
    
    @Override
    public String toString() {
        if (this.get(PdfName.TYPE) == null) {
            return "Dictionary";
        }
        return "Dictionary of type: " + this.get(PdfName.TYPE);
    }
    
    public void put(final PdfName key, final PdfObject object) {
        if (object == null || object.isNull()) {
            this.hashMap.remove(key);
        }
        else {
            this.hashMap.put(key, object);
        }
    }
    
    public void putEx(final PdfName key, final PdfObject value) {
        if (value == null) {
            return;
        }
        this.put(key, value);
    }
    
    public void putAll(final PdfDictionary dic) {
        this.hashMap.putAll(dic.hashMap);
    }
    
    public void remove(final PdfName key) {
        this.hashMap.remove(key);
    }
    
    public PdfObject get(final PdfName key) {
        return this.hashMap.get(key);
    }
    
    public PdfObject getDirectObject(final PdfName key) {
        return PdfReader.getPdfObject(this.get(key));
    }
    
    public Set getKeys() {
        return this.hashMap.keySet();
    }
    
    public int size() {
        return this.hashMap.size();
    }
    
    public boolean contains(final PdfName key) {
        return this.hashMap.containsKey(key);
    }
    
    public boolean isFont() {
        return PdfDictionary.FONT.equals(this.dictionaryType);
    }
    
    public boolean isPage() {
        return PdfDictionary.PAGE.equals(this.dictionaryType);
    }
    
    public boolean isPages() {
        return PdfDictionary.PAGES.equals(this.dictionaryType);
    }
    
    public boolean isCatalog() {
        return PdfDictionary.CATALOG.equals(this.dictionaryType);
    }
    
    public boolean isOutlineTree() {
        return PdfDictionary.OUTLINES.equals(this.dictionaryType);
    }
    
    public void merge(final PdfDictionary other) {
        this.hashMap.putAll(other.hashMap);
    }
    
    public void mergeDifferent(final PdfDictionary other) {
        for (final PdfName key : other.hashMap.keySet()) {
            if (!this.hashMap.containsKey(key)) {
                this.hashMap.put(key, other.hashMap.get(key));
            }
        }
    }
    
    public PdfDictionary getAsDict(final PdfName key) {
        PdfDictionary dict = null;
        final PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isDictionary()) {
            dict = (PdfDictionary)orig;
        }
        return dict;
    }
    
    public PdfArray getAsArray(final PdfName key) {
        PdfArray array = null;
        final PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isArray()) {
            array = (PdfArray)orig;
        }
        return array;
    }
    
    public PdfStream getAsStream(final PdfName key) {
        PdfStream stream = null;
        final PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isStream()) {
            stream = (PdfStream)orig;
        }
        return stream;
    }
    
    public PdfString getAsString(final PdfName key) {
        PdfString string = null;
        final PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isString()) {
            string = (PdfString)orig;
        }
        return string;
    }
    
    public PdfNumber getAsNumber(final PdfName key) {
        PdfNumber number = null;
        final PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isNumber()) {
            number = (PdfNumber)orig;
        }
        return number;
    }
    
    public PdfName getAsName(final PdfName key) {
        PdfName name = null;
        final PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isName()) {
            name = (PdfName)orig;
        }
        return name;
    }
    
    public PdfBoolean getAsBoolean(final PdfName key) {
        PdfBoolean bool = null;
        final PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isBoolean()) {
            bool = (PdfBoolean)orig;
        }
        return bool;
    }
    
    public PdfIndirectReference getAsIndirectObject(final PdfName key) {
        PdfIndirectReference ref = null;
        final PdfObject orig = this.get(key);
        if (orig != null && orig.isIndirect()) {
            ref = (PdfIndirectReference)orig;
        }
        return ref;
    }
    
    static {
        FONT = PdfName.FONT;
        OUTLINES = PdfName.OUTLINES;
        PAGE = PdfName.PAGE;
        PAGES = PdfName.PAGES;
        CATALOG = PdfName.CATALOG;
    }
}
