package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class RangeQueryBuilder implements QueryBuilder
{
    @Override
    public Query getQuery(final Element e) throws ParserException {
        final String fieldName = DOMUtils.getAttributeWithInheritance(e, "fieldName");
        final String lowerTerm = e.getAttribute("lowerTerm");
        final String upperTerm = e.getAttribute("upperTerm");
        final boolean includeLower = DOMUtils.getAttribute(e, "includeLower", true);
        final boolean includeUpper = DOMUtils.getAttribute(e, "includeUpper", true);
        return (Query)TermRangeQuery.newStringRange(fieldName, lowerTerm, upperTerm, includeLower, includeUpper);
    }
}
