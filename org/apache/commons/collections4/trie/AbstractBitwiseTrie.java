package org.apache.commons.collections4.trie;

import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;
import org.apache.commons.collections4.Trie;
import java.util.AbstractMap;

public abstract class AbstractBitwiseTrie<K, V> extends AbstractMap<K, V> implements Trie<K, V>, Serializable
{
    private static final long serialVersionUID = 5826987063535505652L;
    private final KeyAnalyzer<? super K> keyAnalyzer;
    
    protected AbstractBitwiseTrie(final KeyAnalyzer<? super K> keyAnalyzer) {
        if (keyAnalyzer == null) {
            throw new NullPointerException("keyAnalyzer");
        }
        this.keyAnalyzer = keyAnalyzer;
    }
    
    protected KeyAnalyzer<? super K> getKeyAnalyzer() {
        return this.keyAnalyzer;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("Trie[").append(this.size()).append("]={\n");
        for (final Map.Entry<K, V> entry : this.entrySet()) {
            buffer.append("  ").append(entry).append("\n");
        }
        buffer.append("}\n");
        return buffer.toString();
    }
    
    final K castKey(final Object key) {
        return (K)key;
    }
    
    final int lengthInBits(final K key) {
        if (key == null) {
            return 0;
        }
        return this.keyAnalyzer.lengthInBits(key);
    }
    
    final int bitsPerElement() {
        return this.keyAnalyzer.bitsPerElement();
    }
    
    final boolean isBitSet(final K key, final int bitIndex, final int lengthInBits) {
        return key != null && this.keyAnalyzer.isBitSet(key, bitIndex, lengthInBits);
    }
    
    final int bitIndex(final K key, final K foundKey) {
        return this.keyAnalyzer.bitIndex((Object)key, 0, this.lengthInBits(key), (Object)foundKey, 0, this.lengthInBits(foundKey));
    }
    
    final boolean compareKeys(final K key, final K other) {
        if (key == null) {
            return other == null;
        }
        return other != null && this.keyAnalyzer.compare((Object)key, (Object)other) == 0;
    }
    
    static boolean compare(final Object a, final Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }
    
    abstract static class BasicEntry<K, V> implements Map.Entry<K, V>, Serializable
    {
        private static final long serialVersionUID = -944364551314110330L;
        protected K key;
        protected V value;
        
        public BasicEntry(final K key) {
            this.key = key;
        }
        
        public BasicEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        public V setKeyValue(final K key, final V value) {
            this.key = key;
            return this.setValue(value);
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public V setValue(final V value) {
            final V previous = this.value;
            this.value = value;
            return previous;
        }
        
        @Override
        public int hashCode() {
            return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>)o;
            return AbstractBitwiseTrie.compare(this.key, other.getKey()) && AbstractBitwiseTrie.compare(this.value, other.getValue());
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
