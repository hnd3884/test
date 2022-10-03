package org.apache.lucene.search.postingshighlight;

public class PassageScorer
{
    final float k1;
    final float b;
    final float pivot;
    
    public PassageScorer() {
        this(1.2f, 0.75f, 87.0f);
    }
    
    public PassageScorer(final float k1, final float b, final float pivot) {
        this.k1 = k1;
        this.b = b;
        this.pivot = pivot;
    }
    
    public float weight(final int contentLength, final int totalTermFreq) {
        final float numDocs = 1.0f + contentLength / this.pivot;
        return (this.k1 + 1.0f) * (float)Math.log(1.0 + (numDocs + 0.5) / (totalTermFreq + 0.5));
    }
    
    public float tf(final int freq, final int passageLen) {
        final float norm = this.k1 * (1.0f - this.b + this.b * (passageLen / this.pivot));
        return freq / (freq + norm);
    }
    
    public float norm(final int passageStart) {
        return 1.0f + 1.0f / (float)Math.log(this.pivot + passageStart);
    }
}
