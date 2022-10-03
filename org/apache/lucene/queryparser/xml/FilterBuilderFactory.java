package org.apache.lucene.queryparser.xml;

import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;
import java.util.HashMap;

public class FilterBuilderFactory implements FilterBuilder
{
    HashMap<String, FilterBuilder> builders;
    
    public FilterBuilderFactory() {
        this.builders = new HashMap<String, FilterBuilder>();
    }
    
    @Override
    public Filter getFilter(final Element n) throws ParserException {
        final FilterBuilder builder = this.builders.get(n.getNodeName());
        if (builder == null) {
            throw new ParserException("No FilterBuilder defined for node " + n.getNodeName());
        }
        return builder.getFilter(n);
    }
    
    public void addBuilder(final String nodeName, final FilterBuilder builder) {
        this.builders.put(nodeName, builder);
    }
    
    public FilterBuilder getFilterBuilder(final String nodeName) {
        return this.builders.get(nodeName);
    }
}
