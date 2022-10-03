package org.apache.lucene.analysis.pattern;

import java.io.Reader;
import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

public final class PatternTokenizer extends Tokenizer
{
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final StringBuilder str;
    private int index;
    private final int group;
    private final Matcher matcher;
    final char[] buffer;
    
    public PatternTokenizer(final Pattern pattern, final int group) {
        this(PatternTokenizer.DEFAULT_TOKEN_ATTRIBUTE_FACTORY, pattern, group);
    }
    
    public PatternTokenizer(final AttributeFactory factory, final Pattern pattern, final int group) {
        super(factory);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.str = new StringBuilder();
        this.buffer = new char[8192];
        this.group = group;
        this.matcher = pattern.matcher("");
        if (group >= 0 && group > this.matcher.groupCount()) {
            throw new IllegalArgumentException("invalid group specified: pattern only has: " + this.matcher.groupCount() + " capturing groups");
        }
    }
    
    public boolean incrementToken() {
        if (this.index >= this.str.length()) {
            return false;
        }
        this.clearAttributes();
        if (this.group >= 0) {
            while (this.matcher.find()) {
                this.index = this.matcher.start(this.group);
                final int endIndex = this.matcher.end(this.group);
                if (this.index == endIndex) {
                    continue;
                }
                this.termAtt.setEmpty().append((CharSequence)this.str, this.index, endIndex);
                this.offsetAtt.setOffset(this.correctOffset(this.index), this.correctOffset(endIndex));
                return true;
            }
            this.index = Integer.MAX_VALUE;
            return false;
        }
        while (this.matcher.find()) {
            if (this.matcher.start() - this.index > 0) {
                this.termAtt.setEmpty().append((CharSequence)this.str, this.index, this.matcher.start());
                this.offsetAtt.setOffset(this.correctOffset(this.index), this.correctOffset(this.matcher.start()));
                this.index = this.matcher.end();
                return true;
            }
            this.index = this.matcher.end();
        }
        if (this.str.length() - this.index == 0) {
            this.index = Integer.MAX_VALUE;
            return false;
        }
        this.termAtt.setEmpty().append((CharSequence)this.str, this.index, this.str.length());
        this.offsetAtt.setOffset(this.correctOffset(this.index), this.correctOffset(this.str.length()));
        this.index = Integer.MAX_VALUE;
        return true;
    }
    
    public void end() throws IOException {
        super.end();
        final int ofs = this.correctOffset(this.str.length());
        this.offsetAtt.setOffset(ofs, ofs);
    }
    
    public void close() throws IOException {
        try {
            super.close();
        }
        finally {
            this.str.setLength(0);
            this.str.trimToSize();
        }
    }
    
    public void reset() throws IOException {
        super.reset();
        this.fillBuffer(this.input);
        this.matcher.reset(this.str);
        this.index = 0;
    }
    
    private void fillBuffer(final Reader input) throws IOException {
        this.str.setLength(0);
        int len;
        while ((len = input.read(this.buffer)) > 0) {
            this.str.append(this.buffer, 0, len);
        }
    }
}
