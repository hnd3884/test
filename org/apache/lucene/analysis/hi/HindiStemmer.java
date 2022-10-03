package org.apache.lucene.analysis.hi;

import org.apache.lucene.analysis.util.StemmerUtil;

public class HindiStemmer
{
    public int stem(final char[] buffer, final int len) {
        if (len > 6 && (StemmerUtil.endsWith(buffer, len, "\u093e\u090f\u0902\u0917\u0940") || StemmerUtil.endsWith(buffer, len, "\u093e\u090f\u0902\u0917\u0947") || StemmerUtil.endsWith(buffer, len, "\u093e\u090a\u0902\u0917\u0940") || StemmerUtil.endsWith(buffer, len, "\u093e\u090a\u0902\u0917\u093e") || StemmerUtil.endsWith(buffer, len, "\u093e\u0907\u092f\u093e\u0901") || StemmerUtil.endsWith(buffer, len, "\u093e\u0907\u092f\u094b\u0902") || StemmerUtil.endsWith(buffer, len, "\u093e\u0907\u092f\u093e\u0902"))) {
            return len - 5;
        }
        if (len > 5 && (StemmerUtil.endsWith(buffer, len, "\u093e\u090f\u0917\u0940") || StemmerUtil.endsWith(buffer, len, "\u093e\u090f\u0917\u093e") || StemmerUtil.endsWith(buffer, len, "\u093e\u0913\u0917\u0940") || StemmerUtil.endsWith(buffer, len, "\u093e\u0913\u0917\u0947") || StemmerUtil.endsWith(buffer, len, "\u090f\u0902\u0917\u0940") || StemmerUtil.endsWith(buffer, len, "\u0947\u0902\u0917\u0940") || StemmerUtil.endsWith(buffer, len, "\u090f\u0902\u0917\u0947") || StemmerUtil.endsWith(buffer, len, "\u0947\u0902\u0917\u0947") || StemmerUtil.endsWith(buffer, len, "\u0942\u0902\u0917\u0940") || StemmerUtil.endsWith(buffer, len, "\u0942\u0902\u0917\u093e") || StemmerUtil.endsWith(buffer, len, "\u093e\u0924\u0940\u0902") || StemmerUtil.endsWith(buffer, len, "\u0928\u093e\u0913\u0902") || StemmerUtil.endsWith(buffer, len, "\u0928\u093e\u090f\u0902") || StemmerUtil.endsWith(buffer, len, "\u0924\u093e\u0913\u0902") || StemmerUtil.endsWith(buffer, len, "\u0924\u093e\u090f\u0902") || StemmerUtil.endsWith(buffer, len, "\u093f\u092f\u093e\u0901") || StemmerUtil.endsWith(buffer, len, "\u093f\u092f\u094b\u0902") || StemmerUtil.endsWith(buffer, len, "\u093f\u092f\u093e\u0902"))) {
            return len - 4;
        }
        if (len > 4 && (StemmerUtil.endsWith(buffer, len, "\u093e\u0915\u0930") || StemmerUtil.endsWith(buffer, len, "\u093e\u0907\u090f") || StemmerUtil.endsWith(buffer, len, "\u093e\u0908\u0902") || StemmerUtil.endsWith(buffer, len, "\u093e\u092f\u093e") || StemmerUtil.endsWith(buffer, len, "\u0947\u0917\u0940") || StemmerUtil.endsWith(buffer, len, "\u0947\u0917\u093e") || StemmerUtil.endsWith(buffer, len, "\u094b\u0917\u0940") || StemmerUtil.endsWith(buffer, len, "\u094b\u0917\u0947") || StemmerUtil.endsWith(buffer, len, "\u093e\u0928\u0947") || StemmerUtil.endsWith(buffer, len, "\u093e\u0928\u093e") || StemmerUtil.endsWith(buffer, len, "\u093e\u0924\u0947") || StemmerUtil.endsWith(buffer, len, "\u093e\u0924\u0940") || StemmerUtil.endsWith(buffer, len, "\u093e\u0924\u093e") || StemmerUtil.endsWith(buffer, len, "\u0924\u0940\u0902") || StemmerUtil.endsWith(buffer, len, "\u093e\u0913\u0902") || StemmerUtil.endsWith(buffer, len, "\u093e\u090f\u0902") || StemmerUtil.endsWith(buffer, len, "\u0941\u0913\u0902") || StemmerUtil.endsWith(buffer, len, "\u0941\u090f\u0902") || StemmerUtil.endsWith(buffer, len, "\u0941\u0906\u0902"))) {
            return len - 3;
        }
        if (len > 3 && (StemmerUtil.endsWith(buffer, len, "\u0915\u0930") || StemmerUtil.endsWith(buffer, len, "\u093e\u0913") || StemmerUtil.endsWith(buffer, len, "\u093f\u090f") || StemmerUtil.endsWith(buffer, len, "\u093e\u0908") || StemmerUtil.endsWith(buffer, len, "\u093e\u090f") || StemmerUtil.endsWith(buffer, len, "\u0928\u0947") || StemmerUtil.endsWith(buffer, len, "\u0928\u0940") || StemmerUtil.endsWith(buffer, len, "\u0928\u093e") || StemmerUtil.endsWith(buffer, len, "\u0924\u0947") || StemmerUtil.endsWith(buffer, len, "\u0940\u0902") || StemmerUtil.endsWith(buffer, len, "\u0924\u0940") || StemmerUtil.endsWith(buffer, len, "\u0924\u093e") || StemmerUtil.endsWith(buffer, len, "\u093e\u0901") || StemmerUtil.endsWith(buffer, len, "\u093e\u0902") || StemmerUtil.endsWith(buffer, len, "\u094b\u0902") || StemmerUtil.endsWith(buffer, len, "\u0947\u0902"))) {
            return len - 2;
        }
        if (len > 2 && (StemmerUtil.endsWith(buffer, len, "\u094b") || StemmerUtil.endsWith(buffer, len, "\u0947") || StemmerUtil.endsWith(buffer, len, "\u0942") || StemmerUtil.endsWith(buffer, len, "\u0941") || StemmerUtil.endsWith(buffer, len, "\u0940") || StemmerUtil.endsWith(buffer, len, "\u093f") || StemmerUtil.endsWith(buffer, len, "\u093e"))) {
            return len - 1;
        }
        return len;
    }
}
