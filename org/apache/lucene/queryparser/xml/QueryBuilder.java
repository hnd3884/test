package org.apache.lucene.queryparser.xml;

import org.apache.lucene.search.Query;
import org.w3c.dom.Element;

public interface QueryBuilder
{
    Query getQuery(final Element p0) throws ParserException;
}
