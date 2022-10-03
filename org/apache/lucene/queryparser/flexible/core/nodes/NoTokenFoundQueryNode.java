package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class NoTokenFoundQueryNode extends DeletedQueryNode
{
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escaper) {
        return "[NTF]";
    }
    
    @Override
    public String toString() {
        return "<notokenfound/>";
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final NoTokenFoundQueryNode clone = (NoTokenFoundQueryNode)super.cloneTree();
        return clone;
    }
}
