package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class BoostQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public Query build(final QueryNode queryNode) throws QueryNodeException {
        final BoostQueryNode boostNode = (BoostQueryNode)queryNode;
        final QueryNode child = boostNode.getChild();
        if (child == null) {
            return null;
        }
        final Query query = (Query)child.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
        return (Query)new BoostQuery(query, boostNode.getValue());
    }
}
