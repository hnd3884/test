package org.apache.lucene.analysis.payloads;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.TokenFilter;

public class TokenOffsetPayloadTokenFilter extends TokenFilter
{
    private final OffsetAttribute offsetAtt;
    private final PayloadAttribute payAtt;
    
    public TokenOffsetPayloadTokenFilter(final TokenStream input) {
        super(input);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.payAtt = (PayloadAttribute)this.addAttribute((Class)PayloadAttribute.class);
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            final byte[] data = new byte[8];
            PayloadHelper.encodeInt(this.offsetAtt.startOffset(), data, 0);
            PayloadHelper.encodeInt(this.offsetAtt.endOffset(), data, 4);
            final BytesRef payload = new BytesRef(data);
            this.payAtt.setPayload(payload);
            return true;
        }
        return false;
    }
}
