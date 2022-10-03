package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeImpl;

public class PositionIncrementAttributeImpl extends AttributeImpl implements PositionIncrementAttribute, Cloneable
{
    private int positionIncrement;
    
    public PositionIncrementAttributeImpl() {
        this.positionIncrement = 1;
    }
    
    @Override
    public void setPositionIncrement(final int positionIncrement) {
        if (positionIncrement < 0) {
            throw new IllegalArgumentException("Increment must be zero or greater: got " + positionIncrement);
        }
        this.positionIncrement = positionIncrement;
    }
    
    @Override
    public int getPositionIncrement() {
        return this.positionIncrement;
    }
    
    @Override
    public void clear() {
        this.positionIncrement = 1;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof PositionIncrementAttributeImpl) {
            final PositionIncrementAttributeImpl _other = (PositionIncrementAttributeImpl)other;
            return this.positionIncrement == _other.positionIncrement;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.positionIncrement;
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        final PositionIncrementAttribute t = (PositionIncrementAttribute)target;
        t.setPositionIncrement(this.positionIncrement);
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(PositionIncrementAttribute.class, "positionIncrement", this.positionIncrement);
    }
}
