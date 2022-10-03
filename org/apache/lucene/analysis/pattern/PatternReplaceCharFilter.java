package org.apache.lucene.analysis.pattern;

import java.util.regex.Matcher;
import java.io.StringReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.charfilter.BaseCharFilter;

public class PatternReplaceCharFilter extends BaseCharFilter
{
    private final Pattern pattern;
    private final String replacement;
    private Reader transformedInput;
    
    public PatternReplaceCharFilter(final Pattern pattern, final String replacement, final Reader in) {
        super(in);
        this.pattern = pattern;
        this.replacement = replacement;
    }
    
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        if (this.transformedInput == null) {
            this.fill();
        }
        return this.transformedInput.read(cbuf, off, len);
    }
    
    private void fill() throws IOException {
        final StringBuilder buffered = new StringBuilder();
        final char[] temp = new char[1024];
        for (int cnt = this.input.read(temp); cnt > 0; cnt = this.input.read(temp)) {
            buffered.append(temp, 0, cnt);
        }
        this.transformedInput = new StringReader(this.processPattern(buffered).toString());
    }
    
    public int read() throws IOException {
        if (this.transformedInput == null) {
            this.fill();
        }
        return this.transformedInput.read();
    }
    
    @Override
    protected int correct(final int currentOff) {
        return Math.max(0, super.correct(currentOff));
    }
    
    CharSequence processPattern(final CharSequence input) {
        final Matcher m = this.pattern.matcher(input);
        final StringBuffer cumulativeOutput = new StringBuffer();
        int cumulative = 0;
        int lastMatchEnd = 0;
        while (m.find()) {
            final int groupSize = m.end() - m.start();
            final int skippedSize = m.start() - lastMatchEnd;
            lastMatchEnd = m.end();
            final int lengthBeforeReplacement = cumulativeOutput.length() + skippedSize;
            m.appendReplacement(cumulativeOutput, this.replacement);
            final int replacementSize = cumulativeOutput.length() - lengthBeforeReplacement;
            if (groupSize != replacementSize) {
                if (replacementSize < groupSize) {
                    cumulative += groupSize - replacementSize;
                    final int atIndex = lengthBeforeReplacement + replacementSize;
                    this.addOffCorrectMap(atIndex, cumulative);
                }
                else {
                    for (int i = groupSize; i < replacementSize; ++i) {
                        this.addOffCorrectMap(lengthBeforeReplacement + i, --cumulative);
                    }
                }
            }
        }
        m.appendTail(cumulativeOutput);
        return cumulativeOutput;
    }
}
