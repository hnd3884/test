package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class QuotedFieldQueryNode extends FieldQueryNode
{
    public QuotedFieldQueryNode(final CharSequence field, final CharSequence text, final int begin, final int end) {
        super(field, text, begin, end);
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escaper) {
        if (this.isDefaultField(this.field)) {
            return "\"" + (Object)this.getTermEscapeQuoted(escaper) + "\"";
        }
        return (Object)this.field + ":" + "\"" + (Object)this.getTermEscapeQuoted(escaper) + "\"";
    }
    
    @Override
    public String toString() {
        return "<quotedfield start='" + this.begin + "' end='" + this.end + "' field='" + (Object)this.field + "' term='" + (Object)this.text + "'/>";
    }
    
    @Override
    public QuotedFieldQueryNode cloneTree() throws CloneNotSupportedException {
        final QuotedFieldQueryNode clone = (QuotedFieldQueryNode)super.cloneTree();
        return clone;
    }
}
