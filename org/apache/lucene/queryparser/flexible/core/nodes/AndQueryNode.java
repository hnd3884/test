package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import java.util.Iterator;
import java.util.List;

public class AndQueryNode extends BooleanQueryNode
{
    public AndQueryNode(final List<QueryNode> clauses) {
        super(clauses);
        if (clauses == null || clauses.size() == 0) {
            throw new IllegalArgumentException("AND query must have at least one clause");
        }
    }
    
    @Override
    public String toString() {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<boolean operation='and'/>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<boolean operation='and'>");
        for (final QueryNode child : this.getChildren()) {
            sb.append("\n");
            sb.append(child.toString());
        }
        sb.append("\n</boolean>");
        return sb.toString();
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        String filler = "";
        for (final QueryNode child : this.getChildren()) {
            sb.append(filler).append(child.toQueryString(escapeSyntaxParser));
            filler = " AND ";
        }
        if ((this.getParent() != null && this.getParent() instanceof GroupQueryNode) || this.isRoot()) {
            return sb.toString();
        }
        return "( " + sb.toString() + " )";
    }
}
