package org.apache.lucene.analysis.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.AbstractSet;

public class CharArraySet extends AbstractSet<Object>
{
    public static final CharArraySet EMPTY_SET;
    private static final Object PLACEHOLDER;
    private final CharArrayMap<Object> map;
    
    public CharArraySet(final int startSize, final boolean ignoreCase) {
        this(new CharArrayMap<Object>(startSize, ignoreCase));
    }
    
    public CharArraySet(final Collection<?> c, final boolean ignoreCase) {
        this(c.size(), ignoreCase);
        this.addAll(c);
    }
    
    CharArraySet(final CharArrayMap<Object> map) {
        this.map = map;
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    public boolean contains(final char[] text, final int off, final int len) {
        return this.map.containsKey(text, off, len);
    }
    
    public boolean contains(final CharSequence cs) {
        return this.map.containsKey(cs);
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.map.containsKey(o);
    }
    
    @Override
    public boolean add(final Object o) {
        return this.map.put(o, CharArraySet.PLACEHOLDER) == null;
    }
    
    public boolean add(final CharSequence text) {
        return this.map.put(text, CharArraySet.PLACEHOLDER) == null;
    }
    
    public boolean add(final String text) {
        return this.map.put(text, CharArraySet.PLACEHOLDER) == null;
    }
    
    public boolean add(final char[] text) {
        return this.map.put(text, CharArraySet.PLACEHOLDER) == null;
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    public static CharArraySet unmodifiableSet(final CharArraySet set) {
        if (set == null) {
            throw new NullPointerException("Given set is null");
        }
        if (set == CharArraySet.EMPTY_SET) {
            return CharArraySet.EMPTY_SET;
        }
        if (set.map instanceof CharArrayMap.UnmodifiableCharArrayMap) {
            return set;
        }
        return new CharArraySet(CharArrayMap.unmodifiableMap(set.map));
    }
    
    public static CharArraySet copy(final Set<?> set) {
        if (set == CharArraySet.EMPTY_SET) {
            return CharArraySet.EMPTY_SET;
        }
        if (set instanceof CharArraySet) {
            final CharArraySet source = (CharArraySet)set;
            return new CharArraySet(CharArrayMap.copy((Map<?, ?>)source.map));
        }
        return new CharArraySet(set, false);
    }
    
    @Override
    public Iterator<Object> iterator() {
        return this.map.originalKeySet().iterator();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        for (final Object item : this) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            if (item instanceof char[]) {
                sb.append((char[])item);
            }
            else {
                sb.append(item);
            }
        }
        return sb.append(']').toString();
    }
    
    static {
        EMPTY_SET = new CharArraySet(CharArrayMap.emptyMap());
        PLACEHOLDER = new Object();
    }
}
