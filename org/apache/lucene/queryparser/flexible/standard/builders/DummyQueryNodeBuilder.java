package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class DummyQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public TermQuery build(final QueryNode queryNode) throws QueryNodeException {
        return null;
    }
}
