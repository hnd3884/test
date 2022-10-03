package org.apache.lucene.sandbox.queries;

import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.FuzzyTermsEnum;

@Deprecated
public final class SlowFuzzyTermsEnum extends FuzzyTermsEnum
{
    public SlowFuzzyTermsEnum(final Terms terms, final AttributeSource atts, final Term term, final float minSimilarity, final int prefixLength) throws IOException {
        super(terms, atts, term, minSimilarity, prefixLength, false);
    }
    
    protected void maxEditDistanceChanged(final BytesRef lastTerm, final int maxEdits, final boolean init) throws IOException {
        final TermsEnum newEnum = this.getAutomatonEnum(maxEdits, lastTerm);
        if (newEnum != null) {
            this.setEnum(newEnum);
        }
        else if (init) {
            this.setEnum((TermsEnum)new LinearFuzzyTermsEnum());
        }
    }
    
    private class LinearFuzzyTermsEnum extends FilteredTermsEnum
    {
        private int[] d;
        private int[] p;
        private final int[] text;
        private final BoostAttribute boostAtt;
        private final BytesRef prefixBytesRef;
        private final IntsRefBuilder utf32;
        
        public LinearFuzzyTermsEnum() throws IOException {
            super(SlowFuzzyTermsEnum.this.terms.iterator());
            this.boostAtt = (BoostAttribute)this.attributes().addAttribute((Class)BoostAttribute.class);
            this.utf32 = new IntsRefBuilder();
            this.text = new int[SlowFuzzyTermsEnum.this.termLength - SlowFuzzyTermsEnum.this.realPrefixLength];
            System.arraycopy(SlowFuzzyTermsEnum.this.termText, SlowFuzzyTermsEnum.this.realPrefixLength, this.text, 0, this.text.length);
            final String prefix = UnicodeUtil.newString(SlowFuzzyTermsEnum.this.termText, 0, SlowFuzzyTermsEnum.this.realPrefixLength);
            this.prefixBytesRef = new BytesRef((CharSequence)prefix);
            this.d = new int[this.text.length + 1];
            this.p = new int[this.text.length + 1];
            this.setInitialSeekTerm(this.prefixBytesRef);
        }
        
        protected final FilteredTermsEnum.AcceptStatus accept(final BytesRef term) {
            if (!StringHelper.startsWith(term, this.prefixBytesRef)) {
                return FilteredTermsEnum.AcceptStatus.END;
            }
            this.utf32.copyUTF8Bytes(term);
            final int distance = this.calcDistance(this.utf32.ints(), SlowFuzzyTermsEnum.this.realPrefixLength, this.utf32.length() - SlowFuzzyTermsEnum.this.realPrefixLength);
            if (distance == Integer.MIN_VALUE) {
                return FilteredTermsEnum.AcceptStatus.NO;
            }
            if (SlowFuzzyTermsEnum.this.raw && distance > SlowFuzzyTermsEnum.this.maxEdits) {
                return FilteredTermsEnum.AcceptStatus.NO;
            }
            final float similarity = this.calcSimilarity(distance, this.utf32.length() - SlowFuzzyTermsEnum.this.realPrefixLength, this.text.length);
            if (SlowFuzzyTermsEnum.this.raw || (!SlowFuzzyTermsEnum.this.raw && similarity > SlowFuzzyTermsEnum.this.minSimilarity)) {
                this.boostAtt.setBoost((similarity - SlowFuzzyTermsEnum.this.minSimilarity) * SlowFuzzyTermsEnum.this.scale_factor);
                return FilteredTermsEnum.AcceptStatus.YES;
            }
            return FilteredTermsEnum.AcceptStatus.NO;
        }
        
        private final int calcDistance(final int[] target, final int offset, final int length) {
            final int m = length;
            final int n = this.text.length;
            if (n == 0) {
                return m;
            }
            if (m == 0) {
                return n;
            }
            final int maxDistance = this.calculateMaxDistance(m);
            if (maxDistance < Math.abs(m - n)) {
                return Integer.MIN_VALUE;
            }
            for (int i = 0; i <= n; ++i) {
                this.p[i] = i;
            }
            for (int j = 1; j <= m; ++j) {
                int bestPossibleEditDistance = m;
                final int t_j = target[offset + j - 1];
                this.d[0] = j;
                for (int k = 1; k <= n; ++k) {
                    if (t_j != this.text[k - 1]) {
                        this.d[k] = Math.min(Math.min(this.d[k - 1], this.p[k]), this.p[k - 1]) + 1;
                    }
                    else {
                        this.d[k] = Math.min(Math.min(this.d[k - 1] + 1, this.p[k] + 1), this.p[k - 1]);
                    }
                    bestPossibleEditDistance = Math.min(bestPossibleEditDistance, this.d[k]);
                }
                if (j > maxDistance && bestPossibleEditDistance > maxDistance) {
                    return Integer.MIN_VALUE;
                }
                final int[] _d = this.p;
                this.p = this.d;
                this.d = _d;
            }
            return this.p[n];
        }
        
        private float calcSimilarity(final int edits, final int m, final int n) {
            return 1.0f - edits / (float)(SlowFuzzyTermsEnum.this.realPrefixLength + Math.min(n, m));
        }
        
        private int calculateMaxDistance(final int m) {
            return SlowFuzzyTermsEnum.this.raw ? SlowFuzzyTermsEnum.this.maxEdits : Math.min(SlowFuzzyTermsEnum.this.maxEdits, (int)((1.0f - SlowFuzzyTermsEnum.this.minSimilarity) * (Math.min(this.text.length, m) + SlowFuzzyTermsEnum.this.realPrefixLength)));
        }
    }
}
