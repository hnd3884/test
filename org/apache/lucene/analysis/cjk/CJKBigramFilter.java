package org.apache.lucene.analysis.cjk;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class CJKBigramFilter extends TokenFilter
{
    public static final int HAN = 1;
    public static final int HIRAGANA = 2;
    public static final int KATAKANA = 4;
    public static final int HANGUL = 8;
    public static final String DOUBLE_TYPE = "<DOUBLE>";
    public static final String SINGLE_TYPE = "<SINGLE>";
    private static final String HAN_TYPE;
    private static final String HIRAGANA_TYPE;
    private static final String KATAKANA_TYPE;
    private static final String HANGUL_TYPE;
    private static final Object NO;
    private final Object doHan;
    private final Object doHiragana;
    private final Object doKatakana;
    private final Object doHangul;
    private final boolean outputUnigrams;
    private boolean ngramState;
    private final CharTermAttribute termAtt;
    private final TypeAttribute typeAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posIncAtt;
    private final PositionLengthAttribute posLengthAtt;
    int[] buffer;
    int[] startOffset;
    int[] endOffset;
    int bufferLen;
    int index;
    int lastEndOffset;
    private boolean exhausted;
    private AttributeSource.State loneState;
    
    public CJKBigramFilter(final TokenStream in) {
        this(in, 15);
    }
    
    public CJKBigramFilter(final TokenStream in, final int flags) {
        this(in, flags, false);
    }
    
    public CJKBigramFilter(final TokenStream in, final int flags, final boolean outputUnigrams) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.posLengthAtt = (PositionLengthAttribute)this.addAttribute((Class)PositionLengthAttribute.class);
        this.buffer = new int[8];
        this.startOffset = new int[8];
        this.endOffset = new int[8];
        this.doHan = (((flags & 0x1) == 0x0) ? CJKBigramFilter.NO : CJKBigramFilter.HAN_TYPE);
        this.doHiragana = (((flags & 0x2) == 0x0) ? CJKBigramFilter.NO : CJKBigramFilter.HIRAGANA_TYPE);
        this.doKatakana = (((flags & 0x4) == 0x0) ? CJKBigramFilter.NO : CJKBigramFilter.KATAKANA_TYPE);
        this.doHangul = (((flags & 0x8) == 0x0) ? CJKBigramFilter.NO : CJKBigramFilter.HANGUL_TYPE);
        this.outputUnigrams = outputUnigrams;
    }
    
    public boolean incrementToken() throws IOException {
        while (!this.hasBufferedBigram()) {
            if (this.doNext()) {
                final String type = this.typeAtt.type();
                if (type == this.doHan || type == this.doHiragana || type == this.doKatakana || type == this.doHangul) {
                    if (this.offsetAtt.startOffset() != this.lastEndOffset) {
                        if (this.hasBufferedUnigram()) {
                            this.loneState = this.captureState();
                            this.flushUnigram();
                            return true;
                        }
                        this.index = 0;
                        this.bufferLen = 0;
                    }
                    this.refill();
                }
                else {
                    if (this.hasBufferedUnigram()) {
                        this.loneState = this.captureState();
                        this.flushUnigram();
                        return true;
                    }
                    return true;
                }
            }
            else {
                if (this.hasBufferedUnigram()) {
                    this.flushUnigram();
                    return true;
                }
                return false;
            }
        }
        if (this.outputUnigrams) {
            if (this.ngramState) {
                this.flushBigram();
            }
            else {
                this.flushUnigram();
                --this.index;
            }
            this.ngramState = !this.ngramState;
        }
        else {
            this.flushBigram();
        }
        return true;
    }
    
    private boolean doNext() throws IOException {
        if (this.loneState != null) {
            this.restoreState(this.loneState);
            this.loneState = null;
            return true;
        }
        if (this.exhausted) {
            return false;
        }
        if (this.input.incrementToken()) {
            return true;
        }
        this.exhausted = true;
        return false;
    }
    
    private void refill() {
        if (this.bufferLen > 64) {
            final int last = this.bufferLen - 1;
            this.buffer[0] = this.buffer[last];
            this.startOffset[0] = this.startOffset[last];
            this.endOffset[0] = this.endOffset[last];
            this.bufferLen = 1;
            this.index -= last;
        }
        final char[] termBuffer = this.termAtt.buffer();
        final int len = this.termAtt.length();
        int start = this.offsetAtt.startOffset();
        final int end = this.offsetAtt.endOffset();
        final int newSize = this.bufferLen + len;
        this.buffer = ArrayUtil.grow(this.buffer, newSize);
        this.startOffset = ArrayUtil.grow(this.startOffset, newSize);
        this.endOffset = ArrayUtil.grow(this.endOffset, newSize);
        this.lastEndOffset = end;
        if (end - start != len) {
            for (int i = 0, cp = 0; i < len; i += Character.charCount(cp)) {
                final int[] buffer = this.buffer;
                final int bufferLen = this.bufferLen;
                final int codePoint = Character.codePointAt(termBuffer, i, len);
                buffer[bufferLen] = codePoint;
                cp = codePoint;
                this.startOffset[this.bufferLen] = start;
                this.endOffset[this.bufferLen] = end;
                ++this.bufferLen;
            }
        }
        else {
            int i = 0;
            int cp = 0;
            for (int cpLen = 0; i < len; i += cpLen) {
                final int[] buffer2 = this.buffer;
                final int bufferLen2 = this.bufferLen;
                final int codePoint2 = Character.codePointAt(termBuffer, i, len);
                buffer2[bufferLen2] = codePoint2;
                cp = codePoint2;
                cpLen = Character.charCount(cp);
                this.startOffset[this.bufferLen] = start;
                final int[] endOffset = this.endOffset;
                final int bufferLen3 = this.bufferLen;
                final int n = start + cpLen;
                endOffset[bufferLen3] = n;
                start = n;
                ++this.bufferLen;
            }
        }
    }
    
    private void flushBigram() {
        this.clearAttributes();
        final char[] termBuffer = this.termAtt.resizeBuffer(4);
        final int len1 = Character.toChars(this.buffer[this.index], termBuffer, 0);
        final int len2 = len1 + Character.toChars(this.buffer[this.index + 1], termBuffer, len1);
        this.termAtt.setLength(len2);
        this.offsetAtt.setOffset(this.startOffset[this.index], this.endOffset[this.index + 1]);
        this.typeAtt.setType("<DOUBLE>");
        if (this.outputUnigrams) {
            this.posIncAtt.setPositionIncrement(0);
            this.posLengthAtt.setPositionLength(2);
        }
        ++this.index;
    }
    
    private void flushUnigram() {
        this.clearAttributes();
        final char[] termBuffer = this.termAtt.resizeBuffer(2);
        final int len = Character.toChars(this.buffer[this.index], termBuffer, 0);
        this.termAtt.setLength(len);
        this.offsetAtt.setOffset(this.startOffset[this.index], this.endOffset[this.index]);
        this.typeAtt.setType("<SINGLE>");
        ++this.index;
    }
    
    private boolean hasBufferedBigram() {
        return this.bufferLen - this.index > 1;
    }
    
    private boolean hasBufferedUnigram() {
        if (this.outputUnigrams) {
            return this.bufferLen - this.index == 1;
        }
        return this.bufferLen == 1 && this.index == 0;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.bufferLen = 0;
        this.index = 0;
        this.lastEndOffset = 0;
        this.loneState = null;
        this.exhausted = false;
        this.ngramState = false;
    }
    
    static {
        HAN_TYPE = StandardTokenizer.TOKEN_TYPES[10];
        HIRAGANA_TYPE = StandardTokenizer.TOKEN_TYPES[11];
        KATAKANA_TYPE = StandardTokenizer.TOKEN_TYPES[12];
        HANGUL_TYPE = StandardTokenizer.TOKEN_TYPES[13];
        NO = new Object();
    }
}
