package org.apache.lucene.search;

public class TopFieldDocs extends TopDocs
{
    public SortField[] fields;
    
    public TopFieldDocs(final int totalHits, final ScoreDoc[] scoreDocs, final SortField[] fields, final float maxScore) {
        super(totalHits, scoreDocs, maxScore);
        this.fields = fields;
    }
}
