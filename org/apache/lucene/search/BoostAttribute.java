package org.apache.lucene.search;

import org.apache.lucene.util.Attribute;

public interface BoostAttribute extends Attribute
{
    void setBoost(final float p0);
    
    float getBoost();
}
