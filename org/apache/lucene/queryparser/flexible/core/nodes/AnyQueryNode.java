package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import java.util.Iterator;
import java.util.List;

public class AnyQueryNode extends AndQueryNode
{
    private CharSequence field;
    private int minimumMatchingmElements;
    
    public AnyQueryNode(final List<QueryNode> clauses, final CharSequence field, final int minimumMatchingElements) {
        super(clauses);
        this.field = null;
        this.minimumMatchingmElements = 0;
        this.field = field;
        this.minimumMatchingmElements = minimumMatchingElements;
        if (clauses != null) {
            for (final QueryNode clause : clauses) {
                if (clause instanceof FieldQueryNode) {
                    if (clause instanceof QueryNodeImpl) {
                        ((QueryNodeImpl)clause).toQueryStringIgnoreFields = true;
                    }
                    if (!(clause instanceof FieldableNode)) {
                        continue;
                    }
                    ((FieldableNode)clause).setField(field);
                }
            }
        }
    }
    
    public int getMinimumMatchingElements() {
        return this.minimumMatchingmElements;
    }
    
    public CharSequence getField() {
        return this.field;
    }
    
    public String getFieldAsString() {
        if (this.field == null) {
            return null;
        }
        return this.field.toString();
    }
    
    public void setField(final CharSequence field) {
        this.field = field;
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final AnyQueryNode clone = (AnyQueryNode)super.cloneTree();
        clone.field = this.field;
        clone.minimumMatchingmElements = this.minimumMatchingmElements;
        return clone;
    }
    
    @Override
    public String toString() {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<any field='" + (Object)this.field + "'  matchelements=" + this.minimumMatchingmElements + "/>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<any field='" + (Object)this.field + "'  matchelements=" + this.minimumMatchingmElements + ">");
        for (final QueryNode clause : this.getChildren()) {
            sb.append("\n");
            sb.append(clause.toString());
        }
        sb.append("\n</any>");
        return sb.toString();
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        final String anySTR = "ANY " + this.minimumMatchingmElements;
        final StringBuilder sb = new StringBuilder();
        if (this.getChildren() != null) {
            if (this.getChildren().size() != 0) {
                String filler = "";
                for (final QueryNode clause : this.getChildren()) {
                    sb.append(filler).append(clause.toQueryString(escapeSyntaxParser));
                    filler = " ";
                }
            }
        }
        if (this.isDefaultField(this.field)) {
            return "( " + sb.toString() + " ) " + anySTR;
        }
        return (Object)this.field + ":(( " + sb.toString() + " ) " + anySTR + ")";
    }
}
