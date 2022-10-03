package org.apache.lucene.analysis.miscellaneous;

import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.util.Collection;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;

public final class CapitalizationFilter extends TokenFilter
{
    public static final int DEFAULT_MAX_WORD_COUNT = Integer.MAX_VALUE;
    public static final int DEFAULT_MAX_TOKEN_LENGTH = Integer.MAX_VALUE;
    private final boolean onlyFirstWord;
    private final CharArraySet keep;
    private final boolean forceFirstLetter;
    private final Collection<char[]> okPrefix;
    private final int minWordLength;
    private final int maxWordCount;
    private final int maxTokenLength;
    private final CharTermAttribute termAtt;
    
    public CapitalizationFilter(final TokenStream in) {
        this(in, true, null, true, null, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public CapitalizationFilter(final TokenStream in, final boolean onlyFirstWord, final CharArraySet keep, final boolean forceFirstLetter, final Collection<char[]> okPrefix, final int minWordLength, final int maxWordCount, final int maxTokenLength) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.onlyFirstWord = onlyFirstWord;
        this.keep = keep;
        this.forceFirstLetter = forceFirstLetter;
        this.okPrefix = okPrefix;
        if (minWordLength < 0) {
            throw new IllegalArgumentException("minWordLength must be greater than or equal to zero");
        }
        if (maxWordCount < 1) {
            throw new IllegalArgumentException("maxWordCount must be greater than zero");
        }
        if (maxTokenLength < 1) {
            throw new IllegalArgumentException("maxTokenLength must be greater than zero");
        }
        this.minWordLength = minWordLength;
        this.maxWordCount = maxWordCount;
        this.maxTokenLength = maxTokenLength;
    }
    
    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        final char[] termBuffer = this.termAtt.buffer();
        final int termBufferLength = this.termAtt.length();
        char[] backup = null;
        if (this.maxWordCount < Integer.MAX_VALUE) {
            backup = new char[termBufferLength];
            System.arraycopy(termBuffer, 0, backup, 0, termBufferLength);
        }
        if (termBufferLength < this.maxTokenLength) {
            int wordCount = 0;
            int lastWordStart = 0;
            for (int i = 0; i < termBufferLength; ++i) {
                final char c = termBuffer[i];
                if (c <= ' ' || c == '.') {
                    final int len = i - lastWordStart;
                    if (len > 0) {
                        this.processWord(termBuffer, lastWordStart, len, wordCount++);
                        lastWordStart = i + 1;
                        ++i;
                    }
                }
            }
            if (lastWordStart < termBufferLength) {
                this.processWord(termBuffer, lastWordStart, termBufferLength - lastWordStart, wordCount++);
            }
            if (wordCount > this.maxWordCount) {
                this.termAtt.copyBuffer(backup, 0, termBufferLength);
            }
        }
        return true;
    }
    
    private void processWord(final char[] buffer, final int offset, final int length, final int wordCount) {
        if (length < 1) {
            return;
        }
        if (this.onlyFirstWord && wordCount > 0) {
            for (int i = 0; i < length; ++i) {
                buffer[offset + i] = Character.toLowerCase(buffer[offset + i]);
            }
            return;
        }
        if (this.keep != null && this.keep.contains(buffer, offset, length)) {
            if (wordCount == 0 && this.forceFirstLetter) {
                buffer[offset] = Character.toUpperCase(buffer[offset]);
            }
            return;
        }
        if (length < this.minWordLength) {
            return;
        }
        if (this.okPrefix != null) {
            for (final char[] prefix : this.okPrefix) {
                if (length >= prefix.length) {
                    boolean match = true;
                    for (int j = 0; j < prefix.length; ++j) {
                        if (prefix[j] != buffer[offset + j]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return;
                    }
                    continue;
                }
            }
        }
        buffer[offset] = Character.toUpperCase(buffer[offset]);
        for (int i = 1; i < length; ++i) {
            buffer[offset + i] = Character.toLowerCase(buffer[offset + i]);
        }
    }
}
