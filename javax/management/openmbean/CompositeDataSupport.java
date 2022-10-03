package javax.management.openmbean;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;
import java.io.Serializable;

public class CompositeDataSupport implements CompositeData, Serializable
{
    static final long serialVersionUID = 8003518976613702244L;
    private final SortedMap<String, Object> contents;
    private final CompositeType compositeType;
    
    public CompositeDataSupport(final CompositeType compositeType, final String[] array, final Object[] array2) throws OpenDataException {
        this(makeMap(array, array2), compositeType);
    }
    
    private static SortedMap<String, Object> makeMap(final String[] array, final Object[] array2) throws OpenDataException {
        if (array == null || array2 == null) {
            throw new IllegalArgumentException("Null itemNames or itemValues");
        }
        if (array.length == 0 || array2.length == 0) {
            throw new IllegalArgumentException("Empty itemNames or itemValues");
        }
        if (array.length != array2.length) {
            throw new IllegalArgumentException("Different lengths: itemNames[" + array.length + "], itemValues[" + array2.length + "]");
        }
        final TreeMap treeMap = new TreeMap();
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            if (s == null || s.equals("")) {
                throw new IllegalArgumentException("Null or empty item name");
            }
            if (treeMap.containsKey(s)) {
                throw new OpenDataException("Duplicate item name " + s);
            }
            treeMap.put(array[i], array2[i]);
        }
        return treeMap;
    }
    
    public CompositeDataSupport(final CompositeType compositeType, final Map<String, ?> map) throws OpenDataException {
        this(makeMap(map), compositeType);
    }
    
    private static SortedMap<String, Object> makeMap(final Map<String, ?> map) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("Null or empty items map");
        }
        final TreeMap treeMap = new TreeMap();
        for (final String next : map.keySet()) {
            if (next == null || next.equals("")) {
                throw new IllegalArgumentException("Null or empty item name");
            }
            if (!(next instanceof String)) {
                throw new ArrayStoreException("Item name is not string: " + (Object)next);
            }
            treeMap.put(next, map.get(next));
        }
        return treeMap;
    }
    
    private CompositeDataSupport(final SortedMap<String, Object> contents, final CompositeType compositeType) throws OpenDataException {
        if (compositeType == null) {
            throw new IllegalArgumentException("Argument compositeType cannot be null.");
        }
        final Set<String> keySet = compositeType.keySet();
        final Set<Object> keySet2 = contents.keySet();
        if (!keySet.equals(keySet2)) {
            final TreeSet set = new TreeSet(keySet);
            set.removeAll(keySet2);
            final TreeSet set2 = new TreeSet(keySet2);
            set2.removeAll(keySet);
            if (!set.isEmpty() || !set2.isEmpty()) {
                throw new OpenDataException("Item names do not match CompositeType: names in items but not in CompositeType: " + set2 + "; names in CompositeType but not in items: " + set);
            }
        }
        for (final String s : keySet) {
            final Object value = contents.get(s);
            if (value != null) {
                final OpenType<?> type = compositeType.getType(s);
                if (!type.isValue(value)) {
                    throw new OpenDataException("Argument value of wrong type for item " + s + ": value " + value + ", type " + type);
                }
                continue;
            }
        }
        this.compositeType = compositeType;
        this.contents = contents;
    }
    
    @Override
    public CompositeType getCompositeType() {
        return this.compositeType;
    }
    
    @Override
    public Object get(final String s) {
        if (s == null || s.trim().equals("")) {
            throw new IllegalArgumentException("Argument key cannot be a null or empty String.");
        }
        if (!this.contents.containsKey(s.trim())) {
            throw new InvalidKeyException("Argument key=\"" + s.trim() + "\" is not an existing item name for this CompositeData instance.");
        }
        return this.contents.get(s.trim());
    }
    
    @Override
    public Object[] getAll(final String[] array) {
        if (array == null || array.length == 0) {
            return new Object[0];
        }
        final Object[] array2 = new Object[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = this.get(array[i]);
        }
        return array2;
    }
    
    @Override
    public boolean containsKey(final String s) {
        return s != null && !s.trim().equals("") && this.contents.containsKey(s);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.contents.containsValue(o);
    }
    
    @Override
    public Collection<?> values() {
        return Collections.unmodifiableCollection((Collection<?>)this.contents.values());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeData)) {
            return false;
        }
        final CompositeData compositeData = (CompositeData)o;
        if (!this.getCompositeType().equals(compositeData.getCompositeType())) {
            return false;
        }
        if (this.contents.size() != compositeData.values().size()) {
            return false;
        }
        for (final Map.Entry entry : this.contents.entrySet()) {
            final Object value = entry.getValue();
            final Object value2 = compositeData.get((String)entry.getKey());
            if (value == value2) {
                continue;
            }
            if (value == null) {
                return false;
            }
            if (!(value.getClass().isArray() ? Arrays.deepEquals(new Object[] { value }, new Object[] { value2 }) : value.equals(value2))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.compositeType.hashCode();
        for (final boolean[] next : this.contents.values()) {
            if (next instanceof Object[]) {
                hashCode += Arrays.deepHashCode((Object[])next);
            }
            else if (next instanceof byte[]) {
                hashCode += Arrays.hashCode((byte[])next);
            }
            else if (next instanceof short[]) {
                hashCode += Arrays.hashCode((short[])next);
            }
            else if (next instanceof int[]) {
                hashCode += Arrays.hashCode((int[])next);
            }
            else if (next instanceof long[]) {
                hashCode += Arrays.hashCode((long[])next);
            }
            else if (next instanceof char[]) {
                hashCode += Arrays.hashCode((char[])next);
            }
            else if (next instanceof float[]) {
                hashCode += Arrays.hashCode((float[])next);
            }
            else if (next instanceof double[]) {
                hashCode += Arrays.hashCode((double[])next);
            }
            else if (next instanceof boolean[]) {
                hashCode += Arrays.hashCode(next);
            }
            else {
                if (next == null) {
                    continue;
                }
                hashCode += next.hashCode();
            }
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "(compositeType=" + this.compositeType.toString() + ",contents=" + this.contentString() + ")";
    }
    
    private String contentString() {
        final StringBuilder sb = new StringBuilder("{");
        String s = "";
        for (final Map.Entry entry : this.contents.entrySet()) {
            sb.append(s).append((String)entry.getKey()).append("=");
            final String deepToString = Arrays.deepToString(new Object[] { entry.getValue() });
            sb.append(deepToString.substring(1, deepToString.length() - 1));
            s = ", ";
        }
        sb.append("}");
        return sb.toString();
    }
}
