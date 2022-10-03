package org.apache.lucene.codecs.idversion;

import org.apache.lucene.index.TermState;
import org.apache.lucene.codecs.BlockTermState;

final class IDVersionTermState extends BlockTermState
{
    long idVersion;
    int docID;
    
    public IDVersionTermState clone() {
        final IDVersionTermState other = new IDVersionTermState();
        other.copyFrom((TermState)this);
        return other;
    }
    
    public void copyFrom(final TermState _other) {
        super.copyFrom(_other);
        final IDVersionTermState other = (IDVersionTermState)_other;
        this.idVersion = other.idVersion;
        this.docID = other.docID;
    }
}
