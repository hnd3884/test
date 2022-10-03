package org.apache.lucene.sandbox.queries.regex;

import org.apache.lucene.util.StringHelper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.FilteredTermsEnum;

@Deprecated
public class RegexTermsEnum extends FilteredTermsEnum
{
    private RegexCapabilities.RegexMatcher regexImpl;
    private final BytesRef prefixRef;
    
    public RegexTermsEnum(final TermsEnum tenum, final Term term, final RegexCapabilities regexCap) {
        super(tenum);
        final String text = term.text();
        this.regexImpl = regexCap.compile(text);
        String pre = this.regexImpl.prefix();
        if (pre == null) {
            pre = "";
        }
        this.setInitialSeekTerm(this.prefixRef = new BytesRef((CharSequence)pre));
    }
    
    protected FilteredTermsEnum.AcceptStatus accept(final BytesRef term) {
        if (StringHelper.startsWith(term, this.prefixRef)) {
            return this.regexImpl.match(term) ? FilteredTermsEnum.AcceptStatus.YES : FilteredTermsEnum.AcceptStatus.NO;
        }
        return FilteredTermsEnum.AcceptStatus.NO;
    }
}
