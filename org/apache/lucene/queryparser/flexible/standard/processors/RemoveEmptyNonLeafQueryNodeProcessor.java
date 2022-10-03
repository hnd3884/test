package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.Iterator;
import java.util.Collection;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchNoDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import java.util.LinkedList;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class RemoveEmptyNonLeafQueryNodeProcessor extends QueryNodeProcessorImpl
{
    private LinkedList<QueryNode> childrenBuffer;
    
    public RemoveEmptyNonLeafQueryNodeProcessor() {
        this.childrenBuffer = new LinkedList<QueryNode>();
    }
    
    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        queryTree = super.process(queryTree);
        if (!queryTree.isLeaf()) {
            final List<QueryNode> children = queryTree.getChildren();
            if (children == null || children.size() == 0) {
                return new MatchNoDocsQueryNode();
            }
        }
        return queryTree;
    }
    
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        return node;
    }
    
    @Override
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        return node;
    }
    
    @Override
    protected List<QueryNode> setChildrenOrder(final List<QueryNode> children) throws QueryNodeException {
        try {
            for (final QueryNode child : children) {
                if (!child.isLeaf()) {
                    final List<QueryNode> grandChildren = child.getChildren();
                    if (grandChildren == null || grandChildren.size() <= 0) {
                        continue;
                    }
                    this.childrenBuffer.add(child);
                }
                else {
                    this.childrenBuffer.add(child);
                }
            }
            children.clear();
            children.addAll(this.childrenBuffer);
        }
        finally {
            this.childrenBuffer.clear();
        }
        return children;
    }
}
