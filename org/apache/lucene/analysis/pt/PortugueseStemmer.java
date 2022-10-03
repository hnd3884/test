package org.apache.lucene.analysis.pt;

import java.util.Map;

public class PortugueseStemmer extends RSLPStemmerBase
{
    private static final Step plural;
    private static final Step feminine;
    private static final Step adverb;
    private static final Step augmentative;
    private static final Step noun;
    private static final Step verb;
    private static final Step vowel;
    
    public int stem(final char[] s, int len) {
        assert s.length >= len + 1 : "this stemmer requires an oversized array of at least 1";
        len = PortugueseStemmer.plural.apply(s, len);
        len = PortugueseStemmer.adverb.apply(s, len);
        len = PortugueseStemmer.feminine.apply(s, len);
        int oldlen;
        len = (oldlen = PortugueseStemmer.augmentative.apply(s, len));
        len = PortugueseStemmer.noun.apply(s, len);
        if (len == oldlen) {
            oldlen = len;
            len = PortugueseStemmer.verb.apply(s, len);
            if (len == oldlen) {
                len = PortugueseStemmer.vowel.apply(s, len);
            }
        }
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e0':
                case '\u00e1':
                case '\u00e2':
                case '\u00e3':
                case '\u00e4':
                case '\u00e5': {
                    s[i] = 'a';
                    break;
                }
                case '\u00e7': {
                    s[i] = 'c';
                    break;
                }
                case '\u00e8':
                case '\u00e9':
                case '\u00ea':
                case '\u00eb': {
                    s[i] = 'e';
                    break;
                }
                case '\u00ec':
                case '\u00ed':
                case '\u00ee':
                case '\u00ef': {
                    s[i] = 'i';
                    break;
                }
                case '\u00f1': {
                    s[i] = 'n';
                    break;
                }
                case '\u00f2':
                case '\u00f3':
                case '\u00f4':
                case '\u00f5':
                case '\u00f6': {
                    s[i] = 'o';
                    break;
                }
                case '\u00f9':
                case '\u00fa':
                case '\u00fb':
                case '\u00fc': {
                    s[i] = 'u';
                    break;
                }
                case '\u00fd':
                case '\u00ff': {
                    s[i] = 'y';
                    break;
                }
            }
        }
        return len;
    }
    
    static {
        final Map<String, Step> steps = RSLPStemmerBase.parse(PortugueseStemmer.class, "portuguese.rslp");
        plural = steps.get("Plural");
        feminine = steps.get("Feminine");
        adverb = steps.get("Adverb");
        augmentative = steps.get("Augmentative");
        noun = steps.get("Noun");
        verb = steps.get("Verb");
        vowel = steps.get("Vowel");
    }
}
