package org.apache.lucene.search;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.automaton.ByteRunAutomaton;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.LevenshteinAutomata;
import org.apache.lucene.util.UnicodeUtil;
import java.util.List;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import java.io.IOException;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import java.util.Comparator;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.TermsEnum;

public class FuzzyTermsEnum extends TermsEnum
{
    private TermsEnum actualEnum;
    private BoostAttribute actualBoostAtt;
    private final BoostAttribute boostAtt;
    private final MaxNonCompetitiveBoostAttribute maxBoostAtt;
    private final LevenshteinAutomataAttribute dfaAtt;
    private float bottom;
    private BytesRef bottomTerm;
    private final Comparator<BytesRef> termComparator;
    protected final float minSimilarity;
    protected final float scale_factor;
    protected final int termLength;
    protected int maxEdits;
    protected final boolean raw;
    protected final Terms terms;
    private final Term term;
    protected final int[] termText;
    protected final int realPrefixLength;
    private final boolean transpositions;
    private BytesRef queuedBottom;
    
    public FuzzyTermsEnum(final Terms terms, final AttributeSource atts, final Term term, final float minSimilarity, final int prefixLength, final boolean transpositions) throws IOException {
        this.boostAtt = this.attributes().addAttribute(BoostAttribute.class);
        this.termComparator = BytesRef.getUTF8SortedAsUnicodeComparator();
        this.queuedBottom = null;
        if (minSimilarity >= 1.0f && minSimilarity != (int)minSimilarity) {
            throw new IllegalArgumentException("fractional edit distances are not allowed");
        }
        if (minSimilarity < 0.0f) {
            throw new IllegalArgumentException("minimumSimilarity cannot be less than 0");
        }
        if (prefixLength < 0) {
            throw new IllegalArgumentException("prefixLength cannot be less than 0");
        }
        this.terms = terms;
        this.term = term;
        final String utf16 = term.text();
        this.termText = new int[utf16.codePointCount(0, utf16.length())];
        int i = 0;
        int j = 0;
        while (i < utf16.length()) {
            final int cp = this.termText[j++] = utf16.codePointAt(i);
            i += Character.charCount(cp);
        }
        this.termLength = this.termText.length;
        this.dfaAtt = atts.addAttribute(LevenshteinAutomataAttribute.class);
        this.realPrefixLength = ((prefixLength > this.termLength) ? this.termLength : prefixLength);
        if (minSimilarity >= 1.0f) {
            this.minSimilarity = 0.0f;
            this.maxEdits = (int)minSimilarity;
            this.raw = true;
        }
        else {
            this.minSimilarity = minSimilarity;
            this.maxEdits = this.initialMaxDistance(this.minSimilarity, this.termLength);
            this.raw = false;
        }
        if (transpositions && this.maxEdits > 2) {
            throw new UnsupportedOperationException("with transpositions enabled, distances > 2 are not supported ");
        }
        this.transpositions = transpositions;
        this.scale_factor = 1.0f / (1.0f - this.minSimilarity);
        this.maxBoostAtt = atts.addAttribute(MaxNonCompetitiveBoostAttribute.class);
        this.bottom = this.maxBoostAtt.getMaxNonCompetitiveBoost();
        this.bottomTerm = this.maxBoostAtt.getCompetitiveTerm();
        this.bottomChanged(null, true);
    }
    
    protected TermsEnum getAutomatonEnum(final int editDistance, final BytesRef lastTerm) throws IOException {
        final List<CompiledAutomaton> runAutomata = this.initAutomata(editDistance);
        if (editDistance < runAutomata.size()) {
            final CompiledAutomaton compiled = runAutomata.get(editDistance);
            return new AutomatonFuzzyTermsEnum(this.terms.intersect(compiled, (lastTerm == null) ? null : compiled.floor(lastTerm, new BytesRefBuilder())), runAutomata.subList(0, editDistance + 1).toArray(new CompiledAutomaton[editDistance + 1]));
        }
        return null;
    }
    
