package io.grpc;

import java.util.Arrays;

final class PersistentHashArrayMappedTrie
{
    private PersistentHashArrayMappedTrie() {
    }
    
    static <K, V> V get(final Node<K, V> root, final K key) {
        if (root == null) {
            return null;
        }
        return root.get(key, key.hashCode(), 0);
    }
    
    static <K, V> Node<K, V> put(final Node<K, V> root, final K key, final V value) {
        if (root == null) {
            return new Leaf<K, V>(key, value);
        }
        return root.put(key, value, key.hashCode(), 0);
    }
    
    static final class Leaf<K, V> implements Node<K, V>
    {
        private final K key;
        private final V value;
        
        public Leaf(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public int size() {
            return 1;
        }
        
        @Override
        public V get(final K key, final int hash, final int bitsConsumed) {
            if (this.key == key) {
                return this.value;
            }
            return null;
        }
        
        @Override
        public Node<K, V> put(final K key, final V value, final int hash, final int bitsConsumed) {
            final int thisHash = this.key.hashCode();
            if (thisHash != hash) {
                return CompressedIndex.combine((Node<K, V>)new Leaf<Object, Object>(key, value), hash, this, thisHash, bitsConsumed);
            }
            if (this.key == key) {
                return new Leaf(key, value);
            }
            return new CollisionLeaf<K, V>(this.key, this.value, key, value);
        }
        
        @Override
        public String toString() {
            return String.format("Leaf(key=%s value=%s)", this.key, this.value);
        }
    }
    
    static final class CollisionLeaf<K, V> implements Node<K, V>
    {
        private final K[] keys;
        private final V[] values;
        
        CollisionLeaf(final K key1, final V value1, final K key2, final V value2) {
            this(new Object[] { key1, key2 }, new Object[] { value1, value2 });
            assert key1 != key2;
            assert key1.hashCode() == key2.hashCode();
        }
        
        private CollisionLeaf(final K[] keys, final V[] values) {
            this.keys = keys;
            this.values = values;
        }
        
        @Override
        public int size() {
            return this.values.length;
        }
        
        @Override
        public V get(final K key, final int hash, final int bitsConsumed) {
            for (int i = 0; i < this.keys.length; ++i) {
                if (this.keys[i] == key) {
                    return this.values[i];
                }
            }
            return null;
        }
        
        @Override
        public Node<K, V> put(final K key, final V value, final int hash, final int bitsConsumed) {
            final int thisHash = this.keys[0].hashCode();
            if (thisHash != hash) {
                return CompressedIndex.combine(new Leaf<K, V>(key, value), hash, this, thisHash, bitsConsumed);
            }
            final int keyIndex;
            if ((keyIndex = this.indexOfKey(key)) != -1) {
                final K[] newKeys = Arrays.copyOf(this.keys, this.keys.length);
                final V[] newValues = Arrays.copyOf(this.values, this.keys.length);
                newKeys[keyIndex] = key;
                newValues[keyIndex] = value;
                return new CollisionLeaf(newKeys, newValues);
            }
            final K[] newKeys = Arrays.copyOf(this.keys, this.keys.length + 1);
            final V[] newValues = Arrays.copyOf(this.values, this.keys.length + 1);
            newKeys[this.keys.length] = key;
            newValues[this.keys.length] = value;
            return new CollisionLeaf(newKeys, newValues);
        }
        
        private int indexOfKey(final K key) {
            for (int i = 0; i < this.keys.length; ++i) {
                if (this.keys[i] == key) {
                    return i;
                }
            }
            return -1;
        }
        
        @Override
        public String toString() {
            final StringBuilder valuesSb = new StringBuilder();
            valuesSb.append("CollisionLeaf(");
            for (int i = 0; i < this.values.length; ++i) {
                valuesSb.append("(key=").append(this.keys[i]).append(" value=").append(this.values[i]).append(") ");
            }
            return valuesSb.append(")").toString();
        }
    }
    
    static final class CompressedIndex<K, V> implements Node<K, V>
    {
        private static final int BITS = 5;
        private static final int BITS_MASK = 31;
        final int bitmap;
        final Node<K, V>[] values;
        private final int size;
        
