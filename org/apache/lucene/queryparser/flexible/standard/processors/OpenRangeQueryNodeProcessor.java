package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.util.UnescapedCharSequence;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class OpenRangeQueryNodeProcessor extends QueryNodeProcessorImpl
{
    public static final String OPEN_RANGE_TOKEN = "*";
    
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof TermRangeQueryNode) {
            final TermRangeQueryNode rangeNode = (TermRangeQueryNode)node;
            final FieldQueryNode lowerNode = rangeNode.getLowerBound();
            final FieldQueryNode upperNode = rangeNode.getUpperBound();
            CharSequence lowerText = lowerNode.getText();
            CharSequence upperText = upperNode.getText();
            if ("*".equals(upperNode.getTextAsString()) && (!(upperText instanceof UnescapedCharSequence) || !((UnescapedCharSequence)upperText).wasEscaped(0))) {
                upperText = "";
            }
            if ("*".equals(lowerNode.getTextAsString()) && (!(lowerText instanceof UnescapedCharSequence) || !((UnescapedCharSequence)lowerText).wasEscaped(0))) {
                lowerText = "";
            }
            lowerNode.setText(lowerText);
            upperNode.setText(upperText);
        }
        return node;
    }
    
    @Override
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        return node;
    }
    
    @Override
    protected List<QueryNode> setChildrenOrder(final List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}
