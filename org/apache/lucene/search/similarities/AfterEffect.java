package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;

public abstract class AfterEffect
{
    public abstract float score(final BasicStats p0, final float p1);
    
    public abstract Explanation explain(final BasicStats p0, final float p1);
    
    @Override
    public abstract String toString();
    
    public static final class NoAfterEffect extends AfterEffect
    {
        @Override
        public final float score(final BasicStats stats, final float tfn) {
            return 1.0f;
        }
        
        @Override
        public final Explanation explain(final BasicStats stats, final float tfn) {
            return Explanation.match(1.0f, "no aftereffect", new Explanation[0]);
        }
        
        @Override
        public String toString() {
            return "";
        }
    }
}
