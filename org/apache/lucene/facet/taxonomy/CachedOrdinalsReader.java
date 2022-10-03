package org.apache.lucene.facet.taxonomy;

import java.util.Collections;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Accountables;
import java.util.Collection;
import java.util.Iterator;
import org.apache.lucene.util.IntsRef;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import java.util.WeakHashMap;
import java.util.Map;
import org.apache.lucene.util.Accountable;

public class CachedOrdinalsReader extends OrdinalsReader implements Accountable
{
    private final OrdinalsReader source;
    private final Map<Object, CachedOrds> ordsCache;
    
    public CachedOrdinalsReader(final OrdinalsReader source) {
        this.ordsCache = new WeakHashMap<Object, CachedOrds>();
        this.source = source;
    }
    
    private synchronized CachedOrds getCachedOrds(final LeafReaderContext context) throws IOException {
        final Object cacheKey = context.reader().getCoreCacheKey();
        CachedOrds ords = this.ordsCache.get(cacheKey);
        if (ords == null) {
            ords = new CachedOrds(this.source.getReader(context), context.reader().maxDoc());
            this.ordsCache.put(cacheKey, ords);
        }
        return ords;
    }
    
    @Override
    public String getIndexFieldName() {
        return this.source.getIndexFieldName();
    }
    
    @Override
    public OrdinalsSegmentReader getReader(final LeafReaderContext context) throws IOException {
        final CachedOrds cachedOrds = this.getCachedOrds(context);
        return new OrdinalsSegmentReader() {
            @Override
            public void get(final int docID, final IntsRef ordinals) {
                ordinals.ints = cachedOrds.ordinals;
                ordinals.offset = cachedOrds.offsets[docID];
                ordinals.length = cachedOrds.offsets[docID + 1] - ordinals.offset;
            }
        };
    }
    
    public synchronized long ramBytesUsed() {
        long bytes = 0L;
        for (final CachedOrds ords : this.ordsCache.values()) {
            bytes += ords.ramBytesUsed();
        }
        return bytes;
    }
    
    public synchronized Collection<Accountable> getChildResources() {
        return Accountables.namedAccountables("segment", (Map)this.ordsCache);
    }
    
    public static final class CachedOrds implements Accountable
    {
        public final int[] offsets;
        public final int[] ordinals;
        
        public CachedOrds(final OrdinalsSegmentReader source, final int maxDoc) throws IOException {
            this.offsets = new int[maxDoc + 1];
            int[] ords = new int[maxDoc];
            long totOrds = 0L;
            final IntsRef values = new IntsRef(32);
            for (int docID = 0; docID < maxDoc; ++docID) {
                this.offsets[docID] = (int)totOrds;
                source.get(docID, values);
                final long nextLength = totOrds + values.length;
                if (nextLength > ords.length) {
                    if (nextLength > ArrayUtil.MAX_ARRAY_LENGTH) {
                        throw new IllegalStateException("too many ordinals (>= " + nextLength + ") to cache");
                    }
                    ords = ArrayUtil.grow(ords, (int)nextLength);
                }
                System.arraycopy(values.ints, 0, ords, (int)totOrds, values.length);
                totOrds = nextLength;
            }
            this.offsets[maxDoc] = (int)totOrds;
            if (totOrds / (double)ords.length < 0.9) {
                System.arraycopy(ords, 0, this.ordinals = new int[(int)totOrds], 0, (int)totOrds);
            }
            else {
                this.ordinals = ords;
            }
        }
        
        public long ramBytesUsed() {
            long mem = RamUsageEstimator.shallowSizeOf((Object)this) + RamUsageEstimator.sizeOf(this.offsets);
            if (this.offsets != this.ordinals) {
                mem += RamUsageEstimator.sizeOf(this.ordinals);
            }
            return mem;
        }
        
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
    }
}
