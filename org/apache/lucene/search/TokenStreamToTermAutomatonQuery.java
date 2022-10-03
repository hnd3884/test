package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.TokenStream;

public class TokenStreamToTermAutomatonQuery
{
    private boolean preservePositionIncrements;
    
    public TokenStreamToTermAutomatonQuery() {
        this.preservePositionIncrements = true;
    }
    
    public void setPreservePositionIncrements(final boolean enablePositionIncrements) {
        this.preservePositionIncrements = enablePositionIncrements;
    }
    
    public TermAutomatonQuery toQuery(final String field, final TokenStream in) throws IOException {
        final TermToBytesRefAttribute termBytesAtt = (TermToBytesRefAttribute)in.addAttribute((Class)TermToBytesRefAttribute.class);
        final PositionIncrementAttribute posIncAtt = (PositionIncrementAttribute)in.addAttribute((Class)PositionIncrementAttribute.class);
        final PositionLengthAttribute posLengthAtt = (PositionLengthAttribute)in.addAttribute((Class)PositionLengthAttribute.class);
        final OffsetAttribute offsetAtt = (OffsetAttribute)in.addAttribute((Class)OffsetAttribute.class);
        in.reset();
        final TermAutomatonQuery query = new TermAutomatonQuery(field);
        int pos = -1;
        final int lastPos = 0;
        int maxOffset = 0;
        int maxPos = -1;
        int state = -1;
        while (in.incrementToken()) {
            int posInc = posIncAtt.getPositionIncrement();
            if (!this.preservePositionIncrements && posInc > 1) {
                posInc = 1;
            }
            assert posInc > 0;
            if (posInc > 1) {
                throw new IllegalArgumentException("cannot handle holes; to accept any term, use '*' term");
            }
            if (posInc > 0) {
                pos += posInc;
            }
            int endPos;
            for (endPos = pos + posLengthAtt.getPositionLength(); state < endPos; state = query.createState()) {}
            final BytesRef term = termBytesAtt.getBytesRef();
            if (term.length == 1 && term.bytes[term.offset] == 42) {
                query.addAnyTransition(pos, endPos);
            }
            else {
                query.addTransition(pos, endPos, term);
            }
            maxOffset = Math.max(maxOffset, offsetAtt.endOffset());
            maxPos = Math.max(maxPos, endPos);
        }
        in.end();
        query.setAccept(state, true);
        query.finish();
        return query;
    }
}
