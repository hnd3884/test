package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import java.util.Iterator;

public class TokenizedPhraseQueryNode extends QueryNodeImpl implements FieldableNode
{
    public TokenizedPhraseQueryNode() {
        this.setLeaf(false);
        this.allocate();
    }
    
    @Override
    public String toString() {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<tokenizedphrase/>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<tokenizedtphrase>");
        for (final QueryNode child : this.getChildren()) {
            sb.append("\n");
            sb.append(child.toString());
        }
        sb.append("\n</tokenizedphrase>");
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
            filler = ",";
        }
        return "[TP[" + sb.toString() + "]]";
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final TokenizedPhraseQueryNode clone = (TokenizedPhraseQueryNode)super.cloneTree();
        return clone;
    }
    
    @Override
    public CharSequence getField() {
        final List<QueryNode> children = this.getChildren();
        if (children == null || children.size() == 0) {
            return null;
        }
        return children.get(0).getField();
    }
    
    @Override
    public void setField(final CharSequence fieldName) {
        final List<QueryNode> children = this.getChildren();
        if (children != null) {
            for (final QueryNode child : this.getChildren()) {
                if (child instanceof FieldableNode) {
                    ((FieldableNode)child).setField(fieldName);
                }
            }
        }
    }
}
