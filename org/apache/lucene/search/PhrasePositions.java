package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.PostingsEnum;

final class PhrasePositions
{
    int position;
    int count;
    int offset;
    final int ord;
    final PostingsEnum postings;
    PhrasePositions next;
    int rptGroup;
    int rptInd;
    final Term[] terms;
    
    PhrasePositions(final PostingsEnum postings, final int o, final int ord, final Term[] terms) {
        this.rptGroup = -1;
        this.postings = postings;
        this.offset = o;
        this.ord = ord;
        this.terms = terms;
    }
    
    final void firstPosition() throws IOException {
        this.count = this.postings.freq();
        this.nextPosition();
    }
    
    final boolean nextPosition() throws IOException {
        if (this.count-- > 0) {
            this.position = this.postings.nextPosition() - this.offset;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        String s = "o:" + this.offset + " p:" + this.position + " c:" + this.count;
        if (this.rptGroup >= 0) {
            s = s + " rpt:" + this.rptGroup + ",i" + this.rptInd;
        }
        return s;
    }
}
