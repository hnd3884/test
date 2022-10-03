package org.apache.lucene.search.grouping.term;

import java.util.Iterator;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Collection;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.index.SortedDocValues;
import java.util.List;
import org.apache.lucene.util.SentinelIntSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.grouping.AbstractAllGroupsCollector;

public class TermAllGroupsCollector extends AbstractAllGroupsCollector<BytesRef>
{
    private static final int DEFAULT_INITIAL_SIZE = 128;
    private final String groupField;
    private final SentinelIntSet ordSet;
    private final List<BytesRef> groups;
    private SortedDocValues index;
    
    public TermAllGroupsCollector(final String groupField, final int initialSize) {
        this.ordSet = new SentinelIntSet(initialSize, -2);
        this.groups = new ArrayList<BytesRef>(initialSize);
        this.groupField = groupField;
    }
    
    public TermAllGroupsCollector(final String groupField) {
        this(groupField, 128);
    }
    
    public void collect(final int doc) throws IOException {
        final int key = this.index.getOrd(doc);
        if (!this.ordSet.exists(key)) {
            this.ordSet.put(key);
            BytesRef term;
            if (key == -1) {
                term = null;
            }
            else {
                term = BytesRef.deepCopyOf(this.index.lookupOrd(key));
            }
            this.groups.add(term);
        }
    }
    
    @Override
    public Collection<BytesRef> getGroups() {
        return this.groups;
    }
    
    protected void doSetNextReader(final LeafReaderContext context) throws IOException {
        this.index = DocValues.getSorted(context.reader(), this.groupField);
        this.ordSet.clear();
        for (final BytesRef countedGroup : this.groups) {
            if (countedGroup == null) {
                this.ordSet.put(-1);
            }
            else {
                final int ord = this.index.lookupTerm(countedGroup);
                if (ord < 0) {
                    continue;
                }
                this.ordSet.put(ord);
            }
        }
    }
}
