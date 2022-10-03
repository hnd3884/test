package org.apache.lucene.search.spans;

import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;

public class SpanFirstQuery extends SpanPositionRangeQuery
{
    public SpanFirstQuery(final SpanQuery match, final int end) {
        super(match, 0, end);
    }
    
    @Override
    protected FilterSpans.AcceptStatus acceptPosition(final Spans spans) throws IOException {
        assert spans.startPosition() != spans.endPosition() : "start equals end: " + spans.startPosition();
        if (spans.startPosition() >= this.end) {
            return FilterSpans.AcceptStatus.NO_MORE_IN_CURRENT_DOC;
        }
        if (spans.endPosition() <= this.end) {
            return FilterSpans.AcceptStatus.YES;
        }
        return FilterSpans.AcceptStatus.NO;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("spanFirst(");
        buffer.append(this.match.toString(field));
        buffer.append(", ");
        buffer.append(this.end);
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
}
