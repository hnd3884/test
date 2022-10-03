package org.apache.lucene.search.grouping;

import org.apache.lucene.search.Weight;
import java.util.List;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.grouping.term.TermSecondPassGroupingCollector;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.search.grouping.function.FunctionSecondPassGroupingCollector;
import org.apache.lucene.search.SortField;
import java.util.Collections;
import org.apache.lucene.search.CachingCollector;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Collector;
import java.util.ArrayList;
import org.apache.lucene.search.grouping.term.TermAllGroupHeadsCollector;
import org.apache.lucene.search.grouping.term.TermAllGroupsCollector;
import org.apache.lucene.search.grouping.term.TermFirstPassGroupingCollector;
import org.apache.lucene.search.grouping.function.FunctionAllGroupHeadsCollector;
import org.apache.lucene.search.grouping.function.FunctionAllGroupsCollector;
import org.apache.lucene.search.grouping.function.FunctionFirstPassGroupingCollector;
import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.Bits;
import java.util.Collection;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.Query;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;

public class GroupingSearch
{
    private final String groupField;
    private final ValueSource groupFunction;
    private final Map<?, ?> valueSourceContext;
    private final Query groupEndDocs;
    private Sort groupSort;
    private Sort sortWithinGroup;
    private int groupDocsOffset;
    private int groupDocsLimit;
    private boolean fillSortFields;
    private boolean includeScores;
    private boolean includeMaxScore;
    private Double maxCacheRAMMB;
    private Integer maxDocsToCache;
    private boolean cacheScores;
    private boolean allGroups;
    private boolean allGroupHeads;
    private int initialSize;
    private Collection<?> matchingGroups;
    private Bits matchingGroupHeads;
    
    public GroupingSearch(final String groupField) {
        this(groupField, null, null, null);
    }
    
    public GroupingSearch(final ValueSource groupFunction, final Map<?, ?> valueSourceContext) {
        this(null, groupFunction, valueSourceContext, null);
    }
    
    public GroupingSearch(final Query groupEndDocs) {
        this(null, null, null, groupEndDocs);
    }
    
    private GroupingSearch(final String groupField, final ValueSource groupFunction, final Map<?, ?> valueSourceContext, final Query groupEndDocs) {
        this.groupSort = Sort.RELEVANCE;
        this.sortWithinGroup = Sort.RELEVANCE;
        this.groupDocsLimit = 1;
        this.includeScores = true;
        this.includeMaxScore = true;
        this.initialSize = 128;
        this.groupField = groupField;
        this.groupFunction = groupFunction;
        this.valueSourceContext = valueSourceContext;
        this.groupEndDocs = groupEndDocs;
    }
    
    public <T> TopGroups<T> search(final IndexSearcher searcher, final Query query, final int groupOffset, final int groupLimit) throws IOException {
        if (this.groupField != null || this.groupFunction != null) {
            return this.groupByFieldOrFunction(searcher, query, groupOffset, groupLimit);
        }
        if (this.groupEndDocs != null) {
            return (TopGroups<T>)this.groupByDocBlock(searcher, query, groupOffset, groupLimit);
        }
        throw new IllegalStateException("Either groupField, groupFunction or groupEndDocs must be set.");
    }
    
