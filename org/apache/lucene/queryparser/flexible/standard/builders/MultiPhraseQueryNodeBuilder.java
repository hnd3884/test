package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.index.Term;
import java.util.List;
import java.util.TreeMap;
import org.apache.lucene.queryparser.flexible.standard.nodes.MultiPhraseQueryNode;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class MultiPhraseQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public MultiPhraseQuery build(final QueryNode queryNode) throws QueryNodeException {
        final MultiPhraseQueryNode phraseNode = (MultiPhraseQueryNode)queryNode;
        final MultiPhraseQuery phraseQuery = new MultiPhraseQuery();
        final List<QueryNode> children = phraseNode.getChildren();
        if (children != null) {
            final TreeMap<Integer, List<Term>> positionTermMap = new TreeMap<Integer, List<Term>>();
            for (final QueryNode child : children) {
                final FieldQueryNode termNode = (FieldQueryNode)child;
                final TermQuery termQuery = (TermQuery)termNode.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
                List<Term> termList = positionTermMap.get(termNode.getPositionIncrement());
                if (termList == null) {
                    termList = new LinkedList<Term>();
                    positionTermMap.put(termNode.getPositionIncrement(), termList);
                }
                termList.add(termQuery.getTerm());
            }
            for (final int positionIncrement : positionTermMap.keySet()) {
                final List<Term> termList2 = positionTermMap.get(positionIncrement);
                phraseQuery.add((Term[])termList2.toArray(new Term[termList2.size()]), positionIncrement);
            }
        }
        return phraseQuery;
    }
}
