package org.apache.lucene.analysis.sr;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class SerbianNormalizationFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    
    public SerbianNormalizationFilter(final TokenStream input) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();
            for (int i = 0; i < length; ++i) {
                final char c = buffer[i];
                switch (c) {
                    case '\u0430': {
                        buffer[i] = 'a';
                        break;
                    }
                    case '\u0431': {
                        buffer[i] = 'b';
                        break;
                    }
                    case '\u0432': {
                        buffer[i] = 'v';
                        break;
                    }
                    case '\u0433': {
                        buffer[i] = 'g';
                        break;
                    }
                    case '\u0434': {
                        buffer[i] = 'd';
                        break;
                    }
                    case '\u0111':
                    case '\u0452': {
                        buffer = this.termAtt.resizeBuffer(1 + length);
                        if (i < length) {
                            System.arraycopy(buffer, i, buffer, i + 1, length - i);
                        }
                        buffer[i] = 'd';
                        buffer[++i] = 'j';
                        ++length;
                        break;
                    }
                    case '\u0435': {
                        buffer[i] = 'e';
                        break;
                    }
                    case '\u017e':
                    case '\u0436':
                    case '\u0437': {
                        buffer[i] = 'z';
                        break;
                    }
                    case '\u0438': {
                        buffer[i] = 'i';
                        break;
                    }
                    case '\u0458': {
                        buffer[i] = 'j';
                        break;
                    }
                    case '\u043a': {
                        buffer[i] = 'k';
                        break;
                    }
                    case '\u043b': {
                        buffer[i] = 'l';
                        break;
                    }
                    case '\u0459': {
                        buffer = this.termAtt.resizeBuffer(1 + length);
                        if (i < length) {
                            System.arraycopy(buffer, i, buffer, i + 1, length - i);
                        }
                        buffer[i] = 'l';
                        buffer[++i] = 'j';
                        ++length;
                        break;
                    }
                    case '\u043c': {
                        buffer[i] = 'm';
                        break;
                    }
                    case '\u043d': {
                        buffer[i] = 'n';
                        break;
                    }
                    case '\u045a': {
                        buffer = this.termAtt.resizeBuffer(1 + length);
                        if (i < length) {
                            System.arraycopy(buffer, i, buffer, i + 1, length - i);
                        }
                        buffer[i] = 'n';
                        buffer[++i] = 'j';
                        ++length;
                        break;
                    }
                    case '\u043e': {
                        buffer[i] = 'o';
                        break;
                    }
                    case '\u043f': {
                        buffer[i] = 'p';
                        break;
                    }
                    case '\u0440': {
                        buffer[i] = 'r';
                        break;
                    }
                    case '\u0441': {
                        buffer[i] = 's';
                        break;
                    }
                    case '\u0442': {
                        buffer[i] = 't';
                        break;
                    }
                    case '\u0107':
                    case '\u010d':
                    case '\u0446':
                    case '\u0447':
                    case '\u045b': {
                        buffer[i] = 'c';
                        break;
                    }
                    case '\u0443': {
                        buffer[i] = 'u';
                        break;
                    }
                    case '\u0444': {
                        buffer[i] = 'f';
                        break;
                    }
                    case '\u0445': {
                        buffer[i] = 'h';
                        break;
                    }
                    case '\u045f': {
                        buffer = this.termAtt.resizeBuffer(1 + length);
                        if (i < length) {
                            System.arraycopy(buffer, i, buffer, i + 1, length - i);
                        }
                        buffer[i] = 'd';
                        buffer[++i] = 'z';
                        ++length;
                        break;
                    }
                    case '\u0161':
                    case '\u0448': {
                        buffer[i] = 's';
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
