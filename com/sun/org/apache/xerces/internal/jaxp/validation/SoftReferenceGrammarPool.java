package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.lang.ref.SoftReference;
import java.lang.ref.Reference;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import java.lang.ref.ReferenceQueue;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class SoftReferenceGrammarPool implements XMLGrammarPool
{
    protected static final int TABLE_SIZE = 11;
    protected static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY;
    protected Entry[] fGrammars;
    protected boolean fPoolIsLocked;
    protected int fGrammarCount;
    protected final ReferenceQueue fReferenceQueue;
    
    public SoftReferenceGrammarPool() {
        this.fGrammars = null;
        this.fGrammarCount = 0;
        this.fReferenceQueue = new ReferenceQueue();
        this.fGrammars = new Entry[11];
        this.fPoolIsLocked = false;
    }
    
    public SoftReferenceGrammarPool(final int initialCapacity) {
        this.fGrammars = null;
        this.fGrammarCount = 0;
        this.fReferenceQueue = new ReferenceQueue();
        this.fGrammars = new Entry[initialCapacity];
        this.fPoolIsLocked = false;
    }
    
    @Override
    public Grammar[] retrieveInitialGrammarSet(final String grammarType) {
        synchronized (this.fGrammars) {
            this.clean();
            return SoftReferenceGrammarPool.ZERO_LENGTH_GRAMMAR_ARRAY;
        }
    }
    
    @Override
    public void cacheGrammars(final String grammarType, final Grammar[] grammars) {
        if (!this.fPoolIsLocked) {
            for (int i = 0; i < grammars.length; ++i) {
                this.putGrammar(grammars[i]);
            }
        }
    }
    
    @Override
    public Grammar retrieveGrammar(final XMLGrammarDescription desc) {
        return this.getGrammar(desc);
    }
    
    public void putGrammar(final Grammar grammar) {
        if (!this.fPoolIsLocked) {
            synchronized (this.fGrammars) {
                this.clean();
                final XMLGrammarDescription desc = grammar.getGrammarDescription();
                final int hash = this.hashCode(desc);
                final int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;
                for (Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
                    if (entry.hash == hash && this.equals(entry.desc, desc)) {
                        if (entry.grammar.get() != grammar) {
                            entry.grammar = new SoftGrammarReference(entry, grammar, this.fReferenceQueue);
                        }
                        return;
                    }
                }
                Entry entry = new Entry(hash, index, desc, grammar, this.fGrammars[index], this.fReferenceQueue);
                this.fGrammars[index] = entry;
                ++this.fGrammarCount;
            }
        }
    }
    
    public Grammar getGrammar(final XMLGrammarDescription desc) {
        synchronized (this.fGrammars) {
            this.clean();
            final int hash = this.hashCode(desc);
            final int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;
            for (Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
                final Grammar tempGrammar = entry.grammar.get();
                if (tempGrammar == null) {
                    this.removeEntry(entry);
                }
                else if (entry.hash == hash && this.equals(entry.desc, desc)) {
                    return tempGrammar;
                }
            }
            return null;
        }
    }
    
    public Grammar removeGrammar(final XMLGrammarDescription desc) {
        synchronized (this.fGrammars) {
            this.clean();
            final int hash = this.hashCode(desc);
            final int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;
            for (Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
                if (entry.hash == hash && this.equals(entry.desc, desc)) {
                    return this.removeEntry(entry);
                }
            }
            return null;
        }
    }
    
    public boolean containsGrammar(final XMLGrammarDescription desc) {
        synchronized (this.fGrammars) {
            this.clean();
            final int hash = this.hashCode(desc);
            final int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;
            for (Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
                final Grammar tempGrammar = entry.grammar.get();
                if (tempGrammar == null) {
                    this.removeEntry(entry);
                }
                else if (entry.hash == hash && this.equals(entry.desc, desc)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public void lockPool() {
        this.fPoolIsLocked = true;
    }
    
    @Override
    public void unlockPool() {
        this.fPoolIsLocked = false;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < this.fGrammars.length; ++i) {
            if (this.fGrammars[i] != null) {
                this.fGrammars[i].clear();
                this.fGrammars[i] = null;
            }
        }
        this.fGrammarCount = 0;
    }
    
    public boolean equals(final XMLGrammarDescription desc1, final XMLGrammarDescription desc2) {
        if (!(desc1 instanceof XMLSchemaDescription)) {
            return desc1.equals(desc2);
        }
        if (!(desc2 instanceof XMLSchemaDescription)) {
            return false;
        }
        final XMLSchemaDescription sd1 = (XMLSchemaDescription)desc1;
        final XMLSchemaDescription sd2 = (XMLSchemaDescription)desc2;
        final String targetNamespace = sd1.getTargetNamespace();
        if (targetNamespace != null) {
            if (!targetNamespace.equals(sd2.getTargetNamespace())) {
                return false;
            }
        }
        else if (sd2.getTargetNamespace() != null) {
            return false;
        }
        final String expandedSystemId = sd1.getExpandedSystemId();
        if (expandedSystemId != null) {
            if (!expandedSystemId.equals(sd2.getExpandedSystemId())) {
                return false;
            }
        }
        else if (sd2.getExpandedSystemId() != null) {
            return false;
        }
        return true;
    }
    
    public int hashCode(final XMLGrammarDescription desc) {
        if (desc instanceof XMLSchemaDescription) {
            final XMLSchemaDescription sd = (XMLSchemaDescription)desc;
            final String targetNamespace = sd.getTargetNamespace();
            final String expandedSystemId = sd.getExpandedSystemId();
            int hash = (targetNamespace != null) ? targetNamespace.hashCode() : 0;
            hash ^= ((expandedSystemId != null) ? expandedSystemId.hashCode() : 0);
            return hash;
        }
        return desc.hashCode();
    }
    
    private Grammar removeEntry(final Entry entry) {
        if (entry.prev != null) {
            entry.prev.next = entry.next;
        }
        else {
            this.fGrammars[entry.bucket] = entry.next;
        }
        if (entry.next != null) {
            entry.next.prev = entry.prev;
        }
        --this.fGrammarCount;
        entry.grammar.entry = null;
        return entry.grammar.get();
    }
    
    private void clean() {
        for (Reference ref = this.fReferenceQueue.poll(); ref != null; ref = this.fReferenceQueue.poll()) {
            final Entry entry = ((SoftGrammarReference)ref).entry;
            if (entry != null) {
                this.removeEntry(entry);
            }
        }
    }
    
    static {
        ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];
    }
    
    static final class Entry
    {
        public int hash;
        public int bucket;
        public Entry prev;
        public Entry next;
        public XMLGrammarDescription desc;
        public SoftGrammarReference grammar;
        
        protected Entry(final int hash, final int bucket, final XMLGrammarDescription desc, final Grammar grammar, final Entry next, final ReferenceQueue queue) {
            this.hash = hash;
            this.bucket = bucket;
            this.prev = null;
            this.next = next;
            if (next != null) {
                next.prev = this;
            }
            this.desc = desc;
            this.grammar = new SoftGrammarReference(this, grammar, queue);
        }
        
        protected void clear() {
            this.desc = null;
            this.grammar = null;
            if (this.next != null) {
                this.next.clear();
                this.next = null;
            }
        }
    }
    
    static final class SoftGrammarReference extends SoftReference
    {
        public Entry entry;
        
        protected SoftGrammarReference(final Entry entry, final Grammar grammar, final ReferenceQueue queue) {
            super(grammar, queue);
            this.entry = entry;
        }
    }
}
