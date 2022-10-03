package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeImpl;

public class OffsetAttributeImpl extends AttributeImpl implements OffsetAttribute, Cloneable
{
    private int startOffset;
    private int endOffset;
    
    @Override
    public int startOffset() {
        return this.startOffset;
    }
    
    @Override
    public void setOffset(final int startOffset, final int endOffset) {
        if (startOffset < 0 || endOffset < startOffset) {
            throw new IllegalArgumentException("startOffset must be non-negative, and endOffset must be >= startOffset, startOffset=" + startOffset + ",endOffset=" + endOffset);
        }
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }
    
    @Override
    public int endOffset() {
        return this.endOffset;
    }
    
    @Override
    public void clear() {
        this.startOffset = 0;
        this.endOffset = 0;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof OffsetAttributeImpl) {
            final OffsetAttributeImpl o = (OffsetAttributeImpl)other;
            return o.startOffset == this.startOffset && o.endOffset == this.endOffset;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int code = this.startOffset;
        code = code * 31 + this.endOffset;
        return code;
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        final OffsetAttribute t = (OffsetAttribute)target;
        t.setOffset(this.startOffset, this.endOffset);
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(OffsetAttribute.class, "startOffset", this.startOffset);
        reflector.reflect(OffsetAttribute.class, "endOffset", this.endOffset);
    }
}
