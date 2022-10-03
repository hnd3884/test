package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import java.util.HashMap;
import java.util.Map;

public class SpanQueryBuilderFactory implements SpanQueryBuilder
{
    private final Map<String, SpanQueryBuilder> builders;
    
    public SpanQueryBuilderFactory() {
        this.builders = new HashMap<String, SpanQueryBuilder>();
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        return (Query)this.getSpanQuery(e);
    }
    
    public void addBuilder(final String nodeName, final SpanQueryBuilder builder) {
        this.builders.put(nodeName, builder);
    }
    
    @Override
    public SpanQuery getSpanQuery(final Element e) throws ParserException {
        final SpanQueryBuilder builder = this.builders.get(e.getNodeName());
        if (builder == null) {
            throw new ParserException("No SpanQueryObjectBuilder defined for node " + e.getNodeName());
        }
        return builder.getSpanQuery(e);
    }
}
