package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeImpl;

public class PositionLengthAttributeImpl extends AttributeImpl implements PositionLengthAttribute, Cloneable
{
    private int positionLength;
    
    public PositionLengthAttributeImpl() {
        this.positionLength = 1;
    }
    
    @Override
    public void setPositionLength(final int positionLength) {
        if (positionLength < 1) {
            throw new IllegalArgumentException("Position length must be 1 or greater: got " + positionLength);
        }
        this.positionLength = positionLength;
    }
    
    @Override
    public int getPositionLength() {
        return this.positionLength;
    }
    
    @Override
    public void clear() {
        this.positionLength = 1;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof PositionLengthAttributeImpl) {
            final PositionLengthAttributeImpl _other = (PositionLengthAttributeImpl)other;
            return this.positionLength == _other.positionLength;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.positionLength;
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        final PositionLengthAttribute t = (PositionLengthAttribute)target;
        t.setPositionLength(this.positionLength);
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(PositionLengthAttribute.class, "positionLength", this.positionLength);
    }
}
