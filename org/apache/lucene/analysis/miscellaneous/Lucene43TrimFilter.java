package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

@Deprecated
public final class Lucene43TrimFilter extends TokenFilter
{
    final boolean updateOffsets;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    
    public Lucene43TrimFilter(final TokenStream in, final boolean updateOffsets) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.updateOffsets = updateOffsets;
    }
    
    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        final char[] termBuffer = this.termAtt.buffer();
        final int len = this.termAtt.length();
        if (len == 0) {
            return true;
        }
        int start = 0;
        int end = 0;
        int endOff = 0;
        for (start = 0; start < len && termBuffer[start] <= ' '; ++start) {}
        for (end = len; end >= start && termBuffer[end - 1] <= ' '; --end) {
            ++endOff;
        }
        if (start > 0 || end < len) {
            if (start < end) {
                this.termAtt.copyBuffer(termBuffer, start, end - start);
            }
            else {
                this.termAtt.setEmpty();
            }
            if (this.updateOffsets && len == this.offsetAtt.endOffset() - this.offsetAtt.startOffset()) {
                final int newStart = this.offsetAtt.startOffset() + start;
                final int newEnd = this.offsetAtt.endOffset() - ((start < end) ? endOff : 0);
                this.offsetAtt.setOffset(newStart, newEnd);
            }
        }
        return true;
    }
}
