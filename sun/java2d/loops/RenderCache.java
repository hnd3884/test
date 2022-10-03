package sun.java2d.loops;

public final class RenderCache
{
    private Entry[] entries;
    
    public RenderCache(final int n) {
        this.entries = new Entry[n];
    }
    
    public synchronized Object get(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        int i;
        for (int n = i = this.entries.length - 1; i >= 0; --i) {
            final Entry entry = this.entries[i];
            if (entry == null) {
                break;
            }
            if (entry.matches(surfaceType, compositeType, surfaceType2)) {
                if (i < n - 4) {
                    System.arraycopy(this.entries, i + 1, this.entries, i, n - i);
                    this.entries[n] = entry;
                }
                return entry.getValue();
            }
        }
        return null;
    }
    
    public synchronized void put(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2, final Object o) {
        final Entry entry = new Entry(surfaceType, compositeType, surfaceType2, o);
        final int length = this.entries.length;
        System.arraycopy(this.entries, 1, this.entries, 0, length - 1);
        this.entries[length - 1] = entry;
    }
    
    final class Entry
    {
        private SurfaceType src;
        private CompositeType comp;
        private SurfaceType dst;
        private Object value;
        
        public Entry(final SurfaceType src, final CompositeType comp, final SurfaceType dst, final Object value) {
            this.src = src;
            this.comp = comp;
            this.dst = dst;
            this.value = value;
        }
        
        public boolean matches(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
            return this.src == surfaceType && this.comp == compositeType && this.dst == surfaceType2;
        }
        
        public Object getValue() {
            return this.value;
        }
    }
}
