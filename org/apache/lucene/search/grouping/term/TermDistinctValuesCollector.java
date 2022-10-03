package org.apache.lucene.search.grouping.term;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.grouping.SearchGroup;
import java.util.Collection;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.util.SentinelIntSet;
import java.util.List;
import org.apache.lucene.search.grouping.AbstractDistinctValuesCollector;

public class TermDistinctValuesCollector extends AbstractDistinctValuesCollector<GroupCount>
{
    private final String groupField;
    private final String countField;
    private final List<GroupCount> groups;
    private final SentinelIntSet ordSet;
    private final GroupCount[] groupCounts;
    private SortedDocValues groupFieldTermIndex;
    private SortedDocValues countFieldTermIndex;
    
    public TermDistinctValuesCollector(final String groupField, final String countField, final Collection<SearchGroup<BytesRef>> groups) {
        this.groupField = groupField;
        this.countField = countField;
        this.groups = new ArrayList<GroupCount>(groups.size());
        for (final SearchGroup<BytesRef> group : groups) {
            this.groups.add(new GroupCount(group.groupValue));
        }
        this.ordSet = new SentinelIntSet(groups.size(), -2);
        this.groupCounts = new GroupCount[this.ordSet.keys.length];
    }
    
    public void collect(final int doc) throws IOException {
        final int slot = this.ordSet.find(this.groupFieldTermIndex.getOrd(doc));
        if (slot < 0) {
            return;
        }
        final GroupCount gc = this.groupCounts[slot];
        final int countOrd = this.countFieldTermIndex.getOrd(doc);
        if (this.doesNotContainOrd(countOrd, gc.ords)) {
            if (countOrd == -1) {
                gc.uniqueValues.add(null);
            }
            else {
                final BytesRef term = BytesRef.deepCopyOf(this.countFieldTermIndex.lookupOrd(countOrd));
                gc.uniqueValues.add((GROUP_VALUE_TYPE)term);
            }
            (gc.ords = Arrays.copyOf(gc.ords, gc.ords.length + 1))[gc.ords.length - 1] = countOrd;
            if (gc.ords.length > 1) {
                Arrays.sort(gc.ords);
            }
        }
    }
    
    private boolean doesNotContainOrd(final int ord, final int[] ords) {
        if (ords.length == 0) {
            return true;
        }
        if (ords.length == 1) {
            return ord != ords[0];
        }
        return Arrays.binarySearch(ords, ord) < 0;
    }
    
    @Override
    public List<GroupCount> getGroups() {
        return this.groups;
    }
    
    protected void doSetNextReader(final LeafReaderContext context) throws IOException {
        this.groupFieldTermIndex = DocValues.getSorted(context.reader(), this.groupField);
        this.countFieldTermIndex = DocValues.getSorted(context.reader(), this.countField);
        this.ordSet.clear();
        for (final GroupCount group : this.groups) {
            final int groupOrd = (group.groupValue == null) ? -1 : this.groupFieldTermIndex.lookupTerm((BytesRef)group.groupValue);
            if (group.groupValue != null && groupOrd < 0) {
                continue;
            }
            this.groupCounts[this.ordSet.put(groupOrd)] = group;
            Arrays.fill(group.ords = new int[group.uniqueValues.size()], -2);
            int i = 0;
            for (final BytesRef value : group.uniqueValues) {
                final int countOrd = (value == null) ? -1 : this.countFieldTermIndex.lookupTerm(value);
                if (value == null || countOrd >= 0) {
                    group.ords[i++] = countOrd;
                }
            }
        }
    }
    
    public static class GroupCount extends AbstractDistinctValuesCollector.GroupCount<BytesRef>
    {
        int[] ords;
        
        GroupCount(final BytesRef groupValue) {
            super(groupValue);
        }
    }
}
