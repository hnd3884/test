package sun.nio.cs;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeMap;
import sun.misc.ASCIICaseInsensitiveComparator;
import java.nio.charset.Charset;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.nio.charset.spi.CharsetProvider;

public class AbstractCharsetProvider extends CharsetProvider
{
    private Map<String, String> classMap;
    private Map<String, String> aliasMap;
    private Map<String, String[]> aliasNameMap;
    private Map<String, SoftReference<Charset>> cache;
    private String packagePrefix;
    
    protected AbstractCharsetProvider() {
        this.classMap = new TreeMap<String, String>(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
        this.aliasMap = new TreeMap<String, String>(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
        this.aliasNameMap = new TreeMap<String, String[]>(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
        this.cache = new TreeMap<String, SoftReference<Charset>>(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
        this.packagePrefix = "sun.nio.cs";
    }
    
    protected AbstractCharsetProvider(final String packagePrefix) {
        this.classMap = new TreeMap<String, String>(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
        this.aliasMap = new TreeMap<String, String>(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
        this.aliasNameMap = new TreeMap<String, String[]>(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
        this.cache = new TreeMap<String, SoftReference<Charset>>(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
        this.packagePrefix = packagePrefix;
    }
    
    private static <K, V> void put(final Map<K, V> map, final K k, final V v) {
        if (!map.containsKey(k)) {
            map.put(k, v);
        }
    }
    
    private static <K, V> void remove(final Map<K, V> map, final K k) {
        final V remove = map.remove(k);
        assert remove != null;
    }
    
    protected void charset(final String s, final String s2, final String[] array) {
        synchronized (this) {
            put(this.classMap, s, s2);
            for (int i = 0; i < array.length; ++i) {
                put(this.aliasMap, array[i], s);
            }
            put(this.aliasNameMap, s, array);
            this.cache.clear();
        }
    }
    
    protected void deleteCharset(final String s, final String[] array) {
        synchronized (this) {
            remove(this.classMap, s);
            for (int i = 0; i < array.length; ++i) {
                remove(this.aliasMap, array[i]);
            }
            remove(this.aliasNameMap, s);
            this.cache.clear();
        }
    }
    
    protected void init() {
    }
    
    private String canonicalize(final String s) {
        final String s2 = this.aliasMap.get(s);
        return (s2 != null) ? s2 : s;
    }
    
    private Charset lookup(final String s) {
        final SoftReference softReference = this.cache.get(s);
        if (softReference != null) {
            final Charset charset = (Charset)softReference.get();
            if (charset != null) {
                return charset;
            }
        }
        final String s2 = this.classMap.get(s);
        if (s2 == null) {
            return null;
        }
        try {
            final Charset charset2 = (Charset)Class.forName(this.packagePrefix + "." + s2, true, this.getClass().getClassLoader()).newInstance();
            this.cache.put(s, new SoftReference<Charset>(charset2));
            return charset2;
        }
        catch (final ClassNotFoundException ex) {
            return null;
        }
        catch (final IllegalAccessException ex2) {
            return null;
        }
        catch (final InstantiationException ex3) {
            return null;
        }
    }
    
    @Override
    public final Charset charsetForName(final String s) {
        synchronized (this) {
            this.init();
            return this.lookup(this.canonicalize(s));
        }
    }
    
    @Override
    public final Iterator<Charset> charsets() {
        final ArrayList list;
        synchronized (this) {
            this.init();
            list = new ArrayList((Collection<? extends E>)this.classMap.keySet());
        }
        return new Iterator<Charset>() {
            Iterator<String> i = list.iterator();
            
            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }
            
            @Override
            public Charset next() {
                final String s = this.i.next();
                synchronized (AbstractCharsetProvider.this) {
                    return AbstractCharsetProvider.this.lookup(s);
                }
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    public final String[] aliases(final String s) {
        synchronized (this) {
            this.init();
            return this.aliasNameMap.get(s);
        }
    }
}
