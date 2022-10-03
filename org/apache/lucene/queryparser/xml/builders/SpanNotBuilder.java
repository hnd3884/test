package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.spans.SpanBoostQuery;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public class SpanNotBuilder extends SpanBuilderBase
{
    private final SpanQueryBuilder factory;
    
    public SpanNotBuilder(final SpanQueryBuilder factory) {
        this.factory = factory;
    }
    
    @Override
    public SpanQuery getSpanQuery(final Element e) throws ParserException {
        Element includeElem = DOMUtils.getChildByTagOrFail(e, "Include");
        includeElem = DOMUtils.getFirstChildOrFail(includeElem);
        Element excludeElem = DOMUtils.getChildByTagOrFail(e, "Exclude");
        excludeElem = DOMUtils.getFirstChildOrFail(excludeElem);
        final SpanQuery include = this.factory.getSpanQuery(includeElem);
        final SpanQuery exclude = this.factory.getSpanQuery(excludeElem);
        final SpanNotQuery snq = new SpanNotQuery(include, exclude);
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        return (SpanQuery)new SpanBoostQuery((SpanQuery)snq, boost);
    }
}
