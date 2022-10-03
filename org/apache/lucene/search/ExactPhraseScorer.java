package org.apache.lucene.search;

import org.apache.lucene.index.PostingsEnum;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.search.similarities.Similarity;

final class ExactPhraseScorer extends Scorer
{
    private final ConjunctionDISI conjunction;
    private final PostingsAndPosition[] postings;
    private int freq;
    private final Similarity.SimScorer docScorer;
    private final boolean needsScores;
    private float matchCost;
    
    ExactPhraseScorer(final Weight weight, final PhraseQuery.PostingsAndFreq[] postings, final Similarity.SimScorer docScorer, final boolean needsScores, final float matchCost) throws IOException {
        super(weight);
        this.docScorer = docScorer;
        this.needsScores = needsScores;
        final List<DocIdSetIterator> iterators = new ArrayList<DocIdSetIterator>();
        final List<PostingsAndPosition> postingsAndPositions = new ArrayList<PostingsAndPosition>();
        for (final PhraseQuery.PostingsAndFreq posting : postings) {
            iterators.add(posting.postings);
            postingsAndPositions.add(new PostingsAndPosition(posting.postings, posting.position));
        }
        this.conjunction = ConjunctionDISI.intersectIterators(iterators);
        this.postings = postingsAndPositions.toArray(new PostingsAndPosition[postingsAndPositions.size()]);
        this.matchCost = matchCost;
    }
    
    @Override
    public TwoPhaseIterator twoPhaseIterator() {
        return new TwoPhaseIterator(this.conjunction) {
            @Override
            public boolean matches() throws IOException {
                return ExactPhraseScorer.this.phraseFreq() > 0;
            }
            
            @Override
            public float matchCost() {
                return ExactPhraseScorer.this.matchCost;
            }
        };
    }
    
    @Override
    public DocIdSetIterator iterator() {
        return TwoPhaseIterator.asDocIdSetIterator(this.twoPhaseIterator());
    }
    
    @Override
    public String toString() {
        return "ExactPhraseScorer(" + this.weight + ")";
    }
    
    @Override
    public int freq() {
        return this.freq;
    }
    
    @Override
    public int docID() {
        return this.conjunction.docID();
    }
    
    @Override
    public float score() {
        return this.docScorer.score(this.docID(), (float)this.freq);
    }
    
    private static boolean advancePosition(final PostingsAndPosition posting, final int target) throws IOException {
        while (posting.pos < target) {
            if (posting.upTo == posting.freq) {
                return false;
            }
            posting.pos = posting.postings.nextPosition();
            ++posting.upTo;
        }
        return true;
    }
    
    private int phraseFreq() throws IOException {
        final PostingsAndPosition[] arr$;
        final PostingsAndPosition[] postings = arr$ = this.postings;
        for (final PostingsAndPosition posting : arr$) {
            posting.freq = posting.postings.freq();
            posting.pos = posting.postings.nextPosition();
            posting.upTo = 1;
        }
        int freq = 0;
        final PostingsAndPosition lead = postings[0];
    Label_0212:
        while (true) {
            final int phrasePos = lead.pos - lead.offset;
            int j = 1;
            while (j < postings.length) {
                final PostingsAndPosition posting2 = postings[j];
                final int expectedPos = phrasePos + posting2.offset;
                if (!advancePosition(posting2, expectedPos)) {
                    break Label_0212;
                }
                if (posting2.pos != expectedPos) {
                    if (advancePosition(lead, posting2.pos - posting2.offset + lead.offset)) {
                        continue Label_0212;
                    }
                    break Label_0212;
                }
                else {
                    ++j;
                }
            }
            ++freq;
            if (!this.needsScores) {
                break;
            }
            if (lead.upTo == lead.freq) {
                break;
            }
            lead.pos = lead.postings.nextPosition();
            ++lead.upTo;
        }
        return this.freq = freq;
    }
    
    private static class PostingsAndPosition
    {
        private final PostingsEnum postings;
        private final int offset;
        private int freq;
        private int upTo;
        private int pos;
        
        public PostingsAndPosition(final PostingsEnum postings, final int offset) {
            this.postings = postings;
            this.offset = offset;
        }
    }
}
