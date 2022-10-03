package org.apache.lucene.analysis.payloads;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.analysis.TokenFilter;

public class NumericPayloadTokenFilter extends TokenFilter
{
    private String typeMatch;
    private BytesRef thePayload;
    private final PayloadAttribute payloadAtt;
    private final TypeAttribute typeAtt;
    
    public NumericPayloadTokenFilter(final TokenStream input, final float payload, final String typeMatch) {
        super(input);
        this.payloadAtt = (PayloadAttribute)this.addAttribute((Class)PayloadAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        if (typeMatch == null) {
            throw new IllegalArgumentException("typeMatch cannot be null");
        }
        this.thePayload = new BytesRef(PayloadHelper.encodeFloat(payload));
        this.typeMatch = typeMatch;
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (this.typeAtt.type().equals(this.typeMatch)) {
                this.payloadAtt.setPayload(this.thePayload);
            }
            return true;
        }
        return false;
    }
}
