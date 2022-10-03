package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;

public class WildcardQueryNode extends FieldQueryNode
{
    public WildcardQueryNode(final CharSequence field, final CharSequence text, final int begin, final int end) {
        super(field, text, begin, end);
    }
    
    public WildcardQueryNode(final FieldQueryNode fqn) {
        this(fqn.getField(), fqn.getText(), fqn.getBegin(), fqn.getEnd());
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escaper) {
        if (this.isDefaultField(this.field)) {
            return this.text;
        }
        return (Object)this.field + ":" + (Object)this.text;
    }
    
    @Override
    public String toString() {
        return "<wildcard field='" + (Object)this.field + "' term='" + (Object)this.text + "'/>";
    }
    
    @Override
    public WildcardQueryNode cloneTree() throws CloneNotSupportedException {
        final WildcardQueryNode clone = (WildcardQueryNode)super.cloneTree();
        return clone;
    }
}
