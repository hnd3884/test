package org.apache.lucene.search.grouping;

import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Scorer;
import java.io.IOException;
import org.apache.lucene.search.TopDocsCollector;
import java.util.Iterator;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map;
import org.apache.lucene.search.Sort;
import java.util.Collection;
import org.apache.lucene.search.SimpleCollector;

public abstract class AbstractSecondPassGroupingCollector<GROUP_VALUE_TYPE> extends SimpleCollector
{
    private final Collection<SearchGroup<GROUP_VALUE_TYPE>> groups;
    private final Sort groupSort;
    private final Sort withinGroupSort;
    private final int maxDocsPerGroup;
    private final boolean needsScores;
    protected final Map<GROUP_VALUE_TYPE, SearchGroupDocs<GROUP_VALUE_TYPE>> groupMap;
    protected SearchGroupDocs<GROUP_VALUE_TYPE>[] groupDocs;
    private int totalHitCount;
    private int totalGroupedHitCount;
    
    public AbstractSecondPassGroupingCollector(final Collection<SearchGroup<GROUP_VALUE_TYPE>> groups, final Sort groupSort, final Sort withinGroupSort, final int maxDocsPerGroup, final boolean getScores, final boolean getMaxScores, final boolean fillSortFields) throws IOException {
        if (groups.isEmpty()) {
            throw new IllegalArgumentException("no groups to collect (groups is empty)");
        }
        this.groups = Objects.requireNonNull(groups);
        this.groupSort = Objects.requireNonNull(groupSort);
        this.withinGroupSort = Objects.requireNonNull(withinGroupSort);
        this.maxDocsPerGroup = maxDocsPerGroup;
        this.needsScores = (getScores || getMaxScores || withinGroupSort.needsScores());
        this.groupMap = new HashMap<GROUP_VALUE_TYPE, SearchGroupDocs<GROUP_VALUE_TYPE>>(groups.size());
        for (final SearchGroup<GROUP_VALUE_TYPE> group : groups) {
            TopDocsCollector<?> collector;
            if (withinGroupSort.equals((Object)Sort.RELEVANCE)) {
                collector = (TopDocsCollector<?>)TopScoreDocCollector.create(maxDocsPerGroup);
            }
            else {
                collector = (TopDocsCollector<?>)TopFieldCollector.create(withinGroupSort, maxDocsPerGroup, fillSortFields, getScores, getMaxScores);
            }
            this.groupMap.put(group.groupValue, new SearchGroupDocs<GROUP_VALUE_TYPE>(group.groupValue, collector));
        }
    }
    
    public boolean needsScores() {
        return this.needsScores;
    }
    
    public void setScorer(final Scorer scorer) throws IOException {
        for (final SearchGroupDocs<GROUP_VALUE_TYPE> group : this.groupMap.values()) {
            group.leafCollector.setScorer(scorer);
        }
    }
    
    public void collect(final int doc) throws IOException {
        ++this.totalHitCount;
        final SearchGroupDocs<GROUP_VALUE_TYPE> group = this.retrieveGroup(doc);
        if (group != null) {
            ++this.totalGroupedHitCount;
            group.leafCollector.collect(doc);
        }
    }
    
    protected abstract SearchGroupDocs<GROUP_VALUE_TYPE> retrieveGroup(final int p0) throws IOException;
    
    protected void doSetNextReader(final LeafReaderContext readerContext) throws IOException {
        for (final SearchGroupDocs<GROUP_VALUE_TYPE> group : this.groupMap.values()) {
            group.leafCollector = group.collector.getLeafCollector(readerContext);
        }
    }
    
    public TopGroups<GROUP_VALUE_TYPE> getTopGroups(final int withinGroupOffset) {
        final GroupDocs<GROUP_VALUE_TYPE>[] groupDocsResult = new GroupDocs[this.groups.size()];
        int groupIDX = 0;
        float maxScore = Float.MIN_VALUE;
        for (final SearchGroup<?> group : this.groups) {
            final SearchGroupDocs<GROUP_VALUE_TYPE> groupDocs = this.groupMap.get(group.groupValue);
            final TopDocs topDocs = groupDocs.collector.topDocs(withinGroupOffset, this.maxDocsPerGroup);
            groupDocsResult[groupIDX++] = new GroupDocs<GROUP_VALUE_TYPE>(Float.NaN, topDocs.getMaxScore(), topDocs.totalHits, topDocs.scoreDocs, groupDocs.groupValue, group.sortValues);
            maxScore = Math.max(maxScore, topDocs.getMaxScore());
        }
        return new TopGroups<GROUP_VALUE_TYPE>(this.groupSort.getSort(), this.withinGroupSort.getSort(), this.totalHitCount, this.totalGroupedHitCount, groupDocsResult, maxScore);
    }
    
    public class SearchGroupDocs<GROUP_VALUE_TYPE>
    {
        public final GROUP_VALUE_TYPE groupValue;
        public final TopDocsCollector<?> collector;
        public LeafCollector leafCollector;
        
        public SearchGroupDocs(final GROUP_VALUE_TYPE groupValue, final TopDocsCollector<?> collector) {
            this.groupValue = groupValue;
            this.collector = collector;
        }
    }
}
