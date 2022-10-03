package sun.nio.cs;

import java.util.Iterator;
import java.nio.charset.Charset;
import java.util.Map;
import java.nio.charset.spi.CharsetProvider;

public class FastCharsetProvider extends CharsetProvider
{
    private Map<String, String> classMap;
    private Map<String, String> aliasMap;
    private Map<String, Charset> cache;
    private String packagePrefix;
    
    protected FastCharsetProvider(final String packagePrefix, final Map<String, String> aliasMap, final Map<String, String> classMap, final Map<String, Charset> cache) {
        this.packagePrefix = packagePrefix;
        this.aliasMap = aliasMap;
        this.classMap = classMap;
        this.cache = cache;
    }
    
    private String canonicalize(final String s) {
        final String s2 = this.aliasMap.get(s);
        return (s2 != null) ? s2 : s;
    }
    
    private static String toLower(final String s) {
        final int length = s.length();
        boolean b = true;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if ((char1 - 'A' | 'Z' - char1) >= 0) {
                b = false;
                break;
            }
        }
        if (b) {
            return s;
        }
        final char[] array = new char[length];
        for (int j = 0; j < length; ++j) {
            final char char2 = s.charAt(j);
            if ((char2 - 'A' | 'Z' - char2) >= 0) {
                array[j] = (char)(char2 + ' ');
            }
            else {
                array[j] = char2;
            }
        }
        return new String(array);
    }
    
    private Charset lookup(final String s) {
        final String canonicalize = this.canonicalize(toLower(s));
        final Charset charset = this.cache.get(canonicalize);
        if (charset != null) {
            return charset;
        }
        final String s2 = this.classMap.get(canonicalize);
        if (s2 == null) {
            return null;
        }
        if (s2.equals("US_ASCII")) {
            final US_ASCII us_ASCII = new US_ASCII();
            this.cache.put(canonicalize, us_ASCII);
            return us_ASCII;
        }
        try {
            final Charset charset2 = (Charset)Class.forName(this.packagePrefix + "." + s2, true, this.getClass().getClassLoader()).newInstance();
            this.cache.put(canonicalize, charset2);
            return charset2;
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            return null;
        }
    }
    
    @Override
    public final Charset charsetForName(final String s) {
        synchronized (this) {
            return this.lookup(this.canonicalize(s));
        }
    }
    
    @Override
    public final Iterator<Charset> charsets() {
        return new Iterator<Charset>() {
            Iterator<String> i = FastCharsetProvider.this.classMap.keySet().iterator();
            
            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }
            
            @Override
            public Charset next() {
                return FastCharsetProvider.this.lookup(this.i.next());
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
