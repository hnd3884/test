package io.netty.channel.nio;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.nio.channels.SelectionKey;
import java.util.AbstractSet;

final class SelectedSelectionKeySet extends AbstractSet<SelectionKey>
{
    SelectionKey[] keys;
    int size;
    
    SelectedSelectionKeySet() {
        this.keys = new SelectionKey[1024];
    }
    
    @Override
    public boolean add(final SelectionKey o) {
        if (o == null) {
            return false;
        }
        this.keys[this.size++] = o;
        if (this.size == this.keys.length) {
            this.increaseCapacity();
        }
        return true;
    }
    
    @Override
    public boolean remove(final Object o) {
        return false;
    }
    
    @Override
    public boolean contains(final Object o) {
        return false;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public Iterator<SelectionKey> iterator() {
        return new Iterator<SelectionKey>() {
            private int idx;
            
            @Override
            public boolean hasNext() {
                return this.idx < SelectedSelectionKeySet.this.size;
            }
            
            @Override
            public SelectionKey next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return SelectedSelectionKeySet.this.keys[this.idx++];
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    void reset() {
        this.reset(0);
    }
    
    void reset(final int start) {
        Arrays.fill(this.keys, start, this.size, null);
        this.size = 0;
    }
    
    private void increaseCapacity() {
        final SelectionKey[] newKeys = new SelectionKey[this.keys.length << 1];
        System.arraycopy(this.keys, 0, newKeys, 0, this.size);
        this.keys = newKeys;
    }
}
