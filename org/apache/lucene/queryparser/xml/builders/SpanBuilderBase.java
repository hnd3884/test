package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;

public abstract class SpanBuilderBase implements SpanQueryBuilder
{
    @Override
    public Query getQuery(final Element e) throws ParserException {
        return (Query)this.getSpanQuery(e);
    }
}
