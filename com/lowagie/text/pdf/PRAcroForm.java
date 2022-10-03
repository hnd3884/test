package com.lowagie.text.pdf;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;

public class PRAcroForm extends PdfDictionary
{
    ArrayList fields;
    ArrayList stack;
    HashMap fieldByName;
    PdfReader reader;
    
    public PRAcroForm(final PdfReader reader) {
        this.reader = reader;
        this.fields = new ArrayList();
        this.fieldByName = new HashMap();
        this.stack = new ArrayList();
    }
    
    @Override
    public int size() {
        return this.fields.size();
    }
    
    public ArrayList getFields() {
        return this.fields;
    }
    
    public FieldInformation getField(final String name) {
        return this.fieldByName.get(name);
    }
    
    public PRIndirectReference getRefByName(final String name) {
        final FieldInformation fi = this.fieldByName.get(name);
        if (fi == null) {
            return null;
        }
        return fi.getRef();
    }
    
    public void readAcroForm(final PdfDictionary root) {
        if (root == null) {
            return;
        }
        this.hashMap = root.hashMap;
        this.pushAttrib(root);
        final PdfArray fieldlist = (PdfArray)PdfReader.getPdfObjectRelease(root.get(PdfName.FIELDS));
        this.iterateFields(fieldlist, null, null);
    }
    
    protected void iterateFields(final PdfArray fieldlist, final PRIndirectReference fieldDict, final String title) {
        final Iterator it = fieldlist.listIterator();
        while (it.hasNext()) {
            final PRIndirectReference ref = it.next();
            final PdfDictionary dict = (PdfDictionary)PdfReader.getPdfObjectRelease(ref);
            PRIndirectReference myFieldDict = fieldDict;
            String myTitle = title;
            final PdfString tField = (PdfString)dict.get(PdfName.T);
            final boolean isFieldDict = tField != null;
            if (isFieldDict) {
                myFieldDict = ref;
                if (title == null) {
                    myTitle = tField.toString();
                }
                else {
                    myTitle = title + '.' + tField.toString();
                }
            }
            final PdfArray kids = (PdfArray)dict.get(PdfName.KIDS);
            if (kids != null) {
                this.pushAttrib(dict);
                this.iterateFields(kids, myFieldDict, myTitle);
                this.stack.remove(this.stack.size() - 1);
            }
            else {
                if (myFieldDict == null) {
                    continue;
                }
                PdfDictionary mergedDict = this.stack.get(this.stack.size() - 1);
                if (isFieldDict) {
                    mergedDict = this.mergeAttrib(mergedDict, dict);
                }
                mergedDict.put(PdfName.T, new PdfString(myTitle));
                final FieldInformation fi = new FieldInformation(myTitle, mergedDict, myFieldDict);
                this.fields.add(fi);
                this.fieldByName.put(myTitle, fi);
            }
        }
    }
    
    protected PdfDictionary mergeAttrib(final PdfDictionary parent, final PdfDictionary child) {
        final PdfDictionary targ = new PdfDictionary();
        if (parent != null) {
            targ.putAll(parent);
        }
        for (final PdfName key : child.getKeys()) {
            if (key.equals(PdfName.DR) || key.equals(PdfName.DA) || key.equals(PdfName.Q) || key.equals(PdfName.FF) || key.equals(PdfName.DV) || key.equals(PdfName.V) || key.equals(PdfName.FT) || key.equals(PdfName.F)) {
                targ.put(key, child.get(key));
            }
        }
        return targ;
    }
    
    protected void pushAttrib(final PdfDictionary dict) {
        PdfDictionary dic = null;
        if (!this.stack.isEmpty()) {
            dic = this.stack.get(this.stack.size() - 1);
        }
        dic = this.mergeAttrib(dic, dict);
        this.stack.add(dic);
    }
    
    public static class FieldInformation
    {
        String name;
        PdfDictionary info;
        PRIndirectReference ref;
        
        FieldInformation(final String name, final PdfDictionary info, final PRIndirectReference ref) {
            this.name = name;
            this.info = info;
            this.ref = ref;
        }
        
        public String getName() {
            return this.name;
        }
        
        public PdfDictionary getInfo() {
            return this.info;
        }
        
        public PRIndirectReference getRef() {
            return this.ref;
        }
    }
}