    private List<CompiledAutomaton> initAutomata(final int maxDistance) {
        final List<CompiledAutomaton> runAutomata = this.dfaAtt.automata();
        if (runAutomata.size() <= maxDistance && maxDistance <= 2) {
            final LevenshteinAutomata builder = new LevenshteinAutomata(UnicodeUtil.newString(this.termText, this.realPrefixLength, this.termText.length - this.realPrefixLength), this.transpositions);
            final String prefix = UnicodeUtil.newString(this.termText, 0, this.realPrefixLength);
            for (int i = runAutomata.size(); i <= maxDistance; ++i) {
                final Automaton a = builder.toAutomaton(i, prefix);
                runAutomata.add(new CompiledAutomaton(a, true, false));
            }
        }
        return runAutomata;
    }
    
    protected void setEnum(final TermsEnum actualEnum) {
        this.actualEnum = actualEnum;
        this.actualBoostAtt = actualEnum.attributes().addAttribute(BoostAttribute.class);
    }
    
    private void bottomChanged(final BytesRef lastTerm, final boolean init) throws IOException {
        final int oldMaxEdits = this.maxEdits;
        final boolean termAfter = this.bottomTerm == null || (lastTerm != null && this.termComparator.compare(lastTerm, this.bottomTerm) >= 0);
        while (this.maxEdits > 0) {
            if (termAfter) {
                if (this.bottom < this.calculateMaxBoost(this.maxEdits)) {
                    break;
                }
            }
            else if (this.bottom <= this.calculateMaxBoost(this.maxEdits)) {
                break;
            }
            --this.maxEdits;
        }
        if (oldMaxEdits != this.maxEdits || init) {
            this.maxEditDistanceChanged(lastTerm, this.maxEdits, init);
        }
    }
    
    protected void maxEditDistanceChanged(final BytesRef lastTerm, final int maxEdits, final boolean init) throws IOException {
        final TermsEnum newEnum = this.getAutomatonEnum(maxEdits, lastTerm);
        if (newEnum != null) {
            this.setEnum(newEnum);
            return;
        }
        assert maxEdits > 2;
        throw new IllegalArgumentException("maxEdits cannot be > LevenshteinAutomata.MAXIMUM_SUPPORTED_DISTANCE");
    }
    
    private int initialMaxDistance(final float minimumSimilarity, final int termLen) {
        return (int)((1.0 - minimumSimilarity) * termLen);
    }
    
    private float calculateMaxBoost(final int nEdits) {
        final float similarity = 1.0f - nEdits / (float)this.termLength;
        return (similarity - this.minSimilarity) * this.scale_factor;
    }
    
    @Override
    public BytesRef next() throws IOException {
        if (this.queuedBottom != null) {
            this.bottomChanged(this.queuedBottom, false);
            this.queuedBottom = null;
        }
        final BytesRef term = this.actualEnum.next();
        this.boostAtt.setBoost(this.actualBoostAtt.getBoost());
        final float bottom = this.maxBoostAtt.getMaxNonCompetitiveBoost();
        final BytesRef bottomTerm = this.maxBoostAtt.getCompetitiveTerm();
        if (term != null && (bottom != this.bottom || bottomTerm != this.bottomTerm)) {
            this.bottom = bottom;
            this.bottomTerm = bottomTerm;
            this.queuedBottom = BytesRef.deepCopyOf(term);
        }
        return term;
    }
    
    @Override
    public int docFreq() throws IOException {
        return this.actualEnum.docFreq();
    }
    
    @Override
    public long totalTermFreq() throws IOException {
        return this.actualEnum.totalTermFreq();
    }
    
