package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class DuplicateAttributeVerifier
{
    public static final int MAP_SIZE = 256;
    public int _currentIteration;
    private Entry[] _map;
    public final Entry _poolHead;
    public Entry _poolCurrent;
    private Entry _poolTail;
    
    public DuplicateAttributeVerifier() {
        final Entry entry = new Entry();
        this._poolHead = entry;
        this._poolTail = entry;
    }
    
    public final void clear() {
        this._currentIteration = 0;
        for (Entry e = this._poolHead; e != null; e = e.poolNext) {
            e.iteration = 0;
        }
        this.reset();
    }
    
    public final void reset() {
        this._poolCurrent = this._poolHead;
        if (this._map == null) {
            this._map = new Entry[256];
        }
    }
    
    private final void increasePool(final int capacity) {
        if (this._map == null) {
            this._map = new Entry[256];
            this._poolCurrent = this._poolHead;
        }
        else {
            final Entry tail = this._poolTail;
            for (int i = 0; i < capacity; ++i) {
                final Entry e = new Entry();
                this._poolTail.poolNext = e;
                this._poolTail = e;
            }
            this._poolCurrent = tail.poolNext;
        }
    }
    
    public final void checkForDuplicateAttribute(final int hash, final int value) throws FastInfosetException {
        if (this._poolCurrent == null) {
            this.increasePool(16);
        }
        final Entry newEntry = this._poolCurrent;
        this._poolCurrent = this._poolCurrent.poolNext;
        final Entry head = this._map[hash];
        if (head != null && head.iteration >= this._currentIteration) {
            Entry e = head;
            while (e.value != value) {
                if ((e = e.hashNext) == null) {
                    newEntry.hashNext = head;
                    (this._map[hash] = newEntry).iteration = this._currentIteration;
                    newEntry.value = value;
                    return;
                }
            }
            this.reset();
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateAttribute"));
        }
        newEntry.hashNext = null;
        (this._map[hash] = newEntry).iteration = this._currentIteration;
        newEntry.value = value;
    }
    
    public static class Entry
    {
        private int iteration;
        private int value;
        private Entry hashNext;
        private Entry poolNext;
    }
}
