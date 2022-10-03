package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.SlopQueryNode;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class SlopQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public Query build(final QueryNode queryNode) throws QueryNodeException {
        final SlopQueryNode phraseSlopNode = (SlopQueryNode)queryNode;
        Query query = (Query)phraseSlopNode.getChild().getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
        if (query instanceof PhraseQuery) {
            final PhraseQuery.Builder builder = new PhraseQuery.Builder();
            builder.setSlop(phraseSlopNode.getValue());
            final PhraseQuery pq = (PhraseQuery)query;
            final Term[] terms = pq.getTerms();
            final int[] positions = pq.getPositions();
            for (int i = 0; i < terms.length; ++i) {
                builder.add(terms[i], positions[i]);
            }
            query = (Query)builder.build();
        }
        else {
            ((MultiPhraseQuery)query).setSlop(phraseSlopNode.getValue());
        }
        return query;
    }
}
