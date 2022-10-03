package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class ModifierQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public Query build(final QueryNode queryNode) throws QueryNodeException {
        final ModifierQueryNode modifierNode = (ModifierQueryNode)queryNode;
        return (Query)modifierNode.getChild().getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
    }
}
