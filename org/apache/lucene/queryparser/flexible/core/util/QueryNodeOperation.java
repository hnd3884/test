package org.apache.lucene.queryparser.flexible.core.util;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.nodes.AndQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public final class QueryNodeOperation
{
    private QueryNodeOperation() {
    }
    
    public static final QueryNode logicalAnd(final QueryNode q1, final QueryNode q2) {
        if (q1 == null) {
            return q2;
        }
        if (q2 == null) {
            return q1;
        }
        ANDOperation op = null;
        if (q1 instanceof AndQueryNode && q2 instanceof AndQueryNode) {
            op = ANDOperation.BOTH;
        }
        else if (q1 instanceof AndQueryNode) {
            op = ANDOperation.Q1;
        }
        else if (q1 instanceof AndQueryNode) {
            op = ANDOperation.Q2;
        }
        else {
            op = ANDOperation.NONE;
        }
        try {
            QueryNode result = null;
            switch (op) {
                case NONE: {
                    final List<QueryNode> children = new ArrayList<QueryNode>();
                    children.add(q1.cloneTree());
                    children.add(q2.cloneTree());
                    result = new AndQueryNode(children);
                    return result;
                }
                case Q1: {
                    result = q1.cloneTree();
                    result.add(q2.cloneTree());
                    return result;
                }
                case Q2: {
                    result = q2.cloneTree();
                    result.add(q1.cloneTree());
                    return result;
                }
                case BOTH: {
                    result = q1.cloneTree();
                    result.add(q2.cloneTree().getChildren());
                    return result;
                }
            }
        }
        catch (final CloneNotSupportedException e) {
            throw new QueryNodeError(e);
        }
        return null;
    }
    
    private enum ANDOperation
    {
        BOTH, 
        Q1, 
        Q2, 
        NONE;
    }
}
