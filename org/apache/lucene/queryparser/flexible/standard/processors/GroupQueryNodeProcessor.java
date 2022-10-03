package org.apache.lucene.queryparser.flexible.standard.processors;

import org.apache.lucene.queryparser.flexible.core.nodes.AndQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.BooleanModifierNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.OrQueryNode;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;

@Deprecated
public class GroupQueryNodeProcessor implements QueryNodeProcessor
{
    private ArrayList<QueryNode> queryNodeList;
    private boolean latestNodeVerified;
    private QueryConfigHandler queryConfig;
    private Boolean usingAnd;
    
    public GroupQueryNodeProcessor() {
        this.usingAnd = false;
    }
    
    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        final StandardQueryConfigHandler.Operator defaultOperator = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR);
        if (defaultOperator == null) {
            throw new IllegalArgumentException("DEFAULT_OPERATOR should be set on the QueryConfigHandler");
        }
        this.usingAnd = (StandardQueryConfigHandler.Operator.AND == defaultOperator);
        if (queryTree instanceof GroupQueryNode) {
            queryTree = ((GroupQueryNode)queryTree).getChild();
        }
        this.queryNodeList = new ArrayList<QueryNode>();
        this.latestNodeVerified = false;
        this.readTree(queryTree);
        final List<QueryNode> actualQueryNodeList = this.queryNodeList;
        for (int i = 0; i < actualQueryNodeList.size(); ++i) {
            final QueryNode node = actualQueryNodeList.get(i);
            if (node instanceof GroupQueryNode) {
                actualQueryNodeList.set(i, this.process(node));
            }
        }
        this.usingAnd = false;
        if (queryTree instanceof BooleanQueryNode) {
            queryTree.set(actualQueryNodeList);
            return queryTree;
        }
        return new BooleanQueryNode(actualQueryNodeList);
    }
    
    private QueryNode applyModifier(final QueryNode node, final QueryNode parent) {
        if (this.usingAnd) {
            if (parent instanceof OrQueryNode) {
                if (node instanceof ModifierQueryNode) {
                    final ModifierQueryNode modNode = (ModifierQueryNode)node;
                    if (modNode.getModifier() == ModifierQueryNode.Modifier.MOD_REQ) {
                        return modNode.getChild();
                    }
                }
            }
            else {
                if (!(node instanceof ModifierQueryNode)) {
                    return new BooleanModifierNode(node, ModifierQueryNode.Modifier.MOD_REQ);
                }
                final ModifierQueryNode modNode = (ModifierQueryNode)node;
                if (modNode.getModifier() == ModifierQueryNode.Modifier.MOD_NONE) {
                    return new BooleanModifierNode(modNode.getChild(), ModifierQueryNode.Modifier.MOD_REQ);
                }
            }
        }
        else if (node.getParent() instanceof AndQueryNode) {
            if (!(node instanceof ModifierQueryNode)) {
                return new BooleanModifierNode(node, ModifierQueryNode.Modifier.MOD_REQ);
            }
            final ModifierQueryNode modNode = (ModifierQueryNode)node;
            if (modNode.getModifier() == ModifierQueryNode.Modifier.MOD_NONE) {
                return new BooleanModifierNode(modNode.getChild(), ModifierQueryNode.Modifier.MOD_REQ);
            }
        }
        return node;
    }
    
    private void readTree(final QueryNode node) {
        if (node instanceof BooleanQueryNode) {
            final List<QueryNode> children = node.getChildren();
            if (children != null && children.size() > 0) {
                for (int i = 0; i < children.size() - 1; ++i) {
                    this.readTree(children.get(i));
                }
                this.processNode(node);
                this.readTree(children.get(children.size() - 1));
            }
            else {
                this.processNode(node);
            }
        }
        else {
            this.processNode(node);
        }
    }
    
    private void processNode(final QueryNode node) {
        if (node instanceof AndQueryNode || node instanceof OrQueryNode) {
            if (!this.latestNodeVerified && !this.queryNodeList.isEmpty()) {
                this.queryNodeList.add(this.applyModifier(this.queryNodeList.remove(this.queryNodeList.size() - 1), node));
                this.latestNodeVerified = true;
            }
        }
        else if (!(node instanceof BooleanQueryNode)) {
            this.queryNodeList.add(this.applyModifier(node, node.getParent()));
            this.latestNodeVerified = false;
        }
    }
    
    @Override
    public QueryConfigHandler getQueryConfigHandler() {
        return this.queryConfig;
    }
    
    @Override
    public void setQueryConfigHandler(final QueryConfigHandler queryConfigHandler) {
        this.queryConfig = queryConfigHandler;
    }
}
