package org.apache.lucene.queryparser.flexible.core.nodes;

public interface ValueQueryNode<T> extends QueryNode
{
    void setValue(final T p0);
    
    T getValue();
}
