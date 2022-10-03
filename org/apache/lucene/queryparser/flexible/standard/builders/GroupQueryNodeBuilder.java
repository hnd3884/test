package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class GroupQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public Query build(final QueryNode queryNode) throws QueryNodeException {
        final GroupQueryNode groupNode = (GroupQueryNode)queryNode;
        return (Query)groupNode.getChild().getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
    }
}
