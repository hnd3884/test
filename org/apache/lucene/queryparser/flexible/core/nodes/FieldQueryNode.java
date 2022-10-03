package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class FieldQueryNode extends QueryNodeImpl implements FieldValuePairQueryNode<CharSequence>, TextableQueryNode
{
    protected CharSequence field;
    protected CharSequence text;
    protected int begin;
    protected int end;
    protected int positionIncrement;
    
    public FieldQueryNode(final CharSequence field, final CharSequence text, final int begin, final int end) {
        this.field = field;
        this.text = text;
        this.begin = begin;
        this.end = end;
        this.setLeaf(true);
    }
    
    protected CharSequence getTermEscaped(final EscapeQuerySyntax escaper) {
        return escaper.escape(this.text, Locale.getDefault(), EscapeQuerySyntax.Type.NORMAL);
    }
    
    protected CharSequence getTermEscapeQuoted(final EscapeQuerySyntax escaper) {
        return escaper.escape(this.text, Locale.getDefault(), EscapeQuerySyntax.Type.STRING);
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escaper) {
        if (this.isDefaultField(this.field)) {
            return this.getTermEscaped(escaper);
        }
        return (Object)this.field + ":" + (Object)this.getTermEscaped(escaper);
    }
    
    @Override
    public String toString() {
        return "<field start='" + this.begin + "' end='" + this.end + "' field='" + (Object)this.field + "' text='" + (Object)this.text + "'/>";
    }
    
    public String getTextAsString() {
        if (this.text == null) {
            return null;
        }
        return this.text.toString();
    }
    
    public String getFieldAsString() {
        if (this.field == null) {
            return null;
        }
        return this.field.toString();
    }
    
    public int getBegin() {
        return this.begin;
    }
    
    public void setBegin(final int begin) {
        this.begin = begin;
    }
    
    public int getEnd() {
        return this.end;
    }
    
    public void setEnd(final int end) {
        this.end = end;
    }
    
    @Override
    public CharSequence getField() {
        return this.field;
    }
    
    @Override
    public void setField(final CharSequence field) {
        this.field = field;
    }
    
    public int getPositionIncrement() {
        return this.positionIncrement;
    }
    
    public void setPositionIncrement(final int pi) {
        this.positionIncrement = pi;
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
    public FieldQueryNode cloneTree() throws CloneNotSupportedException {
        final FieldQueryNode fqn = (FieldQueryNode)super.cloneTree();
        fqn.begin = this.begin;
        fqn.end = this.end;
        fqn.field = this.field;
        fqn.text = this.text;
        fqn.positionIncrement = this.positionIncrement;
        fqn.toQueryStringIgnoreFields = this.toQueryStringIgnoreFields;
        return fqn;
    }
    
    @Override
    public CharSequence getValue() {
        return this.getText();
    }
    
    @Override
    public void setValue(final CharSequence value) {
        this.setText(value);
    }
}
