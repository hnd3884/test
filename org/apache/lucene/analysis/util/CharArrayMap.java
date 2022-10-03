package org.apache.lucene.analysis.util;

import java.util.AbstractSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;
import java.util.AbstractMap;

public class CharArrayMap<V> extends AbstractMap<Object, V>
{
    private static final CharArrayMap<?> EMPTY_MAP;
    private static final int INIT_SIZE = 8;
    private final CharacterUtils charUtils;
    private boolean ignoreCase;
    private int count;
    char[][] keys;
    V[] values;
    private EntrySet entrySet;
    private CharArraySet keySet;
    
    public CharArrayMap(final int startSize, final boolean ignoreCase) {
        this.entrySet = null;
        this.keySet = null;
        this.ignoreCase = ignoreCase;
        int size;
        for (size = 8; startSize + (startSize >> 2) > size; size <<= 1) {}
        this.keys = new char[size][];
        this.values = (V[])new Object[size];
        this.charUtils = CharacterUtils.getInstance();
    }
    
    public CharArrayMap(final Map<?, ? extends V> c, final boolean ignoreCase) {
        this(c.size(), ignoreCase);
        this.putAll(c);
    }
    
    private CharArrayMap(final CharArrayMap<V> toCopy) {
        this.entrySet = null;
        this.keySet = null;
        this.keys = toCopy.keys;
        this.values = toCopy.values;
        this.ignoreCase = toCopy.ignoreCase;
        this.count = toCopy.count;
        this.charUtils = toCopy.charUtils;
    }
    
    @Override
    public void clear() {
        this.count = 0;
        Arrays.fill(this.keys, null);
        Arrays.fill(this.values, null);
    }
    
    public boolean containsKey(final char[] text, final int off, final int len) {
        return this.keys[this.getSlot(text, off, len)] != null;
    }
    
    public boolean containsKey(final CharSequence cs) {
        return this.keys[this.getSlot(cs)] != null;
    }
    
    @Override
    public boolean containsKey(final Object o) {
        if (o instanceof char[]) {
            final char[] text = (char[])o;
            return this.containsKey(text, 0, text.length);
        }
        return this.containsKey(o.toString());
    }
    
    public V get(final char[] text, final int off, final int len) {
        return this.values[this.getSlot(text, off, len)];
    }
    
    public V get(final CharSequence cs) {
        return this.values[this.getSlot(cs)];
    }
    
    @Override
    public V get(final Object o) {
        if (o instanceof char[]) {
            final char[] text = (char[])o;
            return this.get(text, 0, text.length);
        }
        return this.get(o.toString());
    }
    
    private int getSlot(final char[] text, final int off, final int len) {
        int code = this.getHashCode(text, off, len);
        int pos = code & this.keys.length - 1;
        char[] text2 = this.keys[pos];
        if (text2 != null && !this.equals(text, off, len, text2)) {
            final int inc = (code >> 8) + code | 0x1;
            do {
                code += inc;
                pos = (code & this.keys.length - 1);
                text2 = this.keys[pos];
            } while (text2 != null && !this.equals(text, off, len, text2));
        }
        return pos;
    }
    
    private int getSlot(final CharSequence text) {
        int code = this.getHashCode(text);
        int pos = code & this.keys.length - 1;
        char[] text2 = this.keys[pos];
        if (text2 != null && !this.equals(text, text2)) {
            final int inc = (code >> 8) + code | 0x1;
            do {
                code += inc;
                pos = (code & this.keys.length - 1);
                text2 = this.keys[pos];
            } while (text2 != null && !this.equals(text, text2));
        }
        return pos;
    }
    
    public V put(final CharSequence text, final V value) {
        return this.put(text.toString(), value);
    }
    
    @Override
    public V put(final Object o, final V value) {
        if (o instanceof char[]) {
            return this.put((char[])o, value);
        }
        return this.put(o.toString(), value);
    }
    
    public V put(final String text, final V value) {
        return this.put(text.toCharArray(), value);
    }
    
    public V put(final char[] text, final V value) {
        if (this.ignoreCase) {
            this.charUtils.toLowerCase(text, 0, text.length);
        }
        final int slot = this.getSlot(text, 0, text.length);
        if (this.keys[slot] != null) {
            final V oldValue = this.values[slot];
            this.values[slot] = value;
            return oldValue;
        }
        this.keys[slot] = text;
        this.values[slot] = value;
        ++this.count;
        if (this.count + (this.count >> 2) > this.keys.length) {
            this.rehash();
        }
        return null;
    }
    
