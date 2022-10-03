package com.lowagie.text.pdf;

import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.util.HashMap;

public class FdfReader extends PdfReader
{
    HashMap fields;
    String fileSpec;
    PdfName encoding;
    
    public FdfReader(final String filename) throws IOException {
        super(filename);
    }
    
    public FdfReader(final byte[] pdfIn) throws IOException {
        super(pdfIn);
    }
    
    public FdfReader(final URL url) throws IOException {
        super(url);
    }
    
    public FdfReader(final InputStream is) throws IOException {
        super(is);
    }
    
    @Override
    protected void readPdf() throws IOException {
        this.fields = new HashMap();
        try {
            this.tokens.checkFdfHeader();
            this.rebuildXref();
            this.readDocObj();
        }
        finally {
            try {
                this.tokens.close();
            }
            catch (final Exception ex) {}
        }
        this.readFields();
    }
    
    protected void kidNode(final PdfDictionary merged, String name) {
        final PdfArray kids = merged.getAsArray(PdfName.KIDS);
        if (kids == null || kids.isEmpty()) {
            if (name.length() > 0) {
                name = name.substring(1);
            }
            this.fields.put(name, merged);
        }
        else {
            merged.remove(PdfName.KIDS);
            for (int k = 0; k < kids.size(); ++k) {
                final PdfDictionary dic = new PdfDictionary();
                dic.merge(merged);
                final PdfDictionary newDic = kids.getAsDict(k);
                final PdfString t = newDic.getAsString(PdfName.T);
                String newName = name;
                if (t != null) {
                    newName = newName + "." + t.toUnicodeString();
                }
                dic.merge(newDic);
                dic.remove(PdfName.T);
                this.kidNode(dic, newName);
            }
        }
    }
    
    protected void readFields() {
        this.catalog = this.trailer.getAsDict(PdfName.ROOT);
        final PdfDictionary fdf = this.catalog.getAsDict(PdfName.FDF);
        if (fdf == null) {
            return;
        }
        final PdfString fs = fdf.getAsString(PdfName.F);
        if (fs != null) {
            this.fileSpec = fs.toUnicodeString();
        }
        final PdfArray fld = fdf.getAsArray(PdfName.FIELDS);
        if (fld == null) {
            return;
        }
        this.encoding = fdf.getAsName(PdfName.ENCODING);
        final PdfDictionary merged = new PdfDictionary();
        merged.put(PdfName.KIDS, fld);
        this.kidNode(merged, "");
    }
    
    public HashMap getFields() {
        return this.fields;
    }
    
    public PdfDictionary getField(final String name) {
        return this.fields.get(name);
    }
    
    public String getFieldValue(final String name) {
        final PdfDictionary field = this.fields.get(name);
        if (field == null) {
            return null;
        }
        final PdfObject v = PdfReader.getPdfObject(field.get(PdfName.V));
        if (v == null) {
            return null;
        }
        if (v.isName()) {
            return PdfName.decodeName(v.toString());
        }
        if (!v.isString()) {
            return null;
        }
        final PdfString vs = (PdfString)v;
        if (this.encoding == null || vs.getEncoding() != null) {
            return vs.toUnicodeString();
        }
        final byte[] b = vs.getBytes();
        if (b.length >= 2 && b[0] == -2 && b[1] == -1) {
            return vs.toUnicodeString();
        }
        try {
            if (this.encoding.equals(PdfName.SHIFT_JIS)) {
                return new String(b, "SJIS");
            }
            if (this.encoding.equals(PdfName.UHC)) {
                return new String(b, "MS949");
            }
            if (this.encoding.equals(PdfName.GBK)) {
                return new String(b, "GBK");
            }
            if (this.encoding.equals(PdfName.BIGFIVE)) {
                return new String(b, "Big5");
            }
        }
        catch (final Exception ex) {}
        return vs.toUnicodeString();
    }
    
    public String getFileSpec() {
        return this.fileSpec;
    }
}
