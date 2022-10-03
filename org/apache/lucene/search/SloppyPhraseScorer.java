package org.apache.lucene.search;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Comparator;
import java.util.ArrayList;
import org.apache.lucene.index.Term;
import java.util.LinkedHashMap;
import org.apache.lucene.util.FixedBitSet;
import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.search.similarities.Similarity;

final class SloppyPhraseScorer extends Scorer
{
    private final ConjunctionDISI conjunction;
    private final PhrasePositions[] phrasePositions;
    private float sloppyFreq;
    private final Similarity.SimScorer docScorer;
    private final int slop;
    private final int numPostings;
    private final PhraseQueue pq;
    private int end;
    private boolean hasRpts;
    private boolean checkedRpts;
    private boolean hasMultiTermRpts;
    private PhrasePositions[][] rptGroups;
    private PhrasePositions[] rptStack;
    private int numMatches;
    final boolean needsScores;
    private final float matchCost;
    
    SloppyPhraseScorer(final Weight weight, final PhraseQuery.PostingsAndFreq[] postings, final int slop, final Similarity.SimScorer docScorer, final boolean needsScores, final float matchCost) {
        super(weight);
        this.docScorer = docScorer;
        this.needsScores = needsScores;
        this.slop = slop;
        this.numPostings = ((postings == null) ? 0 : postings.length);
        this.pq = new PhraseQueue(postings.length);
        final DocIdSetIterator[] iterators = new DocIdSetIterator[postings.length];
        this.phrasePositions = new PhrasePositions[postings.length];
        for (int i = 0; i < postings.length; ++i) {
            iterators[i] = postings[i].postings;
            this.phrasePositions[i] = new PhrasePositions(postings[i].postings, postings[i].position, i, postings[i].terms);
        }
        this.conjunction = ConjunctionDISI.intersectIterators(Arrays.asList(iterators));
        this.matchCost = matchCost;
    }
    
    private float phraseFreq() throws IOException {
        if (!this.initPhrasePositions()) {
            return 0.0f;
        }
        float freq = 0.0f;
        this.numMatches = 0;
        PhrasePositions pp = this.pq.pop();
        int matchLength = this.end - pp.position;
        int next = this.pq.top().position;
        while (this.advancePP(pp) && (!this.hasRpts || this.advanceRpts(pp))) {
            if (pp.position > next) {
                if (matchLength <= this.slop) {
                    freq += this.docScorer.computeSlopFactor(matchLength);
                    ++this.numMatches;
                    if (!this.needsScores) {
                        return freq;
                    }
                }
                this.pq.add(pp);
                pp = this.pq.pop();
                next = this.pq.top().position;
                matchLength = this.end - pp.position;
            }
            else {
                final int matchLength2 = this.end - pp.position;
                if (matchLength2 >= matchLength) {
                    continue;
                }
                matchLength = matchLength2;
            }
        }
        if (matchLength <= this.slop) {
            freq += this.docScorer.computeSlopFactor(matchLength);
            ++this.numMatches;
        }
        return freq;
    }
    
    private boolean advancePP(final PhrasePositions pp) throws IOException {
        if (!pp.nextPosition()) {
            return false;
        }
        if (pp.position > this.end) {
            this.end = pp.position;
        }
        return true;
    }
    
    private boolean advanceRpts(PhrasePositions pp) throws IOException {
        if (pp.rptGroup < 0) {
            return true;
        }
        final PhrasePositions[] rg = this.rptGroups[pp.rptGroup];
        FixedBitSet bits = new FixedBitSet(rg.length);
        final int k0 = pp.rptInd;
        int i;
        while ((i = this.collide(pp)) >= 0) {
            pp = this.lesser(pp, rg[i]);
            if (!this.advancePP(pp)) {
                return false;
            }
            if (i == k0) {
                continue;
            }
            bits = FixedBitSet.ensureCapacity(bits, i);
            bits.set(i);
        }
        int n = 0;
        final int numBits = bits.length();
        while (bits.cardinality() > 0) {
            final PhrasePositions pp2 = this.pq.pop();
            this.rptStack[n++] = pp2;
            if (pp2.rptGroup >= 0 && pp2.rptInd < numBits && bits.get(pp2.rptInd)) {
                bits.clear(pp2.rptInd);
            }
        }
        for (int j = n - 1; j >= 0; --j) {
            this.pq.add(this.rptStack[j]);
        }
        return true;
    }
    
    private PhrasePositions lesser(final PhrasePositions pp, final PhrasePositions pp2) {
        if (pp.position < pp2.position || (pp.position == pp2.position && pp.offset < pp2.offset)) {
            return pp;
        }
        return pp2;
    }
    
    private int collide(final PhrasePositions pp) {
        final int tpPos = this.tpPos(pp);
        final PhrasePositions[] rg = this.rptGroups[pp.rptGroup];
        for (int i = 0; i < rg.length; ++i) {
            final PhrasePositions pp2 = rg[i];
            if (pp2 != pp && this.tpPos(pp2) == tpPos) {
                return pp2.rptInd;
            }
        }
        return -1;
    }
    
