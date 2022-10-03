package org.apache.lucene.analysis.gl;

import org.apache.lucene.analysis.pt.RSLPStemmerBase;

public class GalicianMinimalStemmer extends RSLPStemmerBase
{
    private static final Step pluralStep;
    
    public int stem(final char[] s, final int len) {
        return GalicianMinimalStemmer.pluralStep.apply(s, len);
    }
    
    static {
        pluralStep = RSLPStemmerBase.parse(GalicianMinimalStemmer.class, "galician.rslp").get("Plural");
    }
}
