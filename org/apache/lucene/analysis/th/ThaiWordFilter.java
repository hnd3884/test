package org.apache.lucene.analysis.th;

import java.util.Locale;
import java.io.IOException;
import java.text.CharacterIterator;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArrayIterator;
import java.text.BreakIterator;
import org.apache.lucene.analysis.TokenFilter;

@Deprecated
public final class ThaiWordFilter extends TokenFilter
{
    public static final boolean DBBI_AVAILABLE;
    private static final BreakIterator proto;
    private final BreakIterator breaker;
    private final CharArrayIterator charIterator;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posAtt;
    private AttributeSource clonedToken;
    private CharTermAttribute clonedTermAtt;
    private OffsetAttribute clonedOffsetAtt;
    private boolean hasMoreTokensInClone;
    private boolean hasIllegalOffsets;
    
    public ThaiWordFilter(final TokenStream input) {
        super(input);
        this.breaker = (BreakIterator)ThaiWordFilter.proto.clone();
        this.charIterator = CharArrayIterator.newWordInstance();
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.clonedToken = null;
        this.clonedTermAtt = null;
        this.clonedOffsetAtt = null;
        this.hasMoreTokensInClone = false;
        this.hasIllegalOffsets = false;
        if (!ThaiWordFilter.DBBI_AVAILABLE) {
            throw new UnsupportedOperationException("This JRE does not have support for Thai segmentation");
        }
    }
    
    public boolean incrementToken() throws IOException {
        if (this.hasMoreTokensInClone) {
            final int start = this.breaker.current();
            final int end = this.breaker.next();
            if (end != -1) {
                this.clonedToken.copyTo((AttributeSource)this);
                this.termAtt.copyBuffer(this.clonedTermAtt.buffer(), start, end - start);
                if (this.hasIllegalOffsets) {
                    this.offsetAtt.setOffset(this.clonedOffsetAtt.startOffset(), this.clonedOffsetAtt.endOffset());
                }
                else {
                    this.offsetAtt.setOffset(this.clonedOffsetAtt.startOffset() + start, this.clonedOffsetAtt.startOffset() + end);
                }
                this.posAtt.setPositionIncrement(1);
                return true;
            }
            this.hasMoreTokensInClone = false;
        }
        if (!this.input.incrementToken()) {
            return false;
        }
        if (this.termAtt.length() == 0 || Character.UnicodeBlock.of(this.termAtt.charAt(0)) != Character.UnicodeBlock.THAI) {
            return true;
        }
        this.hasMoreTokensInClone = true;
        this.hasIllegalOffsets = (this.offsetAtt.endOffset() - this.offsetAtt.startOffset() != this.termAtt.length());
        if (this.clonedToken == null) {
            this.clonedToken = this.cloneAttributes();
            this.clonedTermAtt = (CharTermAttribute)this.clonedToken.getAttribute((Class)CharTermAttribute.class);
            this.clonedOffsetAtt = (OffsetAttribute)this.clonedToken.getAttribute((Class)OffsetAttribute.class);
        }
        else {
            this.copyTo(this.clonedToken);
        }
        this.charIterator.setText(this.clonedTermAtt.buffer(), 0, this.clonedTermAtt.length());
        this.breaker.setText(this.charIterator);
        final int end2 = this.breaker.next();
        if (end2 != -1) {
            this.termAtt.setLength(end2);
            if (this.hasIllegalOffsets) {
                this.offsetAtt.setOffset(this.clonedOffsetAtt.startOffset(), this.clonedOffsetAtt.endOffset());
            }
            else {
                this.offsetAtt.setOffset(this.clonedOffsetAtt.startOffset(), this.clonedOffsetAtt.startOffset() + end2);
            }
            return true;
        }
        return false;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.hasMoreTokensInClone = false;
        this.clonedToken = null;
        this.clonedTermAtt = null;
        this.clonedOffsetAtt = null;
    }
    
    static {
        DBBI_AVAILABLE = ThaiTokenizer.DBBI_AVAILABLE;
        proto = BreakIterator.getWordInstance(new Locale("th"));
    }
}
