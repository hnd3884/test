package org.apache.lucene.analysis.ckb;

import org.apache.lucene.analysis.util.StemmerUtil;

public class SoraniStemmer
{
    public int stem(final char[] s, int len) {
        if (len > 5 && StemmerUtil.endsWith(s, len, "\u062f\u0627")) {
            len -= 2;
        }
        else if (len > 4 && StemmerUtil.endsWith(s, len, "\u0646\u0627")) {
            --len;
        }
        else if (len > 6 && StemmerUtil.endsWith(s, len, "\u06d5\u0648\u06d5")) {
            len -= 3;
        }
        if (len > 6 && (StemmerUtil.endsWith(s, len, "\u0645\u0627\u0646") || StemmerUtil.endsWith(s, len, "\u06cc\u0627\u0646") || StemmerUtil.endsWith(s, len, "\u062a\u0627\u0646"))) {
            len -= 3;
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "\u06ce\u06a9\u06cc")) {
            return len - 3;
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "\u06cc\u06d5\u06a9\u06cc")) {
            return len - 4;
        }
        if (len > 5 && StemmerUtil.endsWith(s, len, "\u06ce\u06a9")) {
            return len - 2;
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "\u06cc\u06d5\u06a9")) {
            return len - 3;
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "\u06d5\u06a9\u06d5")) {
            return len - 3;
        }
        if (len > 5 && StemmerUtil.endsWith(s, len, "\u06a9\u06d5")) {
            return len - 2;
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "\u06d5\u06a9\u0627\u0646")) {
            return len - 4;
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "\u06a9\u0627\u0646")) {
            return len - 3;
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "\u06cc\u0627\u0646\u06cc")) {
            return len - 4;
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "\u0627\u0646\u06cc")) {
            return len - 3;
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "\u06cc\u0627\u0646")) {
            return len - 3;
        }
        if (len > 5 && StemmerUtil.endsWith(s, len, "\u0627\u0646")) {
            return len - 2;
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "\u06cc\u0627\u0646\u06d5")) {
            return len - 4;
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "\u0627\u0646\u06d5")) {
            return len - 3;
        }
        if (len > 5 && (StemmerUtil.endsWith(s, len, "\u0627\u06cc\u06d5") || StemmerUtil.endsWith(s, len, "\u06d5\u06cc\u06d5"))) {
            return len - 2;
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "\u06d5")) {
            return len - 1;
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "\u06cc")) {
            return len - 1;
        }
        return len;
    }
}
