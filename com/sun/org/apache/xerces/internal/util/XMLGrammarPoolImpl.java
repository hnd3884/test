package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public class XMLGrammarPoolImpl implements XMLGrammarPool
{
    protected static final int TABLE_SIZE = 11;
    protected Entry[] fGrammars;
    protected boolean fPoolIsLocked;
    protected int fGrammarCount;
    private static final boolean DEBUG = false;
    
    public XMLGrammarPoolImpl() {
        this.fGrammars = null;
        this.fGrammarCount = 0;
        this.fGrammars = new Entry[11];
        this.fPoolIsLocked = false;
    }
    
    public XMLGrammarPoolImpl(final int initialCapacity) {
        this.fGrammars = null;
        this.fGrammarCount = 0;
        this.fGrammars = new Entry[initialCapacity];
        this.fPoolIsLocked = false;
    }
    
    @Override
    public Grammar[] retrieveInitialGrammarSet(final String grammarType) {
        synchronized (this.fGrammars) {
            final int grammarSize = this.fGrammars.length;
            final Grammar[] tempGrammars = new Grammar[this.fGrammarCount];
            int pos = 0;
            for (int i = 0; i < grammarSize; ++i) {
                for (Entry e = this.fGrammars[i]; e != null; e = e.next) {
                    if (e.desc.getGrammarType().equals(grammarType)) {
                        tempGrammars[pos++] = e.grammar;
                    }
                }
            }
            final Grammar[] toReturn = new Grammar[pos];
            System.arraycopy(tempGrammars, 0, toReturn, 0, pos);
            return toReturn;
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
                final XMLGrammarDescription desc = grammar.getGrammarDescription();
                final int hash = this.hashCode(desc);
                final int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;
                for (Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
                    if (entry.hash == hash && this.equals(entry.desc, desc)) {
                        entry.grammar = grammar;
                        return;
                    }
                }
                Entry entry = new Entry(hash, desc, grammar, this.fGrammars[index]);
                this.fGrammars[index] = entry;
                ++this.fGrammarCount;
            }
        }
    }
    
    public Grammar getGrammar(final XMLGrammarDescription desc) {
        synchronized (this.fGrammars) {
            final int hash = this.hashCode(desc);
            final int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;
            for (Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
                if (entry.hash == hash && this.equals(entry.desc, desc)) {
                    return entry.grammar;
                }
            }
            return null;
        }
    }
    
    public Grammar removeGrammar(final XMLGrammarDescription desc) {
        synchronized (this.fGrammars) {
            final int hash = this.hashCode(desc);
            final int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;
            Entry entry = this.fGrammars[index];
            Entry prev = null;
            while (entry != null) {
                if (entry.hash == hash && this.equals(entry.desc, desc)) {
                    if (prev != null) {
                        prev.next = entry.next;
                    }
                    else {
                        this.fGrammars[index] = entry.next;
                    }
                    final Grammar tempGrammar = entry.grammar;
                    entry.grammar = null;
                    --this.fGrammarCount;
                    return tempGrammar;
                }
                prev = entry;
                entry = entry.next;
            }
            return null;
        }
    }
    
    public boolean containsGrammar(final XMLGrammarDescription desc) {
        synchronized (this.fGrammars) {
            final int hash = this.hashCode(desc);
            final int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;
            for (Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
                if (entry.hash == hash && this.equals(entry.desc, desc)) {
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
        return desc1.equals(desc2);
    }
    
    public int hashCode(final XMLGrammarDescription desc) {
        return desc.hashCode();
    }
    
    protected static final class Entry
    {
        public int hash;
        public XMLGrammarDescription desc;
        public Grammar grammar;
        public Entry next;
        
        protected Entry(final int hash, final XMLGrammarDescription desc, final Grammar grammar, final Entry next) {
            this.hash = hash;
            this.desc = desc;
            this.grammar = grammar;
            this.next = next;
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
}
