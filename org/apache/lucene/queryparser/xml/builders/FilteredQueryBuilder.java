package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class FilteredQueryBuilder implements QueryBuilder
{
    private final FilterBuilder filterFactory;
    private final QueryBuilder queryFactory;
    
    public FilteredQueryBuilder(final FilterBuilder filterFactory, final QueryBuilder queryFactory) {
        this.filterFactory = filterFactory;
        this.queryFactory = queryFactory;
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        Element filterElement = DOMUtils.getChildByTagOrFail(e, "Filter");
        filterElement = DOMUtils.getFirstChildOrFail(filterElement);
        final Filter f = this.filterFactory.getFilter(filterElement);
        Element queryElement = DOMUtils.getChildByTagOrFail(e, "Query");
        queryElement = DOMUtils.getFirstChildOrFail(queryElement);
        final Query q = this.queryFactory.getQuery(queryElement);
        final FilteredQuery fq = new FilteredQuery(q, f);
        fq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return (Query)fq;
    }
}
