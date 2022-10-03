package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.AttributeImpl;

public class PayloadAttributeImpl extends AttributeImpl implements PayloadAttribute, Cloneable
{
    private BytesRef payload;
    
    public PayloadAttributeImpl() {
    }
    
    public PayloadAttributeImpl(final BytesRef payload) {
        this.payload = payload;
    }
    
    @Override
    public BytesRef getPayload() {
        return this.payload;
    }
    
    @Override
    public void setPayload(final BytesRef payload) {
        this.payload = payload;
    }
    
    @Override
    public void clear() {
        this.payload = null;
    }
    
    @Override
    public PayloadAttributeImpl clone() {
        final PayloadAttributeImpl clone = (PayloadAttributeImpl)super.clone();
        if (this.payload != null) {
            clone.payload = BytesRef.deepCopyOf(this.payload);
        }
        return clone;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PayloadAttribute)) {
            return false;
        }
        final PayloadAttributeImpl o = (PayloadAttributeImpl)other;
        if (o.payload == null || this.payload == null) {
            return o.payload == null && this.payload == null;
        }
        return o.payload.equals(this.payload);
    }
    
    @Override
    public int hashCode() {
        return (this.payload == null) ? 0 : this.payload.hashCode();
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        final PayloadAttribute t = (PayloadAttribute)target;
        t.setPayload((this.payload == null) ? null : BytesRef.deepCopyOf(this.payload));
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(PayloadAttribute.class, "payload", this.payload);
    }
}
