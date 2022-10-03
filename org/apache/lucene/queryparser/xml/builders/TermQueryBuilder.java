package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class TermQueryBuilder implements QueryBuilder
{
    @Override
    public Query getQuery(final Element e) throws ParserException {
        final String field = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        final String value = DOMUtils.getNonBlankTextOrFail(e);
        Query tq = (Query)new TermQuery(new Term(field, value));
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        if (boost != 1.0f) {
            tq = (Query)new BoostQuery(tq, boost);
        }
        return tq;
    }
}
