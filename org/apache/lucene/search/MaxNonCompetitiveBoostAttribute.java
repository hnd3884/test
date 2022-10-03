package org.apache.lucene.search;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Attribute;

public interface MaxNonCompetitiveBoostAttribute extends Attribute
{
    void setMaxNonCompetitiveBoost(final float p0);
    
    float getMaxNonCompetitiveBoost();
    
    void setCompetitiveTerm(final BytesRef p0);
    
    BytesRef getCompetitiveTerm();
}
