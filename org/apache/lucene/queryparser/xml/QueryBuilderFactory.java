package org.apache.lucene.queryparser.xml;

import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import java.util.HashMap;

public class QueryBuilderFactory implements QueryBuilder
{
    HashMap<String, QueryBuilder> builders;
    
    public QueryBuilderFactory() {
        this.builders = new HashMap<String, QueryBuilder>();
    }
    
    @Override
    public Query getQuery(final Element n) throws ParserException {
        final QueryBuilder builder = this.builders.get(n.getNodeName());
        if (builder == null) {
            throw new ParserException("No QueryObjectBuilder defined for node " + n.getNodeName());
        }
        return builder.getQuery(n);
    }
    
    public void addBuilder(final String nodeName, final QueryBuilder builder) {
        this.builders.put(nodeName, builder);
    }
    
    public QueryBuilder getQueryBuilder(final String nodeName) {
        return this.builders.get(nodeName);
    }
}
