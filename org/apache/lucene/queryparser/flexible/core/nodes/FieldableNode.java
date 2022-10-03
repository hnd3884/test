package org.apache.lucene.queryparser.flexible.core.nodes;

public interface FieldableNode extends QueryNode
{
    CharSequence getField();
    
    void setField(final CharSequence p0);
}
