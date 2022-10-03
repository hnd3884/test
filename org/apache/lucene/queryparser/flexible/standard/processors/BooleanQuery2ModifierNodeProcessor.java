package org.apache.lucene.queryparser.flexible.standard.processors;

import org.apache.lucene.queryparser.flexible.standard.nodes.BooleanModifierNode;
import org.apache.lucene.queryparser.flexible.core.nodes.AndQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;

public class BooleanQuery2ModifierNodeProcessor implements QueryNodeProcessor
{
    static final String TAG_REMOVE = "remove";
    static final String TAG_MODIFIER = "wrapWithModifier";
    static final String TAG_BOOLEAN_ROOT = "booleanRoot";
    QueryConfigHandler queryConfigHandler;
    private final ArrayList<QueryNode> childrenBuffer;
    private Boolean usingAnd;
    
    public BooleanQuery2ModifierNodeProcessor() {
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
        return this.processIteration(queryTree);
    }
    
    protected void processChildren(final QueryNode queryTree) throws QueryNodeException {
        final List<QueryNode> children = queryTree.getChildren();
        if (children != null && children.size() > 0) {
            for (QueryNode child : children) {
                child = this.processIteration(child);
            }
        }
    }
    
    private QueryNode processIteration(QueryNode queryTree) throws QueryNodeException {
        queryTree = this.preProcessNode(queryTree);
        this.processChildren(queryTree);
        queryTree = this.postProcessNode(queryTree);
        return queryTree;
    }
    
    protected void fillChildrenBufferAndApplyModifiery(final QueryNode parent) {
        for (final QueryNode node : parent.getChildren()) {
            if (node.containsTag("remove")) {
                this.fillChildrenBufferAndApplyModifiery(node);
            }
            else if (node.containsTag("wrapWithModifier")) {
                this.childrenBuffer.add(this.applyModifier(node, (ModifierQueryNode.Modifier)node.getTag("wrapWithModifier")));
            }
            else {
                this.childrenBuffer.add(node);
            }
        }
    }
    
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node.containsTag("booleanRoot")) {
            this.childrenBuffer.clear();
            this.fillChildrenBufferAndApplyModifiery(node);
            node.set(this.childrenBuffer);
        }
        return node;
    }
    
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        final QueryNode parent = node.getParent();
        if (node instanceof BooleanQueryNode) {
            if (parent instanceof BooleanQueryNode) {
                node.setTag("remove", Boolean.TRUE);
            }
            else {
                node.setTag("booleanRoot", Boolean.TRUE);
            }
        }
        else if (parent instanceof BooleanQueryNode && (parent instanceof AndQueryNode || (this.usingAnd && this.isDefaultBooleanQueryNode(parent)))) {
            this.tagModifierButDoNotOverride(node, ModifierQueryNode.Modifier.MOD_REQ);
        }
        return node;
    }
    
    protected boolean isDefaultBooleanQueryNode(final QueryNode toTest) {
        return toTest != null && BooleanQueryNode.class.equals(toTest.getClass());
    }
    
    private QueryNode applyModifier(final QueryNode node, final ModifierQueryNode.Modifier mod) {
        if (!(node instanceof ModifierQueryNode)) {
            return new BooleanModifierNode(node, mod);
        }
        final ModifierQueryNode modNode = (ModifierQueryNode)node;
        if (modNode.getModifier() == ModifierQueryNode.Modifier.MOD_NONE) {
            return new ModifierQueryNode(modNode.getChild(), mod);
        }
        return node;
    }
    
    protected void tagModifierButDoNotOverride(final QueryNode node, final ModifierQueryNode.Modifier mod) {
        if (node instanceof ModifierQueryNode) {
            final ModifierQueryNode modNode = (ModifierQueryNode)node;
            if (modNode.getModifier() == ModifierQueryNode.Modifier.MOD_NONE) {
                node.setTag("wrapWithModifier", mod);
            }
        }
        else {
            node.setTag("wrapWithModifier", ModifierQueryNode.Modifier.MOD_REQ);
        }
    }
    
    @Override
    public void setQueryConfigHandler(final QueryConfigHandler queryConfigHandler) {
        this.queryConfigHandler = queryConfigHandler;
    }
    
    @Override
    public QueryConfigHandler getQueryConfigHandler() {
        return this.queryConfigHandler;
    }
}
