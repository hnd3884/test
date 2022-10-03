package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class MatchAllDocsQueryBuilder implements QueryBuilder
{
    @Override
    public Query getQuery(final Element e) throws ParserException {
        return (Query)new MatchAllDocsQuery();
    }
}
