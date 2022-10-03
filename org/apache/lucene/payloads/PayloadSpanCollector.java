package org.apache.lucene.payloads;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.PostingsEnum;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.search.spans.SpanCollector;

public class PayloadSpanCollector implements SpanCollector
{
    private final Collection<byte[]> payloads;
    
    public PayloadSpanCollector() {
        this.payloads = new ArrayList<byte[]>();
    }
    
    public void collectLeaf(final PostingsEnum postings, final int position, final Term term) throws IOException {
        final BytesRef payload = postings.getPayload();
        if (payload == null) {
            return;
        }
        final byte[] bytes = new byte[payload.length];
        System.arraycopy(payload.bytes, payload.offset, bytes, 0, payload.length);
        this.payloads.add(bytes);
    }
    
    public void reset() {
        this.payloads.clear();
    }
    
    public Collection<byte[]> getPayloads() {
        return this.payloads;
    }
}
