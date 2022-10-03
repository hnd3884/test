package org.apache.lucene.analysis.tr;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class TurkishLowerCaseFilter extends TokenFilter
{
    private static final int LATIN_CAPITAL_LETTER_I = 73;
    private static final int LATIN_SMALL_LETTER_I = 105;
    private static final int LATIN_SMALL_LETTER_DOTLESS_I = 305;
    private static final int COMBINING_DOT_ABOVE = 775;
    private final CharTermAttribute termAtt;
    
    public TurkishLowerCaseFilter(final TokenStream in) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public final boolean incrementToken() throws IOException {
        boolean iOrAfter = false;
        if (this.input.incrementToken()) {
            final char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();
            int i = 0;
            while (i < length) {
                final int ch = Character.codePointAt(buffer, i, length);
                iOrAfter = (ch == 73 || (iOrAfter && Character.getType(ch) == 6));
                if (iOrAfter) {
                    switch (ch) {
                        case 775: {
                            length = this.delete(buffer, i, length);
                            continue;
                        }
                        case 73: {
                            if (this.isBeforeDot(buffer, i + 1, length)) {
                                buffer[i] = 'i';
                            }
                            else {
                                buffer[i] = '\u0131';
                                iOrAfter = false;
                            }
                            ++i;
                            continue;
                        }
                    }
                }
                i += Character.toChars(Character.toLowerCase(ch), buffer, i);
            }
            this.termAtt.setLength(length);
            return true;
        }
        return false;
    }
    
    private boolean isBeforeDot(final char[] s, final int pos, final int len) {
        int ch;
        for (int i = pos; i < len; i += Character.charCount(ch)) {
            ch = Character.codePointAt(s, i, len);
            if (Character.getType(ch) != 6) {
                return false;
            }
            if (ch == 775) {
                return true;
            }
        }
        return false;
    }
    
    private int delete(final char[] s, final int pos, final int len) {
        if (pos < len) {
            System.arraycopy(s, pos + 1, s, pos, len - pos - 1);
        }
        return len - 1;
    }
}
