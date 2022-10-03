package org.apache.lucene.search.grouping.term;

import java.util.Iterator;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.grouping.SearchGroup;
import java.util.Collection;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.util.SentinelIntSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.grouping.AbstractSecondPassGroupingCollector;

public class TermSecondPassGroupingCollector extends AbstractSecondPassGroupingCollector<BytesRef>
{
    private final String groupField;
    private final SentinelIntSet ordSet;
    private SortedDocValues index;
    
    public TermSecondPassGroupingCollector(final String groupField, final Collection<SearchGroup<BytesRef>> groups, final Sort groupSort, final Sort withinGroupSort, final int maxDocsPerGroup, final boolean getScores, final boolean getMaxScores, final boolean fillSortFields) throws IOException {
        super(groups, groupSort, withinGroupSort, maxDocsPerGroup, getScores, getMaxScores, fillSortFields);
        this.groupField = groupField;
        this.ordSet = new SentinelIntSet(this.groupMap.size(), -2);
        super.groupDocs = new SearchGroupDocs[this.ordSet.keys.length];
    }
    
    @Override
    protected void doSetNextReader(final LeafReaderContext readerContext) throws IOException {
        super.doSetNextReader(readerContext);
        this.index = DocValues.getSorted(readerContext.reader(), this.groupField);
        this.ordSet.clear();
        for (final SearchGroupDocs<BytesRef> group : this.groupMap.values()) {
            final int ord = (group.groupValue == null) ? -1 : this.index.lookupTerm((BytesRef)group.groupValue);
            if (group.groupValue == null || ord >= 0) {
                this.groupDocs[this.ordSet.put(ord)] = (SearchGroupDocs<GROUP_VALUE_TYPE>)group;
            }
        }
    }
    
    @Override
    protected SearchGroupDocs<BytesRef> retrieveGroup(final int doc) throws IOException {
        final int slot = this.ordSet.find(this.index.getOrd(doc));
        if (slot >= 0) {
            return (SearchGroupDocs<BytesRef>)this.groupDocs[slot];
        }
        return null;
    }
}
