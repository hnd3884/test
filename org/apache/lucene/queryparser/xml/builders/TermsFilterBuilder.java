package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.analysis.TokenStream;
import java.util.List;
import org.apache.lucene.queries.TermsFilter;
import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.util.BytesRef;
import java.util.ArrayList;
import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.xml.FilterBuilder;

public class TermsFilterBuilder implements FilterBuilder
{
    private final Analyzer analyzer;
    
    public TermsFilterBuilder(final Analyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    @Override
    public Filter getFilter(final Element e) throws ParserException {
        final List<BytesRef> terms = new ArrayList<BytesRef>();
        final String text = DOMUtils.getNonBlankTextOrFail(e);
        final String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        try (final TokenStream ts = this.analyzer.tokenStream(fieldName, text)) {
            final TermToBytesRefAttribute termAtt = (TermToBytesRefAttribute)ts.addAttribute((Class)TermToBytesRefAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                terms.add(BytesRef.deepCopyOf(termAtt.getBytesRef()));
            }
            ts.end();
        }
        catch (final IOException ioe) {
            throw new RuntimeException("Error constructing terms from index:" + ioe);
        }
        return (Filter)new TermsFilter(fieldName, (List)terms);
    }
}
