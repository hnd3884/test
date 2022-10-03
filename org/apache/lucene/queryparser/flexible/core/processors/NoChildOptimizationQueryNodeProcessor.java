package org.apache.lucene.queryparser.flexible.core.processors;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchNoDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.DeletedQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class NoChildOptimizationQueryNodeProcessor extends QueryNodeProcessorImpl
{
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof BooleanQueryNode || node instanceof BoostQueryNode || node instanceof TokenizedPhraseQueryNode || node instanceof ModifierQueryNode) {
            final List<QueryNode> children = node.getChildren();
            if (children != null && children.size() > 0) {
                for (final QueryNode child : children) {
                    if (!(child instanceof DeletedQueryNode)) {
                        return node;
                    }
                }
            }
            return new MatchNoDocsQueryNode();
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
