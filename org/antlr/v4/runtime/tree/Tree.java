package org.antlr.v4.runtime.tree;

public interface Tree
{
    Tree getParent();
    
    Object getPayload();
    
    Tree getChild(final int p0);
    
    int getChildCount();
    
    String toStringTree();
}
