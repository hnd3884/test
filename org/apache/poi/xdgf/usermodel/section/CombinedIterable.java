package org.apache.poi.xdgf.usermodel.section;

import java.util.Set;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedMap;

public class CombinedIterable<T> implements Iterable<T>
{
    final SortedMap<Long, T> _baseItems;
    final SortedMap<Long, T> _masterItems;
    
    public CombinedIterable(final SortedMap<Long, T> baseItems, final SortedMap<Long, T> masterItems) {
        this._baseItems = baseItems;
        this._masterItems = masterItems;
    }
    
    @Override
    public Iterator<T> iterator() {
        Iterator<Map.Entry<Long, T>> vmasterI;
        if (this._masterItems != null) {
            vmasterI = this._masterItems.entrySet().iterator();
        }
        else {
            final Set<Map.Entry<Long, T>> empty = Collections.emptySet();
            vmasterI = empty.iterator();
        }
        return new Iterator<T>() {
            Long lastI = Long.MIN_VALUE;
            Map.Entry<Long, T> currentBase;
            Map.Entry<Long, T> currentMaster;
            Iterator<Map.Entry<Long, T>> baseI = CombinedIterable.this._baseItems.entrySet().iterator();
            Iterator<Map.Entry<Long, T>> masterI = vmasterI;
            
            @Override
            public boolean hasNext() {
                return this.currentBase != null || this.currentMaster != null || this.baseI.hasNext() || this.masterI.hasNext();
            }
            
            @Override
            public T next() {
                long baseIdx = Long.MAX_VALUE;
                long masterIdx = Long.MAX_VALUE;
                if (this.currentBase == null) {
                    while (this.baseI.hasNext()) {
                        this.currentBase = this.baseI.next();
                        if (this.currentBase.getKey() > this.lastI) {
                            baseIdx = this.currentBase.getKey();
                            break;
                        }
                    }
                }
                else {
                    baseIdx = this.currentBase.getKey();
                }
                if (this.currentMaster == null) {
                    while (this.masterI.hasNext()) {
                        this.currentMaster = this.masterI.next();
                        if (this.currentMaster.getKey() > this.lastI) {
                            masterIdx = this.currentMaster.getKey();
                            break;
                        }
                    }
                }
                else {
                    masterIdx = this.currentMaster.getKey();
                }
                T val;
                if (this.currentBase != null) {
                    if (baseIdx <= masterIdx) {
                        this.lastI = baseIdx;
                        val = this.currentBase.getValue();
                        if (masterIdx == baseIdx) {
                            this.currentMaster = null;
                        }
                        this.currentBase = null;
                    }
                    else {
                        this.lastI = masterIdx;
                        val = ((this.currentMaster != null) ? this.currentMaster.getValue() : null);
                        this.currentMaster = null;
                    }
                }
                else {
                    if (this.currentMaster == null) {
                        throw new NoSuchElementException();
                    }
                    this.lastI = this.currentMaster.getKey();
                    val = this.currentMaster.getValue();
                    this.currentMaster = null;
                }
                return val;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
