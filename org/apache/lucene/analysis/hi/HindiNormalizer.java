package org.apache.lucene.analysis.hi;

import org.apache.lucene.analysis.util.StemmerUtil;

public class HindiNormalizer
{
    public int normalize(final char[] s, int len) {
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u0928': {
                    if (i + 1 < len && s[i + 1] == '\u094d') {
                        s[i] = '\u0902';
                        len = StemmerUtil.delete(s, i + 1, len);
                        break;
                    }
                    break;
                }
                case '\u0901': {
                    s[i] = '\u0902';
                    break;
                }
                case '\u093c': {
                    len = StemmerUtil.delete(s, i, len);
                    --i;
                    break;
                }
                case '\u0929': {
                    s[i] = '\u0928';
                    break;
                }
                case '\u0931': {
                    s[i] = '\u0930';
                    break;
                }
                case '\u0934': {
                    s[i] = '\u0933';
                    break;
                }
                case '\u0958': {
                    s[i] = '\u0915';
                    break;
                }
                case '\u0959': {
                    s[i] = '\u0916';
                    break;
                }
                case '\u095a': {
                    s[i] = '\u0917';
                    break;
                }
                case '\u095b': {
                    s[i] = '\u091c';
                    break;
                }
                case '\u095c': {
                    s[i] = '\u0921';
                    break;
                }
                case '\u095d': {
                    s[i] = '\u0922';
                    break;
                }
                case '\u095e': {
                    s[i] = '\u092b';
                    break;
                }
                case '\u095f': {
                    s[i] = '\u092f';
                    break;
                }
                case '\u200c':
                case '\u200d': {
                    len = StemmerUtil.delete(s, i, len);
                    --i;
                    break;
                }
                case '\u094d': {
                    len = StemmerUtil.delete(s, i, len);
                    --i;
                    break;
                }
                case '\u0945':
                case '\u0946': {
                    s[i] = '\u0947';
                    break;
                }
                case '\u0949':
                case '\u094a': {
                    s[i] = '\u094b';
                    break;
                }
                case '\u090d':
                case '\u090e': {
                    s[i] = '\u090f';
                    break;
                }
                case '\u0911':
                case '\u0912': {
                    s[i] = '\u0913';
                    break;
                }
                case '\u0972': {
                    s[i] = '\u0905';
                    break;
                }
                case '\u0906': {
                    s[i] = '\u0905';
                    break;
                }
                case '\u0908': {
                    s[i] = '\u0907';
                    break;
                }
                case '\u090a': {
                    s[i] = '\u0909';
                    break;
                }
                case '\u0960': {
                    s[i] = '\u090b';
                    break;
                }
                case '\u0961': {
                    s[i] = '\u090c';
                    break;
                }
                case '\u0910': {
                    s[i] = '\u090f';
                    break;
                }
                case '\u0914': {
                    s[i] = '\u0913';
                    break;
                }
                case '\u0940': {
                    s[i] = '\u093f';
                    break;
                }
                case '\u0942': {
                    s[i] = '\u0941';
                    break;
                }
                case '\u0944': {
                    s[i] = '\u0943';
                    break;
                }
                case '\u0963': {
                    s[i] = '\u0962';
                    break;
                }
                case '\u0948': {
                    s[i] = '\u0947';
                    break;
                }
                case '\u094c': {
                    s[i] = '\u094b';
                    break;
                }
            }
        }
        return len;
    }
}
