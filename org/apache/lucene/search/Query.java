package org.apache.lucene.search;

import org.apache.lucene.index.IndexReader;
import java.io.IOException;

public abstract class Query implements Cloneable
{
    private float boost;
    
    public Query() {
        this.boost = 1.0f;
    }
    
    @Deprecated
    public void setBoost(final float b) {
        this.boost = b;
    }
    
    @Deprecated
    public float getBoost() {
        return this.boost;
    }
    
    public abstract String toString(final String p0);
    
    @Override
    public final String toString() {
        return this.toString("");
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        throw new UnsupportedOperationException("Query " + this + " does not implement createWeight");
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.boost != 1.0f) {
            final Query rewritten = this.clone();
            rewritten.setBoost(1.0f);
            return new BoostQuery(rewritten, this.boost);
        }
        return this;
    }
    
    @Deprecated
    public Query clone() {
        try {
            return (Query)super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported: " + e.getMessage());
        }
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.boost) ^ this.getClass().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Query other = (Query)obj;
        return Float.floatToIntBits(this.boost) == Float.floatToIntBits(other.boost);
    }
}
