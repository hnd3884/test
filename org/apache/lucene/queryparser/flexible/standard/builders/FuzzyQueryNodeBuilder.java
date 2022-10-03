package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class FuzzyQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public FuzzyQuery build(final QueryNode queryNode) throws QueryNodeException {
        final FuzzyQueryNode fuzzyNode = (FuzzyQueryNode)queryNode;
        final String text = fuzzyNode.getTextAsString();
        final int numEdits = FuzzyQuery.floatToEdits(fuzzyNode.getSimilarity(), text.codePointCount(0, text.length()));
        return new FuzzyQuery(new Term(fuzzyNode.getFieldAsString(), fuzzyNode.getTextAsString()), numEdits, fuzzyNode.getPrefixLength());
    }
}
