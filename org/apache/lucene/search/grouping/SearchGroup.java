package org.apache.lucene.search.grouping;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map;
import java.util.NavigableSet;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.FieldComparator;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.search.Sort;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;

public class SearchGroup<GROUP_VALUE_TYPE>
{
    public GROUP_VALUE_TYPE groupValue;
    public Object[] sortValues;
    
    @Override
    public String toString() {
        return "SearchGroup(groupValue=" + this.groupValue + " sortValues=" + Arrays.toString(this.sortValues) + ")";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SearchGroup<?> that = (SearchGroup<?>)o;
        if (this.groupValue == null) {
            if (that.groupValue != null) {
                return false;
            }
        }
        else if (!this.groupValue.equals(that.groupValue)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return (this.groupValue != null) ? this.groupValue.hashCode() : 0;
    }
    
    public static <T> Collection<SearchGroup<T>> merge(final List<Collection<SearchGroup<T>>> topGroups, final int offset, final int topN, final Sort groupSort) throws IOException {
        if (topGroups.size() == 0) {
            return null;
        }
        return new GroupMerger<T>(groupSort).merge(topGroups, offset, topN);
    }
    
    private static class ShardIter<T>
    {
        public final Iterator<SearchGroup<T>> iter;
        public final int shardIndex;
        
        public ShardIter(final Collection<SearchGroup<T>> shard, final int shardIndex) {
            this.shardIndex = shardIndex;
            this.iter = shard.iterator();
            assert this.iter.hasNext();
        }
        
        public SearchGroup<T> next() {
            assert this.iter.hasNext();
            final SearchGroup<T> group = this.iter.next();
            if (group.sortValues == null) {
                throw new IllegalArgumentException("group.sortValues is null; you must pass fillFields=true to the first pass collector");
            }
            return group;
        }
        
        @Override
        public String toString() {
            return "ShardIter(shard=" + this.shardIndex + ")";
        }
    }
    
    private static class MergedGroup<T>
    {
        public final T groupValue;
        public Object[] topValues;
        public final List<ShardIter<T>> shards;
        public int minShardIndex;
        public boolean processed;
        public boolean inQueue;
        
        public MergedGroup(final T groupValue) {
            this.shards = new ArrayList<ShardIter<T>>();
            this.groupValue = groupValue;
        }
        
        private boolean neverEquals(final Object _other) {
            if (_other instanceof MergedGroup) {
                final MergedGroup<?> other = (MergedGroup<?>)_other;
                if (this.groupValue == null) {
                    assert other.groupValue != null;
                }
                else {
                    assert !this.groupValue.equals(other.groupValue);
                }
            }
            return true;
        }
        
        @Override
        public boolean equals(final Object _other) {
            assert this.neverEquals(_other);
            if (!(_other instanceof MergedGroup)) {
                return false;
            }
            final MergedGroup<?> other = (MergedGroup<?>)_other;
            if (this.groupValue == null) {
                return other == null;
            }
            return this.groupValue.equals(other);
        }
        
        @Override
        public int hashCode() {
            if (this.groupValue == null) {
                return 0;
            }
            return this.groupValue.hashCode();
        }
    }
    
    private static class GroupComparator<T> implements Comparator<MergedGroup<T>>
    {
        public final FieldComparator[] comparators;
        public final int[] reversed;
        
        public GroupComparator(final Sort groupSort) throws IOException {
            final SortField[] sortFields = groupSort.getSort();
            this.comparators = new FieldComparator[sortFields.length];
            this.reversed = new int[sortFields.length];
            for (int compIDX = 0; compIDX < sortFields.length; ++compIDX) {
                final SortField sortField = sortFields[compIDX];
                this.comparators[compIDX] = sortField.getComparator(1, compIDX);
                this.reversed[compIDX] = (sortField.getReverse() ? -1 : 1);
            }
        }
        
        @Override
        public int compare(final MergedGroup<T> group, final MergedGroup<T> other) {
            if (group == other) {
                return 0;
            }
            final Object[] groupValues = group.topValues;
            final Object[] otherValues = other.topValues;
            for (int compIDX = 0; compIDX < this.comparators.length; ++compIDX) {
                final int c = this.reversed[compIDX] * this.comparators[compIDX].compareValues(groupValues[compIDX], otherValues[compIDX]);
                if (c != 0) {
                    return c;
                }
            }
            assert group.minShardIndex != other.minShardIndex;
            return group.minShardIndex - other.minShardIndex;
        }
    }
    
    private static class GroupMerger<T>
    {
        private final GroupComparator<T> groupComp;
        private final NavigableSet<MergedGroup<T>> queue;
        private final Map<T, MergedGroup<T>> groupsSeen;
        
        public GroupMerger(final Sort groupSort) throws IOException {
            this.groupComp = new GroupComparator<T>(groupSort);
            this.queue = new TreeSet<MergedGroup<T>>(this.groupComp);
            this.groupsSeen = new HashMap<T, MergedGroup<T>>();
        }
        
        private void updateNextGroup(final int topN, final ShardIter<T> shard) {
            while (shard.iter.hasNext()) {
                final SearchGroup<T> group = shard.next();
                MergedGroup<T> mergedGroup = this.groupsSeen.get(group.groupValue);
                final boolean isNew = mergedGroup == null;
                if (isNew) {
                    mergedGroup = new MergedGroup<T>(group.groupValue);
                    mergedGroup.minShardIndex = shard.shardIndex;
                    assert group.sortValues != null;
                    mergedGroup.topValues = group.sortValues;
                    this.groupsSeen.put(group.groupValue, mergedGroup);
                    mergedGroup.inQueue = true;
                    this.queue.add(mergedGroup);
                }
                else {
                    if (mergedGroup.processed) {
                        continue;
                    }
                    boolean competes = false;
                    for (int compIDX = 0; compIDX < this.groupComp.comparators.length; ++compIDX) {
                        final int cmp = this.groupComp.reversed[compIDX] * this.groupComp.comparators[compIDX].compareValues(group.sortValues[compIDX], mergedGroup.topValues[compIDX]);
                        if (cmp < 0) {
                            competes = true;
                            break;
                        }
                        if (cmp > 0) {
                            break;
                        }
                        if (compIDX == this.groupComp.comparators.length - 1 && shard.shardIndex < mergedGroup.minShardIndex) {
                            competes = true;
                        }
                    }
                    if (competes) {
                        if (mergedGroup.inQueue) {
                            this.queue.remove(mergedGroup);
                        }
                        mergedGroup.topValues = group.sortValues;
                        mergedGroup.minShardIndex = shard.shardIndex;
                        this.queue.add(mergedGroup);
                        mergedGroup.inQueue = true;
                    }
                }
                mergedGroup.shards.add(shard);
                break;
            }
            while (this.queue.size() > topN) {
                final MergedGroup<T> group2 = this.queue.pollLast();
                group2.inQueue = false;
            }
        }
        
        public Collection<SearchGroup<T>> merge(final List<Collection<SearchGroup<T>>> shards, final int offset, final int topN) {
            final int maxQueueSize = offset + topN;
            for (int shardIDX = 0; shardIDX < shards.size(); ++shardIDX) {
                final Collection<SearchGroup<T>> shard = shards.get(shardIDX);
                if (!shard.isEmpty()) {
                    this.updateNextGroup(maxQueueSize, new ShardIter<T>(shard, shardIDX));
                }
            }
            final List<SearchGroup<T>> newTopGroups = new ArrayList<SearchGroup<T>>();
            int count = 0;
            while (this.queue.size() != 0) {
                final MergedGroup<T> group = this.queue.pollFirst();
                group.processed = true;
                if (count++ >= offset) {
                    final SearchGroup<T> newGroup = new SearchGroup<T>();
                    newGroup.groupValue = group.groupValue;
                    newGroup.sortValues = group.topValues;
                    newTopGroups.add(newGroup);
                    if (newTopGroups.size() == topN) {
                        break;
                    }
                }
                for (final ShardIter<T> shardIter : group.shards) {
                    this.updateNextGroup(maxQueueSize, shardIter);
                }
            }
            if (newTopGroups.size() == 0) {
                return null;
            }
            return newTopGroups;
        }
    }
}
