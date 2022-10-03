package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.analysis.TokenStream;
import java.util.List;
import java.io.IOException;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.spans.SpanBoostQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import java.util.ArrayList;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;
import org.apache.lucene.analysis.Analyzer;

public class SpanOrTermsBuilder extends SpanBuilderBase
{
    private final Analyzer analyzer;
    
    public SpanOrTermsBuilder(final Analyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    @Override
    public SpanQuery getSpanQuery(final Element e) throws ParserException {
        final String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        final String value = DOMUtils.getNonBlankTextOrFail(e);
        final List<SpanQuery> clausesList = new ArrayList<SpanQuery>();
        try (final TokenStream ts = this.analyzer.tokenStream(fieldName, value)) {
            final TermToBytesRefAttribute termAtt = (TermToBytesRefAttribute)ts.addAttribute((Class)TermToBytesRefAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                final SpanTermQuery stq = new SpanTermQuery(new Term(fieldName, BytesRef.deepCopyOf(termAtt.getBytesRef())));
                clausesList.add((SpanQuery)stq);
            }
            ts.end();
            final SpanOrQuery soq = new SpanOrQuery((SpanQuery[])clausesList.toArray(new SpanQuery[clausesList.size()]));
            final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
            return (SpanQuery)new SpanBoostQuery((SpanQuery)soq, boost);
        }
        catch (final IOException ioe) {
            throw new ParserException("IOException parsing value:" + value);
        }
    }
}
