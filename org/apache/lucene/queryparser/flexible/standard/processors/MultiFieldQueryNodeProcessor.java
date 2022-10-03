package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.OrQueryNode;
import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class MultiFieldQueryNodeProcessor extends QueryNodeProcessorImpl
{
    private boolean processChildren;
    
    public MultiFieldQueryNodeProcessor() {
        this.processChildren = true;
    }
    
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        return node;
    }
    
    @Override
    protected void processChildren(final QueryNode queryTree) throws QueryNodeException {
        if (this.processChildren) {
            super.processChildren(queryTree);
        }
        else {
            this.processChildren = true;
        }
    }
    
    @Override
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof FieldableNode) {
            this.processChildren = false;
            FieldableNode fieldNode = (FieldableNode)node;
            if (fieldNode.getField() == null) {
                final CharSequence[] fields = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.MULTI_FIELDS);
                if (fields == null) {
                    throw new IllegalArgumentException("StandardQueryConfigHandler.ConfigurationKeys.MULTI_FIELDS should be set on the QueryConfigHandler");
                }
                if (fields != null && fields.length > 0) {
                    fieldNode.setField(fields[0]);
                    if (fields.length == 1) {
                        return fieldNode;
                    }
                    final List<QueryNode> children = new ArrayList<QueryNode>(fields.length);
                    children.add(fieldNode);
                    for (int i = 1; i < fields.length; ++i) {
                        try {
                            fieldNode = (FieldableNode)fieldNode.cloneTree();
                            fieldNode.setField(fields[i]);
                            children.add(fieldNode);
                        }
                        catch (final CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return new GroupQueryNode(new OrQueryNode(children));
                }
            }
        }
        return node;
    }
    
    @Override
    protected List<QueryNode> setChildrenOrder(final List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}
