package com.lowagie.text.pdf;

import com.lowagie.text.pdf.hyphenation.Hyphenation;
import com.lowagie.text.pdf.hyphenation.Hyphenator;

public class HyphenationAuto implements HyphenationEvent
{
    protected Hyphenator hyphenator;
    protected String post;
    
    public HyphenationAuto(final String lang, final String country, final int leftMin, final int rightMin) {
        this.hyphenator = new Hyphenator(lang, country, leftMin, rightMin);
    }
    
    @Override
    public String getHyphenSymbol() {
        return "-";
    }
    
    @Override
    public String getHyphenatedWordPre(final String word, final BaseFont font, final float fontSize, final float remainingWidth) {
        this.post = word;
        final String hyphen = this.getHyphenSymbol();
        final float hyphenWidth = font.getWidthPoint(hyphen, fontSize);
        if (hyphenWidth > remainingWidth) {
            return "";
        }
        final Hyphenation hyphenation = this.hyphenator.hyphenate(word);
        if (hyphenation == null) {
            return "";
        }
        int len;
        int k;
        for (len = hyphenation.length(), k = 0; k < len && font.getWidthPoint(hyphenation.getPreHyphenText(k), fontSize) + hyphenWidth <= remainingWidth; ++k) {}
        if (--k < 0) {
            return "";
        }
        this.post = hyphenation.getPostHyphenText(k);
        return hyphenation.getPreHyphenText(k) + hyphen;
    }
    
    @Override
    public String getHyphenatedWordPost() {
        return this.post;
    }
}
