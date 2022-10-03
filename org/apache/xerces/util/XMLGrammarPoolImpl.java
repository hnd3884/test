package org.apache.xerces.util;

import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

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
    
    public XMLGrammarPoolImpl(final int n) {
        this.fGrammars = null;
        this.fGrammarCount = 0;
        this.fGrammars = new Entry[n];
        this.fPoolIsLocked = false;
    }
    
    public Grammar[] retrieveInitialGrammarSet(final String s) {
        synchronized (this.fGrammars) {
            final int length = this.fGrammars.length;
            final Grammar[] array = new Grammar[this.fGrammarCount];
            int n = 0;
            for (int i = 0; i < length; ++i) {
                for (Entry next = this.fGrammars[i]; next != null; next = next.next) {
                    if (next.desc.getGrammarType().equals(s)) {
                        array[n++] = next.grammar;
                    }
                }
            }
            final Grammar[] array2 = new Grammar[n];
            System.arraycopy(array, 0, array2, 0, n);
            return array2;
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
                final XMLGrammarDescription grammarDescription = grammar.getGrammarDescription();
                final int hashCode = this.hashCode(grammarDescription);
                final int n = (hashCode & Integer.MAX_VALUE) % this.fGrammars.length;
                for (Entry next = this.fGrammars[n]; next != null; next = next.next) {
                    if (next.hash == hashCode && this.equals(next.desc, grammarDescription)) {
                        next.grammar = grammar;
                        return;
                    }
                }
                this.fGrammars[n] = new Entry(hashCode, grammarDescription, grammar, this.fGrammars[n]);
                ++this.fGrammarCount;
            }
        }
    }
    
    public Grammar getGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        synchronized (this.fGrammars) {
            final int hashCode = this.hashCode(xmlGrammarDescription);
            for (Entry next = this.fGrammars[(hashCode & Integer.MAX_VALUE) % this.fGrammars.length]; next != null; next = next.next) {
                if (next.hash == hashCode && this.equals(next.desc, xmlGrammarDescription)) {
                    return next.grammar;
                }
            }
            return null;
        }
    }
    
    public Grammar removeGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        synchronized (this.fGrammars) {
            final int hashCode = this.hashCode(xmlGrammarDescription);
            final int n = (hashCode & Integer.MAX_VALUE) % this.fGrammars.length;
            Entry next = this.fGrammars[n];
            Entry entry = null;
            while (next != null) {
                if (next.hash == hashCode && this.equals(next.desc, xmlGrammarDescription)) {
                    if (entry != null) {
                        entry.next = next.next;
                    }
                    else {
                        this.fGrammars[n] = next.next;
                    }
                    final Grammar grammar = next.grammar;
                    next.grammar = null;
                    --this.fGrammarCount;
                    return grammar;
                }
                entry = next;
                next = next.next;
            }
            return null;
        }
    }
    
    public boolean containsGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        synchronized (this.fGrammars) {
            final int hashCode = this.hashCode(xmlGrammarDescription);
            for (Entry next = this.fGrammars[(hashCode & Integer.MAX_VALUE) % this.fGrammars.length]; next != null; next = next.next) {
                if (next.hash == hashCode && this.equals(next.desc, xmlGrammarDescription)) {
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
        return xmlGrammarDescription.equals(xmlGrammarDescription2);
    }
    
    public int hashCode(final XMLGrammarDescription xmlGrammarDescription) {
        return xmlGrammarDescription.hashCode();
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
