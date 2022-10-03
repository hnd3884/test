package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.spans.SpanBoostQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public class SpanTermBuilder extends SpanBuilderBase
{
    @Override
    public SpanQuery getSpanQuery(final Element e) throws ParserException {
        final String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        final String value = DOMUtils.getNonBlankTextOrFail(e);
        final SpanTermQuery stq = new SpanTermQuery(new Term(fieldName, value));
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        return (SpanQuery)new SpanBoostQuery((SpanQuery)stq, boost);
    }
}
