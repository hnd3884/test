package org.apache.lucene.search;

import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import java.io.IOException;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;
import java.util.Map;
import org.apache.lucene.util.automaton.RunAutomaton;
import org.apache.lucene.util.PriorityQueue;

class TermAutomatonScorer extends Scorer
{
    private final TermAutomatonQuery.EnumAndScorer[] subs;
    private final TermAutomatonQuery.EnumAndScorer[] subsOnDoc;
    private final PriorityQueue<TermAutomatonQuery.EnumAndScorer> docIDQueue;
    private final PriorityQueue<TermAutomatonQuery.EnumAndScorer> posQueue;
    private final RunAutomaton runAutomaton;
    private final Map<Integer, BytesRef> idToTerm;
    private PosState[] positions;
    int posShift;
    private final int anyTermID;
    private final Similarity.SimScorer docScorer;
    private int numSubsOnDoc;
    private final long cost;
    private int docID;
    private int freq;
    
    public TermAutomatonScorer(final TermAutomatonQuery.TermAutomatonWeight weight, final TermAutomatonQuery.EnumAndScorer[] subs, final int anyTermID, final Map<Integer, BytesRef> idToTerm, final Similarity.SimScorer docScorer) throws IOException {
        super((Weight)weight);
        this.docID = -1;
        this.runAutomaton = new TermRunAutomaton(weight.automaton, subs.length);
        this.docScorer = docScorer;
        this.idToTerm = idToTerm;
        this.subs = subs;
        this.docIDQueue = new DocIDQueue(subs.length);
        this.posQueue = new PositionQueue(subs.length);
        this.anyTermID = anyTermID;
        this.subsOnDoc = new TermAutomatonQuery.EnumAndScorer[subs.length];
        this.positions = new PosState[4];
        for (int i = 0; i < this.positions.length; ++i) {
            this.positions[i] = new PosState();
        }
        long cost = 0L;
        for (final TermAutomatonQuery.EnumAndScorer sub : subs) {
            if (sub != null) {
                cost += sub.posEnum.cost();
                this.subsOnDoc[this.numSubsOnDoc++] = sub;
            }
        }
        this.cost = cost;
    }
    
    private void popCurrentDoc() {
        assert this.numSubsOnDoc == 0;
        assert this.docIDQueue.size() > 0;
        this.subsOnDoc[this.numSubsOnDoc++] = (TermAutomatonQuery.EnumAndScorer)this.docIDQueue.pop();
        this.docID = this.subsOnDoc[0].posEnum.docID();
        while (this.docIDQueue.size() > 0 && ((TermAutomatonQuery.EnumAndScorer)this.docIDQueue.top()).posEnum.docID() == this.docID) {
            this.subsOnDoc[this.numSubsOnDoc++] = (TermAutomatonQuery.EnumAndScorer)this.docIDQueue.pop();
        }
    }
    
    private void pushCurrentDoc() {
        for (int i = 0; i < this.numSubsOnDoc; ++i) {
            this.docIDQueue.add((Object)this.subsOnDoc[i]);
        }
        this.numSubsOnDoc = 0;
    }
    
    public DocIdSetIterator iterator() {
        return new DocIdSetIterator() {
            public int docID() {
                return TermAutomatonScorer.this.docID;
            }
            
            public long cost() {
                return TermAutomatonScorer.this.cost;
            }
            
            public int nextDoc() throws IOException {
                for (int i = 0; i < TermAutomatonScorer.this.numSubsOnDoc; ++i) {
                    final TermAutomatonQuery.EnumAndScorer sub = TermAutomatonScorer.this.subsOnDoc[i];
                    if (sub.posEnum.nextDoc() != Integer.MAX_VALUE) {
                        sub.posLeft = sub.posEnum.freq() - 1;
                        sub.pos = sub.posEnum.nextPosition();
                    }
                }
                TermAutomatonScorer.this.pushCurrentDoc();
                return this.doNext();
            }
            
            public int advance(final int target) throws IOException {
                if (TermAutomatonScorer.this.docIDQueue.size() > 0) {
                    for (TermAutomatonQuery.EnumAndScorer top = (TermAutomatonQuery.EnumAndScorer)TermAutomatonScorer.this.docIDQueue.top(); top.posEnum.docID() < target; top = (TermAutomatonQuery.EnumAndScorer)TermAutomatonScorer.this.docIDQueue.updateTop()) {
                        if (top.posEnum.advance(target) != Integer.MAX_VALUE) {
                            top.posLeft = top.posEnum.freq() - 1;
                            top.pos = top.posEnum.nextPosition();
                        }
                    }
                }
                for (int i = 0; i < TermAutomatonScorer.this.numSubsOnDoc; ++i) {
                    final TermAutomatonQuery.EnumAndScorer sub = TermAutomatonScorer.this.subsOnDoc[i];
                    if (sub.posEnum.advance(target) != Integer.MAX_VALUE) {
                        sub.posLeft = sub.posEnum.freq() - 1;
                        sub.pos = sub.posEnum.nextPosition();
                    }
                }
                TermAutomatonScorer.this.pushCurrentDoc();
                return this.doNext();
            }
            
            private int doNext() throws IOException {
                assert TermAutomatonScorer.this.numSubsOnDoc == 0;
                assert ((TermAutomatonQuery.EnumAndScorer)TermAutomatonScorer.this.docIDQueue.top()).posEnum.docID() > TermAutomatonScorer.this.docID;
                while (true) {
                    TermAutomatonScorer.this.popCurrentDoc();
                    if (TermAutomatonScorer.this.docID == Integer.MAX_VALUE) {
                        return TermAutomatonScorer.this.docID;
                    }
                    TermAutomatonScorer.this.countMatches();
                    if (TermAutomatonScorer.this.freq > 0) {
                        return TermAutomatonScorer.this.docID;
                    }
                    for (int i = 0; i < TermAutomatonScorer.this.numSubsOnDoc; ++i) {
                        final TermAutomatonQuery.EnumAndScorer sub = TermAutomatonScorer.this.subsOnDoc[i];
                        if (sub.posEnum.nextDoc() != Integer.MAX_VALUE) {
                            sub.posLeft = sub.posEnum.freq() - 1;
                            sub.pos = sub.posEnum.nextPosition();
                        }
                    }
                    TermAutomatonScorer.this.pushCurrentDoc();
                }
            }
        };
    }
    
