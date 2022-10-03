package org.apache.lucene.queries.payloads;

public class MinPayloadFunction extends PayloadFunction
{
    @Override
    public float currentScore(final int docId, final String field, final int start, final int end, final int numPayloadsSeen, final float currentScore, final float currentPayloadScore) {
        if (numPayloadsSeen == 0) {
            return currentPayloadScore;
        }
        return Math.min(currentPayloadScore, currentScore);
    }
    
    @Override
    public float docScore(final int docId, final String field, final int numPayloadsSeen, final float payloadScore) {
        return (numPayloadsSeen > 0) ? payloadScore : 1.0f;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.getClass().hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj != null && this.getClass() == obj.getClass());
    }
}
