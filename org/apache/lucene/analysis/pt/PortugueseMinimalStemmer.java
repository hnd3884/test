package org.apache.lucene.analysis.pt;

public class PortugueseMinimalStemmer extends RSLPStemmerBase
{
    private static final Step pluralStep;
    
    public int stem(final char[] s, final int len) {
        return PortugueseMinimalStemmer.pluralStep.apply(s, len);
    }
    
    static {
        pluralStep = RSLPStemmerBase.parse(PortugueseMinimalStemmer.class, "portuguese.rslp").get("Plural");
    }
}
