package org.apache.lucene.analysis.tokenattributes;

import java.util.Objects;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.AttributeImpl;

public class BytesTermAttributeImpl extends AttributeImpl implements BytesTermAttribute, TermToBytesRefAttribute
{
    private BytesRef bytes;
    
    @Override
    public BytesRef getBytesRef() {
        return this.bytes;
    }
    
    @Override
    public void setBytesRef(final BytesRef bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public void clear() {
        this.bytes = null;
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        final BytesTermAttributeImpl other = (BytesTermAttributeImpl)target;
        other.bytes = ((this.bytes == null) ? null : BytesRef.deepCopyOf(this.bytes));
    }
    
    @Override
    public AttributeImpl clone() {
        final BytesTermAttributeImpl c = (BytesTermAttributeImpl)super.clone();
        this.copyTo(c);
        return c;
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(TermToBytesRefAttribute.class, "bytes", this.bytes);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BytesTermAttributeImpl)) {
            return false;
        }
        final BytesTermAttributeImpl that = (BytesTermAttributeImpl)o;
        return Objects.equals(this.bytes, that.bytes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.bytes);
    }
}
