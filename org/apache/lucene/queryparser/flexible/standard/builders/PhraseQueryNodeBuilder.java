package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class PhraseQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public Query build(final QueryNode queryNode) throws QueryNodeException {
        final TokenizedPhraseQueryNode phraseNode = (TokenizedPhraseQueryNode)queryNode;
        final PhraseQuery.Builder builder = new PhraseQuery.Builder();
        final List<QueryNode> children = phraseNode.getChildren();
        if (children != null) {
            for (final QueryNode child : children) {
                final TermQuery termQuery = (TermQuery)child.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
                final FieldQueryNode termNode = (FieldQueryNode)child;
                builder.add(termQuery.getTerm(), termNode.getPositionIncrement());
            }
        }
        return (Query)builder.build();
    }
}
