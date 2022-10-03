package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class BooleanQueryBuilder implements QueryBuilder
{
    private final QueryBuilder factory;
    
    public BooleanQueryBuilder(final QueryBuilder factory) {
        this.factory = factory;
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        bq.setDisableCoord(DOMUtils.getAttribute(e, "disableCoord", false));
        bq.setMinimumNumberShouldMatch(DOMUtils.getAttribute(e, "minimumNumberShouldMatch", 0));
        final NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (node.getNodeName().equals("Clause")) {
                final Element clauseElem = (Element)node;
                final BooleanClause.Occur occurs = getOccursValue(clauseElem);
                final Element clauseQuery = DOMUtils.getFirstChildOrFail(clauseElem);
                final Query q = this.factory.getQuery(clauseQuery);
                bq.add(new BooleanClause(q, occurs));
            }
        }
        Query q2 = (Query)bq.build();
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        if (boost != 1.0f) {
            q2 = (Query)new BoostQuery(q2, boost);
        }
        return q2;
    }
    
    static BooleanClause.Occur getOccursValue(final Element clauseElem) throws ParserException {
        final String occs = clauseElem.getAttribute("occurs");
        if (occs == null || "should".equalsIgnoreCase(occs)) {
            return BooleanClause.Occur.SHOULD;
        }
        if ("must".equalsIgnoreCase(occs)) {
            return BooleanClause.Occur.MUST;
        }
        if ("mustNot".equalsIgnoreCase(occs)) {
            return BooleanClause.Occur.MUST_NOT;
        }
        if ("filter".equals(occs)) {
            return BooleanClause.Occur.FILTER;
        }
        throw new ParserException("Invalid value for \"occurs\" attribute of clause:" + occs);
    }
}
