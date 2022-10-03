package sun.java2d;

import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import java.lang.ref.Reference;

public abstract class ReentrantContextProvider<K extends ReentrantContext>
{
    static final byte USAGE_TL_INACTIVE = 0;
    static final byte USAGE_TL_IN_USE = 1;
    static final byte USAGE_CLQ = 2;
    public static final int REF_HARD = 0;
    public static final int REF_SOFT = 1;
    public static final int REF_WEAK = 2;
    private final int refType;
    
    protected ReentrantContextProvider(final int refType) {
        this.refType = refType;
    }
    
    protected abstract K newContext();
    
    public abstract K acquire();
    
    public abstract void release(final K p0);
    
    protected final Reference<K> getOrCreateReference(final K k) {
        if (k.reference == null) {
            switch (this.refType) {
                case 0: {
                    k.reference = new HardReference<ReentrantContext>((ReentrantContext)k);
                    break;
                }
                case 1: {
                    k.reference = new SoftReference<ReentrantContext>((ReentrantContext)k);
                    break;
                }
                default: {
                    k.reference = new WeakReference<ReentrantContext>((ReentrantContext)k);
                    break;
                }
            }
        }
        return (Reference<K>)k.reference;
    }
    
    static final class HardReference<V> extends WeakReference<V>
    {
        private final V strongRef;
        
        HardReference(final V strongRef) {
            super(null);
            this.strongRef = strongRef;
        }
        
        @Override
        public V get() {
            return this.strongRef;
        }
    }
}
