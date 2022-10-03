package org.apache.lucene.analysis.en;

public class EnglishMinimalStemmer
{
    public int stem(final char[] s, final int len) {
        if (len < 3 || s[len - 1] != 's') {
            return len;
        }
        switch (s[len - 2]) {
            case 's':
            case 'u': {
                return len;
            }
            case 'e': {
                if (len > 3 && s[len - 3] == 'i' && s[len - 4] != 'a' && s[len - 4] != 'e') {
                    s[len - 3] = 'y';
                    return len - 2;
                }
                if (s[len - 3] == 'i' || s[len - 3] == 'a' || s[len - 3] == 'o' || s[len - 3] == 'e') {
                    return len;
                }
                break;
            }
        }
        return len - 1;
    }
}
