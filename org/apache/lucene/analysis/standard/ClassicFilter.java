package org.apache.lucene.analysis.standard;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.TokenFilter;

public class ClassicFilter extends TokenFilter
{
    private static final String APOSTROPHE_TYPE;
    private static final String ACRONYM_TYPE;
    private final TypeAttribute typeAtt;
    private final CharTermAttribute termAtt;
    
    public ClassicFilter(final TokenStream in) {
        super(in);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public final boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        final char[] buffer = this.termAtt.buffer();
        final int bufferLength = this.termAtt.length();
        final String type = this.typeAtt.type();
        if (type == ClassicFilter.APOSTROPHE_TYPE && bufferLength >= 2 && buffer[bufferLength - 2] == '\'' && (buffer[bufferLength - 1] == 's' || buffer[bufferLength - 1] == 'S')) {
            this.termAtt.setLength(bufferLength - 2);
        }
        else if (type == ClassicFilter.ACRONYM_TYPE) {
            int upto = 0;
            for (final char c : buffer) {
                if (c != '.') {
                    buffer[upto++] = c;
                }
            }
            this.termAtt.setLength(upto);
        }
        return true;
    }
    
    static {
        APOSTROPHE_TYPE = ClassicTokenizer.TOKEN_TYPES[1];
        ACRONYM_TYPE = ClassicTokenizer.TOKEN_TYPES[2];
    }
}
