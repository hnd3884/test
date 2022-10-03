package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.sandbox.queries.DuplicateFilter;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.FilterBuilder;

public class DuplicateFilterBuilder implements FilterBuilder
{
    @Override
    public Filter getFilter(final Element e) throws ParserException {
        final String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        final DuplicateFilter df = new DuplicateFilter(fieldName);
        final String keepMode = DOMUtils.getAttribute(e, "keepMode", "first");
        if (keepMode.equalsIgnoreCase("first")) {
            df.setKeepMode(DuplicateFilter.KeepMode.KM_USE_FIRST_OCCURRENCE);
        }
        else {
            if (!keepMode.equalsIgnoreCase("last")) {
                throw new ParserException("Illegal keepMode attribute in DuplicateFilter:" + keepMode);
            }
            df.setKeepMode(DuplicateFilter.KeepMode.KM_USE_LAST_OCCURRENCE);
        }
        final String processingMode = DOMUtils.getAttribute(e, "processingMode", "full");
        if (processingMode.equalsIgnoreCase("full")) {
            df.setProcessingMode(DuplicateFilter.ProcessingMode.PM_FULL_VALIDATION);
        }
        else {
            if (!processingMode.equalsIgnoreCase("fast")) {
                throw new ParserException("Illegal processingMode attribute in DuplicateFilter:" + processingMode);
            }
            df.setProcessingMode(DuplicateFilter.ProcessingMode.PM_FAST_INVALIDATION);
        }
        return (Filter)df;
    }
}
