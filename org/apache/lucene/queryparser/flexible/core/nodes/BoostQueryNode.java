package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import java.util.List;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;

public class BoostQueryNode extends QueryNodeImpl
{
    private float value;
    
    public BoostQueryNode(final QueryNode query, final float value) {
        this.value = 0.0f;
        if (query == null) {
            throw new QueryNodeError(new MessageImpl(QueryParserMessages.NODE_ACTION_NOT_SUPPORTED, new Object[] { "query", "null" }));
        }
        this.value = value;
        this.setLeaf(false);
        this.allocate();
        this.add(query);
    }
    
    public QueryNode getChild() {
        final List<QueryNode> children = this.getChildren();
        if (children == null || children.size() == 0) {
            return null;
        }
        return children.get(0);
    }
    
    public float getValue() {
        return this.value;
    }
    
    private CharSequence getValueString() {
        final Float f = this.value;
        if (f == f.longValue()) {
            return "" + f.longValue();
        }
        return "" + f;
    }
    
    @Override
    public String toString() {
        return "<boost value='" + (Object)this.getValueString() + "'>" + "\n" + this.getChild().toString() + "\n</boost>";
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        if (this.getChild() == null) {
            return "";
        }
        return (Object)this.getChild().toQueryString(escapeSyntaxParser) + "^" + (Object)this.getValueString();
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final BoostQueryNode clone = (BoostQueryNode)super.cloneTree();
        clone.value = this.value;
        return clone;
    }
}
