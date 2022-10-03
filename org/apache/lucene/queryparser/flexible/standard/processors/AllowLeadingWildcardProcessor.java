package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.standard.parser.EscapeQuerySyntaxImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.util.UnescapedCharSequence;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class AllowLeadingWildcardProcessor extends QueryNodeProcessorImpl
{
    @Override
    public QueryNode process(final QueryNode queryTree) throws QueryNodeException {
        final Boolean allowsLeadingWildcard = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ALLOW_LEADING_WILDCARD);
        if (allowsLeadingWildcard != null && !allowsLeadingWildcard) {
            return super.process(queryTree);
        }
        return queryTree;
    }
    
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof WildcardQueryNode) {
            final WildcardQueryNode wildcardNode = (WildcardQueryNode)node;
            if (wildcardNode.getText().length() > 0) {
                if (UnescapedCharSequence.wasEscaped(wildcardNode.getText(), 0)) {
                    return node;
                }
                switch (wildcardNode.getText().charAt(0)) {
                    case '*':
                    case '?': {
                        throw new QueryNodeException(new MessageImpl(QueryParserMessages.LEADING_WILDCARD_NOT_ALLOWED, new Object[] { node.toQueryString(new EscapeQuerySyntaxImpl()) }));
                    }
                }
            }
        }
        return node;
    }
    
    @Override
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        return node;
    }
    
    @Override
    protected List<QueryNode> setChildrenOrder(final List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}
