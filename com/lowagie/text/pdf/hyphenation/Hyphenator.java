package com.lowagie.text.pdf.hyphenation;

import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import com.lowagie.text.pdf.BaseFont;
import java.util.Hashtable;

public class Hyphenator
{
    private static Hashtable hyphenTrees;
    private HyphenationTree hyphenTree;
    private int remainCharCount;
    private int pushCharCount;
    private static final String defaultHyphLocation = "com/lowagie/text/pdf/hyphenation/hyph/";
    private static String hyphenDir;
    
    public Hyphenator(final String lang, final String country, final int leftMin, final int rightMin) {
        this.hyphenTree = null;
        this.remainCharCount = 2;
        this.pushCharCount = 2;
        this.hyphenTree = getHyphenationTree(lang, country);
        this.remainCharCount = leftMin;
        this.pushCharCount = rightMin;
    }
    
    public static HyphenationTree getHyphenationTree(final String lang, final String country) {
        String key = lang;
        if (country != null && !country.equals("none")) {
            key = key + "_" + country;
        }
        if (Hyphenator.hyphenTrees.containsKey(key)) {
            return Hyphenator.hyphenTrees.get(key);
        }
        if (Hyphenator.hyphenTrees.containsKey(lang)) {
            return Hyphenator.hyphenTrees.get(lang);
        }
        HyphenationTree hTree = getResourceHyphenationTree(key);
        if (hTree == null) {
            hTree = getFileHyphenationTree(key);
        }
        if (hTree != null) {
            Hyphenator.hyphenTrees.put(key, hTree);
        }
        return hTree;
    }
    
    public static HyphenationTree getResourceHyphenationTree(final String key) {
        try {
            InputStream stream = BaseFont.getResourceStream("com/lowagie/text/pdf/hyphenation/hyph/" + key + ".xml");
            if (stream == null && key.length() > 2) {
                stream = BaseFont.getResourceStream("com/lowagie/text/pdf/hyphenation/hyph/" + key.substring(0, 2) + ".xml");
            }
            if (stream == null) {
                return null;
            }
            final HyphenationTree hTree = new HyphenationTree();
            hTree.loadSimplePatterns(stream);
            return hTree;
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static HyphenationTree getFileHyphenationTree(final String key) {
        try {
            if (Hyphenator.hyphenDir == null) {
                return null;
            }
            InputStream stream = null;
            File hyphenFile = new File(Hyphenator.hyphenDir, key + ".xml");
            if (hyphenFile.canRead()) {
                stream = new FileInputStream(hyphenFile);
            }
            if (stream == null && key.length() > 2) {
                hyphenFile = new File(Hyphenator.hyphenDir, key.substring(0, 2) + ".xml");
                if (hyphenFile.canRead()) {
                    stream = new FileInputStream(hyphenFile);
                }
            }
            if (stream == null) {
                return null;
            }
            final HyphenationTree hTree = new HyphenationTree();
            hTree.loadSimplePatterns(stream);
            return hTree;
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static Hyphenation hyphenate(final String lang, final String country, final String word, final int leftMin, final int rightMin) {
        final HyphenationTree hTree = getHyphenationTree(lang, country);
        if (hTree == null) {
            return null;
        }
        return hTree.hyphenate(word, leftMin, rightMin);
    }
    
    public static Hyphenation hyphenate(final String lang, final String country, final char[] word, final int offset, final int len, final int leftMin, final int rightMin) {
        final HyphenationTree hTree = getHyphenationTree(lang, country);
        if (hTree == null) {
            return null;
        }
        return hTree.hyphenate(word, offset, len, leftMin, rightMin);
    }
    
    public void setMinRemainCharCount(final int min) {
        this.remainCharCount = min;
    }
    
    public void setMinPushCharCount(final int min) {
        this.pushCharCount = min;
    }
    
    public void setLanguage(final String lang, final String country) {
        this.hyphenTree = getHyphenationTree(lang, country);
    }
    
    public Hyphenation hyphenate(final char[] word, final int offset, final int len) {
        if (this.hyphenTree == null) {
            return null;
        }
        return this.hyphenTree.hyphenate(word, offset, len, this.remainCharCount, this.pushCharCount);
    }
    
    public Hyphenation hyphenate(final String word) {
        if (this.hyphenTree == null) {
            return null;
        }
        return this.hyphenTree.hyphenate(word, this.remainCharCount, this.pushCharCount);
    }
    
    public static String getHyphenDir() {
        return Hyphenator.hyphenDir;
    }
    
    public static void setHyphenDir(final String _hyphenDir) {
        Hyphenator.hyphenDir = _hyphenDir;
    }
    
    static {
        Hyphenator.hyphenTrees = new Hashtable();
        Hyphenator.hyphenDir = "";
    }
}
