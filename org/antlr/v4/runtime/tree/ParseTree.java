package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Parser;

public interface ParseTree extends SyntaxTree
{
    ParseTree getParent();
    
    ParseTree getChild(final int p0);
    
     <T> T accept(final ParseTreeVisitor<? extends T> p0);
    
    String getText();
    
    String toStringTree(final Parser p0);
}
