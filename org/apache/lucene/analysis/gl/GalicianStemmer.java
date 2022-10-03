package org.apache.lucene.analysis.gl;

import java.util.Map;
import org.apache.lucene.analysis.pt.RSLPStemmerBase;

public class GalicianStemmer extends RSLPStemmerBase
{
    private static final Step plural;
    private static final Step unification;
    private static final Step adverb;
    private static final Step augmentative;
    private static final Step noun;
    private static final Step verb;
    private static final Step vowel;
    
    public int stem(final char[] s, int len) {
        assert s.length >= len + 1 : "this stemmer requires an oversized array of at least 1";
        len = GalicianStemmer.plural.apply(s, len);
        len = GalicianStemmer.unification.apply(s, len);
        len = GalicianStemmer.adverb.apply(s, len);
        int oldlen;
        do {
            oldlen = len;
            len = GalicianStemmer.augmentative.apply(s, len);
        } while (len != oldlen);
        oldlen = len;
        len = GalicianStemmer.noun.apply(s, len);
        if (len == oldlen) {
            len = GalicianStemmer.verb.apply(s, len);
        }
        len = GalicianStemmer.vowel.apply(s, len);
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e1': {
                    s[i] = 'a';
                    break;
                }
                case '\u00e9':
                case '\u00ea': {
                    s[i] = 'e';
                    break;
                }
                case '\u00ed': {
                    s[i] = 'i';
                    break;
                }
                case '\u00f3': {
                    s[i] = 'o';
                    break;
                }
                case '\u00fa': {
                    s[i] = 'u';
                    break;
                }
            }
        }
        return len;
    }
    
    static {
        final Map<String, Step> steps = RSLPStemmerBase.parse(GalicianStemmer.class, "galician.rslp");
        plural = steps.get("Plural");
        unification = steps.get("Unification");
        adverb = steps.get("Adverb");
        augmentative = steps.get("Augmentative");
        noun = steps.get("Noun");
        verb = steps.get("Verb");
        vowel = steps.get("Vowel");
    }
}
