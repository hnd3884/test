package org.apache.lucene.analysis.th;

import java.util.Locale;
import java.text.CharacterIterator;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArrayIterator;
import java.text.BreakIterator;
import org.apache.lucene.analysis.util.SegmentingTokenizerBase;

public class ThaiTokenizer extends SegmentingTokenizerBase
{
    public static final boolean DBBI_AVAILABLE;
    private static final BreakIterator proto;
    private static final BreakIterator sentenceProto;
    private final BreakIterator wordBreaker;
    private final CharArrayIterator wrapper;
    int sentenceStart;
    int sentenceEnd;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    
    public ThaiTokenizer() {
        this(ThaiTokenizer.DEFAULT_TOKEN_ATTRIBUTE_FACTORY);
    }
    
    public ThaiTokenizer(final AttributeFactory factory) {
        super(factory, (BreakIterator)ThaiTokenizer.sentenceProto.clone());
        this.wrapper = CharArrayIterator.newWordInstance();
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        if (!ThaiTokenizer.DBBI_AVAILABLE) {
            throw new UnsupportedOperationException("This JRE does not have support for Thai segmentation");
        }
        this.wordBreaker = (BreakIterator)ThaiTokenizer.proto.clone();
    }
    
    @Override
    protected void setNextSentence(final int sentenceStart, final int sentenceEnd) {
        this.sentenceStart = sentenceStart;
        this.sentenceEnd = sentenceEnd;
        this.wrapper.setText(this.buffer, sentenceStart, sentenceEnd - sentenceStart);
        this.wordBreaker.setText(this.wrapper);
    }
    
    @Override
    protected boolean incrementWord() {
        int start = this.wordBreaker.current();
        if (start == -1) {
            return false;
        }
        int end;
        for (end = this.wordBreaker.next(); end != -1 && !Character.isLetterOrDigit(Character.codePointAt(this.buffer, this.sentenceStart + start, this.sentenceEnd)); start = end, end = this.wordBreaker.next()) {}
        if (end == -1) {
            return false;
        }
        this.clearAttributes();
        this.termAtt.copyBuffer(this.buffer, this.sentenceStart + start, end - start);
        this.offsetAtt.setOffset(this.correctOffset(this.offset + this.sentenceStart + start), this.correctOffset(this.offset + this.sentenceStart + end));
        return true;
    }
    
    static {
        (proto = BreakIterator.getWordInstance(new Locale("th"))).setText("\u0e20\u0e32\u0e29\u0e32\u0e44\u0e17\u0e22");
        DBBI_AVAILABLE = ThaiTokenizer.proto.isBoundary(4);
        sentenceProto = BreakIterator.getSentenceInstance(Locale.ROOT);
    }
}
