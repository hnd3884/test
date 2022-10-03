package javax.swing.text;

import java.util.ArrayList;
import java.util.List;

class SegmentCache
{
    private static SegmentCache sharedCache;
    private List<Segment> segments;
    
    public static SegmentCache getSharedInstance() {
        return SegmentCache.sharedCache;
    }
    
    public static Segment getSharedSegment() {
        return getSharedInstance().getSegment();
    }
    
    public static void releaseSharedSegment(final Segment segment) {
        getSharedInstance().releaseSegment(segment);
    }
    
    public SegmentCache() {
        this.segments = new ArrayList<Segment>(11);
    }
    
    public Segment getSegment() {
        synchronized (this) {
            final int size = this.segments.size();
            if (size > 0) {
                return this.segments.remove(size - 1);
            }
        }
        return new CachedSegment();
    }
    
    public void releaseSegment(final Segment segment) {
        if (segment instanceof CachedSegment) {
            synchronized (this) {
                segment.array = null;
                segment.count = 0;
                this.segments.add(segment);
            }
        }
    }
    
    static {
        SegmentCache.sharedCache = new SegmentCache();
    }
    
    private static class CachedSegment extends Segment
    {
    }
}
