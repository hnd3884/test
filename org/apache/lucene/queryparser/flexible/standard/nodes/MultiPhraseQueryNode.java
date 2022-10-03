package org.apache.lucene.queryparser.flexible.standard.nodes;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import java.util.Iterator;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;

public class MultiPhraseQueryNode extends QueryNodeImpl implements FieldableNode
{
    public MultiPhraseQueryNode() {
        this.setLeaf(false);
        this.allocate();
    }
    
    @Override
    public String toString() {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<multiPhrase/>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<multiPhrase>");
        for (final QueryNode child : this.getChildren()) {
            sb.append("\n");
            sb.append(child.toString());
        }
        sb.append("\n</multiPhrase>");
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
        return "[MTP[" + sb.toString() + "]]";
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final MultiPhraseQueryNode clone = (MultiPhraseQueryNode)super.cloneTree();
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
            for (final QueryNode child : children) {
                if (child instanceof FieldableNode) {
                    ((FieldableNode)child).setField(fieldName);
                }
            }
        }
    }
}
