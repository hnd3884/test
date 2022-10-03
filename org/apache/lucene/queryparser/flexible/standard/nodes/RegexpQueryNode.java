package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TextableQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;

public class RegexpQueryNode extends QueryNodeImpl implements TextableQueryNode, FieldableNode
{
    private CharSequence text;
    private CharSequence field;
    
    public RegexpQueryNode(final CharSequence field, final CharSequence text, final int begin, final int end) {
        this.field = field;
        this.text = text.subSequence(begin, end);
    }
    
    public BytesRef textToBytesRef() {
        return new BytesRef(this.text);
    }
    
    @Override
    public String toString() {
        return "<regexp field='" + (Object)this.field + "' term='" + (Object)this.text + "'/>";
    }
    
    @Override
    public RegexpQueryNode cloneTree() throws CloneNotSupportedException {
        final RegexpQueryNode clone = (RegexpQueryNode)super.cloneTree();
        clone.field = this.field;
        clone.text = this.text;
        return clone;
    }
    
    @Override
    public CharSequence getText() {
        return this.text;
    }
    
    @Override
    public void setText(final CharSequence text) {
        this.text = text;
    }
    
    @Override
    public CharSequence getField() {
        return this.field;
    }
    
    public String getFieldAsString() {
        return this.field.toString();
    }
    
    @Override
    public void setField(final CharSequence field) {
        this.field = field;
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        return this.isDefaultField(this.field) ? ("/" + (Object)this.text + "/") : ((Object)this.field + ":/" + (Object)this.text + "/");
    }
}
