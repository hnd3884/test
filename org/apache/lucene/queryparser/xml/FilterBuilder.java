package org.apache.lucene.queryparser.xml;

import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;

public interface FilterBuilder
{
    Filter getFilter(final Element p0) throws ParserException;
}
