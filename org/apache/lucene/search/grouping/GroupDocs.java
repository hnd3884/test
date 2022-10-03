package org.apache.lucene.search.grouping;

import org.apache.lucene.search.ScoreDoc;

public class GroupDocs<GROUP_VALUE_TYPE>
{
    public final GROUP_VALUE_TYPE groupValue;
    public final float maxScore;
    public final float score;
    public final ScoreDoc[] scoreDocs;
    public final int totalHits;
    public final Object[] groupSortValues;
    
    public GroupDocs(final float score, final float maxScore, final int totalHits, final ScoreDoc[] scoreDocs, final GROUP_VALUE_TYPE groupValue, final Object[] groupSortValues) {
        this.score = score;
        this.maxScore = maxScore;
        this.totalHits = totalHits;
        this.scoreDocs = scoreDocs;
        this.groupValue = groupValue;
        this.groupSortValues = groupSortValues;
    }
}
