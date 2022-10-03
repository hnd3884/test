package org.apache.lucene.codecs;

import org.apache.lucene.index.TermState;
import org.apache.lucene.index.OrdTermState;

public class BlockTermState extends OrdTermState
{
    public int docFreq;
    public long totalTermFreq;
    public int termBlockOrd;
    public long blockFilePointer;
    public boolean isRealTerm;
    
    protected BlockTermState() {
        this.isRealTerm = true;
    }
    
    @Override
    public void copyFrom(final TermState _other) {
        assert _other instanceof BlockTermState : "can not copy from " + _other.getClass().getName();
        final BlockTermState other = (BlockTermState)_other;
        super.copyFrom(_other);
        this.docFreq = other.docFreq;
        this.totalTermFreq = other.totalTermFreq;
        this.termBlockOrd = other.termBlockOrd;
        this.blockFilePointer = other.blockFilePointer;
        this.isRealTerm = other.isRealTerm;
    }
    
    @Override
    public boolean isRealTerm() {
        return this.isRealTerm;
    }
    
    @Override
    public String toString() {
        return "docFreq=" + this.docFreq + " totalTermFreq=" + this.totalTermFreq + " termBlockOrd=" + this.termBlockOrd + " blockFP=" + this.blockFilePointer + " isRealTerm=" + this.isRealTerm;
    }
}