    private PosState getPosition(final int pos) {
        return this.positions[pos - this.posShift];
    }
    
    private void shift(final int pos) {
        for (int limit = pos - this.posShift, i = 0; i < limit; ++i) {
            this.positions[i].count = 0;
        }
        this.posShift = pos;
    }
    
    private void countMatches() throws IOException {
        this.freq = 0;
        for (int i = 0; i < this.numSubsOnDoc; ++i) {
            this.posQueue.add((Object)this.subsOnDoc[i]);
        }
        int lastPos = -1;
        this.posShift = -1;
        while (this.posQueue.size() != 0) {
            final TermAutomatonQuery.EnumAndScorer sub = (TermAutomatonQuery.EnumAndScorer)this.posQueue.pop();
            final int pos = sub.pos;
            if (this.posShift == -1) {
                this.posShift = pos;
            }
            if (pos + 1 - this.posShift >= this.positions.length) {
                final PosState[] newPositions = new PosState[ArrayUtil.oversize(pos + 1 - this.posShift, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.positions, 0, newPositions, 0, this.positions.length);
                for (int j = this.positions.length; j < newPositions.length; ++j) {
                    newPositions[j] = new PosState();
                }
                this.positions = newPositions;
            }
            if (lastPos != -1 && this.anyTermID != -1) {
                final int startLastPos = lastPos;
                while (lastPos < pos) {
                    final PosState posState = this.getPosition(lastPos);
                    if (posState.count == 0 && lastPos > startLastPos) {
                        lastPos = pos;
                        break;
                    }
                    final PosState nextPosState = this.getPosition(lastPos + 1);
                    for (int k = 0; k < posState.count; ++k) {
                        final int state = this.runAutomaton.step(posState.states[k], this.anyTermID);
                        if (state != -1) {
                            nextPosState.add(state);
                        }
                    }
                    ++lastPos;
                }
            }
            PosState posState = this.getPosition(pos);
            PosState nextPosState = this.getPosition(pos + 1);
            if (posState.count == 0 && nextPosState.count == 0) {
                this.shift(pos);
                posState = this.getPosition(pos);
                nextPosState = this.getPosition(pos + 1);
            }
            for (int l = 0; l < posState.count; ++l) {
                final int state2 = this.runAutomaton.step(posState.states[l], sub.termID);
                if (state2 != -1) {
                    nextPosState.add(state2);
                    if (this.runAutomaton.isAccept(state2)) {
                        ++this.freq;
                    }
                }
            }
            final int state3 = this.runAutomaton.step(0, sub.termID);
            if (state3 != -1) {
                nextPosState.add(state3);
                if (this.runAutomaton.isAccept(state3)) {
                    ++this.freq;
                }
            }
            if (sub.posLeft > 0) {
                sub.pos = sub.posEnum.nextPosition();
                final TermAutomatonQuery.EnumAndScorer enumAndScorer = sub;
                --enumAndScorer.posLeft;
                this.posQueue.add((Object)sub);
            }
            lastPos = pos;
        }
        for (int limit = lastPos + 1 - this.posShift, m = 0; m <= limit; ++m) {
            this.positions[m].count = 0;
        }
    }
    
    public String toString() {
        return "TermAutomatonScorer(" + this.weight + ")";
    }
    
    public int freq() {
        return this.freq;
    }
    
    public int docID() {
        return this.docID;
    }
    
    public float score() {
        return this.docScorer.score(this.docID, (float)this.freq);
    }
    
    private static class DocIDQueue extends PriorityQueue<TermAutomatonQuery.EnumAndScorer>
    {
        public DocIDQueue(final int maxSize) {
            super(maxSize, false);
        }
        
        protected boolean lessThan(final TermAutomatonQuery.EnumAndScorer a, final TermAutomatonQuery.EnumAndScorer b) {
            return a.posEnum.docID() < b.posEnum.docID();
        }
    }
    
    private static class PositionQueue extends PriorityQueue<TermAutomatonQuery.EnumAndScorer>
    {
        public PositionQueue(final int maxSize) {
            super(maxSize, false);
        }
        
        protected boolean lessThan(final TermAutomatonQuery.EnumAndScorer a, final TermAutomatonQuery.EnumAndScorer b) {
            return a.pos < b.pos;
        }
    }
    
    static class TermRunAutomaton extends RunAutomaton
    {
        public TermRunAutomaton(final Automaton a, final int termCount) {
            super(a, termCount, true);
        }
    }
    
    private static class PosState
    {
        int[] states;
        int count;
        
        private PosState() {
            this.states = new int[2];
        }
        
        public void add(final int state) {
            if (this.states.length == this.count) {
                this.states = ArrayUtil.grow(this.states);
            }
            this.states[this.count++] = state;
        }
    }
}
