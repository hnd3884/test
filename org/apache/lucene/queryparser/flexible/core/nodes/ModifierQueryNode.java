package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;

public class ModifierQueryNode extends QueryNodeImpl
{
    private Modifier modifier;
    
    public ModifierQueryNode(final QueryNode query, final Modifier mod) {
        this.modifier = Modifier.MOD_NONE;
        if (query == null) {
            throw new QueryNodeError(new MessageImpl(QueryParserMessages.PARAMETER_VALUE_NOT_SUPPORTED, new Object[] { "query", "null" }));
        }
        this.allocate();
        this.setLeaf(false);
        this.add(query);
        this.modifier = mod;
    }
    
    public QueryNode getChild() {
        return this.getChildren().get(0);
    }
    
    public Modifier getModifier() {
        return this.modifier;
    }
    
    @Override
    public String toString() {
        return "<modifier operation='" + this.modifier.toString() + "'>" + "\n" + this.getChild().toString() + "\n</modifier>";
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        if (this.getChild() == null) {
            return "";
        }
        String leftParenthensis = "";
        String rightParenthensis = "";
        if (this.getChild() != null && this.getChild() instanceof ModifierQueryNode) {
            leftParenthensis = "(";
            rightParenthensis = ")";
        }
        if (this.getChild() instanceof BooleanQueryNode) {
            return this.modifier.toLargeString() + leftParenthensis + (Object)this.getChild().toQueryString(escapeSyntaxParser) + rightParenthensis;
        }
        return this.modifier.toDigitString() + leftParenthensis + (Object)this.getChild().toQueryString(escapeSyntaxParser) + rightParenthensis;
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final ModifierQueryNode clone = (ModifierQueryNode)super.cloneTree();
        clone.modifier = this.modifier;
        return clone;
    }
    
    public void setChild(final QueryNode child) {
        final List<QueryNode> list = new ArrayList<QueryNode>();
        list.add(child);
        this.set(list);
    }
    
    public enum Modifier
    {
        MOD_NONE, 
        MOD_NOT, 
        MOD_REQ;
        
        @Override
        public String toString() {
            switch (this) {
                case MOD_NONE: {
                    return "MOD_NONE";
                }
                case MOD_NOT: {
                    return "MOD_NOT";
                }
                case MOD_REQ: {
                    return "MOD_REQ";
                }
                default: {
                    return "MOD_DEFAULT";
                }
            }
        }
        
        public String toDigitString() {
            switch (this) {
                case MOD_NONE: {
                    return "";
                }
                case MOD_NOT: {
                    return "-";
                }
                case MOD_REQ: {
                    return "+";
                }
                default: {
                    return "";
                }
            }
        }
        
        public String toLargeString() {
            switch (this) {
                case MOD_NONE: {
                    return "";
                }
                case MOD_NOT: {
                    return "NOT ";
                }
                case MOD_REQ: {
                    return "+";
                }
                default: {
                    return "";
                }
            }
        }
    }
}
