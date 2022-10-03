package org.apache.xerces.jaxp.validation;

import java.lang.ref.SoftReference;
import java.lang.ref.Reference;
import org.apache.xerces.xni.grammars.XMLSchemaDescription;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import java.lang.ref.ReferenceQueue;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

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
    
    public SoftReferenceGrammarPool(final int n) {
        this.fGrammars = null;
        this.fGrammarCount = 0;
        this.fReferenceQueue = new ReferenceQueue();
        this.fGrammars = new Entry[n];
        this.fPoolIsLocked = false;
    }
    
    public Grammar[] retrieveInitialGrammarSet(final String s) {
        synchronized (this.fGrammars) {
            this.clean();
            return SoftReferenceGrammarPool.ZERO_LENGTH_GRAMMAR_ARRAY;
        }
    }
    
    public void cacheGrammars(final String s, final Grammar[] array) {
        if (!this.fPoolIsLocked) {
            for (int i = 0; i < array.length; ++i) {
                this.putGrammar(array[i]);
            }
        }
    }
    
    public Grammar retrieveGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        return this.getGrammar(xmlGrammarDescription);
    }
    
    public void putGrammar(final Grammar grammar) {
        if (!this.fPoolIsLocked) {
            synchronized (this.fGrammars) {
                this.clean();
                final XMLGrammarDescription grammarDescription = grammar.getGrammarDescription();
                final int hashCode = this.hashCode(grammarDescription);
                final int n = (hashCode & Integer.MAX_VALUE) % this.fGrammars.length;
                for (Entry next = this.fGrammars[n]; next != null; next = next.next) {
                    if (next.hash == hashCode && this.equals(next.desc, grammarDescription)) {
                        if (next.grammar.get() != grammar) {
                            next.grammar = new SoftGrammarReference(next, grammar, this.fReferenceQueue);
                        }
                        return;
                    }
                }
                this.fGrammars[n] = new Entry(hashCode, n, grammarDescription, grammar, this.fGrammars[n], this.fReferenceQueue);
                ++this.fGrammarCount;
            }
        }
    }
    
    public Grammar getGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        synchronized (this.fGrammars) {
            this.clean();
            final int hashCode = this.hashCode(xmlGrammarDescription);
            for (Entry next = this.fGrammars[(hashCode & Integer.MAX_VALUE) % this.fGrammars.length]; next != null; next = next.next) {
                final Grammar grammar = next.grammar.get();
                if (grammar == null) {
                    this.removeEntry(next);
                }
                else if (next.hash == hashCode && this.equals(next.desc, xmlGrammarDescription)) {
                    return grammar;
                }
            }
            return null;
        }
    }
    
    public Grammar removeGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        synchronized (this.fGrammars) {
            this.clean();
            final int hashCode = this.hashCode(xmlGrammarDescription);
            for (Entry next = this.fGrammars[(hashCode & Integer.MAX_VALUE) % this.fGrammars.length]; next != null; next = next.next) {
                if (next.hash == hashCode && this.equals(next.desc, xmlGrammarDescription)) {
                    return this.removeEntry(next);
                }
            }
            return null;
        }
    }
    
    public boolean containsGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        synchronized (this.fGrammars) {
            this.clean();
            final int hashCode = this.hashCode(xmlGrammarDescription);
            for (Entry next = this.fGrammars[(hashCode & Integer.MAX_VALUE) % this.fGrammars.length]; next != null; next = next.next) {
                if (next.grammar.get() == null) {
                    this.removeEntry(next);
                }
                else if (next.hash == hashCode && this.equals(next.desc, xmlGrammarDescription)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public void lockPool() {
        this.fPoolIsLocked = true;
    }
    
    public void unlockPool() {
        this.fPoolIsLocked = false;
    }
    
    public void clear() {
        for (int i = 0; i < this.fGrammars.length; ++i) {
            if (this.fGrammars[i] != null) {
                this.fGrammars[i].clear();
                this.fGrammars[i] = null;
            }
        }
        this.fGrammarCount = 0;
    }
    
    public boolean equals(final XMLGrammarDescription xmlGrammarDescription, final XMLGrammarDescription xmlGrammarDescription2) {
        if (!(xmlGrammarDescription instanceof XMLSchemaDescription)) {
            return xmlGrammarDescription.equals(xmlGrammarDescription2);
        }
        if (!(xmlGrammarDescription2 instanceof XMLSchemaDescription)) {
            return false;
        }
        final XMLSchemaDescription xmlSchemaDescription = (XMLSchemaDescription)xmlGrammarDescription;
        final XMLSchemaDescription xmlSchemaDescription2 = (XMLSchemaDescription)xmlGrammarDescription2;
        final String targetNamespace = xmlSchemaDescription.getTargetNamespace();
        if (targetNamespace != null) {
            if (!targetNamespace.equals(xmlSchemaDescription2.getTargetNamespace())) {
                return false;
            }
        }
        else if (xmlSchemaDescription2.getTargetNamespace() != null) {
            return false;
        }
        final String expandedSystemId = xmlSchemaDescription.getExpandedSystemId();
        if (expandedSystemId != null) {
            if (!expandedSystemId.equals(xmlSchemaDescription2.getExpandedSystemId())) {
                return false;
            }
        }
        else if (xmlSchemaDescription2.getExpandedSystemId() != null) {
            return false;
        }
        return true;
    }
    
    public int hashCode(final XMLGrammarDescription xmlGrammarDescription) {
        if (xmlGrammarDescription instanceof XMLSchemaDescription) {
            final XMLSchemaDescription xmlSchemaDescription = (XMLSchemaDescription)xmlGrammarDescription;
            final String targetNamespace = xmlSchemaDescription.getTargetNamespace();
            final String expandedSystemId = xmlSchemaDescription.getExpandedSystemId();
            return ((targetNamespace != null) ? targetNamespace.hashCode() : 0) ^ ((expandedSystemId != null) ? expandedSystemId.hashCode() : 0);
        }
        return xmlGrammarDescription.hashCode();
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
        for (Reference reference = this.fReferenceQueue.poll(); reference != null; reference = this.fReferenceQueue.poll()) {
            final Entry entry = ((SoftGrammarReference)reference).entry;
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
        
        protected Entry(final int hash, final int bucket, final XMLGrammarDescription desc, final Grammar grammar, final Entry next, final ReferenceQueue referenceQueue) {
            this.hash = hash;
            this.bucket = bucket;
            this.prev = null;
            this.next = next;
            if (next != null) {
                next.prev = this;
            }
            this.desc = desc;
            this.grammar = new SoftGrammarReference(this, grammar, referenceQueue);
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
        
        protected SoftGrammarReference(final Entry entry, final Grammar grammar, final ReferenceQueue referenceQueue) {
            super(grammar, referenceQueue);
            this.entry = entry;
        }
    }
}
