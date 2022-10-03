package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.List;
import org.apache.lucene.search.BoostQuery;
import java.util.Collection;
import org.apache.lucene.search.DisjunctionMaxQuery;
import java.util.ArrayList;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class DisjunctionMaxQueryBuilder implements QueryBuilder
{
    private final QueryBuilder factory;
    
    public DisjunctionMaxQueryBuilder(final QueryBuilder factory) {
        this.factory = factory;
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        final float tieBreaker = DOMUtils.getAttribute(e, "tieBreaker", 0.0f);
        final List<Query> disjuncts = new ArrayList<Query>();
        final NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (node instanceof Element) {
                final Element queryElem = (Element)node;
                final Query q = this.factory.getQuery(queryElem);
                disjuncts.add(q);
            }
        }
        Query q2 = (Query)new DisjunctionMaxQuery((Collection)disjuncts, tieBreaker);
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        if (boost != 1.0f) {
            q2 = (Query)new BoostQuery(q2, boost);
        }
        return q2;
    }
}