        private CompressedIndex(final int bitmap, final Node<K, V>[] values, final int size) {
            this.bitmap = bitmap;
            this.values = values;
            this.size = size;
        }
        
        @Override
        public int size() {
            return this.size;
        }
        
        @Override
        public V get(final K key, final int hash, final int bitsConsumed) {
            final int indexBit = indexBit(hash, bitsConsumed);
            if ((this.bitmap & indexBit) == 0x0) {
                return null;
            }
            final int compressedIndex = this.compressedIndex(indexBit);
            return this.values[compressedIndex].get(key, hash, bitsConsumed + 5);
        }
        
        @Override
        public Node<K, V> put(final K key, final V value, final int hash, final int bitsConsumed) {
            final int indexBit = indexBit(hash, bitsConsumed);
            final int compressedIndex = this.compressedIndex(indexBit);
            if ((this.bitmap & indexBit) == 0x0) {
                final int newBitmap = this.bitmap | indexBit;
                final Node<K, V>[] newValues = new Node[this.values.length + 1];
                System.arraycopy(this.values, 0, newValues, 0, compressedIndex);
                newValues[compressedIndex] = new Leaf<K, V>(key, value);
                System.arraycopy(this.values, compressedIndex, newValues, compressedIndex + 1, this.values.length - compressedIndex);
                return new CompressedIndex(newBitmap, (Node<Object, Object>[])newValues, this.size() + 1);
            }
            final Node<K, V>[] newValues2 = Arrays.copyOf(this.values, this.values.length);
            newValues2[compressedIndex] = this.values[compressedIndex].put(key, value, hash, bitsConsumed + 5);
            int newSize = this.size();
            newSize += newValues2[compressedIndex].size();
            newSize -= this.values[compressedIndex].size();
            return new CompressedIndex(this.bitmap, (Node<Object, Object>[])newValues2, newSize);
        }
        
        static <K, V> Node<K, V> combine(Node<K, V> node1, final int hash1, Node<K, V> node2, final int hash2, final int bitsConsumed) {
            assert hash1 != hash2;
            final int indexBit1 = indexBit(hash1, bitsConsumed);
            final int indexBit2 = indexBit(hash2, bitsConsumed);
            if (indexBit1 == indexBit2) {
                final Node<K, V> node3 = (Node<K, V>)combine((Node<Object, Object>)node1, hash1, (Node<Object, Object>)node2, hash2, bitsConsumed + 5);
                final Node<K, V>[] values = { node3 };
                return new CompressedIndex<K, V>(indexBit1, values, node3.size());
            }
            if (uncompressedIndex(hash1, bitsConsumed) > uncompressedIndex(hash2, bitsConsumed)) {
                final Node<K, V> nodeCopy = node1;
                node1 = node2;
                node2 = nodeCopy;
            }
            final Node<K, V>[] values2 = { node1, node2 };
            return new CompressedIndex<K, V>(indexBit1 | indexBit2, values2, node1.size() + node2.size());
        }
        
        @Override
        public String toString() {
            final StringBuilder valuesSb = new StringBuilder();
            valuesSb.append("CompressedIndex(").append(String.format("bitmap=%s ", Integer.toBinaryString(this.bitmap)));
            for (final Node<K, V> value : this.values) {
                valuesSb.append(value).append(" ");
            }
            return valuesSb.append(")").toString();
        }
        
        private int compressedIndex(final int indexBit) {
            return Integer.bitCount(this.bitmap & indexBit - 1);
        }
        
        private static int uncompressedIndex(final int hash, final int bitsConsumed) {
            return hash >>> bitsConsumed & 0x1F;
        }
        
        private static int indexBit(final int hash, final int bitsConsumed) {
            final int uncompressedIndex = uncompressedIndex(hash, bitsConsumed);
            return 1 << uncompressedIndex;
        }
    }
    
    interface Node<K, V>
    {
        V get(final K p0, final int p1, final int p2);
        
        Node<K, V> put(final K p0, final V p1, final int p2, final int p3);
        
        int size();
    }
}
