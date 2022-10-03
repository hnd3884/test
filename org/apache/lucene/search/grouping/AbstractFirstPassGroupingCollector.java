package org.apache.lucene.search.grouping;

import org.apache.lucene.index.LeafReaderContext;
import java.util.Comparator;
import org.apache.lucene.search.Scorer;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.io.IOException;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.Sort;
import java.util.TreeSet;
import java.util.HashMap;
import org.apache.lucene.search.LeafFieldComparator;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.SimpleCollector;

public abstract class AbstractFirstPassGroupingCollector<GROUP_VALUE_TYPE> extends SimpleCollector
{
    private final FieldComparator<?>[] comparators;
    private final LeafFieldComparator[] leafComparators;
    private final int[] reversed;
    private final int topNGroups;
    private final boolean needsScores;
    private final HashMap<GROUP_VALUE_TYPE, CollectedSearchGroup<GROUP_VALUE_TYPE>> groupMap;
    private final int compIDXEnd;
    protected TreeSet<CollectedSearchGroup<GROUP_VALUE_TYPE>> orderedGroups;
    private int docBase;
    private int spareSlot;
    
    public AbstractFirstPassGroupingCollector(final Sort groupSort, final int topNGroups) throws IOException {
        if (topNGroups < 1) {
            throw new IllegalArgumentException("topNGroups must be >= 1 (got " + topNGroups + ")");
        }
        this.topNGroups = topNGroups;
        this.needsScores = groupSort.needsScores();
        final SortField[] sortFields = groupSort.getSort();
        this.comparators = (FieldComparator<?>[])new FieldComparator[sortFields.length];
        this.leafComparators = new LeafFieldComparator[sortFields.length];
        this.compIDXEnd = this.comparators.length - 1;
        this.reversed = new int[sortFields.length];
        for (int i = 0; i < sortFields.length; ++i) {
            final SortField sortField = sortFields[i];
            this.comparators[i] = (FieldComparator<?>)sortField.getComparator(topNGroups + 1, i);
            this.reversed[i] = (sortField.getReverse() ? -1 : 1);
        }
        this.spareSlot = topNGroups;
        this.groupMap = new HashMap<GROUP_VALUE_TYPE, CollectedSearchGroup<GROUP_VALUE_TYPE>>(topNGroups);
    }
    
    public boolean needsScores() {
        return this.needsScores;
    }
    
    public Collection<SearchGroup<GROUP_VALUE_TYPE>> getTopGroups(final int groupOffset, final boolean fillFields) {
        if (groupOffset < 0) {
            throw new IllegalArgumentException("groupOffset must be >= 0 (got " + groupOffset + ")");
        }
        if (this.groupMap.size() <= groupOffset) {
            return null;
        }
        if (this.orderedGroups == null) {
            this.buildSortedSet();
        }
        final Collection<SearchGroup<GROUP_VALUE_TYPE>> result = new ArrayList<SearchGroup<GROUP_VALUE_TYPE>>();
        int upto = 0;
        final int sortFieldCount = this.comparators.length;
        for (final CollectedSearchGroup<GROUP_VALUE_TYPE> group : this.orderedGroups) {
            if (upto++ < groupOffset) {
                continue;
            }
            final SearchGroup<GROUP_VALUE_TYPE> searchGroup = new SearchGroup<GROUP_VALUE_TYPE>();
            searchGroup.groupValue = group.groupValue;
            if (fillFields) {
                searchGroup.sortValues = new Object[sortFieldCount];
                for (int sortFieldIDX = 0; sortFieldIDX < sortFieldCount; ++sortFieldIDX) {
                    searchGroup.sortValues[sortFieldIDX] = this.comparators[sortFieldIDX].value(group.comparatorSlot);
                }
            }
            result.add(searchGroup);
        }
        return result;
    }
    
    public void setScorer(final Scorer scorer) throws IOException {
        for (final LeafFieldComparator comparator : this.leafComparators) {
            comparator.setScorer(scorer);
        }
    }
    
