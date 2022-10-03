package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.search.BooleanClause;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.standard.parser.EscapeQuerySyntaxImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class BooleanQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public BooleanQuery build(final QueryNode queryNode) throws QueryNodeException {
        final BooleanQueryNode booleanNode = (BooleanQueryNode)queryNode;
        final BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
        final List<QueryNode> children = booleanNode.getChildren();
        if (children != null) {
            for (final QueryNode child : children) {
                final Object obj = child.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
                if (obj != null) {
                    final Query query = (Query)obj;
                    try {
                        bQuery.add(query, getModifierValue(child));
                    }
                    catch (final BooleanQuery.TooManyClauses ex) {
                        throw new QueryNodeException(new MessageImpl(QueryParserMessages.TOO_MANY_BOOLEAN_CLAUSES, new Object[] { BooleanQuery.getMaxClauseCount(), queryNode.toQueryString(new EscapeQuerySyntaxImpl()) }), (Throwable)ex);
                    }
                }
            }
        }
        return bQuery.build();
    }
    
    private static BooleanClause.Occur getModifierValue(final QueryNode node) {
        if (node instanceof ModifierQueryNode) {
            final ModifierQueryNode mNode = (ModifierQueryNode)node;
            switch (mNode.getModifier()) {
                case MOD_REQ: {
                    return BooleanClause.Occur.MUST;
                }
                case MOD_NOT: {
                    return BooleanClause.Occur.MUST_NOT;
                }
                case MOD_NONE: {
                    return BooleanClause.Occur.SHOULD;
                }
            }
        }
        return BooleanClause.Occur.SHOULD;
    }
}
