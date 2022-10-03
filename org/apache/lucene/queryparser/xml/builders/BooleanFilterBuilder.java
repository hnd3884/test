package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.BooleanClause;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.lucene.queries.FilterClause;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;
import org.apache.lucene.queryparser.xml.FilterBuilder;

public class BooleanFilterBuilder implements FilterBuilder
{
    private final FilterBuilder factory;
    
    public BooleanFilterBuilder(final FilterBuilder factory) {
        this.factory = factory;
    }
    
    @Override
    public Filter getFilter(final Element e) throws ParserException {
        final BooleanFilter bf = new BooleanFilter();
        final NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (node.getNodeName().equals("Clause")) {
                final Element clauseElem = (Element)node;
                final BooleanClause.Occur occurs = BooleanQueryBuilder.getOccursValue(clauseElem);
                final Element clauseFilter = DOMUtils.getFirstChildOrFail(clauseElem);
                final Filter f = this.factory.getFilter(clauseFilter);
                bf.add(new FilterClause(f, occurs));
            }
        }
        return (Filter)bf;
    }
}
