package org.apache.lucene.index;

import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.util.BytesRef;
import java.util.Iterator;
import java.util.List;

class MergedPrefixCodedTermsIterator extends FieldTermIterator
{
    final TermMergeQueue termQueue;
    final FieldMergeQueue fieldQueue;
    String field;
    
    public MergedPrefixCodedTermsIterator(final List<PrefixCodedTerms> termsList) {
        this.fieldQueue = new FieldMergeQueue(termsList.size());
        for (final PrefixCodedTerms terms : termsList) {
            final PrefixCodedTerms.TermIterator iter = terms.iterator();
            iter.next();
            if (iter.field != null) {
                this.fieldQueue.add(iter);
            }
        }
        this.termQueue = new TermMergeQueue(termsList.size());
    }
    
    @Override
    public BytesRef next() {
        if (this.termQueue.size() == 0) {
            if (this.fieldQueue.size() == 0) {
                this.field = null;
                return null;
            }
            final PrefixCodedTerms.TermIterator top = this.fieldQueue.pop();
            this.termQueue.add(top);
            this.field = top.field;
            assert this.field != null;
            while (this.fieldQueue.size() != 0 && this.fieldQueue.top().field.equals(top.field)) {
                final PrefixCodedTerms.TermIterator iter = this.fieldQueue.pop();
                assert iter.field.equals(this.field);
                iter.field = this.field;
                this.termQueue.add(iter);
            }
            return this.termQueue.top().bytes;
        }
        else {
            final PrefixCodedTerms.TermIterator top = this.termQueue.top();
            if (top.next() == null) {
                this.termQueue.pop();
            }
            else if (top.field() != this.field) {
                this.termQueue.pop();
                this.fieldQueue.add(top);
            }
            else {
                this.termQueue.updateTop();
            }
            if (this.termQueue.size() == 0) {
                return this.next();
            }
            return this.termQueue.top().bytes;
        }
    }
    
    public String field() {
        return this.field;
    }
    
    public long delGen() {
        return this.termQueue.top().delGen();
    }
    
    private static class TermMergeQueue extends PriorityQueue<PrefixCodedTerms.TermIterator>
    {
        TermMergeQueue(final int size) {
            super(size);
        }
        
        @Override
        protected boolean lessThan(final PrefixCodedTerms.TermIterator a, final PrefixCodedTerms.TermIterator b) {
            final int cmp = a.bytes.compareTo(b.bytes);
            return cmp < 0 || (cmp <= 0 && a.delGen() > b.delGen());
        }
    }
    
    private static class FieldMergeQueue extends PriorityQueue<PrefixCodedTerms.TermIterator>
    {
        FieldMergeQueue(final int size) {
            super(size);
        }
        
        @Override
        protected boolean lessThan(final PrefixCodedTerms.TermIterator a, final PrefixCodedTerms.TermIterator b) {
            return a.field.compareTo(b.field) < 0;
        }
    }
}
