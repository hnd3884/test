package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class OpaqueQueryNode extends QueryNodeImpl
{
    private CharSequence schema;
    private CharSequence value;
    
    public OpaqueQueryNode(final CharSequence schema, final CharSequence value) {
        this.schema = null;
        this.value = null;
        this.setLeaf(true);
        this.schema = schema;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "<opaque schema='" + (Object)this.schema + "' value='" + (Object)this.value + "'/>";
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        return "@" + (Object)this.schema + ":'" + (Object)this.value + "'";
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final OpaqueQueryNode clone = (OpaqueQueryNode)super.cloneTree();
        clone.schema = this.schema;
        clone.value = this.value;
        return clone;
    }
    
    public CharSequence getSchema() {
        return this.schema;
    }
    
    public CharSequence getValue() {
        return this.value;
    }
}
