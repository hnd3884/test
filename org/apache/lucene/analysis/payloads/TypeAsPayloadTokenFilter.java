package org.apache.lucene.analysis.payloads;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.TokenFilter;

public class TypeAsPayloadTokenFilter extends TokenFilter
{
    private final PayloadAttribute payloadAtt;
    private final TypeAttribute typeAtt;
    
    public TypeAsPayloadTokenFilter(final TokenStream input) {
        super(input);
        this.payloadAtt = (PayloadAttribute)this.addAttribute((Class)PayloadAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            final String type = this.typeAtt.type();
            if (type != null && !type.isEmpty()) {
                this.payloadAtt.setPayload(new BytesRef((CharSequence)type));
            }
            return true;
        }
        return false;
    }
}
