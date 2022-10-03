package org.apache.lucene.queryparser.flexible.core.processors;

import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import java.util.ArrayList;

public abstract class QueryNodeProcessorImpl implements QueryNodeProcessor
{
    private ArrayList<ChildrenList> childrenListPool;
    private QueryConfigHandler queryConfig;
    
    public QueryNodeProcessorImpl() {
        this.childrenListPool = new ArrayList<ChildrenList>();
    }
    
    public QueryNodeProcessorImpl(final QueryConfigHandler queryConfigHandler) {
        this.childrenListPool = new ArrayList<ChildrenList>();
        this.queryConfig = queryConfigHandler;
    }
    
    @Override
    public QueryNode process(final QueryNode queryTree) throws QueryNodeException {
        return this.processIteration(queryTree);
    }
    
    private QueryNode processIteration(QueryNode queryTree) throws QueryNodeException {
        queryTree = this.preProcessNode(queryTree);
        this.processChildren(queryTree);
        queryTree = this.postProcessNode(queryTree);
        return queryTree;
    }
    
    protected void processChildren(final QueryNode queryTree) throws QueryNodeException {
        final List<QueryNode> children = queryTree.getChildren();
        if (children != null && children.size() > 0) {
            final ChildrenList newChildren = this.allocateChildrenList();
            try {
                for (QueryNode child : children) {
                    child = this.processIteration(child);
                    if (child == null) {
                        throw new NullPointerException();
                    }
                    newChildren.add(child);
                }
                final List<QueryNode> orderedChildrenList = this.setChildrenOrder(newChildren);
                queryTree.set(orderedChildrenList);
            }
            finally {
                newChildren.beingUsed = false;
            }
        }
    }
    
    private ChildrenList allocateChildrenList() {
        ChildrenList list = null;
        for (final ChildrenList auxList : this.childrenListPool) {
            if (!auxList.beingUsed) {
                list = auxList;
                list.clear();
                break;
            }
        }
        if (list == null) {
            list = new ChildrenList();
            this.childrenListPool.add(list);
        }
        list.beingUsed = true;
        return list;
    }
    
    @Override
    public void setQueryConfigHandler(final QueryConfigHandler queryConfigHandler) {
        this.queryConfig = queryConfigHandler;
    }
    
    @Override
    public QueryConfigHandler getQueryConfigHandler() {
        return this.queryConfig;
    }
    
    protected abstract QueryNode preProcessNode(final QueryNode p0) throws QueryNodeException;
    
    protected abstract QueryNode postProcessNode(final QueryNode p0) throws QueryNodeException;
    
    protected abstract List<QueryNode> setChildrenOrder(final List<QueryNode> p0) throws QueryNodeException;
    
    private static class ChildrenList extends ArrayList<QueryNode>
    {
        boolean beingUsed;
    }
}
