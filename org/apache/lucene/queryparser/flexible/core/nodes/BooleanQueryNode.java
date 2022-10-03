package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import java.util.Iterator;
import java.util.List;

public class BooleanQueryNode extends QueryNodeImpl
{
    public BooleanQueryNode(final List<QueryNode> clauses) {
        this.setLeaf(false);
        this.allocate();
        this.set(clauses);
    }
    
    @Override
    public String toString() {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<boolean operation='default'/>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<boolean operation='default'>");
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
            filler = " ";
        }
        if ((this.getParent() != null && this.getParent() instanceof GroupQueryNode) || this.isRoot()) {
            return sb.toString();
        }
        return "( " + sb.toString() + " )";
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final BooleanQueryNode clone = (BooleanQueryNode)super.cloneTree();
        return clone;
    }
}
