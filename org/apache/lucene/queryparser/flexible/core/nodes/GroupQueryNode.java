package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;

public class GroupQueryNode extends QueryNodeImpl
{
    public GroupQueryNode(final QueryNode query) {
        if (query == null) {
            throw new QueryNodeError(new MessageImpl(QueryParserMessages.PARAMETER_VALUE_NOT_SUPPORTED, new Object[] { "query", "null" }));
        }
        this.allocate();
        this.setLeaf(false);
        this.add(query);
    }
    
    public QueryNode getChild() {
        return this.getChildren().get(0);
    }
    
    @Override
    public String toString() {
        return "<group>\n" + this.getChild().toString() + "\n</group>";
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        if (this.getChild() == null) {
            return "";
        }
        return "( " + (Object)this.getChild().toQueryString(escapeSyntaxParser) + " )";
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final GroupQueryNode clone = (GroupQueryNode)super.cloneTree();
        return clone;
    }
    
    public void setChild(final QueryNode child) {
        final List<QueryNode> list = new ArrayList<QueryNode>();
        list.add(child);
        this.set(list);
    }
}
