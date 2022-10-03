package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class TermRangeQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public TermRangeQuery build(final QueryNode queryNode) throws QueryNodeException {
        final TermRangeQueryNode rangeNode = (TermRangeQueryNode)queryNode;
        final FieldQueryNode upper = rangeNode.getUpperBound();
        final FieldQueryNode lower = rangeNode.getLowerBound();
        final String field = StringUtils.toString(rangeNode.getField());
        String lowerText = lower.getTextAsString();
        String upperText = upper.getTextAsString();
        if (lowerText.length() == 0) {
            lowerText = null;
        }
        if (upperText.length() == 0) {
            upperText = null;
        }
        final TermRangeQuery rangeQuery = TermRangeQuery.newStringRange(field, lowerText, upperText, rangeNode.isLowerInclusive(), rangeNode.isUpperInclusive());
        final MultiTermQuery.RewriteMethod method = (MultiTermQuery.RewriteMethod)queryNode.getTag("MultiTermRewriteMethodConfiguration");
        if (method != null) {
            rangeQuery.setRewriteMethod(method);
        }
        return rangeQuery;
    }
}
