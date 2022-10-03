package org.apache.lucene.search;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.AttributeImpl;

public final class MaxNonCompetitiveBoostAttributeImpl extends AttributeImpl implements MaxNonCompetitiveBoostAttribute
{
    private float maxNonCompetitiveBoost;
    private BytesRef competitiveTerm;
    
    public MaxNonCompetitiveBoostAttributeImpl() {
        this.maxNonCompetitiveBoost = Float.NEGATIVE_INFINITY;
        this.competitiveTerm = null;
    }
    
    @Override
    public void setMaxNonCompetitiveBoost(final float maxNonCompetitiveBoost) {
        this.maxNonCompetitiveBoost = maxNonCompetitiveBoost;
    }
    
    @Override
    public float getMaxNonCompetitiveBoost() {
        return this.maxNonCompetitiveBoost;
    }
    
    @Override
    public void setCompetitiveTerm(final BytesRef competitiveTerm) {
        this.competitiveTerm = competitiveTerm;
    }
    
    @Override
    public BytesRef getCompetitiveTerm() {
        return this.competitiveTerm;
    }
    
    @Override
    public void clear() {
        this.maxNonCompetitiveBoost = Float.NEGATIVE_INFINITY;
        this.competitiveTerm = null;
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        final MaxNonCompetitiveBoostAttributeImpl t = (MaxNonCompetitiveBoostAttributeImpl)target;
        t.setMaxNonCompetitiveBoost(this.maxNonCompetitiveBoost);
        t.setCompetitiveTerm(this.competitiveTerm);
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(MaxNonCompetitiveBoostAttribute.class, "maxNonCompetitiveBoost", this.maxNonCompetitiveBoost);
        reflector.reflect(MaxNonCompetitiveBoostAttribute.class, "competitiveTerm", this.competitiveTerm);
    }
}
