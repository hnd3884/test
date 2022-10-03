package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;

@Deprecated
public final class Lucene47WordDelimiterFilter extends TokenFilter
{
    public static final int LOWER = 1;
    public static final int UPPER = 2;
    public static final int DIGIT = 4;
    public static final int SUBWORD_DELIM = 8;
    public static final int ALPHA = 3;
    public static final int ALPHANUM = 7;
    public static final int GENERATE_WORD_PARTS = 1;
    public static final int GENERATE_NUMBER_PARTS = 2;
    public static final int CATENATE_WORDS = 4;
    public static final int CATENATE_NUMBERS = 8;
    public static final int CATENATE_ALL = 16;
    public static final int PRESERVE_ORIGINAL = 32;
    public static final int SPLIT_ON_CASE_CHANGE = 64;
    public static final int SPLIT_ON_NUMERICS = 128;
    public static final int STEM_ENGLISH_POSSESSIVE = 256;
    final CharArraySet protWords;
    private final int flags;
    private final CharTermAttribute termAttribute;
    private final OffsetAttribute offsetAttribute;
    private final PositionIncrementAttribute posIncAttribute;
    private final TypeAttribute typeAttribute;
    private final WordDelimiterIterator iterator;
    private final WordDelimiterConcatenation concat;
    private int lastConcatCount;
    private final WordDelimiterConcatenation concatAll;
    private int accumPosInc;
    private char[] savedBuffer;
    private int savedStartOffset;
    private int savedEndOffset;
    private String savedType;
    private boolean hasSavedState;
    private boolean hasIllegalOffsets;
    private boolean hasOutputToken;
    private boolean hasOutputFollowingOriginal;
    
    public Lucene47WordDelimiterFilter(final TokenStream in, final byte[] charTypeTable, final int configurationFlags, final CharArraySet protWords) {
        super(in);
        this.termAttribute = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAttribute = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncAttribute = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.typeAttribute = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.concat = new WordDelimiterConcatenation();
        this.lastConcatCount = 0;
        this.concatAll = new WordDelimiterConcatenation();
        this.accumPosInc = 0;
        this.savedBuffer = new char[1024];
        this.hasSavedState = false;
        this.hasIllegalOffsets = false;
        this.hasOutputToken = false;
        this.hasOutputFollowingOriginal = false;
        this.flags = configurationFlags;
        this.protWords = protWords;
        this.iterator = new WordDelimiterIterator(charTypeTable, this.has(64), this.has(128), this.has(256));
    }
    
    public Lucene47WordDelimiterFilter(final TokenStream in, final int configurationFlags, final CharArraySet protWords) {
        this(in, WordDelimiterIterator.DEFAULT_WORD_DELIM_TABLE, configurationFlags, protWords);
    }
    
    public boolean incrementToken() throws IOException {
        while (true) {
            if (!this.hasSavedState) {
                if (!this.input.incrementToken()) {
                    return false;
                }
                final int termLength = this.termAttribute.length();
                final char[] termBuffer = this.termAttribute.buffer();
                this.accumPosInc += this.posIncAttribute.getPositionIncrement();
                this.iterator.setText(termBuffer, termLength);
                this.iterator.next();
                if ((this.iterator.current == 0 && this.iterator.end == termLength) || (this.protWords != null && this.protWords.contains(termBuffer, 0, termLength))) {
                    this.posIncAttribute.setPositionIncrement(this.accumPosInc);
                    this.accumPosInc = 0;
                    return true;
                }
                if (this.iterator.end == -1 && !this.has(32)) {
                    if (this.posIncAttribute.getPositionIncrement() == 1) {
                        --this.accumPosInc;
                        continue;
                    }
                    continue;
                }
                else {
                    this.saveState();
                    this.hasOutputToken = false;
                    this.hasOutputFollowingOriginal = !this.has(32);
                    this.lastConcatCount = 0;
                    if (this.has(32)) {
                        this.posIncAttribute.setPositionIncrement(this.accumPosInc);
                        this.accumPosInc = 0;
                        return true;
                    }
                }
            }
            if (this.iterator.end == -1) {
                if (!this.concat.isEmpty() && this.flushConcatenation(this.concat)) {
                    return true;
                }
                if (!this.concatAll.isEmpty()) {
                    if (this.concatAll.subwordCount > this.lastConcatCount) {
                        this.concatAll.writeAndClear();
                        return true;
                    }
                    this.concatAll.clear();
                }
                this.hasSavedState = false;
            }
            else {
                if (this.iterator.isSingleWord()) {
                    this.generatePart(true);
                    this.iterator.next();
                    return true;
                }
                final int wordType = this.iterator.type();
                if (!this.concat.isEmpty() && (this.concat.type & wordType) == 0x0) {
                    if (this.flushConcatenation(this.concat)) {
                        this.hasOutputToken = false;
                        return true;
                    }
                    this.hasOutputToken = false;
                }
                if (this.shouldConcatenate(wordType)) {
                    if (this.concat.isEmpty()) {
                        this.concat.type = wordType;
                    }
                    this.concatenate(this.concat);
                }
                if (this.has(16)) {
                    this.concatenate(this.concatAll);
                }
                if (this.shouldGenerateParts(wordType)) {
                    this.generatePart(false);
                    this.iterator.next();
                    return true;
                }
                this.iterator.next();
            }
        }
    }
    
    public void reset() throws IOException {
        super.reset();
        this.hasSavedState = false;
        this.concat.clear();
        this.concatAll.clear();
        this.accumPosInc = 0;
    }
    
