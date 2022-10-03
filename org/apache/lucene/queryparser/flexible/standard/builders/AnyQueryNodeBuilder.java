package org.apache.lucene.queryparser.flexible.standard.builders;

import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.AnyQueryNode;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class AnyQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public BooleanQuery build(final QueryNode queryNode) throws QueryNodeException {
        final AnyQueryNode andNode = (AnyQueryNode)queryNode;
        final BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
        final List<QueryNode> children = andNode.getChildren();
        if (children != null) {
            for (final QueryNode child : children) {
                final Object obj = child.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
                if (obj != null) {
                    final Query query = (Query)obj;
                    try {
                        bQuery.add(query, BooleanClause.Occur.SHOULD);
                    }
                    catch (final BooleanQuery.TooManyClauses ex) {
                        throw new QueryNodeException(new MessageImpl(QueryParserMessages.EMPTY_MESSAGE), (Throwable)ex);
                    }
                }
            }
        }
        bQuery.setMinimumNumberShouldMatch(andNode.getMinimumMatchingElements());
        return bQuery.build();
    }
}
