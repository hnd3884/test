package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class WildcardQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public WildcardQuery build(final QueryNode queryNode) throws QueryNodeException {
        final WildcardQueryNode wildcardNode = (WildcardQueryNode)queryNode;
        final WildcardQuery q = new WildcardQuery(new Term(wildcardNode.getFieldAsString(), wildcardNode.getTextAsString()));
        final MultiTermQuery.RewriteMethod method = (MultiTermQuery.RewriteMethod)queryNode.getTag("MultiTermRewriteMethodConfiguration");
        if (method != null) {
            q.setRewriteMethod(method);
        }
        return q;
    }
}
