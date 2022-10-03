package org.apache.lucene.collation.tokenattributes;

import org.apache.lucene.util.BytesRef;
import java.text.Collator;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;

public class CollatedTermAttributeImpl extends CharTermAttributeImpl
{
    private final Collator collator;
    
    public CollatedTermAttributeImpl(final Collator collator) {
        this.collator = (Collator)collator.clone();
    }
    
    public BytesRef getBytesRef() {
        final BytesRef ref = this.builder.get();
        ref.bytes = this.collator.getCollationKey(this.toString()).toByteArray();
        ref.offset = 0;
        ref.length = ref.bytes.length;
        return ref;
    }
}
