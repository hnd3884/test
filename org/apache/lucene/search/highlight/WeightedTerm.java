package org.apache.lucene.search.highlight;

public class WeightedTerm
{
    float weight;
    String term;
    
    public WeightedTerm(final float weight, final String term) {
        this.weight = weight;
        this.term = term;
    }
    
    public String getTerm() {
        return this.term;
    }
    
    public float getWeight() {
        return this.weight;
    }
    
    public void setTerm(final String term) {
        this.term = term;
    }
    
    public void setWeight(final float weight) {
        this.weight = weight;
    }
}
