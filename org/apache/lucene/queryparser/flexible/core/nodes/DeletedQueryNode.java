package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class DeletedQueryNode extends QueryNodeImpl
{
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escaper) {
        return "[DELETEDCHILD]";
    }
    
    @Override
    public String toString() {
        return "<deleted/>";
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final DeletedQueryNode clone = (DeletedQueryNode)super.cloneTree();
        return clone;
    }
}
