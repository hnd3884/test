package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;

public abstract class Normalization
{
    public abstract float tfn(final BasicStats p0, final float p1, final float p2);
    
    public Explanation explain(final BasicStats stats, final float tf, final float len) {
        return Explanation.match(this.tfn(stats, tf, len), this.getClass().getSimpleName() + ", computed from: ", Explanation.match(tf, "tf", new Explanation[0]), Explanation.match(stats.getAvgFieldLength(), "avgFieldLength", new Explanation[0]), Explanation.match(len, "len", new Explanation[0]));
    }
    
    @Override
    public abstract String toString();
    
    public static final class NoNormalization extends Normalization
    {
        @Override
        public final float tfn(final BasicStats stats, final float tf, final float len) {
            return tf;
        }
        
        @Override
        public final Explanation explain(final BasicStats stats, final float tf, final float len) {
            return Explanation.match(1.0f, "no normalization", new Explanation[0]);
        }
        
        @Override
        public String toString() {
            return "";
        }
    }
}
