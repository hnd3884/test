package org.apache.xerces.util;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Enumeration;
import org.apache.xerces.xni.Augmentations;

public class AugmentationsImpl implements Augmentations
{
    private AugmentationsItemsContainer fAugmentationsContainer;
    
    public AugmentationsImpl() {
        this.fAugmentationsContainer = new SmallContainer();
    }
    
    public Object putItem(final String s, final Object o) {
        final Object putItem = this.fAugmentationsContainer.putItem(s, o);
        if (putItem == null && this.fAugmentationsContainer.isFull()) {
            this.fAugmentationsContainer = this.fAugmentationsContainer.expand();
        }
        return putItem;
    }
    
    public Object getItem(final String s) {
        return this.fAugmentationsContainer.getItem(s);
    }
    
    public Object removeItem(final String s) {
        return this.fAugmentationsContainer.removeItem(s);
    }
    
    public Enumeration keys() {
        return this.fAugmentationsContainer.keys();
    }
    
    public void removeAllItems() {
        this.fAugmentationsContainer.clear();
    }
    
    public String toString() {
        return this.fAugmentationsContainer.toString();
    }
    
    abstract static class AugmentationsItemsContainer
    {
        public abstract Object putItem(final Object p0, final Object p1);
        
        public abstract Object getItem(final Object p0);
        
        public abstract Object removeItem(final Object p0);
        
        public abstract Enumeration keys();
        
        public abstract void clear();
        
        public abstract boolean isFull();
        
        public abstract AugmentationsItemsContainer expand();
    }
    
    static final class LargeContainer extends AugmentationsItemsContainer
    {
        private final HashMap fAugmentations;
        
        LargeContainer() {
            this.fAugmentations = new HashMap();
        }
        
        public Object getItem(final Object o) {
            return this.fAugmentations.get(o);
        }
        
        public Object putItem(final Object o, final Object o2) {
            return this.fAugmentations.put(o, o2);
        }
        
        public Object removeItem(final Object o) {
            return this.fAugmentations.remove(o);
        }
        
        public Enumeration keys() {
            return Collections.enumeration((Collection<Object>)this.fAugmentations.keySet());
        }
        
        public void clear() {
            this.fAugmentations.clear();
        }
        
        public boolean isFull() {
            return false;
        }
        
        public AugmentationsItemsContainer expand() {
            return this;
        }
        
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("LargeContainer");
            final Iterator iterator = this.fAugmentations.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                sb.append("\nkey == ");
                sb.append(entry.getKey());
                sb.append("; value == ");
                sb.append(entry.getValue());
            }
            return sb.toString();
        }
    }
    
    static final class SmallContainer extends AugmentationsItemsContainer
    {
        static final int SIZE_LIMIT = 10;
        final Object[] fAugmentations;
        int fNumEntries;
        
        SmallContainer() {
            this.fAugmentations = new Object[20];
            this.fNumEntries = 0;
        }
        
        public Enumeration keys() {
            return new SmallContainerKeyEnumeration();
        }
        
        public Object getItem(final Object o) {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                if (this.fAugmentations[i].equals(o)) {
                    return this.fAugmentations[i + 1];
                }
            }
            return null;
        }
        
        public Object putItem(final Object o, final Object o2) {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                if (this.fAugmentations[i].equals(o)) {
                    final Object o3 = this.fAugmentations[i + 1];
                    this.fAugmentations[i + 1] = o2;
                    return o3;
                }
            }
            this.fAugmentations[this.fNumEntries * 2] = o;
            this.fAugmentations[this.fNumEntries * 2 + 1] = o2;
            ++this.fNumEntries;
            return null;
        }
        
        public Object removeItem(final Object o) {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                if (this.fAugmentations[i].equals(o)) {
                    final Object o2 = this.fAugmentations[i + 1];
                    for (int j = i; j < this.fNumEntries * 2 - 2; j += 2) {
                        this.fAugmentations[j] = this.fAugmentations[j + 2];
                        this.fAugmentations[j + 1] = this.fAugmentations[j + 3];
                    }
                    this.fAugmentations[this.fNumEntries * 2 - 2] = null;
                    this.fAugmentations[this.fNumEntries * 2 - 1] = null;
                    --this.fNumEntries;
                    return o2;
                }
            }
            return null;
        }
        
        public void clear() {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                this.fAugmentations[i] = null;
                this.fAugmentations[i + 1] = null;
            }
            this.fNumEntries = 0;
        }
        
        public boolean isFull() {
            return this.fNumEntries == 10;
        }
        
        public AugmentationsItemsContainer expand() {
            final LargeContainer largeContainer = new LargeContainer();
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                largeContainer.putItem(this.fAugmentations[i], this.fAugmentations[i + 1]);
            }
            return largeContainer;
        }
        
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("SmallContainer - fNumEntries == ").append(this.fNumEntries);
            for (int i = 0; i < 20; i += 2) {
                sb.append("\nfAugmentations[");
                sb.append(i);
                sb.append("] == ");
                sb.append(this.fAugmentations[i]);
                sb.append("; fAugmentations[");
                sb.append(i + 1);
                sb.append("] == ");
                sb.append(this.fAugmentations[i + 1]);
            }
            return sb.toString();
        }
        
        final class SmallContainerKeyEnumeration implements Enumeration
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
            
            public boolean hasMoreElements() {
                return this.next < this.enumArray.length;
            }
            
            public Object nextElement() {
                if (this.next >= this.enumArray.length) {
                    throw new NoSuchElementException();
                }
                final Object o = this.enumArray[this.next];
                this.enumArray[this.next] = null;
                ++this.next;
                return o;
            }
        }
    }
}
