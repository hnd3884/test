package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class NumericRangeQueryBuilder implements QueryBuilder
{
    @Override
    public Query getQuery(final Element e) throws ParserException {
        final String field = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        final String lowerTerm = DOMUtils.getAttribute(e, "lowerTerm", null);
        final String upperTerm = DOMUtils.getAttribute(e, "upperTerm", null);
        final boolean lowerInclusive = DOMUtils.getAttribute(e, "includeLower", true);
        final boolean upperInclusive = DOMUtils.getAttribute(e, "includeUpper", true);
        final int precisionStep = DOMUtils.getAttribute(e, "precisionStep", 16);
        final String type = DOMUtils.getAttribute(e, "type", "int");
        try {
            Query filter;
            if (type.equalsIgnoreCase("int")) {
                filter = (Query)NumericRangeQuery.newIntRange(field, precisionStep, (lowerTerm == null) ? null : Integer.valueOf(lowerTerm), (upperTerm == null) ? null : Integer.valueOf(upperTerm), lowerInclusive, upperInclusive);
            }
            else if (type.equalsIgnoreCase("long")) {
                filter = (Query)NumericRangeQuery.newLongRange(field, precisionStep, (lowerTerm == null) ? null : Long.valueOf(lowerTerm), (upperTerm == null) ? null : Long.valueOf(upperTerm), lowerInclusive, upperInclusive);
            }
            else if (type.equalsIgnoreCase("double")) {
                filter = (Query)NumericRangeQuery.newDoubleRange(field, precisionStep, (lowerTerm == null) ? null : Double.valueOf(lowerTerm), (upperTerm == null) ? null : Double.valueOf(upperTerm), lowerInclusive, upperInclusive);
            }
            else {
                if (!type.equalsIgnoreCase("float")) {
                    throw new ParserException("type attribute must be one of: [long, int, double, float]");
                }
                filter = (Query)NumericRangeQuery.newFloatRange(field, precisionStep, (lowerTerm == null) ? null : Float.valueOf(lowerTerm), (upperTerm == null) ? null : Float.valueOf(upperTerm), lowerInclusive, upperInclusive);
            }
            return filter;
        }
        catch (final NumberFormatException nfe) {
            throw new ParserException("Could not parse lowerTerm or upperTerm into a number", nfe);
        }
    }
}
