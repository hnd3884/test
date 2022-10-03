package org.apache.lucene.analysis.path;

import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

public class PathHierarchyTokenizer extends Tokenizer
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
    private int startPosition;
    private int skipped;
    private boolean endDelimiter;
    private StringBuilder resultToken;
    private int charsRead;
    
    public PathHierarchyTokenizer() {
        this(1024, '/', '/', 0);
    }
    
    public PathHierarchyTokenizer(final int skip) {
        this(1024, '/', '/', skip);
    }
    
    public PathHierarchyTokenizer(final int bufferSize, final char delimiter) {
        this(bufferSize, delimiter, delimiter, 0);
    }
    
    public PathHierarchyTokenizer(final char delimiter, final char replacement) {
        this(1024, delimiter, replacement, 0);
    }
    
    public PathHierarchyTokenizer(final char delimiter, final char replacement, final int skip) {
        this(1024, delimiter, replacement, skip);
    }
    
    public PathHierarchyTokenizer(final AttributeFactory factory, final char delimiter, final char replacement, final int skip) {
        this(factory, 1024, delimiter, replacement, skip);
    }
    
    public PathHierarchyTokenizer(final int bufferSize, final char delimiter, final char replacement, final int skip) {
        this(PathHierarchyTokenizer.DEFAULT_TOKEN_ATTRIBUTE_FACTORY, bufferSize, delimiter, replacement, skip);
    }
    
    public PathHierarchyTokenizer(final AttributeFactory factory, final int bufferSize, final char delimiter, final char replacement, final int skip) {
        super(factory);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.startPosition = 0;
        this.skipped = 0;
        this.endDelimiter = false;
        this.charsRead = 0;
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
    }
    
    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        this.termAtt.append(this.resultToken);
        if (this.resultToken.length() == 0) {
            this.posAtt.setPositionIncrement(1);
        }
        else {
            this.posAtt.setPositionIncrement(0);
        }
        int length = 0;
        boolean added = false;
        if (this.endDelimiter) {
            this.termAtt.append(this.replacement);
            ++length;
            this.endDelimiter = false;
            added = true;
        }
        while (true) {
            final int c = this.input.read();
            if (c >= 0) {
                ++this.charsRead;
                if (!added) {
                    added = true;
                    ++this.skipped;
                    if (this.skipped > this.skip) {
                        this.termAtt.append((c == this.delimiter) ? this.replacement : ((char)c));
                        ++length;
                    }
                    else {
                        ++this.startPosition;
                    }
                }
                else if (c == this.delimiter) {
                    if (this.skipped > this.skip) {
                        this.endDelimiter = true;
                        length += this.resultToken.length();
                        this.termAtt.setLength(length);
                        this.offsetAtt.setOffset(this.correctOffset(this.startPosition), this.correctOffset(this.startPosition + length));
                        this.resultToken.setLength(0);
                        this.resultToken.append(this.termAtt.buffer(), 0, length);
                        return true;
                    }
                    ++this.skipped;
                    if (this.skipped > this.skip) {
                        this.termAtt.append(this.replacement);
                        ++length;
                    }
                    else {
                        ++this.startPosition;
                    }
                }
                else if (this.skipped > this.skip) {
                    this.termAtt.append((char)c);
                    ++length;
                }
                else {
                    ++this.startPosition;
                }
            }
            else {
                if (this.skipped > this.skip) {
                    length += this.resultToken.length();
                    this.termAtt.setLength(length);
                    this.offsetAtt.setOffset(this.correctOffset(this.startPosition), this.correctOffset(this.startPosition + length));
                    if (added) {
                        this.resultToken.setLength(0);
                        this.resultToken.append(this.termAtt.buffer(), 0, length);
                    }
                    return added;
                }
                return false;
            }
        }
    }
    
    public final void end() throws IOException {
        super.end();
        final int finalOffset = this.correctOffset(this.charsRead);
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }
    
    public void reset() throws IOException {
        super.reset();
        this.resultToken.setLength(0);
        this.charsRead = 0;
        this.endDelimiter = false;
        this.skipped = 0;
        this.startPosition = 0;
    }
}
