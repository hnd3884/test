package org.apache.lucene.search.spell;

import org.apache.lucene.index.Terms;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.util.BytesRef;
import java.util.PriorityQueue;
import org.apache.lucene.search.FuzzyTermsEnum;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.search.MaxNonCompetitiveBoostAttribute;
import org.apache.lucene.util.AttributeSource;
import java.util.Iterator;
import org.apache.lucene.util.ArrayUtil;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import org.apache.lucene.util.CharsRefBuilder;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import java.util.Comparator;

public class DirectSpellChecker
{
    public static final StringDistance INTERNAL_LEVENSHTEIN;
    private int maxEdits;
    private int minPrefix;
    private int maxInspections;
    private float accuracy;
    private float thresholdFrequency;
    private int minQueryLength;
    private float maxQueryFrequency;
    private boolean lowerCaseTerms;
    private Comparator<SuggestWord> comparator;
    private StringDistance distance;
    
    public DirectSpellChecker() {
        this.maxEdits = 2;
        this.minPrefix = 1;
        this.maxInspections = 5;
        this.accuracy = 0.5f;
        this.thresholdFrequency = 0.0f;
        this.minQueryLength = 4;
        this.maxQueryFrequency = 0.01f;
        this.lowerCaseTerms = true;
        this.comparator = SuggestWordQueue.DEFAULT_COMPARATOR;
        this.distance = DirectSpellChecker.INTERNAL_LEVENSHTEIN;
    }
    
    public int getMaxEdits() {
        return this.maxEdits;
    }
    
    public void setMaxEdits(final int maxEdits) {
        if (maxEdits < 1 || maxEdits > 2) {
            throw new UnsupportedOperationException("Invalid maxEdits");
        }
        this.maxEdits = maxEdits;
    }
    
    public int getMinPrefix() {
        return this.minPrefix;
    }
    
    public void setMinPrefix(final int minPrefix) {
        this.minPrefix = minPrefix;
    }
    
    public int getMaxInspections() {
        return this.maxInspections;
    }
    
    public void setMaxInspections(final int maxInspections) {
        this.maxInspections = maxInspections;
    }
    
    public float getAccuracy() {
        return this.accuracy;
    }
    
    public void setAccuracy(final float accuracy) {
        this.accuracy = accuracy;
    }
    
    public float getThresholdFrequency() {
        return this.thresholdFrequency;
    }
    
    public void setThresholdFrequency(final float thresholdFrequency) {
        if (thresholdFrequency >= 1.0f && thresholdFrequency != (int)thresholdFrequency) {
            throw new IllegalArgumentException("Fractional absolute document frequencies are not allowed");
        }
        this.thresholdFrequency = thresholdFrequency;
    }
    
    public int getMinQueryLength() {
        return this.minQueryLength;
    }
    
    public void setMinQueryLength(final int minQueryLength) {
        this.minQueryLength = minQueryLength;
    }
    
    public float getMaxQueryFrequency() {
        return this.maxQueryFrequency;
    }
    
    public void setMaxQueryFrequency(final float maxQueryFrequency) {
        if (maxQueryFrequency >= 1.0f && maxQueryFrequency != (int)maxQueryFrequency) {
            throw new IllegalArgumentException("Fractional absolute document frequencies are not allowed");
        }
        this.maxQueryFrequency = maxQueryFrequency;
    }
    
    public boolean getLowerCaseTerms() {
        return this.lowerCaseTerms;
    }
    
    public void setLowerCaseTerms(final boolean lowerCaseTerms) {
        this.lowerCaseTerms = lowerCaseTerms;
    }
    
    public Comparator<SuggestWord> getComparator() {
        return this.comparator;
    }
    
    public void setComparator(final Comparator<SuggestWord> comparator) {
        this.comparator = comparator;
    }
    
    public StringDistance getDistance() {
        return this.distance;
    }
    
    public void setDistance(final StringDistance distance) {
        this.distance = distance;
    }
    
    public SuggestWord[] suggestSimilar(final Term term, final int numSug, final IndexReader ir) throws IOException {
        return this.suggestSimilar(term, numSug, ir, SuggestMode.SUGGEST_WHEN_NOT_IN_INDEX);
    }
    
    public SuggestWord[] suggestSimilar(final Term term, final int numSug, final IndexReader ir, final SuggestMode suggestMode) throws IOException {
        return this.suggestSimilar(term, numSug, ir, suggestMode, this.accuracy);
    }
    
