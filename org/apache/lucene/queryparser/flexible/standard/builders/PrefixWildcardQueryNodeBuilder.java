package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.standard.nodes.PrefixWildcardQueryNode;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class PrefixWildcardQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public PrefixQuery build(final QueryNode queryNode) throws QueryNodeException {
        final PrefixWildcardQueryNode wildcardNode = (PrefixWildcardQueryNode)queryNode;
        final String text = wildcardNode.getText().subSequence(0, wildcardNode.getText().length() - 1).toString();
        final PrefixQuery q = new PrefixQuery(new Term(wildcardNode.getFieldAsString(), text));
        final MultiTermQuery.RewriteMethod method = (MultiTermQuery.RewriteMethod)queryNode.getTag("MultiTermRewriteMethodConfiguration");
        if (method != null) {
            q.setRewriteMethod(method);
        }
        return q;
    }
}
