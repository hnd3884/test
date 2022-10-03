package org.apache.lucene.analysis.pattern;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.CharsRefBuilder;
import java.util.regex.Matcher;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class PatternCaptureGroupTokenFilter extends TokenFilter
{
    private final CharTermAttribute charTermAttr;
    private final PositionIncrementAttribute posAttr;
    private AttributeSource.State state;
    private final Matcher[] matchers;
    private final CharsRefBuilder spare;
    private final int[] groupCounts;
    private final boolean preserveOriginal;
    private int[] currentGroup;
    private int currentMatcher;
    
    public PatternCaptureGroupTokenFilter(final TokenStream input, final boolean preserveOriginal, final Pattern... patterns) {
        super(input);
        this.charTermAttr = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.posAttr = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.spare = new CharsRefBuilder();
        this.preserveOriginal = preserveOriginal;
        this.matchers = new Matcher[patterns.length];
        this.groupCounts = new int[patterns.length];
        this.currentGroup = new int[patterns.length];
        for (int i = 0; i < patterns.length; ++i) {
            this.matchers[i] = patterns[i].matcher("");
            this.groupCounts[i] = this.matchers[i].groupCount();
            this.currentGroup[i] = -1;
        }
    }
    
    private boolean nextCapture() {
        int min_offset = Integer.MAX_VALUE;
        this.currentMatcher = -1;
        for (int i = 0; i < this.matchers.length; ++i) {
            final Matcher matcher = this.matchers[i];
            if (this.currentGroup[i] == -1) {
                this.currentGroup[i] = (matcher.find() ? 1 : 0);
            }
            if (this.currentGroup[i] != 0) {
                while (this.currentGroup[i] < this.groupCounts[i] + 1) {
                    final int start = matcher.start(this.currentGroup[i]);
                    final int end = matcher.end(this.currentGroup[i]);
                    if (start == end || (this.preserveOriginal && start == 0 && this.spare.length() == end)) {
                        final int[] currentGroup = this.currentGroup;
                        final int n = i;
                        ++currentGroup[n];
                    }
                    else {
                        if (start < min_offset) {
                            min_offset = start;
                            this.currentMatcher = i;
                            break;
                        }
                        break;
                    }
                }
                if (this.currentGroup[i] == this.groupCounts[i] + 1) {
                    this.currentGroup[i] = -1;
                    --i;
                }
            }
        }
        return this.currentMatcher != -1;
    }
    
    public boolean incrementToken() throws IOException {
        if (this.currentMatcher != -1 && this.nextCapture()) {
            assert this.state != null;
            this.clearAttributes();
            this.restoreState(this.state);
            final int start = this.matchers[this.currentMatcher].start(this.currentGroup[this.currentMatcher]);
            final int end = this.matchers[this.currentMatcher].end(this.currentGroup[this.currentMatcher]);
            this.posAttr.setPositionIncrement(0);
            this.charTermAttr.copyBuffer(this.spare.chars(), start, end - start);
            final int[] currentGroup = this.currentGroup;
            final int currentMatcher = this.currentMatcher;
            ++currentGroup[currentMatcher];
            return true;
        }
        else {
            if (!this.input.incrementToken()) {
                return false;
            }
            final char[] buffer = this.charTermAttr.buffer();
            final int length = this.charTermAttr.length();
            this.spare.copyChars(buffer, 0, length);
            this.state = this.captureState();
            for (int i = 0; i < this.matchers.length; ++i) {
                this.matchers[i].reset((CharSequence)this.spare.get());
                this.currentGroup[i] = -1;
            }
            if (this.preserveOriginal) {
                this.currentMatcher = 0;
            }
            else if (this.nextCapture()) {
                final int start2 = this.matchers[this.currentMatcher].start(this.currentGroup[this.currentMatcher]);
                final int end2 = this.matchers[this.currentMatcher].end(this.currentGroup[this.currentMatcher]);
                if (start2 == 0) {
                    this.charTermAttr.setLength(end2);
                }
                else {
                    this.charTermAttr.copyBuffer(this.spare.chars(), start2, end2 - start2);
                }
                final int[] currentGroup2 = this.currentGroup;
                final int currentMatcher2 = this.currentMatcher;
                ++currentGroup2[currentMatcher2];
            }
            return true;
        }
    }
    
    public void reset() throws IOException {
        super.reset();
        this.state = null;
        this.currentMatcher = -1;
    }
}
