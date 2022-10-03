package org.apache.lucene.search.join;

import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.index.BinaryDocValues;
import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.BytesRefHash;

abstract class TermsWithScoreCollector<DV> extends DocValuesTermsCollector<DV> implements GenericTermsCollector
{
    private static final int INITIAL_ARRAY_SIZE = 0;
    final BytesRefHash collectedTerms;
    final ScoreMode scoreMode;
    Scorer scorer;
    float[] scoreSums;
    
    TermsWithScoreCollector(final Function<DV> docValuesCall, final ScoreMode scoreMode) {
        super(docValuesCall);
        this.collectedTerms = new BytesRefHash();
        this.scoreSums = new float[0];
        this.scoreMode = scoreMode;
        if (scoreMode == ScoreMode.Min) {
            Arrays.fill(this.scoreSums, Float.POSITIVE_INFINITY);
        }
        else if (scoreMode == ScoreMode.Max) {
            Arrays.fill(this.scoreSums, Float.NEGATIVE_INFINITY);
        }
    }
    
    @Override
    public BytesRefHash getCollectedTerms() {
        return this.collectedTerms;
    }
    
    @Override
    public float[] getScoresPerTerm() {
        return this.scoreSums;
    }
    
    public void setScorer(final Scorer scorer) throws IOException {
        this.scorer = scorer;
    }
    
    static TermsWithScoreCollector<?> create(final String field, final boolean multipleValuesPerDocument, final ScoreMode scoreMode) {
        if (multipleValuesPerDocument) {
            switch (scoreMode) {
                case Avg: {
                    return new MV.Avg(DocValuesTermsCollector.sortedSetDocValues(field));
                }
                default: {
                    return new MV(DocValuesTermsCollector.sortedSetDocValues(field), scoreMode);
                }
            }
        }
        else {
            switch (scoreMode) {
                case Avg: {
                    return new SV.Avg(DocValuesTermsCollector.binaryDocValues(field));
                }
                default: {
                    return new SV(DocValuesTermsCollector.binaryDocValues(field), scoreMode);
                }
            }
        }
    }
    
    public boolean needsScores() {
        return true;
    }
    
    static class SV extends TermsWithScoreCollector<BinaryDocValues>
    {
        SV(final Function<BinaryDocValues> docValuesCall, final ScoreMode scoreMode) {
            super(docValuesCall, scoreMode);
        }
        
        public void collect(final int doc) throws IOException {
            int ord = this.collectedTerms.add(((BinaryDocValues)this.docValues).get(doc));
            if (ord < 0) {
                ord = -ord - 1;
            }
            else if (ord >= this.scoreSums.length) {
                final int begin = this.scoreSums.length;
                this.scoreSums = ArrayUtil.grow(this.scoreSums);
                if (this.scoreMode == ScoreMode.Min) {
                    Arrays.fill(this.scoreSums, begin, this.scoreSums.length, Float.POSITIVE_INFINITY);
                }
                else if (this.scoreMode == ScoreMode.Max) {
                    Arrays.fill(this.scoreSums, begin, this.scoreSums.length, Float.NEGATIVE_INFINITY);
                }
            }
            final float current = this.scorer.score();
            final float existing = this.scoreSums[ord];
            if (Float.compare(existing, 0.0f) == 0) {
                this.scoreSums[ord] = current;
            }
            else {
                switch (this.scoreMode) {
                    case Total: {
                        this.scoreSums[ord] += current;
                        break;
                    }
                    case Min: {
                        if (current < existing) {
                            this.scoreSums[ord] = current;
                            break;
                        }
                        break;
                    }
                    case Max: {
                        if (current > existing) {
                            this.scoreSums[ord] = current;
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new AssertionError((Object)("unexpected: " + this.scoreMode));
                    }
                }
            }
        }
        
        static class Avg extends SV
        {
            int[] scoreCounts;
            
            Avg(final Function<BinaryDocValues> docValuesCall) {
                super(docValuesCall, ScoreMode.Avg);
                this.scoreCounts = new int[0];
            }
            
            @Override
            public void collect(final int doc) throws IOException {
                int ord = this.collectedTerms.add(((BinaryDocValues)this.docValues).get(doc));
                if (ord < 0) {
                    ord = -ord - 1;
                }
                else if (ord >= this.scoreSums.length) {
                    this.scoreSums = ArrayUtil.grow(this.scoreSums);
                    this.scoreCounts = ArrayUtil.grow(this.scoreCounts);
                }
                final float current = this.scorer.score();
                final float existing = this.scoreSums[ord];
                if (Float.compare(existing, 0.0f) == 0) {
                    this.scoreSums[ord] = current;
                    this.scoreCounts[ord] = 1;
                }
                else {
                    this.scoreSums[ord] += current;
                    final int[] scoreCounts = this.scoreCounts;
                    final int n = ord;
                    ++scoreCounts[n];
                }
            }
            
            @Override
            public float[] getScoresPerTerm() {
                if (this.scoreCounts != null) {
                    for (int i = 0; i < this.scoreCounts.length; ++i) {
                        this.scoreSums[i] /= this.scoreCounts[i];
                    }
                    this.scoreCounts = null;
                }
                return this.scoreSums;
            }
        }
    }
    
