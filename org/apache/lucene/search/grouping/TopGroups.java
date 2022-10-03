package org.apache.lucene.search.grouping;

import java.io.IOException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

public class TopGroups<GROUP_VALUE_TYPE>
{
    public final int totalHitCount;
    public final int totalGroupedHitCount;
    public final Integer totalGroupCount;
    public final GroupDocs<GROUP_VALUE_TYPE>[] groups;
    public final SortField[] groupSort;
    public final SortField[] withinGroupSort;
    public final float maxScore;
    
    public TopGroups(final SortField[] groupSort, final SortField[] withinGroupSort, final int totalHitCount, final int totalGroupedHitCount, final GroupDocs<GROUP_VALUE_TYPE>[] groups, final float maxScore) {
        this.groupSort = groupSort;
        this.withinGroupSort = withinGroupSort;
        this.totalHitCount = totalHitCount;
        this.totalGroupedHitCount = totalGroupedHitCount;
        this.groups = groups;
        this.totalGroupCount = null;
        this.maxScore = maxScore;
    }
    
    public TopGroups(final TopGroups<GROUP_VALUE_TYPE> oldTopGroups, final Integer totalGroupCount) {
        this.groupSort = oldTopGroups.groupSort;
        this.withinGroupSort = oldTopGroups.withinGroupSort;
        this.totalHitCount = oldTopGroups.totalHitCount;
        this.totalGroupedHitCount = oldTopGroups.totalGroupedHitCount;
        this.groups = oldTopGroups.groups;
        this.maxScore = oldTopGroups.maxScore;
        this.totalGroupCount = totalGroupCount;
    }
    
    public static <T> TopGroups<T> merge(final TopGroups<T>[] shardGroups, final Sort groupSort, final Sort docSort, final int docOffset, final int docTopN, final ScoreMergeMode scoreMergeMode) throws IOException {
        if (shardGroups.length == 0) {
            return null;
        }
        int totalHitCount = 0;
        int totalGroupedHitCount = 0;
        Integer totalGroupCount = null;
        final int numGroups = shardGroups[0].groups.length;
        for (final TopGroups<T> shard : shardGroups) {
            if (numGroups != shard.groups.length) {
                throw new IllegalArgumentException("number of groups differs across shards; you must pass same top groups to all shards' second-pass collector");
            }
            totalHitCount += shard.totalHitCount;
            totalGroupedHitCount += shard.totalGroupedHitCount;
            if (shard.totalGroupCount != null) {
                if (totalGroupCount == null) {
                    totalGroupCount = 0;
                }
                totalGroupCount += shard.totalGroupCount;
            }
        }
        final GroupDocs<T>[] mergedGroupDocs = new GroupDocs[numGroups];
        TopDocs[] shardTopDocs;
        if (docSort.equals((Object)Sort.RELEVANCE)) {
            shardTopDocs = new TopDocs[shardGroups.length];
        }
        else {
            shardTopDocs = (TopDocs[])new TopFieldDocs[shardGroups.length];
        }
        float totalMaxScore = Float.MIN_VALUE;
        for (int groupIDX = 0; groupIDX < numGroups; ++groupIDX) {
            final T groupValue = shardGroups[0].groups[groupIDX].groupValue;
            float maxScore = Float.MIN_VALUE;
            int totalHits = 0;
            double scoreSum = 0.0;
            for (int shardIDX = 0; shardIDX < shardGroups.length; ++shardIDX) {
                final TopGroups<T> shard2 = shardGroups[shardIDX];
                final GroupDocs<?> shardGroupDocs = shard2.groups[groupIDX];
                if (groupValue == null) {
                    if (shardGroupDocs.groupValue != null) {
                        throw new IllegalArgumentException("group values differ across shards; you must pass same top groups to all shards' second-pass collector");
                    }
                }
                else if (!groupValue.equals(shardGroupDocs.groupValue)) {
                    throw new IllegalArgumentException("group values differ across shards; you must pass same top groups to all shards' second-pass collector");
                }
                if (docSort.equals((Object)Sort.RELEVANCE)) {
                    shardTopDocs[shardIDX] = new TopDocs(shardGroupDocs.totalHits, shardGroupDocs.scoreDocs, shardGroupDocs.maxScore);
                }
                else {
                    shardTopDocs[shardIDX] = (TopDocs)new TopFieldDocs(shardGroupDocs.totalHits, shardGroupDocs.scoreDocs, docSort.getSort(), shardGroupDocs.maxScore);
                }
                maxScore = Math.max(maxScore, shardGroupDocs.maxScore);
                totalHits += shardGroupDocs.totalHits;
                scoreSum += shardGroupDocs.score;
            }
            TopDocs mergedTopDocs;
            if (docSort.equals((Object)Sort.RELEVANCE)) {
                mergedTopDocs = TopDocs.merge(docOffset + docTopN, shardTopDocs);
            }
            else {
                mergedTopDocs = (TopDocs)TopDocs.merge(docSort, docOffset + docTopN, (TopFieldDocs[])shardTopDocs);
            }
            ScoreDoc[] mergedScoreDocs;
            if (docOffset == 0) {
                mergedScoreDocs = mergedTopDocs.scoreDocs;
            }
            else if (docOffset >= mergedTopDocs.scoreDocs.length) {
                mergedScoreDocs = new ScoreDoc[0];
            }
            else {
                mergedScoreDocs = new ScoreDoc[mergedTopDocs.scoreDocs.length - docOffset];
                System.arraycopy(mergedTopDocs.scoreDocs, docOffset, mergedScoreDocs, 0, mergedTopDocs.scoreDocs.length - docOffset);
            }
            float groupScore = 0.0f;
            switch (scoreMergeMode) {
                case None: {
                    groupScore = Float.NaN;
                    break;
                }
                case Avg: {
                    if (totalHits > 0) {
                        groupScore = (float)(scoreSum / totalHits);
                        break;
                    }
                    groupScore = Float.NaN;
                    break;
                }
                case Total: {
                    groupScore = (float)scoreSum;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("can't handle ScoreMergeMode " + scoreMergeMode);
                }
            }
            mergedGroupDocs[groupIDX] = new GroupDocs<T>(groupScore, maxScore, totalHits, mergedScoreDocs, groupValue, shardGroups[0].groups[groupIDX].groupSortValues);
            totalMaxScore = Math.max(totalMaxScore, maxScore);
        }
        if (totalGroupCount != null) {
            final TopGroups<T> result = new TopGroups<T>(groupSort.getSort(), docSort.getSort(), totalHitCount, totalGroupedHitCount, mergedGroupDocs, totalMaxScore);
            return new TopGroups<T>(result, totalGroupCount);
        }
        return new TopGroups<T>(groupSort.getSort(), docSort.getSort(), totalHitCount, totalGroupedHitCount, mergedGroupDocs, totalMaxScore);
    }
    
    public enum ScoreMergeMode
    {
        None, 
        Total, 
        Avg;
    }
}
