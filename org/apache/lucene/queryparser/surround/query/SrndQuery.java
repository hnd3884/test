package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;

public abstract class SrndQuery implements Cloneable
{
    private float weight;
    private boolean weighted;
    
    public SrndQuery() {
        this.weight = 1.0f;
        this.weighted = false;
    }
    
    public void setWeight(final float w) {
        this.weight = w;
        this.weighted = true;
    }
    
    public boolean isWeighted() {
        return this.weighted;
    }
    
    public float getWeight() {
        return this.weight;
    }
    
    public String getWeightString() {
        return Float.toString(this.getWeight());
    }
    
    public String getWeightOperator() {
        return "^";
    }
    
    protected void weightToString(final StringBuilder r) {
        if (this.isWeighted()) {
            r.append(this.getWeightOperator());
            r.append(this.getWeightString());
        }
    }
    
    public Query makeLuceneQueryField(final String fieldName, final BasicQueryFactory qf) {
        Query q = this.makeLuceneQueryFieldNoBoost(fieldName, qf);
        if (this.isWeighted()) {
            q = (Query)new BoostQuery(q, this.getWeight());
        }
        return q;
    }
    
    public abstract Query makeLuceneQueryFieldNoBoost(final String p0, final BasicQueryFactory p1);
    
    @Override
    public abstract String toString();
    
    public boolean isFieldsSubQueryAcceptable() {
        return true;
    }
    
    public SrndQuery clone() {
        try {
            return (SrndQuery)super.clone();
        }
        catch (final CloneNotSupportedException cns) {
            throw new Error(cns);
        }
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode() ^ this.toString().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null && this.getClass().equals(obj.getClass()) && this.toString().equals(obj.toString());
    }
}
