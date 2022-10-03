package org.apache.lucene.analysis.miscellaneous;

import java.util.Arrays;
import java.util.Comparator;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public class FingerprintFilter extends TokenFilter
{
    public static final int DEFAULT_MAX_OUTPUT_TOKEN_SIZE = 1024;
    public static final char DEFAULT_SEPARATOR = ' ';
    private final CharTermAttribute termAttribute;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private final PositionLengthAttribute posLenAtt;
    private final TypeAttribute typeAtt;
    private CharArraySet uniqueTerms;
    private final int maxOutputTokenSize;
    private AttributeSource.State finalState;
    private final char separator;
    private boolean inputEnded;
    
    public FingerprintFilter(final TokenStream input) {
        this(input, 1024, ' ');
    }
    
    public FingerprintFilter(final TokenStream input, final int maxOutputTokenSize, final char separator) {
        super(input);
        this.termAttribute = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.posLenAtt = (PositionLengthAttribute)this.addAttribute((Class)PositionLengthAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.uniqueTerms = null;
        this.inputEnded = false;
        this.maxOutputTokenSize = maxOutputTokenSize;
        this.separator = separator;
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.uniqueTerms != null) {
            return false;
        }
        final boolean result = this.buildSingleOutputToken();
        this.finalState = this.captureState();
        return result;
    }
    
    private final boolean buildSingleOutputToken() throws IOException {
        this.inputEnded = false;
        char[] clonedLastTerm = null;
        this.uniqueTerms = new CharArraySet(8, false);
        int outputTokenSize = 0;
        while (this.input.incrementToken()) {
            if (outputTokenSize > this.maxOutputTokenSize) {
                continue;
            }
            final char[] term = this.termAttribute.buffer();
            final int length = this.termAttribute.length();
            if (this.uniqueTerms.contains(term, 0, length)) {
                continue;
            }
            clonedLastTerm = new char[length];
            System.arraycopy(term, 0, clonedLastTerm, 0, length);
            if (this.uniqueTerms.size() > 0) {
                ++outputTokenSize;
            }
            this.uniqueTerms.add(clonedLastTerm);
            outputTokenSize += length;
        }
        this.input.end();
        this.inputEnded = true;
        this.offsetAtt.setOffset(0, this.offsetAtt.endOffset());
        this.posLenAtt.setPositionLength(1);
        this.posIncrAtt.setPositionIncrement(1);
        this.typeAtt.setType("fingerprint");
        if (this.uniqueTerms.size() < 1) {
            this.termAttribute.setEmpty();
            return false;
        }
        if (outputTokenSize > this.maxOutputTokenSize) {
            this.termAttribute.setEmpty();
            this.uniqueTerms.clear();
            return false;
        }
        if (this.uniqueTerms.size() == 1) {
            this.termAttribute.setEmpty().append(new String(clonedLastTerm));
            this.uniqueTerms.clear();
            return true;
        }
        final Object[] items = this.uniqueTerms.toArray();
        Arrays.sort(items, new Comparator<Object>() {
            @Override
            public int compare(final Object o1, final Object o2) {
                final char[] v1 = (char[])o1;
                final char[] v2 = (char[])o2;
                final int len1 = v1.length;
                final int len2 = v2.length;
                for (int lim = Math.min(len1, len2), k = 0; k < lim; ++k) {
                    final char c1 = v1[k];
                    final char c2 = v2[k];
                    if (c1 != c2) {
                        return c1 - c2;
                    }
                }
                return len1 - len2;
            }
        });
        final StringBuilder sb = new StringBuilder();
        for (final Object item : items) {
            if (sb.length() >= 1) {
                sb.append(this.separator);
            }
            sb.append((char[])item);
        }
        this.termAttribute.setEmpty().append(sb);
        this.uniqueTerms.clear();
        return true;
    }
    
    public final void end() throws IOException {
        if (!this.inputEnded) {
            this.input.end();
            this.inputEnded = true;
        }
        if (this.finalState != null) {
            this.restoreState(this.finalState);
        }
    }
    
    public void reset() throws IOException {
        super.reset();
        this.inputEnded = false;
        this.uniqueTerms = null;
    }
}
