package org.apache.lucene.analysis.payloads;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class DelimitedPayloadTokenFilter extends TokenFilter
{
    public static final char DEFAULT_DELIMITER = '|';
    private final char delimiter;
    private final CharTermAttribute termAtt;
    private final PayloadAttribute payAtt;
    private final PayloadEncoder encoder;
    
    public DelimitedPayloadTokenFilter(final TokenStream input, final char delimiter, final PayloadEncoder encoder) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.payAtt = (PayloadAttribute)this.addAttribute((Class)PayloadAttribute.class);
        this.delimiter = delimiter;
        this.encoder = encoder;
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            final char[] buffer = this.termAtt.buffer();
            for (int length = this.termAtt.length(), i = 0; i < length; ++i) {
                if (buffer[i] == this.delimiter) {
                    this.payAtt.setPayload(this.encoder.encode(buffer, i + 1, length - (i + 1)));
                    this.termAtt.setLength(i);
                    return true;
                }
            }
            this.payAtt.setPayload((BytesRef)null);
            return true;
        }
        return false;
    }
}
