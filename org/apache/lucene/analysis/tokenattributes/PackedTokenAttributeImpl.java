package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeImpl;

public class PackedTokenAttributeImpl extends CharTermAttributeImpl implements TypeAttribute, PositionIncrementAttribute, PositionLengthAttribute, OffsetAttribute
{
    private int startOffset;
    private int endOffset;
    private String type;
    private int positionIncrement;
    private int positionLength;
    
    public PackedTokenAttributeImpl() {
        this.type = "word";
        this.positionIncrement = 1;
        this.positionLength = 1;
    }
    
    @Override
    public void setPositionIncrement(final int positionIncrement) {
        if (positionIncrement < 0) {
            throw new IllegalArgumentException("Increment must be zero or greater: " + positionIncrement);
        }
        this.positionIncrement = positionIncrement;
    }
    
    @Override
    public int getPositionIncrement() {
        return this.positionIncrement;
    }
    
    @Override
    public void setPositionLength(final int positionLength) {
        this.positionLength = positionLength;
    }
    
    @Override
    public int getPositionLength() {
        return this.positionLength;
    }
    
    @Override
    public final int startOffset() {
        return this.startOffset;
    }
    
    @Override
    public final int endOffset() {
        return this.endOffset;
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
    public final String type() {
        return this.type;
    }
    
    @Override
    public final void setType(final String type) {
        this.type = type;
    }
    
    @Override
    public void clear() {
        super.clear();
        final int n = 1;
        this.positionLength = n;
        this.positionIncrement = n;
        final int n2 = 0;
        this.endOffset = n2;
        this.startOffset = n2;
        this.type = "word";
    }
    
    @Override
    public PackedTokenAttributeImpl clone() {
        return (PackedTokenAttributeImpl)super.clone();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PackedTokenAttributeImpl) {
            final PackedTokenAttributeImpl other = (PackedTokenAttributeImpl)obj;
            if (this.startOffset == other.startOffset && this.endOffset == other.endOffset && this.positionIncrement == other.positionIncrement && this.positionLength == other.positionLength) {
                if (this.type == null) {
                    if (other.type != null) {
                        return false;
                    }
                }
                else if (!this.type.equals(other.type)) {
                    return false;
                }
                if (super.equals(obj)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int code = super.hashCode();
        code = code * 31 + this.startOffset;
        code = code * 31 + this.endOffset;
        code = code * 31 + this.positionIncrement;
        code = code * 31 + this.positionLength;
        if (this.type != null) {
            code = code * 31 + this.type.hashCode();
        }
        return code;
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        if (target instanceof PackedTokenAttributeImpl) {
            final PackedTokenAttributeImpl to = (PackedTokenAttributeImpl)target;
            to.copyBuffer(this.buffer(), 0, this.length());
            to.positionIncrement = this.positionIncrement;
            to.positionLength = this.positionLength;
            to.startOffset = this.startOffset;
            to.endOffset = this.endOffset;
            to.type = this.type;
        }
        else {
            super.copyTo(target);
            ((OffsetAttribute)target).setOffset(this.startOffset, this.endOffset);
            ((PositionIncrementAttribute)target).setPositionIncrement(this.positionIncrement);
            ((PositionLengthAttribute)target).setPositionLength(this.positionLength);
            ((TypeAttribute)target).setType(this.type);
        }
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        super.reflectWith(reflector);
        reflector.reflect(OffsetAttribute.class, "startOffset", this.startOffset);
        reflector.reflect(OffsetAttribute.class, "endOffset", this.endOffset);
        reflector.reflect(PositionIncrementAttribute.class, "positionIncrement", this.positionIncrement);
        reflector.reflect(PositionLengthAttribute.class, "positionLength", this.positionLength);
        reflector.reflect(TypeAttribute.class, "type", this.type);
    }
}
