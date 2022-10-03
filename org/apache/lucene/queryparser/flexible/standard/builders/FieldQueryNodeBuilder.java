package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class FieldQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public TermQuery build(final QueryNode queryNode) throws QueryNodeException {
        final FieldQueryNode fieldNode = (FieldQueryNode)queryNode;
        return new TermQuery(new Term(fieldNode.getFieldAsString(), fieldNode.getTextAsString()));
    }
}
