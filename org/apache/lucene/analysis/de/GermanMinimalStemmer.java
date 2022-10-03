package org.apache.lucene.analysis.de;

public class GermanMinimalStemmer
{
    public int stem(final char[] s, final int len) {
        if (len < 5) {
            return len;
        }
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e4': {
                    s[i] = 'a';
                    break;
                }
                case '\u00f6': {
                    s[i] = 'o';
                    break;
                }
                case '\u00fc': {
                    s[i] = 'u';
                    break;
                }
            }
        }
        if (len > 6 && s[len - 3] == 'n' && s[len - 2] == 'e' && s[len - 1] == 'n') {
            return len - 3;
        }
        if (len > 5) {
            switch (s[len - 1]) {
                case 'n': {
                    if (s[len - 2] == 'e') {
                        return len - 2;
                    }
                    break;
                }
                case 'e': {
                    if (s[len - 2] == 's') {
                        return len - 2;
                    }
                    break;
                }
                case 's': {
                    if (s[len - 2] == 'e') {
                        return len - 2;
                    }
                    break;
                }
                case 'r': {
                    if (s[len - 2] == 'e') {
                        return len - 2;
                    }
                    break;
                }
            }
        }
        switch (s[len - 1]) {
            case 'e':
            case 'n':
            case 'r':
            case 's': {
                return len - 1;
            }
            default: {
                return len;
            }
        }
    }
}
