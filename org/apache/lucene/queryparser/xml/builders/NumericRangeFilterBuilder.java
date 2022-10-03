package org.apache.lucene.queryparser.xml.builders;

import java.io.IOException;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.FilterBuilder;

public class NumericRangeFilterBuilder implements FilterBuilder
{
    private static final NoMatchFilter NO_MATCH_FILTER;
    private boolean strictMode;
    
    public NumericRangeFilterBuilder() {
        this.strictMode = false;
    }
    
    public void setStrictMode(final boolean strictMode) {
        this.strictMode = strictMode;
    }
    
    @Override
    public Filter getFilter(final Element e) throws ParserException {
        final String field = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        final String lowerTerm = DOMUtils.getAttribute(e, "lowerTerm", null);
        final String upperTerm = DOMUtils.getAttribute(e, "upperTerm", null);
        final boolean lowerInclusive = DOMUtils.getAttribute(e, "includeLower", true);
        final boolean upperInclusive = DOMUtils.getAttribute(e, "includeUpper", true);
        final int precisionStep = DOMUtils.getAttribute(e, "precisionStep", 16);
        final String type = DOMUtils.getAttribute(e, "type", "int");
        try {
            Filter filter;
            if (type.equalsIgnoreCase("int")) {
                filter = (Filter)NumericRangeFilter.newIntRange(field, precisionStep, (lowerTerm == null) ? null : Integer.valueOf(lowerTerm), (upperTerm == null) ? null : Integer.valueOf(upperTerm), lowerInclusive, upperInclusive);
            }
            else if (type.equalsIgnoreCase("long")) {
                filter = (Filter)NumericRangeFilter.newLongRange(field, precisionStep, (lowerTerm == null) ? null : Long.valueOf(lowerTerm), (upperTerm == null) ? null : Long.valueOf(upperTerm), lowerInclusive, upperInclusive);
            }
            else if (type.equalsIgnoreCase("double")) {
                filter = (Filter)NumericRangeFilter.newDoubleRange(field, precisionStep, (lowerTerm == null) ? null : Double.valueOf(lowerTerm), (upperTerm == null) ? null : Double.valueOf(upperTerm), lowerInclusive, upperInclusive);
            }
            else {
                if (!type.equalsIgnoreCase("float")) {
                    throw new ParserException("type attribute must be one of: [long, int, double, float]");
                }
                filter = (Filter)NumericRangeFilter.newFloatRange(field, precisionStep, (lowerTerm == null) ? null : Float.valueOf(lowerTerm), (upperTerm == null) ? null : Float.valueOf(upperTerm), lowerInclusive, upperInclusive);
            }
            return filter;
        }
        catch (final NumberFormatException nfe) {
            if (this.strictMode) {
                throw new ParserException("Could not parse lowerTerm or upperTerm into a number", nfe);
            }
            return NumericRangeFilterBuilder.NO_MATCH_FILTER;
        }
    }
    
    static {
        NO_MATCH_FILTER = new NoMatchFilter();
    }
    
    static class NoMatchFilter extends Filter
    {
        public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
            return null;
        }
        
        public String toString(final String field) {
            return "NoMatchFilter()";
        }
    }
}
