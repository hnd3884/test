package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.spans.SpanBoostQuery;
import org.apache.lucene.queries.payloads.PayloadFunction;
import org.apache.lucene.queries.payloads.PayloadTermQuery;
import org.apache.lucene.queries.payloads.AveragePayloadFunction;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public class BoostingTermBuilder extends SpanBuilderBase
{
    @Override
    public SpanQuery getSpanQuery(final Element e) throws ParserException {
        final String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        final String value = DOMUtils.getNonBlankTextOrFail(e);
        SpanQuery btq = (SpanQuery)new PayloadTermQuery(new Term(fieldName, value), (PayloadFunction)new AveragePayloadFunction());
        btq = (SpanQuery)new SpanBoostQuery(btq, DOMUtils.getAttribute(e, "boost", 1.0f));
        return btq;
    }
}