    static class MV extends TermsWithScoreCollector<SortedSetDocValues>
    {
        MV(final Function<SortedSetDocValues> docValuesCall, final ScoreMode scoreMode) {
            super(docValuesCall, scoreMode);
        }
        
        public void collect(final int doc) throws IOException {
            ((SortedSetDocValues)this.docValues).setDocument(doc);
            long ord;
            while ((ord = ((SortedSetDocValues)this.docValues).nextOrd()) != -1L) {
                int termID = this.collectedTerms.add(((SortedSetDocValues)this.docValues).lookupOrd(ord));
                if (termID < 0) {
                    termID = -termID - 1;
                }
                else if (termID >= this.scoreSums.length) {
                    final int begin = this.scoreSums.length;
                    this.scoreSums = ArrayUtil.grow(this.scoreSums);
                    if (this.scoreMode == ScoreMode.Min) {
                        Arrays.fill(this.scoreSums, begin, this.scoreSums.length, Float.POSITIVE_INFINITY);
                    }
                    else if (this.scoreMode == ScoreMode.Max) {
                        Arrays.fill(this.scoreSums, begin, this.scoreSums.length, Float.NEGATIVE_INFINITY);
                    }
                }
                switch (this.scoreMode) {
                    case Total: {
                        final float[] scoreSums = this.scoreSums;
                        final int n = termID;
                        scoreSums[n] += this.scorer.score();
                        continue;
                    }
                    case Min: {
                        this.scoreSums[termID] = Math.min(this.scoreSums[termID], this.scorer.score());
                        continue;
                    }
                    case Max: {
                        this.scoreSums[termID] = Math.max(this.scoreSums[termID], this.scorer.score());
                        continue;
                    }
                    default: {
                        throw new AssertionError((Object)("unexpected: " + this.scoreMode));
                    }
                }
            }
        }
        
        static class Avg extends MV
        {
            int[] scoreCounts;
            
            Avg(final Function<SortedSetDocValues> docValuesCall) {
                super(docValuesCall, ScoreMode.Avg);
                this.scoreCounts = new int[0];
            }
            
            @Override
            public void collect(final int doc) throws IOException {
                ((SortedSetDocValues)this.docValues).setDocument(doc);
                long ord;
                while ((ord = ((SortedSetDocValues)this.docValues).nextOrd()) != -1L) {
                    int termID = this.collectedTerms.add(((SortedSetDocValues)this.docValues).lookupOrd(ord));
                    if (termID < 0) {
                        termID = -termID - 1;
                    }
                    else if (termID >= this.scoreSums.length) {
                        this.scoreSums = ArrayUtil.grow(this.scoreSums);
                        this.scoreCounts = ArrayUtil.grow(this.scoreCounts);
                    }
                    final float[] scoreSums = this.scoreSums;
                    final int n = termID;
                    scoreSums[n] += this.scorer.score();
                    final int[] scoreCounts = this.scoreCounts;
                    final int n2 = termID;
                    ++scoreCounts[n2];
                }
            }
            
            @Override
            public float[] getScoresPerTerm() {
                if (this.scoreCounts != null) {
                    for (int i = 0; i < this.scoreCounts.length; ++i) {
                        this.scoreSums[i] /= this.scoreCounts[i];
                    }
                    this.scoreCounts = null;
                }
                return this.scoreSums;
            }
        }
    }
}