    protected TopGroups groupByFieldOrFunction(final IndexSearcher searcher, final Query query, final int groupOffset, final int groupLimit) throws IOException {
        final int topN = groupOffset + groupLimit;
        AbstractFirstPassGroupingCollector firstPassCollector;
        AbstractAllGroupsCollector allGroupsCollector;
        AbstractAllGroupHeadsCollector allGroupHeadsCollector;
        if (this.groupFunction != null) {
            firstPassCollector = new FunctionFirstPassGroupingCollector(this.groupFunction, this.valueSourceContext, this.groupSort, topN);
            if (this.allGroups) {
                allGroupsCollector = new FunctionAllGroupsCollector(this.groupFunction, this.valueSourceContext);
            }
            else {
                allGroupsCollector = null;
            }
            if (this.allGroupHeads) {
                allGroupHeadsCollector = new FunctionAllGroupHeadsCollector(this.groupFunction, this.valueSourceContext, this.sortWithinGroup);
            }
            else {
                allGroupHeadsCollector = null;
            }
        }
        else {
            firstPassCollector = new TermFirstPassGroupingCollector(this.groupField, this.groupSort, topN);
            if (this.allGroups) {
                allGroupsCollector = new TermAllGroupsCollector(this.groupField, this.initialSize);
            }
            else {
                allGroupsCollector = null;
            }
            if (this.allGroupHeads) {
                allGroupHeadsCollector = TermAllGroupHeadsCollector.create(this.groupField, this.sortWithinGroup, this.initialSize);
            }
            else {
                allGroupHeadsCollector = null;
            }
        }
        Collector firstRound;
        if (this.allGroupHeads || this.allGroups) {
            final List<Collector> collectors = new ArrayList<Collector>();
            collectors.add((Collector)firstPassCollector);
            if (this.allGroups) {
                collectors.add((Collector)allGroupsCollector);
            }
            if (this.allGroupHeads) {
                collectors.add((Collector)allGroupHeadsCollector);
            }
            firstRound = MultiCollector.wrap((Collector[])collectors.toArray(new Collector[collectors.size()]));
        }
        else {
            firstRound = (Collector)firstPassCollector;
        }
        CachingCollector cachedCollector = null;
        if (this.maxCacheRAMMB != null || this.maxDocsToCache != null) {
            if (this.maxCacheRAMMB != null) {
                cachedCollector = CachingCollector.create(firstRound, this.cacheScores, (double)this.maxCacheRAMMB);
            }
            else {
                cachedCollector = CachingCollector.create(firstRound, this.cacheScores, (int)this.maxDocsToCache);
            }
            searcher.search(query, (Collector)cachedCollector);
        }
        else {
            searcher.search(query, firstRound);
        }
        if (this.allGroups) {
            this.matchingGroups = allGroupsCollector.getGroups();
        }
        else {
            this.matchingGroups = Collections.emptyList();
        }
        if (this.allGroupHeads) {
            this.matchingGroupHeads = (Bits)allGroupHeadsCollector.retrieveGroupHeads(searcher.getIndexReader().maxDoc());
        }
        else {
            this.matchingGroupHeads = (Bits)new Bits.MatchNoBits(searcher.getIndexReader().maxDoc());
        }
        final Collection<SearchGroup> topSearchGroups = firstPassCollector.getTopGroups(groupOffset, this.fillSortFields);
        if (topSearchGroups == null) {
            return new TopGroups(new SortField[0], new SortField[0], 0, 0, new GroupDocs[0], Float.NaN);
        }
        final int topNInsideGroup = this.groupDocsOffset + this.groupDocsLimit;
        AbstractSecondPassGroupingCollector secondPassCollector;
        if (this.groupFunction != null) {
            secondPassCollector = new FunctionSecondPassGroupingCollector((Collection<SearchGroup<MutableValue>>)topSearchGroups, this.groupSort, this.sortWithinGroup, topNInsideGroup, this.includeScores, this.includeMaxScore, this.fillSortFields, this.groupFunction, this.valueSourceContext);
        }
        else {
            secondPassCollector = new TermSecondPassGroupingCollector(this.groupField, (Collection<SearchGroup<BytesRef>>)topSearchGroups, this.groupSort, this.sortWithinGroup, topNInsideGroup, this.includeScores, this.includeMaxScore, this.fillSortFields);
        }
        if (cachedCollector != null && cachedCollector.isCached()) {
            cachedCollector.replay((Collector)secondPassCollector);
        }
        else {
            searcher.search(query, (Collector)secondPassCollector);
        }
        if (this.allGroups) {
            return new TopGroups(secondPassCollector.getTopGroups(this.groupDocsOffset), this.matchingGroups.size());
        }
        return secondPassCollector.getTopGroups(this.groupDocsOffset);
    }
    
    protected TopGroups<?> groupByDocBlock(final IndexSearcher searcher, final Query query, final int groupOffset, final int groupLimit) throws IOException {
        final int topN = groupOffset + groupLimit;
        final Weight groupEndDocs = searcher.createNormalizedWeight(this.groupEndDocs, false);
        final BlockGroupingCollector c = new BlockGroupingCollector(this.groupSort, topN, this.includeScores, groupEndDocs);
        searcher.search(query, (Collector)c);
        final int topNInsideGroup = this.groupDocsOffset + this.groupDocsLimit;
        return c.getTopGroups(this.sortWithinGroup, groupOffset, this.groupDocsOffset, topNInsideGroup, this.fillSortFields);
    }
    
    public GroupingSearch setCachingInMB(final double maxCacheRAMMB, final boolean cacheScores) {
        this.maxCacheRAMMB = maxCacheRAMMB;
        this.maxDocsToCache = null;
        this.cacheScores = cacheScores;
        return this;
    }
    
    public GroupingSearch setCaching(final int maxDocsToCache, final boolean cacheScores) {
        this.maxDocsToCache = maxDocsToCache;
        this.maxCacheRAMMB = null;
        this.cacheScores = cacheScores;
        return this;
    }
    
    public GroupingSearch disableCaching() {
        this.maxCacheRAMMB = null;
        this.maxDocsToCache = null;
        return this;
    }
    
    public GroupingSearch setGroupSort(final Sort groupSort) {
        this.groupSort = groupSort;
        return this;
    }
    
    public GroupingSearch setSortWithinGroup(final Sort sortWithinGroup) {
        this.sortWithinGroup = sortWithinGroup;
        return this;
    }
    
    public GroupingSearch setGroupDocsOffset(final int groupDocsOffset) {
        this.groupDocsOffset = groupDocsOffset;
        return this;
    }
    
    public GroupingSearch setGroupDocsLimit(final int groupDocsLimit) {
        this.groupDocsLimit = groupDocsLimit;
        return this;
    }
    
    public GroupingSearch setFillSortFields(final boolean fillSortFields) {
        this.fillSortFields = fillSortFields;
        return this;
    }
    
    public GroupingSearch setIncludeScores(final boolean includeScores) {
        this.includeScores = includeScores;
        return this;
    }
    
    public GroupingSearch setIncludeMaxScore(final boolean includeMaxScore) {
        this.includeMaxScore = includeMaxScore;
        return this;
    }
    
    public GroupingSearch setAllGroups(final boolean allGroups) {
        this.allGroups = allGroups;
        return this;
    }
    
    public <T> Collection<T> getAllMatchingGroups() {
        return (Collection<T>)this.matchingGroups;
    }
    
    public GroupingSearch setAllGroupHeads(final boolean allGroupHeads) {
        this.allGroupHeads = allGroupHeads;
        return this;
    }
    
    public Bits getAllGroupHeads() {
        return this.matchingGroupHeads;
    }
    
    public GroupingSearch setInitialSize(final int initialSize) {
        this.initialSize = initialSize;
        return this;
    }
}
