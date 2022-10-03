package org.apache.lucene.search;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeImpl;

public final class BoostAttributeImpl extends AttributeImpl implements BoostAttribute
{
    private float boost;
    
    public BoostAttributeImpl() {
        this.boost = 1.0f;
    }
    
    @Override
    public void setBoost(final float boost) {
        this.boost = boost;
    }
    
    @Override
    public float getBoost() {
        return this.boost;
    }
    
    @Override
    public void clear() {
        this.boost = 1.0f;
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        ((BoostAttribute)target).setBoost(this.boost);
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(BoostAttribute.class, "boost", this.boost);
    }
}
