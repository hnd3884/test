package org.apache.commons.math3.util;

public class Pair<K, V>
{
    private final K key;
    private final V value;
    
    public Pair(final K k, final V v) {
        this.key = k;
        this.value = v;
    }
    
    public Pair(final Pair<? extends K, ? extends V> entry) {
        this(entry.getKey(), entry.getValue());
    }
    
    public K getKey() {
        return this.key;
    }
    
    public V getValue() {
        return this.value;
    }
    
    public K getFirst() {
        return this.key;
    }
    
    public V getSecond() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        final Pair<?, ?> oP = (Pair<?, ?>)o;
        if (this.key == null) {
            if (oP.key != null) {
                return false;
            }
        }
        else if (!this.key.equals(oP.key)) {
            return false;
        }
        if ((this.value != null) ? this.value.equals(oP.value) : (oP.value == null)) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.key == null) ? 0 : this.key.hashCode();
        final int h = (this.value == null) ? 0 : this.value.hashCode();
        result = (37 * result + h ^ h >>> 16);
        return result;
    }
    
    @Override
    public String toString() {
        return "[" + this.getKey() + ", " + this.getValue() + "]";
    }
    
    public static <K, V> Pair<K, V> create(final K k, final V v) {
        return new Pair<K, V>(k, v);
    }
}
