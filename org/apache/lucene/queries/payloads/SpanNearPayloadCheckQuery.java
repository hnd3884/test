package org.apache.lucene.queries.payloads;

import org.apache.lucene.search.Query;
import java.util.Iterator;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.util.BytesRef;
import java.util.List;
import org.apache.lucene.search.spans.SpanNearQuery;

@Deprecated
public class SpanNearPayloadCheckQuery extends SpanPayloadCheckQuery
{
    public SpanNearPayloadCheckQuery(final SpanNearQuery match, final List<BytesRef> payloadToMatch) {
        super((SpanQuery)match, payloadToMatch);
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("spanPayCheck(");
        buffer.append(this.match.toString(field));
        buffer.append(", payloadRef: ");
        for (final BytesRef bytes : this.payloadToMatch) {
            buffer.append(Term.toString(bytes));
            buffer.append(';');
        }
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    public SpanNearPayloadCheckQuery clone() {
        final SpanNearPayloadCheckQuery result = new SpanNearPayloadCheckQuery((SpanNearQuery)this.match.clone(), this.payloadToMatch);
        result.setBoost(this.getBoost());
        return result;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final SpanNearPayloadCheckQuery other = (SpanNearPayloadCheckQuery)o;
        return this.payloadToMatch.equals(other.payloadToMatch);
    }
    
    @Override
    public int hashCode() {
        int h = super.hashCode();
        h = (h * 15 ^ this.payloadToMatch.hashCode());
        return h;
    }
}
