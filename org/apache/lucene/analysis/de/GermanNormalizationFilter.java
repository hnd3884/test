package org.apache.lucene.analysis.de;

import java.io.IOException;
import org.apache.lucene.analysis.util.StemmerUtil;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class GermanNormalizationFilter extends TokenFilter
{
    private static final int N = 0;
    private static final int V = 1;
    private static final int U = 2;
    private final CharTermAttribute termAtt;
    
    public GermanNormalizationFilter(final TokenStream input) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            int state = 0;
            char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();
            for (int i = 0; i < length; ++i) {
                final char c = buffer[i];
                switch (c) {
                    case 'a':
                    case 'o': {
                        state = 2;
                        break;
                    }
                    case 'u': {
                        state = ((state == 0) ? 2 : 1);
                        break;
                    }
                    case 'e': {
                        if (state == 2) {
                            length = StemmerUtil.delete(buffer, i--, length);
                        }
                        state = 1;
                        break;
                    }
                    case 'i':
                    case 'q':
                    case 'y': {
                        state = 1;
                        break;
                    }
                    case '\u00e4': {
                        buffer[i] = 'a';
                        state = 1;
                        break;
                    }
                    case '\u00f6': {
                        buffer[i] = 'o';
                        state = 1;
                        break;
                    }
                    case '\u00fc': {
                        buffer[i] = 'u';
                        state = 1;
                        break;
                    }
                    case '\u00df': {
                        buffer[i++] = 's';
                        buffer = this.termAtt.resizeBuffer(1 + length);
                        if (i < length) {
                            System.arraycopy(buffer, i, buffer, i + 1, length - i);
                        }
                        buffer[i] = 's';
                        ++length;
                        state = 0;
                        break;
                    }
                    default: {
                        state = 0;
                        break;
                    }
                }
            }
            this.termAtt.setLength(length);
            return true;
        }
        return false;
    }
}
