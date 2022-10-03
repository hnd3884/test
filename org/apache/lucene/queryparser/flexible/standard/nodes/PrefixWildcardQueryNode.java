package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;

public class PrefixWildcardQueryNode extends WildcardQueryNode
{
    public PrefixWildcardQueryNode(final CharSequence field, final CharSequence text, final int begin, final int end) {
        super(field, text, begin, end);
    }
    
    public PrefixWildcardQueryNode(final FieldQueryNode fqn) {
        this(fqn.getField(), fqn.getText(), fqn.getBegin(), fqn.getEnd());
    }
    
    @Override
    public String toString() {
        return "<prefixWildcard field='" + (Object)this.field + "' term='" + (Object)this.text + "'/>";
    }
    
    @Override
    public PrefixWildcardQueryNode cloneTree() throws CloneNotSupportedException {
        final PrefixWildcardQueryNode clone = (PrefixWildcardQueryNode)super.cloneTree();
        return clone;
    }
}
