package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class FdfWriter
{
    private static final byte[] HEADER_FDF;
    HashMap fields;
    private String file;
    
    public FdfWriter() {
        this.fields = new HashMap();
    }
    
    public void writeTo(final OutputStream os) throws IOException {
        final Wrt wrt = new Wrt(os, this);
        wrt.writeTo();
    }
    
    boolean setField(final String field, final PdfObject value) {
        HashMap map = this.fields;
        final StringTokenizer tk = new StringTokenizer(field, ".");
        if (!tk.hasMoreTokens()) {
            return false;
        }
        while (true) {
            final String s = tk.nextToken();
            Object obj = map.get(s);
            if (tk.hasMoreTokens()) {
                if (obj == null) {
                    obj = new HashMap();
                    map.put(s, obj);
                    map = (HashMap)obj;
                }
                else {
                    if (!(obj instanceof HashMap)) {
                        return false;
                    }
                    map = (HashMap)obj;
                }
            }
            else {
                if (!(obj instanceof HashMap)) {
                    map.put(s, value);
                    return true;
                }
                return false;
            }
        }
    }
    
    void iterateFields(final HashMap values, final HashMap map, final String name) {
        for (final Map.Entry entry : map.entrySet()) {
            final String s = entry.getKey();
            final Object obj = entry.getValue();
            if (obj instanceof HashMap) {
                this.iterateFields(values, (HashMap)obj, name + "." + s);
            }
            else {
                values.put((name + "." + s).substring(1), obj);
            }
        }
    }
    
    public boolean removeField(final String field) {
        HashMap map = this.fields;
        final StringTokenizer tk = new StringTokenizer(field, ".");
        if (!tk.hasMoreTokens()) {
            return false;
        }
        final ArrayList hist = new ArrayList();
        while (true) {
            final String s = tk.nextToken();
            final Object obj = map.get(s);
            if (obj == null) {
                return false;
            }
            hist.add(map);
            hist.add(s);
            if (tk.hasMoreTokens()) {
                if (!(obj instanceof HashMap)) {
                    return false;
                }
                map = (HashMap)obj;
            }
            else {
                if (obj instanceof HashMap) {
                    return false;
                }
                for (int k = hist.size() - 2; k >= 0; k -= 2) {
                    map = hist.get(k);
                    final String s2 = hist.get(k + 1);
                    map.remove(s2);
                    if (!map.isEmpty()) {
                        break;
                    }
                }
                return true;
            }
        }
    }
    
    public HashMap getFields() {
        final HashMap values = new HashMap();
        this.iterateFields(values, this.fields, "");
        return values;
    }
    
    public String getField(final String field) {
        HashMap map = this.fields;
        final StringTokenizer tk = new StringTokenizer(field, ".");
        if (!tk.hasMoreTokens()) {
            return null;
        }
        while (true) {
            final String s = tk.nextToken();
            final Object obj = map.get(s);
            if (obj == null) {
                return null;
            }
            if (tk.hasMoreTokens()) {
                if (!(obj instanceof HashMap)) {
                    return null;
                }
                map = (HashMap)obj;
            }
            else {
                if (obj instanceof HashMap) {
                    return null;
                }
                if (((PdfObject)obj).isString()) {
                    return ((PdfString)obj).toUnicodeString();
                }
                return PdfName.decodeName(obj.toString());
            }
        }
    }
    
    public boolean setFieldAsName(final String field, final String value) {
        return this.setField(field, new PdfName(value));
    }
    
    public boolean setFieldAsString(final String field, final String value) {
        return this.setField(field, new PdfString(value, "UnicodeBig"));
    }
    
    public boolean setFieldAsAction(final String field, final PdfAction action) {
        return this.setField(field, action);
    }
    
    public void setFields(final FdfReader fdf) {
        final HashMap map = fdf.getFields();
        for (final Map.Entry entry : map.entrySet()) {
            final String key = entry.getKey();
            final PdfDictionary dic = entry.getValue();
            PdfObject v = dic.get(PdfName.V);
            if (v != null) {
                this.setField(key, v);
            }
            v = dic.get(PdfName.A);
            if (v != null) {
                this.setField(key, v);
            }
        }
    }
    
    public void setFields(final PdfReader pdf) {
        this.setFields(pdf.getAcroFields());
    }
    
    public void setFields(final AcroFields af) {
        for (final Map.Entry entry : af.getFields().entrySet()) {
            final String fn = entry.getKey();
            final AcroFields.Item item = entry.getValue();
            final PdfDictionary dic = item.getMerged(0);
            final PdfObject v = PdfReader.getPdfObjectRelease(dic.get(PdfName.V));
            if (v == null) {
                continue;
            }
            final PdfObject ft = PdfReader.getPdfObjectRelease(dic.get(PdfName.FT));
            if (ft == null) {
                continue;
            }
            if (PdfName.SIG.equals(ft)) {
                continue;
            }
            this.setField(fn, v);
        }
    }
    
    public String getFile() {
        return this.file;
    }
    
    public void setFile(final String file) {
        this.file = file;
    }
    
    static {
        HEADER_FDF = DocWriter.getISOBytes("%FDF-1.2\n%\u00e2\u00e3\u00cf\u00d3\n");
    }
    
    static class Wrt extends PdfWriter
    {
        private FdfWriter fdf;
        
        Wrt(final OutputStream os, final FdfWriter fdf) throws IOException {
            super(new PdfDocument(), os);
            this.fdf = fdf;
            this.os.write(FdfWriter.HEADER_FDF);
            this.body = new PdfBody(this);
        }
        
        void writeTo() throws IOException {
            final PdfDictionary dic = new PdfDictionary();
            dic.put(PdfName.FIELDS, this.calculate(this.fdf.fields));
            if (this.fdf.file != null) {
                dic.put(PdfName.F, new PdfString(this.fdf.file, "UnicodeBig"));
            }
            final PdfDictionary fd = new PdfDictionary();
            fd.put(PdfName.FDF, dic);
            final PdfIndirectReference ref = this.addToBody(fd).getIndirectReference();
            this.os.write(DocWriter.getISOBytes("trailer\n"));
            final PdfDictionary trailer = new PdfDictionary();
            trailer.put(PdfName.ROOT, ref);
            trailer.toPdf(null, this.os);
            this.os.write(DocWriter.getISOBytes("\n%%EOF\n"));
            this.os.close();
        }
        
        PdfArray calculate(final HashMap map) {
            final PdfArray ar = new PdfArray();
            for (final Map.Entry entry : map.entrySet()) {
                final String key = entry.getKey();
                final Object v = entry.getValue();
                final PdfDictionary dic = new PdfDictionary();
                dic.put(PdfName.T, new PdfString(key, "UnicodeBig"));
                if (v instanceof HashMap) {
                    dic.put(PdfName.KIDS, this.calculate((HashMap)v));
                }
                else if (v instanceof PdfAction) {
                    dic.put(PdfName.A, (PdfObject)v);
                }
                else {
                    dic.put(PdfName.V, (PdfObject)v);
                }
                ar.add(dic);
            }
            return ar;
        }
    }
}
