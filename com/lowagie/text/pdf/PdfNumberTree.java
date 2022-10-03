package com.lowagie.text.pdf;

import java.util.HashMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class PdfNumberTree
{
    private static final int leafSize = 64;
    
    public static PdfDictionary writeTree(final Map items, final PdfWriter writer) throws IOException {
        if (items.isEmpty()) {
            return null;
        }
        Integer[] numbers = new Integer[items.size()];
        numbers = (Integer[])items.keySet().toArray(numbers);
        Arrays.sort(numbers);
        if (numbers.length <= 64) {
            final PdfDictionary dic = new PdfDictionary();
            final PdfArray ar = new PdfArray();
            for (int k = 0; k < numbers.length; ++k) {
                ar.add(new PdfNumber(numbers[k]));
                ar.add(items.get(numbers[k]));
            }
            dic.put(PdfName.NUMS, ar);
            return dic;
        }
        int skip = 64;
        final PdfIndirectReference[] kids = new PdfIndirectReference[(numbers.length + 64 - 1) / 64];
        for (int k = 0; k < kids.length; ++k) {
            int offset = k * 64;
            final int end = Math.min(offset + 64, numbers.length);
            final PdfDictionary dic2 = new PdfDictionary();
            PdfArray arr = new PdfArray();
            arr.add(new PdfNumber(numbers[offset]));
            arr.add(new PdfNumber(numbers[end - 1]));
            dic2.put(PdfName.LIMITS, arr);
            arr = new PdfArray();
            while (offset < end) {
                arr.add(new PdfNumber(numbers[offset]));
                arr.add(items.get(numbers[offset]));
                ++offset;
            }
            dic2.put(PdfName.NUMS, arr);
            kids[k] = writer.addToBody(dic2).getIndirectReference();
        }
        int top;
        int tt;
        for (top = kids.length; top > 64; top = tt) {
            skip *= 64;
            tt = (numbers.length + skip - 1) / skip;
            for (int i = 0; i < tt; ++i) {
                int offset2 = i * 64;
                final int end2 = Math.min(offset2 + 64, top);
                final PdfDictionary dic3 = new PdfDictionary();
                PdfArray arr2 = new PdfArray();
                arr2.add(new PdfNumber(numbers[i * skip]));
                arr2.add(new PdfNumber(numbers[Math.min((i + 1) * skip, numbers.length) - 1]));
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
        PdfArray nn = (PdfArray)PdfReader.getPdfObjectRelease(dic.get(PdfName.NUMS));
        if (nn != null) {
            for (int k = 0; k < nn.size(); ++k) {
                final PdfNumber s = (PdfNumber)PdfReader.getPdfObjectRelease(nn.getPdfObject(k++));
                items.put(new Integer(s.intValue()), nn.getPdfObject(k));
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
