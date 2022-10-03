package org.apache.lucene.search.suggest;

import org.apache.lucene.util.PriorityQueue;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.util.BytesRef;
import java.util.Set;
import java.util.List;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.OutputStreamDataOutput;
import java.io.OutputStream;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.InputStreamDataInput;
import java.io.InputStream;
import java.io.IOException;
import org.apache.lucene.search.spell.Dictionary;
import java.util.Comparator;
import org.apache.lucene.util.Accountable;

public abstract class Lookup implements Accountable
{
    public static final Comparator<CharSequence> CHARSEQUENCE_COMPARATOR;
    
    public void build(final Dictionary dict) throws IOException {
        this.build(dict.getEntryIterator());
    }
    
    public boolean load(final InputStream input) throws IOException {
        final DataInput dataIn = (DataInput)new InputStreamDataInput(input);
        try {
            return this.load(dataIn);
        }
        finally {
            IOUtils.close(new Closeable[] { input });
        }
    }
    
    public boolean store(final OutputStream output) throws IOException {
        final DataOutput dataOut = (DataOutput)new OutputStreamDataOutput(output);
        try {
            return this.store(dataOut);
        }
        finally {
            IOUtils.close(new Closeable[] { output });
        }
    }
    
    public abstract long getCount() throws IOException;
    
    public abstract void build(final InputIterator p0) throws IOException;
    
    public List<LookupResult> lookup(final CharSequence key, final boolean onlyMorePopular, final int num) throws IOException {
        return this.lookup(key, null, onlyMorePopular, num);
    }
    
    public abstract List<LookupResult> lookup(final CharSequence p0, final Set<BytesRef> p1, final boolean p2, final int p3) throws IOException;
    
    public List<LookupResult> lookup(final CharSequence key, final BooleanQuery contextFilerQuery, final int num, final boolean allTermsRequired, final boolean doHighlight) throws IOException {
        return null;
    }
    
    public abstract boolean store(final DataOutput p0) throws IOException;
    
    public abstract boolean load(final DataInput p0) throws IOException;
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    static {
        CHARSEQUENCE_COMPARATOR = new CharSequenceComparator();
    }
    
    public static final class LookupResult implements Comparable<LookupResult>
    {
        public final CharSequence key;
        public final Object highlightKey;
        public final long value;
        public final BytesRef payload;
        public final Set<BytesRef> contexts;
        
        public LookupResult(final CharSequence key, final long value) {
            this(key, null, value, null, null);
        }
        
        public LookupResult(final CharSequence key, final long value, final BytesRef payload) {
            this(key, null, value, payload, null);
        }
        
        public LookupResult(final CharSequence key, final Object highlightKey, final long value, final BytesRef payload) {
            this(key, highlightKey, value, payload, null);
        }
        
        public LookupResult(final CharSequence key, final long value, final BytesRef payload, final Set<BytesRef> contexts) {
            this(key, null, value, payload, contexts);
        }
        
        public LookupResult(final CharSequence key, final long value, final Set<BytesRef> contexts) {
            this(key, null, value, null, contexts);
        }
        
        public LookupResult(final CharSequence key, final Object highlightKey, final long value, final BytesRef payload, final Set<BytesRef> contexts) {
            this.key = key;
            this.highlightKey = highlightKey;
            this.value = value;
            this.payload = payload;
            this.contexts = contexts;
        }
        
        @Override
        public String toString() {
            return (Object)this.key + "/" + this.value;
        }
        
        @Override
        public int compareTo(final LookupResult o) {
            return Lookup.CHARSEQUENCE_COMPARATOR.compare(this.key, o.key);
        }
    }
    
    private static class CharSequenceComparator implements Comparator<CharSequence>
    {
        @Override
        public int compare(final CharSequence o1, final CharSequence o2) {
            final int l1 = o1.length();
            final int l2 = o2.length();
            for (int aStop = Math.min(l1, l2), i = 0; i < aStop; ++i) {
                final int diff = o1.charAt(i) - o2.charAt(i);
                if (diff != 0) {
                    return diff;
                }
            }
            return l1 - l2;
        }
    }
    
    public static final class LookupPriorityQueue extends PriorityQueue<LookupResult>
    {
        public LookupPriorityQueue(final int size) {
            super(size);
        }
        
        protected boolean lessThan(final LookupResult a, final LookupResult b) {
            return a.value < b.value;
        }
        
        public LookupResult[] getResults() {
            final int size = this.size();
            final LookupResult[] res = new LookupResult[size];
            for (int i = size - 1; i >= 0; --i) {
                res[i] = (LookupResult)this.pop();
            }
            return res;
        }
    }
}
