package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.FilterBuilder;

public class RangeFilterBuilder implements FilterBuilder
{
    @Override
    public Filter getFilter(final Element e) throws ParserException {
        final String fieldName = DOMUtils.getAttributeWithInheritance(e, "fieldName");
        final String lowerTerm = e.getAttribute("lowerTerm");
        final String upperTerm = e.getAttribute("upperTerm");
        final boolean includeLower = DOMUtils.getAttribute(e, "includeLower", true);
        final boolean includeUpper = DOMUtils.getAttribute(e, "includeUpper", true);
        return (Filter)TermRangeFilter.newStringRange(fieldName, lowerTerm, upperTerm, includeLower, includeUpper);
    }
}
