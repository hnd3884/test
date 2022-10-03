package com.lowagie.text.pdf;

import java.util.Arrays;
import com.lowagie.text.factories.RomanAlphabetFactory;
import com.lowagie.text.factories.RomanNumberFactory;
import java.io.IOException;
import com.lowagie.text.ExceptionConverter;
import java.util.Map;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.HashMap;

public class PdfPageLabels
{
    public static final int DECIMAL_ARABIC_NUMERALS = 0;
    public static final int UPPERCASE_ROMAN_NUMERALS = 1;
    public static final int LOWERCASE_ROMAN_NUMERALS = 2;
    public static final int UPPERCASE_LETTERS = 3;
    public static final int LOWERCASE_LETTERS = 4;
    public static final int EMPTY = 5;
    static PdfName[] numberingStyle;
    private HashMap map;
    
    public PdfPageLabels() {
        this.map = new HashMap();
        this.addPageLabel(1, 0, null, 1);
    }
    
    public void addPageLabel(final int page, final int numberStyle, final String text, final int firstPage) {
        if (page < 1 || firstPage < 1) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("in.a.page.label.the.page.numbers.must.be.greater.or.equal.to.1"));
        }
        final PdfDictionary dic = new PdfDictionary();
        if (numberStyle >= 0 && numberStyle < PdfPageLabels.numberingStyle.length) {
            dic.put(PdfName.S, PdfPageLabels.numberingStyle[numberStyle]);
        }
        if (text != null) {
            dic.put(PdfName.P, new PdfString(text, "UnicodeBig"));
        }
        if (firstPage != 1) {
            dic.put(PdfName.ST, new PdfNumber(firstPage));
        }
        this.map.put(new Integer(page - 1), dic);
    }
    
    public void addPageLabel(final int page, final int numberStyle, final String text) {
        this.addPageLabel(page, numberStyle, text, 1);
    }
    
    public void addPageLabel(final int page, final int numberStyle) {
        this.addPageLabel(page, numberStyle, null, 1);
    }
    
    public void addPageLabel(final PdfPageLabelFormat format) {
        this.addPageLabel(format.physicalPage, format.numberStyle, format.prefix, format.logicalPage);
    }
    
    public void removePageLabel(final int page) {
        if (page <= 1) {
            return;
        }
        this.map.remove(new Integer(page - 1));
    }
    
    PdfDictionary getDictionary(final PdfWriter writer) {
        try {
            return PdfNumberTree.writeTree(this.map, writer);
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static String[] getPageLabels(final PdfReader reader) {
        final int n = reader.getNumberOfPages();
        final PdfDictionary dict = reader.getCatalog();
        final PdfDictionary labels = (PdfDictionary)PdfReader.getPdfObjectRelease(dict.get(PdfName.PAGELABELS));
        if (labels == null) {
            return null;
        }
        final String[] labelstrings = new String[n];
        final HashMap numberTree = PdfNumberTree.readTree(labels);
        int pagecount = 1;
        char type = 'D';
        String prefix = "";
        for (int i = 0; i < n; ++i) {
            final Integer current = new Integer(i);
            if (numberTree.containsKey(current)) {
                final PdfDictionary d = (PdfDictionary)PdfReader.getPdfObjectRelease(numberTree.get(current));
                if (d.contains(PdfName.ST)) {
                    pagecount = ((PdfNumber)d.get(PdfName.ST)).intValue();
                }
                else {
                    pagecount = 1;
                }
                if (d.contains(PdfName.P)) {
                    prefix = ((PdfString)d.get(PdfName.P)).toUnicodeString();
                }
                else {
                    prefix = "";
                }
                if (d.contains(PdfName.S)) {
                    type = d.get(PdfName.S).toString().charAt(1);
                }
            }
            switch (type) {
                default: {
                    labelstrings[i] = prefix + pagecount;
                    break;
                }
                case 'R': {
                    labelstrings[i] = prefix + RomanNumberFactory.getUpperCaseString(pagecount);
                    break;
                }
                case 'r': {
                    labelstrings[i] = prefix + RomanNumberFactory.getLowerCaseString(pagecount);
                    break;
                }
                case 'A': {
                    labelstrings[i] = prefix + RomanAlphabetFactory.getUpperCaseString(pagecount);
                    break;
                }
                case 'a': {
                    labelstrings[i] = prefix + RomanAlphabetFactory.getLowerCaseString(pagecount);
                    break;
                }
            }
            ++pagecount;
        }
        return labelstrings;
    }
    
    public static PdfPageLabelFormat[] getPageLabelFormats(final PdfReader reader) {
        final PdfDictionary dict = reader.getCatalog();
        final PdfDictionary labels = (PdfDictionary)PdfReader.getPdfObjectRelease(dict.get(PdfName.PAGELABELS));
        if (labels == null) {
            return null;
        }
        final HashMap numberTree = PdfNumberTree.readTree(labels);
        Integer[] numbers = new Integer[numberTree.size()];
        numbers = (Integer[])numberTree.keySet().toArray(numbers);
        Arrays.sort(numbers);
        final PdfPageLabelFormat[] formats = new PdfPageLabelFormat[numberTree.size()];
        for (int k = 0; k < numbers.length; ++k) {
            final Integer key = numbers[k];
            final PdfDictionary d = (PdfDictionary)PdfReader.getPdfObjectRelease(numberTree.get(key));
            int pagecount;
            if (d.contains(PdfName.ST)) {
                pagecount = ((PdfNumber)d.get(PdfName.ST)).intValue();
            }
            else {
                pagecount = 1;
            }
            String prefix;
            if (d.contains(PdfName.P)) {
                prefix = ((PdfString)d.get(PdfName.P)).toUnicodeString();
            }
            else {
                prefix = "";
            }
            int numberStyle = 0;
            if (d.contains(PdfName.S)) {
                final char type = d.get(PdfName.S).toString().charAt(1);
                switch (type) {
                    case 'R': {
                        numberStyle = 1;
                        break;
                    }
                    case 'r': {
                        numberStyle = 2;
                        break;
                    }
                    case 'A': {
                        numberStyle = 3;
                        break;
                    }
                    case 'a': {
                        numberStyle = 4;
                        break;
                    }
                    default: {
                        numberStyle = 0;
                        break;
                    }
                }
            }
            else {
                numberStyle = 5;
            }
            formats[k] = new PdfPageLabelFormat(key + 1, numberStyle, prefix, pagecount);
        }
        return formats;
    }
    
    static {
        PdfPageLabels.numberingStyle = new PdfName[] { PdfName.D, PdfName.R, new PdfName("r"), PdfName.A, new PdfName("a") };
    }
    
    public static class PdfPageLabelFormat
    {
        public int physicalPage;
        public int numberStyle;
        public String prefix;
        public int logicalPage;
        
        public PdfPageLabelFormat(final int physicalPage, final int numberStyle, final String prefix, final int logicalPage) {
            this.physicalPage = physicalPage;
            this.numberStyle = numberStyle;
            this.prefix = prefix;
            this.logicalPage = logicalPage;
        }
    }
}