    private boolean initPhrasePositions() throws IOException {
        this.end = Integer.MIN_VALUE;
        if (!this.checkedRpts) {
            return this.initFirstTime();
        }
        if (!this.hasRpts) {
            this.initSimple();
            return true;
        }
        return this.initComplex();
    }
    
    private void initSimple() throws IOException {
        this.pq.clear();
        for (final PhrasePositions pp : this.phrasePositions) {
            pp.firstPosition();
            if (pp.position > this.end) {
                this.end = pp.position;
            }
            this.pq.add(pp);
        }
    }
    
    private boolean initComplex() throws IOException {
        this.placeFirstPositions();
        if (!this.advanceRepeatGroups()) {
            return false;
        }
        this.fillQueue();
        return true;
    }
    
    private void placeFirstPositions() throws IOException {
        for (final PhrasePositions pp : this.phrasePositions) {
            pp.firstPosition();
        }
    }
    
    private void fillQueue() {
        this.pq.clear();
        for (final PhrasePositions pp : this.phrasePositions) {
            if (pp.position > this.end) {
                this.end = pp.position;
            }
            this.pq.add(pp);
        }
    }
    
    private boolean advanceRepeatGroups() throws IOException {
        for (final PhrasePositions[] rg : this.rptGroups) {
            if (this.hasMultiTermRpts) {
                int incr;
                for (int i = 0; i < rg.length; i += incr) {
                    incr = 1;
                    final PhrasePositions pp = rg[i];
                    int k;
                    while ((k = this.collide(pp)) >= 0) {
                        final PhrasePositions pp2 = this.lesser(pp, rg[k]);
                        if (!this.advancePP(pp2)) {
                            return false;
                        }
                        if (pp2.rptInd < i) {
                            incr = 0;
                            break;
                        }
                    }
                }
            }
            else {
                for (int j = 1; j < rg.length; ++j) {
                    for (int l = 0; l < j; ++l) {
                        if (!rg[j].nextPosition()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private boolean initFirstTime() throws IOException {
        this.checkedRpts = true;
        this.placeFirstPositions();
        final LinkedHashMap<Term, Integer> rptTerms = this.repeatingTerms();
        this.hasRpts = !rptTerms.isEmpty();
        if (this.hasRpts) {
            this.rptStack = new PhrasePositions[this.numPostings];
            final ArrayList<ArrayList<PhrasePositions>> rgs = this.gatherRptGroups(rptTerms);
            this.sortRptGroups(rgs);
            if (!this.advanceRepeatGroups()) {
                return false;
            }
        }
        this.fillQueue();
        return true;
    }
    
    private void sortRptGroups(final ArrayList<ArrayList<PhrasePositions>> rgs) {
        this.rptGroups = new PhrasePositions[rgs.size()][];
        final Comparator<PhrasePositions> cmprtr = new Comparator<PhrasePositions>() {
            @Override
            public int compare(final PhrasePositions pp1, final PhrasePositions pp2) {
                return pp1.offset - pp2.offset;
            }
        };
        for (int i = 0; i < this.rptGroups.length; ++i) {
            final PhrasePositions[] rg = (PhrasePositions[])rgs.get(i).toArray(new PhrasePositions[0]);
            Arrays.sort(rg, cmprtr);
            this.rptGroups[i] = rg;
            for (int j = 0; j < rg.length; ++j) {
                rg[j].rptInd = j;
            }
        }
    }
    
    private ArrayList<ArrayList<PhrasePositions>> gatherRptGroups(final LinkedHashMap<Term, Integer> rptTerms) throws IOException {
        final PhrasePositions[] rpp = this.repeatingPPs(rptTerms);
        final ArrayList<ArrayList<PhrasePositions>> res = new ArrayList<ArrayList<PhrasePositions>>();
        if (!this.hasMultiTermRpts) {
            for (int i = 0; i < rpp.length; ++i) {
                final PhrasePositions pp = rpp[i];
                if (pp.rptGroup < 0) {
                    final int tpPos = this.tpPos(pp);
                    for (int j = i + 1; j < rpp.length; ++j) {
                        final PhrasePositions pp2 = rpp[j];
                        if (pp2.rptGroup < 0 && pp2.offset != pp.offset) {
                            if (this.tpPos(pp2) == tpPos) {
                                int g = pp.rptGroup;
                                if (g < 0) {
                                    g = res.size();
                                    pp.rptGroup = g;
                                    final ArrayList<PhrasePositions> rl = new ArrayList<PhrasePositions>(2);
                                    rl.add(pp);
                                    res.add(rl);
                                }
                                pp2.rptGroup = g;
                                res.get(g).add(pp2);
                            }
                        }
                    }
                }
            }
        }
        else {
            final ArrayList<HashSet<PhrasePositions>> tmp = new ArrayList<HashSet<PhrasePositions>>();
            final ArrayList<FixedBitSet> bb = this.ppTermsBitSets(rpp, rptTerms);
            this.unionTermGroups(bb);
            final HashMap<Term, Integer> tg = this.termGroups(rptTerms, bb);
            final HashSet<Integer> distinctGroupIDs = new HashSet<Integer>(tg.values());
            for (int k = 0; k < distinctGroupIDs.size(); ++k) {
                tmp.add(new HashSet<PhrasePositions>());
            }
            for (final PhrasePositions pp3 : rpp) {
                for (final Term t : pp3.terms) {
                    if (rptTerms.containsKey(t)) {
                        final int g2 = tg.get(t);
                        tmp.get(g2).add(pp3);
                        assert pp3.rptGroup == g2;
                        pp3.rptGroup = g2;
                    }
                }
            }
            for (final HashSet<PhrasePositions> hs : tmp) {
                res.add(new ArrayList<PhrasePositions>(hs));
            }
        }
        return res;
    }
    
    private final int tpPos(final PhrasePositions pp) {
        return pp.position + pp.offset;
    }
    
    private LinkedHashMap<Term, Integer> repeatingTerms() {
        final LinkedHashMap<Term, Integer> tord = new LinkedHashMap<Term, Integer>();
        final HashMap<Term, Integer> tcnt = new HashMap<Term, Integer>();
        for (final PhrasePositions pp : this.phrasePositions) {
            for (final Term t : pp.terms) {
                final Integer cnt0 = tcnt.get(t);
                final Integer cnt2 = (cnt0 == null) ? new Integer(1) : new Integer(1 + cnt0);
                tcnt.put(t, cnt2);
                if (cnt2 == 2) {
                    tord.put(t, tord.size());
                }
            }
        }
        return tord;
    }
    
    private PhrasePositions[] repeatingPPs(final HashMap<Term, Integer> rptTerms) {
        final ArrayList<PhrasePositions> rp = new ArrayList<PhrasePositions>();
        for (final PhrasePositions pp : this.phrasePositions) {
            for (final Term t : pp.terms) {
                if (rptTerms.containsKey(t)) {
                    rp.add(pp);
                    this.hasMultiTermRpts |= (pp.terms.length > 1);
                    break;
                }
            }
        }
        return rp.toArray(new PhrasePositions[0]);
    }
    
    private ArrayList<FixedBitSet> ppTermsBitSets(final PhrasePositions[] rpp, final HashMap<Term, Integer> tord) {
        final ArrayList<FixedBitSet> bb = new ArrayList<FixedBitSet>(rpp.length);
        for (final PhrasePositions pp : rpp) {
            final FixedBitSet b = new FixedBitSet(tord.size());
            for (final Term t : pp.terms) {
                final Integer ord;
                if ((ord = tord.get(t)) != null) {
                    b.set(ord);
                }
            }
            bb.add(b);
        }
        return bb;
    }
    
    private void unionTermGroups(final ArrayList<FixedBitSet> bb) {
        int incr;
        for (int i = 0; i < bb.size() - 1; i += incr) {
            incr = 1;
            int j = i + 1;
            while (j < bb.size()) {
                if (bb.get(i).intersects(bb.get(j))) {
                    bb.get(i).or(bb.get(j));
                    bb.remove(j);
                    incr = 0;
                }
                else {
                    ++j;
                }
            }
        }
    }
    
    private HashMap<Term, Integer> termGroups(final LinkedHashMap<Term, Integer> tord, final ArrayList<FixedBitSet> bb) throws IOException {
        final HashMap<Term, Integer> tg = new HashMap<Term, Integer>();
        final Term[] t = tord.keySet().toArray(new Term[0]);
        for (int i = 0; i < bb.size(); ++i) {
            final FixedBitSet bits = bb.get(i);
            for (int ord = bits.nextSetBit(0); ord != Integer.MAX_VALUE; ord = ((ord + 1 >= bits.length()) ? Integer.MAX_VALUE : bits.nextSetBit(ord + 1))) {
                tg.put(t[ord], i);
            }
        }
        return tg;
    }
    
    @Override
    public int freq() {
        return this.numMatches;
    }
    
    float sloppyFreq() {
        return this.sloppyFreq;
    }
    
    @Override
    public int docID() {
        return this.conjunction.docID();
    }
    
    @Override
    public float score() {
        return this.docScorer.score(this.docID(), this.sloppyFreq);
    }
    
    @Override
    public String toString() {
        return "scorer(" + this.weight + ")";
    }
    
    @Override
    public TwoPhaseIterator twoPhaseIterator() {
        return new TwoPhaseIterator(this.conjunction) {
            @Override
            public boolean matches() throws IOException {
                SloppyPhraseScorer.this.sloppyFreq = SloppyPhraseScorer.this.phraseFreq();
                return SloppyPhraseScorer.this.sloppyFreq != 0.0f;
            }
            
            @Override
            public float matchCost() {
                return SloppyPhraseScorer.this.matchCost;
            }
            
            @Override
            public String toString() {
                return "SloppyPhraseScorer@asTwoPhaseIterator(" + SloppyPhraseScorer.this + ")";
            }
        };
    }
    
    @Override
    public DocIdSetIterator iterator() {
        return TwoPhaseIterator.asDocIdSetIterator(this.twoPhaseIterator());
    }
}
