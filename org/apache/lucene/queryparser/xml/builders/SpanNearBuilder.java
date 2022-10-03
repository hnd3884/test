package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.w3c.dom.Node;
import java.util.List;
import org.apache.lucene.search.spans.SpanBoostQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import java.util.ArrayList;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public class SpanNearBuilder extends SpanBuilderBase
{
    private final SpanQueryBuilder factory;
    
    public SpanNearBuilder(final SpanQueryBuilder factory) {
        this.factory = factory;
    }
    
    @Override
    public SpanQuery getSpanQuery(final Element e) throws ParserException {
        final String slopString = DOMUtils.getAttributeOrFail(e, "slop");
        final int slop = Integer.parseInt(slopString);
        final boolean inOrder = DOMUtils.getAttribute(e, "inOrder", false);
        final List<SpanQuery> spans = new ArrayList<SpanQuery>();
        for (Node kid = e.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == 1) {
                spans.add(this.factory.getSpanQuery((Element)kid));
            }
        }
        final SpanQuery[] spanQueries = spans.toArray(new SpanQuery[spans.size()]);
        final SpanQuery snq = (SpanQuery)new SpanNearQuery(spanQueries, slop, inOrder);
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        return (SpanQuery)new SpanBoostQuery(snq, boost);
    }
}
