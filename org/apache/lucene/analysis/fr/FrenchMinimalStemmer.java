package org.apache.lucene.analysis.fr;

public class FrenchMinimalStemmer
{
    public int stem(final char[] s, int len) {
        if (len < 6) {
            return len;
        }
        if (s[len - 1] == 'x') {
            if (s[len - 3] == 'a' && s[len - 2] == 'u') {
                s[len - 2] = 'l';
            }
            return len - 1;
        }
        if (s[len - 1] == 's') {
            --len;
        }
        if (s[len - 1] == 'r') {
            --len;
        }
        if (s[len - 1] == 'e') {
            --len;
        }
        if (s[len - 1] == '\u00e9') {
            --len;
        }
        if (s[len - 1] == s[len - 2]) {
            --len;
        }
        return len;
    }
}
