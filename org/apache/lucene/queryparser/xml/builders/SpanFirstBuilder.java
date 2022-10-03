package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.spans.SpanBoostQuery;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public class SpanFirstBuilder extends SpanBuilderBase
{
    private final SpanQueryBuilder factory;
    
    public SpanFirstBuilder(final SpanQueryBuilder factory) {
        this.factory = factory;
    }
    
    @Override
    public SpanQuery getSpanQuery(final Element e) throws ParserException {
        final int end = DOMUtils.getAttribute(e, "end", 1);
        final Element child = DOMUtils.getFirstChildElement(e);
        final SpanQuery q = this.factory.getSpanQuery(child);
        final SpanFirstQuery sfq = new SpanFirstQuery(q, end);
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        return (SpanQuery)new SpanBoostQuery((SpanQuery)sfq, boost);
    }
}
