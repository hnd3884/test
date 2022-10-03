package org.apache.lucene.search;

public class ScoreDoc
{
    public float score;
    public int doc;
    public int shardIndex;
    
    public ScoreDoc(final int doc, final float score) {
        this(doc, score, -1);
    }
    
    public ScoreDoc(final int doc, final float score, final int shardIndex) {
        this.doc = doc;
        this.score = score;
        this.shardIndex = shardIndex;
    }
    
    @Override
    public String toString() {
        return "doc=" + this.doc + " score=" + this.score + " shardIndex=" + this.shardIndex;
    }
}
