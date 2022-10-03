package org.apache.lucene.search;

import java.util.Arrays;

public class FieldDoc extends ScoreDoc
{
    public Object[] fields;
    
    public FieldDoc(final int doc, final float score) {
        super(doc, score);
    }
    
    public FieldDoc(final int doc, final float score, final Object[] fields) {
        super(doc, score);
        this.fields = fields;
    }
    
    public FieldDoc(final int doc, final float score, final Object[] fields, final int shardIndex) {
        super(doc, score, shardIndex);
        this.fields = fields;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" fields=");
        sb.append(Arrays.toString(this.fields));
        return sb.toString();
    }
}
