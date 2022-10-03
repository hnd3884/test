package org.glassfish.hk2.utilities.general.internal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class DoubleNode<K, V>
{
    private final WeakReference<K> weakKey;
    private final V value;
    private DoubleNode<K, V> previous;
    private DoubleNode<K, V> next;
    private K hardenedKey;
    
    public DoubleNode(final K key, final V value, final ReferenceQueue<? super K> queue) {
        this.weakKey = new WeakReference<K>(key, queue);
        this.value = value;
    }
    
    public DoubleNode<K, V> getPrevious() {
        return this.previous;
    }
    
    public void setPrevious(final DoubleNode<K, V> previous) {
        this.previous = previous;
    }
    
    public DoubleNode<K, V> getNext() {
        return this.next;
    }
    
    public void setNext(final DoubleNode<K, V> next) {
        this.next = next;
    }
    
    public WeakReference<K> getWeakKey() {
        return this.weakKey;
    }
    
    public V getValue() {
        return this.value;
    }
    
    public K getHardenedKey() {
        return this.hardenedKey;
    }
    
    public void setHardenedKey(final K hardenedKey) {
        this.hardenedKey = hardenedKey;
    }
}
