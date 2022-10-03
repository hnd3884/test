package org.apache.lucene.search.spans;

import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;

public class SpanPositionRangeQuery extends SpanPositionCheckQuery
{
    protected int start;
    protected int end;
    
    public SpanPositionRangeQuery(final SpanQuery match, final int start, final int end) {
        super(match);
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected FilterSpans.AcceptStatus acceptPosition(final Spans spans) throws IOException {
        assert spans.startPosition() != spans.endPosition();
        final FilterSpans.AcceptStatus res = (spans.startPosition() >= this.end) ? FilterSpans.AcceptStatus.NO_MORE_IN_CURRENT_DOC : ((spans.startPosition() >= this.start && spans.endPosition() <= this.end) ? FilterSpans.AcceptStatus.YES : FilterSpans.AcceptStatus.NO);
        return res;
    }
    
    public int getStart() {
        return this.start;
    }
    
    public int getEnd() {
        return this.end;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("spanPosRange(");
        buffer.append(this.match.toString(field));
        buffer.append(", ").append(this.start).append(", ");
        buffer.append(this.end);
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final SpanPositionRangeQuery other = (SpanPositionRangeQuery)o;
        return this.end == other.end && this.start == other.start;
    }
    
    @Override
    public int hashCode() {
        int h = super.hashCode() ^ this.end;
        h = (h * 127 ^ this.start);
        return h;
    }
}
