package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.standard.nodes.RegexpQueryNode;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class RegexpQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public RegexpQuery build(final QueryNode queryNode) throws QueryNodeException {
        final RegexpQueryNode regexpNode = (RegexpQueryNode)queryNode;
        final RegexpQuery q = new RegexpQuery(new Term(regexpNode.getFieldAsString(), regexpNode.textToBytesRef()));
        final MultiTermQuery.RewriteMethod method = (MultiTermQuery.RewriteMethod)queryNode.getTag("MultiTermRewriteMethodConfiguration");
        if (method != null) {
            q.setRewriteMethod(method);
        }
        return q;
    }
}
