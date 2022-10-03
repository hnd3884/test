package org.apache.lucene.analysis.path;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.util.AttributeFactory;
import java.util.List;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

public class ReversePathHierarchyTokenizer extends Tokenizer
{
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final char DEFAULT_DELIMITER = '/';
    public static final int DEFAULT_SKIP = 0;
    private final char delimiter;
    private final char replacement;
    private final int skip;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posAtt;
    private int endPosition;
    private int finalOffset;
    private int skipped;
    private StringBuilder resultToken;
    private List<Integer> delimiterPositions;
    private int delimitersCount;
    private char[] resultTokenBuffer;
    
    public ReversePathHierarchyTokenizer() {
        this(1024, '/', '/', 0);
    }
    
    public ReversePathHierarchyTokenizer(final int skip) {
        this(1024, '/', '/', skip);
    }
    
    public ReversePathHierarchyTokenizer(final int bufferSize, final char delimiter) {
        this(bufferSize, delimiter, delimiter, 0);
    }
    
    public ReversePathHierarchyTokenizer(final char delimiter, final char replacement) {
        this(1024, delimiter, replacement, 0);
    }
    
    public ReversePathHierarchyTokenizer(final int bufferSize, final char delimiter, final char replacement) {
        this(bufferSize, delimiter, replacement, 0);
    }
    
    public ReversePathHierarchyTokenizer(final char delimiter, final int skip) {
        this(1024, delimiter, delimiter, skip);
    }
    
    public ReversePathHierarchyTokenizer(final char delimiter, final char replacement, final int skip) {
        this(1024, delimiter, replacement, skip);
    }
    
    public ReversePathHierarchyTokenizer(final AttributeFactory factory, final char delimiter, final char replacement, final int skip) {
        this(factory, 1024, delimiter, replacement, skip);
    }
    
    public ReversePathHierarchyTokenizer(final int bufferSize, final char delimiter, final char replacement, final int skip) {
        this(ReversePathHierarchyTokenizer.DEFAULT_TOKEN_ATTRIBUTE_FACTORY, bufferSize, delimiter, replacement, skip);
    }
    
    public ReversePathHierarchyTokenizer(final AttributeFactory factory, final int bufferSize, final char delimiter, final char replacement, final int skip) {
        super(factory);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.endPosition = 0;
        this.finalOffset = 0;
        this.skipped = 0;
        this.delimitersCount = -1;
        if (bufferSize < 0) {
            throw new IllegalArgumentException("bufferSize cannot be negative");
        }
        if (skip < 0) {
            throw new IllegalArgumentException("skip cannot be negative");
        }
        this.termAtt.resizeBuffer(bufferSize);
        this.delimiter = delimiter;
        this.replacement = replacement;
        this.skip = skip;
        this.resultToken = new StringBuilder(bufferSize);
        this.resultTokenBuffer = new char[bufferSize];
        this.delimiterPositions = new ArrayList<Integer>(bufferSize / 10);
    }
    
    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        if (this.delimitersCount == -1) {
            int length = 0;
            this.delimiterPositions.add(0);
            while (true) {
                final int c = this.input.read();
                if (c < 0) {
                    break;
                }
                ++length;
                if (c == this.delimiter) {
                    this.delimiterPositions.add(length);
                    this.resultToken.append(this.replacement);
                }
                else {
                    this.resultToken.append((char)c);
                }
            }
            this.delimitersCount = this.delimiterPositions.size();
            if (this.delimiterPositions.get(this.delimitersCount - 1) < length) {
                this.delimiterPositions.add(length);
                ++this.delimitersCount;
            }
            if (this.resultTokenBuffer.length < this.resultToken.length()) {
                this.resultTokenBuffer = new char[this.resultToken.length()];
            }
            this.resultToken.getChars(0, this.resultToken.length(), this.resultTokenBuffer, 0);
            this.resultToken.setLength(0);
            final int idx = this.delimitersCount - 1 - this.skip;
            if (idx >= 0) {
                this.endPosition = this.delimiterPositions.get(idx);
            }
            this.finalOffset = this.correctOffset(length);
            this.posAtt.setPositionIncrement(1);
        }
        else {
            this.posAtt.setPositionIncrement(0);
        }
        if (this.skipped < this.delimitersCount - this.skip - 1) {
            final int start = this.delimiterPositions.get(this.skipped);
            this.termAtt.copyBuffer(this.resultTokenBuffer, start, this.endPosition - start);
            this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(this.endPosition));
            ++this.skipped;
            return true;
        }
        return false;
    }
    
    public final void end() throws IOException {
        super.end();
        this.offsetAtt.setOffset(this.finalOffset, this.finalOffset);
    }
    
    public void reset() throws IOException {
        super.reset();
        this.resultToken.setLength(0);
        this.finalOffset = 0;
        this.endPosition = 0;
        this.skipped = 0;
        this.delimitersCount = -1;
        this.delimiterPositions.clear();
    }
}