    @Override
    public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
        return this.actualEnum.postings(reuse, flags);
    }
    
    @Override
    public void seekExact(final BytesRef term, final TermState state) throws IOException {
        this.actualEnum.seekExact(term, state);
    }
    
    @Override
    public TermState termState() throws IOException {
        return this.actualEnum.termState();
    }
    
    @Override
    public long ord() throws IOException {
        return this.actualEnum.ord();
    }
    
    @Override
    public boolean seekExact(final BytesRef text) throws IOException {
        return this.actualEnum.seekExact(text);
    }
    
    @Override
    public SeekStatus seekCeil(final BytesRef text) throws IOException {
        return this.actualEnum.seekCeil(text);
    }
    
    @Override
    public void seekExact(final long ord) throws IOException {
        this.actualEnum.seekExact(ord);
    }
    
    @Override
    public BytesRef term() throws IOException {
        return this.actualEnum.term();
    }
    
    public float getMinSimilarity() {
        return this.minSimilarity;
    }
    
    public float getScaleFactor() {
        return this.scale_factor;
    }
    
    private class AutomatonFuzzyTermsEnum extends FilteredTermsEnum
    {
        private final ByteRunAutomaton[] matchers;
        private final BytesRef termRef;
        private final BoostAttribute boostAtt;
        
        public AutomatonFuzzyTermsEnum(final TermsEnum tenum, final CompiledAutomaton[] compiled) {
            super(tenum, false);
            this.boostAtt = this.attributes().addAttribute(BoostAttribute.class);
            this.matchers = new ByteRunAutomaton[compiled.length];
            for (int i = 0; i < compiled.length; ++i) {
                this.matchers[i] = compiled[i].runAutomaton;
            }
            this.termRef = new BytesRef(FuzzyTermsEnum.this.term.text());
        }
        
        @Override
        protected AcceptStatus accept(final BytesRef term) {
            int ed;
            for (ed = this.matchers.length - 1; ed > 0 && this.matches(term, ed - 1); --ed) {}
            if (ed == 0) {
                this.boostAtt.setBoost(1.0f);
                return AcceptStatus.YES;
            }
            final int codePointCount = UnicodeUtil.codePointCount(term);
            final float similarity = 1.0f - ed / (float)Math.min(codePointCount, FuzzyTermsEnum.this.termLength);
            if (similarity > FuzzyTermsEnum.this.minSimilarity) {
                this.boostAtt.setBoost((similarity - FuzzyTermsEnum.this.minSimilarity) * FuzzyTermsEnum.this.scale_factor);
                return AcceptStatus.YES;
            }
            return AcceptStatus.NO;
        }
        
        final boolean matches(final BytesRef term, final int k) {
            return (k == 0) ? term.equals(this.termRef) : this.matchers[k].run(term.bytes, term.offset, term.length);
        }
    }
    
    public static final class LevenshteinAutomataAttributeImpl extends AttributeImpl implements LevenshteinAutomataAttribute
    {
        private final List<CompiledAutomaton> automata;
        
        public LevenshteinAutomataAttributeImpl() {
            this.automata = new ArrayList<CompiledAutomaton>();
        }
        
        @Override
        public List<CompiledAutomaton> automata() {
            return this.automata;
        }
        
        @Override
        public void clear() {
            this.automata.clear();
        }
        
        @Override
        public int hashCode() {
            return this.automata.hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof LevenshteinAutomataAttributeImpl && this.automata.equals(((LevenshteinAutomataAttributeImpl)other).automata));
        }
        
        @Override
        public void copyTo(final AttributeImpl target) {
            final List<CompiledAutomaton> targetAutomata = ((LevenshteinAutomataAttribute)target).automata();
            targetAutomata.clear();
            targetAutomata.addAll(this.automata);
        }
        
        @Override
        public void reflectWith(final AttributeReflector reflector) {
            reflector.reflect(LevenshteinAutomataAttribute.class, "automata", this.automata);
        }
    }
    
    public interface LevenshteinAutomataAttribute extends Attribute
    {
        List<CompiledAutomaton> automata();
    }
}
