package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.w3c.dom.Node;
import java.util.List;
import org.apache.lucene.search.spans.SpanBoostQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.spans.SpanOrQuery;
import java.util.ArrayList;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public class SpanOrBuilder extends SpanBuilderBase
{
    private final SpanQueryBuilder factory;
    
    public SpanOrBuilder(final SpanQueryBuilder factory) {
        this.factory = factory;
    }
    
    @Override
    public SpanQuery getSpanQuery(final Element e) throws ParserException {
        final List<SpanQuery> clausesList = new ArrayList<SpanQuery>();
        for (Node kid = e.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == 1) {
                final SpanQuery clause = this.factory.getSpanQuery((Element)kid);
                clausesList.add(clause);
            }
        }
        final SpanQuery[] clauses = clausesList.toArray(new SpanQuery[clausesList.size()]);
        final SpanOrQuery soq = new SpanOrQuery(clauses);
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        return (SpanQuery)new SpanBoostQuery((SpanQuery)soq, boost);
    }
}
