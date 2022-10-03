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
import org.apache.lucene.queryparser.flexible.standard.nodes.StandardBooleanQueryNode;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class StandardBooleanQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public BooleanQuery build(final QueryNode queryNode) throws QueryNodeException {
        final StandardBooleanQueryNode booleanNode = (StandardBooleanQueryNode)queryNode;
        final BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
        bQuery.setDisableCoord(booleanNode.isDisableCoord());
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
        if (!(node instanceof ModifierQueryNode)) {
            return BooleanClause.Occur.SHOULD;
        }
        final ModifierQueryNode mNode = (ModifierQueryNode)node;
        final ModifierQueryNode.Modifier modifier = mNode.getModifier();
        if (ModifierQueryNode.Modifier.MOD_NONE.equals(modifier)) {
            return BooleanClause.Occur.SHOULD;
        }
        if (ModifierQueryNode.Modifier.MOD_NOT.equals(modifier)) {
            return BooleanClause.Occur.MUST_NOT;
        }
        return BooleanClause.Occur.MUST;
    }
}
