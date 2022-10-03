package org.apache.lucene.search;

import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

@Deprecated
public interface FilterCachingPolicy
{
    public static final FilterCachingPolicy ALWAYS_CACHE = new FilterCachingPolicy() {
        @Override
        public void onUse(final Filter filter) {
        }
        
        @Override
        public boolean shouldCache(final Filter filter, final LeafReaderContext context, final DocIdSet set) throws IOException {
            return true;
        }
    };
    
    void onUse(final Filter p0);
    
    boolean shouldCache(final Filter p0, final LeafReaderContext p1, final DocIdSet p2) throws IOException;
    
    public static class CacheOnLargeSegments implements FilterCachingPolicy
    {
        public static final CacheOnLargeSegments DEFAULT;
        private final float minSizeRatio;
        
        public CacheOnLargeSegments(final float minSizeRatio) {
            if (minSizeRatio <= 0.0f || minSizeRatio >= 1.0f) {
                throw new IllegalArgumentException("minSizeRatio must be in ]0, 1[, got " + minSizeRatio);
            }
            this.minSizeRatio = minSizeRatio;
        }
        
        @Override
        public void onUse(final Filter filter) {
        }
        
        @Override
        public boolean shouldCache(final Filter filter, final LeafReaderContext context, final DocIdSet set) throws IOException {
            final IndexReaderContext topLevelContext = ReaderUtil.getTopLevelContext(context);
            final float sizeRatio = context.reader().maxDoc() / (float)topLevelContext.reader().maxDoc();
            return sizeRatio >= this.minSizeRatio;
        }
        
        static {
            DEFAULT = new CacheOnLargeSegments(0.03f);
        }
    }
}
