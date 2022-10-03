package com.lowagie.text.pdf.hyphenation;

public class Hyphenation
{
    private int[] hyphenPoints;
    private String word;
    private int len;
    
    Hyphenation(final String word, final int[] points) {
        this.word = word;
        this.hyphenPoints = points;
        this.len = points.length;
    }
    
    public int length() {
        return this.len;
    }
    
    public String getPreHyphenText(final int index) {
        return this.word.substring(0, this.hyphenPoints[index]);
    }
    
    public String getPostHyphenText(final int index) {
        return this.word.substring(this.hyphenPoints[index]);
    }
    
    public int[] getHyphenationPoints() {
        return this.hyphenPoints;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer();
        int start = 0;
        for (int i = 0; i < this.len; ++i) {
            str.append(this.word, start, this.hyphenPoints[i]).append('-');
            start = this.hyphenPoints[i];
        }
        str.append(this.word.substring(start));
        return str.toString();
    }
}
