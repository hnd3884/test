package org.apache.lucene.queryparser.flexible.precedence.processors;

import java.util.Iterator;
import org.apache.lucene.queryparser.flexible.core.nodes.OrQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.AndQueryNode;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class BooleanModifiersQueryNodeProcessor extends QueryNodeProcessorImpl
{
    private ArrayList<QueryNode> childrenBuffer;
    private Boolean usingAnd;
    
    public BooleanModifiersQueryNodeProcessor() {
        this.childrenBuffer = new ArrayList<QueryNode>();
        this.usingAnd = false;
    }
    
    @Override
    public QueryNode process(final QueryNode queryTree) throws QueryNodeException {
        final StandardQueryConfigHandler.Operator op = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR);
        if (op == null) {
            throw new IllegalArgumentException("StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR should be set on the QueryConfigHandler");
        }
        this.usingAnd = (StandardQueryConfigHandler.Operator.AND == op);
        return super.process(queryTree);
    }
    
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof AndQueryNode) {
            this.childrenBuffer.clear();
            final List<QueryNode> children = node.getChildren();
            for (final QueryNode child : children) {
                this.childrenBuffer.add(this.applyModifier(child, ModifierQueryNode.Modifier.MOD_REQ));
            }
            node.set(this.childrenBuffer);
        }
        else if (this.usingAnd && node instanceof BooleanQueryNode && !(node instanceof OrQueryNode)) {
            this.childrenBuffer.clear();
            final List<QueryNode> children = node.getChildren();
            for (final QueryNode child : children) {
                this.childrenBuffer.add(this.applyModifier(child, ModifierQueryNode.Modifier.MOD_REQ));
            }
            node.set(this.childrenBuffer);
        }
        return node;
    }
    
    private QueryNode applyModifier(final QueryNode node, final ModifierQueryNode.Modifier mod) {
        if (!(node instanceof ModifierQueryNode)) {
            return new ModifierQueryNode(node, mod);
        }
        final ModifierQueryNode modNode = (ModifierQueryNode)node;
        if (modNode.getModifier() == ModifierQueryNode.Modifier.MOD_NONE) {
            return new ModifierQueryNode(modNode.getChild(), mod);
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