    public void collect(final int doc) throws IOException {
        if (this.orderedGroups != null) {
            int compIDX = 0;
            while (true) {
                final int c = this.reversed[compIDX] * this.leafComparators[compIDX].compareBottom(doc);
                if (c < 0) {
                    return;
                }
                if (c > 0) {
                    break;
                }
                if (compIDX == this.compIDXEnd) {
                    return;
                }
                ++compIDX;
            }
        }
        final GROUP_VALUE_TYPE groupValue = this.getDocGroupValue(doc);
        final CollectedSearchGroup<GROUP_VALUE_TYPE> group = this.groupMap.get(groupValue);
        if (group == null) {
            if (this.groupMap.size() < this.topNGroups) {
                final CollectedSearchGroup<GROUP_VALUE_TYPE> sg = new CollectedSearchGroup<GROUP_VALUE_TYPE>();
                sg.groupValue = (GROUP_VALUE_TYPE)this.copyDocGroupValue((GROUP_VALUE_TYPE)groupValue, (GROUP_VALUE_TYPE)null);
                sg.comparatorSlot = this.groupMap.size();
                sg.topDoc = this.docBase + doc;
                for (final LeafFieldComparator fc : this.leafComparators) {
                    fc.copy(sg.comparatorSlot, doc);
                }
                this.groupMap.put(sg.groupValue, sg);
                if (this.groupMap.size() == this.topNGroups) {
                    this.buildSortedSet();
                }
                return;
            }
            final CollectedSearchGroup<GROUP_VALUE_TYPE> bottomGroup = this.orderedGroups.pollLast();
            assert this.orderedGroups.size() == this.topNGroups - 1;
            this.groupMap.remove(bottomGroup.groupValue);
            bottomGroup.groupValue = (GROUP_VALUE_TYPE)this.copyDocGroupValue((GROUP_VALUE_TYPE)groupValue, (GROUP_VALUE_TYPE)bottomGroup.groupValue);
            bottomGroup.topDoc = this.docBase + doc;
            for (final LeafFieldComparator fc : this.leafComparators) {
                fc.copy(bottomGroup.comparatorSlot, doc);
            }
            this.groupMap.put(bottomGroup.groupValue, bottomGroup);
            this.orderedGroups.add(bottomGroup);
            assert this.orderedGroups.size() == this.topNGroups;
            final int lastComparatorSlot = this.orderedGroups.last().comparatorSlot;
            for (final LeafFieldComparator fc2 : this.leafComparators) {
                fc2.setBottom(lastComparatorSlot);
            }
        }
        else {
            int compIDX2 = 0;
            while (true) {
                this.leafComparators[compIDX2].copy(this.spareSlot, doc);
                final int c2 = this.reversed[compIDX2] * this.comparators[compIDX2].compare(group.comparatorSlot, this.spareSlot);
                if (c2 < 0) {
                    return;
                }
                if (c2 > 0) {
                    for (int compIDX3 = compIDX2 + 1; compIDX3 < this.comparators.length; ++compIDX3) {
                        this.leafComparators[compIDX3].copy(this.spareSlot, doc);
                    }
                    CollectedSearchGroup<GROUP_VALUE_TYPE> prevLast;
                    if (this.orderedGroups != null) {
                        prevLast = this.orderedGroups.last();
                        this.orderedGroups.remove(group);
                        assert this.orderedGroups.size() == this.topNGroups - 1;
                    }
                    else {
                        prevLast = null;
                    }
                    group.topDoc = this.docBase + doc;
                    final int tmp = this.spareSlot;
                    this.spareSlot = group.comparatorSlot;
                    group.comparatorSlot = tmp;
                    if (this.orderedGroups != null) {
                        this.orderedGroups.add(group);
                        assert this.orderedGroups.size() == this.topNGroups;
                        final CollectedSearchGroup<?> newLast = this.orderedGroups.last();
                        if (group == newLast || prevLast != newLast) {
                            for (final LeafFieldComparator fc3 : this.leafComparators) {
                                fc3.setBottom(newLast.comparatorSlot);
                            }
                        }
                    }
                    return;
                }
                if (compIDX2 == this.compIDXEnd) {
                    return;
                }
                ++compIDX2;
            }
        }
    }
    
    private void buildSortedSet() {
        final Comparator<CollectedSearchGroup<?>> comparator = new Comparator<CollectedSearchGroup<?>>() {
            @Override
            public int compare(final CollectedSearchGroup<?> o1, final CollectedSearchGroup<?> o2) {
                int compIDX = 0;
                while (true) {
                    final FieldComparator<?> fc = AbstractFirstPassGroupingCollector.this.comparators[compIDX];
                    final int c = AbstractFirstPassGroupingCollector.this.reversed[compIDX] * fc.compare(o1.comparatorSlot, o2.comparatorSlot);
                    if (c != 0) {
                        return c;
                    }
                    if (compIDX == AbstractFirstPassGroupingCollector.this.compIDXEnd) {
                        return o1.topDoc - o2.topDoc;
                    }
                    ++compIDX;
                }
            }
        };
        (this.orderedGroups = new TreeSet<CollectedSearchGroup<GROUP_VALUE_TYPE>>(comparator)).addAll(this.groupMap.values());
        assert this.orderedGroups.size() > 0;
        for (final LeafFieldComparator fc : this.leafComparators) {
            fc.setBottom(this.orderedGroups.last().comparatorSlot);
        }
    }
    
    protected void doSetNextReader(final LeafReaderContext readerContext) throws IOException {
        this.docBase = readerContext.docBase;
        for (int i = 0; i < this.comparators.length; ++i) {
            this.leafComparators[i] = this.comparators[i].getLeafComparator(readerContext);
        }
    }
    
    protected abstract GROUP_VALUE_TYPE getDocGroupValue(final int p0);
    
    protected abstract GROUP_VALUE_TYPE copyDocGroupValue(final GROUP_VALUE_TYPE p0, final GROUP_VALUE_TYPE p1);
}
