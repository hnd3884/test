package org.apache.lucene.queryparser.flexible.core.nodes;

public interface TextableQueryNode
{
    CharSequence getText();
    
    void setText(final CharSequence p0);
}
