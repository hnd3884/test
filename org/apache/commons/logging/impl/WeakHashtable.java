package org.apache.commons.logging.impl;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Enumeration;
import java.lang.ref.ReferenceQueue;
import java.util.Hashtable;

public final class WeakHashtable extends Hashtable
{
    private static final int MAX_CHANGES_BEFORE_PURGE = 100;
    private static final int PARTIAL_PURGE_COUNT = 10;
    private ReferenceQueue queue;
    private int changeCount;
    
    public WeakHashtable() {
        this.queue = new ReferenceQueue();
        this.changeCount = 0;
    }
    
    public boolean containsKey(final Object key) {
        final Referenced referenced = new Referenced(null, key);
        return super.containsKey(referenced);
    }
    
    public Enumeration elements() {
        this.purge();
        return super.elements();
    }
    
    public Set entrySet() {
        this.purge();
        final Set referencedEntries = super.entrySet();
        final Set unreferencedEntries = new HashSet();
        final Iterator it = referencedEntries.iterator();
        while (it.hasNext()) {
            final Map.Entry entry = (Map.Entry)it.next();
            final Referenced referencedKey = entry.getKey();
            final Object key = referencedKey.getValue();
            final Object value = entry.getValue();
            if (key != null) {
                final Entry dereferencedEntry = new Entry(null, key, value);
                unreferencedEntries.add(dereferencedEntry);
            }
        }
        return unreferencedEntries;
    }
    
    public Object get(final Object key) {
        final Referenced referenceKey = new Referenced(null, key);
        return super.get(referenceKey);
    }
    
    public boolean isEmpty() {
        this.purge();
        return super.isEmpty();
    }
    
    public Set keySet() {
        this.purge();
        final Set referencedKeys = super.keySet();
        final Set unreferencedKeys = new HashSet();
        final Iterator it = referencedKeys.iterator();
        while (it.hasNext()) {
            final Referenced referenceKey = it.next();
            final Object keyValue = referenceKey.getValue();
            if (keyValue != null) {
                unreferencedKeys.add(keyValue);
            }
        }
        return unreferencedKeys;
    }
    
    public Enumeration keys() {
        this.purge();
        final Enumeration enumer = super.keys();
        return new Enumeration() {
            private final /* synthetic */ Enumeration val$enumer = val$enumer;
            
            public boolean hasMoreElements() {
                return this.val$enumer.hasMoreElements();
            }
            
            public Object nextElement() {
                final Referenced nextReference = this.val$enumer.nextElement();
                return nextReference.getValue();
            }
        };
    }
    
    private void purge() {
        synchronized (this.queue) {
            WeakKey key;
            while ((key = (WeakKey)this.queue.poll()) != null) {
                super.remove(key.getReferenced());
            }
            monitorexit(this.queue);
        }
    }
    
    private void purgeOne() {
        synchronized (this.queue) {
            final WeakKey key = (WeakKey)this.queue.poll();
            if (key != null) {
                super.remove(key.getReferenced());
            }
            monitorexit(this.queue);
        }
    }
    
    public Object put(final Object key, final Object value) {
        if (key == null) {
            throw new NullPointerException("Null keys are not allowed");
        }
        if (value == null) {
            throw new NullPointerException("Null values are not allowed");
        }
        if (this.changeCount++ > 100) {
            this.purge();
            this.changeCount = 0;
        }
        else if (this.changeCount % 10 == 0) {
            this.purgeOne();
        }
        final Object result = null;
        final Referenced keyRef = new Referenced(null, key, this.queue);
        return super.put(keyRef, value);
    }
    
    public void putAll(final Map t) {
        if (t != null) {
            final Set entrySet = t.entrySet();
            final Iterator it = entrySet.iterator();
            while (it.hasNext()) {
                final Map.Entry entry = (Map.Entry)it.next();
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    protected void rehash() {
        this.purge();
        super.rehash();
    }
    
    public Object remove(final Object key) {
        if (this.changeCount++ > 100) {
            this.purge();
            this.changeCount = 0;
        }
        else if (this.changeCount % 10 == 0) {
            this.purgeOne();
        }
        return super.remove(new Referenced(null, key));
    }
    
    public int size() {
        this.purge();
        return super.size();
    }
    
    public String toString() {
        this.purge();
        return super.toString();
    }
    
    public Collection values() {
        this.purge();
        return super.values();
    }
    
    private static final class Entry implements Map.Entry
    {
        private final Object key;
        private final Object value;
        
        private Entry(final Object key, final Object value) {
            this.key = key;
            this.value = value;
        }
        
        public boolean equals(final Object o) {
            boolean result = false;
            if (o != null && o instanceof Map.Entry) {
                final Map.Entry entry = (Map.Entry)o;
                boolean b = false;
                Label_0095: {
                    Label_0090: {
                        Label_0054: {
                            boolean equals;
                            if (this.getKey() == null) {
                                if (entry.getKey() == null) {
                                    break Label_0054;
                                }
                                equals = false;
                            }
                            else {
                                equals = this.getKey().equals(entry.getKey());
                            }
                            if (!equals) {
                                break Label_0090;
                            }
                        }
                        Label_0094: {
                            boolean equals2;
                            if (this.getValue() == null) {
                                if (entry.getValue() == null) {
                                    break Label_0094;
                                }
                                equals2 = false;
                            }
                            else {
                                equals2 = this.getValue().equals(entry.getValue());
                            }
                            if (!equals2) {
                                break Label_0090;
                            }
                        }
                        b = true;
                        break Label_0095;
                    }
                    b = false;
                }
                result = b;
            }
            return result;
        }
        
        public Object getKey() {
            return this.key;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public int hashCode() {
            return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
        }
        
        public Object setValue(final Object value) {
            throw new UnsupportedOperationException("Entry.setValue is not supported.");
        }
    }
    
    private static final class Referenced
    {
        private final WeakReference reference;
        private final int hashCode;
        
        private Referenced(final Object referant) {
            this.reference = new WeakReference((T)referant);
            this.hashCode = referant.hashCode();
        }
        
        private Referenced(final Object key, final ReferenceQueue queue) {
            this.reference = new WeakKey(null, key, queue, this);
            this.hashCode = key.hashCode();
        }
        
        public boolean equals(final Object o) {
            boolean result = false;
            if (o instanceof Referenced) {
                final Referenced otherKey = (Referenced)o;
                final Object thisKeyValue = this.getValue();
                final Object otherKeyValue = otherKey.getValue();
                if (thisKeyValue == null) {
                    result = (otherKeyValue == null);
                    if (result) {
                        result = (this.hashCode() == otherKey.hashCode());
                    }
                }
                else {
                    result = thisKeyValue.equals(otherKeyValue);
                }
            }
            return result;
        }
        
        private Object getValue() {
            return this.reference.get();
        }
        
        public int hashCode() {
            return this.hashCode;
        }
    }
    
    private static final class WeakKey extends WeakReference
    {
        private final Referenced referenced;
        
        private WeakKey(final Object key, final ReferenceQueue queue, final Referenced referenced) {
            super(key, queue);
            this.referenced = referenced;
        }
        
        private Referenced getReferenced() {
            return this.referenced;
        }
    }
    
    static class 2
    {
    }
}
