package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.queries.BoostingQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class BoostingQueryBuilder implements QueryBuilder
{
    private static float DEFAULT_BOOST;
    private final QueryBuilder factory;
    
    public BoostingQueryBuilder(final QueryBuilder factory) {
        this.factory = factory;
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        Element mainQueryElem = DOMUtils.getChildByTagOrFail(e, "Query");
        mainQueryElem = DOMUtils.getFirstChildOrFail(mainQueryElem);
        final Query mainQuery = this.factory.getQuery(mainQueryElem);
        Element boostQueryElem = DOMUtils.getChildByTagOrFail(e, "BoostQuery");
        float boost = DOMUtils.getAttribute(boostQueryElem, "boost", BoostingQueryBuilder.DEFAULT_BOOST);
        boostQueryElem = DOMUtils.getFirstChildOrFail(boostQueryElem);
        final Query boostQuery = this.factory.getQuery(boostQueryElem);
        final Query bq = (Query)new BoostingQuery(mainQuery, boostQuery, boost);
        boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        if (boost != 1.0f) {
            return (Query)new BoostQuery(bq, boost);
        }
        return bq;
    }
    
    static {
        BoostingQueryBuilder.DEFAULT_BOOST = 0.01f;
    }
}
