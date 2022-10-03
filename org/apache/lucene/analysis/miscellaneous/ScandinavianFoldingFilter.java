package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.util.StemmerUtil;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class ScandinavianFoldingFilter extends TokenFilter
{
    private final CharTermAttribute charTermAttribute;
    private static final char AA = '\u00c5';
    private static final char aa = '\u00e5';
    private static final char AE = '\u00c6';
    private static final char ae = '\u00e6';
    private static final char AE_se = '\u00c4';
    private static final char ae_se = '\u00e4';
    private static final char OE = '\u00d8';
    private static final char oe = '\u00f8';
    private static final char OE_se = '\u00d6';
    private static final char oe_se = '\u00f6';
    
    public ScandinavianFoldingFilter(final TokenStream input) {
        super(input);
        this.charTermAttribute = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        final char[] buffer = this.charTermAttribute.buffer();
        int length = this.charTermAttribute.length();
        for (int i = 0; i < length; ++i) {
            if (buffer[i] == '\u00e5' || buffer[i] == '\u00e4' || buffer[i] == '\u00e6') {
                buffer[i] = 'a';
            }
            else if (buffer[i] == '\u00c5' || buffer[i] == '\u00c4' || buffer[i] == '\u00c6') {
                buffer[i] = 'A';
            }
            else if (buffer[i] == '\u00f8' || buffer[i] == '\u00f6') {
                buffer[i] = 'o';
            }
            else if (buffer[i] == '\u00d8' || buffer[i] == '\u00d6') {
                buffer[i] = 'O';
            }
            else if (length - 1 > i) {
                if ((buffer[i] == 'a' || buffer[i] == 'A') && (buffer[i + 1] == 'a' || buffer[i + 1] == 'A' || buffer[i + 1] == 'e' || buffer[i + 1] == 'E' || buffer[i + 1] == 'o' || buffer[i + 1] == 'O')) {
                    length = StemmerUtil.delete(buffer, i + 1, length);
                }
                else if ((buffer[i] == 'o' || buffer[i] == 'O') && (buffer[i + 1] == 'e' || buffer[i + 1] == 'E' || buffer[i + 1] == 'o' || buffer[i + 1] == 'O')) {
                    length = StemmerUtil.delete(buffer, i + 1, length);
                }
            }
        }
        this.charTermAttribute.setLength(length);
        return true;
    }
}
