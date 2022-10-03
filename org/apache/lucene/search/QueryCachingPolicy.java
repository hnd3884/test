package org.apache.lucene.search;

import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

public interface QueryCachingPolicy
{
    public static final QueryCachingPolicy ALWAYS_CACHE = new QueryCachingPolicy() {
        @Override
        public void onUse(final Query query) {
        }
        
        @Override
        public boolean shouldCache(final Query query, final LeafReaderContext context) throws IOException {
            return true;
        }
    };
    
    void onUse(final Query p0);
    
    boolean shouldCache(final Query p0, final LeafReaderContext p1) throws IOException;
    
    public static class CacheOnLargeSegments implements QueryCachingPolicy
    {
        public static final CacheOnLargeSegments DEFAULT;
        private final int minIndexSize;
        private final float minSizeRatio;
        
        public CacheOnLargeSegments(final int minIndexSize, final float minSizeRatio) {
            if (minSizeRatio <= 0.0f || minSizeRatio >= 1.0f) {
                throw new IllegalArgumentException("minSizeRatio must be in ]0, 1[, got " + minSizeRatio);
            }
            this.minIndexSize = minIndexSize;
            this.minSizeRatio = minSizeRatio;
        }
        
        @Override
        public void onUse(final Query query) {
        }
        
        @Override
        public boolean shouldCache(final Query query, final LeafReaderContext context) throws IOException {
            final IndexReaderContext topLevelContext = ReaderUtil.getTopLevelContext(context);
            if (topLevelContext.reader().maxDoc() < this.minIndexSize) {
                return false;
            }
            final float sizeRatio = context.reader().maxDoc() / (float)topLevelContext.reader().maxDoc();
            return sizeRatio >= this.minSizeRatio;
        }
        
        static {
            DEFAULT = new CacheOnLargeSegments(10000, 0.03f);
        }
    }
}
