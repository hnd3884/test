package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.QueryBuilderFactory;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class ConstantScoreQueryBuilder implements QueryBuilder
{
    private final QueryBuilderFactory queryFactory;
    
    public ConstantScoreQueryBuilder(final QueryBuilderFactory queryFactory) {
        this.queryFactory = queryFactory;
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        final Element queryElem = DOMUtils.getFirstChildOrFail(e);
        Query q = (Query)new ConstantScoreQuery(this.queryFactory.getQuery(queryElem));
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        if (boost != 1.0f) {
            q = (Query)new BoostQuery(q, boost);
        }
        return q;
    }
}
