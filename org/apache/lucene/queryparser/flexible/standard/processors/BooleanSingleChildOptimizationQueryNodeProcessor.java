package org.apache.lucene.queryparser.flexible.standard.processors;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import java.util.List;
import org.apache.lucene.queryparser.flexible.standard.nodes.BooleanModifierNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class BooleanSingleChildOptimizationQueryNodeProcessor extends QueryNodeProcessorImpl
{
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof BooleanQueryNode) {
            final List<QueryNode> children = node.getChildren();
            if (children != null && children.size() == 1) {
                final QueryNode child = children.get(0);
                if (!(child instanceof ModifierQueryNode)) {
                    return child;
                }
                final ModifierQueryNode modNode = (ModifierQueryNode)child;
                if (modNode instanceof BooleanModifierNode || modNode.getModifier() == ModifierQueryNode.Modifier.MOD_NONE) {
                    return child;
                }
            }
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
