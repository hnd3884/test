package org.apache.lucene.queryparser.flexible.standard.nodes;

import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import java.text.NumberFormat;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldValuePairQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;

public class NumericQueryNode extends QueryNodeImpl implements FieldValuePairQueryNode<Number>
{
    private NumberFormat numberFormat;
    private CharSequence field;
    private Number value;
    
    public NumericQueryNode(final CharSequence field, final Number value, final NumberFormat numberFormat) {
        this.setNumberFormat(numberFormat);
        this.setField(field);
        this.setValue(value);
    }
    
    @Override
    public CharSequence getField() {
        return this.field;
    }
    
    @Override
    public void setField(final CharSequence fieldName) {
        this.field = fieldName;
    }
    
    protected CharSequence getTermEscaped(final EscapeQuerySyntax escaper) {
        return escaper.escape(this.numberFormat.format(this.value), Locale.ROOT, EscapeQuerySyntax.Type.NORMAL);
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        if (this.isDefaultField(this.field)) {
            return this.getTermEscaped(escapeSyntaxParser);
        }
        return (Object)this.field + ":" + (Object)this.getTermEscaped(escapeSyntaxParser);
    }
    
    public void setNumberFormat(final NumberFormat format) {
        this.numberFormat = format;
    }
    
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }
    
    @Override
    public Number getValue() {
        return this.value;
    }
    
    @Override
    public void setValue(final Number value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "<numeric field='" + (Object)this.field + "' number='" + this.numberFormat.format(this.value) + "'/>";
    }
}
