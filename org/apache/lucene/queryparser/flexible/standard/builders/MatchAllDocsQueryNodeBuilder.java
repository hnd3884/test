package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.standard.parser.EscapeQuerySyntaxImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchAllDocsQueryNode;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class MatchAllDocsQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public MatchAllDocsQuery build(final QueryNode queryNode) throws QueryNodeException {
        if (!(queryNode instanceof MatchAllDocsQueryNode)) {
            throw new QueryNodeException(new MessageImpl(QueryParserMessages.LUCENE_QUERY_CONVERSION_ERROR, new Object[] { queryNode.toQueryString(new EscapeQuerySyntaxImpl()), queryNode.getClass().getName() }));
        }
        return new MatchAllDocsQuery();
    }
}