    private void saveState() {
        this.savedStartOffset = this.offsetAttribute.startOffset();
        this.savedEndOffset = this.offsetAttribute.endOffset();
        this.hasIllegalOffsets = (this.savedEndOffset - this.savedStartOffset != this.termAttribute.length());
        this.savedType = this.typeAttribute.type();
        if (this.savedBuffer.length < this.termAttribute.length()) {
            this.savedBuffer = new char[ArrayUtil.oversize(this.termAttribute.length(), 2)];
        }
        System.arraycopy(this.termAttribute.buffer(), 0, this.savedBuffer, 0, this.termAttribute.length());
        this.iterator.text = this.savedBuffer;
        this.hasSavedState = true;
    }
    
    private boolean flushConcatenation(final WordDelimiterConcatenation concatenation) {
        this.lastConcatCount = concatenation.subwordCount;
        if (concatenation.subwordCount != 1 || !this.shouldGenerateParts(concatenation.type)) {
            concatenation.writeAndClear();
            return true;
        }
        concatenation.clear();
        return false;
    }
    
    private boolean shouldConcatenate(final int wordType) {
        return (this.has(4) && isAlpha(wordType)) || (this.has(8) && isDigit(wordType));
    }
    
    private boolean shouldGenerateParts(final int wordType) {
        return (this.has(1) && isAlpha(wordType)) || (this.has(2) && isDigit(wordType));
    }
    
    private void concatenate(final WordDelimiterConcatenation concatenation) {
        if (concatenation.isEmpty()) {
            concatenation.startOffset = this.savedStartOffset + this.iterator.current;
        }
        concatenation.append(this.savedBuffer, this.iterator.current, this.iterator.end - this.iterator.current);
        concatenation.endOffset = this.savedStartOffset + this.iterator.end;
    }
    
    private void generatePart(final boolean isSingleWord) {
        this.clearAttributes();
        this.termAttribute.copyBuffer(this.savedBuffer, this.iterator.current, this.iterator.end - this.iterator.current);
        final int startOffset = this.savedStartOffset + this.iterator.current;
        final int endOffset = this.savedStartOffset + this.iterator.end;
        if (this.hasIllegalOffsets) {
            if (isSingleWord && startOffset <= this.savedEndOffset) {
                this.offsetAttribute.setOffset(startOffset, this.savedEndOffset);
            }
            else {
                this.offsetAttribute.setOffset(this.savedStartOffset, this.savedEndOffset);
            }
        }
        else {
            this.offsetAttribute.setOffset(startOffset, endOffset);
        }
        this.posIncAttribute.setPositionIncrement(this.position(false));
        this.typeAttribute.setType(this.savedType);
    }
    
    private int position(final boolean inject) {
        final int posInc = this.accumPosInc;
        if (this.hasOutputToken) {
            this.accumPosInc = 0;
            return inject ? 0 : Math.max(1, posInc);
        }
        this.hasOutputToken = true;
        if (!this.hasOutputFollowingOriginal) {
            this.hasOutputFollowingOriginal = true;
            return 0;
        }
        this.accumPosInc = 0;
        return Math.max(1, posInc);
    }
    
    static boolean isAlpha(final int type) {
        return (type & 0x3) != 0x0;
    }
    
    static boolean isDigit(final int type) {
        return (type & 0x4) != 0x0;
    }
    
    static boolean isSubwordDelim(final int type) {
        return (type & 0x8) != 0x0;
    }
    
    static boolean isUpper(final int type) {
        return (type & 0x2) != 0x0;
    }
    
    private boolean has(final int flag) {
        return (this.flags & flag) != 0x0;
    }
    
    final class WordDelimiterConcatenation
    {
        final StringBuilder buffer;
        int startOffset;
        int endOffset;
        int type;
        int subwordCount;
        
        WordDelimiterConcatenation() {
            this.buffer = new StringBuilder();
        }
        
        void append(final char[] text, final int offset, final int length) {
            this.buffer.append(text, offset, length);
            ++this.subwordCount;
        }
        
        void write() {
            Lucene47WordDelimiterFilter.this.clearAttributes();
            if (Lucene47WordDelimiterFilter.this.termAttribute.length() < this.buffer.length()) {
                Lucene47WordDelimiterFilter.this.termAttribute.resizeBuffer(this.buffer.length());
            }
            final char[] termbuffer = Lucene47WordDelimiterFilter.this.termAttribute.buffer();
            this.buffer.getChars(0, this.buffer.length(), termbuffer, 0);
            Lucene47WordDelimiterFilter.this.termAttribute.setLength(this.buffer.length());
            if (Lucene47WordDelimiterFilter.this.hasIllegalOffsets) {
                Lucene47WordDelimiterFilter.this.offsetAttribute.setOffset(Lucene47WordDelimiterFilter.this.savedStartOffset, Lucene47WordDelimiterFilter.this.savedEndOffset);
            }
            else {
                Lucene47WordDelimiterFilter.this.offsetAttribute.setOffset(this.startOffset, this.endOffset);
            }
            Lucene47WordDelimiterFilter.this.posIncAttribute.setPositionIncrement(Lucene47WordDelimiterFilter.this.position(true));
            Lucene47WordDelimiterFilter.this.typeAttribute.setType(Lucene47WordDelimiterFilter.this.savedType);
            Lucene47WordDelimiterFilter.this.accumPosInc = 0;
        }
        
        boolean isEmpty() {
            return this.buffer.length() == 0;
        }
        
        void clear() {
            this.buffer.setLength(0);
            final int n = 0;
            this.subwordCount = n;
            this.type = n;
            this.endOffset = n;
            this.startOffset = n;
        }
        
        void writeAndClear() {
            this.write();
            this.clear();
        }
    }
}
