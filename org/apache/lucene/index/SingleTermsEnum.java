package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;

public final class SingleTermsEnum extends FilteredTermsEnum
{
    private final BytesRef singleRef;
    
    public SingleTermsEnum(final TermsEnum tenum, final BytesRef termText) {
        super(tenum);
        this.setInitialSeekTerm(this.singleRef = termText);
    }
    
    @Override
    protected AcceptStatus accept(final BytesRef term) {
        return term.equals(this.singleRef) ? AcceptStatus.YES : AcceptStatus.END;
    }
}
