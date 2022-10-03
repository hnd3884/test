package org.apache.lucene.queryparser.flexible.core.nodes;

public interface RangeQueryNode<T extends FieldValuePairQueryNode<?>> extends FieldableNode
{
    T getLowerBound();
    
    T getUpperBound();
    
    boolean isLowerInclusive();
    
    boolean isUpperInclusive();
}
