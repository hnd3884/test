package org.apache.lucene.search.similarities;

public class BasicStats extends Similarity.SimWeight
{
    final String field;
    protected long numberOfDocuments;
    protected long numberOfFieldTokens;
    protected float avgFieldLength;
    protected long docFreq;
    protected long totalTermFreq;
    protected float boost;
    
    public BasicStats(final String field) {
        this.field = field;
        this.normalize(1.0f, 1.0f);
    }
    
    public long getNumberOfDocuments() {
        return this.numberOfDocuments;
    }
    
    public void setNumberOfDocuments(final long numberOfDocuments) {
        this.numberOfDocuments = numberOfDocuments;
    }
    
    public long getNumberOfFieldTokens() {
        return this.numberOfFieldTokens;
    }
    
    public void setNumberOfFieldTokens(final long numberOfFieldTokens) {
        this.numberOfFieldTokens = numberOfFieldTokens;
    }
    
    public float getAvgFieldLength() {
        return this.avgFieldLength;
    }
    
    public void setAvgFieldLength(final float avgFieldLength) {
        this.avgFieldLength = avgFieldLength;
    }
    
    public long getDocFreq() {
        return this.docFreq;
    }
    
    public void setDocFreq(final long docFreq) {
        this.docFreq = docFreq;
    }
    
    public long getTotalTermFreq() {
        return this.totalTermFreq;
    }
    
    public void setTotalTermFreq(final long totalTermFreq) {
        this.totalTermFreq = totalTermFreq;
    }
    
    @Override
    public float getValueForNormalization() {
        final float rawValue = this.rawNormalizationValue();
        return rawValue * rawValue;
    }
    
    protected float rawNormalizationValue() {
        return this.boost;
    }
    
    @Override
    public void normalize(final float queryNorm, final float boost) {
        this.boost = boost;
    }
    
    public float getBoost() {
        return this.boost;
    }
}
