package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;

public class SlopQueryNode extends QueryNodeImpl implements FieldableNode
{
    private int value;
    
    public SlopQueryNode(final QueryNode query, final int value) {
        this.value = 0;
        if (query == null) {
            throw new QueryNodeError(new MessageImpl(QueryParserMessages.NODE_ACTION_NOT_SUPPORTED, new Object[] { "query", "null" }));
        }
        this.value = value;
        this.setLeaf(false);
        this.allocate();
        this.add(query);
    }
    
    public QueryNode getChild() {
        return this.getChildren().get(0);
    }
    
    public int getValue() {
        return this.value;
    }
    
    private CharSequence getValueString() {
        final Float f = (Float)this.value;
        if (f == f.longValue()) {
            return "" + f.longValue();
        }
        return "" + f;
    }
    
    @Override
    public String toString() {
        return "<slop value='" + (Object)this.getValueString() + "'>" + "\n" + this.getChild().toString() + "\n</slop>";
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        if (this.getChild() == null) {
            return "";
        }
        return (Object)this.getChild().toQueryString(escapeSyntaxParser) + "~" + (Object)this.getValueString();
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final SlopQueryNode clone = (SlopQueryNode)super.cloneTree();
        clone.value = this.value;
        return clone;
    }
    
    @Override
    public CharSequence getField() {
        final QueryNode child = this.getChild();
        if (child instanceof FieldableNode) {
            return ((FieldableNode)child).getField();
        }
        return null;
    }
    
    @Override
    public void setField(final CharSequence fieldName) {
        final QueryNode child = this.getChild();
        if (child instanceof FieldableNode) {
            ((FieldableNode)child).setField(fieldName);
        }
    }
}
