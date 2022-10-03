package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public interface SpanQueryBuilder extends QueryBuilder
{
    SpanQuery getSpanQuery(final Element p0) throws ParserException;
}
