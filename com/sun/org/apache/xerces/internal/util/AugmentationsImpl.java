package com.sun.org.apache.xerces.internal.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import com.sun.org.apache.xerces.internal.xni.Augmentations;

public class AugmentationsImpl implements Augmentations
{
    private AugmentationsItemsContainer fAugmentationsContainer;
    
    public AugmentationsImpl() {
        this.fAugmentationsContainer = new SmallContainer();
    }
    
    @Override
    public Object putItem(final String key, final Object item) {
        final Object oldValue = this.fAugmentationsContainer.putItem(key, item);
        if (oldValue == null && this.fAugmentationsContainer.isFull()) {
            this.fAugmentationsContainer = this.fAugmentationsContainer.expand();
        }
        return oldValue;
    }
    
    @Override
    public Object getItem(final String key) {
        return this.fAugmentationsContainer.getItem(key);
    }
    
    @Override
    public Object removeItem(final String key) {
        return this.fAugmentationsContainer.removeItem(key);
    }
    
    @Override
    public Enumeration keys() {
        return this.fAugmentationsContainer.keys();
    }
    
    @Override
    public void removeAllItems() {
        this.fAugmentationsContainer.clear();
    }
    
    @Override
    public String toString() {
        return this.fAugmentationsContainer.toString();
    }
    
    abstract class AugmentationsItemsContainer
    {
        public abstract Object putItem(final Object p0, final Object p1);
        
        public abstract Object getItem(final Object p0);
        
        public abstract Object removeItem(final Object p0);
        
        public abstract Enumeration keys();
        
        public abstract void clear();
        
        public abstract boolean isFull();
        
        public abstract AugmentationsItemsContainer expand();
    }
    
    class SmallContainer extends AugmentationsItemsContainer
    {
        static final int SIZE_LIMIT = 10;
        final Object[] fAugmentations;
        int fNumEntries;
        
        SmallContainer() {
            this.fAugmentations = new Object[20];
            this.fNumEntries = 0;
        }
        
        @Override
        public Enumeration keys() {
            return new SmallContainerKeyEnumeration();
        }
        
        @Override
        public Object getItem(final Object key) {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                if (this.fAugmentations[i].equals(key)) {
                    return this.fAugmentations[i + 1];
                }
            }
            return null;
        }
        
        @Override
        public Object putItem(final Object key, final Object item) {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                if (this.fAugmentations[i].equals(key)) {
                    final Object oldValue = this.fAugmentations[i + 1];
                    this.fAugmentations[i + 1] = item;
                    return oldValue;
                }
            }
            this.fAugmentations[this.fNumEntries * 2] = key;
            this.fAugmentations[this.fNumEntries * 2 + 1] = item;
            ++this.fNumEntries;
            return null;
        }
        
        @Override
        public Object removeItem(final Object key) {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                if (this.fAugmentations[i].equals(key)) {
                    final Object oldValue = this.fAugmentations[i + 1];
                    for (int j = i; j < this.fNumEntries * 2 - 2; j += 2) {
                        this.fAugmentations[j] = this.fAugmentations[j + 2];
                        this.fAugmentations[j + 1] = this.fAugmentations[j + 3];
                    }
                    this.fAugmentations[this.fNumEntries * 2 - 2] = null;
                    this.fAugmentations[this.fNumEntries * 2 - 1] = null;
                    --this.fNumEntries;
                    return oldValue;
                }
            }
            return null;
        }
        
        @Override
        public void clear() {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                this.fAugmentations[i] = null;
                this.fAugmentations[i + 1] = null;
            }
            this.fNumEntries = 0;
        }
        
        @Override
        public boolean isFull() {
            return this.fNumEntries == 10;
        }
        
        @Override
        public AugmentationsItemsContainer expand() {
            final LargeContainer expandedContainer = new LargeContainer();
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                expandedContainer.putItem(this.fAugmentations[i], this.fAugmentations[i + 1]);
            }
            return expandedContainer;
        }
        
        @Override
        public String toString() {
            final StringBuilder buff = new StringBuilder();
            buff.append("SmallContainer - fNumEntries == ").append(this.fNumEntries);
            for (int i = 0; i < 20; i += 2) {
                buff.append("\nfAugmentations[").append(i).append("] == ").append(this.fAugmentations[i]).append("; fAugmentations[").append(i + 1).append("] == ").append(this.fAugmentations[i + 1]);
            }
            return buff.toString();
        }
        
        class SmallContainerKeyEnumeration implements Enumeration
        {
            Object[] enumArray;
            int next;
            
            SmallContainerKeyEnumeration() {
                this.enumArray = new Object[SmallContainer.this.fNumEntries];
                this.next = 0;
                for (int i = 0; i < SmallContainer.this.fNumEntries; ++i) {
                    this.enumArray[i] = SmallContainer.this.fAugmentations[i * 2];
                }
            }
            
            @Override
            public boolean hasMoreElements() {
                return this.next < this.enumArray.length;
            }
            
            @Override
            public Object nextElement() {
                if (this.next >= this.enumArray.length) {
                    throw new NoSuchElementException();
                }
                final Object nextVal = this.enumArray[this.next];
                this.enumArray[this.next] = null;
                ++this.next;
                return nextVal;
            }
        }
    }
    
    class LargeContainer extends AugmentationsItemsContainer
    {
        final Map<Object, Object> fAugmentations;
        
        LargeContainer() {
            this.fAugmentations = new HashMap<Object, Object>();
        }
        
        @Override
        public Object getItem(final Object key) {
            return this.fAugmentations.get(key);
        }
        
        @Override
        public Object putItem(final Object key, final Object item) {
            return this.fAugmentations.put(key, item);
        }
        
        @Override
        public Object removeItem(final Object key) {
            return this.fAugmentations.remove(key);
        }
        
        @Override
        public Enumeration keys() {
            return Collections.enumeration(this.fAugmentations.keySet());
        }
        
        @Override
        public void clear() {
            this.fAugmentations.clear();
        }
        
        @Override
        public boolean isFull() {
            return false;
        }
        
        @Override
        public AugmentationsItemsContainer expand() {
            return this;
        }
        
        @Override
        public String toString() {
            final StringBuilder buff = new StringBuilder();
            buff.append("LargeContainer");
            for (final Object key : this.fAugmentations.keySet()) {
                buff.append("\nkey == ");
                buff.append(key);
                buff.append("; value == ");
                buff.append(this.fAugmentations.get(key));
            }
            return buff.toString();
        }
    }
}