    public SuggestWord[] suggestSimilar(Term term, final int numSug, final IndexReader ir, final SuggestMode suggestMode, final float accuracy) throws IOException {
        final CharsRefBuilder spare = new CharsRefBuilder();
        final String text = term.text();
        if (this.minQueryLength > 0 && text.codePointCount(0, text.length()) < this.minQueryLength) {
            return new SuggestWord[0];
        }
        if (this.lowerCaseTerms) {
            term = new Term(term.field(), text.toLowerCase(Locale.ROOT));
        }
        int docfreq = ir.docFreq(term);
        if (suggestMode == SuggestMode.SUGGEST_WHEN_NOT_IN_INDEX && docfreq > 0) {
            return new SuggestWord[0];
        }
        final int maxDoc = ir.maxDoc();
        if (this.maxQueryFrequency >= 1.0f && docfreq > this.maxQueryFrequency) {
            return new SuggestWord[0];
        }
        if (docfreq > (int)Math.ceil(this.maxQueryFrequency * maxDoc)) {
            return new SuggestWord[0];
        }
        if (suggestMode != SuggestMode.SUGGEST_MORE_POPULAR) {
            docfreq = 0;
        }
        if (this.thresholdFrequency >= 1.0f) {
            docfreq = Math.max(docfreq, (int)this.thresholdFrequency);
        }
        else if (this.thresholdFrequency > 0.0f) {
            docfreq = Math.max(docfreq, (int)(this.thresholdFrequency * maxDoc) - 1);
        }
        Collection<ScoreTerm> terms = null;
        final int inspections = numSug * this.maxInspections;
        terms = this.suggestSimilar(term, inspections, ir, docfreq, 1, accuracy, spare);
        if (this.maxEdits > 1 && terms.size() < inspections) {
            final HashSet<ScoreTerm> moreTerms = new HashSet<ScoreTerm>();
            moreTerms.addAll((Collection<?>)terms);
            moreTerms.addAll((Collection<?>)this.suggestSimilar(term, inspections, ir, docfreq, this.maxEdits, accuracy, spare));
            terms = moreTerms;
        }
        SuggestWord[] suggestions = new SuggestWord[terms.size()];
        int index = suggestions.length - 1;
        for (final ScoreTerm s : terms) {
            final SuggestWord suggestion = new SuggestWord();
            if (s.termAsString == null) {
                spare.copyUTF8Bytes(s.term);
                s.termAsString = spare.toString();
            }
            suggestion.string = s.termAsString;
            suggestion.score = s.score;
            suggestion.freq = s.docfreq;
            suggestions[index--] = suggestion;
        }
        ArrayUtil.timSort((Object[])suggestions, (Comparator)Collections.reverseOrder(this.comparator));
        if (numSug < suggestions.length) {
            final SuggestWord[] trimmed = new SuggestWord[numSug];
            System.arraycopy(suggestions, 0, trimmed, 0, numSug);
            suggestions = trimmed;
        }
        return suggestions;
    }
    
    protected Collection<ScoreTerm> suggestSimilar(final Term term, final int numSug, final IndexReader ir, final int docfreq, final int editDistance, final float accuracy, final CharsRefBuilder spare) throws IOException {
        final AttributeSource atts = new AttributeSource();
        final MaxNonCompetitiveBoostAttribute maxBoostAtt = (MaxNonCompetitiveBoostAttribute)atts.addAttribute((Class)MaxNonCompetitiveBoostAttribute.class);
        final Terms terms = MultiFields.getTerms(ir, term.field());
        if (terms == null) {
            return (Collection<ScoreTerm>)Collections.emptyList();
        }
        final FuzzyTermsEnum e = new FuzzyTermsEnum(terms, atts, term, (float)editDistance, Math.max(this.minPrefix, editDistance - 1), true);
        final PriorityQueue<ScoreTerm> stQueue = new PriorityQueue<ScoreTerm>();
        final BytesRef queryTerm = new BytesRef((CharSequence)term.text());
        ScoreTerm st = new ScoreTerm();
        final BoostAttribute boostAtt = (BoostAttribute)e.attributes().addAttribute((Class)BoostAttribute.class);
        BytesRef candidateTerm;
        while ((candidateTerm = e.next()) != null) {
            final float boost = boostAtt.getBoost();
            if (stQueue.size() >= numSug && boost <= stQueue.peek().boost) {
                continue;
            }
            if (queryTerm.bytesEquals(candidateTerm)) {
                continue;
            }
            final int df = e.docFreq();
            if (df <= docfreq) {
                continue;
            }
            String termAsString;
            float score;
            if (this.distance == DirectSpellChecker.INTERNAL_LEVENSHTEIN) {
                termAsString = null;
                score = boost / e.getScaleFactor() + e.getMinSimilarity();
            }
            else {
                spare.copyUTF8Bytes(candidateTerm);
                termAsString = spare.toString();
                score = this.distance.getDistance(term.text(), termAsString);
            }
            if (score < accuracy) {
                continue;
            }
            st.term = BytesRef.deepCopyOf(candidateTerm);
            st.boost = boost;
            st.docfreq = df;
            st.termAsString = termAsString;
            st.score = score;
            stQueue.offer(st);
            st = ((stQueue.size() > numSug) ? stQueue.poll() : new ScoreTerm());
            maxBoostAtt.setMaxNonCompetitiveBoost((stQueue.size() >= numSug) ? stQueue.peek().boost : Float.NEGATIVE_INFINITY);
        }
        return stQueue;
    }
    
    static {
        INTERNAL_LEVENSHTEIN = new LuceneLevenshteinDistance();
    }
    
    protected static class ScoreTerm implements Comparable<ScoreTerm>
    {
        public BytesRef term;
        public float boost;
        public int docfreq;
        public String termAsString;
        public float score;
        
        public ScoreTerm() {
        }
        
        @Override
        public int compareTo(final ScoreTerm other) {
            if (this.term.bytesEquals(other.term)) {
                return 0;
            }
            if (this.boost == other.boost) {
                return other.term.compareTo(this.term);
            }
            return Float.compare(this.boost, other.boost);
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.term == null) ? 0 : this.term.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final ScoreTerm other = (ScoreTerm)obj;
            if (this.term == null) {
                if (other.term != null) {
                    return false;
                }
            }
            else if (!this.term.bytesEquals(other.term)) {
                return false;
            }
            return true;
        }
    }
}
