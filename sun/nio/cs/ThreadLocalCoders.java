package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;

public class ThreadLocalCoders
{
    private static final int CACHE_SIZE = 3;
    private static Cache decoderCache;
    private static Cache encoderCache;
    
    public static CharsetDecoder decoderFor(final Object o) {
        final CharsetDecoder charsetDecoder = (CharsetDecoder)ThreadLocalCoders.decoderCache.forName(o);
        charsetDecoder.reset();
        return charsetDecoder;
    }
    
    public static CharsetEncoder encoderFor(final Object o) {
        final CharsetEncoder charsetEncoder = (CharsetEncoder)ThreadLocalCoders.encoderCache.forName(o);
        charsetEncoder.reset();
        return charsetEncoder;
    }
    
    static {
        ThreadLocalCoders.decoderCache = new Cache(3) {
            @Override
            boolean hasName(final Object o, final Object o2) {
                if (o2 instanceof String) {
                    return ((CharsetDecoder)o).charset().name().equals(o2);
                }
                return o2 instanceof Charset && ((CharsetDecoder)o).charset().equals(o2);
            }
            
            @Override
            Object create(final Object o) {
                if (o instanceof String) {
                    return Charset.forName((String)o).newDecoder();
                }
                if (o instanceof Charset) {
                    return ((Charset)o).newDecoder();
                }
                assert false;
                return null;
            }
        };
        ThreadLocalCoders.encoderCache = new Cache(3) {
            @Override
            boolean hasName(final Object o, final Object o2) {
                if (o2 instanceof String) {
                    return ((CharsetEncoder)o).charset().name().equals(o2);
                }
                return o2 instanceof Charset && ((CharsetEncoder)o).charset().equals(o2);
            }
            
            @Override
            Object create(final Object o) {
                if (o instanceof String) {
                    return Charset.forName((String)o).newEncoder();
                }
                if (o instanceof Charset) {
                    return ((Charset)o).newEncoder();
                }
                assert false;
                return null;
            }
        };
    }
    
    private abstract static class Cache
    {
        private ThreadLocal<Object[]> cache;
        private final int size;
        
        Cache(final int size) {
            this.cache = new ThreadLocal<Object[]>();
            this.size = size;
        }
        
        abstract Object create(final Object p0);
        
        private void moveToFront(final Object[] array, final int n) {
            final Object o = array[n];
            for (int i = n; i > 0; --i) {
                array[i] = array[i - 1];
            }
            array[0] = o;
        }
        
        abstract boolean hasName(final Object p0, final Object p1);
        
        Object forName(final Object o) {
            Object[] array = this.cache.get();
            if (array == null) {
                array = new Object[this.size];
                this.cache.set(array);
            }
            else {
                for (int i = 0; i < array.length; ++i) {
                    final Object o2 = array[i];
                    if (o2 != null) {
                        if (this.hasName(o2, o)) {
                            if (i > 0) {
                                this.moveToFront(array, i);
                            }
                            return o2;
                        }
                    }
                }
            }
            final Object create = this.create(o);
            array[array.length - 1] = create;
            this.moveToFront(array, array.length - 1);
            return create;
        }
    }
}