    private void rehash() {
        assert this.keys.length == this.values.length;
        final int newSize = 2 * this.keys.length;
        final char[][] oldkeys = this.keys;
        final V[] oldvalues = this.values;
        this.keys = new char[newSize][];
        this.values = (V[])new Object[newSize];
        for (int i = 0; i < oldkeys.length; ++i) {
            final char[] text = oldkeys[i];
            if (text != null) {
                final int slot = this.getSlot(text, 0, text.length);
                this.keys[slot] = text;
                this.values[slot] = oldvalues[i];
            }
        }
    }
    
    private boolean equals(final char[] text1, final int off, final int len, final char[] text2) {
        if (len != text2.length) {
            return false;
        }
        final int limit = off + len;
        if (this.ignoreCase) {
            int codePointAt;
            for (int i = 0; i < len; i += Character.charCount(codePointAt)) {
                codePointAt = this.charUtils.codePointAt(text1, off + i, limit);
                if (Character.toLowerCase(codePointAt) != this.charUtils.codePointAt(text2, i, text2.length)) {
                    return false;
                }
            }
        }
        else {
            for (int i = 0; i < len; ++i) {
                if (text1[off + i] != text2[i]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean equals(final CharSequence text1, final char[] text2) {
        final int len = text1.length();
        if (len != text2.length) {
            return false;
        }
        if (this.ignoreCase) {
            int codePointAt;
            for (int i = 0; i < len; i += Character.charCount(codePointAt)) {
                codePointAt = this.charUtils.codePointAt(text1, i);
                if (Character.toLowerCase(codePointAt) != this.charUtils.codePointAt(text2, i, text2.length)) {
                    return false;
                }
            }
        }
        else {
            for (int i = 0; i < len; ++i) {
                if (text1.charAt(i) != text2[i]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private int getHashCode(final char[] text, final int offset, final int len) {
        if (text == null) {
            throw new NullPointerException();
        }
        int code = 0;
        final int stop = offset + len;
        if (this.ignoreCase) {
            int codePointAt;
            for (int i = offset; i < stop; i += Character.charCount(codePointAt)) {
                codePointAt = this.charUtils.codePointAt(text, i, stop);
                code = code * 31 + Character.toLowerCase(codePointAt);
            }
        }
        else {
            for (int i = offset; i < stop; ++i) {
                code = code * 31 + text[i];
            }
        }
        return code;
    }
    
    private int getHashCode(final CharSequence text) {
        if (text == null) {
            throw new NullPointerException();
        }
        int code = 0;
        final int len = text.length();
        if (this.ignoreCase) {
            int codePointAt;
            for (int i = 0; i < len; i += Character.charCount(codePointAt)) {
                codePointAt = this.charUtils.codePointAt(text, i);
                code = code * 31 + Character.toLowerCase(codePointAt);
            }
        }
        else {
            for (int i = 0; i < len; ++i) {
                code = code * 31 + text.charAt(i);
            }
        }
        return code;
    }
    
    @Override
    public V remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int size() {
        return this.count;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        for (final Map.Entry<Object, V> entry : this.entrySet()) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(entry);
        }
        return sb.append('}').toString();
    }
    
    EntrySet createEntrySet() {
        return new EntrySet(true);
    }
    
    @Override
    public final EntrySet entrySet() {
        if (this.entrySet == null) {
            this.entrySet = this.createEntrySet();
        }
        return this.entrySet;
    }
    
    final Set<Object> originalKeySet() {
        return super.keySet();
    }
    
    @Override
    public final CharArraySet keySet() {
        if (this.keySet == null) {
            this.keySet = new CharArraySet(this) {
                @Override
                public boolean add(final Object o) {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public boolean add(final CharSequence text) {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public boolean add(final String text) {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public boolean add(final char[] text) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return this.keySet;
    }
    
    public static <V> CharArrayMap<V> unmodifiableMap(final CharArrayMap<V> map) {
        if (map == null) {
            throw new NullPointerException("Given map is null");
        }
        if (map == emptyMap() || map.isEmpty()) {
            return emptyMap();
        }
        if (map instanceof UnmodifiableCharArrayMap) {
            return map;
        }
        return new UnmodifiableCharArrayMap<V>(map);
    }
    
    public static <V> CharArrayMap<V> copy(final Map<?, ? extends V> map) {
        if (map == CharArrayMap.EMPTY_MAP) {
            return emptyMap();
        }
        if (map instanceof CharArrayMap) {
            CharArrayMap<V> m = (CharArrayMap)map;
            final char[][] keys = new char[m.keys.length][];
            System.arraycopy(m.keys, 0, keys, 0, keys.length);
            final V[] values = (V[])new Object[m.values.length];
            System.arraycopy(m.values, 0, values, 0, values.length);
            m = new CharArrayMap<V>(m);
            m.keys = keys;
            m.values = values;
            return m;
        }
        return new CharArrayMap<V>(map, false);
    }
    
    public static <V> CharArrayMap<V> emptyMap() {
        return (CharArrayMap<V>)CharArrayMap.EMPTY_MAP;
    }
    
    static {
        EMPTY_MAP = new EmptyCharArrayMap<Object>();
    }
    
    public class EntryIterator implements Iterator<Map.Entry<Object, V>>
    {
        private int pos;
        private int lastPos;
        private final boolean allowModify;
        
        private EntryIterator(final boolean allowModify) {
            this.pos = -1;
            this.allowModify = allowModify;
            this.goNext();
        }
        
        private void goNext() {
            this.lastPos = this.pos;
            ++this.pos;
            while (this.pos < CharArrayMap.this.keys.length && CharArrayMap.this.keys[this.pos] == null) {
                ++this.pos;
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.pos < CharArrayMap.this.keys.length;
        }
        
        public char[] nextKey() {
            this.goNext();
            return CharArrayMap.this.keys[this.lastPos];
        }
        
        public String nextKeyString() {
            return new String(this.nextKey());
        }
        
        public V currentValue() {
            return CharArrayMap.this.values[this.lastPos];
        }
        
        public V setValue(final V value) {
            if (!this.allowModify) {
                throw new UnsupportedOperationException();
            }
            final V old = CharArrayMap.this.values[this.lastPos];
            CharArrayMap.this.values[this.lastPos] = value;
            return old;
        }
        
        @Override
        public Map.Entry<Object, V> next() {
            this.goNext();
            return new MapEntry(this.lastPos, this.allowModify);
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private final class MapEntry implements Map.Entry<Object, V>
    {
        private final int pos;
        private final boolean allowModify;
        
        private MapEntry(final int pos, final boolean allowModify) {
            this.pos = pos;
            this.allowModify = allowModify;
        }
        
        @Override
        public Object getKey() {
            return CharArrayMap.this.keys[this.pos].clone();
        }
        
        @Override
        public V getValue() {
            return CharArrayMap.this.values[this.pos];
        }
        
        @Override
        public V setValue(final V value) {
            if (!this.allowModify) {
                throw new UnsupportedOperationException();
            }
            final V old = CharArrayMap.this.values[this.pos];
            CharArrayMap.this.values[this.pos] = value;
            return old;
        }
        
        @Override
        public String toString() {
            return new StringBuilder().append(CharArrayMap.this.keys[this.pos]).append('=').append((CharArrayMap.this.values[this.pos] == CharArrayMap.this) ? "(this Map)" : CharArrayMap.this.values[this.pos]).toString();
        }
    }
    
    public final class EntrySet extends AbstractSet<Map.Entry<Object, V>>
    {
        private final boolean allowModify;
        
        private EntrySet(final boolean allowModify) {
            this.allowModify = allowModify;
        }
        
        @Override
        public EntryIterator iterator() {
            return new EntryIterator(this.allowModify);
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<Object, V> e = (Map.Entry<Object, V>)o;
            final Object key = e.getKey();
            final Object val = e.getValue();
            final Object v = CharArrayMap.this.get(key);
            return (v == null) ? (val == null) : v.equals(val);
        }
        
        @Override
        public boolean remove(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int size() {
            return CharArrayMap.this.count;
        }
        
        @Override
        public void clear() {
            if (!this.allowModify) {
                throw new UnsupportedOperationException();
            }
            CharArrayMap.this.clear();
        }
    }
    
    static class UnmodifiableCharArrayMap<V> extends CharArrayMap<V>
    {
        UnmodifiableCharArrayMap(final CharArrayMap<V> map) {
            super((CharArrayMap<Object>)map, null);
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public V put(final Object o, final V val) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public V put(final char[] text, final V val) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public V put(final CharSequence text, final V val) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public V put(final String text, final V val) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public V remove(final Object key) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        EntrySet createEntrySet() {
            return new EntrySet(false);
        }
    }
    
    private static final class EmptyCharArrayMap<V> extends UnmodifiableCharArrayMap<V>
    {
        EmptyCharArrayMap() {
            super(new CharArrayMap(0, false));
        }
        
        @Override
        public boolean containsKey(final char[] text, final int off, final int len) {
            if (text == null) {
                throw new NullPointerException();
            }
            return false;
        }
        
        @Override
        public boolean containsKey(final CharSequence cs) {
            if (cs == null) {
                throw new NullPointerException();
            }
            return false;
        }
        
        @Override
        public boolean containsKey(final Object o) {
            if (o == null) {
                throw new NullPointerException();
            }
            return false;
        }
        
        @Override
        public V get(final char[] text, final int off, final int len) {
            if (text == null) {
                throw new NullPointerException();
            }
            return null;
        }
        
        @Override
        public V get(final CharSequence cs) {
            if (cs == null) {
                throw new NullPointerException();
            }
            return null;
        }
        
        @Override
        public V get(final Object o) {
            if (o == null) {
                throw new NullPointerException();
            }
            return null;
        }
    }
}
