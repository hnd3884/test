package com.lowagie.text.pdf;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class PdfNameTree
{
    private static final int leafSize = 64;
    
    public static PdfDictionary writeTree(final HashMap items, final PdfWriter writer) throws IOException {
        if (items.isEmpty()) {
            return null;
        }
        String[] names = new String[items.size()];
        names = (String[])items.keySet().toArray(names);
        Arrays.sort(names);
        if (names.length <= 64) {
            final PdfDictionary dic = new PdfDictionary();
            final PdfArray ar = new PdfArray();
            for (int k = 0; k < names.length; ++k) {
                ar.add(new PdfString(names[k], null));
                ar.add(items.get(names[k]));
            }
            dic.put(PdfName.NAMES, ar);
            return dic;
        }
        int skip = 64;
        final PdfIndirectReference[] kids = new PdfIndirectReference[(names.length + 64 - 1) / 64];
        for (int k = 0; k < kids.length; ++k) {
            int offset = k * 64;
            final int end = Math.min(offset + 64, names.length);
            final PdfDictionary dic2 = new PdfDictionary();
            PdfArray arr = new PdfArray();
            arr.add(new PdfString(names[offset], null));
            arr.add(new PdfString(names[end - 1], null));
            dic2.put(PdfName.LIMITS, arr);
            arr = new PdfArray();
            while (offset < end) {
                arr.add(new PdfString(names[offset], null));
                arr.add(items.get(names[offset]));
                ++offset;
            }
            dic2.put(PdfName.NAMES, arr);
            kids[k] = writer.addToBody(dic2).getIndirectReference();
        }
        int top;
        int tt;
        for (top = kids.length; top > 64; top = tt) {
            skip *= 64;
            tt = (names.length + skip - 1) / skip;
            for (int i = 0; i < tt; ++i) {
                int offset2 = i * 64;
                final int end2 = Math.min(offset2 + 64, top);
                final PdfDictionary dic3 = new PdfDictionary();
                PdfArray arr2 = new PdfArray();
                arr2.add(new PdfString(names[i * skip], null));
                arr2.add(new PdfString(names[Math.min((i + 1) * skip, names.length) - 1], null));
                dic3.put(PdfName.LIMITS, arr2);
                arr2 = new PdfArray();
                while (offset2 < end2) {
                    arr2.add(kids[offset2]);
                    ++offset2;
                }
                dic3.put(PdfName.KIDS, arr2);
                kids[i] = writer.addToBody(dic3).getIndirectReference();
            }
        }
        final PdfArray arr3 = new PdfArray();
        for (int i = 0; i < top; ++i) {
            arr3.add(kids[i]);
        }
        final PdfDictionary dic4 = new PdfDictionary();
        dic4.put(PdfName.KIDS, arr3);
        return dic4;
    }
    
    private static void iterateItems(final PdfDictionary dic, final HashMap items) {
        PdfArray nn = (PdfArray)PdfReader.getPdfObjectRelease(dic.get(PdfName.NAMES));
        if (nn != null) {
            for (int k = 0; k < nn.size(); ++k) {
                final PdfString s = (PdfString)PdfReader.getPdfObjectRelease(nn.getPdfObject(k++));
                items.put(PdfEncodings.convertToString(s.getBytes(), null), nn.getPdfObject(k));
            }
        }
        else if ((nn = (PdfArray)PdfReader.getPdfObjectRelease(dic.get(PdfName.KIDS))) != null) {
            for (int k = 0; k < nn.size(); ++k) {
                final PdfDictionary kid = (PdfDictionary)PdfReader.getPdfObjectRelease(nn.getPdfObject(k));
                iterateItems(kid, items);
            }
        }
    }
    
    public static HashMap readTree(final PdfDictionary dic) {
        final HashMap items = new HashMap();
        if (dic != null) {
            iterateItems(dic, items);
        }
        return items;
    }
}
