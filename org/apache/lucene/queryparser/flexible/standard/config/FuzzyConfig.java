package org.apache.lucene.queryparser.flexible.standard.config;

public class FuzzyConfig
{
    private int prefixLength;
    private float minSimilarity;
    
    public FuzzyConfig() {
        this.prefixLength = 0;
        this.minSimilarity = 2.0f;
    }
    
    public int getPrefixLength() {
        return this.prefixLength;
    }
    
    public void setPrefixLength(final int prefixLength) {
        this.prefixLength = prefixLength;
    }
    
    public float getMinSimilarity() {
        return this.minSimilarity;
    }
    
    public void setMinSimilarity(final float minSimilarity) {
        this.minSimilarity = minSimilarity;
    }
}
