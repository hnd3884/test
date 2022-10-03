package org.apache.lucene.queryparser.flexible.core.processors;

import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchNoDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.DeletedQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class RemoveDeletedQueryNodesProcessor extends QueryNodeProcessorImpl
{
    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        queryTree = super.process(queryTree);
        if (queryTree instanceof DeletedQueryNode && !(queryTree instanceof MatchNoDocsQueryNode)) {
            return new MatchNoDocsQueryNode();
        }
        return queryTree;
    }
    
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (!node.isLeaf()) {
            final List<QueryNode> children = node.getChildren();
            boolean removeBoolean = false;
            if (children == null || children.size() == 0) {
                removeBoolean = true;
            }
            else {
                removeBoolean = true;
                final Iterator<QueryNode> it = children.iterator();
                while (it.hasNext()) {
                    if (!(it.next() instanceof DeletedQueryNode)) {
                        removeBoolean = false;
                        break;
                    }
                }
            }
            if (removeBoolean) {
                return new DeletedQueryNode();
            }
        }
        return node;
    }
    
    @Override
    protected List<QueryNode> setChildrenOrder(final List<QueryNode> children) throws QueryNodeException {
        for (int i = 0; i < children.size(); ++i) {
            if (children.get(i) instanceof DeletedQueryNode) {
                children.remove(i--);
            }
        }
        return children;
    }
    
    @Override
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        return node;
    }
}
